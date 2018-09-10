package com.javadocking.dock;

import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
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
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This is a composite dock that can have one child dock in the center and between zero and 4 child docks
 * at the borders. At every border there is zero or one child dock.
 * This dock can not contain dockables. When dockables are added, child docks are created
 * and the dockables are added to the child docks.
 * </p>
 * <p>
 * Information on using border docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * The positions for child docks of this dock are one-dimensional.
 * The possible child docks and their first position value are:
 * <ul>
 * <li>The child dock in the center: {@link Position#CENTER}.</li>
 * <li>The child dock in the north: {@link Position#TOP}.</li>
 * <li>The child dock in the south: {@link Position#BOTTOM}.</li>
 * <li>The child dock in the east: {@link Position#RIGHT}.</li>
 * <li>The child dock in the west: {@link Position#LEFT}.</li>
 * </ul>
 *
 * <p>
 * A dockable can only be added if it has as one of its docking modes:
 * <ul>
 * <li><code>topDockingMode</code></li>
 * <li><code>bottomDockingMode</code></li>
 * <li><code>leftDockingMode</code></li>
 * <li><code>rightDockingMode</code></li>
 * </ul>
 * It can only be added in a position that corresponds with one of its docking modes.
 *
 * <p>
 * When a dockable is added, a child dock is created with the 'childDockFactory'. The dockable is added to
 * the child dock.
 * </p>
 * <p>
 * In the center there can be a dock or there can be a java.awt.Component.
 * </p>
 * <p>
 * The dock in the center will never be removed, even if it is empty. This kind of dock is never empty,
 * because it contains a component in the center or a child dock in the center.
 * When it contains 5 child docks it is full. When it contains 4 child docks at the borders and a component
 * in the center it is also full.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class BorderDock extends JPanel implements CompositeDock {

	// Static fields.

	/**
	 * When the border dock is set to this mode, the docking mode for dockables
	 * in child docks should have one of the folowing values:
	 * <ul>
	 * <li>for {@link Position#TOP}: {@link DockingMode#TOP},</li>
	 * <li>for {@link Position#BOTTOM}: {@link DockingMode#BOTTOM},</li>
	 * <li>for {@link Position#LEFT}: {@link DockingMode#LEFT}, and</li>
	 * <li>for {@link Position#RIGHT}: {@link DockingMode#RIGHT}.</li>
	 * </ul>
	 */
	public static final int MODE_GENERAL = 0;
	/**
	 * When the border dock is set to this mode, the docking mode for dockables
	 * in child docks should have one of the folowing values:
	 * <ul>
	 * <li>for {@link Position#TOP}: {@link DockingMode#HORIZONTAL_TOOLBAR},</li>
	 * <li>for {@link Position#BOTTOM}: {@link DockingMode#HORIZONTAL_TOOLBAR},</li>
	 * <li>for {@link Position#LEFT}: {@link DockingMode#VERTICAL_TOOLBAR}, and</li>
	 * <li>for {@link Position#RIGHT}: {@link DockingMode#VERTICAL_TOOLBAR}.</li>
	 * </ul>
	 */
	public static final int MODE_TOOL_BAR = 1;
	/**
	 * When the border dock is set to this mode, the docking mode for dockables
	 * in child docks should have one of the folowing values:
	 * <ul>
	 * <li>for {@link Position#TOP}: {@link DockingMode#HORIZONTAL_MINIMIZE},</li>
	 * <li>for {@link Position#BOTTOM}: {@link DockingMode#HORIZONTAL_MINIMIZE},</li>
	 * <li>for {@link Position#LEFT}: {@link DockingMode#VERTICAL_MINIMIZE}, and</li>
	 * <li>for {@link Position#RIGHT}: {@link DockingMode#VERTICAL_MINIMIZE}.</li>
	 * </ul>
	 */
	public static final int MODE_MINIMIZE_BAR = 2;

	/**
	 * The relative width of the left rectangle in which the mouse should be for docking a dockable at the left side.
	 */
	private static final double leftDockingRectangleRelativeWidth = 2.0 / 8.0;
	/**
	 * The relative width of the right rectangle in which the mouse should be for docking a dockable at the right side.
	 */
	private static final double rightDockingRectangleRelativeWidth = 2.0 / 8.0;
	/**
	 * The relative height of the top rectangle in which the mouse should be for docking a dockable at the top.
	 */
	private static final double topDockingRectangleRelativeHeight = 2.0 / 8.0;
	/**
	 * The relative height of the bottom rectangle in which the mouse should be for docking a dockable at the bottom.
	 */
	private static final double bottomDockingRectangleRelativeHeight = 2.0 / 8.0;


	/**
	 * The relative width of the left priority rectangle when there is already one child dock.
	 */
	private static final double leftPriorityRectangleRelativeWidth = 1.0 / 8.0;
	/**
	 * The relative width of the right priority rectangle when there is already one child dock.
	 */
	private static final double rightPriorityRectangleRelativeWidth = 1.0 / 8.0;
	/**
	 * The relative height of the top priority rectangle when there is already one child dock.
	 */
	private static final double topPriorityRectangleRelativeHeight = 1.0 / 8.0;
	/**
	 * The relative height of the bottom priority rectangle when there is already one child dock.
	 */
	private static final double bottomPriorityRectangleRelativeHeight = 1.0 / 8.0;


	// Fields.

	/**
	 * The parent dock of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * The docking mode for a dockable that is docked at the top child dock.
	 */
	private int topDockingMode = DockingMode.TOP;
	/**
	 * The docking mode for a dockable that is docked at the bottom child dock.
	 */
	private int bottomDockingMode = DockingMode.BOTTOM;
	/**
	 * The docking mode for a dockable that is docked at the left child dock.
	 */
	private int leftDockingMode = DockingMode.LEFT;
	/**
	 * The docking mode for a dockable that is docked at the right child dock.
	 */
	private int rightDockingMode = DockingMode.RIGHT;
	/**
	 * The component in the center of the dock. This is null, when there is a center child dock.
	 */
	private Component centerComponent;
	/**
	 * The child dock in the center of the dock. This is null, when there is a center component.
	 */
	private Dock centerChildDock;
	/**
	 * The child dock at the left side of the dock. This may be null.
	 */
	private Dock leftChildDock;
	/**
	 * The child dock at the right side of the dock. This may be null.
	 */
	private Dock rightChildDock;
	/**
	 * The child dock at the top of the dock. This may be null.
	 */
	private Dock topChildDock;
	/**
	 * The child dock at the bottom of the dock. This may be null.
	 */
	private Dock bottomChildDock;
	/**
	 * This factory creates the child docks at the borders of this dock.
	 */
	private DockFactory childDockFactory;
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();
	/**
	 * This is the rectangle in which a dockable can be docked with priority. We keep it as field
	 * because we don't want to create every time a new rectangle.
	 */
	private Rectangle priorityRectangle = new Rectangle();

	// Ghosts.
	/**
	 * This is a dock that has to be removed later. It is already made invisible.
	 * It cannot be removed now because there are still listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private Dock ghostChild;

	// Constructors.

	/**
	 * Constructs an abstract border dock with a {@link LeafDockFactory}
	 * as factory for the child docks.
	 */
	public BorderDock() {
		this(new LeafDockFactory());
	}

	/**
	 * Constructs a border dock with a given child dock for in the center
	 * and a {@link LeafDockFactory} as factory for the child docks.
	 *
	 * @param centerChildDock The child dock that will be put in the center.
	 */
	public BorderDock(Dock centerChildDock) {
		this(new LeafDockFactory(), centerChildDock);
	}

	/**
	 * Constructs a border dock with the given factory for creating the child docks at the borders.
	 *
	 * @param childDockFactory The factory for creating the child docks at the borders.
	 */
	public BorderDock(DockFactory childDockFactory) {

		// Set the layout.
		super(new BorderLayout());

		// Set the factory.
		if (childDockFactory == null) {
			throw new IllegalArgumentException("The child dock factory cannot be null.");
		}
		this.childDockFactory = childDockFactory;

	}

	/**
	 * Constructs a border dock with the given factory for creating the child docks at the borders
	 * and a given child dock for in the center.
	 *
	 * @param childDockFactory The factory for creating the child docks at the borders.
	 * @param centerChildDock  The child dock that will be put in the center.
	 */
	public BorderDock(DockFactory childDockFactory, Dock centerChildDock) {

		// Set the layout.
		super(new BorderLayout());

		// Set the factory.
		if (childDockFactory == null) {
			throw new IllegalArgumentException("The child dock factory cannot be null.");
		}
		this.childDockFactory = childDockFactory;

		// Set the child dock in the center.
		this.addChildDock(centerChildDock, new Position(Position.CENTER));

	}

	// Implementations of Dock.

	/**
	 * <p>
	 * Determines if the dockable can be added.
	 * </p>
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
	 * When there are already 5 child docks, the dockable cannot be added anymore.
	 * </p>
	 * <p>
	 * We only can dock if the dock factory can create a child dock for the given dockable.
	 * </p>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a border dock.
		if (!checkDockingModes(dockable)) {
			return Priority.CANNOT_DOCK;
		}

		// We can only dock if we are not full. 
		if (isFull()) {
			// Sorry, we are full.
			return Priority.CANNOT_DOCK;
		}

		// Get the positon and docking mode.
		int dockingModes = dockable.getDockingModes();
		int dockPosition = getDockPosition(relativeLocation);
		int dockingMode = getDockingMode(dockPosition);
		if (((dockingModes & dockingMode) != 0) && (isFree(dockPosition))) {
			// We can only dock if the dock factory can create a dock.
			if (getChildDockFactory().createDock(dockable, dockingMode) != null) {
				// Can we dock with priority?
				if (canAddDockableWithPriority(dockable, relativeLocation)) {
					return Priority.CAN_DOCK_WITH_PRIORITY;
				}

				// We can dock, but not with priority.
				return Priority.CAN_DOCK;

			}
		}

		return Priority.CANNOT_DOCK;

	}

	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation,
										Point dockableOffset, Rectangle rectangle) {

		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {

			// Get the position for the new dockable.
			int position = getDockPosition(relativeLocation);
			int dockingMode = getDockingMode(position);

			// Get the preferred size of the dockable.
			Dimension preferredSize = childDockFactory.getDockPreferredSize(dockable, dockingMode);
			Dimension centerSize = new Dimension(0, 0);
			Point centerLocation = new Point();
			if (centerChildDock != null) {
				centerSize = ((Component) centerChildDock).getSize();
				centerLocation = ((Component) centerChildDock).getLocation();
			} else if (centerComponent != null) {
				centerSize = centerComponent.getSize();
				centerLocation = centerComponent.getLocation();
			}

			// Get the bounds.
			if (position == Position.LEFT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, getSize().width, position);
				int dockingHeight = centerSize.height;
				int dockingY = centerLocation.y;
				rectangle.setBounds(0, dockingY, dockingWidth, dockingHeight);
			} else if (position == Position.RIGHT) {
				int dockingWidth = getChildDockWidth(preferredSize.width, getSize().width, position);
				int dockingHeight = centerSize.height;
				int dockingY = centerLocation.y;
				rectangle.setBounds(getSize().width - dockingWidth, dockingY, dockingWidth, dockingHeight);
			} else if (position == Position.TOP) {
				int dockingHeight = getChildDockWidth(preferredSize.height, getSize().height, position);
				rectangle.setBounds(0, 0, getSize().width, dockingHeight);
			} else if (position == Position.BOTTOM) {
				int dockingHeight = getChildDockWidth(preferredSize.height, getSize().height, position);
				rectangle.setBounds(0, getSize().height - dockingHeight, getSize().width, dockingHeight);
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

		// Get the docking mode.
		int dockPosition = getDockPosition(relativeLocation);
		int dockingMode = getDockingMode(dockPosition);

		// Create the new child dock.
		Dock childDock = childDockFactory.createDock(dockableToAdd, dockingMode);
		childDock.setParentDock(this);
		childDock.addDockable(dockableToAdd, new Point(0, 0), new Point(0, 0));

		// Get the position for the new dockable.
		int position = getDockPosition(relativeLocation);

		// Add the dock.
		addChildDock(childDock, new Position(position));

		// Repaint.
		SwingUtil.repaintParent(this);

		return true;
	}

	/**
	 * This dock can never be empty. It should always have a child dock in the center.
	 *
	 * @return Always false.
	 */
	public boolean isEmpty() {
		return false;
	}

	public boolean isFull() {

		return ((centerChildDock != null) || (centerComponent != null)) &&
				(leftChildDock != null) &&
				(rightChildDock != null) &&
				(topChildDock != null) &&
				(bottomChildDock != null);

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

		// Save the position of the child docks.
		savePosition(centerChildDock, prefix, properties, childDockIds);
		savePosition(leftChildDock, prefix, properties, childDockIds);
		savePosition(rightChildDock, prefix, properties, childDockIds);
		savePosition(topChildDock, prefix, properties, childDockIds);
		savePosition(bottomChildDock, prefix, properties, childDockIds);

		// Save the docking modes.
		PropertiesUtil.setInteger(properties, prefix + "leftDockingMode", leftDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "rightDockingMode", rightDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "topDockingMode", topDockingMode);
		PropertiesUtil.setInteger(properties, prefix + "bottomDockingMode", bottomDockingMode);

	}

	public void loadProperties(String prefix, Properties properties, Map childDocks, Map dockablesMap, Window owner) throws IOException {

		// Set the docking modes.
		int leftDockingMode = DockingMode.LEFT;
		int rightDockingMode = DockingMode.RIGHT;
		int topDockingMode = DockingMode.TOP;
		int bottomDockingMode = DockingMode.BOTTOM;
		leftDockingMode = PropertiesUtil.getInteger(properties, prefix + "leftDockingMode", leftDockingMode);
		rightDockingMode = PropertiesUtil.getInteger(properties, prefix + "rightDockingMode", rightDockingMode);
		topDockingMode = PropertiesUtil.getInteger(properties, prefix + "topDockingMode", topDockingMode);
		bottomDockingMode = PropertiesUtil.getInteger(properties, prefix + "bottomDockingMode", bottomDockingMode);
		setLeftDockingMode(leftDockingMode);
		setRightDockingMode(rightDockingMode);
		setTopDockingMode(topDockingMode);
		setBottomDockingMode(bottomDockingMode);

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

		if (childDocks != null) {
			// Iterate over the childDocks.
			for (Object o : childDocks.keySet()) {
				// Get the child.
				String childDockId = (String) o;
				Dock childDock = (Dock) childDocks.get(childDockId);

				// Get the position of the child.
				Position position = null;
				position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockId + "." + Position.PROPERTY_POSITION, position);

				// Add the dock in the right position.
				addChildDock(childDock, position);
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

	/**
	 * <p>
	 * Sets the given dock as child dock of this dock in the given position.
	 * </p>
	 * <p>
	 * The valid positions are:
	 * <ul>
	 * <li>Position.TOP</li>
	 * <li>Position.BOTTOM</li>
	 * <li>Position.LEFT</li>
	 * <li>Position.RIGHT</li>
	 * <li>Position.CENTER</li>
	 * </ul>
	 *
	 * <p>
	 * If the position is not valid, the first free position of the dock is taken.
	 * </p>
	 *
	 * @param dock     The new child dock for this dock.
	 * @param position The new child dock position.
	 * @throws IllegalStateException If this dock is full.
	 */
	public void addChildDock(Dock dock, Position position) throws IllegalStateException {

		// Check if this dock is full.
		if (isFull()) {
			throw new IllegalStateException("This dock is full.");
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dock));

		int borderPosition = verifyPosition(position);

		// Try to add the dock in the given position.
		boolean added = false;
		if ((borderPosition == Position.CENTER) && (centerChildDock == null) && (centerComponent == null)) {
			this.centerChildDock = dock;
			centerChildDock.setParentDock(this);
			add((Component) centerChildDock, BorderLayout.CENTER);
			added = true;

		} else if ((borderPosition == Position.TOP) && (topChildDock == null)) {
			this.topChildDock = dock;
			topChildDock.setParentDock(this);
			add((Component) topChildDock, BorderLayout.NORTH);
			added = true;
		} else if ((borderPosition == Position.BOTTOM) && (bottomChildDock == null)) {
			this.bottomChildDock = dock;
			bottomChildDock.setParentDock(this);
			add((Component) bottomChildDock, BorderLayout.SOUTH);
			added = true;
		} else if ((borderPosition == Position.LEFT) && (leftChildDock == null)) {
			this.leftChildDock = dock;
			leftChildDock.setParentDock(this);
			add((Component) leftChildDock, BorderLayout.WEST);
			added = true;
		} else if ((borderPosition == Position.RIGHT) && (rightChildDock == null)) {
			this.rightChildDock = dock;
			rightChildDock.setParentDock(this);
			add((Component) rightChildDock, BorderLayout.EAST);
			added = true;
		}

		// Could we add the child dock in the givne position?
		if (!added) {
			// Add the dock in the first free position.
			if ((centerChildDock == null) && (centerComponent != null)) {
				this.centerChildDock = dock;
				centerChildDock.setParentDock(this);
				add((Component) centerChildDock, BorderLayout.CENTER);
				added = true;

			} else if (topChildDock == null) {
				this.topChildDock = dock;
				topChildDock.setParentDock(this);
				add((Component) topChildDock, BorderLayout.NORTH);
				added = true;
			} else if (bottomChildDock == null) {
				this.bottomChildDock = dock;
				bottomChildDock.setParentDock(this);
				add((Component) bottomChildDock, BorderLayout.SOUTH);
				added = true;
			} else if (leftChildDock == null) {
				this.leftChildDock = dock;
				leftChildDock.setParentDock(this);
				add((Component) leftChildDock, BorderLayout.WEST);
				added = true;
			} else if (rightChildDock == null) {
				this.rightChildDock = dock;
				rightChildDock.setParentDock(this);
				add((Component) rightChildDock, BorderLayout.EAST);
				added = true;
			}
		}

		// Normally the child dock should now be added.
		if (!added) {
			throw new IllegalStateException("The dock is not full, but all the positions are occupied.");
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, dock));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public int getChildDockCount() {

		int count = 0;
		if (centerChildDock != null) {
			count++;
		}
		if (leftChildDock != null) {
			count++;
		}
		if (rightChildDock != null) {
			count++;
		}
		if (topChildDock != null) {
			count++;
		}
		if (bottomChildDock != null) {
			count++;
		}
		return count;

	}

	public Dock getChildDock(int index) throws IndexOutOfBoundsException {

		int count = 0;
		if (centerChildDock != null) {
			if (index == count) {
				return centerChildDock;
			}
			count++;
		}
		if (leftChildDock != null) {
			if (index == count) {
				return leftChildDock;
			}
			count++;
		}
		if (rightChildDock != null) {
			if (index == count) {
				return rightChildDock;
			}
			count++;
		}
		if (topChildDock != null) {
			if (index == count) {
				return topChildDock;
			}
			count++;
		}
		if (bottomChildDock != null) {
			if (index == count) {
				return bottomChildDock;
			}
			count++;
		}

		throw new IndexOutOfBoundsException("Index " + index);

	}

	public Position getChildDockPosition(Dock childDock) throws IllegalArgumentException {

		if (childDock.equals(centerChildDock)) {
			return new Position(Position.CENTER);
		}
		if (childDock.equals(leftChildDock)) {
			return new Position(Position.LEFT);
		}
		if (childDock.equals(rightChildDock)) {
			return new Position(Position.RIGHT);
		}
		if (childDock.equals(topChildDock)) {
			return new Position(Position.TOP);
		}
		if (childDock.equals(bottomChildDock)) {
			return new Position(Position.BOTTOM);
		}

		throw new IllegalArgumentException("The dock is not docked in this composite dock.");

	}

	public void emptyChild(Dock emptyChildDock) {

		if ((centerChildDock != null) && (centerChildDock.equals(emptyChildDock))) {
			// We leave this dock, even if it is empty.
			return;
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, emptyChildDock));

		if ((leftChildDock != null) && (leftChildDock.equals(emptyChildDock))) {
			this.remove((Component) leftChildDock);
			leftChildDock = null;
		} else if ((rightChildDock != null) && (rightChildDock.equals(emptyChildDock))) {
			this.remove((Component) rightChildDock);
			rightChildDock = null;
		} else if ((topChildDock != null) && (topChildDock.equals(emptyChildDock))) {
			this.remove((Component) topChildDock);
			topChildDock = null;
		} else if ((bottomChildDock != null) && (bottomChildDock.equals(emptyChildDock))) {
			this.remove((Component) bottomChildDock);
			bottomChildDock = null;
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, emptyChildDock));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public void ghostChild(Dock childDock) {

		if ((centerChildDock != null) && (centerChildDock.equals(childDock))) {
			// We leave this dock, even if it is empty.
			return;
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

		if ((leftChildDock != null) && (leftChildDock.equals(childDock))) {
			ghostChild = leftChildDock;
			((Component) ghostChild).setVisible(false);
			leftChildDock = null;
		} else if ((rightChildDock != null) && (rightChildDock.equals(childDock))) {
			ghostChild = rightChildDock;
			((Component) ghostChild).setVisible(false);
			rightChildDock = null;
		} else if ((topChildDock != null) && (topChildDock.equals(childDock))) {
			ghostChild = topChildDock;
			((Component) ghostChild).setVisible(false);
			topChildDock = null;
		} else if ((bottomChildDock != null) && (bottomChildDock.equals(childDock))) {
			ghostChild = bottomChildDock;
			((Component) ghostChild).setVisible(false);
			bottomChildDock = null;
		}

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	public void clearGhosts() {

		// Do we have a ghost?
		if (ghostChild != null) {
			// Remove it.
			this.remove((Component) ghostChild);
			Dock oldGhostChild = ghostChild;
			ghostChild = null;

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, oldGhostChild));
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

	// Protected methods.

	/**
	 * Gets the child dock in the given position.
	 *
	 * @return Child doc at given position
	 * @param    position    This is an integer defined by the class {@link Position}.
	 * The possible values are:
	 * <ul>
	 * <li>{@link Position#CENTER}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * </ul>
	 */
	public Dock getChildDockOfPosition(int position) {

		if (position == Position.CENTER) {
			return centerChildDock;
		} else if (position == Position.BOTTOM) {
			return bottomChildDock;
		} else if (position == Position.TOP) {
			return topChildDock;
		} else if (position == Position.LEFT) {
			return leftChildDock;
		} else if (position == Position.RIGHT) {
			return rightChildDock;
		}
		throw new IllegalArgumentException("Illegal position.");

	}

	/**
	 * <p>
	 * Gets the position where the dockable should be docked in the dock given the mouse position.
	 * </p>
	 * <p>
	 * There are 4 possible return values:
	 * <ul>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * </ul>
	 *
	 * @param relativePosition The mouse location, where the dockable will be added.
	 * @return The position where the dockable should be docked in the dock.
	 */
	protected int getDockPosition(Point relativePosition) {

		// Get the size of this dock.
		Dimension dimension = this.getSize();

		// Take the closest border as position.
		int halfWidth = dimension.width / 2;
		int halfHeight = dimension.height / 2;
		if (relativePosition.x < halfWidth) {
			if (relativePosition.y < halfHeight) {
				if (relativePosition.x < relativePosition.y) {
					return Position.LEFT;
				} else {
					return Position.TOP;
				}
			} else {
				if (relativePosition.x < (dimension.height - relativePosition.y)) {
					return Position.LEFT;
				} else {
					return Position.BOTTOM;
				}
			}
		} else {
			if (relativePosition.y < halfHeight) {
				if ((dimension.width - relativePosition.x) < relativePosition.y) {
					return Position.RIGHT;
				} else {
					return Position.TOP;
				}
			} else {
				if ((dimension.width - relativePosition.x) < (dimension.height - relativePosition.y)) {
					return Position.RIGHT;
				} else {
					return Position.BOTTOM;
				}
			}
		}

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
	 * @param position The position of the priority rectangle. There are 4 possibilities:
	 *                 <ul>
	 *                 <li>{@link Position#LEFT}</li>
	 *                 <li>{@link Position#RIGHT}</li>
	 *                 <li>{@link Position#TOP} </li>
	 *                 <li>{@link Position#BOTTOM}</li>
	 *                 </ul>
	 * @param    rectangle                    Gets the size and position of the calculated priority rectangle.
	 */
	protected void getPriorityRectangle(Rectangle rectangle, int position) {

		Dimension size = getSize();
		switch (position) {
			case Position.LEFT:
				int y = 0;
				int height = 0;
				if (centerChildDock != null) {
					y = ((Component) centerChildDock).getLocation().y;
					height = ((Component) centerChildDock).getHeight();
				} else {
					y = centerComponent.getLocation().y;
					height = centerComponent.getHeight();
				}
				rectangle.setBounds(0,
						y,
						(int) (((double) size.width) * leftPriorityRectangleRelativeWidth),
						height);
				break;
			case Position.RIGHT:
				y = 0;
				height = 0;
				if (centerChildDock != null) {
					y = ((Component) centerChildDock).getLocation().y;
					height = ((Component) centerChildDock).getHeight();
				} else {
					y = centerComponent.getLocation().y;
					height = centerComponent.getHeight();
				}
				rectangle.setBounds((int) (((double) size.width) * (1 - rightPriorityRectangleRelativeWidth)),
						y,
						(int) (((double) size.width) * rightPriorityRectangleRelativeWidth),
						height);
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
		}

	}

	/**
	 * <p>
	 * Computes the relative rectangle in this dock in which we can dock for a given position.  The given rectangle gets
	 * the size and position of this priority rectangle.
	 * </p>
	 * <p>
	 * For the positions {@link Position#LEFT}, {@link Position#RIGHT}, {@link Position#TOP}, and {@link Position#BOTTOM}
	 * the rectangle is at the correspondent border of this dock.
	 * </p>
	 *
	 * @param position The position of the rectangle. There are 4 possibilities:
	 *                 <ul>
	 *                 <li>{@link Position#LEFT}</li>
	 *                 <li>{@link Position#RIGHT}</li>
	 *                 <li>{@link Position#TOP} </li>
	 *                 <li>{@link Position#BOTTOM}</li>
	 *                 </ul>
	 * @param    rectangle                    Gets the size and position of the calculated priority rectangle.
	 */
	protected void getDockingRectangle(Rectangle rectangle, int position) {

		Dimension size = getSize();
		switch (position) {
			case Position.LEFT:
				rectangle.setBounds(0,
						0,
						(int) (((double) size.width) * leftDockingRectangleRelativeWidth),
						size.height);
				break;
			case Position.RIGHT:
				rectangle.setBounds((int) (((double) size.width) * rightDockingRectangleRelativeWidth),
						0,
						(int) (((double) size.width) * (1 - rightDockingRectangleRelativeWidth)),
						size.height);
				break;
			case Position.TOP:
				rectangle.setBounds(0,
						0,
						size.width,
						(int) (((double) size.height) * topDockingRectangleRelativeHeight));
				break;
			case Position.BOTTOM:
				rectangle.setBounds(0,
						(int) (((double) size.height) * bottomDockingRectangleRelativeHeight),
						size.width,
						(int) (((double) size.height) * (1 - bottomDockingRectangleRelativeHeight)));
				break;
		}

	}

	// Private methods.

	/**
	 * Saves the position of the child dock in the given properties.
	 *
	 * @param    childDock                    The child dock whose position should be saved.
	 * @param    prefix                        The prefix for the property names.
	 * @param    properties                    The properties where the position should be saved.
	 * @param    childDockIds                The mapping between child docks and their IDs.
	 */
	private void savePosition(Dock childDock, String prefix, Properties properties, Map childDockIds) {

		if (childDock != null) {
			// Get the key of this child dock.
			String childDockKey = (String) childDockIds.get(childDock);

			// Save the position.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + childDockKey + "." + Position.PROPERTY_POSITION, getChildDockPosition(childDock));
		}

	}

	/**
	 * Verifies if the given position is valid for a border dock and verifies if there is not already
	 * a dock at this position. If there is already a dock at the given position, then an alternate position is returned.
	 * If the position is OK, then the original position is returned. The dock may not be full.
	 *
	 * @param position The position for a new child dock.
	 * @return If there is already a dock at the given position, then an alternate position is returned.
	 * If the position is OK, then the original position is returned.
	 * @throws IllegalStateException        If the dock is full.
	 */
	private int verifyPosition(Position position) {

		// Check if this dock is full.
		if (isFull()) {
			throw new IllegalStateException("This dock is full.");
		}

		// Check if the given position is a position for a border dock.
		int borderPosition = Position.RIGHT;
		if ((position != null) && (position.getDimensions() == 1)) {
			int possiblePosition = position.getPosition(0);
			if ((possiblePosition == Position.CENTER) ||
					(possiblePosition == Position.LEFT) ||
					(possiblePosition == Position.RIGHT) ||
					(possiblePosition == Position.TOP) ||
					(possiblePosition == Position.BOTTOM)) {
				borderPosition = possiblePosition;
			}
		}

		// Is this position free?
		if (isFree(borderPosition)) {
			return borderPosition;
		}

		// Find an alternate position.
		int[] alternatePositions = getAlternatePositions(borderPosition);
		for (final int alternatePosition : alternatePositions) {
			if (isFree(alternatePosition)) {
				return alternatePosition;
			}
		}

		throw new IllegalStateException("We should never come here.");


	}

	/**
	 * Verifies if there is not already a child dock at the given position.
	 *
	 * @param position The position for a new child dock.
	 * @throws IllegalArgumentException If the given position is not Position.CENTER,
	 *                                  Position.LEFT, Position.RIGHT, Position.TOP or Position.BOTTOM.
	 * @return True if there is not already a dock at the given position, false otherwise.
	 */
	protected boolean isFree(int position) {
		if (position == Position.CENTER) {
			return centerChildDock == null;
		}
		if (position == Position.LEFT) {
			return leftChildDock == null;
		}
		if (position == Position.RIGHT) {
			return rightChildDock == null;
		}
		if (position == Position.TOP) {
			return topChildDock == null;
		}
		if (position == Position.BOTTOM) {
			return bottomChildDock == null;
		}

		throw new IllegalArgumentException("Illegal position for a border dock [" + position + "].");
	}

	// Getters / Setters.

	/**
	 * Gets the component in the center of the dock.
	 *
	 * @return The component in the center of the dock.
	 */
	public Component getCenterComponent() {
		return centerComponent;
	}

	/**
	 * Sets the component in the center of the dock.
	 * If there was already
	 *
	 * @param centerComponent The component in the center of the dock.
	 * @throws IllegalStateException    When there is already a dock in the center.
	 * @throws IllegalStateException    When there is already a component in the center.
	 */
	public void setCenterComponent(Component centerComponent) {

		// Check if there is a center child dock.
		if (centerChildDock != null) {
			throw new IllegalStateException("There is already a dock in the center of the border dock.");
		}

		// Is there already a center component.
		if (this.centerComponent != null) {
			throw new IllegalStateException("There is already a component in the center of the border dock.");
		}

		// Set and add the new center component.
		this.centerComponent = centerComponent;
		this.add(centerComponent, BorderLayout.CENTER);

	}

	/**
	 * Removes the component in the center of this dock.
	 *
	 * @throws IllegalStateException    If there is no center component in this dock.
	 */
	public void removeCenterComponent() {

		// Is there a center component?
		if (this.centerComponent == null) {
			throw new IllegalStateException("There is no component in the center of the border dock.");
		}

		if (this.centerComponent != null) {
			remove(centerComponent);
			this.centerComponent = null;
		}

	}

	// Private metods.


	private int[] getAlternatePositions(int position) {
		if (position == Position.CENTER) {
			int[] positions = {Position.LEFT, Position.RIGHT, Position.TOP, Position.BOTTOM};
			return positions;
		}
		if (position == Position.LEFT) {
			int[] positions = {Position.RIGHT, Position.TOP, Position.BOTTOM, Position.CENTER};
			return positions;
		}
		if (position == Position.RIGHT) {
			int[] positions = {Position.LEFT, Position.TOP, Position.BOTTOM, Position.CENTER};
			return positions;
		}
		if (position == Position.TOP) {
			int[] positions = {Position.BOTTOM, Position.LEFT, Position.RIGHT, Position.CENTER};
			return positions;
		}
		if (position == Position.BOTTOM) {
			int[] positions = {Position.TOP, Position.LEFT, Position.RIGHT, Position.CENTER};
			return positions;
		}

		throw new IllegalArgumentException("Illegal position for a border dock [" + position + "].");
	}

	/**
	 * Gets the docking mode for a dockable that is docked at the bottom child dock.
	 *
	 * @return The docking mode for a dockable that is docked at the bottom child dock.
	 */
	public int getBottomDockingMode() {
		return bottomDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked at the bottom child dock.
	 *
	 * @param bottomDockingMode The docking mode for a dockable that is docked at the bottom child dock.
	 */
	public void setBottomDockingMode(int bottomDockingMode) {
		this.bottomDockingMode = bottomDockingMode;
	}

	/**
	 * Gets the docking mode for a dockable that is docked at the left child dock.
	 *
	 * @return The docking mode for a dockable that is docked at the left child dock.
	 */
	public int getLeftDockingMode() {
		return leftDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked at the left child dock.
	 *
	 * @param leftDockingMode The docking mode for a dockable that is docked at the left child dock.
	 */
	public void setLeftDockingMode(int leftDockingMode) {
		this.leftDockingMode = leftDockingMode;
	}

	/**
	 * Gets the docking mode for a dockable that is docked at the right child dock.
	 *
	 * @return The docking mode for a dockable that is docked at the right child dock.
	 */
	public int getRightDockingMode() {
		return rightDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked at the right child dock.
	 *
	 * @param rightDockingMode The docking mode for a dockable that is docked at the right child dock.
	 */
	public void setRightDockingMode(int rightDockingMode) {
		this.rightDockingMode = rightDockingMode;
	}

	/**
	 * Gets the docking mode for a dockable that is docked at the top child dock.
	 *
	 * @return The docking mode for a dockable that is docked at the top child dock.
	 */
	public int getTopDockingMode() {
		return topDockingMode;
	}

	/**
	 * Sets the docking mode for a dockable that is docked at the top child dock.
	 *
	 * @param topDockingMode The docking mode for a dockable that is docked at the top child dock.
	 */
	public void setTopDockingMode(int topDockingMode) {
		this.topDockingMode = topDockingMode;
	}

	/**
	 * Checks the docking modes of the dockable. True is returned if the dockable has
	 * <ul>
	 * <li><code>topDockingMode</code>,</li>
	 * <li><code>bottomDockingMode</code>,</li>
	 * <li><code>leftDockingMode</code>, or</li>
	 * <li><code>rightDockingMode</code></li>
	 * </ul>
	 * as possible docking mode.
	 *
	 * @return True is returned if the dockable has
	 * <ul>
	 * <li><code>topDockingMode</code>,</li>
	 * <li><code>bottomDockingMode</code>,</li>
	 * <li><code>leftDockingMode</code>, or</li>
	 * <li><code>rightDockingMode</code></li>
	 * </ul>
	 * as possible docking mode.
	 * @param    dockable    The dockable to add.
	 */
	private boolean checkDockingModes(Dockable dockable) {

		// Check if the dockable may be docked in a border dock.
		int dockPositions = dockable.getDockingModes();
		return ((dockPositions & topDockingMode) != 0) ||
				((dockPositions & rightDockingMode) != 0) ||
				((dockPositions & leftDockingMode) != 0) ||
				((dockPositions & bottomDockingMode) != 0);
	}

	/**
	 * Determines if the given dockable can be added to this dock with priority.
	 *
	 * @param dockable         The dockable that may be added to this dock.
	 * @param relativeLocation The location of the mouse relative to this dock.
	 * @return True if the given dockable can be added to this dock with priority,
	 * false otherwise.
	 */
	private boolean canAddDockableWithPriority(Dockable dockable, Point relativeLocation) {

		// Get the possible docking modes of the new dockable.
		int dockModes = dockable.getDockingModes();

		// Try left.
		if ((dockModes & leftDockingMode) != 0) {
			if (getChildDockOfPosition(Position.LEFT) == null) {
				getPriorityRectangle(priorityRectangle, Position.LEFT);
				if (priorityRectangle.contains(relativeLocation)) {
					return true;
				}
			}
		}

		// Try right.
		if ((dockModes & rightDockingMode) != 0) {
			if (getChildDockOfPosition(Position.RIGHT) == null) {
				getPriorityRectangle(priorityRectangle, Position.RIGHT);
				if (priorityRectangle.contains(relativeLocation)) {
					return true;
				}
			}
		}

		// Try top.
		if ((dockModes & topDockingMode) != 0) {
			if (getChildDockOfPosition(Position.TOP) == null) {
				getPriorityRectangle(priorityRectangle, Position.TOP);
				if (priorityRectangle.contains(relativeLocation)) {
					return true;
				}
			}
		}

		// Try bottom.
		if ((dockModes & bottomDockingMode) != 0) {
			if (getChildDockOfPosition(Position.BOTTOM) == null) {
				getPriorityRectangle(priorityRectangle, Position.BOTTOM);
				return priorityRectangle.contains(relativeLocation);
			}
		}

		// We can't dock with priority.
		return false;

	}

	/**
	 * Calculates the docking mode that corresponds with a given dock position in this dock.
	 *
	 * @param dockPosition The dock position of a dockable in this dock.
	 *                     This should be:
	 *                     <ul>
	 *                     <li>{@link Position#TOP}</li>
	 *                     <li>{@link Position#BOTTOM}</li>
	 *                     <li>{@link Position#LEFT}</li>
	 *                     <li>{@link Position#RIGHT}</li>
	 *                     <li>{@link Position#CENTER}</li>
	 *                     </ul>
	 * @throws IllegalArgumentException If the given position is not a valid position for this dock.
	 * @return The docking mode that corresponds with the given dock position.
	 * This will be:
	 * <ul>
	 * <li><code>topDockingMode</code>,</li>
	 * <li><code>bottomDockingMode</code>,</li>
	 * <li><code>leftDockingMode</code>, or</li>
	 * <li><code>rightDockingMode</code></li>
	 * <li>{@link DockingMode#CENTER}</li>
	 * </ul>
	 */
	public int getDockingMode(int dockPosition) {
		switch (dockPosition) {
			case Position.TOP:
				return topDockingMode;
			case Position.BOTTOM:
				return bottomDockingMode;
			case Position.LEFT:
				return leftDockingMode;
			case Position.RIGHT:
				return rightDockingMode;
			case Position.CENTER:
				return DockingMode.CENTER;
		}

		throw new IllegalArgumentException("Position [" + dockPosition + "] is not a valid position in a BorderDock.");
	}

	/**
	 * Computes the width for a component that will be added.
	 *
	 * @param preferredWidth The preferred width or height of the component that will be added.
	 * @param totalWidth     The total width or height that is available.
	 * @param position       The position where the new component will come. This can be {@link Position#LEFT},{@link Position#RIGHT},
	 *                       {@link Position#TOP}, or {@link Position#BOTTOM}.
	 * @return The new width for the component that will be added.
	 */
	protected int getChildDockWidth(int preferredWidth, int totalWidth, int position) {

		// Is the preferred width of the new component smaller than the half total width? 
		if (preferredWidth < totalWidth / 2) {
			return preferredWidth;
		}

		// They have to share the total width equally.
		return totalWidth / 2;

	}

	/**
	 * Sets the given dock as child dock of this dock in the given position.
	 * The positions can be:
	 * <ul>
	 * <li>{@link Position#TOP},</li>
	 * <li>{@link Position#BOTTOM},</li>
	 * <li>{@link Position#LEFT},</li>
	 * <li>{@link Position#RIGHT}, or</li>
	 * <li>{@link Position#CENTER}.</li>
	 * </ul>
	 * The orientation of the child tool bar dock is set also.
	 *
	 * @param dock The new child dock for this dock.
	 * @throws IllegalStateException If there is already a dock at this position.
	 * @throws IllegalStateException If the position is not valid.
	 */
	public void setDock(Dock dock, int position)//TODO try to remove
	{
		if (dock instanceof LineDock) {
			LineDock lineDock = (LineDock) dock;

			// What is the position?
			if (position == Position.TOP) {
				lineDock.setOrientation(LineDock.ORIENTATION_HORIZONTAL);
			} else if (position == Position.BOTTOM) {
				lineDock.setOrientation(LineDock.ORIENTATION_HORIZONTAL);
			} else if (position == Position.LEFT) {
				lineDock.setOrientation(LineDock.ORIENTATION_VERTICAL);
			} else if (position == Position.RIGHT) {
				lineDock.setOrientation(LineDock.ORIENTATION_VERTICAL);
			}
		}

		addChildDock(dock, new Position(position));

	}

	/**
	 * Defines the docking modes for the dockables that will be added to this dock.
	 *
	 * @param mode Defines the docking modes for the dockables that will be added to this dock.
	 *             Possible values are:
	 *             <ul>
	 *             <li>{@link #MODE_GENERAL},</li>
	 *             <li>{@link #MODE_TOOL_BAR}, or</li>
	 *             <li>{@link #MODE_MINIMIZE_BAR}.</li>
	 *             </ul>
	 */
	public void setMode(int mode) {

		switch (mode) {
			case MODE_GENERAL:
				setTopDockingMode(DockingMode.TOP);
				setBottomDockingMode(DockingMode.BOTTOM);
				setLeftDockingMode(DockingMode.LEFT);
				setRightDockingMode(DockingMode.RIGHT);
				break;
			case MODE_TOOL_BAR:
				setTopDockingMode(DockingMode.HORIZONTAL_TOOLBAR);
				setBottomDockingMode(DockingMode.HORIZONTAL_TOOLBAR);
				setLeftDockingMode(DockingMode.VERTICAL_TOOLBAR);
				setRightDockingMode(DockingMode.VERTICAL_TOOLBAR);
				break;
			case MODE_MINIMIZE_BAR:
				setTopDockingMode(DockingMode.HORIZONTAL_MINIMIZE);
				setBottomDockingMode(DockingMode.HORIZONTAL_MINIMIZE);
				setLeftDockingMode(DockingMode.VERTICAL_MINIMIZE);
				setRightDockingMode(DockingMode.VERTICAL_MINIMIZE);
				break;
			default:
				throw new IllegalArgumentException("Unknown mode.");
		}
	}

}
