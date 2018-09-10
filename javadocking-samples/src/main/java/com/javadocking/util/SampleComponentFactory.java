package com.javadocking.util;

import com.javadocking.component.DefaultSwComponentFactory;

import javax.swing.*;

/**
 * This component factory creates javax.swing.JSplitPane objects with small dividers.
 *
 * @author Heidi Rakels.
 */
public class SampleComponentFactory extends DefaultSwComponentFactory {

	/**
	 * The default size of the divider for splitpanes.
	 */
	public static final int DEFAULT_DIVIDER_SIZE = 2;

	// Fields.

	/**
	 * The size of the divider for splitpanes.
	 */
	private int dividerSize = DEFAULT_DIVIDER_SIZE;

	// Overwritten methods of DefaultSwComponentFactory.

	public JSplitPane createJSplitPane() {

		JSplitPane splitPane = super.createJSplitPane();
		splitPane.setDividerSize(dividerSize);
		return splitPane;

	}

	// Getters / Setters.

	/**
	 * Gets the size of the divider for split panes.
	 *
	 * @return The size of the divider for split panes.
	 */
	public int getDividerSize() {
		return dividerSize;
	}

	/**
	 * Sets the size of the divider for split panes.
	 *
	 * @param dividerSize The size of the divider for split panes.
	 */
	public void setDividerSize(int dividerSize) {
		this.dividerSize = dividerSize;
	}

}
