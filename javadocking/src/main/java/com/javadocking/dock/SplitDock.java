package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.dock.factory.*;
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
 * This is a composite dock that can contain zero, one, or two child docks. It can not contain dockables.
 * When dockables are added, child docks are created, and the dockables are added to the child docks.
 * </p>
 * <p>
 * Information on using split docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html#SplitDock" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * When there is only one child dock, this dock is located in the center of this dock. When there are 2
 * child docks, there is one at the top and one at the bottom, or there is one at the left and one at the
 * right. The child docks are put in a split pane created with the component factory of the docking manager
 * {@link com.javadocking.DockingManager#getComponentFactory()} with the method
 * {@link com.javadocking.component.SwComponentFactory#createJSplitPane()}.
 * </p>
 * <p>
 * The positions for child docks of this dock are one-dimensional.
 * The possible child docks and their first position values are:
 * <ul>
 * <li>The child dock in the center if there is only one child dock: Position.CENTER.</li>
 * <li>The child dock at the top: {@link Position#TOP}.</li>
 * <li>The child dock at the bottom: {@link Position#BOTTOM}.</li>
 * <li>The child dock at the right: {@link Position#RIGHT}.</li>
 * <li>The child dock at the left: {@link Position#LEFT}.</li>
 * </ul>
 *
 * <p>
 * A dockable can only be added if it has as one of its docking modes:
 * <ul>
 * <li>{@link DockingMode#LEFT}</li>
 * <li>{@link DockingMode#RIGHT}</li>
 * <li>{@link DockingMode#TOP}</li>
 * <li>{@link DockingMode#BOTTOM}</li>
 * </ul>
 * It can only be added in a position that corresponds with one of its docking modes.
 *
 * <p>
 * If the mouse is inside a priority rectangle, the dockable can be docked with priority (see {@link Priority#CAN_DOCK_WITH_PRIORITY}).
 * When the mouse is inside the panel of this dock, but outside the priority rectangles,
 * the dockable can be docked, but without priority (see {@link Priority#CAN_DOCK}).
 * When the dock is empty, the only  priority rectangle is in the middle of the dock.
 * When there is already a child dock, there are 4 priority rectangles at the 4 borders of the panel of this dock.
 * </p>
 * <p>
 * When this dock contains no child docks, it is empty. When this dock contains two child docks, it is full.
 * </p>
 * <p>
 * When this dock is empty, and a first dockable is added, a child dock is created with the factory retrieved by {@link #getChildDockFactory()}.
 * The dockable is added to the child dock. The child dock is put in the center of this panel.
 * </p>
 * <p>
 * When there is already one child dock in the center, and a new dockable is added, again a child dock is created
 * with the factory retrieved by {@link #getChildDockFactory()}.
 * The dockable is added to the child dock. But now for both child docks a split dock is created with the factory
 * retrieved by {@link #getCompositeChildDockFactory()}. The child docks are added to these new docks, and these new docks
 * are put at the left and the right side of the split of the split pane. These new docks are now the child docks of this split dock.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class SplitDock extends JPanel implements CompositeDock {
	// Static fields.


	/**
	 * Defines a horizontal orientation of the split dock. The split is vertical.
	 */
	private static final int HORIZONTAL_SPLIT = JSplitPane.HORIZONTAL_SPLIT;
	/**
	 * Defines a vertical orientation of the split dock. The split is horizontal.
	 */
	private static final int VERTICAL_SPLIT = JSplitPane.VERTICAL_SPLIT;

	/**
	 * The name of the <code>orientation</code> property for the orientation.
	 */
	private static final String PROPERTY_ORIENTATION = "orientation";
	/**
	 * The width of the dock.
	 */
	private static final String PROPERTY_LAST_WIDTH = "lastWidth";
	/**
	 * The height of the dock.
	 */
	private static final String PROPERTY_LAST_HEIGHT = "lastHeight";
	/**
	 * The name of the <code>dividerLocation</code> property for the location of the divider.
	 */
	private static final String PROPERTY_DIVIDER_LOCATION = "dividerLocation";
	/**
	 * The name of the <code>removeLastEmptyChild</code> property.
	 */
	private static final String PROPERTY_REMOVE_LAST_EMPTY_CHILD = "removeLastEmptyChild";

	/**
	 * The relative top offset of the priority rectangle when the dock is empty.
	 */
	private static final double centerPriorityRectangleRelativeTopOffset = 2.0 / 8.0;
	/**
	 * The relative left offset of the priority rectangle when the dock is empty.
	 */
	private static final double centerPriorityRectangleRelativeLeftOffset = 2.0 / 8.0;
	/**
	 * The relative bottom offset of the priority rectangle when the dock is empty.
	 */
	private static final double centerPriorityRectangleRelativeBottomOffset = 2.0 / 8.0;
	/**
	 * The relative right offset of the priority rectangle when the dock is empty.
	 */
	private static final double centerPriorityRectangleRelativeRightOffset = 2.0 / 8.0;

	/**
	 * The relative width of the left priority rectangle when there is already one child dock.
	 */
	private static final double leftPriorityRectangleRelativeWidth = 2.0 / 8.0;
	/**
	 * The relative width of the right priority rectangle when there is already one child dock.
	 */
	private static final double rightPriorityRectangleRelativeWidth = 2.0 / 8.0;
	/**
	 * The relative height of the top priority rectangle when there is already one child dock.
	 */
	private static final double topPriorityRectangleRelativeHeight = 2.0 / 8.0;
	/**
	 * The relative height of the bottom priority rectangle when there is already one child dock.
	 */
	private static final double bottomPriorityRectangleRelativeHeight = 2.0 / 8.0;

	// Fields.

	/**
	 * The parent dock of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The child dock when there is only one child. When there are zero or 2 child docks
	 * this is null.
	 */
	private Dock singleChildDock;
	/**
	 * The left or top child dock when there are 2 child docks. When there are zero or 1 child docks this is null.
	 */
	private Dock leftChildDock;
	/**
	 * The right or bottom child dock when there are 2 child docks. When there are zero or 1 child docks this is null.
	 */
	private Dock rightChildDock;
	/**
	 * This factory creates the leaf child docks.
	 */
	private DockFactory childDockFactory;
	/**
	 * This factory creates the new split docks.
	 * This dock factory should create a <code>SplitDock</code> when it is used with the modes
	 * DockingMode.LEFT, DockingMode.RIHT, DockingMode.TOP or DockingMode.BOTTOM.
	 */
	private CompositeDockFactory compositeChildDockFactory;
	/**
	 * This is the split pane that contains the 2 child docks. When there are zero or 1 child docks this is null.
	 * It is created by the component factory of the {@link DockingManager}.
	 */
	private JSplitPane splitPane;
	/**
	 * This is the rectangle in which a dockable can be docked with priority. We keep it as field
	 * because we don't want to create every time a new rectangle.
	 */
	private Rectangle priorityRectangle = new Rectangle();
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();

	// Ghosts.
	/**
	 * This is a dock that has to be removed later. It is already made invisible.
	 * It cannot be removed now because there are still listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private Dock ghostSingleChild;
	/**
	 * This is an old split pane that has to be removed later. It is already made invisible.
	 * It cannot be removed now because it contains an old dock that still has listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private JSplitPane ghostSplitPane;

	/**
	 * This width is used when the dock is not already displayed in a visible window. In that case its width is still 0.
	 */
	private int lastWidth;
	/**
	 * This height is used when the dock is not already displayed in a visible window. In that case its height is still 0.
	 */
	private int lastHeight;

	/**
	 * Determines if the last child dock that is empty, has to be removed.
	 */
	private boolean removeLastEmptyChild = true;

	/**
	 * The last location of the divider.
	 */
	private int lastDividerLocation;

	// Constructors.

	/**
	 * Constructs a split dock with a {@link LeafDockFactory}
	 * as child dock factory and a {@link SplitDockFactory} as factory
	 * for the composite child docks.
	 */
	public SplitDock() {
		this(new LeafDockFactory(), new SplitDockFactory());
	}

	/**
	 * Constructs a split dock with the given child dock factory.
	 *
	 * @param childDockFactory The factory for creating child docks.
	 */
	public SplitDock(DockFactory childDockFactory) {
		this(childDockFactory, new SplitDockFactory());
	}

	/**
	 * Constructs a split dock with the given child dock factories.
	 *
	 * @param childDockFactory      The factory for creating child docks.
	 * @param splitChildDockFactory The factory for creating composite child docks.
	 */
	public SplitDock(DockFactory childDockFactory, CompositeDockFactory splitChildDockFactory) {

		// Set the layout.
		super(new BorderLayout());

		// Set the factories.
		this.childDockFactory = childDockFactory;
		this.compositeChildDockFactory = splitChildDockFactory;

	}


	// Implementations of Dock.

	/**
	 * Calculates the docking mode that corresponds with a given dock position in this dock.
	 *
	 * @param dockPosition The dock position of a dockable in this dock.
	 *                     This should be Position.TOP, Position.BOTTOM, Position.LEFT
	 *                     Position.RIGHT or Position.CENTER.
	 * @throws IllegalArgumentException If the given position is not a valid position for this dock.
	 * @return The docking mode that corresponds with the given dock position.
	 */
	private static int getDockingMode(int dockPosition) {
		switch (dockPosition) {
			case Position.TOP:
				return DockingMode.TOP;
			case Position.BOTTOM:
				return DockingMode.BOTTOM;
			case Position.LEFT:
				return DockingMode.LEFT;
			case Position.RIGHT:
				return DockingMode.RIGHT;
			case Position.CENTER:
				return DockingMode.CENTER;
		}

		throw new IllegalArgumentException("Position [" + dockPosition + "] is not a valid position in a BorderDock.");
	}

	/**
	 * <p>
	 * Determines if the dockable can be added.
	 * </p>
	 * <p>
	 * The dockable can be added, if:
	 * <ul>
	 * <li>the dockable has {@link DockingMode#LEFT}, {@link DockingMode#RIGHT}, {@link DockingMode#TOP}, or
	 * {@link DockingMode#BOTTOM} as one of its possible docking modes.</li>
	 * <li>the dock is not full.</li>
	 * <li>if the dock factory can create a child dock for the given dockable.</li>
	 * </ul>
	 *
	 * <p>
	 * There are also some optimizations done. We want to prevent that dockables
	 * are removed from this dock and added to this dock again at the same
	 * position. In that case {@link Priority#CANNOT_DOCK} is returned.
	 * </p>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a split dock.
		int dockPositions = dockable.getDockingModes();
		if (((dockPositions & DockingMode.LEFT) == 0) &&
				((dockPositions & DockingMode.RIGHT) == 0) &&
				((dockPositions & DockingMode.TOP) == 0) &&
				((dockPositions & DockingMode.BOTTOM) == 0)) {
			return Priority.CANNOT_DOCK;
		}

		// We can only dock if we are not full.
		if (isFull()) {
			// Sorry, we are full.
			return Priority.CANNOT_DOCK;
		}

		// Get the docking mode.
		int dockPosition = getDockPosition(relativeLocation, dockable);
		int dockingMode = getDockingMode(dockPosition);

		// We can only dock if the dock factory can create a dock.
		if (childDockFactory.createDock(dockable, dockingMode) != null) {
			// Test if the dockable is already docked in this dock and at the same position.
			// In that case return Dock.CANNOT_DOCK.

			// Is the dockable already in this dock and are there no others?
			List childrenOfDockable = new ArrayList();
			List childrenOfDock = new ArrayList();
			DockingUtil.retrieveDockables(dockable, childrenOfDockable);
			DockingUtil.retrieveDockables(this, childrenOfDock);
			if (sameElements(childrenOfDockable, childrenOfDock)) {
				return Priority.CANNOT_DOCK;
			}

			// Is the parent of this dock a split dock that is full
			// and with the given dockable already at the same location as where we want to dock it now?
			CompositeDock parentDock = this.getParentDock();
			if (parentDock != null) {
				if (parentDock instanceof SplitDock) {
					SplitDock parentSplitDock = (SplitDock) parentDock;
					if (parentDock.isFull()) {
						int parentOrientation = parentSplitDock.splitPane.getOrientation();

						// Get the position for the new dockable.
						int position = getDockPosition(relativeLocation, dockable);

						// Get the dock in the same position of the parent dock.
						Dock correspondentChild = null;
						if ((position == Position.LEFT) && (parentOrientation == JSplitPane.HORIZONTAL_SPLIT)) {
							correspondentChild = parentSplitDock.leftChildDock;
						} else if ((position == Position.RIGHT) && (parentOrientation == JSplitPane.HORIZONTAL_SPLIT)) {
							correspondentChild = parentSplitDock.rightChildDock;
						} else if ((position == Position.TOP) && (parentOrientation == JSplitPane.VERTICAL_SPLIT)) {
							correspondentChild = parentSplitDock.leftChildDock;
						} else if ((position == Position.BOTTOM) && (parentOrientation == JSplitPane.VERTICAL_SPLIT)) {
							correspondentChild = parentSplitDock.rightChildDock;
						}
						if (correspondentChild != null) {
							// Does this child dock has the same dockables as the dockable we want to add.
							childrenOfDock = new ArrayList();
							DockingUtil.retrieveDockables(correspondentChild, childrenOfDock);
							if (sameElements(childrenOfDockable, childrenOfDock)) {
								return Priority.CANNOT_DOCK;
							}
						}

					}
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

	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation, Point dockableOffset, Rectangle rectangle) {

		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {
			// Is the dock still empty?
			if (singleChildDock == null) {
				// The docking rectangle is the rectangle defined by this dock panel.
				rectangle.setBounds(0, 0, getSize().width, getSize().height);
			} else {
				// Get the position for the new dockable.
				int position = getDockPosition(relativeLocation, dockable);
				int dockingMode = getDockingMode(position);

				// Get the prefered size of the dockable.
				Dimension childDockPreferredSize = ((Component) singleChildDock).getPreferredSize();
				Dimension newDockablePreferredSize = childDockFactory.getDockPreferredSize(dockable, dockingMode);

				// Get the bounds.
				if (position == Position.LEFT) {
					int dockingWidth = getChildDockWidth(newDockablePreferredSize.width, childDockPreferredSize.width, getSize().width, position);
					rectangle.setBounds(0, 0, dockingWidth, getSize().height);
				} else if (position == Position.RIGHT) {
					int dockingWidth = getChildDockWidth(newDockablePreferredSize.width, childDockPreferredSize.width, getSize().width, position);
					rectangle.setBounds(getSize().width - dockingWidth, 0, dockingWidth, getSize().height);
				} else if (position == Position.TOP) {
					int dockingHeight = getChildDockWidth(newDockablePreferredSize.height, childDockPreferredSize.height, getSize().height, position);
					rectangle.setBounds(0, 0, getSize().width, dockingHeight);
				} else if (position == Position.BOTTOM) {
					int dockingHeight = getChildDockWidth(newDockablePreferredSize.height, childDockPreferredSize.height, getSize().height, position);
					rectangle.setBounds(0, getSize().height - dockingHeight, getSize().width, dockingHeight);
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

		// Are we still empty?
		if (singleChildDock == null) {
			// Create the leaf child dock.
			Dock newChildDock = childDockFactory.createDock(dockableToAdd, DockingMode.CENTER);

			// Add the dockable.
			newChildDock.addDockable(dockableToAdd, new Point(), dockableOffset);

			// Inform the listeners.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, newChildDock));

			// Add the child dock.
			singleChildDock = newChildDock;
			singleChildDock.setParentDock(this);
			add((Component) singleChildDock, BorderLayout.CENTER);

			// Inform the listeners.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, newChildDock));
		} else {
			// Get the position for the new dockable.
			int position = getDockPosition(relativeLocation, dockableToAdd);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, singleChildDock));

			// Remove everything.
			Dock oldSingleChildDock = singleChildDock;
			singleChildDock = null;
			this.remove((Component) oldSingleChildDock);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, oldSingleChildDock));

			// Get the preferred size of the current child dock. We need it later.
			Dimension currentChildDockPreferredSize = ((Component) oldSingleChildDock).getPreferredSize();

			// Create the new child split docks.
			SplitDock leftSplitDock = null;
			SplitDock rightSplitDock = null;

			// Add the dockables to this docks.
			try {
				switch (position) {
					case Position.LEFT:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.LEFT);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.RIGHT);
						rightSplitDock.setSingleChildDock(oldSingleChildDock);
						leftSplitDock.addDockable(dockableToAdd, new Point(), dockableOffset);
						break;
					case Position.TOP:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.TOP);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.BOTTOM);
						rightSplitDock.setSingleChildDock(oldSingleChildDock);
						leftSplitDock.addDockable(dockableToAdd, new Point(), dockableOffset);
						break;
					case Position.RIGHT:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.LEFT);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.RIGHT);
						leftSplitDock.setSingleChildDock(oldSingleChildDock);
						rightSplitDock.addDockable(dockableToAdd, new Point(), dockableOffset);
						break;
					case Position.BOTTOM:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.TOP);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.BOTTOM);
						leftSplitDock.setSingleChildDock(oldSingleChildDock);
						rightSplitDock.addDockable(dockableToAdd, new Point(), dockableOffset);
						break;
				}
			} catch (ClassCastException exception) {
				System.out.println("The splitChildDockFactory should create a com.javadocking.dock.SplitDock for the modes DockingMode.LEFT, DockingMode.RIGHT, DockingMode.TOP and DockingMode.BOTTOM.");
				exception.printStackTrace();
			}

			// Where has to be the divider.
			// Get the prefered size of the dockable.
			int dockingMode = getDockingMode(position);
			Dimension preferredSize = childDockFactory.getDockPreferredSize(dockableToAdd, dockingMode);
			int dividerLocation = this.getSize().width / 2;
			if (position == Position.LEFT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, currentChildDockPreferredSize.width, getSize().width, position);
				dividerLocation = dockingWidth;
			} else if (position == Position.RIGHT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, currentChildDockPreferredSize.width, getSize().width, position);
				dividerLocation = this.getSize().width - dockingWidth;
			} else if (position == Position.TOP) {
				int dockingHeight = getChildDockWidth(preferredSize.height, currentChildDockPreferredSize.height, getSize().height, position);
				dividerLocation = dockingHeight;
			} else if (position == Position.BOTTOM) {
				int dockingHeight = getChildDockWidth(preferredSize.height, currentChildDockPreferredSize.height, getSize().height, position);
				dividerLocation = this.getSize().height - dockingHeight;
			}

			// How do we have to split the pane?
			int orientation = HORIZONTAL_SPLIT;
			if ((position == Position.TOP) || (position == Position.BOTTOM)) {
				orientation = VERTICAL_SPLIT;
			}

			// Create the split pane.
			setDocks(leftSplitDock, rightSplitDock, orientation, dividerLocation);
		}

		// Repaint.
		SwingUtil.repaintParent(this);

		return true;

	}

	public boolean isEmpty() {
		return (singleChildDock == null) && (leftChildDock == null) && (rightChildDock == null);
	}

	public boolean isFull() {
		return (leftChildDock != null) && (rightChildDock != null);
	}

	public CompositeDock getParentDock() {
		return parentDock;
	}

	public void setParentDock(CompositeDock parentDock) {
		this.parentDock = parentDock;
	}

	public void saveProperties(String prefix, Properties properties, Map childDockIds) {

		// Save the class of the child dock factory and its properties.
		String leafChildDockFactoryClassName = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "leafChildDockFactory", leafChildDockFactoryClassName);
		childDockFactory.saveProperties(prefix + "leafChildDockFactory.", properties);

		// Save the class of the child dock factory and its properties.
		String splitChildDockFactoryClassName = compositeChildDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "splitChildDockFactory", splitChildDockFactoryClassName);
		compositeChildDockFactory.saveProperties(prefix + "splitChildDockFactory.", properties);

		// Save the orientation, divider location of the splitpane and the 'fixedDivider' property.
		if (splitPane != null) {
			PropertiesUtil.setInteger(properties, prefix + PROPERTY_ORIENTATION, splitPane.getOrientation());
			PropertiesUtil.setInteger(properties, prefix + PROPERTY_DIVIDER_LOCATION, splitPane.getDividerLocation());
		}

		// Save the position of the child docks.
		if ((leftChildDock != null) && (rightChildDock != null)) {
			// Get the ID of this child dock.
			String leftChildDockId = (String) childDockIds.get(leftChildDock);
			String rightChildDockId = (String) childDockIds.get(rightChildDock);

			// Save the position.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + leftChildDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(leftChildDock));
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + rightChildDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(rightChildDock));

		}

		PropertiesUtil.setInteger(properties, prefix + PROPERTY_LAST_WIDTH, getSize().width);
		PropertiesUtil.setInteger(properties, prefix + PROPERTY_LAST_HEIGHT, getSize().height);
		PropertiesUtil.setBoolean(properties, prefix + PROPERTY_REMOVE_LAST_EMPTY_CHILD, removeLastEmptyChild);

	}

	public void loadProperties(String prefix, Properties properties, Map childDocks, Map dockablesMap, Window owner) throws IOException {

		// Load the class and properties of the leaf child dock factory.
		try {
			String leafChildDockFactoryClassName = LeafDockFactory.class.getName();
			leafChildDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "leafChildDockFactory", leafChildDockFactoryClassName);
			Class leafChildDockFactoryClazz = Class.forName(leafChildDockFactoryClassName);
			childDockFactory = (DockFactory) leafChildDockFactoryClazz.newInstance();
			childDockFactory.loadProperties(prefix + "leafChildDockFactory.", properties);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the leaf child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TabDockFactory();
		}

		// Load the class and properties of the child dock factory.
		try {
			String splitChildDockFactoryClassName = SplitDockFactory.class.getName();
			splitChildDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "splitChildDockFactory", splitChildDockFactoryClassName);
			Class splitChildDockFactoryClazz = Class.forName(splitChildDockFactoryClassName);
			compositeChildDockFactory = (CompositeDockFactory) splitChildDockFactoryClazz.newInstance();
			compositeChildDockFactory.loadProperties(prefix + "splitChildDockFactory.", properties);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the split child dock factory.");
			exception.printStackTrace();
			compositeChildDockFactory = new SplitDockFactory();
		}

		lastWidth = PropertiesUtil.getInteger(properties, prefix + PROPERTY_LAST_WIDTH, lastWidth);
		lastHeight = PropertiesUtil.getInteger(properties, prefix + PROPERTY_LAST_HEIGHT, lastHeight);
		removeLastEmptyChild = PropertiesUtil.getBoolean(properties, prefix + PROPERTY_REMOVE_LAST_EMPTY_CHILD, removeLastEmptyChild);

		// How many child docks do we have? 0, 1 or 2.
		if (childDocks != null) {
			int childCount = childDocks.keySet().size();
			if (childCount == 1) {
				// Add the only child.
				Iterator iterator = childDocks.values().iterator();
				Dock childDock = (Dock) iterator.next();

				// Inform the listeners about the add.
				dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, childDock));

				setSingleChildDock(childDock);

				// Inform the listeners about the add.
				dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, childDock));

			} else if (childCount == 2) {
				// Get the IDs of the child docks.
				Iterator iterator = childDocks.keySet().iterator();
				String firstChildDockId = (String) iterator.next();
				String secondChildDockId = (String) iterator.next();

				// Get the position of the first child.
				Position position = null;
				position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + firstChildDockId + "." + Position.PROPERTY_POSITION, position);
				int firstChildPosition = position.getPosition(0);

				// Get the left and right split child dock.
				SplitDock leftSplitDock = null;
				SplitDock rightSplitDock = null;
				if ((firstChildPosition == Position.LEFT) || (firstChildPosition == Position.TOP)) {
					leftSplitDock = (SplitDock) childDocks.get(firstChildDockId);
					rightSplitDock = (SplitDock) childDocks.get(secondChildDockId);
				} else {
					leftSplitDock = (SplitDock) childDocks.get(secondChildDockId);
					rightSplitDock = (SplitDock) childDocks.get(firstChildDockId);
				}

				// Get the orientation and divider location property.
				int orientation = HORIZONTAL_SPLIT;
				int dividerLocation = 200;
				orientation = PropertiesUtil.getInteger(properties, prefix + PROPERTY_ORIENTATION, orientation);
				dividerLocation = PropertiesUtil.getInteger(properties, prefix + PROPERTY_DIVIDER_LOCATION, dividerLocation);
				lastDividerLocation = dividerLocation;

				// Set both child docks in a splitpane.
				setDocks(leftSplitDock, rightSplitDock, orientation, dividerLocation);
			}
		}

	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	// Implementations of CompositeDock.

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	public int getChildDockCount() {

		if (singleChildDock != null) {
			return 1;
		}

		if ((leftChildDock != null) && (rightChildDock != null)) {
			return 2;
		}

		return 0;

	}

	public Dock getChildDock(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getChildDockCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		if (singleChildDock != null) {
			return singleChildDock;
		}

		if ((leftChildDock != null) && (rightChildDock != null)) {
			if (index == 0) {
				return leftChildDock;
			} else {
				return rightChildDock;
			}
		}

		return null;
	}

	public Position getChildDockPosition(Dock childDock) throws IllegalArgumentException {

		if (childDock.equals(singleChildDock)) {
			return new Position(Position.CENTER);
		}

		if (splitPane != null) {
			if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
				if (childDock.equals(leftChildDock)) {
					return new Position(Position.LEFT);
				}
				if (childDock.equals(rightChildDock)) {
					return new Position(Position.RIGHT);
				}
			} else {
				if (childDock.equals(leftChildDock)) {
					return new Position(Position.TOP);
				}
				if (childDock.equals(rightChildDock)) {
					return new Position(Position.BOTTOM);
				}
			}
		}

		throw new IllegalArgumentException("The dock is not docked in this composite dock.");

	}

	public void emptyChild(Dock emptyChildDock) {

		// Do we have one single child dock?
		if ((singleChildDock != null) && (singleChildDock.equals(emptyChildDock))) {
			if (isRemoveLastEmptyChild()) {
				// Inform the listeners about the removal.
				dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

				// Remove this child.
				this.remove((Component) singleChildDock);
				singleChildDock = null;

				// Inform the listeners about the removal.
				dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

				// Inform the parent if we are empty.
				if ((parentDock != null) &&
						(ghostSingleChild == null) &&
						(ghostSplitPane == null)) {
					parentDock.emptyChild(this);
				}
			}
		} else if ((leftChildDock != null) && (leftChildDock.equals(emptyChildDock))) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// The left child dock is empty.
			// The right child dock becomes the single child dock.
			lastDividerLocation = splitPane.getDividerLocation();
			this.remove(splitPane);
			singleChildDock = rightChildDock;
			singleChildDock.setParentDock(this);
			rightChildDock = null;
			leftChildDock = null;
			splitPane = null;
			add((Component) singleChildDock, BorderLayout.CENTER);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

		} else if ((rightChildDock != null) && (rightChildDock.equals(emptyChildDock))) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

			// The right child dock is empty.
			// The left child dock becomes the single child dock.
			lastDividerLocation = splitPane.getDividerLocation();
			this.remove(splitPane);
			singleChildDock = leftChildDock;
			singleChildDock.setParentDock(this);
			rightChildDock = null;
			leftChildDock = null;
			splitPane = null;
			add((Component) singleChildDock, BorderLayout.CENTER);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));
		}

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public void ghostChild(Dock childDock) {

		// Do we have one single child dock?
		if ((singleChildDock != null) && (singleChildDock.equals(childDock))) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

			// The single child dock is made invisible and becomes a ghost.
			ghostSingleChild = singleChildDock;
			singleChildDock = null;
			((Component) ghostSingleChild).setVisible(false);

			// Inform the parent that we only have ghosts.
			if (parentDock != null) {
				parentDock.ghostChild(this);
			}
		} else if ((leftChildDock != null) && (leftChildDock.equals(childDock))) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

			// The split pane is made invisible and becomes a ghost.
			// The right child dock is removed from the split pane and becomes the single child dock.
			lastDividerLocation = splitPane.getDividerLocation();
			ghostSplitPane = splitPane;
			ghostSplitPane.remove((Component) rightChildDock);
			ghostSplitPane.setVisible(false);
			singleChildDock = rightChildDock;
			singleChildDock.setParentDock(this);
			rightChildDock = null;
			leftChildDock = null;
			splitPane = null;
			add((Component) singleChildDock, BorderLayout.CENTER);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, childDock));

		} else if ((rightChildDock != null) && (rightChildDock.equals(childDock))) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

			// The split pane is made invisible and becomes a ghost.
			// The left child dock is removed from the split pane and becomes the single child dock.
			lastDividerLocation = splitPane.getDividerLocation();
			ghostSplitPane = splitPane;
			ghostSplitPane.remove((Component) leftChildDock);
			ghostSplitPane.setVisible(false);
			singleChildDock = leftChildDock;
			singleChildDock.setParentDock(this);
			leftChildDock = null;
			rightChildDock = null;
			splitPane = null;
			add((Component) singleChildDock, BorderLayout.CENTER);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, childDock));

		}

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public void clearGhosts() {

		if (ghostSingleChild != null) {
			this.remove((Component) ghostSingleChild);
			Dock oldGhostChild = ghostSingleChild;
			ghostSingleChild = null;

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, oldGhostChild));

		}
		if (ghostSplitPane != null) {
			this.remove(ghostSplitPane);
			ghostSplitPane = null;
		}

		// Are we empty?
		if ((isEmpty()) && (getParentDock() != null)) {
			getParentDock().emptyChild(this);
		}

	}

	public void addChildDock(Dock dockToAdd, Position position) throws IllegalStateException {

		// Check if this dock is full.
		if (isFull()) {
			throw new IllegalStateException("This dock is full.");
		}

		if (isEmpty()) {
			// Inform the listeners about the add.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dockToAdd));

			setSingleChildDock(dockToAdd);

			// Inform the listeners about the add.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, dockToAdd));

		} else {
			//TODO take same things of addDockable together.

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, singleChildDock));

			// Remove everything.
			Dock oldSingleChildDock = singleChildDock;
			singleChildDock = null;
			this.remove((Component) oldSingleChildDock);

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, oldSingleChildDock));

			// Get the preferred size of the current child dock. We need it later.
			Dimension currentChildDockPreferredSize = ((Component) oldSingleChildDock).getPreferredSize();

			// Get the position for the new dock.
			int splitPosition = Position.RIGHT;
			if (position.getDimensions() == 1) {
				int possiblePosition = position.getPosition(0);
				if ((possiblePosition == Position.LEFT) ||
						(possiblePosition == Position.RIGHT) ||
						(possiblePosition == Position.TOP) ||
						(possiblePosition == Position.BOTTOM)) {
					splitPosition = possiblePosition;
				}
			}

			// Create the new child split docks.
			SplitDock leftSplitDock = null;
			SplitDock rightSplitDock = null;

			// Add the dockable and child dock to this docks.
			try {
				switch (splitPosition) {
					case Position.LEFT:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.LEFT);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.RIGHT);
						rightSplitDock.setSingleChildDock(oldSingleChildDock);
						leftSplitDock.setSingleChildDock(dockToAdd);
						break;
					case Position.TOP:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.TOP);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.BOTTOM);
						rightSplitDock.setSingleChildDock(oldSingleChildDock);
						leftSplitDock.setSingleChildDock(dockToAdd);
						break;
					case Position.RIGHT:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.LEFT);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.RIGHT);
						leftSplitDock.setSingleChildDock(oldSingleChildDock);
						rightSplitDock.setSingleChildDock(dockToAdd);
						break;
					case Position.BOTTOM:
						leftSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.TOP);
						rightSplitDock = (SplitDock) compositeChildDockFactory.createDock(null, DockingMode.BOTTOM);
						leftSplitDock.setSingleChildDock(oldSingleChildDock);
						rightSplitDock.setSingleChildDock(dockToAdd);
						break;
				}
			} catch (ClassCastException exception) {
				System.out.println("The splitChildDockFactory should create a " +
						"com.javadocking.dock.SplitDock for the modes DockingMode.LEFT, DockingMode.RIGHT, DockingMode.TOP and DockingMode.BOTTOM.");
				exception.printStackTrace();
			}

			// Where has to be the divider.
			// Get the prefered size of the dockable.
			Dimension preferredSize = ((Component) dockToAdd).getPreferredSize();
			int dockWidth = this.getSize().width;
			int dockHeight = this.getSize().height;
			if ((dockWidth == 0) && (dockHeight == 0)) {
				dockWidth = lastWidth;
				dockHeight = lastHeight;
			}
			int dividerLocation = dockWidth / 2;
			if (splitPosition == Position.LEFT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, currentChildDockPreferredSize.width, dockWidth, splitPosition);
				dividerLocation = dockingWidth;
			} else if (splitPosition == Position.RIGHT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, currentChildDockPreferredSize.width, dockWidth, splitPosition);
				dividerLocation = dockWidth - dockingWidth;
			} else if (splitPosition == Position.TOP) {
				int dockingHeight = getChildDockWidth(preferredSize.height, currentChildDockPreferredSize.height, dockHeight, splitPosition);
				dividerLocation = dockingHeight;
			} else if (splitPosition == Position.BOTTOM) {
				int dockingHeight = getChildDockWidth(preferredSize.height, currentChildDockPreferredSize.height, dockHeight, splitPosition);
				dividerLocation = dockHeight - dockingHeight;
			}

			// How do we have to split the pane?
			int orientation = HORIZONTAL_SPLIT;
			if ((splitPosition == Position.TOP) || (splitPosition == Position.BOTTOM)) {
				orientation = VERTICAL_SPLIT;
			}

			// Create the split pane.
			setDocks(leftSplitDock, rightSplitDock, orientation, dividerLocation);
		}

		// Repaint.
		SwingUtil.repaintParent(this);
	}

	/**
	 * Gets the dock factory that creates the leaf child docks ({@link LeafDock}) for this dock.
	 *
	 * @return The dock factory that creates the leaf child docks for this dock.
	 */
	public DockFactory getChildDockFactory() {
		return childDockFactory;
	}

	// Getters / setters.

	/**
	 * Sets the dock factory that creates the leaf child docks ({@link LeafDock}) for this dock.
	 *
	 * @param childDockFactory The dock factory that creates the leaf child docks for this dock.
	 * @throws IllegalArgumentException When the child dock factory is null.
	 */
	public void setChildDockFactory(DockFactory childDockFactory) {

		if (childDockFactory == null) {
			throw new IllegalArgumentException("The leaf child dock factory cannot be null.");
		}
		this.childDockFactory = childDockFactory;

	}

	/**
	 * Determines if the last child dock that is empty, has to be removed.
	 *
	 * @return True when the last child dock that is empty, has to be removed.
	 */
	public boolean isRemoveLastEmptyChild() {
		return removeLastEmptyChild;
	}

	/**
	 * Sets if the last child dock that is empty, has to be removed.
	 *
	 * @param removeLasTemptyChild True when the last child dock that is empty, has to be removed.
	 */
	public void setRemoveLastEmptyChild(boolean removeLasTemptyChild) {
		this.removeLastEmptyChild = removeLasTemptyChild;
	}

	/**
	 * Gets the dock factory that creates the split child docks for this dock.
	 * This dock factory should create a <code>SplitDock</code> when it is used with the modes
	 * {@link DockingMode#LEFT}, {@link DockingMode#RIGHT}, {@link DockingMode#TOP} or {@link DockingMode#BOTTOM}.
	 *
	 * @return The dock factory that creates the split child docks for this dock.
	 */
	public CompositeDockFactory getCompositeChildDockFactory() {
		return compositeChildDockFactory;
	}

	/**
	 * Sets the dock factory that creates the split child docks for this dock.
	 *
	 * @param splitDockFactory The dock factory that creates the split child docks for this dock.
	 * @throws IllegalArgumentException When the child dock factory is null.
	 */
	public void setCompositeChildDockFactory(CompositeDockFactory splitDockFactory) {

		if (splitDockFactory == null) {
			throw new IllegalArgumentException("The split dock factory cannot be null.");
		}

		this.compositeChildDockFactory = splitDockFactory;

	}

	/**
	 * Gets the divider location of the split pane.
	 * If there is no split pane in the dock, 0 is returned.
	 *
	 * @return The divider location of the split pane.
	 */
	public int getDividerLocation() {
		if (splitPane != null) {
			return splitPane.getDividerLocation();
		}
		return 0;
	}

	// Public methods.

	/**
	 * Sets the divider location of the split pane, if there is a split pane in the dock.
	 *
	 * @param dividerLocation the divider location of the split pane.
	 */
	public void setDividerLocation(int dividerLocation) {
		if (splitPane != null) {
			splitPane.setDividerLocation(dividerLocation);
		}
	}

	/**
	 * Sets the given dock as single child dock of this dock.
	 * This may only be called when this dock is empty.
	 *
	 * @param dock The single child dock for this dock.
	 * @throws IllegalStateException If this dock is not empty.
	 */
	public void setSingleChildDock(Dock dock) {
		// Check if this dock is empty.
		if (!isEmpty()) {
			throw new IllegalStateException("This dock is not empty.");
		}

		// Add the child dock.
		this.singleChildDock = dock;
		singleChildDock.setParentDock(this);
		add((Component) singleChildDock, BorderLayout.CENTER);

		// Repaint.
		SwingUtil.repaintParent(this);
	}


	// Protected methods.

	/**
	 * Sets the given docks as child docks of this dock.
	 * This may only be called when this dock is empty.
	 *
	 * @param leftDock        The left or top child dock for this dock. Should not be null.
	 * @param rightDock       The right or bottom child dock for this dock. Should not be null.
	 * @param orientation     The orientation of this dock, this can be HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 * @param dividerLocation The location of the divider of the split panes.
	 * @throws IllegalStateException If this dock is not empty.
	 */
	private void setDocks(Dock leftDock, Dock rightDock, int orientation, int dividerLocation) {
		// Check if this dock is empty.
		if (!isEmpty()) {
			throw new IllegalStateException("This dock is not empty.");
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, leftDock));
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, rightDock));

		// Create the split pane with the child docks.
		leftChildDock = leftDock;
		leftChildDock.setParentDock(this);
		rightChildDock = rightDock;
		rightChildDock.setParentDock(this);
		splitPane = DockingManager.getComponentFactory().createJSplitPane();
		splitPane.setLeftComponent((Component) leftChildDock);
		splitPane.setRightComponent((Component) rightChildDock);
		splitPane.setOrientation(orientation);
		if (lastDividerLocation != 0) {
			splitPane.setDividerLocation(lastDividerLocation);
		} else {
			splitPane.setDividerLocation(dividerLocation);
		}
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, leftDock));
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, rightDock));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	/**
	 * <p>
	 * Gets the relative rectangle in this dock in which docking has priority. The given rectangle gets
	 * the size and position of this priority rectangle.
	 * </p>
	 * <p>
	 * For the positions {@link Position#LEFT}, {@link Position#RIGHT}, {@link Position#TOP}, and {@link Position#BOTTOM}
	 * the rectangle is at the correspondent border of this dock.
	 * </p>
	 *
	 * @param position The position of the priority rectangle. There are 5 possibilities:
	 *                 <ul>
	 *                 <li>{@link Position#LEFT}</li>
	 *                 <li>{@link Position#RIGHT}</li>
	 *                 <li>{@link Position#TOP} </li>
	 *                 <li>{@link Position#BOTTOM}</li>
	 *                 <li>{@link Position#CENTER}</li>
	 *                 </ul>
	 * @param    rectangle                    Gets the size and position of the calculated priority rectangle.
	 */
	protected void getPriorityRectangle(Rectangle rectangle, int position) {

		Dimension size = getSize();
		switch (position) {
			case Position.LEFT:
				rectangle.setBounds(0,
						0,
						(int) (((double) size.width) * leftPriorityRectangleRelativeWidth),
						size.height);
				break;
			case Position.RIGHT:
				rectangle.setBounds((int) (((double) size.width) * (1 - rightPriorityRectangleRelativeWidth)),
						0,
						(int) (((double) size.width) * rightPriorityRectangleRelativeWidth),
						size.height);
				break;
			case Position.TOP:
				rectangle.setBounds(0,
						0,
						size.width,
						(int) (((double) size.height) * topPriorityRectangleRelativeHeight));
				break;
			case Position.BOTTOM:
				rectangle.setBounds(0,
						(int) (((double) size.height) * (1 - bottomPriorityRectangleRelativeHeight)),
						size.width,
						(int) (((double) size.height) * bottomPriorityRectangleRelativeHeight));
				break;
			case Position.CENTER:
				rectangle.setBounds((int) (size.width * centerPriorityRectangleRelativeLeftOffset),
						(int) (size.height * centerPriorityRectangleRelativeTopOffset),
						(int) (size.width * (1 - centerPriorityRectangleRelativeLeftOffset - centerPriorityRectangleRelativeRightOffset)),
						(int) (size.height * (1 - centerPriorityRectangleRelativeTopOffset - centerPriorityRectangleRelativeBottomOffset)));
				break;
		}

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

		// If the dock is empty, the dockable will be docked in the center.
		if (isEmpty()) {
			getPriorityRectangle(priorityRectangle, Position.CENTER);
			return (priorityRectangle.contains(relativeLocation));
		}

		// Get the possible docking modes of the new dockable and of the existing dockable.
		int dockModes = dockable.getDockingModes();
		Dockable otherDockable = DockingUtil.createDockable(this);
		int otherDockModes = DockingMode.ALL;
		if (otherDockable != null) {
			otherDockModes = otherDockable.getDockingModes();
		}

		// Try left.
		if (((dockModes & DockingMode.LEFT) != 0) &&
				((otherDockModes & DockingMode.RIGHT) != 0)) {
			getPriorityRectangle(priorityRectangle, Position.LEFT);
			if (priorityRectangle.contains(relativeLocation)) {
				return true;
			}
		}

		// Try right.
		if (((dockModes & DockingMode.RIGHT) != 0) &&
				((otherDockModes & DockingMode.LEFT) != 0)) {
			getPriorityRectangle(priorityRectangle, Position.RIGHT);
			if (priorityRectangle.contains(relativeLocation)) {
				return true;
			}
		}

		// Try top.
		if (((dockModes & DockingMode.TOP) != 0) &&
				((otherDockModes & DockingMode.BOTTOM) != 0)) {
			getPriorityRectangle(priorityRectangle, Position.TOP);
			if (priorityRectangle.contains(relativeLocation)) {
				return true;
			}
		}

		// Try bottom.
		if (((dockModes & DockingMode.BOTTOM) != 0) &&
				((otherDockModes & DockingMode.TOP) != 0)) {
			getPriorityRectangle(priorityRectangle, Position.BOTTOM);
			if (priorityRectangle.contains(relativeLocation)) {
				return true;
			}
		}

		// We can't dock with priority.
		return false;

	}

	/**
	 * <p>
	 * Gets the position where the dockable should be docked in the dock given the mouse position.
	 * </p>
	 * <p>
	 * There are 5 possible return values:
	 * <ul>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#CENTER}</li>
	 * </ul>
	 *
	 * @param relativeLocation The mouse location, where the dockable will be added.
	 * @return The position where the dockable should be docked in the dock.
	 */
	protected int getDockPosition(Point relativeLocation, Dockable newDockable) {

		// When the dock is empty, the dockable will be docked in the center.
		if (isEmpty()) {
			return Position.CENTER;
		}

		// Get the possible docking modes of the new dockable and of the existing dockable.
		int dockModes = newDockable.getDockingModes();
		Dockable otherDockable = DockingUtil.createDockable(this);
		int otherDockModes = DockingMode.ALL;
		if (otherDockable != null) {
			otherDockModes = otherDockable.getDockingModes();
		}

		// Get the size of this dock.
		Dimension dimension = this.getSize();

		// Take the closest border as position.
		int halfWidth = dimension.width / 2;
		int halfHeight = dimension.height / 2;
		if (relativeLocation.x < halfWidth) {
			if (relativeLocation.y < halfHeight) {
				if (relativeLocation.x < relativeLocation.y) {
					if (((dockModes & DockingMode.LEFT) != 0) &&
							((otherDockModes & DockingMode.RIGHT) != 0)) {
						return Position.LEFT;
					}
				} else {
					if (((dockModes & DockingMode.TOP) != 0) &&
							((otherDockModes & DockingMode.BOTTOM) != 0)) {
						return Position.TOP;
					}
				}
			} else {
				if (relativeLocation.x < (dimension.height - relativeLocation.y)) {
					if (((dockModes & DockingMode.LEFT) != 0) &&
							((otherDockModes & DockingMode.RIGHT) != 0)) {

						return Position.LEFT;
					}
				} else {
					if (((dockModes & DockingMode.LEFT) != 0) &&
							((otherDockModes & DockingMode.RIGHT) != 0)) {
						return Position.LEFT;
					}
				}
			}

		} else {
			if (relativeLocation.y < halfHeight) {
				if ((dimension.width - relativeLocation.x) < relativeLocation.y) {
					if (((dockModes & DockingMode.RIGHT) != 0) &&
							((otherDockModes & DockingMode.LEFT) != 0)) {

						return Position.RIGHT;
					}
				} else {
					if (((dockModes & DockingMode.TOP) != 0) &&
							((otherDockModes & DockingMode.BOTTOM) != 0)) {

						return Position.TOP;
					}
				}
			} else {
				if ((dimension.width - relativeLocation.x) < (dimension.height - relativeLocation.y)) {
					if (((dockModes & DockingMode.RIGHT) != 0) &&
							((otherDockModes & DockingMode.LEFT) != 0)) {

						return Position.RIGHT;
					}
				} else {
					if (((dockModes & DockingMode.BOTTOM) != 0) &&
							((otherDockModes & DockingMode.TOP) != 0)) {

						return Position.BOTTOM;
					}
				}
			}
		}

		// This should not occur. We should always find a position before.
		if ((dockModes & DockingMode.RIGHT) != 0) {
			return Position.RIGHT;
		} else if ((dockModes & DockingMode.LEFT) != 0) {
			return Position.LEFT;
		} else if ((dockModes & DockingMode.BOTTOM) != 0) {
			return Position.BOTTOM;
		} else if ((dockModes & DockingMode.TOP) != 0) {
			return Position.TOP;
		}
		return Position.RIGHT;

	}

	// Private methods.

	/**
	 * Computes the width for a component that will be added. There is already one component.
	 * The total width has to be shared by the 2 components.
	 *
	 * @param preferredWidth         The preferred width or height of the component that will be added.
	 * @param existingPreferredWidth The preferred width or height of the existing component.
	 * @param totalWidth             The total width or height that is available.
	 * @param position               The position where the new component will come. This can be Position.LEFT,Position.RIGHT,
	 *                               Position.TOP or Position.BOTTOM.
	 * @return The new width for the component that will be added.
	 */
	protected int getChildDockWidth(int preferredWidth, int existingPreferredWidth, int totalWidth, int position) {

		if ((preferredWidth + existingPreferredWidth) == 0) {
			return 0;
		}

		return preferredWidth * totalWidth / (preferredWidth + existingPreferredWidth);

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

//	private class ResizeListener implements ComponentListener
//	{
//
//		public void componentHidden(ComponentEvent componentEvent)
//		{
//			// Do nothing.
//		}
//
//		public void componentMoved(ComponentEvent componentEvent)
//		{
//			// Do nothing.
//		}
//
//		public void componentResized(ComponentEvent componentEvent)
//		{
//			
//			int total = 0;
//			if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
//			{
//				total = splitPane.getSize().width;
//			}
//			else if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT)
//			{
//				total = splitPane.getSize().height;
//			}
//			if (total > 0)
//			{
//				if (dividerWeight >= 0)
//				{
//					//float dividerLocation = total * dividerWeight;
//					splitPane.setDividerLocation(dividerWeight);
//					System.out.println("new divider location " + (total * dividerWeight));
//				}
//			}
//		}
//
//		public void componentShown(ComponentEvent componentEvent)
//		{
//			// Do nothing.
//		}
//		
//	}
//	
//	private class DividerPropertyChangeListener implements PropertyChangeListener
//	{
//		public void propertyChange( PropertyChangeEvent evt )
//        {
//			
//			int dividerLocation = ((Integer)evt.getNewValue()).intValue();
//			int total = 0;
//			if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
//			{
//				total = splitPane.getSize().width;
//			}
//			else if (splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT)
//			{
//				total = splitPane.getSize().height;
//			}
//			if (total != 0)
//			{
//				dividerWeight = ((float)dividerLocation) / ((float)total);
//			}
//			else
//			{
//				dividerWeight = -1;
//			}
//			System.out.println("new divider position: dividerWeight " + dividerWeight);
//        }
//	}

}
