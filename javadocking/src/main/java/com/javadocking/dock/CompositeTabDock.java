package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.dock.factory.CompositeTabDockFactory;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DragListener;
import com.javadocking.event.*;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a composite dock that has child docks that are organized in a tabbed pane.
 * This dock can not contain dockables. When dockables are added, child docks are created and the
 * dockables are added to the child docks.
 * </p>
 * <p>
 * The tabbed pane of this dock is created with the component factory of the docking manager
 * ({@link com.javadocking.DockingManager#getComponentFactory()}) with the method
 * {@link com.javadocking.component.SwComponentFactory#createJTabbedPane()}.
 * </p>
 * <p>
 * Information on using composite tab docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html#CompositeTabDock" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * A dockable can be added to this dock if:
 * <ul>
 * <li>it has {@link DockingMode#TAB} as possible docking mode.</li>
 * <li>the dock factory can create a child dock for the given dockable.
 * If the dockable is a {@link CompositeDockable}, but a child dock could not be created for the composite,
 * the dockable can be added, if a child dock can be created for every child dockable of the composite.</li>
 * </ul>
 *
 * <p>
 * When a dockable is added, a child dock is created with the <code>childDockFactory</code>. The dockable is added to
 * the child dock.
 * </p>
 * <p>
 * If the mouse is inside the priority rectangle, the dockable can be added with priority (see {@link Priority#CAN_DOCK_WITH_PRIORITY}).
 * When the mouse is inside the panel of this dock, but outside the priority rectangle,
 * the dockable can be added without priority (see {@link Priority#CAN_DOCK}).
 * The priority rectangle is a rectangle in the middle of the dock and retrieved with {@link #getPriorityRectangle(Rectangle)}.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Position} for child docks docked in this dock are one-dimensional.
 * The first position value of a child dock is between 0 and the number of child docks minus 1;
 * it is the index of its tab.
 * </p>
 * <p>
 * This kind of dock is never full. It is empty when there are 0 child docks.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class CompositeTabDock extends JPanel implements CompositeDock {
	// Static fields.

	/**
	 * The relative top offset of the priority rectangle.
	 */
	private static final double priorityRectangleRelativeTopOffset = 2.0 / 8.0;
	/**
	 * The relative left offset of the priority rectangle.
	 */
	private static final double priorityRectangleRelativeLeftOffset = 2.0 / 8.0;
	/**
	 * The relative bottom offset of the priority rectangle.
	 */
	private static final double priorityRectangleRelativeBottomOffset = 2.0 / 8.0;
	/**
	 * The relative right offset of the priority rectangle.
	 */
	private static final double priorityRectangleRelativeRightOffset = 2.0 / 8.0;

	/**
	 * The name of the <code>selectedDockableId</code> property.
	 */
	private static final String PROPERTY_SELECTED_DOCK_ID = "selectedDockableId";

	// Fields.

	/**
	 * The parent dock of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The tabbed pane that contains the docks.
	 */
	private JTabbedPane tabbedPane;
	/**
	 * This factory creates the child docks.
	 */
	private DockFactory childDockFactory;
	/**
	 * This is the rectangle in which a dockable can be docked with priority.
	 * It is computed with the values of priorityRectangleTopOffset, priorityRectangleLeftOffset
	 * priorityRectangleBottomOffset and priorityRectangleRightOffset. We keep it as field
	 * because we don't want to create every time a new rectangle.
	 */
	private Rectangle priorityRectangle = new Rectangle();
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();

	private Map dockingChangeListeners = new HashMap();

	// Ghosts.
	/**
	 * This is an old <code>tabbedPane</code> that has to be removed later. It is already made invisible.
	 * It cannot be removed now because it contains an old dock that still has listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private JTabbedPane ghostTabbedPane;

	// Constructors.

	/**
	 * Constructs a composite tab dock with a {@link CompositeTabDockFactory} as factory
	 * for the child docks.
	 */
	public CompositeTabDock() {
		this(new CompositeTabDockFactory());
	}

	/**
	 * Constructs a composite tab dock with the given child dock factories.
	 *
	 * @param childDockFactory The factory for creating child docks.
	 */
	public CompositeTabDock(DockFactory childDockFactory) {

		// Set the layout.
		super(new BorderLayout());

		// Set the factories.
		this.childDockFactory = childDockFactory;

		// Create the tabbed pane.
		initializeUi();

		// Listen to the selections of the tabs.
		SingleSelectionModel selectionModel = tabbedPane.getModel();
		selectionModel.addChangeListener(new TabChangelistener());

		// Create the dragger.
		DragListener dragListener = DockingManager.getDockDragListenerFactory().createDragListener(this);
		tabbedPane.addMouseListener(dragListener);
		tabbedPane.addMouseMotionListener(dragListener);

	}

	// Implementations of Dock.

	/**
	 * <p>
	 * Determines if the given dockable can be added to this dock.
	 * </p>
	 * <p>
	 * It can be docked in this dock:
	 * <ul>
	 * <li>if it has {@link DockingMode#TAB} as possible docking mode.</li>
	 * <li>the dock factory can create a child dock for the given dockable.
	 * If the dockable is a {@link CompositeDockable}, but a child dock could not be created for the composite,
	 * the dockable can be added, if a child dock can be created for every child dockable of the composite.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a tabbed dock.
		if ((dockable.getDockingModes() & DockingMode.TAB) == 0) {
			return Priority.CANNOT_DOCK;
		}

		// We can dock if the child dock factory can create a dock.
		if (childDockFactory.createDock(dockable, DockingMode.TAB) != null) {
			// Can we dock with priority?
			getPriorityRectangle(priorityRectangle);
			if (priorityRectangle.contains(relativeLocation)) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

			// We can dock, but not with priority.
			return Priority.CAN_DOCK;
		}

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// We can dock if we can dock all the children.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				Dockable childDockable = compositeDockable.getDockable(index);

				// Can we create a dock for this dockable?
				if (childDockFactory.createDock(childDockable, DockingMode.TAB) == null) {
					return Priority.CANNOT_DOCK;
				}

				// Is the component of the dockable not null?
				if (childDockable.getContent() == null) {
					// We don't want deeper nested dockables.
					return Priority.CANNOT_DOCK;
				}

				index++;
			}

			// Can we dock with priority?
			getPriorityRectangle(priorityRectangle);
			if (priorityRectangle.contains(relativeLocation)) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

			// We can dock, but not with priority.
			return Priority.CAN_DOCK;

		}

		return Priority.CANNOT_DOCK;

	}

	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation, Point dockableOffset, Rectangle rectangle) {

		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {
			// The docking rectangle is the rectangle defined by this dock panel.
			rectangle.setBounds(0, 0, getSize().width, getSize().height);
		}

		return priority;

	}

	public boolean addDockable(Dockable dockableToAdd, Point relativeLocation, Point dockableOffset) {

		// Verify the conditions for adding the dockable.
		if (getDockPriority(dockableToAdd, relativeLocation) == Priority.CANNOT_DOCK) {
			// We are not allowed to dock the dockable in this dock.
			return false;
		}

		// Create the new child dock.
		Dock childDock = childDockFactory.createDock(dockableToAdd, DockingMode.TAB);

		// Could we create a child dock?
		if (childDock != null) {
			childDock.setParentDock(this);

			if (dockableToAdd instanceof CompositeDockable) {
				CompositeDockable compositeDockableToAdd = (CompositeDockable) dockableToAdd;
				for (int index = 0; index < compositeDockableToAdd.getDockableCount(); index++) {
					childDock.addDockable(compositeDockableToAdd.getDockable(index), new Point(0, 0), new Point(0, 0));
				}
			} else {
				childDock.addDockable(dockableToAdd, new Point(0, 0), new Point(0, 0));
			}

			// Inform the listeners.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, childDock));

			// Add the component in a tab and select the tab.
			Component header = (Component) DockingManager.getComponentFactory().createCompositeTabDockHeader(childDock, getHeaderPosition());
			TabDockV6Addition.addTab(tabbedPane, (Component) childDock, header);
			if (dockableToAdd.getDescription() != null) {
				tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, dockableToAdd.getDescription());
			} else {
				tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, getTitle(dockableToAdd));
			}

			tabbedPane.setSelectedComponent((Component) childDock);

			// Inform the listeners.
			dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockableToAdd));

			childDock.addDockingListener(new DockingChangeListener(childDock, this));
		} else if (dockableToAdd instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockableToAdd;

			// Get the selected dockable.
			Dockable selectedDockable = compositeDockable.getSelectedDockable();

			// Iterate over the child dockables.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Is the component of the child not null? 
				Dockable childDockable = compositeDockable.getDockable(index);

				// Create the new child dock.
				childDock = childDockFactory.createDock(dockableToAdd, DockingMode.TAB);

				if (childDock != null) {
					childDock.setParentDock(this);
					childDock.addDockable(childDockable, new Point(0, 0), new Point(0, 0));

					// Inform the listeners.
					dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, childDock));

					// Add the child dockable as tab.
					Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(childDockable, getHeaderPosition());
					TabDockV6Addition.addTab(tabbedPane, (Component) childDock, header);
					if (childDockable.getDescription() != null) {
						tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, childDockable.getDescription());
					}

					// Do we have to select the tab of this dockable?
					if ((selectedDockable != null) && (childDockable.equals(selectedDockable))) {
						tabbedPane.setSelectedComponent((Component) childDock);
					}

					// Inform the listeners.
					dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockableToAdd));

					childDock.addDockingListener(new DockingChangeListener(childDock, this));
				}
			}
		}

		// Repaint.
		SwingUtil.repaintParent(this);

		// The add was successful.
		return true;

	}

	public boolean isEmpty() {
		return tabbedPane.getTabCount() == 0;
	}

	public boolean isFull() {
		return false;
	}

	public CompositeDock getParentDock() {
		return parentDock;
	}

	public void setParentDock(CompositeDock parentDock) {
		this.parentDock = parentDock;
	}

	public void saveProperties(String prefix, Properties properties, Map childDockIds) {

		// Save the class of the child dock factory and its properties.
		String className = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", className);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Iterate over the child docks.
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			// Get the child dock.
			Dock dock = (Dock) tabbedPane.getComponentAt(index);

			// Get the ID of the childDock.
			String childDockId = (String) childDockIds.get(dock);

			// Save the position.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(dock));
		}

		// Save the ID of the selected child.
		Dock selectedDock = (Dock) tabbedPane.getSelectedComponent();
		if (selectedDock != null) {
			String childDockId = (String) childDockIds.get(selectedDock);
			PropertiesUtil.setString(properties, prefix + PROPERTY_SELECTED_DOCK_ID, childDockId);
		}
	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException {

		// Load the class and properties of the child dock factory.
		try {
			String className = SingleDockFactory.class.getName();
			className = PropertiesUtil.getString(properties, prefix + "childDockFactory", className);
			Class clazz = Class.forName(className);
			childDockFactory = (DockFactory) clazz.newInstance();
			childDockFactory.loadProperties(prefix + "childDockFactory.", properties);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new SingleDockFactory();
		}

		// Create an array with the child docks ids in the right order.
		String[] childDockIdsArray = new String[newChildDocks.keySet().size()];
		for (Object o : newChildDocks.keySet()) {
			// Get the ID of the child dock.
			String childDockId = (String) o;

			Position position = null;
			position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, position);
			childDockIdsArray[position.getPosition(0)] = childDockId;
			//TODO what if a child dock is not there?
		}

		// Add the child docks.
		int position = 0;
		for (final String aChildDockIdsArray : childDockIdsArray) {
			// Get the child dock.
			Dock dock = (Dock) newChildDocks.get(aChildDockIdsArray);

			// Add only if the child is not empty.
			if (!dock.isEmpty()) {
				addChildDock(dock, new Position(position));
				position++;
			}
		}

		// Select the selected dock.
		boolean hasSelectedDock = false;
		String selectedDockId = null;
		selectedDockId = PropertiesUtil.getString(properties, prefix + PROPERTY_SELECTED_DOCK_ID, selectedDockId);
		if (selectedDockId != null) {
			// Get the selected child dock.
			Dock dock = (Dock) newChildDocks.get(selectedDockId);

			if (dock != null) {
				// Try to select the dockable.
				hasSelectedDock = setSelectedDock(dock);
			}
		}

		// Couldn't we select a dock?
		if (!hasSelectedDock) {
			// Select the first dock.
			if ((tabbedPane != null) && (tabbedPane.getTabCount() > 0)) {
				tabbedPane.setSelectedIndex(0);
			}
		}

	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	// Implementations of CompositeDock.

	public void addChildDock(Dock childDock, Position position) throws IllegalStateException {

		// Get the position in the tabs.
		int tabPosition = tabbedPane.getTabCount();
		if (position.getDimensions() == 1) {
			if ((position.getPosition(0) >= 0) && (position.getPosition(0) <= getChildDockCount())) {
				tabPosition = position.getPosition(0);
			}
		}

		childDock.setParentDock(this);

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, childDock));

		// Add the component in a tab and select the tab.
		Component header = (Component) DockingManager.getComponentFactory().createCompositeTabDockHeader(childDock, getHeaderPosition());
		TabDockV6Addition.insertTab(tabbedPane, tabPosition, (Component) childDock, header);

		tabbedPane.setSelectedComponent((Component) childDock);

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, childDock));

		childDock.addDockingListener(new DockingChangeListener(childDock, this));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public int getChildDockCount() {
		return tabbedPane.getTabCount();
	}

	public Dock getChildDock(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getChildDockCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dock) tabbedPane.getComponentAt(index);

	}

	public Position getChildDockPosition(Dock childDock) throws IllegalArgumentException {

		// Get the index of the child dock component.
		int position = -1;
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			// Get the child dock.
			Dock dock = (Dock) tabbedPane.getComponentAt(index);
			if (dock.equals(childDock)) {
				position = index;
				break;
			}
		}
		if (position >= 0) {
			return new Position(position);
		}

		throw new IllegalArgumentException("The dock is not docked in this composite dock.");

	}

	public void emptyChild(Dock emptyChildDock) {

		// Search the empty child dock.
		// Get the index of the child dock component.
		int position = -1;
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			// Get the child dock.
			Dock dock = (Dock) tabbedPane.getComponentAt(index);
			if (dock.equals(emptyChildDock)) {
				position = index;
				break;
			}
		}
		if (position >= 0) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// Remove the empty dock.
			tabbedPane.remove((Component) emptyChildDock);

			// Remove the docking change listener.
			DockingChangeListener dockingChangeListener = (DockingChangeListener) dockingChangeListeners.get(emptyChildDock);
			emptyChildDock.removeDockingListener(dockingChangeListener);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

			// Are we empty and there aren't any ghosts?
			if ((isEmpty()) &&
					(ghostTabbedPane == null) &&
					(getParentDock() != null)) {
				getParentDock().emptyChild(this);
			}

			// Repaint.
			SwingUtil.repaintParent(this);
		}

	}

	public void ghostChild(Dock emptyChildDock) {

		// Search the empty child dock.
		// Get the index of the child dock component.
		int position = -1;
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			// Get the child dock.
			Dock dock = (Dock) tabbedPane.getComponentAt(index);
			if (dock.equals(emptyChildDock)) {
				position = index;
				break;
			}
		}
		if (position >= 0) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// Remove the empty child from the list of child docks.
			tabbedPane.remove((Component) emptyChildDock);

			// Remove the docking change listener.
			DockingChangeListener dockingChangeListener = (DockingChangeListener) dockingChangeListeners.get(emptyChildDock);
			emptyChildDock.removeDockingListener(dockingChangeListener);

			// The old tabbed pane becomes the ghost.
			ghostTabbedPane = tabbedPane;
			ghostTabbedPane.setVisible(false);

			// Create and add the tabbed pane for the docks.
			initializeUi();

			// Iterate over the remaining child docks.
			List docksToAdd = new ArrayList();
			for (int index = ghostTabbedPane.getTabCount() - 1; index >= 0; index--) {
				if (index != position) {
					Dock childDock = (Dock) ghostTabbedPane.getComponentAt(index);

					// Remove the dock from the ghost panel.
					ghostTabbedPane.remove((Component) childDock);

					// Add to the docks for the new tabbed pane.
					docksToAdd.add(childDock);
				}
			}
			for (int index = docksToAdd.size() - 1; index >= 0; index--) {
				tabbedPane.add((Component) docksToAdd.get(index));
			}

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));
		}

	}

	public void clearGhosts() {

		if (ghostTabbedPane != null) {
			this.remove(ghostTabbedPane);
			ghostTabbedPane = null;

			// Are we empty?
			if ((isEmpty()) && (getParentDock() != null)) {
				getParentDock().emptyChild(this);
			}
		}

	}

	public DockFactory getChildDockFactory() {
		return childDockFactory;
	}

	public void setChildDockFactory(DockFactory childDockFactory) {

		if (childDockFactory == null) {
			throw new IllegalArgumentException("The child dock factory cannot be null.");
		}

		this.childDockFactory = childDockFactory;

	}

	// Getters / Setters.

	/**
	 * Selects the tab of the child dock.
	 *
	 * @param    dock            The tab for this dock should be selected.
	 */
	public boolean setSelectedDock(Dock dock) {
		if (dock instanceof Component) {
			// Is the dock a child dock?
			for (int index = 0; index < tabbedPane.getTabCount(); index++) {
				if (tabbedPane.getComponentAt(index).equals(dock)) {
					tabbedPane.setSelectedComponent((Component) dock);

					// Repaint.
					SwingUtil.repaintParent(this);

					return true;
				}
			}
		}

		return false;

	}

	/**
	 * Gets the dock that is selected in the composite tab dock.
	 *
	 * @return The dock that is selected in the composite tab dock.
	 */
	public Dock getSelectedDock() {

		// Are there child docks?
		if (tabbedPane.getTabCount() > 0) {
			return (Dock) tabbedPane.getSelectedComponent();
		}

		// There are no child docks.
		return null;

	}

	/**
	 * <p>
	 * Gets the position where the headers of the docks are placed.
	 * </p>
	 * <p>
	 * It should be one of the following values:
	 * <ul>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * </ul>
	 *
	 * <p>
	 * The default value is {@link Position#TOP}.
	 * </p>
	 *
	 * @return The position where the headers of the docks are placed.
	 */
	public int getHeaderPosition() {

		switch (tabbedPane.getTabPlacement()) {
			case JTabbedPane.TOP:
				return Position.TOP;
			case JTabbedPane.BOTTOM:
				return Position.BOTTOM;
			case JTabbedPane.LEFT:
				return Position.LEFT;
			case JTabbedPane.RIGHT:
				return Position.RIGHT;
			default:
				throw new IllegalStateException("Illegal placement of tabs [" + tabbedPane.getTabPlacement() + "].");
		}

	}

	/**
	 * <p>
	 * Sets the position where the headers of the docks are placed.
	 * </p>
	 * <p>
	 * It should be one of the following values:
	 * <ul>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * </ul>
	 *
	 * @param headerPosition The position where the headers of the docks are placed.
	 * @throws IllegalArgumentException If the given postion is not Position.TOP, Position.BOTTOM, Position.LEFT or Position.RIGHT.
	 */
	public void setHeaderPosition(int headerPosition) {

		switch (headerPosition) {
			case Position.TOP:
				tabbedPane.setTabPlacement(JTabbedPane.TOP);
			case Position.BOTTOM:
				tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
			case Position.LEFT:
				tabbedPane.setTabPlacement(JTabbedPane.LEFT);
			case Position.RIGHT:
				tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
			default:
				throw new IllegalStateException("Illegal position for the headers of the dockables [" + headerPosition + "].");
		}
	}

	/**
	 * Gets the tabbed pane that contains the dockables.
	 *
	 * @return The tabbed pane that contains the dockables.
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	// Protected methods.

	/**
	 * Computes the relative rectangle in this dock in which docking has priority.
	 * The given rectangle is set to the position and size of this
	 * priority rectangle. This rectangle is calculated as follows:
	 * <ul>
	 * <li>x = dock width  * priorityRectangleRelativeLeftOffset</li>
	 * <li>y = dock height * priorityRectangleRelativeTopOffset </li>
	 * <li>width  = dock width   * ( 1 - priorityRectangleRelativeLeftOffset - priorityRectangleRelativeRightOffset  )</li>
	 * <li>height = dock height  * ( 1 - priorityRectangleRelativeTopOffset  - priorityRectangleRelativeBottomOffset )</li>
	 * </ul>
	 *
	 * @param    rectangle The rectangle that will get the position and size of the priority rectangle for this dock.
	 */
	protected void getPriorityRectangle(Rectangle rectangle) {
		Dimension size = getSize();
		rectangle.setBounds((int) (size.width * priorityRectangleRelativeLeftOffset),
				(int) (size.height * priorityRectangleRelativeTopOffset),
				(int) (size.width * (1 - priorityRectangleRelativeLeftOffset - priorityRectangleRelativeRightOffset)),
				(int) (size.height * (1 - priorityRectangleRelativeTopOffset - priorityRectangleRelativeBottomOffset)));
	}

	/**
	 * Creates a title for the dockable.
	 * <p>
	 * When the dockable is a composite, the title is a combimation of the children, sepatated by a ','.
	 *
	 * @param dockable The dockable for which a title is created.
	 *                 Can be a composite dockable. Not null.
	 * @return A title for the dockable.
	 */
	protected String getTitle(Dockable dockable) {

		if (dockable instanceof CompositeDockable) {
			StringBuilder title = new StringBuilder();
			CompositeDockable compositeDockable = (CompositeDockable) dockable;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				title.append(getTitle(compositeDockable.getDockable(index)));
			}
			return title.toString();
		}

		if (dockable.getTitle() != null) {
			return dockable.getTitle();
		}

		return "";

	}

	/**
	 * Creates the tabbed pane for the child docks and adds it to this dock.
	 */
	private void initializeUi() {

		// Create the tabbed pane.
		tabbedPane = DockingManager.getComponentFactory().createJTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

	}

	// Private classes.

	private class TabChangelistener implements ChangeListener {

		// Implementations of ChangeListener.

		public void stateChanged(ChangeEvent changeEvent) {
			TabDockV6Addition.repaintTabComponents(tabbedPane);
		}

	}

	private class DockingChangeListener implements DockingListener {

		// Fields.

		private Dock childDock;

		// Constructor.

		public DockingChangeListener(Dock childDock, CompositeTabDock parent) {
			this.childDock = childDock;

			dockingChangeListeners.put(parent, childDock);
		}

		// Implementations of DockingListener.

		public void dockingChanged(DockingEvent dockingEvent) {

			// Something changed in the docking of this child dock. Reset the title of this child.
			for (int index = 0; index < getChildDockCount(); index++) {
				if (getChildDock(index).equals(childDock)) {
					// Add the component in a tab and select the tab.
					Component header = (Component) DockingManager.getComponentFactory().createCompositeTabDockHeader(childDock, getHeaderPosition());
					TabDockV6Addition.setTabComponentAt(tabbedPane, index, header);
				}
			}
		}

		public void dockingWillChange(DockingEvent dockingEvent) {
		}

	}

}
