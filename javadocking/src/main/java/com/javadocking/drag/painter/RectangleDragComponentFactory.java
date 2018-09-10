package com.javadocking.drag.painter;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import javax.swing.*;
import java.awt.*;

/**
 * This factory creates a javax.swing.JComponent to represent the dragged dockable.
 * The javax.swing.JComponent uses a {@link com.javadocking.drag.painter.RectanglePainter} to paint the
 * dragged dockable.
 *
 * @author Heidi Rakels.
 */
public class RectangleDragComponentFactory implements DragComponentFactory {

	// Fields.

	/**
	 * The painter that paints the rectangles on the javax.swing.JComponents returned by this factory.
	 */
	private RectanglePainter rectanglePainter;
	/**
	 * True when a label has to be painted on the rectangle.
	 */
	private boolean drawLabel;

	// Constructors.

	/**
	 * Constructs a rectangle drag component factory that uses the given painter
	 * for painting the rectangles on the javax.swing.JComponent.
	 *
	 * @param    rectanglePainter    The painter that paints the rectangles on the javax.swing.JComponents returned by this factory.
	 */
	public RectangleDragComponentFactory(RectanglePainter rectanglePainter) {
		this(rectanglePainter, false);
	}

	/**
	 * Constructs a rectangle drag component factory that uses the given painter
	 * for painting the rectangles on the javax.swing.JComponent.
	 *
	 * @param    rectanglePainter    The painter that paints the rectangles on the javax.swing.JComponents returned by this factory.
	 * @param    drawLabel            True when a label has to be painted on the rectangle.
	 */
	public RectangleDragComponentFactory(RectanglePainter rectanglePainter, boolean drawLabel) {
		super();

		this.rectanglePainter = rectanglePainter;
		this.drawLabel = drawLabel;
	}

	// Implementations of DragComponentFactory.

	public Component createDragComponent(Dockable dockable, Dock dock, Rectangle rectangle) {
		if (drawLabel) {
			rectanglePainter.setLabel(dockable.getTitle());
		}
		return new RectangleComponent(rectanglePainter, rectangle.width, rectangle.height);
	}

	// Getters / Setters.

	/**
	 * Gets the painter that paints the rectangles on the javax.swing.JComponents returned by this factory.
	 *
	 * @return The painter that paints the rectangles on the JComponents returned by this factory.
	 */
	public RectanglePainter getRectanglePainter() {
		return rectanglePainter;
	}

	/**
	 * Sets the painter that paints the rectangles on the javax.swing.JComponents returned by this factory.
	 *
	 * @param rectanglePainter The painter that paints the rectangles on the JComponents returned by this factory.
	 */
	public void setRectanglePainter(RectanglePainter rectanglePainter) {
		this.rectanglePainter = rectanglePainter;
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

	// Private classes.

	/**
	 * This is a JComponent that is not opaque. It delegates to a
	 * {@link com.javadocking.drag.painter.RectanglePainter} to let it paint a rectangle on its graphics.
	 *
	 * @author Heidi Rakels.
	 */
	private static class RectangleComponent extends JComponent {

		// Fields.

		/**
		 * The painter that paints the rectangle on the graphics of this component.
		 */
		private RectanglePainter rectanglePainter;

		// Constructors.

		/**
		 * Constructs a javax.swing.JComponent with the given width and height.
		 * It uses the given rectangle painter to paint a rectangle on itself.
		 *
		 * @param    rectanglePainter    The rectangle painter used for painting a rectangle on itself.
		 * @param    width                The width of this component.
		 * @param    height                The height of this component.
		 */
		public RectangleComponent(RectanglePainter rectanglePainter, int width, int height) {

			this.rectanglePainter = rectanglePainter;

			// Set the bounds.
			setBounds(0, 0, width, height);

			// Make it transparant.
			setOpaque(false);

		}

		// Overwritten methods from JComponent.

		public void paint(Graphics graphics) {
			super.paint(graphics);
			rectanglePainter.paintRectangle(graphics, 0, 0, getWidth(), getHeight());
		}

	}

}
