package com.javadocking.drag.painter;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <p>
 * This dockable drag painter shows a window with the image of the dockable
 * at the current mouse position, when the dockable is dragged.
 * </p>
 * <p>
 * This window is also visible outside the owner window.
 * </p>
 * <p>
 * The dock cursor or cannotdock cursor is shown on the window.
 * </p>
 * <p>
 * Several properties define the size of the image of the dockable:
 * <ul>
 * <li>preferredReduceFactor</li>
 * <li>minImageSize</li>
 * <li>maxImageSize</li>
 * <li>minReduceFactor</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class ImageDockableDragPainter implements DockableDragPainter {

	/**
	 * The window that shows the title of the dockable.
	 */
	private JWindow window;
	/**
	 * The content of the window.
	 */
	private JPanel contentPanel;
	/**
	 * The dockable that is currently shown in the window. Can be null, when no dockable is shown.
	 */
	private Dockable dockable;
	/**
	 * The position where the window will be placed.
	 */
	private Point windowLocation = new Point();
	/**
	 * The small image of the dragged dockable.
	 */
	private Image smallImage;
	/**
	 * The component that contains the image.
	 */
	private ImageComponent component;
	/**
	 * Defines how much the image is shifted from the mouse in the vertical direction.
	 */
	private int verticalShift = 5;
	/**
	 * Defines how much the image of the dockable is made smaller.
	 */
	private float preferredReduceFactor = (float) (1.0 / 3.0);
	/**
	 * Defines how much the image of the dockable is at least made smaller.
	 */
	private float minReduceFactor = (float) (1.0 / 1.3);
	/**
	 * The size of the image is not smaller than this size.
	 */
	private Dimension minImageSize = new Dimension(50, 50);
	/**
	 * The size of the image is not bigger than this size.
	 */
	private Dimension maxImageSize = new Dimension(200, 200);


	// Implementations of DockableDragPainter.

	public void clear() {

		dockable = null;
		if (window != null) {
			window.dispose();
			window = null;
		}
		if (smallImage != null) {
			smallImage.flush();
			smallImage = null;
		}

	}

	public void paintDockableDrag(Dockable newDockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		// Create the window.
		if (window == null) {
			window = new JWindow();
			contentPanel = new JPanel(new BorderLayout());
			contentPanel.setBackground(Color.white);
			window.getContentPane().add(contentPanel);
		}

		// Set the cursor on the window.
		if (dock == null) {
			window.setCursor(DockingManager.getCanNotDockCursor());
		} else {
			window.setCursor(DockingManager.getCanDockCursor());
		}

		// Did the dockable change?
		if (!newDockable.equals(dockable)) {
			// Does the dockable have a component?
			Component dockableComponent = newDockable.getContent();
			if ((dockableComponent == null) ||
					(dockableComponent.getSize().width <= 0) ||
					(dockableComponent.getSize().height <= 0)) {
				return;
			}

			// Get the actual reduce factor.
			float actualReduceFactor = calculateActualReduceFactor(dockableComponent.getSize());

			// Get the current rectangle of the dockable.
			Point dockableLocation = new Point();
			SwingUtilities.convertPointToScreen(dockableLocation, dockableComponent);
			Rectangle dockableRectangle = new Rectangle(dockableLocation.x, dockableLocation.y, dockableComponent.getSize().width, dockableComponent.getSize().height);
			window.setSize((int) (dockableRectangle.width * actualReduceFactor), (int) (dockableRectangle.height * actualReduceFactor));
			dockable = newDockable;

			// Create the image.
			BufferedImage dockableImage = new BufferedImage(dockableComponent.getSize().width, dockableComponent.getSize().height, BufferedImage.TYPE_INT_ARGB);
			Graphics dockableImageGraphics = dockableImage.getGraphics();
			dockableComponent.paint(dockableImage.getGraphics());
			dockableImageGraphics.dispose();
			smallImage = dockableImage.getScaledInstance(window.getWidth(), window.getHeight(), BufferedImage.SCALE_SMOOTH);
			dockableImage.flush();

			// NOTE: http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
			// They say not to use getScaledInstance, but I could not get any better results with the alternatives.
			// So I continue with: BufferedImage.SCALE_SMOOTH.

			// Add a component with the image.
			component = new ImageComponent((int) (dockableRectangle.width * actualReduceFactor), (int) (dockableRectangle.height * actualReduceFactor));
			contentPanel.removeAll();
			contentPanel.add(component, BorderLayout.CENTER);
		}

		// Set the new location.
		windowLocation.move(locationInDestinationDock.x - window.getSize().width / 2,
				locationInDestinationDock.y - window.getSize().height / 2 - verticalShift);
		if (dock instanceof Component) {
			SwingUtilities.convertPointToScreen(windowLocation, (Component) dock);
		}
		window.setLocation(windowLocation);

		// Make the window visible.
		if (!window.isVisible()) {
			window.setVisible(true);
			component.doRepaint();
		}

	}

	// Getters / setters.

	/**
	 * Gets how much the image of the dockable is made smaller.
	 * The default value is <code>(float)(1.0 / 3.0)</code>.
	 *
	 * @return How much the image of the dockable is made smaller.
	 */
	public float getPreferredReduceFactor() {
		return preferredReduceFactor;
	}

	/**
	 * Sets how much the image of the dockable is made smaller.
	 *
	 * @param factor How much the image of the dockable is made smaller.
	 */
	public void setPreferredReduceFactor(float factor) {
		this.preferredReduceFactor = factor;
	}

	/**
	 * Gets how much the image of the dockable is at least made smaller.
	 * The default value is <code>(float)(1.0 / 1.3)</code>.
	 *
	 * @return How much the image of the dockable is at least made smaller.
	 */
	public float getMinReduceFactor() {
		return minReduceFactor;
	}

	/**
	 * Sets how much the image of the dockable is at least made smaller.
	 *
	 * @param minReduceFactor How much the image of the dockable is at least made smaller.
	 */
	public void setMinReduceFactor(float minReduceFactor) {
		this.minReduceFactor = minReduceFactor;
	}

	/**
	 * Gets the maximum image size.
	 * The default value is <code>(200, 200)</code>.
	 *
	 * @return The size of the image is not bigger than this size.
	 */
	public Dimension getMaxImageSize() {
		return maxImageSize;
	}

	/**
	 * Sets the maximum image size.
	 *
	 * @param maxImageSize The size of the image is not bigger than this size.
	 */
	public void setMaxImageSize(Dimension maxImageSize) {
		this.maxImageSize = maxImageSize;
	}

	/**
	 * Gets the minimum image size.
	 * The default value is <code>(50, 50)</code>.
	 *
	 * @return The size of the image is not smaller than this size.
	 */
	public Dimension getMinImageSize() {
		return minImageSize;
	}

	/**
	 * sets the minimum image size.
	 *
	 * @param minImageSize The size of the image is not bigger than this size.
	 *                     The width or height may not be 0.
	 */
	public void setMinImageSize(Dimension minImageSize) {

		if ((minImageSize.width == 0) || (minImageSize.height == 0)) {
			throw new IllegalArgumentException("The width or height may not be 0.");
		}
		this.minImageSize = minImageSize;
	}

	/**
	 * Gets how much the image is shifted from the mouse in the vertical direction.
	 * The default value is <code>5</code>.
	 *
	 * @return How much the image is shifted from the mouse in the vertical direction.
	 */
	public int getVerticalShift() {
		return verticalShift;
	}

	/**
	 * Sets how much the image is shifted from the mouse in the vertical direction.
	 *
	 * @param verticalShift How much the image is shifted from the mouse in the vertical direction.
	 */
	public void setVerticalShift(int verticalShift) {
		this.verticalShift = verticalShift;
	}

	// Protected methods.

	/**
	 * Calculates the actual reduce factor to calculate the image size.
	 *
	 * @param    componentSize    The size of the component of which an image has to be made.
	 * The image size will be this size multiplied with the actual reduce factor.
	 * @return The actual reduce factor to calculate the image size.
	 */
	protected float calculateActualReduceFactor(Dimension componentSize) {

		// Take the preferred reduce factor.
		float actualWidth = componentSize.width * preferredReduceFactor;
		float actualHeight = componentSize.height * preferredReduceFactor;

		// Check that the image will not be too small.
		if ((actualHeight < minImageSize.height)) {
			actualWidth = actualWidth * minImageSize.height / actualHeight;
			actualHeight = minImageSize.height;
		}
		if ((actualWidth < minImageSize.width)) {
			actualHeight = actualHeight * minImageSize.width / actualWidth;
			actualWidth = minImageSize.width;
		}

		// Check that the image will not be too big
		if ((actualHeight > maxImageSize.height)) {
			actualWidth = actualWidth * maxImageSize.height / actualHeight;
			actualHeight = maxImageSize.height;
		}
		if ((actualWidth > maxImageSize.width)) {
			actualWidth = maxImageSize.width;
		}

		// Check that the actual reduce factor is not too big.
		if ((actualWidth / componentSize.width) > minReduceFactor) {
			return minReduceFactor;
		}

		return actualWidth / componentSize.width;
	}

	// Private classes.

	/**
	 * This is a JComponent that is not opaque. It delegates to a
	 * {@link com.javadocking.drag.painter.RectanglePainter} to let it paint a rectangle on its graphics.
	 *
	 * @author Heidi Rakels.
	 */
	private class ImageComponent extends JComponent {

		// Constructors.

		/**
		 * Constructs a javax.swing.JComponent with the given width and height.
		 * It uses the given rectangle painter to paint a rectangle on itself.
		 *
		 * @param    rectanglePainter    The rectangle painter used for painting a rectangle on itself.
		 * @param    width                The width of this component.
		 * @param    height                The height of this component.
		 */
		public ImageComponent(int width, int height) {


			// Set the bounds.
			setBounds(0, 0, width, height);

			// Make it transparant.
			setOpaque(false);

		}

		// Overwritten methods from JComponent.

		public void paint(Graphics graphics) {
			graphics.drawImage(smallImage, 0, 0, getWidth(), getHeight(), null);
		}

		// Public methods.

		/**
		 * Repaints the window.
		 */
		public void doRepaint() {

			Graphics g = getGraphics();
			paint(g);

		}
	}

}
