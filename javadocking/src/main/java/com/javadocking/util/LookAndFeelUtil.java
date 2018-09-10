package com.javadocking.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Most of the existing look and feels use borders for javax.swing.JSplitpanes. In a docking framework
 * split panes can be nested. Borders can become very big. It is useful to remove the borders from the
 * split panes and from the dividers of the split panes. This class provides the methods to do that.
 *
 * @author Heidi Rakels.
 */
public class LookAndFeelUtil {

	/**
	 * Removes the borders from the split panes and split pane dividers.
	 *
	 * @see #removeSplitPaneBorder()
	 * @see #removeSplitPaneDividerBorder()
	 */
	public static void removeAllSplitPaneBorders() {
		removeSplitPaneBorder();
		removeSplitPaneDividerBorder();
	}

	/**
	 * Removes the borders from the split pane.
	 * The key <code>SplitPane.border</code> of the {@link UIManager} is given an {@link EmptyBorder}
	 * with size 0.
	 */
	public static void removeSplitPaneBorder() {
		UIManager.put("SplitPane.border", new EmptyBorder(0, 0, 0, 0));
	}

	/**
	 * Removes the borders from the split pane dividers.
	 * The key <code>SplitPaneDivider.border</code> of the {@link UIManager} is given an {@link EmptyBorder}
	 * with size 0.
	 */
	public static void removeSplitPaneDividerBorder() {
		UIManager.put("SplitPaneDivider.border", new EmptyBorder(0, 0, 0, 0));
	}
}
