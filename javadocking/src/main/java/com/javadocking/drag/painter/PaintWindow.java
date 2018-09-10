package com.javadocking.drag.painter;

import java.awt.*;

/**
 * This is a window on which a rectangle can be painted with a {@link com.javadocking.drag.painter.RectanglePainter}.
 *
 * @author Heidi Rakels.
 */
class PaintWindow extends Window {

	/**
	 * The painter that paints the content on the window.
	 */
	private RectanglePainter rectanglePainter = new DefaultRectanglePainter();

	// Constructors.

	/**
	 * Constructs a transparent window.
	 *
	 * @param    rectanglePainter    The painter that paints the content on the window.
	 */
	public PaintWindow(RectanglePainter rectanglePainter, Window owner) {
		super(owner);
		this.rectanglePainter = rectanglePainter;
		setBackground(Color.white);

	}

	// Overwritten methods.

	public void paint(Graphics graphics) {

		Image windowImage = createImage(getWidth(), getHeight());
		Graphics windowImageGraphics = windowImage.getGraphics();
		rectanglePainter.paintRectangle(windowImageGraphics, 0, 0, getWidth(), getHeight());
		graphics.drawImage(windowImage, 0, 0, null);
		windowImageGraphics.dispose();
		windowImage.flush();

	}

	// Public methods.

	/**
	 * Repaints the window.
	 */
	public void doRepaint() {

		Graphics graphics = getGraphics();
		if (graphics != null) {
			paint(graphics);
		}

	}

}
