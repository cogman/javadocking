package com.javadocking.drag.painter;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import java.awt.*;


/**
 * <p>
 * This painter paints a representation for a {@link com.javadocking.dockable.Dockable} during dragging.
 * </p>
 * <p>
 * Usually a rectangle is painted.
 * It shows where the dockable will be docked in a {@link com.javadocking.dock.Dock},
 * if the mouse would be released at the current mouse position.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockableDragPainter {

	// Interface methods.

	/**
	 * <p>
	 * Paints a dockable during dragging.
	 * </p>
	 * <p>
	 * Usually a rectangle is painted.
	 * It shows where the dockable will be docked in the dock,
	 * if the mouse would be released at the current mouse position.
	 * </p>
	 *
	 * @param dockable  The dockable that will be painted.
	 * @param dock      The dock in which the dockable can be docked.
	 * @param rectangle The rectangle that defines, where the given dockable
	 *                  will be docked, if the mouse is released at the current mouse position.
	 *                  The position of the rectangle is relative to the given dock.
	 * @param    mouseLocation    The position of the mouse relative to the given dock.
	 */
	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point mouseLocation);

	/**
	 * Clears everything what was painted by this painter before.
	 */
	public void clear();

}
