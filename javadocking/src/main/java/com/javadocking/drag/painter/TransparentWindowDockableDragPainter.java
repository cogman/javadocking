package com.javadocking.drag.painter;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dockable.Dockable;

import java.awt.*;

/**
 * <p>
 * A dockable drag painter that only paints the dockable when the new dock is a {@link FloatDock}.
 * The dockable is painted on a transparent window.
 * The given {@link RectanglePainter} paints the dockable on this window.
 * </p>
 * <p>
 * <b>WARNING: this works only for fast computers!</b>
 * </p>
 *
 * @author Heidi Rakels.
 */
public class TransparentWindowDockableDragPainter implements DockableDragPainter {

	// Fields.

	/**
	 * The transparent window on which the dockable is painted.
	 */
	private TransparentWindow window;
	/**
	 * The painter for the rectangle on the window.
	 */
	private RectanglePainter rectanglePainter;
	/**
	 * True when a label has to be painted on the rectangle.
	 */
	private boolean drawLabel;

	// Constructors.

	/**
	 * Constructs a  dockable drag painter.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 */
	public TransparentWindowDockableDragPainter(RectanglePainter rectanglePainter) {
		this(rectanglePainter, false);
	}

	/**
	 * Constructs a  dockable drag painter.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 * @param    drawLabel            True when a label has to be painted on the rectangle.
	 */
	public TransparentWindowDockableDragPainter(RectanglePainter rectanglePainter, boolean drawLabel) {
		this.rectanglePainter = rectanglePainter;
		this.drawLabel = drawLabel;
	}

	// Implementations of DockableDragPainter.

	public void clear() {

		if (window != null) {
			window.dispose();
			window = null;
		}

	}

	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		// Do we have to paint on the float dock?
		if ((dock instanceof FloatDock) && (rectangle != null)) {

			if (window == null) {
				//window = new TransparentWindow(rectanglePainter, ((FloatDock)dock).getOwner());
				window = new TransparentWindow(rectanglePainter);
				window.setCursor(DockingManager.getCanDockCursor());
			}

			if ((dockable != null) && drawLabel) {
				rectanglePainter.setLabel(dockable.getTitle());
			}

			// Capture the screen when the window is not visible already.
			if (!window.isVisible()) {
				window.captureScreen();
			}

			// Position the window.
			window.setSize(new Dimension(rectangle.width, rectangle.height));
			window.setLocation(rectangle.x, rectangle.y);

			// Make the window visible.
			if (!window.isVisible()) {
				window.setVisible(true);
			}

			window.doRepaint();

		} else {
			// Make the window invisible.
			if (window != null) {
				window.setVisible(false);
			}
		}

	}

	/**
	 * Determines if a label has to be painted on the rectangle.
	 *
	 * @return True when a label has to be painted on the rectangle.
	 */
	public boolean isDrawLabel() {
		return drawLabel;
	}

	/**
	 * Sets if a label has to be painted on the rectangle.
	 *
	 * @param drawLabel True when a label has to be painted on the rectangle.
	 */
	public void setDrawLabel(boolean drawLabel) {
		this.drawLabel = drawLabel;
	}
}
