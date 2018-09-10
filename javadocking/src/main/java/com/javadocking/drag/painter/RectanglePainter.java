package com.javadocking.drag.painter;

import java.awt.*;

/**
 * This painter paints a rectangle on a java.awt.Graphics.
 *
 * @author Heidi Rakels.
 */
public interface RectanglePainter {

	// Interface methods.

	/**
	 * Paints a rectangle on the given graphics.
	 *
	 * @param graphics The graphics on which the rectangle should be painted.
	 * @param x        The x-position of the rectangle on the graphics.
	 * @param y        The y-position of the rectangle on the graphics.
	 * @param width    The width of the rectangle.
	 * @param height   The height of the rectangle.
	 */
	void paintRectangle(Graphics graphics, int x, int y, int width, int height);

	/**
	 * Sets the label that has to be painted in the rectangle.
	 *
	 * @param label The label that has to be painted in the middle of the rectangle.
	 */
	void setLabel(String label);

}
