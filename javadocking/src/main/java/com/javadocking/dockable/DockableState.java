package com.javadocking.dockable;


/**
 * This class defines the constants for the possible states of a dockable, e.g. closed, normal, maximized and minimized.
 * These constants are integers that can be combined with the bitwise or-operation.
 *
 * @author Heidi Rakels.
 */
public class DockableState {

	// Static fields.

	/**
	 * This is the integer for the state when the dockable is in a normal dock.
	 */
	public static final int NORMAL = 1;
	/**
	 * This is the integer for the state when the dockable is not in a dock.
	 */
	public static final int CLOSED = 1 << 1;
	/**
	 * This is the integer for the state when the dockable is maximized.
	 */
	public static final int MAXIMIZED = 1 << 2;
	/**
	 * This is the integer for the state when the dockable is minimized.
	 */
	public static final int MINIMIZED = 1 << 3;
	/**
	 * This is the integer for the state when the dockable is externalized.
	 */
	public static final int EXTERNALIZED = 1 << 4;
	/**
	 * This is the integer that combines all the possible states for a dockable.
	 */
	public static final int ALL_STATES = NORMAL | CLOSED | MAXIMIZED | MINIMIZED | EXTERNALIZED;

	/**
	 * An array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED, DockableState.MAXIMIZED, DockableState.NORMAL, and DockableState.CLOSED.
	 */
	private static final int[] STATES_ALL = {EXTERNALIZED, MINIMIZED, MAXIMIZED, NORMAL, CLOSED};
	/**
	 * An array with the constant DockableState.CLOSED.
	 */
	private static final int[] STATES_CLOSED = {CLOSED};
	/**
	 * An array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED, DockableState.MAXIMIZED, and DockableState.NORMAL.
	 */
	private static final int[] STATES_ALL_EXCEPT_CLOSED = {EXTERNALIZED, MINIMIZED, MAXIMIZED, NORMAL};

	// Constructors.

	private DockableState() {
	}

	// Public static methods.

	/**
	 * Gets an array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED,
	 * DockableState.MAXIMIZED, DockableState.NORMAL, and DockableState.CLOSED.
	 *
	 * @return An array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED,
	 * DockableState.MAXIMIZED, DockableState.NORMAL, and DockableState.CLOSED.
	 */
	public static int[] statesAll() {
		return STATES_ALL.clone();
	}

	/**
	 * Gets an array with the constant DockableState.CLOSED.
	 *
	 * @return An array with the constant DockableState.CLOSED.
	 */
	public static int[] statesClosed() {
		return STATES_CLOSED.clone();
	}

	/**
	 * Gets an array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED, DockableState.MAXIMIZED, and DockableState.NORMAL.
	 *
	 * @return An array with the constants DockableState.EXTERNALIZED, DockableState.MINIMIZED, DockableState.MAXIMIZED, and DockableState.NORMAL.
	 */
	public static int[] statesAllExceptClosed() {
		return STATES_ALL_EXCEPT_CLOSED.clone();
	}

}
