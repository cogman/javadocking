package com.javadocking.drag.painter;

import javax.swing.*;
import java.awt.*;

/**
 * This is a window that has the screen as background.
 * The image of the screen is made when {@link #captureScreen()} is called.
 * The window can be repainted by calliong {@link #doRepaint()}.
 *
 * @author Heidi Rakels.
 */
class TransparentWindow extends JWindow {

	/**
	 * The image of the screen.
	 */
	private Image screenImage;
	/**
	 * The image with the dimensions of the window. It will be filled with a part of the screen.
	 */
	private Image windowImage;
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
	public TransparentWindow(RectanglePainter rectanglePainter) {
		this.rectanglePainter = rectanglePainter;
	}

	// Overwritten methods.

	public void paint(Graphics graphics) {

		if (windowImage == null) {
			windowImage = createImage(getWidth(), getHeight());
		}
		/**
		 * The graphics of the window image.
		 */
		final Graphics windowImageGraphics = windowImage.getGraphics();
		windowImageGraphics.drawImage(screenImage, 0, 0, getWidth(), getHeight(), getX(), getY(), getX() + getWidth(), getY() + getHeight(), null);
		rectanglePainter.paintRectangle(windowImageGraphics, 0, 0, getWidth(), getHeight());
		graphics.drawImage(windowImage, 0, 0, null);
		windowImageGraphics.dispose();

	}

	public void dispose() {
		super.dispose();

		// Flush the images.
		if (screenImage != null) {
			screenImage.flush();
			screenImage = null;
		}
		if (windowImage != null) {
			windowImage.flush();
			windowImage = null;
		}
	}

	// Public methods.

	/**
	 * Repaints the window.
	 */
	public void doRepaint() {

		Graphics g = getGraphics();
		paint(g);

	}

	/**
	 * Creates an image of the screen.
	 */
	public void captureScreen() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		try {
			Robot robot = new Robot();
			Rectangle rectangle = new Rectangle(0, 0, dim.width, dim.height);
			screenImage = robot.createScreenCapture(rectangle);
			windowImage = null;
		} catch (AWTException exception) {
			System.out.println("Robot exception.");
		}
	}

}
