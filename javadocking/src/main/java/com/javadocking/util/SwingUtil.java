package com.javadocking.util;

import javax.swing.*;
import java.awt.*;


/**
 * This class contains a collection of static utility methods for Swing.
 *
 * @author Heidi Rakels.
 */
public class SwingUtil {

	// Public static methods.

	private SwingUtil() {
	}

	/**
	 * Repaints the parent of the given component. If the parent is null, the component itself is repainted.
	 *
	 * @param component The component whose parent will be repainted.
	 */
	public static void repaintParent(JComponent component) {

		// Get the parent of the component.
		JComponent parentComponent = (JComponent) SwingUtilities.getAncestorOfClass(JComponent.class, component);

		// Could we find a parent?
		if (parentComponent != null) {
			// Repaint the parent.
			parentComponent.revalidate();
			parentComponent.repaint();
		} else {
			// Repaint the component itself.
			component.revalidate();
			component.repaint();
		}

	}

	/**
	 * Gets the root pane of the given component.
	 *
	 * @param component The component whose root pane is retrieved.
	 * @return The root pane of the component.
	 */
	public static JRootPane getRootPane(Component component) {

		if (component instanceof JRootPane) {
			return (JRootPane) component;
		}
		if (component.getParent() != null) {
			return getRootPane(component.getParent());
		}

		// Get the window of the component.
		Window window = SwingUtilities.windowForComponent(component);
		return getRootPane(window);

	}

	/**
	 * Gets the root pane of the window.
	 * The window should be a javax.swing.JFrame, javax.swing.JDialog
	 * or javax.swing.JWindow. Otherwise null is returned.
	 *
	 * @param window The window whose root pane is retrieved.
	 * @return The root pane of the window of the component.
	 */
	public static JRootPane getRootPane(Window window) {

		if (window == null) {
			return null;
		}

		// Get the root pane if we can find one.
		if (window instanceof JFrame)
			return ((JFrame) window).getRootPane();
		if (window instanceof JWindow)
			return ((JWindow) window).getRootPane();
		if (window instanceof JDialog)
			return ((JDialog) window).getRootPane();

		// We could not find a root pane for this window.
		return null;

	}

	/**
	 * Gets the layered pane of the window of the given component.
	 * The window of the root pane should be a javax.swing.JFrame, javax.swing.JDialog
	 * or javax.swing.JWindow. Otherwise the layered pane of the rootpane is returned.
	 *
	 * @param rootPane The root pane whose layered pane is retrieved.
	 * @return The layered pane.
	 */
	public static JLayeredPane getLayeredPane(JRootPane rootPane) {

		// Get the window of the component.
		Window window = SwingUtilities.getWindowAncestor(rootPane);
		if (window != null) {
			// Get the layered pane if we can find one.
			if (window instanceof JFrame)
				return ((JFrame) window).getLayeredPane();
			if (window instanceof JDialog)
				return ((JDialog) window).getLayeredPane();
			if (window instanceof JWindow)
				return ((JWindow) window).getLayeredPane();
		}

		// Get the layered pane of the root pane immediately.
		return rootPane.getLayeredPane();

	}

	/**
	 * Gets the content pane of the given window.
	 *
	 * @param window The window. It should inherit from javax.swing.JFrame,
	 *               javax.swing.JDialog or javax.swing.JWindow.
	 * @return The content pane of the window.
	 * If the given window is not a javax.swing.JFrame,
	 * javax.swing.JDialog or javax.swing.JWindow, null is returned.
	 */
	public static Container getContentPane(Window window) {

		// Get the layered pane if we can find one.
		if (window instanceof JFrame)
			return ((JFrame) window).getContentPane();
		if (window instanceof JDialog)
			return ((JDialog) window).getContentPane();
		if (window instanceof JWindow)
			return ((JWindow) window).getContentPane();

		// We could not find a root pane for this window.
		return null;

	}

	/**
	 * Verifies if the given point is visible on the screen.
	 *
	 * @param location The given location on the screen.
	 * @return True if the location is on the screen, false otherwise.
	 */
	public static boolean isLocationInScreenBounds(Point location) {

		// Check if the location is in the bounds of one of the graphics devices.
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
		Rectangle graphicsConfigurationBounds = new Rectangle();

		// Iterate over the graphics devices.
		for (int j = 0; j < graphicsDevices.length; j++) {

			// Get the bounds of the device.
			GraphicsDevice graphicsDevice = graphicsDevices[j];
			graphicsConfigurationBounds.setRect(graphicsDevice.getDefaultConfiguration().getBounds());

			// Is the location in this bounds?
			graphicsConfigurationBounds.setRect(graphicsConfigurationBounds.x, graphicsConfigurationBounds.y,
					graphicsConfigurationBounds.width, graphicsConfigurationBounds.height);
			if (graphicsConfigurationBounds.contains(location.x, location.y)) {

				// The location is in this screengraphics.
				return true;

			}

		}

		// We could not find a device that contains the given point.
		return false;

	}

	// Private constructor.

	/**
	 * <p>
	 * Determines if the component is visible in its window at the given screen location.
	 * </p>
	 *
	 * @param location  A location on the screen.
	 * @param component A component in a window.
	 * @return True if the component is visible in its window at the given screen location.
	 */
	public static boolean locationInComponentVisible(Point location, Component component) {

		// Get the root component in the window.
		JRootPane rootPane = getRootPane(component);
		if (rootPane != null) {
			Component rootComponent = rootPane.getContentPane();
			if (rootComponent != null) {
				// Get the location relative to this root component.
				Point locationInRoot = new Point(location);
				SwingUtilities.convertPointFromScreen(locationInRoot, rootComponent);

				// Get the deepest visible component at the given location.
				Component deepestComponent = SwingUtilities.getDeepestComponentAt(rootComponent, locationInRoot.x, locationInRoot.y);
				if (deepestComponent != null) {
					boolean result = SwingUtilities.isDescendingFrom(deepestComponent, component);
					return result;
				}
			}
		}

		return false;

	}
}
