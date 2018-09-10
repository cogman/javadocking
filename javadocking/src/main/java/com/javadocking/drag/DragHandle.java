package com.javadocking.drag;

/**
 * This is a handle for dragging one dockable or a group of dockables.
 *
 * @author Heidi Rakels.
 */
public interface DragHandle {

	// Interface methods.

	/**
	 * Gets the drag listener that will drag the dockables attached to this handle.
	 *
	 * @return The drag listener that drags the dockables attached to this handle.
	 */
	public DragListener getDragListener();

	/**
	 * Sets the drag listener as mouse listener and mouse motion listener
	 * on the handle.
	 *
	 * @param dragListener The drag listener that will drag the dockables attached to this handle.
	 */
	public void setDragListener(DragListener dragListener);

}
