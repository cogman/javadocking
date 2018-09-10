package com.javadocking.dockable;

/**
 * <p>
 * This class defines the constants for the possible modes of docking a dockable.
 * These constants are integers that can be combined with the bitwise or-operation.
 * </p>
 * <p>
 * Information on using docking modes is in
 * <a href="http://www.javadocking.com/developerguide/dockable.html#DockingMode" target="_blank">How to Use Dockables</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DockingMode {

	// Static fields.

	/**
	 * The docking mode is not known.
	 */
	public static final int UNKNOWN = 0;
	/**
	 * The dockable can be docked at the left side of a dock.
	 */
	public static final int LEFT = 1;
	/**
	 * The dockable can be docked at the right side of a dock.
	 */
	public static final int RIGHT = 1 << 1;
	/**
	 * The dockable can be docked at the top  of a dock.
	 */
	public static final int TOP = 1 << 2;
	/**
	 * The dockable can be docked at the bottom  of a dock.
	 */
	public static final int BOTTOM = 1 << 3;
	/**
	 * The dockable can be docked as tab in a tabbed pane.
	 */
	public static final int TAB = 1 << 4;
	/**
	 * The dockable can float.
	 */
	public static final int FLOAT = 1 << 5;
	/**
	 * The dockable can be docked all alone in a single dock.
	 */
	public static final int SINGLE = 1 << 6;
	/**
	 * The dockable can be docked at the center of a dock.
	 */
	public static final int CENTER = 1 << 7;
	/**
	 * The dockable can be docked in a horizontal line with other dockables.
	 */
	public static final int HORIZONTAL_LINE = 1 << 8;
	/**
	 * The dockable can be docked in a vertical line with other dockables.
	 */
	public static final int VERTICAL_LINE = 1 << 9;
	/**
	 * The dockable can be docked in a grid with other dockables.
	 */
	public static final int GRID = 1 << 10;
	/**
	 * The dockable can be docked in a horizontal tool bar.
	 */
	public static final int HORIZONTAL_TOOLBAR = 1 << 11;
	/**
	 * The dockable can be docked in a vertical tool bar.
	 */
	public static final int VERTICAL_TOOLBAR = 1 << 12;
	/**
	 * The dockable can be docked in a tool grid.
	 */
	public static final int TOOL_GRID = 1 << 13;
	/**
	 * The dockable can be docked in a horizontal minimize bar.
	 */
	public static final int HORIZONTAL_MINIMIZE = 1 << 14;
	/**
	 * The dockable can be docked in a vertical minimize bar.
	 */
	public static final int VERTICAL_MINIMIZE = 1 << 15;
	/**
	 * The dockable can be docked in a minimze grid.
	 */
	public static final int MINIMIZE_GRID = 1 << 16;
	/**
	 * The dockable can be docked in a horizontal or vertical tool bar, or in a tool grid.
	 */
	public static final int TOOL_BAR = HORIZONTAL_TOOLBAR | VERTICAL_TOOLBAR | TOOL_GRID;
	/**
	 * The dockable can be docked in a horizontal or vertical minimize bar or in a minimize grid.
	 */
	public static final int MINIMIZE_BAR = HORIZONTAL_MINIMIZE | VERTICAL_MINIMIZE | MINIMIZE_GRID;
	/**
	 * The dockable can be docked in a line with other dockables.
	 */
	public static final int LINE = HORIZONTAL_LINE | VERTICAL_LINE;
	/**
	 * The dockable can be docked in every dock at every position, except in a tool bar, in a tool grid, in a minimize bar, or in a minimize grid.
	 */
	public static final int ALL = Integer.MAX_VALUE - TOOL_BAR - MINIMIZE_BAR;

	private DockingMode() {
	}

	// Constructors.

	/**
	 * Gets a human readable description for a docking mode defined by this class.
	 *
	 * @param dockingMode A docking mode defined by this class.
	 * @return A human readable description for a docking mode defined by this class.
	 */
	public static String getDescription(int dockingMode) {

		switch (dockingMode) {
			case UNKNOWN:
				return "UNKNOWN";
			case LEFT:
				return "LEFT";
			case RIGHT:
				return "RIGHT";
			case TOP:
				return "TOP";
			case BOTTOM:
				return "BOTTOM";
			case TAB:
				return "TAB";
			case FLOAT:
				return "FLOAT";
			case SINGLE:
				return "SINGLE";
			case CENTER:
				return "CENTER";
			case HORIZONTAL_LINE:
				return "HORIZONTAL_LINE";
			case VERTICAL_LINE:
				return "VERTICAL_LINE";
			case GRID:
				return "GRID";
			case HORIZONTAL_TOOLBAR:
				return "HORIZONTAL_TOOLBAR";
			case VERTICAL_TOOLBAR:
				return "VERTICAL_TOOLBAR";
			case TOOL_GRID:
				return "TOOL_GRID";
			case HORIZONTAL_MINIMIZE:
				return "HORIZONTAL_MINIMIZE";
			case VERTICAL_MINIMIZE:
				return "VERTICAL_MINIMIZE";
			case MINIMIZE_GRID:
				return "MINIMIZE_GRID";
			case TOOL_BAR:
				return "TOOL_BAR";
			case MINIMIZE_BAR:
				return "MINIMIZE_BAR";
			case LINE:
				return "LINE";
			case ALL:
				return "ALL";

			default:
				return "UNKNOWN";

		}
	}

}
