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
 * The dockables are organized in a line.
 * </p>
 * <p>
 * Information on using line docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html#LineDock" target="_blank">How to Use Laef Docks</a> in
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
 * <li>it has <code>horizontalDockingMode</code> or <code>verticalDockingMode</code> as possible docking mode.</li>
 * <li>its content component is not null.</li>
 * </ul>
 * A composite dockable can also be docked in this dock if:
 * <ul>
 * <li>all of its child dockables have <code>horizontalDockingMode</code> or <code>verticalDockingMode</code> as possible docking mode.</li>
 * <li>all of its child dockables have a content component that is not null.</li>
 * </ul>
 *
 * <p>
 * The size of all the child dockables is the same when the <code>grid</code> property
 * is set to true, otherwise the size of the different child dockables will be according to the
 * preferred size of their content component.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Position} for dockables docked in this dock are one-dimensional.
 * The first position value of a child dockable is between 0 and the number of child dockables minus 1,
 * it is the position in the line.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class LineDock extends JPanel implements LeafDock {

	// Static fields.

	/**
	 * The value for the orientation when the dock is a horizontal line.
	 */
	public static final int ORIENTATION_HORIZONTAL = 0;
	/**
	 * The value for the orientation when the dock is a vertical line.
	 */
	public static final int ORIENTATION_VERTICAL = 1;
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
	 * The dockables that are docked in this dock.
	 */
	private List childDockables = new ArrayList();
	/**
	 * With this handle all the dockables of this dock can be dragged.
	 */
	private DockHeader handle;

	/**
	 * The orientation of the line dock. This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 * The default is ORIENTATION_HORIZONTAL.
	 */
	private int orientation = ORIENTATION_HORIZONTAL;
	/**
	 * When true the components of the dockables have the same size in the dock, otherwise their size is
	 * computed with the preferred sizes of their components.
	 */
	private boolean grid;
	/**
	 * The docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 */
	private int horizontalDockingMode = DockingMode.HORIZONTAL_LINE;
	/**
	 * The docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 */
	private int verticalDockingMode = DockingMode.VERTICAL_LINE;
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
	 * When the <code>grid</code> property is true, this panel has a java.awt.GridLayout, otherwise
	 * it has a javax.swing.BoxLayout.
	 */
	private JPanel dockablePanel;
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();
	/**
	 * When true, {@link #retrieveDockingRectangle(Dockable, Point, Point, Rectangle)} sets a rectangle
	 * with the real size of the dockable component. This can be even a rectangle outside this dock.
	 * When false, the rectangle will be a rectangle of the grid.
	 */
	private boolean realSizeRectangle = true;


	// Constructors.

	/**
	 * Constructs a horizontal line dock.
	 */
	public LineDock() {
		this(ORIENTATION_HORIZONTAL, false);
	}

	/**
	 * Constructs a line dock with the given orientation.
	 *
	 * @param orientation The orientation for the line dock.
	 * @param grid        True when the dockables will have the same size, false otherwise.
	 */
	public LineDock(int orientation, boolean grid) {

		this(orientation, grid, DockingMode.HORIZONTAL_LINE, DockingMode.VERTICAL_LINE);

	}

	/**
	 * Constructs a line dock with the given orientation.
	 *
	 * @param orientation           The orientation for the line dock.
	 * @param grid                  True when the dockables will have the same size, false otherwise.
	 * @param horizontalDockingMode The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 * @param verticalDockingMode   The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_VERTICAL).
	 */
	public LineDock(int orientation, boolean grid, int horizontalDockingMode, int verticalDockingMode) {

		// Set the properties.
		this.orientation = orientation;
		this.grid = grid;
		this.horizontalDockingMode = horizontalDockingMode;
		this.verticalDockingMode = verticalDockingMode;

		// Create the dragger for the dockables of this dock.
		DragListener dragListener = DockingManager.getDockDragListenerFactory().createDragListener(this);

		// Create the handle and set the dragger on the handle.
		int handlePosition = Position.LEFT;
		if (orientation == ORIENTATION_VERTICAL) {
			handlePosition = Position.TOP;
		}
		handle = DockingManager.getComponentFactory().createDockHeader(this, handlePosition);
		handle.setDragListener(dragListener);

		// Create and add the panel with the docks.
		initializeUi();

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
	 * <li>the abstract method {@link #checkDockingModes(Dockable)} returns true for the composite dockable.</li>
	 * <li>all of its child dockables have a content component that is not null.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a line dock.
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
				if (realSizeRectangle) {
					if (orientation == ORIENTATION_HORIZONTAL) {
						int width = 0;
						if (dockable instanceof CompositeDockable) {
							width = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE).width;
						} else {
							width = dockable.getContent().getPreferredSize().width;
						}
						rectangle.setBounds(0, 0, width, getSize().height);
					} else {
						int height = 0;
						if (dockable instanceof CompositeDockable) {
							height = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE).height;
						} else {
							height = dockable.getContent().getPreferredSize().height;
						}
						rectangle.setBounds(0, 0, getSize().width, height);
					}
				} else {
					// The docking rectangle is the rectangle defined by this dock panel.
					rectangle.setBounds(0, 0, getSize().width, getSize().height);
				}
			} else {
				// Convert the mouse position to the dockablePanel.
				dockablePanelPosition.setLocation(relativeLocation);
				dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

				// Iterate over the dockables.
				for (int index = 0; index < childDockables.size(); index++) {
					// Get the component of the current dockable.
					Component dockableComponent = (Component) ((Dockable) childDockables.get(index)).getContent();
					dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);

					// Set the position of the rectangle on this dockable.
					rectangle.setLocation(dockablePosition);

					// Calculate the width of the rectangle.
					rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);


					// Is the mouse above this component?
					if (rectangle.contains(dockablePanelPosition)) {
						// Are we above the last dockable?
						if (index == childDockables.size() - 1) {
							if (orientation == ORIENTATION_HORIZONTAL) {
								// Set the rectangle on the last half of the dockable.
								dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y);
								rectangle.setLocation(dockablePosition);
								if (realSizeRectangle) {
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);
								} else {
									rectangle.setSize(dockableComponent.getSize().width / 2, dockableComponent.getSize().height);
								}
								if (rectangle.contains(dockablePanelPosition)) {
									if (realSizeRectangle) {
										dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width, dockableComponent.getLocation().y);
									}
								} else {
									// Set the rectangle again on the whole last dockable.
									dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);
									rectangle.setLocation(dockablePosition);
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);
								}
							} else {
								// Set the rectangle on the last half of the dockable.
								dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y + dockableComponent.getSize().height / 2);
								rectangle.setLocation(dockablePosition);
								if (realSizeRectangle) {
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);
								} else {
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height / 2);
								}
								if (rectangle.contains(dockablePanelPosition)) {
									if (realSizeRectangle) {
										dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y + dockableComponent.getSize().height);
									}
								} else {
									// Set the rectangle again on the whole last dockable.
									dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);
									rectangle.setLocation(dockablePosition);
									rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height);
								}
							}
						}

						// Calculate the width or height of the rectangle.
						if (dockable instanceof CompositeDockable) {
							CompositeDockable compositeDockable = (CompositeDockable) dockable;

							if (realSizeRectangle) {
								if (orientation == ORIENTATION_HORIZONTAL) {
									int width = DockingUtil.getCompositeDockablePreferredSize(compositeDockable, DockingMode.HORIZONTAL_LINE).width;
									rectangle.setSize(width, dockableComponent.getSize().height);
								} else {
									int height = DockingUtil.getCompositeDockablePreferredSize(compositeDockable, DockingMode.VERTICAL_LINE).height;
									rectangle.setSize(dockableComponent.getSize().width, height);
								}
							} else {
								Dimension rectangleSize = new Dimension(dockableComponent.getSize().width, dockableComponent.getSize().height);
								int childIndex = 1;
								while ((childIndex < compositeDockable.getDockableCount()) && (childIndex + index < childDockables.size())) {
									Component nextDockableComponent = (Component) ((Dockable) childDockables.get(index + childIndex)).getContent();
									if (orientation == ORIENTATION_HORIZONTAL) {
										rectangleSize.setSize(rectangleSize.getSize().width + nextDockableComponent.getSize().width,
												rectangleSize.getSize().height);
									} else {
										rectangleSize.setSize(rectangleSize.getSize().width,
												rectangleSize.getSize().height + nextDockableComponent.getSize().height);
									}
									childIndex++;
								}
								rectangle.setSize(rectangleSize);
							}
						} else {
							if (realSizeRectangle) {
								if (orientation == ORIENTATION_HORIZONTAL) {
									Dimension rectangleSize = new Dimension(dockable.getContent().getPreferredSize().width, dockableComponent.getSize().height);
									rectangle.setSize(rectangleSize);
								} else {
									Dimension rectangleSize = new Dimension(dockableComponent.getSize().width, dockable.getContent().getPreferredSize().height);
									rectangle.setSize(rectangleSize);
								}
							}
						}

						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);
						rectangle.setLocation(dockablePosition);


						return priority;
					}
				}

				// The mouse is not above a child dockable.
				if (realSizeRectangle) {
					if (orientation == ORIENTATION_HORIZONTAL) {
						// Get the component of the current dockable.
						Component dockableComponent = (Component) ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();
						dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width, dockableComponent.getLocation().y);
						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);

						// The width of the rectangle.
						int width = 0;
						if (dockable instanceof CompositeDockable) {
							width = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE).width;
						} else {
							width = dockable.getContent().getPreferredSize().width;
						}

						// Set the rectangle on this dockable.
						rectangle.setLocation(dockablePosition);
						rectangle.setSize(width, dockableComponent.getSize().height);

						return priority;
					} else {
						// Get the component of the current dockable.
						Component dockableComponent = (Component) ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();
						dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y + dockableComponent.getSize().height);
						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);

						// The height of the rectangle.
						int height = 0;
						if (dockable instanceof CompositeDockable) {
							height = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE).height;
						} else {
							height = dockable.getContent().getPreferredSize().height;
						}

						// Set the rectangle on this dockable.
						rectangle.setLocation(dockablePosition);
						rectangle.setSize(dockableComponent.getSize().width, height);

						return priority;
					}

				} else {
					if (orientation == ORIENTATION_HORIZONTAL) {
						// Get the component of the current dockable.
						Component dockableComponent = (Component) ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();
						dockablePosition.setLocation(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y);
						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);

						// Set the rectangle on this dockable.
						rectangle.setLocation(dockablePosition);
						rectangle.setSize(dockableComponent.getSize().width / 2, dockableComponent.getSize().height);

						return priority;
					} else {
						// Get the component of the current dockable.
						Component dockableComponent = (Component) ((Dockable) childDockables.get(childDockables.size() - 1)).getContent();
						dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y + dockableComponent.getSize().height / 2);
						dockablePosition = SwingUtilities.convertPoint(dockablePanel, dockablePosition, this);

						// Set the rectangle on this dockable.
						rectangle.setLocation(dockablePosition);
						rectangle.setSize(dockableComponent.getSize().width, dockableComponent.getSize().height / 2);

						return priority;
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
		if (childDockables.contains(dockable)) {
			// We could find the dockable.
			return true;
		}

		// The dockable is not docked in this dock.
		return false;

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

				dockablePanel.remove((Component) childDockable.getContent());
				childDockables.remove(childDockable);
				childDockable.setState(DockableState.CLOSED, null);

				// Inform the listeners about the removal.
				dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, childDockable));

			}
			compositeDockable.setState(DockableState.CLOSED, null);

			// Repaint.
			SwingUtil.repaintParent(this);

			// Removed.
			return true;

		}

		// If we are here, we don't have a composite dockable.

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, null, dockable));

		dockablePanel.remove((Component) dockable.getContent());
		childDockables.remove(dockable);
		dockable.setState(DockableState.CLOSED, null);

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, dockable));


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

		// Save the grid and orientation.
		PropertiesUtil.setInteger(properties, prefix + "orientation", orientation);
		PropertiesUtil.setBoolean(properties, prefix + "grid", grid);

		// Save the IDs of the dockables.
		String[] dockableIdArray = new String[childDockables.size()];
		for (int index = 0; index < dockableIdArray.length; index++) {
			dockableIdArray[index] = ((Dockable) childDockables.get(index)).getID();
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);


		// Save the docking modes.
		PropertiesUtil.setInteger(properties, prefix + "horizontalDockingMode", horizontalDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "verticalDockingMode", verticalDockingMode);

	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException {

		// Set the docking mode.
		int horizontalDockingMode = DockingMode.HORIZONTAL_LINE;
		int verticalDockingMode = DockingMode.VERTICAL_LINE;
		horizontalDockingMode = PropertiesUtil.getInteger(properties, prefix + "horizontalDockingMode", horizontalDockingMode);
		verticalDockingMode = PropertiesUtil.getInteger(properties, prefix + "verticalDockingMode", verticalDockingMode);
		setHorizontalDockingMode(horizontalDockingMode);
		setVerticalDockingMode(verticalDockingMode);

		// Set the alignment, grid and orientation.
		int orientation = ORIENTATION_HORIZONTAL;
		boolean grid = false;
		orientation = PropertiesUtil.getInteger(properties, prefix + "orientation", orientation);
		grid = PropertiesUtil.getBoolean(properties, prefix + "grid", grid);
		setOrientation(orientation);
		setGrid(grid);

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
		rebuildUI();

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

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockable));

		// Remove and add all the dockables from the dockable panel.
		dockablePanel.removeAll();
		for (Object childDockable : childDockables) {
			dockablePanel.add((Component) ((Dockable) childDockable).getContent());
		}

		// Repaint.
		SwingUtil.repaintParent(this);
	}

	// Getters / Setters.

	/**
	 * <p>
	 * Gets the orientation of the line dock.
	 * </p>
	 * <p>
	 * This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 * The default is ORIENTATION_HORIZONTAL.
	 * </p>
	 *
	 * @return The orientation of the line dock.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * <p>
	 * Sets the orientation of the line dock.
	 * </p>
	 * <p>
	 * This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 * </p>
	 *
	 * @param newOrientation The orientation of the line dock.
	 */
	public void setOrientation(int newOrientation) {

		// Do we have a new value?
		if (newOrientation != orientation) {
			// Set the new orientation.
			this.orientation = newOrientation;

			// Set the orientation of the handle.
			if (orientation == ORIENTATION_HORIZONTAL) {
				handle.setPosition(Position.LEFT);
			} else {
				handle.setPosition(Position.TOP);
			}

			// Rebuild the UI.
			rebuildUI();
		}

	}

	/**
	 * Returns whether the content components of the dockables have the same size in this dock.
	 *
	 * @return True when the content components of the dockables have the same size in the dock,
	 * false when their size is computed with the preferred sizes of their components.
	 */
	public boolean getGrid() {
		return grid;
	}

	/**
	 * Sets whether the content components of the dockables should have the same size in this dock.
	 *
	 * @param newGrid True when the content components of the dockables have the same size in the dock,
	 *                false when their size is computed with the preferred sizes of their components.
	 */
	public void setGrid(boolean newGrid) {

		// Do we have a new value?
		if (newGrid != grid) {
			// Set the new grid.
			this.grid = newGrid;

			// Rebuild the UI.
			rebuildUI();

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

	/**
	 * <p>
	 * Checks the docking modes of the dockable. True is returned if we have a horizontal dock orientation
	 * (ORIENTATION_HORIZONTAL) and if the dockable has <code>horizontalDockingMode</code> as possible docking mode.
	 * </p>
	 * <p>
	 * True is also returned if we have a vertical dock orientation
	 * (ORIENTATION_VERTICAL) and if the dockable has <code>verticalDockingMode</code> as possible docking mode.
	 * </p>
	 *
	 * @return True is returned if we have a horizontal dock orientation
	 * (ORIENTATION_HORIZONTAL) and if the dockable has <code>horizontalDockingMode</code>
	 * as possible docking mode.<br>
	 * True is also returned if we have a vertical dock orientation
	 * (ORIENTATION_VERTICAL) and if the dockable has <code>verticalDockingMode</code>
	 * as possible docking mode.
	 * Otherwise false is returned.
	 * @param    dockable                The dockable to add.
	 */
	protected boolean checkDockingModes(Dockable dockable) {
		int dockPositions = dockable.getDockingModes();
		if ((getOrientation() == ORIENTATION_HORIZONTAL) &&
				((dockPositions & horizontalDockingMode) == 0)) {
			return false;
		}
		if ((getOrientation() == ORIENTATION_VERTICAL) &&
				((dockPositions & verticalDockingMode) == 0)) {
			return false;
		}

		return true;
	}

	/**
	 * Sets the last docking mode of the dockable to <code>horizontalDockingMode</code>
	 * if this is a horizontal line dock. Sets the last docking mode of the dockable to
	 * <code>verticalDockingMode</code>, if this is a vertical line dock.
	 *
	 * @param dockable The dockable whose property <code>lastDockingMode</code> is set to
	 *                 <code>horizontalDockingMode</code> or <code>verticalDockingMode</code>.
	 */
	private void setLastDockingMode(Dockable dockable) {

		if (getOrientation() == ORIENTATION_HORIZONTAL) {
			dockable.setLastDockingMode(horizontalDockingMode);
		}
		if (getOrientation() == ORIENTATION_VERTICAL) {
			dockable.setLastDockingMode(verticalDockingMode);
		}

	}

	/**
	 * <p>
	 * Gets the integer position in the line, where the dockable should be docked in the dock,
	 * if the mouse was released at the given the position.
	 * </p>
	 * <p>
	 * The position is the future index of the dockable in the line, if it would be docked at the given mouse position.
	 * The first position is 0.
	 * </p>
	 *
	 * @param newDockable      The dockable to add.
	 * @param relativePosition The relative mouse location in this dock.
	 * @return The position where the dockable should be docked in the dock.
	 * The position is the future index of the dockable in the line.
	 */
	private int getDockPosition(Dockable newDockable, Point relativePosition) {

		// Are there no child dockables already?
		if (childDockables.size() == 0) {
			// The first position.
			return 0;
		}

		// When we are here, there are already dockables in this dock.

		// Convert the mouse position to the dockablePanel.
		dockablePanelPosition.setLocation(relativePosition);
		dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

		// Iterate over the dockables.
		for (int index = 0; index < childDockables.size(); index++) {
			// Get the component of the current dockable.
			Component dockableComponent = (Component) ((Dockable) childDockables.get(index)).getContent();
			dockablePosition.setLocation(dockableComponent.getLocation().x, dockableComponent.getLocation().y);

			// Set the rectangle on the dockable.
			helpRectangle.setBounds(dockableComponent.getLocation().x, dockableComponent.getLocation().y,
					dockableComponent.getSize().width, dockableComponent.getSize().height);

			// Is the mouse above this recangle?
			if (helpRectangle.contains(dockablePanelPosition)) {
				// Are we above the last dockable?
				if (index == childDockables.size() - 1) {
					// Set the rectangle on the last half of the dockable.
					if (orientation == ORIENTATION_HORIZONTAL) {
						helpRectangle.setBounds(dockableComponent.getLocation().x + dockableComponent.getSize().width / 2, dockableComponent.getLocation().y,
								dockableComponent.getSize().width / 2, dockableComponent.getSize().height);
						if (helpRectangle.contains(dockablePanelPosition)) {
							return childDockables.size();
						}
					} else {
						helpRectangle.setBounds(dockableComponent.getLocation().x, dockableComponent.getLocation().y + dockableComponent.getSize().height / 2,
								dockableComponent.getSize().width, dockableComponent.getSize().height / 2);
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
			if (priorityRectangle.contains(relativeLocation)) {
				// Inside the priority rectangle we can dock with priority.
				return true;
			}

			// Outside the priority rectangle.
			return false;
		}

		// When we are here, there are already dockables in this dock.

		// Convert the mouse position to the dockablePanel.
		dockablePanelPosition.setLocation(relativeLocation);
		dockablePanelPosition = SwingUtilities.convertPoint(this, dockablePanelPosition, dockablePanel);

		// Iterate over the dockables.
		for (Object childDockable : childDockables) {
			// Get the component of the current dockable.
			Component dockableComponent = (Component) ((Dockable) childDockable).getContent();
			dockablePosition.setLocation(dockableComponent.getLocation().x + priorityBorder, dockableComponent.getLocation().y + priorityBorder);

			// Set the rectangle on the dockable.
			helpRectangle.setBounds(dockableComponent.getLocation().x, dockableComponent.getLocation().y,
					dockableComponent.getSize().width - priorityBorder * 2, dockableComponent.getSize().height - priorityBorder * 2);

			// Is the mouse above this recangle?
			if (helpRectangle.contains(dockablePanelPosition)) {
				// Return the index of the dockable we are above.
				return true;
			}
		}


		// We can't dock with priority.
		return false;

	}

	// Private methods.

	/**
	 * Creates the panels for the dockables and adds them to this dock.
	 */
	private void initializeUi() {

		// Create the deepest panel that will contain the dockables.
		dockablePanel = new JPanel();
		if (grid) {
			if (orientation == ORIENTATION_VERTICAL) {
				dockablePanel.setLayout(new GridLayout(0, 1));
			} else {
				dockablePanel.setLayout(new GridLayout(1, 0));
			}
		} else {
			if (orientation == ORIENTATION_VERTICAL) {
				dockablePanel.setLayout(new BoxLayout(dockablePanel, BoxLayout.Y_AXIS));
			} else {
				dockablePanel.setLayout(new BoxLayout(dockablePanel, BoxLayout.X_AXIS));
			}
		}

		// Set the layout.
		if (orientation == ORIENTATION_VERTICAL) {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		} else {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		}

		// Add the panel and handle.
		add((JComponent) handle);
		add(dockablePanel);

	}

	/**
	 * Rebuilds the whole dock again with the existing dockables.
	 */
	private void rebuildUI() {

		// Remove all the existing things.
		this.removeAll();

		// Create and add the panel for the docks.
		initializeUi();

		// Add all the dockables.
		for (Object childDockable1 : childDockables) {
			Dockable childDockable = (Dockable) childDockable1;
			dockablePanel.add((Component) childDockable.getContent());
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

	/**
	 * Gets the docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 *
	 * @return The docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 */
	public int getHorizontalDockingMode() {
		return horizontalDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 *
	 * @param horizontalDockingMode The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 */
	public void setHorizontalDockingMode(int horizontalDockingMode) {
		this.horizontalDockingMode = horizontalDockingMode;
	}

	/**
	 * Gets the docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 *
	 * @return The docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 */
	public int getVerticalDockingMode() {
		return verticalDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked in this line dock,
	 * if the line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 *
	 * @param verticalDockingMode The docking mode for a dockable that is docked in this line dock,
	 *                            if the line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 */
	public void setVerticalDockingMode(int verticalDockingMode) {
		this.verticalDockingMode = verticalDockingMode;
	}

	/**
	 * <p>
	 * Gets how docking rectangles have to be painted.
	 * </p>
	 * <p>
	 * The default value is <code>true</code>.
	 * </p>
	 *
	 * @return When true, {@link #retrieveDockingRectangle(Dockable, Point, Point, Rectangle)} sets a rectangle
	 * with the real size of the dockable component. This can be even a rectangle outside this dock.
	 * When false, the rectangle will be a rectangle of the grid.
	 * @see #setRealSizeRectangle(boolean)
	 */
	public boolean getRealSizeRectangle() {
		return realSizeRectangle;
	}

	/**
	 * <p>
	 * Sets how docking rectangles have to be painted.
	 * </p>
	 * <p>
	 * For tool bars and minimize bars it should be <code>true</code>,
	 * for regular line docks it should be <code>false</code>.
	 * </p>
	 * <p>
	 * The default value is <code>true</code>.
	 * </p>
	 *
	 * @param realSizeRectangle When true, {@link #retrieveDockingRectangle(Dockable, Point, Point, Rectangle)} sets a rectangle
	 *                          with the real size of the dockable component. This can be even a rectangle outside this dock.
	 *                          When false, the rectangle will be a rectangle of the grid.
	 */
	public void setRealSizeRectangle(boolean realSizeRectangle) {
		this.realSizeRectangle = realSizeRectangle;
	}


}
