package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.component.DockHeader;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DragListener;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This is a dock that can contain zero, one or multiple dockables.
 * The dockables are organized in a grid. The size of all the child dockables is the same.
 * </p>
 * <p>
 * Information on using grid docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html#GridDock" target="_blank">How to Use Laef Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * It is a leaf dock. It cannot contain other docks.
 * </p>
 * <p>
 * When it contains no dockable it is empty. It is never full.
 * </p>
 * <p>
 * A dockable can be docked in this dock if:
 * <ul>
 * <li>the dockable has <code>dockingMode</code> as one of its possible docking modes.</li>
 * <li>its content component is not null.</li>
 * </ul>
 * A composite dockable can also be docked in this dock if:
 * <ul>
 * <li>every dockable of the the composite dockable has <code>dockingMode</code> as one of its possible docking modes..</li>
 * <li>every dockable of the the composite dockable a content component that is not null.</li>
 * </ul>
 *
 * <p>
 * The {@link com.javadocking.dock.Position} for dockables of this dock are one-dimensional.
 * The first position value of a child dockable is between 0 and the number of child dockables minus 1,
 * it is the position in the grid.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class GridDock extends JPanel implements LeafDock {

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
	 * It takes the size of the panel and the preferred sizes of the child dockables into account. There is a preference
	 * for first adding columns.
	 */
	public static final int FILL_FLOW_HORIZONTAL = 2;
	/**
	 * With this fill mode the number of columns and rows is calculated to fill the panel of the grid optimally.
	 * It takes the size of the panel and the preferred sizes of the child dockables into account. There is a preference
	 * for first adding rows.
	 */
	public static final int FILL_FLOW_VERTICAL = 3;
	/**
	 * The name of the <code>dockableIds</code> property.
	 */
	private static final String PROPERTY_DOCKABLE_IDS = "dockableIds";
	/**
	 * The border that is used to compute the rectangles that have priority for docking.
	 *
	 * @see #canAddDockableWithPriority(Dockable, Point).
	 */
	private static final int priorityBorder = 10;
	/**
	 * The relative offset of the priority rectangle.
	 */
	private static final double priorityRectangleRelativeOffset = 2.0 / 8.0;


	// Fields.
	/**
	 * The parent of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The docking mode for a dockable that is docked in this grid dock.
	 */
	private int dockingMode = DockingMode.GRID;
	/**
	 * The dockables that are docked in this dock.
	 */
	private List childDockables = new ArrayList();
	/**
	 * With this handle all the dockables of this dock can be dragged.
	 */
	private DockHeader handle;
	/**
	 * The number of columns in the grid.
	 */
	private int columnCount = 1;
	/**
	 * The fill mode determines the strategy for organizing the dockables in rows and columns.
	 * The default fill mode is FILL_FLOW_HORIZONTAL.
	 */
	private int fillMode = FILL_FLOW_HORIZONTAL;
	/**
	 * True if composite dockables may be added to this dock, when it is not empty. False otherwise.
	 */
	private boolean addCompositeDockables = true;

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
	 * This is the position of the mouse in the dockablePanel.
	 * We keep it as field because we don't want to create every time a new point.
	 */
	private Point dockablePanelPosition = new Point();
	/**
	 * This is the position of the dockable.
	 * We keep it as field because we don't want to create every time a new point.
	 */
	private Point dockablePosition = new Point();

	/**
	 * This is the deepest panel that contains the components of the dockables.
	 */
	private JPanel dockablePanel;
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();


	// Constructors.

	/**
	 * Constructs a grid dock with docking mode for the dockables {@link DockingMode#GRID}.
	 */
	public GridDock() {
		this(DockingMode.GRID);
	}

	/**
	 * Constructs a grid dock.
	 *
	 * @param    dockingMode        The docking mode for a dockable that is docked in this grid dock.
	 */
	public GridDock(int dockingMode) {

		// Set the layout.
		super(new BorderLayout());

		this.dockingMode = dockingMode;

		// Create the dragger for the dockables of this dock.
		DragListener dragListener = DockingManager.getDockDragListenerFactory().createDragListener(this);

		// Create the handle and set the dragger on the handle.
		handle = DockingManager.getComponentFactory().createDockHeader(this, Position.LEFT);
		handle.setDragListener(dragListener);

		// Create and add the panel with the docks.
		initializeUi(1);

	}

	// Implementations of Dock.

	/**
	 * <p>
	 * Determines if the dockable can be added.
	 * </p>
	 * <p>
	 * A dockable can be docked in this dock if:
	 * <ul>
	 * <li>the abstract method {@link #checkDockingModes(Dockable)} returns true for the dockable.</li>
	 * <li>its content component is not null.</li>
	 * </ul>
	 * A composite dockable can also be docked in this dock if:
	 * <ul>
	 * <li>the abstract method {@link #checkDockingModes(Dockable)} returns true for the dockable.</li>
	 * <li>all of its child dockables have a content component that is not null.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a grid dock.
		if (!checkDockingModes(dockable)) {
			return Priority.CANNOT_DOCK;
		}

		// Is the component of the dockable not null?
		if (dockable.getContent() != null) {

			// Can we dock with priority?
			if (canAddDockableWithPriority(dockable, relativeLocation)) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

			// We can dock, but not with priority.
			return Priority.CAN_DOCK;
		}

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			// Check if we can add composite dockables.
			if ((!addCompositeDockables) && (!isEmpty())) {
				return Priority.CANNOT_DOCK;
			}

			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// Is the dockable already in this dock and are there no others?
			List childrenOfDockable = new ArrayList();
			List childrenOfDock = new ArrayList();
			DockingUtil.retrieveDockables(dockable, childrenOfDockable);
			DockingUtil.retrieveDockables(this, childrenOfDock);
			if (sameElements(childrenOfDockable, childrenOfDock)) {
				return Priority.CANNOT_DOCK;
			}

			// The components of all its child dockables should not be null.
			// Check if the child dockables may be docked in a line dock.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				Dockable childDockable = compositeDockable.getDockable(index);

				// Is the component of the dockable not null?
				if (childDockable.getContent() == null) {
					// We don't want deeper nested dockables.
					return Priority.CANNOT_DOCK;
				}
			}

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

			// Are there no child dockables already?
			if (childDockables.size() == 0) {
				// The docking rectangle is the rectangle defined by this dock panel.
				rectangle.setBounds(0, 0, getSize().width, getSize().height);
			} else {

				// Convert the mouse position to the dockablePanel.
				dockablePanelPosition.setLocation(relativeLocation);
				dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

				// Iterate over the dockables.
				for (int index = 0; index < childDockables.size(); index++) {
					// Get the component of the current dockable.
					Component dockableComponent = ((Dockable) childDockables.get(index)).getContent();
					dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);

					// Set the rectangle on this dockable.
					rectangle.setLocation(dockablePosition);
					rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);

					// Is the mouse above this component?
					if (rectangle.contains(dockablePanelPosition)) {
						// Are we above the last dockable?
						if (index == childDockables.size() - 1) {
							// Is the grid full?
							if ((childDockables.size() % columnCount) == 0) {
								// Set the rectangle on the last half of the dockable.
								dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y);
								rectangle.setLocation(dockablePosition);
								rectangle.setSize(dockableComponent.getSize().width / 2, dockableComponent.getSize().height);
								if (!rectangle.contains(dockablePanelPosition)) {
									// Set the rectangle again on the whole last dockable.
									dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);
									rectangle.setLocation(dockablePosition);
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);
								}
							}
						}

						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);
						rectangle.setLocation(dockablePosition);


						return priority;
					}
				}

				// The last half dock if it is full, otherwise the empty space.
				// Get the last dockable.
				Component dockableComponent = ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();

				// Is the grid full?
				if ((childDockables.size() % columnCount) == 0) {

					dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y);
					dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);
					rectangle.setLocation(dockablePosition);
					rectangle.setSize(dockableComponent.getSize().width / 2, dockableComponent.getSize().height);
					return priority;
				} else {
					dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width, dockableComponent.getLocation().y);
					dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);
					rectangle.setLocation(dockablePosition);
					int width = dockableComponent.getSize().width;
					if (childDockables.size() - columnCount >= 0) {
						Component previousRowComponent = ((Dockable) childDockables.get(childDockables.size() - columnCount)).getContent();
						width = previousRowComponent.getSize().width;
					}
					rectangle.setSize(width, dockableComponent.getSize().height);

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

		// Get the position in the line for the new dockable.
		int position = getDockPosition(dockableToAdd, relativeLocation);

		// Do we have a composite dockable?
		if (dockableToAdd instanceof CompositeDockable) {
			// Add every child dockable.
			CompositeDockable compositeDockable = (CompositeDockable) dockableToAdd;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Add the dockable.
				addDockable(compositeDockable.getDockable(index), new Position(position));
				position++;
			}
			dockableToAdd.setState(DockableState.NORMAL, this);

			// Repaint.
			SwingUtil.repaintParent(this);

			// Succes.
			return true;
		}

		// When we are here we have a dockable with a component that is not null.

		// Add the dockable.
		addDockable(dockableToAdd, new Position(position));

		// Repaint.
		SwingUtil.repaintParent(this);

		// Succes.
		return true;

	}

	public boolean canRemoveDockable(Dockable dockable) {
		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// Are all the child dockables docked in this dock?
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				if (!childDockables.contains(compositeDockable.getDockable(index))) {
					// We could not find the child dockable.
					return false;
				}
			}

			// We could find all the child dockables.
			return true;

		}

		// If we are here, we don't have a composite dockable.

		// Is the dockable docked in this dock?
		// We could find the dockable.
		return childDockables.contains(dockable);

		// The dockable is not docked in this dock.

	}

	public boolean removeDockable(Dockable dockable) {

		// Verify the conditions for removing the dockable.
		if (!canRemoveDockable(dockable)) {
			// We can not remove the dockable from this dock.
			return false;
		}

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// Remove all the child dockables from this dock.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				Dockable childDockable = compositeDockable.getDockable(index);

				// Inform the listeners about the removal.
				dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, null, childDockable));

				dockablePanel.remove(childDockable.getContent());
				childDockables.remove(childDockable);
				childDockable.setState(DockableState.CLOSED, null);

				// Inform the listeners about the removal.
				dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, childDockable));

			}
			compositeDockable.setState(DockableState.CLOSED, null);

			rebuildUI(calculateColumnCount());

			// Repaint.
			SwingUtil.repaintParent(this);

			// Removed.
			return true;

		}

		// If we are here we don't have a composite dockable.

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, null, dockable));

		dockablePanel.remove(dockable.getContent());
		childDockables.remove(dockable);
		dockable.setState(DockableState.CLOSED, null);

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, dockable));


		rebuildUI(calculateColumnCount());

		// Repaint.
		SwingUtil.repaintParent(this);

		// Removed.
		return true;

	}

	public boolean isEmpty() {
		return childDockables.size() == 0;
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

		// Save the docking mode, the fill mode, and the column count.
		PropertiesUtil.setInteger(properties, prefix + "dockingMode", dockingMode);
		PropertiesUtil.setInteger(properties, prefix + "fillMode", fillMode);
		PropertiesUtil.setInteger(properties, prefix + "columnCount", columnCount);

		// Save the IDs of the dockables.
		String[] dockableIdArray = new String[childDockables.size()];
		for (int index = 0; index < dockableIdArray.length; index++) {
			dockableIdArray[index] = ((Dockable) childDockables.get(index)).getID();
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);

	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException {

		// Set the docking mode.
		int dockingMode = DockingMode.GRID;
		dockingMode = PropertiesUtil.getInteger(properties, prefix + "dockingMode", dockingMode);
		setDockingMode(dockingMode);

		// Set the fill mode.
		int fillMode = FILL_FLOW_HORIZONTAL;
		fillMode = PropertiesUtil.getInteger(properties, prefix + "fillMode", fillMode);
		setFillMode(fillMode);

		// Load the IDs of the dockables.
		String[] dockableIdArray = new String[0];
		dockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);

		// Iterate over the IDs of the dockables.
		int position = 0;
		for (final String aDockableIdArray : dockableIdArray) {
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(aDockableIdArray);
			if (dockableObject != null) {
				if (dockableObject instanceof Dockable) {
					Dockable dockable = (Dockable) dockableObject;

					// Add the dockable.
					addDockable(dockable, new Position(position));
					position++;
				} else {
					throw new IOException("The values in the dockables mapping should be of type com.javadocking.Dockable.");
				}
			}
		}

		// Get the saved column count and make it as small as possible.
		int savedColumnCount = columnCount;
		savedColumnCount = PropertiesUtil.getInteger(properties, prefix + "columnCount", savedColumnCount);
		int savedRowCount = (int) Math.ceil(dockablesMap.size() / (double) savedColumnCount);
		while (dockablesMap.size() <= savedRowCount * (savedColumnCount - 1)) {
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

	// Implementations of LeafDock.

	public int getDockableCount() {
		return childDockables.size();
	}

	public Dockable getDockable(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dockable) childDockables.get(index);

	}

	public boolean containsDockable(Dockable dockable) {
		return childDockables.contains(dockable);
	}

	public boolean moveDockable(Dockable dockableToMove, Point relativeLocation) {

		// Don't move a composite dockable. 
		if (dockableToMove instanceof CompositeDockable) {
			return false;
		}

		// Check if the dockable is docked in this dock.
		if (!childDockables.contains(dockableToMove)) {
			throw new IllegalArgumentException("The dockable should be docked in this dock.");
		}

		// Check if we are above a dockable.
		int newDockableIndex = getDockPosition(dockableToMove, relativeLocation);
		if ((newDockableIndex < 0) || (newDockableIndex >= childDockables.size())) {
			newDockableIndex = childDockables.size() - 1;
		}

		// Get the current position number of this dockable.
		int previousDockableIndex = childDockables.indexOf(dockableToMove);

		// If the indices are the same, we don't have to move the dockable.
		if (previousDockableIndex == newDockableIndex) {
			return false;
		}

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, this, dockableToMove));

		// Set the new index.
		childDockables.remove(dockableToMove);
		childDockables.add(newDockableIndex, dockableToMove);
		rebuildUI(calculateColumnCount());

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, this, dockableToMove));

		// Repaint.
		SwingUtil.repaintParent(this);

		return true;

	}

	public Position getDockablePosition(Dockable dockable) throws IllegalArgumentException {

		int position = childDockables.indexOf(dockable);
		if (position >= 0) {
			return new Position(position);
		}

		throw new IllegalArgumentException("The dockable is not docked in this dock.");

	}

	public void addDockable(Dockable dockable, Position position) {

		// Is it a composite dockable?
		if (dockable instanceof CompositeDockable) {
			// Check if we can add composite dockables.
			if ((!addCompositeDockables) && (!isEmpty())) {
				return;
			}

			CompositeDockable compositeDockable = (CompositeDockable) dockable;
			for (int index = compositeDockable.getDockableCount() - 1; index > 0; index--) {
				addDockable(compositeDockable.getDockable(index), position);
			}
			return;
		}

		// Get the position in the line.
		int linePosition = getDockableCount();
		if (position.getDimensions() == 1) {
			if ((position.getPosition(0) >= 0) && (position.getPosition(0) <= getDockableCount())) {
				linePosition = position.getPosition(0);
			}
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, null, this, dockable));

		// Add the dockable to the list of dockables.
		childDockables.add(linePosition, dockable);
		dockable.setState(DockableState.NORMAL, this);
		setLastDockingMode(dockable);

		// Rebuild the UI.
		rebuildUI(calculateColumnCount());

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockable));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	// Getters / Setters.

	/**
	 * <p>
	 * Gets the strategy for organizing the dockables in rows and columns.
	 * </p>
	 * <p>
	 * This can be FILL_FLOW_HORIZONTAL, FILL_FLOW_VERTICAL, FILL_SQUARE_HORIZONTAL or FILL_SQUARE_VERTICAL.
	 * </p>
	 * <p>
	 * The default fill mode is FILL_FLOW_HORIZONTAL.
	 * </p>
	 *
	 * @return The strategy for organizing the dockables in rows and columns.
	 */
	public int getFillMode() {
		return fillMode;
	}

	/**
	 * <p>
	 * Sets the strategy for organizing the dockables in rows and columns.
	 * </p>
	 * <p>
	 * This can be FILL_FLOW_HORIZONTAL, FILL_FLOW_VERTICAL, FILL_SQUARE_HORIZONTAL or FILL_SQUARE_VERTICAL.
	 * </p>
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

	/**
	 * Determines if composite dockables may be added to this dock, when it is not empty.
	 *
	 * @return True if composite dockables may be added to this dock,
	 * when it is not empty. False otherwise.
	 */
	public boolean isAddCompositeDockables() {
		return addCompositeDockables;
	}

	/**
	 * Sets if composite dockables may be added to this dock, when it is not empty.
	 *
	 * @param addCompositeDockables True if composite dockables may be added to this dock,
	 *                              when it is not empty. False otherwise.
	 */
	public void setAddCompositeDockables(boolean addCompositeDockables) {
		this.addCompositeDockables = addCompositeDockables;
	}

	// Protected methods.

	/**
	 * <p>
	 * Gets the integer position in the grid, where the dockable should be docked in the dock,
	 * if the mouse was released at the given position.
	 * </p>
	 * <p>
	 * The position is the future index of the dockable in the grid, if it would be docked at the given mouse position.
	 * The first position is 0.
	 * </p>
	 *
	 * @param newDockable      The dockable to add.
	 * @param relativeLocation The relative mouse location in this dock.
	 * @return The position where the dockable should be docked in the dock.
	 * The position is the future index of the dockable in the line.
	 */
	protected int getDockPosition(Dockable newDockable, Point relativeLocation) {

		// Are there no child dockables already?
		if (childDockables.size() == 0) {
			// The first position.
			return 0;
		}

		// When we are here, there are already dockables in this dock.

		// Convert the mouse position to the dockablePanel.
		dockablePanelPosition.setLocation(relativeLocation);
		dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

		// Iterate over the dockables.
		for (int index = 0; index < childDockables.size(); index++) {
			// Get the component of the current dockable.
			Component dockableComponent = ((Dockable) childDockables.get(index)).getContent();
			dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);

			// Set the rectangle on the dockable.
			helpRectangle.setBounds(dockableComponent.getLocation().x, dockableComponent.getLocation().y,
					dockableComponent.getSize().width, dockableComponent.getSize().height);

			// Is the mouse above this recangle?
			if (helpRectangle.contains(dockablePanelPosition)) {
				// Are we above the last dockable?
				if (index == childDockables.size() - 1) {
					// Is the grid full?
					if ((childDockables.size() % columnCount) == 0) {
						// Set the rectangle on the last half of the dockable.
						helpRectangle.setBounds(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y,
								dockableComponent.getSize().width / 2, dockableComponent.getSize().height);
						if (helpRectangle.contains(dockablePanelPosition)) {
							return childDockables.size();
						}
					}
				}

				// Return the index of the dockable we are above.
				return index;
			}
		}

		return childDockables.size();

	}

	/**
	 * Determines if the given dockable can be added to this dock with priority,
	 * if the mouse is released at the given position.
	 *
	 * @param dockable         The dockable that may be added to this dock.
	 * @param relativeLocation The location of the mouse relative to this dock.
	 * @return True if the given dockable can be added to this dock with priority,
	 * false otherwise.
	 */
	protected boolean canAddDockableWithPriority(Dockable dockable, Point relativeLocation) {

		// Are there no dockables already?
		if (childDockables.size() == 0) {
			// There is priority if we are not near the border of the dock.
			Dimension size = getSize();
			priorityRectangle.setBounds((int) (size.width * priorityRectangleRelativeOffset),
					(int) (size.height * priorityRectangleRelativeOffset),
					(int) (size.width * (1 - 2 * priorityRectangleRelativeOffset)),
					(int) (size.height * (1 - 2 * priorityRectangleRelativeOffset)));
			// Inside the priority rectangle we can dock with priority.
			return priorityRectangle.contains(relativeLocation);

			// Outside the priority rectangle.
		}

		// When we are here, there are already dockables in this dock.

		// There is priority if we are above the center of a dockable.

		// Convert the mouse position to the dockablePanel.
		dockablePanelPosition.setLocation(relativeLocation);
		dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

		// Iterate over the dockables.
		for (Object childDockable : childDockables) {
			// Get the component of the current dockable.
			Component dockableComponent = ((Dockable) childDockable).getContent();
			dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);

			// Set the rectangle on the center of this dockable.
			priorityRectangle.setBounds(dockableComponent.getLocation().x + priorityBorder, dockableComponent.getLocation().y + priorityBorder,
					dockableComponent.getSize().width - 2 * priorityBorder, dockableComponent.getSize().height - 2 * priorityBorder);

			// Is the mouse above this recangle?
			if (priorityRectangle.contains(dockablePanelPosition)) {
				// Inside the priority rectangle we can dock with priority.
				return true;
			}
		}

		// Set the rectangle on the center of the last dockable.
		Component dockableComponent = ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();
		priorityRectangle.setBounds(dockableComponent.getLocation().x + priorityBorder, dockableComponent.getLocation().y + priorityBorder,
				dockableComponent.getSize().width - 2 * priorityBorder, dockableComponent.getSize().height - 2 * priorityBorder);

		if (childDockables.size() < columnCount * columnCount) {
			priorityRectangle.setLocation(dockableComponent.getLocation().x + priorityBorder + dockableComponent.getSize().width, dockableComponent.getLocation().y + priorityBorder);

			// Is the mouse above this recangle?
			// Inside the priority rectangle we can dock with priority.
			return priorityRectangle.contains(dockablePanelPosition);
		}


		// We can't dock with priority.
		return false;
	}

	/**
	 * <p>
	 * Calculates the number of columns there will be in the grid.
	 * </p>
	 * <p>
	 * The calculation depends on the <code>fillMode</code>.
	 * Valid fill modes are FILL_SQUARE_HORIZONTAL, FILL_SQUARE_VERTICAL, FILL_FLOW_HORIZONTAL or FILL_FLOW_VERTICAL.
	 * </p>
	 *
	 * @return The number of columns there will be in the grid.
	 * @throws IllegalStateException when the <code>fillMode</code> is not FILL_SQUARE_HORIZONTAL,
	 * FILL_SQUARE_VERTICAL, FILL_FLOW_HORIZONTAL or FILL_FLOW_VERTICAL.
	 */
	protected int calculateColumnCount() {

		// The number of dockables that should be docked.
		int dockableCount = childDockables.size();

		// Return 1 when there are no dockables.
		if (dockableCount == 0) {
			return 1;
		}

		if ((getSize().width == 0) ||
				(getSize().height == 0) ||
				(fillMode == FILL_SQUARE_HORIZONTAL) ||
				(fillMode == FILL_SQUARE_VERTICAL)) {

			// Calculate the number of columns.
			int newColumnCount = (int) Math.ceil(Math.sqrt((double) (childDockables.size())));
			if (fillMode == FILL_SQUARE_VERTICAL) {
				if (((newColumnCount - 1) * newColumnCount) >= childDockables.size()) {
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
			for (Object childDockable : childDockables) {
				// Get the preferred size of the child.
				Dimension childSize = ((Dockable) childDockable).getContent().getPreferredSize();

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
				int newColumnCount = (int) Math.ceil(Math.sqrt(((double) ((getSize().width * maxPreferredSize.height) * dockableCount)) / (getSize().height * maxPreferredSize.width)));

				if (newColumnCount <= 0) {
					newColumnCount = 1;
				}

				// Get the number of rows for this column count.
				int newRowCount = (int) Math.ceil(dockableCount / (double) newColumnCount);

				// Try to make the column count smaller.
				while (dockableCount <= newRowCount * (newColumnCount - 1)) {
					newColumnCount--;
				}

				if (newColumnCount <= 0) {
					newColumnCount = 1;
				}

				return newColumnCount;
			} else if (fillMode == FILL_FLOW_VERTICAL) {
				// Calculate the number of rows.
				int newRowCount = (int) Math.ceil(Math.sqrt(((double) ((getSize().height * maxPreferredSize.width) * dockableCount)) / (getSize().width * maxPreferredSize.height)));

				if (newRowCount <= 0) {
					newRowCount = 1;
				}

				// Get the number of columns for this row count.

				return (int) Math.ceil(dockableCount / (double) newRowCount);

			}

		}

		throw new IllegalStateException("The fill mode [" + fillMode + "] is unknown.");
	}

	// Private methods.

	/**
	 * Creates the panels for the dockables and adds them to this dock.
	 */
	private void initializeUi(int newColumnCount) {

		this.columnCount = newColumnCount;

		// Set the layout.
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

//		// Set the maximum size of the handle.
//		((JComponent)handle).setMaximumSize(new Dimension(handle.getPreferredWidth(), Integer.MAX_VALUE));

		// Create the deepest panel that will contain the dockables.
		dockablePanel = new JPanel();
		dockablePanel.setLayout(new GridLayout(0, newColumnCount));

		// The handle is at the left or top.
		add((JComponent) handle);

		// In the middle:
		add(dockablePanel);

	}

	/**
	 * Rebuilds the whole dock again with the existing dockables.
	 */
	private void rebuildUI(int columnCount) {

		// Remove all the existing things.
		this.removeAll();

		// Create and add the panel for the docks.
		initializeUi(columnCount);

		// Add all the dockables.
		for (Object childDockable1 : childDockables) {
			Dockable childDockable = (Dockable) childDockable1;
			dockablePanel.add(childDockable.getContent());
		}

	}

	/**
	 * Determines if the given lists contain the same elements. We suppose that all the elements of the given lists
	 * are different.
	 *
	 * @param firstList  The first list.
	 * @param secondList The second list.
	 * @return True if the given lists contain the same elements, false otherwise.
	 */
	private boolean sameElements(List firstList, List secondList) {

		// The size hould be the same, otherwise stop.
		if (firstList.size() != secondList.size()) {
			return false;
		}

		// Iterate over the elements of the first list.
		for (Object aFirstList : firstList) {
			// Check if the element is also in the second list.
			if (!secondList.contains(aFirstList)) {
				return false;
			}
		}

		// They heve the same elements.
		return true;
	}

	// Protected methods.

	/**
	 * Checks the docking modes of the dockable.
	 * True is returned if the dockable has <code>dockingMode</code> as possible docking mode.
	 *
	 * @return True is returned if the dockable has <code>dockingMode</code>
	 * as possible docking mode.
	 * @param    dockable                The dockable to add.
	 */
	private boolean checkDockingModes(Dockable dockable) {

		int dockPositions = dockable.getDockingModes();
		return (dockPositions & dockingMode) != 0;

	}

	/**
	 * Sets the last docking mode of the dockable to <code>dockingMode</code>.
	 *
	 * @param dockable The dockable whose property <code>lastDockingMode</code> is set to
	 *                 <code>dockingMode</code>.
	 */
	private void setLastDockingMode(Dockable dockable) {
		dockable.setLastDockingMode(dockingMode);
	}

	/**
	 * Gets the docking mode for a dockable that is docked in this grid dock.
	 * The default is {@link DockingMode#GRID}.
	 *
	 * @return The docking mode for a dockable that is docked in this grid dock.
	 */
	public int getDockingMode() {
		return dockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked in this grid dock.
	 *
	 * @param dockingMode The docking mode for a dockable that is docked in this grid dock.
	 */
	public void setDockingMode(int dockingMode) {
		this.dockingMode = dockingMode;
	}


}
