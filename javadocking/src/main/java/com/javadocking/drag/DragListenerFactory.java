package com.javadocking.drag;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

/**
 * This is an interface for factories that create {@link DragListener}s.
 *
 * @author Heidi Rakels.
 */
public interface DragListenerFactory {

	// The factory method.

	/**
	 * Creates a drag listener for the given dock.
	 *
	 * @param    dock        The drag listener will listen to drag events on this dock.
	 * @return The drag listener.
	 */
	DragListener createDragListener(Dock dock);

	/**
	 * Creates a drag listener for the given dockable.
	 *
	 * @param    dockable    The drag listener will listen to drag events of this dockable.
	 * @return The drag listener.
	 */
	DragListener createDragListener(Dockable dockable);

}
