package com.javadocking.drag.painter;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import java.awt.*;

/**
 * This is an interface for a factory that creates a java.awt.Component to show where a {@link Dockable}
 * will be docked in a {@link Dock} while dragging the dockable.
 *
 * @author Heidi Rakels.
 */
public interface DragComponentFactory {

	// Interface methods.

	/**
	 * Creates a component to show where a dockable will be docked in a dock while dragging the dockable.
	 *
	 * @param dockable  The dragged dockable.
	 * @param dock      The dock in which the dockable will be docked, if the mouse is released in the current position.
	 * @param rectangle The rectangle relative to the dock that represents the window for the dockable,
	 *                  if the mouse is released in the current position.
	 * @return The component that shows where a dockable will be docked in a dock.
	 */
	public Component createDragComponent(Dockable dockable, Dock dock, Rectangle rectangle);

}
