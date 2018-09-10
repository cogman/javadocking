package com.javadocking.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This mouse listener for a java.awt.Window resizes the window.
 * This listener should be added as java.awt.event.MouseListener and as java.awt.event.MouseMotionListener
 * on the given java.awt.Window.
 *
 * @author Heidi Rakels.
 */
class WindowResizer implements MouseMotionListener, MouseListener {

	// Static fields.

	/**
	 * The width of the border where the window can be resized.
	 */
	private static final int RESIZE_BORDER = 3;
	/**
	 * The width of the corner where the window can be resized.
	 */
	private static final int RESIZE_CORNER = 10;


	/**
	 * The window that will be resized.
	 */
	@Nullable
	private Window window;

	/**
	 * The current direction cursor constant on the window.
	 * Possible values are:
	 * <ul>
	 * <li>Cursor.DEFAULT_CURSOR</li>
	 * <li>Cursor.N_RESIZE_CURSOR</li>
	 * <li>Cursor.E_RESIZE_CURSOR</li>
	 * <li>Cursor.S_RESIZE_CURSOR</li>
	 * <li>Cursor.W_RESIZE_CURSOR</li>
	 * <li>Cursor.NE_RESIZE_CURSOR</li>
	 * <li>Cursor.SE_RESIZE_CURSOR</li>
	 * <li>Cursor.SW_RESIZE_CURSOR</li>
	 * <li>Cursor.NW_RESIZE_CURSOR</li>
	 * </ul>
	 */
	private int currentCursorDirection = Cursor.DEFAULT_CURSOR;
	/**
	 * The old cursor.
	 */
	private Cursor oldCursor;

	/**
	 * The mouse location x, when dragging starts.
	 */
	private int startX;
	/**
	 * The mouse location y, when dragging starts.
	 */
	private int startY;
	/**
	 * The width of the window, when dragging starts.
	 */
	private int startWidth;
	/**
	 * The height of the window, when dragging starts.
	 */
	private int startHeight;


	/**
	 * Constructs a resizer for the given window.
	 *
	 * @param window The window that will be resized. Not null.
	 */
	public WindowResizer(@Nullable Window window) {

		if (window == null) {
			throw new NullPointerException("Window null");
		}
		this.window = window;
	}

	// Implementations of MouseListener.

	public void mousePressed(@NotNull MouseEvent mouseEvent) {

		// Initialize.
		Component component = (Component) mouseEvent.getSource();
		startX = mouseEvent.getPoint().x;
		startY = mouseEvent.getPoint().y;
		startWidth = component.getWidth();
		startHeight = component.getHeight();

		// Get the dection for resizing.
		currentCursorDirection = getCursorDirection(component, startX, startY);

	}

	public void mouseReleased(MouseEvent mouseEvent) {

		// Repaint.
		if ((currentCursorDirection != Cursor.DEFAULT_CURSOR) && (!window.isValid())) {
			window.validate();
		}
		currentCursorDirection = Cursor.DEFAULT_CURSOR;
		window.setCursor(oldCursor);

	}

	public void mouseClicked(MouseEvent mouseEvent) {
	}

	// Implementations of MouseMotionListener.

	public void mouseEntered(@NotNull MouseEvent mouseEvent) {

		// Save the cursor.
		Component component = (Component) mouseEvent.getSource();
		oldCursor = component.getCursor();

		// Set the appropriate cursor.
		component.setCursor(retrieveCursor(mouseEvent));

	}

	public void mouseExited(@NotNull MouseEvent mouseEvent) {

		// Reset the cursor.
		Component component = (Component) mouseEvent.getSource();
		if (oldCursor != null) {
			component.setCursor(oldCursor);
		} else {
			component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	public void mouseMoved(@NotNull MouseEvent mouseEvent) {

		// Set the appropriate cursor to show the user which resizing is possible.
		Component component = (Component) mouseEvent.getSource();
		component.setCursor(retrieveCursor(mouseEvent));

	}

	public void mouseDragged(@NotNull MouseEvent mouseEvent) {

		// Are we resizing?
		if (currentCursorDirection != Cursor.DEFAULT_CURSOR) {
			resizeWindow(mouseEvent);
		}

	}

	// Private metods.

	/**
	 * Resizes the window.
	 *
	 * @param    mouseEvent    The mouse event for resizing the window.
	 */
	private void resizeWindow(MouseEvent mouseEvent) {

		// Get the current locaton of the mouse.
		int mouseX = mouseEvent.getPoint().x;
		int mouseY = mouseEvent.getPoint().y;

		// Initialize the rectangles.
		Rectangle newBounds = new Rectangle(window.getBounds());
		Rectangle oldBounds = new Rectangle(window.getBounds());
		int minWidth = window.getMinimumSize().width;
		if (minWidth < 100) {
			minWidth = 100;
		}
		int minHeight = window.getMinimumSize().height;
		if (minHeight < 30) {
			minHeight = 30;
		}

		switch (currentCursorDirection) {
			case Cursor.E_RESIZE_CURSOR:
				changeEast(newBounds, mouseX, minWidth);
				break;

			case Cursor.S_RESIZE_CURSOR:
				changeSouth(newBounds, mouseY, minHeight);
				break;

			case Cursor.W_RESIZE_CURSOR:
				changeWest(newBounds, mouseX, minWidth);
				break;

			case Cursor.N_RESIZE_CURSOR:
				changeNorth(newBounds, mouseY, minHeight);
				break;

			case Cursor.NE_RESIZE_CURSOR:
				changeNorth(newBounds, mouseY, minHeight);
				changeEast(newBounds, mouseX, minWidth);
				break;
			case Cursor.SE_RESIZE_CURSOR:
				changeSouth(newBounds, mouseY, minHeight);
				changeEast(newBounds, mouseX, minWidth);
				break;
			case Cursor.NW_RESIZE_CURSOR:
				changeNorth(newBounds, mouseY, minHeight);
				changeWest(newBounds, mouseX, minWidth);
				break;
			case Cursor.SW_RESIZE_CURSOR:
				changeSouth(newBounds, mouseY, minHeight);
				changeWest(newBounds, mouseX, minWidth);
				break;
			default:
				break;
		}

		// Only change the bounds when they are different.
		if (!newBounds.equals(oldBounds)) {
			window.setBounds(newBounds);

			// When dynamic layout is active, we have to repaint now.
			// Otherwise we wait until the mouse is released.
			if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
				window.validate();
			}
		}

	}

	/**
	 * Sets the new bounds for the window rectangle in the north.
	 *
	 * @param newBounds The bounds of the window rectangle
	 * @param mouseY    The mouse y-position.
	 * @param minHeight The minimum height of the window.
	 */
	private void changeNorth(Rectangle newBounds, int mouseY, int minHeight) {

		// The height and y can change.
		int newHeight = newBounds.height - (mouseY - startY);
		int newY = newBounds.getLocation().y + (mouseY - startY);
		if (newHeight < minHeight) {
			newY = newY - (minHeight - newHeight);
			newHeight = minHeight;
		}
		newBounds.setBounds(newBounds.getLocation().x, newY, newBounds.width, newHeight);

	}

	/**
	 * Sets the new bounds for the window rectangle in the south.
	 *
	 * @param newBounds The bounds of the window rectangle
	 * @param mouseY    The mouse y-position.
	 * @param minHeight The minimum height of the window.
	 */
	private void changeSouth(@NotNull Rectangle newBounds, int mouseY, int minHeight) {

		// Only the height can change.
		int newHeight = startHeight + (mouseY - startY);
		if (newHeight < minHeight) {
			newHeight = minHeight;
		}
		newBounds.setSize(newBounds.width, newHeight);

	}

	/**
	 * Sets the new bounds for the window rectangle in the east.
	 *
	 * @param newBounds The bounds of the window rectangle
	 * @param mouseX    The mouse x-position.
	 * @param minWidth  The minimum width of the window.
	 */
	private void changeEast(@NotNull Rectangle newBounds, int mouseX, int minWidth) {

		// Only the width can change.
		int newWidth = startWidth + (mouseX - startX);
		if (newWidth < minWidth) {
			newWidth = minWidth;
		}
		newBounds.setSize(newWidth, newBounds.height);

	}

	/**
	 * Sets the new bounds for the window rectangle in the west.
	 *
	 * @param newBounds The bounds of the window rectangle
	 * @param mouseX    The mouse x-position.
	 * @param minWidth  The minimum width of the window.
	 */
	private void changeWest(Rectangle newBounds, int mouseX, int minWidth) {

		// The width and x can change.
		int newWidth = newBounds.width - (mouseX - startX);
		int newX = newBounds.getLocation().x + (mouseX - startX);
		if (newWidth < minWidth) {
			newX = newX - (minWidth - newWidth);
			newWidth = minWidth;
		}
		newBounds.setBounds(newX, newBounds.getLocation().y, newWidth, newBounds.height);
	}

	/**
	 * Retrieves the cursor to show the user which resizing is possible for the
	 * current mouse location.
	 *
	 * @param mouseEvent The mouse event.
	 * @return The cursor to show the user which resizing is possible for the
	 * current mouse location.
	 */
	private Cursor retrieveCursor(MouseEvent mouseEvent) {

		int cursor = getCursorDirection((Component) mouseEvent.getSource(), mouseEvent.getPoint().x, mouseEvent.getPoint().y);
		if (cursor != Cursor.DEFAULT_CURSOR) {
			return Cursor.getPredefinedCursor(cursor);
		} else {
			return oldCursor;
		}

	}

	/**
	 * Gets the current direction cursor constant for the position on the component.
	 *
	 * @param component The component.
	 * @param x         The x-location on the component.
	 * @param y         The y-location on the component.
	 * @return The current direction cursor constant on the window.
	 * Possible values are:
	 * <ul>
	 * <li>Cursor.DEFAULT_CURSOR</li>
	 * <li>Cursor.N_RESIZE_CURSOR</li>
	 * <li>Cursor.E_RESIZE_CURSOR</li>
	 * <li>Cursor.S_RESIZE_CURSOR</li>
	 * <li>Cursor.W_RESIZE_CURSOR</li>
	 * <li>Cursor.NE_RESIZE_CURSOR</li>
	 * <li>Cursor.SE_RESIZE_CURSOR</li>
	 * <li>Cursor.SW_RESIZE_CURSOR</li>
	 * <li>Cursor.NW_RESIZE_CURSOR</li>
	 * </ul>
	 */
	private int getCursorDirection(Component component, int x, int y) {

		// Get the component sizes.
		int width = component.getSize().width;
		int height = component.getSize().height;

		// Are we in the NW?
		if ((0 <= y) && (y <= RESIZE_CORNER) && (0 <= x) && (x <= RESIZE_CORNER)) {
			return Cursor.NW_RESIZE_CURSOR;
		}
		// Are we in the NE?
		if ((0 <= y) && (y <= RESIZE_CORNER) && (width - RESIZE_CORNER <= x) && (x <= width)) {
			return Cursor.NE_RESIZE_CURSOR;
		}
		// Are we in the SW?
		if ((height - RESIZE_CORNER <= y) && (y <= height) && (0 <= x) && (x <= RESIZE_CORNER)) {
			return Cursor.SW_RESIZE_CURSOR;
		}
		// Are we in the SE?
		if ((height - RESIZE_CORNER <= y) && (y <= height) && (width - RESIZE_CORNER <= x) && (x <= width)) {
			return Cursor.SE_RESIZE_CURSOR;
		}

		// Are we in the north?
		if ((0 <= y) && (y <= RESIZE_BORDER) && (0 <= x) && (x <= width)) {
			return Cursor.N_RESIZE_CURSOR;
		}
		// Are we in the south?
		if ((height - RESIZE_BORDER <= y) && (y <= height) && (0 <= x) && (x <= width)) {
			return Cursor.S_RESIZE_CURSOR;
		}
		// Are we in the west?
		if ((0 <= x) && (x <= RESIZE_BORDER) && (0 <= y) && (y <= height)) {
			return Cursor.W_RESIZE_CURSOR;
		}
		// Are we in the east?
		if ((width - RESIZE_BORDER <= x) && (x <= width) && (0 <= y) && (y <= height)) {
			return Cursor.E_RESIZE_CURSOR;
		}

		return Cursor.DEFAULT_CURSOR;

	}

}
