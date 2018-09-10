package com.javadocking.dock;

import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.event.ChildDockEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a composite dock that has child docks that are organized in a grid.
 * This dock can not contain dockables. When dockables are added, child docks are created and the
 * dockables are added to the child docks.
 * </p>
 * <p>
 * Information on using composite grid docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html#CompositeGridDock" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * The positions for child docks of this dock are one-dimensional.
 * The first position value of a child dock is between 0 and the number of child docks minus 1,
 * it is the position in the grid.
 * </p>
 * <p>
 * A dockable can be added to this dock if:<ul>
 * <li>it has as possible docking mode {@link DockingMode#GRID}.</li>
 * <li>the dock factory can create a child dock for the given dockable.
 * If the dockable is a {@link CompositeDockable}, but a child dock could not be created for the composite,
 * the dockable can be added, if a child dock can be created for every child dockable of the composite.</li>
 * </ul>
 *
 * <p>
 * When a dockable is added, a child dock is created with the 'childDockFactory'. The dockable is added to
 * the child dock.
 * </p>
 * <p>
 * This kind of dock is never full. It is empty when there are 0 child docks.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class CompositeGridDock extends JPanel implements CompositeDock {

	// Static fields.

	/**
	 * With this fill mode the number of rows and columns are equal or there is one more column.
	 */
	public static final int FILL_SQUARE_HORIZONTAL = 0;
	/**
	 * With this fill mode the number of rows and columns are equal or there is one more row.
	 */
	public static final int FILL_SQUARE_VERTICAL = 1;
	/**
	 * With this fill mode the number of columns and rows is calculated to fill the panel of the grid optimally.
	 * It takes the size of the panel and the preferred sizes of the child docks into account. There is a preference
	 * for first adding columns.
	 */
	public static final int FILL_FLOW_HORIZONTAL = 2;
	/**
	 * With this fill mode the number of columns and rows is calculated to fill the panel of the grid optimally.
	 * It takes the size of the panel and the preferred sizes of the child docks into account. There is a preference
	 * for first adding rows.
	 */
	public static final int FILL_FLOW_VERTICAL = 3;
	/**
	 * The relative offset of the center priority rectangle when there are no child docks.
	 */
	private static final double centerPriorityRectangleRelativeOffset = 2.0 / 8.0;
	/**
	 * The relative offset of the center priority rectangle when there are no child docks.
	 */
	private static final double leftPriorityRectangleRelativeOffset = 1.0 / 8.0;
	/**
	 * The relative offset of the center priority rectangle when there are no child docks.
	 */
	private static final double rightPriorityRectangleRelativeOffset = 1.0 / 8.0;


	// Fields.
	/**
	 * The parent of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The child docks of this dock.
	 */
	private List childDocks = new ArrayList();
	/**
	 * This factory creates the child docks.
	 */
	private DockFactory childDockFactory;
	/**
	 * The number of columns in the grid.
	 */
	private int columnCount = 1;
	/**
	 * The fill mode determines the strategy for organizing the child docks in rows and columns.
	 * The default fill mode is FILL_FLOW_HORIZONTAL.
	 */
	private int fillMode = FILL_FLOW_HORIZONTAL;

	/**
	 * This is the rectangle in which a dockable can be docked with priority.
	 * We keep it as field because we don't want to create every time a new rectangle.
	 */
	private Rectangle priorityRectangle = new Rectangle();
	/**
	 * This is a rectangle for doing calculations.
	 * We keep it as field because we don't want to create every time a new rectangle.
	 */
	private Rectangle helpRectangle = new Rectangle();
	/**
	 * This is the deepest panel that contains the child docks.
	 */
	private JPanel dockPanel;
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();


	// Ghosts.
	/**
	 * This is an old <code>dockPanel</code> that has to be removed later. It is already made invisible.
	 * It cannot be removed now because it contains an old dock that still has listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private JPanel ghostDockPanel;

	// Constructors.

	/**
	 * Constructs a composite grid dock with a {@link SingleDockFactory}
	 * as factory for creating the child docks.
	 */
	public CompositeGridDock() {
		this(new SingleDockFactory());
	}

	/**
	 * Constructs a composite grid dock with the given factory for the creating child docks.
	 *
	 * @param childDockFactory The factory for creating the child docks.
	 */
	public CompositeGridDock(DockFactory childDockFactory) {

		// Set the layout.
		super(new BorderLayout());

		// Set the properties.
		this.childDockFactory = childDockFactory;

		// Create and add the panel with the docks.
		initializeUi(1);
	}

	// Implementations of Dock.

	/**
	 * <p>
	 * Determines if the dockable can be added.
	 * </p>
	 * <p>
	 * A dockable can be added in if:
	 * <ul>
	 * <li>the method {@link #checkDockingModes(Dockable)} returns <code>true</code>.</li>
	 * <li>the dock factory can create a child dock for the given dockable.
	 * If the dockable is a {@link CompositeDockable}, but a child dock could not be created for the composite,
	 * the dockable can be added, if a child dock can be created for every child dockable of the composite.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a grid dock.
		if (!checkDockingModes(dockable)) {
			return Priority.CANNOT_DOCK;
		}

		// We can dock if the dock factory can create a dock.
		if (childDockFactory.createDock(dockable, getDockingMode()) != null) {
			// Can we dock with priority?
			if (canAddDockableWithPriority(dockable, relativeLocation)) {
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
				if (childDockFactory.createDock(childDockable, getDockingMode()) == null) {
					return Priority.CANNOT_DOCK;
				}

				// Can this dockable be added in a line dock?
				if (!checkDockingModes(childDockable)) {
					return Priority.CANNOT_DOCK;
				}

				// Is the component of the dockable not null?
				if (childDockable.getContent() == null) {
					// We don't want deeper nested dockables.
					return Priority.CANNOT_DOCK;
				}

				index++;
			}

			// If we are here, we can dock.
			// Can we dock with priority?
			if (canAddDockableWithPriority(dockable, relativeLocation)) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

			// We can dock, but not with priority.
			return Priority.CAN_DOCK;

		}

		return Priority.CANNOT_DOCK;

	}

	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation,
										Point dockableOffset, Rectangle rectangle) {

		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {

			// Are there no child docks already?
			if (childDocks.size() == 0) {
				// The docking rectangle is the rectangle defined by this dock panel.
				rectangle.setBounds(0, 0, getSize().width, getSize().height);
			} else {
				// Get the position for the dockable.
				int dockPosition = getDockPosition(dockable, relativeLocation);

				if (dockPosition == childDocks.size()) {
					Component childDock = (Component) childDocks.get(childDocks.size() - 1);
					rectangle.setSize(childDock.getWidth(), childDock.getHeight());
					rectangle.setLocation(childDock.getLocation().x + childDock.getWidth(), childDock.getLocation().y);
					//TODO if it is not visible.
				} else if (dockPosition == 0) {
					Component childDock = (Component) childDocks.get(0);
					rectangle.setSize(childDock.getWidth() / 2, childDock.getHeight());
					rectangle.setLocation(childDock.getLocation().x, childDock.getLocation().y);
				} else {
					Component childDockBefore = (Component) childDocks.get(dockPosition - 1);
					Component childDockAfter = (Component) childDocks.get(dockPosition);
					if (childDockBefore.getLocation().x < childDockAfter.getLocation().x) {
						rectangle.setSize(childDockBefore.getWidth() / 2 + childDockAfter.getWidth() / 2, childDockBefore.getHeight());
						rectangle.setLocation(childDockBefore.getLocation().x + childDockBefore.getWidth() / 2, childDockBefore.getLocation().y);
					} else {
						helpRectangle.setBounds(childDockBefore.getLocation().x,
								childDockBefore.getLocation().y,
								childDockBefore.getSize().width,
								childDockBefore.getSize().height);
						if (helpRectangle.contains(relativeLocation)) {
							rectangle.setSize(childDockBefore.getWidth() / 2, childDockBefore.getHeight());
							rectangle.setLocation(childDockBefore.getLocation().x + childDockBefore.getWidth() / 2, childDockBefore.getLocation().y);
						} else {
							rectangle.setSize(childDockAfter.getWidth() / 2, childDockAfter.getHeight());
							rectangle.setLocation(childDockAfter.getLocation().x, childDockAfter.getLocation().y);
						}
					}
				}
			}
		}

		return priority;

	}

	public boolean addDockable(Dockable dockableToAdd, Point relativeLocation, Point dockableOffset) {

		// Verify the conditions for adding the dockable.
		if (getDockPriority(dockableToAdd, relativeLocation) == Priority.CANNOT_DOCK) {
			// We are not allowed to dock the dockable in this dock.
			return false;
		}

		// Get the position for the new dockable.
		int position = getDockPosition(dockableToAdd, relativeLocation);

		// Create the new child dock.
		Dock childDock = childDockFactory.createDock(dockableToAdd, getDockingMode());

		// Could we create a child dock?
		if (childDock != null) {
			childDock.setParentDock(this);

			childDock.addDockable(dockableToAdd, new Point(0, 0), new Point(0, 0));

			// Add the dock.
			addChildDock(childDock, new Position(position));

			// Repaint.
			SwingUtil.repaintParent(this);

			return true;
		}

		// Do we have a composite dockable?
		if (dockableToAdd instanceof CompositeDockable) {
			// Add every child dockable.
			CompositeDockable compositeDockable = (CompositeDockable) dockableToAdd;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Create the new child dock.
				Dock oneChildDock = childDockFactory.createDock(compositeDockable.getDockable(index), getDockingMode());
				if (oneChildDock != null) {
					oneChildDock.setParentDock(this);

					// Add the dock.
					oneChildDock.addDockable(compositeDockable.getDockable(index), new Point(0, 0), new Point(0, 0));
					addChildDock(oneChildDock, new Position(position));
					position++;
				}

			}

			// Repaint.
			SwingUtil.repaintParent(this);

			return true;
		}

		return false;

	}

	public boolean isEmpty() {
		return childDocks.size() == 0;
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

		// Save the fill mode and the column count.
		PropertiesUtil.setInteger(properties, prefix + "fillMode", fillMode);
		PropertiesUtil.setInteger(properties, prefix + "columnCount", columnCount);

		// Save the class of the child dock factory and its properties.
		String className = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", className);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Iterate over the child docks.
		for (Object childDock : childDocks) {
			// Get the child dock.
			Dock dock = (Dock) childDock;

			// Get the ID of the childDock.
			String childDockId = (String) childDockIds.get(dock);

			// Save the position.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(dock));
		}

	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockables, Window owner) throws IOException {

		// Set the fill mode.
		int fillMode = FILL_FLOW_HORIZONTAL;
		fillMode = PropertiesUtil.getInteger(properties, prefix + "fillMode", fillMode);
		setFillMode(fillMode);

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

			// Get the position of this child.
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

		// Get the saved column count and make it as small as possible.
		int savedColumnCount = columnCount;
		savedColumnCount = PropertiesUtil.getInteger(properties, prefix + "columnCount", savedColumnCount);
		int savedRowCount = (int) Math.ceil(childDocks.size() / (double) savedColumnCount);
		while (childDocks.size() <= savedRowCount * (savedColumnCount - 1)) {
			savedColumnCount--;
		}
		if (savedColumnCount <= 0) {
			savedColumnCount = 1;
		}
		if (savedColumnCount != columnCount) {
			rebuildUI(savedColumnCount);
		}
	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	// Implementations of CompositeDock.

	public void addChildDock(Dock dock, Position position) throws IllegalStateException {

		// Get the position in the grid.
		int gridPosition = getChildDockCount();
		if (position.getDimensions() == 1) {
			if ((position.getPosition(0) >= 0) && (position.getPosition(0) <= getChildDockCount())) {
				gridPosition = position.getPosition(0);
			}
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dock));

		// Add the dock to the list of child docks.
		childDocks.add(gridPosition, dock);
		dock.setParentDock(this);

		// Remove and add all the childdocks from the dock panel.
		rebuildUI(calculateColumnCount());

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, dock));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public int getChildDockCount() {
		return childDocks.size();
	}

	public Dock getChildDock(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getChildDockCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dock) childDocks.get(index);

	}

	public Position getChildDockPosition(Dock childDock) throws IllegalArgumentException {

		int position = childDocks.indexOf(childDock);
		if (position >= 0) {
			return new Position(position);
		}

		throw new IllegalArgumentException("The dock is not docked in this composite dock.");

	}

	public void emptyChild(Dock emptyChildDock) {

		// Search the empty child dock.
		if (childDocks.contains(emptyChildDock)) {

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// Remove the empty dock.
			//dockPanel.remove((Component) emptyChildDock);
			childDocks.remove(emptyChildDock);

			// Rebuild.
			rebuildUI(calculateColumnCount());

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

			// Are we empty and there aren't any ghosts?
			if ((isEmpty()) &&
					(ghostDockPanel == null) &&
					(getParentDock() != null)) {
				getParentDock().emptyChild(this);
			}

			// Repaint.
			SwingUtil.repaintParent(this);
		}

	}

	public void ghostChild(Dock emptyChildDock) {

		// Search the empty child dock.
		if (childDocks.contains(emptyChildDock)) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// Remove the empty child from the list of child docks.
			childDocks.remove(emptyChildDock);

			// The old panel becomes the ghost.
			ghostDockPanel = dockPanel;
			ghostDockPanel.setVisible(false);

			// Create and add the panel for the docks.
			initializeUi(calculateColumnCount());

			// Iterate over the remaining child docks.
			for (Object childDock1 : childDocks) {
				Dock childDock = (Dock) childDock1;

				// Remove the dock from the ghost panel.
				ghostDockPanel.remove((Component) childDock);

				// Add the dock to the new panel.
				dockPanel.add((Component) childDock);
			}

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

			// Repaint.
			SwingUtil.repaintParent(this);
		}

	}

	public void clearGhosts() {

		if (ghostDockPanel != null) {
			this.remove(ghostDockPanel);
			ghostDockPanel = null;

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
	 * Determines the strategy for organizing the dockables in rows and columns.
	 * The default fill mode is FILL_FLOW_HORIZONTAL.
	 *
	 * @return The strategy for organizing the dockables in rows and columns.
	 */
	public int getFillMode() {
		return fillMode;
	}

	/**
	 * Sets the strategy for organizing the dockables in rows and columns.
	 *
	 * @param newFillMode The strategy for organizing the dockables in rows and columns.
	 */
	public void setFillMode(int newFillMode) {

		// Do we have a new value?
		if (newFillMode != fillMode) {
			// Set the new fill mode.
			this.fillMode = newFillMode;

			// Rebuild the UI.
			rebuildUI(calculateColumnCount());
		}

	}

	// Protected metods.

	/**
	 * Gets the position where the dockable should be docked in the dock given the mouse position.
	 * The position is the future index of the dockable in the grid. The first position is 0.
	 *
	 * @param newDockable      The dockable to add.
	 * @param relativePosition The relative mouse location in this dock.
	 * @return The position where the dockable should be docked in the dock.
	 * The position is the future index of the dockable in the grid.
	 */
	protected int getDockPosition(Dockable newDockable, Point relativePosition) {

		// Are there no child docks already?
		if (childDocks.size() == 0) {
			return 0;
		}

		// When we are here, there are already dockables in this dock.

		// Iterate over the dockables.
		for (int index = 0; index < childDocks.size(); index++) {

			// Get the child.
			Component childDock = (Component) childDocks.get(index);

			helpRectangle.setBounds(childDock.getLocation().x, childDock.getLocation().y,
					childDock.getSize().width, childDock.getSize().height);

			// Is the mouse above this recangle?
			if (helpRectangle.contains(relativePosition)) {

				// Set the rectangle on the first half of the dock.
				helpRectangle.setSize(childDock.getSize().width / 2, childDock.getSize().height);

				if (helpRectangle.contains(relativePosition)) {
					return index;
				} else {
					return index + 1;
				}
			}
		}

		return childDocks.size();
	}

	/**
	 * Determines if the given dockable can be added to this dock with priority. When there are no dockables already,
	 * it can be added with priority if the mouse is above the middle of the rectangle.
	 * If there are already dockables, the dockable can be added with priority if the mouse is near
	 * the left or right border of  the child docks.
	 *
	 * @param dockable         The dockable that may be added to this dock.
	 * @param relativeLocation The location of the mouse relative to this dock.
	 * @return True if the given dockable can be added to this dock with priority,
	 * false otherwise.
	 */
	protected boolean canAddDockableWithPriority(Dockable dockable, Point relativeLocation) {
		// Are there no dockables already?
		if (childDocks.size() == 0) {
			// There is priority if we are not near the border of the dock.
			Dimension size = getSize();
			priorityRectangle.setBounds((int) (size.width * centerPriorityRectangleRelativeOffset),
					(int) (size.height * centerPriorityRectangleRelativeOffset),
					(int) (size.width * (1 - 2 * centerPriorityRectangleRelativeOffset)),
					(int) (size.height * (1 - 2 * centerPriorityRectangleRelativeOffset)));
			// Inside the priority rectangle we can dock with priority.
			return priorityRectangle.contains(relativeLocation);

			// Outside the priority rectangle.
		}

		// When we are here, there are already dockables in this dock.

		// Iterate over the dockables.
		for (Object childDock1 : childDocks) {

			// Get the child.
			Component childDock = (Component) childDock1;
			helpRectangle.setBounds(childDock.getLocation().x,
					childDock.getLocation().y,
					childDock.getSize().width,
					childDock.getSize().height);

			// Is the mouse above this recangle?
			if (helpRectangle.contains(relativeLocation)) {

				// Set the rectangle on the left side of the child dock.
				priorityRectangle.setBounds(childDock.getLocation().x,
						childDock.getLocation().y,
						(int) (childDock.getSize().width * leftPriorityRectangleRelativeOffset),
						childDock.getSize().height);

				if (priorityRectangle.contains(relativeLocation)) {
					return true;
				}

				// Set the rectangle on the right side of the child dock.
				priorityRectangle.setBounds(childDock.getLocation().x + (int) (childDock.getSize().width * (1 - rightPriorityRectangleRelativeOffset)),
						childDock.getLocation().y,
						(int) (childDock.getSize().width * rightPriorityRectangleRelativeOffset),
						childDock.getSize().height);

				return priorityRectangle.contains(relativeLocation);

			}
		}

		// We can't dock with priority.
		return false;
	}

	/**
	 * Calculates the number of columns there will be in the grid. The calculation depends on the <code>fillMode</code>.
	 * Valid fill modes are FILL_SQUARE_HORIZONTAL, FILL_SQUARE_VERTICAL, FILL_FLOW_HORIZONTAL or FILL_FLOW_VERTICAL.
	 *
	 * @return The number of columns there will be in the grid.
	 * @throws IllegalStateException when the <code>fillMode</code> is not FILL_SQUARE_HORIZONTAL,
	 * FILL_SQUARE_VERTICAL, FILL_FLOW_HORIZONTAL or FILL_FLOW_VERTICAL.
	 */
	protected int calculateColumnCount() {

		// The number of dockables that should be docked.
		int dockCount = childDocks.size();

		// Return 1 when there are no dockables.
		if (dockCount == 0) {
			return 1;
		}

		if ((getSize().width == 0) ||
				(getSize().height == 0) ||
				(fillMode == FILL_SQUARE_HORIZONTAL) ||
				(fillMode == FILL_SQUARE_VERTICAL)) {
			// Calculate the number of columns.
			int newColumnCount = (int) Math.ceil(Math.sqrt((double) (dockCount)));
			if (fillMode == FILL_SQUARE_VERTICAL) {
				if (((newColumnCount - 1) * newColumnCount) >= dockCount) {
					newColumnCount--;
				}
			}
			if (newColumnCount <= 0) {
				newColumnCount = 1;
			}

			return newColumnCount;
		} else {

			// There are 2 equations that define the rowCount and columnCount:
			//
			// width / (columnCount * preferredWidth) 			= height / (rowCount * preferredHeight)
			// childDockablesCount 			= columnCount * rowCount
			//					||
			//					\/
			// columnCount 					= (width * preferredHeight) * rowCount / (height * preferredWidth)
			// rowCount						= childDockablesCount / columnCount
			//					||
			//					\/
			// columnCount *  columnCount	= (width * preferredHeight) * childDockablesCount / (height * preferredWidth)
			// rowCount						= childDockablesCount / columnCount
			//					||
			//					\/
			// columnCount              	= sqrt((width * preferredHeight) * childDockablesCount / (height * preferredWidth))
			// rowCount						= childDockablesCount / columnCount

			// Calculate the maximum component size of the children.
			Dimension maxPreferredSize = new Dimension(0, 0);
			for (Object childDock : childDocks) {
				// Get the preferred size of the child.
				Dimension childSize = ((Component) childDock).getPreferredSize();

				// Adjust the union size.
				maxPreferredSize.setSize(Math.max(maxPreferredSize.width, childSize.width), Math.max(maxPreferredSize.height, childSize.height));
			}
			if (maxPreferredSize.width <= 0) {
				maxPreferredSize.width = 1;
			}
			if (maxPreferredSize.height <= 0) {
				maxPreferredSize.height = 1;
			}

			if (fillMode == FILL_FLOW_HORIZONTAL) {
				// Calculate the number of columns.
				int newColumnCount = (int) Math.ceil(Math.sqrt(((double) ((getSize().width * maxPreferredSize.height) * dockCount)) / (getSize().height * maxPreferredSize.width)));

				if (newColumnCount <= 0) {
					newColumnCount = 1;
				}

				// Get the number of rows for this column count.
				int newRowCount = (int) Math.ceil(dockCount / (double) newColumnCount);

				// Try to make the column count smaller.
				while (dockCount <= newRowCount * (newColumnCount - 1)) {
					newColumnCount--;
				}

				if (newColumnCount <= 0) {
					newColumnCount = 1;
				}

				return newColumnCount;
			} else if (fillMode == FILL_FLOW_VERTICAL) {
				// Calculate the number of rows.
				int newRowCount = (int) Math.ceil(Math.sqrt(((double) ((getSize().height * maxPreferredSize.width) * dockCount)) / (getSize().width * maxPreferredSize.height)));

				if (newRowCount <= 0) {
					newRowCount = 1;
				}

				// Get the number of columns for this row count.

				return (int) Math.ceil(dockCount / (double) newRowCount);

			}

		}

		throw new IllegalStateException("The fill mode [" + fillMode + "] is unknown.");
	}

	/**
	 * Checks the docking modes of the dockable. True is returned if the dockable has {@link DockingMode#GRID} as possible docking mode.
	 *
	 * @return True is returned if the dockable has DockingMode.GRID
	 * as possible docking mode.
	 * @param    dockable                The dockable to add.
	 */
	protected boolean checkDockingModes(Dockable dockable) {
		int dockPositions = dockable.getDockingModes();
		return (dockPositions & DockingMode.GRID) != 0;

	}

	/**
	 * Gets the docking mode for a dockable that is docked in this dock. This is always {@link DockingMode#GRID}.
	 *
	 * @return The docking mode for a dockable that is docked in this dock. This is always DockingMode.GRID.
	 */
	protected int getDockingMode() {
		return DockingMode.GRID;
	}

	// Private metods.

	/**
	 * Creates the panels for the child docks and adds them to this dock.
	 */
	private void initializeUi(int newColumnCount) {

		this.columnCount = newColumnCount;

		// Set the layout.
		this.setLayout(new BorderLayout());

		// Create the panel that will contain the docks.
		dockPanel = new JPanel();
		dockPanel.setLayout(new GridLayout(0, columnCount));

		// Add it.
		this.add(dockPanel, BorderLayout.CENTER);

	}

	/**
	 * Rebuilds the whole dock again with the existing child docks.
	 */
	private void rebuildUI(int newColumnCount) {

		// Remove everything, except the ghostpanel.
		for (int index = 0; index < getComponentCount(); index++) {
			Component component = getComponent(index);
			if (!component.equals(ghostDockPanel)) {
				remove(component);
			}
		}

		// Create and add the panel for the docks.
		initializeUi(newColumnCount);

		// Add all the docks.
		for (Object childDock1 : childDocks) {
			// Add the child to the panel.
			Dock childDock = (Dock) childDock1;
			dockPanel.add((Component) childDock);
		}

	}


}
