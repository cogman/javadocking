package com.javadocking.dock;

/**
 * <p>
 * This class defines the integer constants for the priority for adding
 * a dockable to a specific dock.
 * </p>
 * <p>
 * Depending on the location of the mouse above the dock, when dragging a dockable,
 * a dockable can be docked in different ways in the dock.
 * Every location has a priority for docking a dockable.
 * If a dockable cannot be docked at all, the priority is CANNOT_DOCK. Other possible priority values
 * with ascending priority are CAN_DOCK_AS_LAST, CAN_DOCK, CAN_DOCK_WITH_PRIORITY and CAN_DOCK_WITH_HIGH_PRIORITY.
 * </p>
 * <p>
 * It is possible that a dockable can be docked in multiple docks for the same screen location. In that case the dock
 * that has the highest priority, will be chosen.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class Priority {

	// Static fields.

	/**
	 * A dockable cannot be docked.
	 */
	public static final int CANNOT_DOCK = 0;
	/**
	 * A dockable can be docked, but only if there are no other possiblities.
	 */
	public static final int CAN_DOCK_AS_LAST = 1;
	/**
	 * A dockable can be docked.
	 */
	public static final int CAN_DOCK = 2;
	/**
	 * A dockable can be docked with priority. With priority means that it is a good position.
	 * If the dock can also be docked in other docks, but not with priority, it will be added here.
	 */
	public static final int CAN_DOCK_WITH_PRIORITY = 3;
	/**
	 * A dockable can be docked with high priority. With high priority means that it is the best position
	 * for this dockable.
	 */
	public static final int CAN_DOCK_WITH_HIGH_PRIORITY = 4;

	// Private constructor.

	private Priority() {
	}

}
