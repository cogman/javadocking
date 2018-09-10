package com.javadocking.dock;

import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.event.ChildDockEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a composite dock that has child docks that are organized in a line.
 * This dock can not contain dockables. When dockables are added, child docks are created and the
 * dockables are added to the child docks.
 * </p>
 * <p>
 * The size of all the child docks is the same when the <code>grid</code> property
 * is set to true, otherwise the size of the different child docks will be according to their
 * preferred size.
 * </p>
 * <p>
 * The positions for child docks of this dock are one-dimensional.
 * The first position value of a child dock is between 0 and the number of child docks minus 1,
 * it is the position in the line.
 * </p>
 * <p>
 * A dockable can be added to this dock if:<ul>
 * <li>the method {@link #checkDockingModes(Dockable)} returns <code>true</code>.</li>
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
 * This kind of dock is never full. It is empty when there are 0 child docks.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class CompositeLineDock extends JPanel implements CompositeDock {

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
	 * The border that is used to compute the rectangles that have priority for docking.
	 *
	 * @see #canAddDockableWithPriority(Dockable, Point).
	 */
	private static final int priorityBorder = 10;

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
	 * The orientation of the line dock. This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 * The default is ORIENTATION_HORIZONTAL.
	 */
	private int orientation = ORIENTATION_HORIZONTAL;
	/**
	 * When true the child docks have the same size, otherwise their size is
	 * computed with the preferred sizes of their components.
	 */
	private boolean grid;
	/**
	 * The docking mode for dockables that will be docked in this composite line dock,
	 * if the composite line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 */
	private int horizontalDockingMode = DockingMode.HORIZONTAL_LINE;
	/**
	 * The docking mode for dockables that will be docked in this composite line dock,
	 * if the composite line dock has a vertical orientation (ORIENTATION_VERTICAL).
	 */
	private int verticalDockingMode = DockingMode.VERTICAL_LINE;


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
	 * When the <code>grid</code> property is true, this panel has a java.awt.GridLayout, otherwise
	 * it has a javax.swing.BoxLayout.
	 */
	private JPanel dockPanel;
	/**
	 * This is the panel that contains the dockPanel. This panel has a javax.swing.BoxLayout.
	 * It is responsible for the alignment of the child docks in the dock.
	 */
	private JPanel alignmentPanel;
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
	/**
	 * This is an old <code>alignmentPanel</code> that has to be removed later. It is already made invisible.
	 * It cannot be removed now because it contains an old dock panel that still has listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private JPanel ghostAlignmentPanel;

	/**
	 * When true, {@link #retrieveDockingRectangle(Dockable, Point, Point, Rectangle)} sets a rectangle
	 * with the real size of the dockable component. This can be even a rectangle outside this dock.
	 * When false, the rectangle will be a rectangle of the grid.
	 */
	private boolean realSizeRectangle = true;


	// Constructors.

	/**
	 * Constructs a horizontal composite line dock with a {@link SingleDockFactory}
	 * as factory for creating the child docks.
	 */
	public CompositeLineDock() {
		this(ORIENTATION_HORIZONTAL, false, new SingleDockFactory());
	}

	/**
	 * Constructs a composite line dock with the given factory for the creating child docks.
	 *
	 * @param childDockFactory The factory for creating the child docks.
	 */
	public CompositeLineDock(DockFactory childDockFactory) {

		this(ORIENTATION_HORIZONTAL, false, childDockFactory);
	}

	/**
	 * Constructs a composite line dock with a {@link SingleDockFactory}
	 * as factory for creating the  child docks.
	 *
	 * @param orientation The orientation for the line dock.
	 * @param grid        True when the dockables will have the same size, false otherwise.
	 */
	public CompositeLineDock(int orientation, boolean grid) {
		this(orientation, grid, new SingleDockFactory());
	}

	/**
	 * Constructs a composite line dock with a {@link SingleDockFactory}
	 * as factory for creating the  child docks.
	 *
	 * @param orientation           The orientation for the line dock.
	 * @param grid                  True when the dockables will have the same size, false otherwise.
	 * @param horizontalDockingMode The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 * @param verticalDockingMode   The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_VERTICAL).
	 */
	public CompositeLineDock(int orientation, boolean grid, int horizontalDockingMode, int verticalDockingMode) {
		this(orientation, grid, new SingleDockFactory(), horizontalDockingMode, verticalDockingMode);
	}

	/**
	 * Constructs a composite line dock with the given factory for the creating child docks.
	 *
	 * @param orientation      The orientation for the line dock.
	 * @param grid             True when the dockables will have the same size, false otherwise.
	 * @param childDockFactory The factory for creating the child docks.
	 */
	public CompositeLineDock(int orientation, boolean grid, DockFactory childDockFactory) {

		this(orientation, grid, childDockFactory, DockingMode.HORIZONTAL_LINE, DockingMode.VERTICAL_LINE);
	}

	/**
	 * Constructs a composite line dock with the given factory for the creating child docks.
	 *
	 * @param orientation           The orientation for the line dock.
	 * @param grid                  True when the dockables will have the same size, false otherwise.
	 * @param childDockFactory      The factory for creating the child docks.
	 * @param horizontalDockingMode The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_HORIZONTAL).
	 * @param verticalDockingMode   The docking mode for a dockable that is docked in this line dock,
	 *                              if the line dock has a horizontal orientation (ORIENTATION_VERTICAL).
	 */
	public CompositeLineDock(int orientation, boolean grid, DockFactory childDockFactory, int horizontalDockingMode, int verticalDockingMode) {

		// Set the layout.
		super(new BorderLayout());

		// Set the properties.
		this.childDockFactory = childDockFactory;
		this.orientation = orientation;
		this.grid = grid;
		this.horizontalDockingMode = horizontalDockingMode;
		this.verticalDockingMode = verticalDockingMode;

		// Create and add the panel with the docks.
		initializeUi();

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

		// Check if the dockable may be docked in a line dock.
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

				if (realSizeRectangle) {
					// Convert the mouse position to the dockablePanel.
					Point dockablePanelPosition = new Point();
					dockablePanelPosition.setLocation(relativeLocation);

					// Iterate over the docks.
					for (int index = 0; index < childDocks.size(); index++) {
						// Get the component of the current dock.
						Component dockComponent = (Component) ((Dock) childDocks.get(index));
						Point dockPosition = new Point();
						dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y);

						// Set the position of the rectangle on this dock.
						rectangle.setLocation(dockPosition);

						// Calculate the width of the rectangle.
						rectangle.setSize(dockComponent.getSize().width, dockComponent.getSize().height);

						// Is the mouse above this component?
						if (rectangle.contains(dockablePanelPosition)) {
							// Are we above the last dock?
							if (index == childDocks.size() - 1) {
								if (orientation == ORIENTATION_HORIZONTAL) {
									// Set the rectangle on the last half of the dock.
									dockPosition.setLocation(dockComponent.getLocation().x + dockComponent.getSize().width / 2, dockComponent.getLocation().y);
									rectangle.setLocation(dockPosition);
									rectangle.setSize(dockComponent.getSize().width, dockComponent.getSize().height);

									if (rectangle.contains(dockablePanelPosition)) {
										dockPosition.setLocation(dockComponent.getLocation().x + dockComponent.getSize().width, dockComponent.getLocation().y);
									} else {
										// Set the rectangle again on the whole last dock.
										dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y);
										rectangle.setLocation(dockPosition);
										rectangle.setSize(dockComponent.getSize().width, dockComponent.getSize().height);
									}
								} else {
									// Set the rectangle on the last half of the dock.
									dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y + dockComponent.getSize().height / 2);
									rectangle.setLocation(dockPosition);
									rectangle.setSize(dockComponent.getSize().width, dockComponent.getSize().height);

									if (rectangle.contains(dockablePanelPosition)) {
										dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y + dockComponent.getSize().height);
									} else {
										// Set the rectangle again on the whole last dockable.
										dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y);
										rectangle.setLocation(dockPosition);
										rectangle.setSize(dockComponent.getSize().width, dockComponent.getSize().height);
									}
								}
							}

							// Calculate the width or height of the rectangle.
							if (dockable instanceof CompositeDockable) {
								CompositeDockable compositeDockable = (CompositeDockable) dockable;

								if (realSizeRectangle) {
									if (orientation == ORIENTATION_HORIZONTAL) {
										int width = DockingUtil.getCompositeDockablePreferredSize(compositeDockable, DockingMode.HORIZONTAL_LINE).width;
										rectangle.setSize(width, dockComponent.getSize().height);
									} else {
										int height = DockingUtil.getCompositeDockablePreferredSize(compositeDockable, DockingMode.VERTICAL_LINE).height;
										rectangle.setSize(dockComponent.getSize().width, height);
									}
								}
							} else {
								if (orientation == ORIENTATION_HORIZONTAL) {
									Dimension rectangleSize = new Dimension(dockable.getContent().getPreferredSize().width, dockComponent.getSize().height);
									rectangle.setSize(rectangleSize);
								} else {
									Dimension rectangleSize = new Dimension(dockComponent.getSize().width, dockable.getContent().getPreferredSize().height);
									rectangle.setSize(rectangleSize);
								}

							}

							rectangle.setLocation(dockPosition);

							return priority;
						}
					}

					// The mouse is not above a child dockable.
					if (orientation == ORIENTATION_HORIZONTAL) {
						// Get the component of the current dock.
						Point dockPosition = new Point();
						Component dockComponent = (Component) ((Dock) childDocks.get(childDocks.size() - 1));
						dockPosition.setLocation(dockComponent.getLocation().x + dockComponent.getSize().width, dockComponent.getLocation().y);

						// The width of the rectangle.
						int width = 0;
						if (dockable instanceof CompositeDockable) {
							width = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE).width;
						} else {
							width = dockable.getContent().getPreferredSize().width;
						}

						// Set the rectangle on this dock.
						rectangle.setLocation(dockPosition);
						rectangle.setSize(width, dockComponent.getSize().height);

						return priority;
					} else {
						// Get the component of the current dock.
						Point dockPosition = new Point();
						Component dockComponent = (Component) ((Dock) childDocks.get(childDocks.size() - 1));
						dockPosition.setLocation(dockComponent.getLocation().x, dockComponent.getLocation().y + dockComponent.getSize().height);

						// The height of the rectangle.
						int height = 0;
						if (dockable instanceof CompositeDockable) {
							height = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE).height;
						} else {
							height = dockable.getContent().getPreferredSize().height;
						}

						// Set the rectangle on this dock.
						rectangle.setLocation(dockPosition);
						rectangle.setSize(dockComponent.getSize().width, height);

						return priority;

					}
				} else {

					// Do we have horizontal orientation?
					if (orientation == ORIENTATION_HORIZONTAL) {
						// Initialize the help fields.
						int currentDockWidth = 0;
						int nextDockWidth = ((Component) childDocks.get(0)).getSize().width;
						int nextDockStart = 0; // The x-value where the next dock starts.

						// The docking rectangle starts at the left side of the deepest panel and 
						// ends in the middle of the first dock.
						rectangle.setBounds(0, 0, nextDockWidth / 2, dockPanel.getSize().height);

						// Iterate over the dockables - 1.
						for (int index = 0; index < childDocks.size() - 1; index++) {
							// Is the location in this rectangle?
							if (rectangle.contains(relativeLocation)) {
								return priority;
							}

							// The docking rectangle starts at the middle of the currnet dock and 
							// ends in the middle of the next dock.
							currentDockWidth = nextDockWidth;
							nextDockWidth = ((Component) childDocks.get(index + 1)).getSize().width;
							nextDockStart += currentDockWidth;
							rectangle.setBounds(nextDockStart - currentDockWidth / 2, 0,
									(currentDockWidth + nextDockWidth) / 2, dockPanel.getSize().height);
						}

						// Is the location in this rectangle?
						if (rectangle.contains(relativeLocation)) {
							return priority;
						}

						// The docking rectangle starts at the middle of the last dock and 
						// ends at the right side of the deepest panel.
						currentDockWidth = nextDockWidth;
						nextDockStart += currentDockWidth;
						rectangle.setBounds(nextDockStart - currentDockWidth / 2, 0,
								(currentDockWidth) / 2, dockPanel.getSize().height);

						// Is the location in this rectangle?
						if (rectangle.contains(relativeLocation)) {
							return priority;
						}

					}

					// Do we have vertical orientation?
					if (orientation == ORIENTATION_VERTICAL) {
						// Initialize the help fields.
						int currentDockHeight = 0;
						int nextDockHeight = ((Component) childDocks.get(0)).getSize().height;
						int nextDockStart = 0; // The y-value where the next dock starts.

						// The docking rectangle starts at the top of the deepest panel and 
						// ends in the middle of the first dock.
						rectangle.setBounds(0, 0, dockPanel.getSize().width, nextDockHeight / 2);

						// Iterate over the dockables - 1.
						for (int index = 0; index < childDocks.size() - 1; index++) {
							// Is the location in this rectangle?
							if (rectangle.contains(relativeLocation)) {
								return priority;
							}

							// The docking rectangle starts at the middle of the currnet dock and 
							// ends in the middle of the next dock.
							currentDockHeight = nextDockHeight;
							nextDockHeight = ((Component) childDocks.get(index + 1)).getSize().height;
							nextDockStart += currentDockHeight;
							rectangle.setBounds(0, nextDockStart - currentDockHeight / 2,
									dockPanel.getSize().width, (currentDockHeight + nextDockHeight) / 2);
						}

						// Is the location in this rectangle?
						if (rectangle.contains(relativeLocation)) {
							return priority;
						}

						// The docking rectangle starts at the middle of the last dock and 
						// ends at the bottom of the deepest panel.
						currentDockHeight = nextDockHeight;
						nextDockStart += currentDockHeight;
						rectangle.setBounds(0, nextDockStart - currentDockHeight / 2,
								dockPanel.getSize().width, (currentDockHeight) / 2);

						// Is the location in this rectangle?
						if (rectangle.contains(relativeLocation)) {
							return priority;
						}

					}
				}
			}
		}

		return priority;

	}

	public boolean addDockable(Dockable dockableToAdd, Point relativeLocation,
							   Point dockableOffset) {

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

		// SAve the horizontalDockingMode, verticalDockingMode, orientation, and grid.
		PropertiesUtil.setInteger(properties, prefix + "horizontalDockingMode", horizontalDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "verticalDockingMode", verticalDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "orientation", orientation);
		PropertiesUtil.setBoolean(properties, prefix + "grid", grid);

		// Save the class of the child dock factory and its properties.
		String className = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", className);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Iterate over the child docks.
		for (int index = 0; index < childDocks.size(); index++) {
			// Get the child dock.
			Dock dock = (Dock) childDocks.get(index);

			// Get the ID of the childDock.
			String childDockId = (String) childDockIds.get(dock);

			// Save the position.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(dock));
		}

	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException {

		// Set the horizontalDockingMode, verticalDockingMode, grid and orientation.
		int horizontalDockingMode = DockingMode.HORIZONTAL_LINE;
		int verticalDockingMode = DockingMode.VERTICAL_LINE;
		int orientation = ORIENTATION_HORIZONTAL;
		boolean grid = false;
		horizontalDockingMode = PropertiesUtil.getInteger(properties, prefix + "horizontalDockingMode", horizontalDockingMode);
		verticalDockingMode = PropertiesUtil.getInteger(properties, prefix + "verticalDockingMode", verticalDockingMode);
		orientation = PropertiesUtil.getInteger(properties, prefix + "orientation", orientation);
		grid = PropertiesUtil.getBoolean(properties, prefix + "grid", grid);
		setOrientation(orientation);
		setGrid(grid);
		this.horizontalDockingMode = horizontalDockingMode;
		this.verticalDockingMode = verticalDockingMode;

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
		Iterator keyIterator = newChildDocks.keySet().iterator();
		while (keyIterator.hasNext()) {
			// Get the ID of the child dock.
			String childDockId = (String) keyIterator.next();

			Position position = null;
			position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, position);
			childDockIdsArray[position.getPosition(0)] = childDockId;
			//TODO what if a child dock is not there?
		}

		// Add the child docks.
		int position = 0;
		for (int index = 0; index < childDockIdsArray.length; index++) {
			// Get the child dock.
			Dock dock = (Dock) newChildDocks.get(childDockIdsArray[index]);

			// Add only if the child is not empty.
			if (!dock.isEmpty()) {
				addChildDock(dock, new Position(position));
				position++;
			}
		}

	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	// Implementations of ParentDock.

	public void addChildDock(Dock dock, Position position) throws IllegalStateException {

		// Get the position in the line.
		int linePosition = getChildDockCount();
		if (position.getDimensions() == 1) {
			if ((position.getPosition(0) >= 0) && (position.getPosition(0) <= getChildDockCount())) {
				linePosition = position.getPosition(0);
			}
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dock));

		// Add the dock to the list of child docks.
		childDocks.add(linePosition, dock);
		dock.setParentDock(this);

		// Remove and add all the childdocks from the dock panel.
		dockPanel.removeAll();
		for (int index = 0; index < childDocks.size(); index++) {
			dockPanel.add((Component) childDocks.get(index));
		}

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
			dockPanel.remove((Component) emptyChildDock);
			childDocks.remove(emptyChildDock);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

			// Are we empty and there aren't any ghosts?
			if ((isEmpty()) &&
					(ghostDockPanel == null) &&
					(ghostAlignmentPanel == null) &&
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
			ghostAlignmentPanel = alignmentPanel;
			ghostAlignmentPanel.setVisible(false);

			// Create and add the panel for the docks.
			initializeUi();

			// Iterate over the remaining child docks.
			for (int index = 0; index < childDocks.size(); index++) {
				Dock childDock = (Dock) childDocks.get(index);

				// Remove the dock from the ghost panel.
				ghostDockPanel.remove((Component) childDock);

				// Add the dock to the new panel.
				dockPanel.add((Component) childDock);
			}

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));
		}

	}

	public void clearGhosts() {

		if (ghostDockPanel != null) {
			this.remove(ghostAlignmentPanel);
			ghostAlignmentPanel = null;
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
	 * Gets the orientation of the composite line dock. This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 * The default is ORIENTATION_HORIZONTAL.
	 *
	 * @return The orientation of the composite line dock.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Sets the orientation of the composite line dock. This can be ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL.
	 *
	 * @param newOrientation The orientation of the composite line dock.
	 */
	public void setOrientation(int newOrientation) {

		// Do we have a new value?
		if (newOrientation != orientation) {
			// Set the new orientation.
			this.orientation = newOrientation;

			// Rebuild the UI.
			rebuildUI();
		}

	}

	/**
	 * Determines if the docks have the same size.
	 *
	 * @return True when the docks have the same size, false when their size is
	 * computed with their preferred sizes.
	 */
	public boolean getGrid() {
		return grid;
	}

	/**
	 * Sets if the docks should have the same size.
	 *
	 * @param newGrid True when the docks have the same size, false when their size is
	 *                computed with their preferred sizes.
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
	 * For composite tool bars and composite minimize bars it should be <code>true</code>,
	 * for regular composite line docks it should be <code>false</code>.
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


	// Protected metods.

	/**
	 * Checks the docking modes of the dockable. True is returned, if we have a horizontal dock orientation
	 * (ORIENTATION_HORIZONTAL), and if the dockable has <code>verticalDockingMode</code> as possible docking mode.
	 * True is also returned, if we have a vertical dock orientation
	 * (ORIENTATION_VERTICAL), and if the dockable has <code>verticalDockingMode</code> as possible docking mode.
	 *
	 * @return True if we have a horizontal dock orientation
	 * (ORIENTATION_HORIZONTAL), and if the dockable has <code>horizontalDockingMode</code>
	 * as possible docking mode.
	 * Also true if we have a vertical dock orientation
	 * (ORIENTATION_VERTICAL), and if the dockable has <code>verticalDockingMode</code>
	 * as possible docking mode.
	 * Otherwise false is returned.
	 * @param    dockable                    The dockable to add.
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
	 * Gets the docking mode for a dockable that is docked in this dock.
	 * This is <code>horizontalDockingMode</code>, when the orientation of is dock is horizontal.
	 * This is <code>verticalDockingMode</code> , when the orientation of is dock is vertical.
	 *
	 * @return                                <code>horizontalDockingMode</code> when the orientation of is dock is horizontal,
	 * <code>verticalDockingMode</code>  when the orientation of is dock is vertical.
	 */
	protected int getDockingMode() {

		switch (getOrientation()) {

			case ORIENTATION_HORIZONTAL:
				return horizontalDockingMode;
			case ORIENTATION_VERTICAL:
				return verticalDockingMode;
			default:
				throw new IllegalArgumentException("The orinetation [" + getOrientation() + "] is illegal for this dock.");
		}

	}

	/**
	 * Gets the position where the dockable should be docked in the dock given the mouse position.
	 * The position is the future index of the dockable in the line. The first position is 0.
	 *
	 * @param newDockable      The dockable to add.
	 * @param relativePosition The relative mouse location in this dock.
	 * @return The position where the dockable should be docked in the dock.
	 * The position is the future index of the dockable in the line.
	 */
	protected int getDockPosition(Dockable newDockable, Point relativePosition) {

		// Are there no child docks already?
		if (childDocks.size() == 0) {
			return 0;
		}

		// Do we have horizontal orientation?
		if (orientation == ORIENTATION_HORIZONTAL) {
			// We have a horizontal orientation.

			// Initialize the help fields.
			int currentDockWidth = 0;
			int nextDockWidth = ((Component) childDocks.get(0)).getSize().width;
			int nextDockStart = 0; // The x-value where the next dock starts.

			// The rectangle starts at the left side of the deepest panel and
			// ends at the middle of the first child dock.
			helpRectangle.setBounds(0, 0, nextDockWidth / 2, dockPanel.getSize().height);

			// Iterate over the docks - 1.
			for (int index = 0; index < childDocks.size() - 1; index++) {
				// Is the location in this priority rectangle?
				if (helpRectangle.contains(relativePosition)) {
					return index;
				}

				// The next rectangle starts at the middle of the current child dock and
				// ends at the middle of the next child dock.
				currentDockWidth = nextDockWidth;
				nextDockWidth = ((Component) childDocks.get(index + 1)).getSize().width;
				nextDockStart += currentDockWidth;
				helpRectangle.setBounds(nextDockStart - currentDockWidth / 2, 0,
						(currentDockWidth + nextDockWidth) / 2, dockPanel.getSize().height);
			}

			// Is the location in this priority rectangle?
			if (helpRectangle.contains(relativePosition)) {
				return childDocks.size() - 1;
			}

			// The next rectangle starts at the middle of the last child dock and
			// ends at the right of the deepest panel.
			currentDockWidth = nextDockWidth;
			nextDockWidth = 0;
			nextDockStart += currentDockWidth;
			helpRectangle.setBounds(nextDockStart - currentDockWidth / 2, 0,
					(currentDockWidth + nextDockWidth) / 2, dockPanel.getSize().height);

			// Is the location in this priority rectangle?
			if (helpRectangle.contains(relativePosition)) {
				return childDocks.size();
			}

		} else {
			// We have a vertical orientation.

			// Initialize the help fields.
			int currentDockHeight = 0;
			int nextDockHeight = ((Component) childDocks.get(0)).getSize().height;
			int nextDockStart = 0; // The y-value where the next dock starts.

			// The rectangle starts at the top of the deepest panel and
			// ends at the middle of the first child dock.
			helpRectangle.setBounds(0, 0, dockPanel.getSize().width, nextDockHeight / 2);

			// Iterate over the child docks - 1.
			for (int index = 0; index < childDocks.size() - 1; index++) {
				// Is the location in this priority rectangle?
				if (helpRectangle.contains(relativePosition)) {
					return index;
				}

				// The next rectangle starts at the middle of the current child dock and
				// ends at the middle of the next child dock.
				currentDockHeight = nextDockHeight;
				nextDockHeight = ((Component) childDocks.get(index + 1)).getSize().height;
				nextDockStart += currentDockHeight;
				helpRectangle.setBounds(0, nextDockStart - currentDockHeight / 2,
						dockPanel.getSize().width, (currentDockHeight + nextDockHeight) / 2);
			}

			// Is the location in this priority rectangle?
			if (helpRectangle.contains(relativePosition)) {
				return childDocks.size() - 1;
			}

			// The next rectangle starts at the middle of the last child dock and
			// ends at the bottom of the deepest panel.
			currentDockHeight = nextDockHeight;
			nextDockHeight = 0;
			nextDockStart += currentDockHeight;
			helpRectangle.setBounds(0, nextDockStart - currentDockHeight / 2,
					dockPanel.getSize().width, (currentDockHeight + nextDockHeight) / 2);

			// Is the location in this priority rectangle?
			if (helpRectangle.contains(relativePosition)) {
				return childDocks.size();
			}

		}

		return childDocks.size();
	}

	/**
	 * Determines if the given dockable can be added to this dock with priority.
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
			priorityRectangle.setBounds(priorityBorder, priorityBorder,
					getSize().width - priorityBorder * 2, getSize().height - priorityBorder * 2);
			if (priorityRectangle.contains(relativeLocation)) {
				// Inside the priority rectangle we can dock with priority.
				return true;
			}

			// Outside the priority rectangle.
			return false;
		}

		// When we are here, there are already dockables in this dock.

		// Do we have a horizontal orientation?
		if (orientation == ORIENTATION_HORIZONTAL) {
			// We have a horizontal orientation.

			// There is priority if we are near the vertical borders of the dockables
			// and not near the top and bottom border of the dock.

			// Set the priority rectangle at the left side of the first dockable.
			priorityRectangle.setBounds(-priorityBorder, priorityBorder,
					priorityBorder * 2, dockPanel.getSize().height - priorityBorder * 2);

			// Iterate over all the dockables.
			for (int index = 0; index < childDocks.size(); index++) {
				// Is the location in this priority rectangle?
				if (priorityRectangle.contains(relativeLocation)) {
					// We are in a priority rectangle. We can dock with priority.
					return true;
				}

				// Shift the rectangle horizontally to the left side of the next dockable.
				int childWidth = ((Component) childDocks.get(index)).getSize().width;
				priorityRectangle.translate(childWidth, 0);
			}

			// Now we are at the right side of the last dockable.
			// Is the location in this priority rectangle?
			if (priorityRectangle.contains(relativeLocation)) {
				// We are in a priority rectangle. We can dock with priority.
				return true;
			}

		} else {
			// We have a vertical orientation.

			// There is priority if we are near the horizontal borders of the dockables
			// and not near the left and right border of the dock.

			// Set the priority rectangle at the top of the first dockable.
			priorityRectangle.setBounds(priorityBorder, -priorityBorder,
					dockPanel.getSize().width - priorityBorder * 2, priorityBorder * 2);

			// Iterate over all the dockables.
			for (int index = 0; index < childDocks.size(); index++) {
				// Is the location in this priority rectangle?
				if (priorityRectangle.contains(relativeLocation)) {
					// We are in a priority rectangle. We can dock with priority.
					return true;
				}

				// Shift the rectangle vertically to the top of the next dockable.
				int childHeight = ((Component) childDocks.get(index)).getSize().height;
				priorityRectangle.translate(0, childHeight);
			}

			// Is the location in this priority rectangle?
			if (priorityRectangle.contains(relativeLocation)) {
				// We are in a priority rectangle. We can dock with priority.
				return true;
			}

		}


		// We can't dock with priority.
		return false;
	}

	// Private metods.

	/**
	 * Creates the panels for the child docks and adds them to this dock.
	 */
	private void initializeUi() {

		// Create the deepest panel that will contain the docks.
		dockPanel = new JPanel();
		if (grid) {
			if (orientation == ORIENTATION_VERTICAL) {
				dockPanel.setLayout(new GridLayout(0, 1));
			} else {
				dockPanel.setLayout(new GridLayout(1, 0));
			}
		} else {

			if (orientation == ORIENTATION_VERTICAL) {
				dockPanel.setLayout(new BoxLayout(dockPanel, BoxLayout.Y_AXIS));
			} else {
				dockPanel.setLayout(new BoxLayout(dockPanel, BoxLayout.X_AXIS));
			}
		}

		// Create the panel that takes care of the alignment of the docks.
		alignmentPanel = new JPanel();
		if (orientation == ORIENTATION_VERTICAL) {
			alignmentPanel.setLayout(new BoxLayout(alignmentPanel, BoxLayout.Y_AXIS));
		} else {
			alignmentPanel.setLayout(new BoxLayout(alignmentPanel, BoxLayout.X_AXIS));
		}

		// Add the panels and glues.
		// At the left or top:
		// There is a glue at the left or top.
		if (orientation == ORIENTATION_HORIZONTAL) {
			alignmentPanel.add(Box.createVerticalGlue());
		} else {
			alignmentPanel.add(Box.createHorizontalGlue());
		}


		// In the middle:
		alignmentPanel.add(dockPanel);

		// Add it.
		this.add(alignmentPanel, BorderLayout.CENTER);

	}

	/**
	 * Rebuilds the whole dock again with the existing child docks.
	 */
	private void rebuildUI() {
		this.removeAll();

		// Create and add the panel for the docks.
		initializeUi();

		// Add all the child docks.
		for (int index = 0; index < childDocks.size(); index++) {
			// Add the child to the panel.
			Dock childDock = (Dock) childDocks.get(index);
			dockPanel.add((Component) childDock);
		}

	}

}
