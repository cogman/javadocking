package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DragListener;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a dock that can contain zero, one or multiple dockables.
 * The dockables are organized in the tabs of a tabbed pane.
 * The tabbed pane of this dock is created with the component factory of the docking manager
 * ({@link com.javadocking.DockingManager#getComponentFactory()}) with the method
 * {@link com.javadocking.component.SwComponentFactory#createJTabbedPane()}.
 * </p>
 * <p>
 * Information on using tab docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html#TabDock" target="_blank">How to Use Laef Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * This is a leaf dock. It cannot contain other docks.
 * </p>
 * <p>
 * When it contains no dockable it is empty. It is never full.
 * </p>
 * <p>
 * A dockable can be docked in this dock if:
 * <ul>
 * <li>it has {@link DockingMode#TAB} as possible docking mode.</li>
 * <li>its content component is not null.</li>
 * </ul>
 * A composite dockable can also be docked in this dock if:
 * <ul>
 * <li>all of its child dockables have {@link DockingMode#TAB} as possible docking mode.</li>
 * <li>all of its child dockables have a content component that is not null.</li>
 * </ul>
 *
 * <p>
 * If the mouse is inside the priority rectangle, the dockable can be docked with priority (see {@link Priority#CAN_DOCK_WITH_PRIORITY}).
 * When the mouse is inside the panel of this dock, but outside the priority rectangle,
 * the dockable can be docked without priority (see {@link Priority#CAN_DOCK}).
 * The priority rectangle is a rectangle in the middle of the dock and retrieved with {@link #getPriorityRectangle(Rectangle)}.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Position} for dockables docked in this dock are one-dimensional.
 * The first position value of a child dockable is between 0 and the number of child dockables minus 1;
 * it is the index of its tab.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class TabDock extends JPanel implements LeafDock, DockableHider {
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
	 * The name of the <code>dockableIds</code> property.
	 */
	private static final String PROPERTY_DOCKABLE_IDS = "dockableIds";
	/**
	 * The name of the <code>selectedDockableId</code> property.
	 */
	private static final String PROPERTY_SELECTED_DOCKABLE_ID = "selectedDockableId";

	// Fields.

	/**
	 * The mapping between the components used for the dockables and the dockables that are docked in this dock.
	 */
	private Map panelDockableMapping = new HashMap();
	/**
	 * The mapping between the contents of the dockable and the components that are used for the dockables.
	 */
	private Map contentPanelMapping = new HashMap();
	/**
	 * The mapping between the dockables and the listeners for description changes.
	 */
	private Map descriptionListenerMapping = new HashMap();

	/**
	 * The parent dock of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The tabbed pane that contains the components of the dockables.
	 */
	private JTabbedPane tabbedPane;
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

	// For hiding.
	private List hiddenDockables = new ArrayList();


	// Constructors.

	/**
	 * Constructs a tab dock.
	 */
	public TabDock() {

		// Set the layout.
		super(new BorderLayout());

		// Create the tabbed pane.
		tabbedPane = DockingManager.getComponentFactory().createJTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);

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
	 * <li>if it has DockingMode.TAB as possible docking mode.</li>
	 * <li>if its content component is not null.</li>
	 * </ul>
	 * A composite dockable can also be docked in this dock if:
	 * <ul>
	 * <li>all of its child dockables have {@link DockingMode#TAB} as possible docking mode.</li>
	 * <li>all of its child dockables have a content component that is not null.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a tabbed dock.
		if ((dockable.getDockingModes() & DockingMode.TAB) == 0) {
			return Priority.CANNOT_DOCK;
		}

		// Is the component of the dockable not null?
		if (dockable.getContent() != null) {
			// If the tab dock is empty, we can dock with priority.
			if (isEmpty()) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

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

			// Iterate over the child dockables.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Is the object of the child not null? 
				if (compositeDockable.getDockable(index).getContent() == null) {
					// The component is null.
					return Priority.CANNOT_DOCK;
				}
			}

			// All the children are OK.
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

		// Do we have dockable with a component that is not null.
		Component dockableContent = dockableToAdd.getContent();
		if (dockableContent != null) {
			// Create the panel for the dockable.
			Component dockablePanel = createComponentOfDockable(dockableToAdd);

			// Inform the listeners.
			dockingEventSupport.fireDockingWillChange(new DockableEvent(this, null, this, dockableToAdd));

			// Add the component in a tab and select the component.
			dockableToAdd.setState(DockableState.NORMAL, this);
			Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(dockableToAdd, getHeaderPosition());
			TabDockV6Addition.addTab(tabbedPane, dockablePanel, header);
			if (dockableToAdd.getDescription() != null) {
				tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, dockableToAdd.getDescription());
			}

			// Listen to changes of the description.
			DockableChangeListener changeListener = new DockableChangeListener(dockableToAdd);
			dockableToAdd.addPropertyChangeListener(changeListener);
			descriptionListenerMapping.put(dockableToAdd, changeListener);

			tabbedPane.setSelectedComponent(dockablePanel);
			panelDockableMapping.put(dockablePanel, dockableToAdd);
			contentPanelMapping.put(dockableToAdd.getContent(), dockablePanel);
			dockableToAdd.setLastDockingMode(DockingMode.TAB);

			// Inform the listeners.
			dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockableToAdd));
		}

		// Do we have a composite dockable?
		if (dockableToAdd instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockableToAdd;

			// Get the selected dockable.
			Dockable selectedDockable = compositeDockable.getSelectedDockable();

			// Iterate over the child dockables.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Is the component of the child not null? 
				Dockable childDockable = compositeDockable.getDockable(index);
				Component childDockableContent = childDockable.getContent();
				if (childDockableContent != null) {
					// Create the panel for the dockable.
					Component dockablePanel = createComponentOfDockable(childDockable);

					// Add the child dockable as tab.
					childDockable.setState(DockableState.NORMAL, this);
					Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(childDockable, getHeaderPosition());
					TabDockV6Addition.addTab(tabbedPane, dockablePanel, header);
					if (childDockable.getDescription() != null) {
						tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, childDockable.getDescription());
					}

					// Listen to changes of the description.
					DockableChangeListener descriptionListener = new DockableChangeListener(childDockable);
					dockableToAdd.addPropertyChangeListener(descriptionListener);
					descriptionListenerMapping.put(childDockable, descriptionListener);

					panelDockableMapping.put(dockablePanel, childDockable);
					contentPanelMapping.put(childDockable.getContent(), dockablePanel);
					childDockable.setLastDockingMode(DockingMode.TAB);

					// Do we have to select the tab of this dockable?
					if ((selectedDockable != null) && (childDockable.equals(selectedDockable))) {
						tabbedPane.setSelectedComponent(dockablePanel);
					}
				}
			}
			compositeDockable.setDock(this);
		}

		// Repaint.
		SwingUtil.repaintParent(this);

		// The add was successful.
		return true;

	}

	public boolean canRemoveDockable(Dockable dockableToRemove) {

		// Is the dockable in the list of dockables?
		if (panelDockableMapping.values().contains(dockableToRemove)) {
			return true;
		}

		// Is the given dockable a composite dockable and does this dock contains every child?
		if (dockableToRemove instanceof CompositeDockable) {
			// Iterate over the child dockables.
			CompositeDockable compositeDockable = (CompositeDockable) dockableToRemove;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				if (!canRemoveDockable(compositeDockable.getDockable(index))) {
					// We can not remove this dockable.
					return false;
				}
			}

			// We can remove all the dockables.
			return true;
		}

		// We couldn't find the dockable, so we can't remove it.
		return false;

	}

	public boolean removeDockable(Dockable dockableToRemove) {
		// Verify the conditions for removing the dockable.
		if (!canRemoveDockable(dockableToRemove)) {
			return false;
		}

		// Is the component of the dockable not null?
		Component dockableComponent = dockableToRemove.getContent();
		if (dockableComponent != null) {
			// The panel of the dockable.
			Component dockablePanel = (Component) contentPanelMapping.get(dockableToRemove.getContent());

			// Remove the dockable from the map with dockables.
			panelDockableMapping.remove(dockablePanel);
			contentPanelMapping.remove(dockableComponent);
			PropertyChangeListener descriptionListener = (PropertyChangeListener) descriptionListenerMapping.remove(dockableToRemove);
			dockableToRemove.removePropertyChangeListener(descriptionListener);

			// Search the tab that contains the component of the dockable.
			for (int index = 0; index < tabbedPane.getTabCount(); index++) {
				if (tabbedPane.getComponentAt(index).equals(dockablePanel)) {
					// Inform the listeners about the removal.
					dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, null, dockableToRemove));

					// Remove the dockable.
					tabbedPane.remove(index);
					TabDockV6Addition.repaintTabComponents(tabbedPane);
					dockableToRemove.setState(DockableState.CLOSED, null);

					// Inform the listeners about the removal.
					dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, dockableToRemove));

					return true;
				}
			}
		}

		// Do we have a composite dockable and does this dock contains every child?
		if (dockableToRemove instanceof CompositeDockable) {
			// Iterate over the child dockables.
			CompositeDockable compositeDockable = (CompositeDockable) dockableToRemove;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Remove this dockable.
				if (!removeDockable(compositeDockable.getDockable(index))) {
					// We can not remove this dockable.
					return false;
				}
			}

			// We removed all the dockables.
			compositeDockable.setDock(null);
			return true;
		}

		throw new IllegalStateException("Couldn't find a tab with the component of the dockable");

	}

	public boolean isEmpty() {
		return panelDockableMapping.size() == 0;
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

	public void saveProperties(String prefix, Properties properties, Map childDocks) {
		// Save the IDs of the dockables.
		String[] dockableIdArray = new String[tabbedPane.getTabCount()];
		for (int index = 0; index < dockableIdArray.length; index++) {
			// Get the ID of the dockable.
			dockableIdArray[index] = ((Dockable) panelDockableMapping.get(tabbedPane.getComponentAt(index))).getID();
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);

		// Is there a dockable maximized?
		String maximizedDockableId = null;
		for (int index = 0; index < getDockableCount(); index++) {
			Dockable dockable = getDockable(index);
			if (dockable.getState() == DockableState.MAXIMIZED) {
				maximizedDockableId = dockable.getID();
				break;
			}
		}

		// Save the ID of the selected child.
		if (maximizedDockableId != null) {
			PropertiesUtil.setString(properties, prefix + PROPERTY_SELECTED_DOCKABLE_ID, maximizedDockableId);
		} else {
			Component selectedComponent = tabbedPane.getSelectedComponent();
			if (selectedComponent != null) {
				Dockable dockable = retrieveDockableOfComponent(selectedComponent);
				if (dockable != null) {
					PropertiesUtil.setString(properties, prefix + PROPERTY_SELECTED_DOCKABLE_ID, dockable.getID());
				}
			}
		}
	}

	public void loadProperties(String prefix, Properties properties, Map childDockIds, Map dockablesMap, Window owner) throws IOException {

		// Load the IDs of the dockables.
		String[] dockableIdArray = new String[0];
		dockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);

		// Iterate over the IDs of the dockables.
		for (int index = 0; index < dockableIdArray.length; index++) {
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(dockableIdArray[index]);
			if (dockableObject != null) {
				if (dockableObject instanceof Dockable) {
					Dockable dockable = (Dockable) dockableObject;

					// Try to add the dockable.
					addDockable(dockable, new Position(this.panelDockableMapping.size()));
				} else {
					throw new IOException("The values in the dockables mapping should be of type com.javadocking.Dockable.");
				}
			}

		}

		// Select the selected dockable.
		boolean hasSelectedDockable = false;
		String selectedDockableId = null;
		selectedDockableId = PropertiesUtil.getString(properties, prefix + PROPERTY_SELECTED_DOCKABLE_ID, selectedDockableId);
		if (selectedDockableId != null) {
			// Try to get the dockable.
			Object selectedDockableObject = dockablesMap.get(selectedDockableId);
			if (selectedDockableObject instanceof Dockable) {
				Dockable selectedDockable = (Dockable) selectedDockableObject;

				// Try to select the dockable.
				hasSelectedDockable = setSelectedDockable(selectedDockable);
			}
		}

		// Couldn't we select a dockable?
		if (!hasSelectedDockable) {
			// Select the first dockable.
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

	// Implementations of LeafDock.

	public Dockable getDockable(int index) throws IndexOutOfBoundsException {
		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dockable) panelDockableMapping.get(tabbedPane.getComponentAt(index));
	}

	public int getDockableCount() {
		return panelDockableMapping.size();
	}

	public boolean containsDockable(Dockable dockable) {
		return panelDockableMapping.values().contains(dockable);
	}

	public boolean moveDockable(Dockable dockableToMove, Point relativeLocation) {

		// Don't move a composite dockable. 
		if (dockableToMove instanceof CompositeDockable) {
			return false;
		}

		// Check if the dockable is docked in this dock.
		if (!panelDockableMapping.values().contains(dockableToMove)) {
			throw new IllegalArgumentException("The dockable should be docked in this dock.");
		}

		// The content and the panel of the the dockable.
		Component dockableContent = dockableToMove.getContent();
		Component dockablePanel = (Component) contentPanelMapping.get(dockableContent);

		// Check if we are above a tab.
		int newTabIndex = tabbedPane.indexAtLocation(relativeLocation.x, relativeLocation.y);
		if (newTabIndex < 0) {
			// Is the mouse over the dockable itself?
			Point relativeLocationInDockable = new Point(relativeLocation);
			relativeLocationInDockable = SwingUtilities.convertPoint(this, relativeLocationInDockable, dockablePanel);
			if (dockablePanel.contains(relativeLocationInDockable)) {
				// The mouse is over the dockable itself, so don't move it.
				return false;
			} else {
				newTabIndex = panelDockableMapping.size() - 1;
			}
		}

		// Get the current tab number of this dockable.
		int previousTabIndex = tabbedPane.indexOfComponent(dockablePanel);

		// If the indices are the same, we don't have to move the dockable.
		if (previousTabIndex == newTabIndex) {
			return false;
		}

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, this, dockableToMove));


		// Set the new index.
		tabbedPane.removeTabAt(previousTabIndex);
		Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(dockableToMove, getHeaderPosition());
		TabDockV6Addition.insertTab(tabbedPane, newTabIndex, dockablePanel, header);

		tabbedPane.setSelectedIndex(newTabIndex);

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, this, dockableToMove));

		return true;

	}

	public Position getDockablePosition(Dockable dockable) throws IllegalArgumentException {

		// Get the panel of the dockable.
		Component dockablePanel = (Component) contentPanelMapping.get(dockable.getContent());
		int position = tabbedPane.indexOfComponent(dockablePanel);
		if (position >= 0) {
			return new Position(position);
		}

		throw new IllegalArgumentException("The dockable is not docked in this dock.");

	}

	public void addDockable(Dockable dockableToAdd, Position position) {

		// Get the position in the tabs.
		int tabPosition = getDockableCount();
		if (position.getDimensions() == 1) {
			if ((position.getPosition(0) >= 0) && (position.getPosition(0) <= getDockableCount())) {
				tabPosition = position.getPosition(0);
			}
		}

		// Do we have dockable with a component that is not null?
		Component dockableContent = dockableToAdd.getContent();
		if (dockableContent != null) {
			// Create the panel for the dockable.
			Component dockablePanel = createComponentOfDockable(dockableToAdd);


			// Insert a tab with the dockable.
			// Add the component in a tab and select the component.
			dockableToAdd.setState(DockableState.NORMAL, this);
			Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(dockableToAdd, getHeaderPosition());
			TabDockV6Addition.insertTab(tabbedPane, tabPosition, dockablePanel, header);
			if (dockableToAdd.getDescription() != null) {
				tabbedPane.setToolTipTextAt(tabPosition, dockableToAdd.getDescription());
			}

			// Listen to changes of the description.
			DockableChangeListener changeListener = new DockableChangeListener(dockableToAdd);
			dockableToAdd.addPropertyChangeListener(changeListener);
			descriptionListenerMapping.put(dockableToAdd, changeListener);

			tabbedPane.setSelectedComponent(dockablePanel);
			panelDockableMapping.put(dockablePanel, dockableToAdd);
			contentPanelMapping.put(dockableToAdd.getContent(), dockablePanel);
			dockableToAdd.setLastDockingMode(DockingMode.TAB);

		}

		// Do we have a composite dockable?
		if (dockableToAdd instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockableToAdd;

			// Get the selected dockable.
			Dockable selectedDockable = compositeDockable.getSelectedDockable();

			// Iterate over the child dockables.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Is the object of the child not null? 
				Dockable childDockable = compositeDockable.getDockable(index);
				Component childDockableContent = childDockable.getContent();

				if (childDockableContent != null) {
					// Create the panel for the child dockable.
					Component childDockablePanel = createComponentOfDockable(childDockable);

					// Insert a tab with the child dockable.
					// Add the component in a tab and select the component.
					childDockable.setDock(this);
					Component header = (Component) DockingManager.getComponentFactory().createTabDockHeader(childDockable, getHeaderPosition());
					TabDockV6Addition.insertTab(tabbedPane, tabPosition, childDockablePanel, header);

					if (childDockable.getDescription() != null) {
						tabbedPane.setToolTipTextAt(tabPosition, childDockable.getDescription());
					}

					// Listen to changes of the description.
					DockableChangeListener descriptionListener = new DockableChangeListener(childDockable);
					dockableToAdd.addPropertyChangeListener(descriptionListener);
					descriptionListenerMapping.put(childDockable, descriptionListener);

					panelDockableMapping.put(childDockablePanel, childDockable);
					contentPanelMapping.put(childDockable.getContent(), childDockablePanel);
					tabPosition++;

					// Do we have to select this dockable?
					if ((selectedDockable != null) && (childDockable.equals(selectedDockable))) {
						tabbedPane.setSelectedComponent(childDockablePanel);
					}
				}
			}
			compositeDockable.setDock(this);
		}


		// Repaint.
		SwingUtil.repaintParent(this);

	}

	// Implementations of DockableHider.

	public void hideDockable(Dockable dockableToHide) throws IllegalArgumentException {

		// Check if the dockable is docked in this dock.
		if (!panelDockableMapping.containsValue(dockableToHide)) {
			throw new IllegalArgumentException("The dockable is not docked in this dock.");
		}

		// Check if the dockable is not already hidden.
		if (hiddenDockables.contains(dockableToHide)) {
			throw new IllegalStateException("The dockable is already hidden.");
		}

		// Hide the dockable.
		hiddenDockables.add(dockableToHide);

		// Get the panel of the dockable.
		JPanel dockablePanel = (JPanel) contentPanelMapping.get(dockableToHide.getContent());
		dockablePanel.removeAll();

		// Repaint.
		dockablePanel.revalidate();
		dockablePanel.repaint();

	}

	public int getHiddenDockableCount() {
		return hiddenDockables.size();
	}

	public Dockable getHiddenDockable(int index) {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getHiddenDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dockable) hiddenDockables.get(index);
	}


	public void restoreDockable(Dockable dockableToRestore) {

		// Check if the dockable is docked in this dock.
		if (!(panelDockableMapping.values().contains(dockableToRestore))) {
			throw new IllegalArgumentException("The dockable is not hidden.");
		}

		// Check if the dockable is hidden.
		if (!(hiddenDockables.contains(dockableToRestore))) {
			throw new IllegalStateException("The dockable is not hidden.");
		}

		// Restore the dockable.
		hiddenDockables.remove(dockableToRestore);

		// Get the panel of the dockable.
		JPanel dockablePanel = (JPanel) contentPanelMapping.get(dockableToRestore.getContent());
		dockablePanel.add(dockableToRestore.getContent());

		// Repaint.
		dockablePanel.revalidate();
		dockablePanel.repaint();

	}

	// Public methods.

	/**
	 * Retrieves the dockable of this dock that has the given component as content.
	 *
	 * @param component The component of the dockable that is retrieved in the tabbed pane.
	 * @return The dockable in the given dock that has the given component as inner component,
	 * if it exists, null otherwise.
	 */
	public Dockable retrieveDockableOfComponent(Component component) {

		if (!(component instanceof JPanel)) {
			return null;
		}
		if (((JPanel) component).getComponentCount() > 0) // Otherwise, exception when dockable is maximized
		{
			// Get the first child component.
			Component dockableContent = ((JPanel) component).getComponent(0);

			// Iterate over the dockables of the dock.
			for (int index = 0; index < getDockableCount(); index++) {
				// Verify if te inner component is the given component.
				if (dockableContent.equals(getDockable(index).getContent())) {
					return getDockable(index);
				}
			}
		}

		return null;

	}

	/**
	 * Creates the component that contains the content of the dockable.
	 * If this method is overwritten {@link #retrieveDockableOfComponent(Component)}
	 * should be made compatible.
	 *
	 * @param dockable The dockable for whicha a component has to be created.
	 * @return The component for the dockable.
	 */
	protected JPanel createComponentOfDockable(Dockable dockable) {

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(dockable.getContent(), BorderLayout.CENTER);
		return panel;

	}

	// Getters / Setters.

	/**
	 * Selects the tab of the dockable.
	 * If the dockable is a composite, the selected dockable of the composite
	 * will be selected. If the composite doesn't have a selected dockable, the first child
	 * of the composite dockable is selected.
	 *
	 * @param    dockable        The tab for this dockable should be selected.
	 * @return True if the dockable could be selected, false otherwise.
	 */
	public boolean setSelectedDockable(Dockable dockable) {

		boolean selected = false;

		// Do we have dockable with a component that is not null.
		Component dockableContent = dockable.getContent();
		if (dockableContent != null) {
			// Get the panel of the dockable.
			Component dockablePanel = (Component) contentPanelMapping.get(dockableContent);
			int indexToSelect = tabbedPane.indexOfComponent(dockablePanel);
			if (indexToSelect >= 0) {
				tabbedPane.setSelectedIndex(indexToSelect);
				selected = true;
			}
		} else if (dockable instanceof CompositeDockable) {
			// We have a composite dockable.
			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// Get the selected dockable of the composite.
			Dockable selectedDockable = compositeDockable.getSelectedDockable();
			if (selectedDockable == null) {
				if (compositeDockable.getDockableCount() > 0) {
					// Select the first child of the composite.
					selectedDockable = compositeDockable.getDockable(0);
				}
			}

			// Select the dockable.
			if (selectedDockable != null) {
				selected = setSelectedDockable(selectedDockable);
			}
		}


		// Repaint.
		SwingUtil.repaintParent(this);

		return selected;

	}

	/**
	 * Gets the dockable that is selected in the tab dock.
	 *
	 * @return The dockable that is selected in the tab dock.
	 */
	public Dockable getSelectedDockable() {

		// Are there dockables?
		if (tabbedPane.getTabCount() > 0) {
			// Get the component of the selected dockable.
			Component selectedComponent = tabbedPane.getSelectedComponent();
			return (Dockable) panelDockableMapping.get(selectedComponent);
		}

		// There are no dockables.
		return null;

	}

	/**
	 * <p>
	 * Gets the position where the headers of the dockables are placed.
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
	 * @return The position where the headers of the dockables are placed.
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
	 * Sets the position where the headers of the dockables are placed.
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
	 * @param headerPosition The position where the headers of the dockables are placed.
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

	// Private classes.

	private class TabChangelistener implements ChangeListener {

		// Implementations of ChangeListener.

		public void stateChanged(ChangeEvent changeEvent) {
			TabDockV6Addition.repaintTabComponents(tabbedPane);
		}

	}

	/**
	 * Changes the tooltip text of the tab header, when the description of the dockable changes.
	 */
	private class DockableChangeListener implements PropertyChangeListener {

		private Dockable dockable;

		// Constructors.

		public DockableChangeListener(Dockable dockable) {
			this.dockable = dockable;
		}

		// Implementations of PropertyChangeListener.

		public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
			if (propertyChangeEvent.getPropertyName().equals("description")) {
				Position position = getDockablePosition(dockable);
				int index = position.getPosition(0);
				tabbedPane.setToolTipTextAt(index, dockable.getDescription());
			} else if (propertyChangeEvent.getPropertyName().equals("title")) {
				Position position = getDockablePosition(dockable);
				int index = position.getPosition(0);
				tabbedPane.setTitleAt(index, dockable.getTitle());
			} else if (propertyChangeEvent.getPropertyName().equals("icon")) {
				Position position = getDockablePosition(dockable);
				int index = position.getPosition(0);
				tabbedPane.setIconAt(index, dockable.getIcon());
			}
		}

	}


}
