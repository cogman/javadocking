package com.javadocking.drag.painter;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dockable.Dockable;

import java.awt.*;

/**
 * A dockable drag painter that only paints the dockable when the new dock is a {@link FloatDock}.
 * The dockable is painted on a transparent window.
 * The given {@link RectanglePainter} paints the dockable on this window.
 *
 * @author Heidi Rakels.
 */
public class WindowDockableDragPainter implements DockableDragPainter {

	// Fields.

	/**
	 * The transparent window on which the dockable is painted.
	 */
	private PaintWindow window;
	/**
	 * The painter for the rectangle on the window.
	 */
	private RectanglePainter rectanglePainter;
	/**
	 * True when a label has to be painted on the rectangle.
	 */
	private boolean drawLabel;
	/**
	 * The dockable for which the rectangle was painted.
	 */
	private Dockable previousDockable;
	/**
	 * The rectangle that was painted.
	 */
	private Rectangle previousRectangle = new Rectangle();

	// Constructors.

	/**
	 * Constructs a  dockable drag painter.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 */
	public WindowDockableDragPainter(RectanglePainter rectanglePainter) {
		this(rectanglePainter, false);
	}

	/**
	 * Constructs a  dockable drag painter.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 * @param    drawLabel            True when a label has to be painted on the rectangle.
	 */
	public WindowDockableDragPainter(RectanglePainter rectanglePainter, boolean drawLabel) {
		this.rectanglePainter = rectanglePainter;
		this.drawLabel = drawLabel;
	}


	// Implementations of DockableDragPainter.

	public void clear() {

		if (window != null) {
			window.setVisible(false);
		}

	}

	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		// Do we have to paint on the float dock?
		if ((dock instanceof FloatDock) && (rectangle != null)) {
			// Do we have already a window?
			if (window == null) {
				window = new PaintWindow(rectanglePainter, ((FloatDock) dock).getOwner());
				window.setCursor(DockingManager.getCanDockCursor());
			}

			// Did the label change?
			if ((dockable != null) && drawLabel && (!dockable.equals(previousDockable))) {
				rectanglePainter.setLabel(dockable.getTitle());
			}

			// Position the window.
			if (!rectangle.equals(previousRectangle)) {
				window.setSize(new Dimension(rectangle.width, rectangle.height));
				window.setLocation(rectangle.x, rectangle.y);
			}

			// Make the window visible.
			if (!window.isVisible()) {
				window.setVisible(true);
				window.doRepaint();
			}

			// Do we have to repaint?
			if ((previousRectangle == null) ||
					(previousRectangle.width != rectangle.width) ||
					(previousRectangle.height != rectangle.height) ||
					(!dockable.equals(previousDockable))) {
				window.doRepaint();
				previousDockable = dockable;
				previousRectangle.setRect(rectangle);
			}

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
