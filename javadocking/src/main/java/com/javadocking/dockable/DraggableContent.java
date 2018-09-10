package com.javadocking.dockable;

import com.javadocking.drag.DragListener;

/**
 * <p>
 * A {@link Dockable} that has an implementation of this interface as content,
 * can be dragged by dragging the content component.
 * </p>
 * <p>
 * Information on using a draggable content is in
 * <a href="http://www.javadocking.com/developerguide/dockable.html#DragListener" target="_blank">How to Use Dockables</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * To make this dragging possible a {@link DragListener} should be added as mouse listener and mouse motion
 * listener to this component or deeper components.
 * A possible implementation of {@link DraggableContent#addDragListener(DragListener)} is:
 * <blockquote><pre>
 *
 * 		this.addMouseListener(dragListener);
 * 		this.addMouseMotionListener(dragListener);
 *
 * </pre></blockquote>
 *
 * @author Heidi Rakels.
 */
public interface DraggableContent {

	/**
	 * Adds the given drag listener as mouse listener and mouse motion listener on the content of this component.
	 *
	 * @param dragListener The drag listener for dragging and docking the dockable with this component as content.
	 */
	public void addDragListener(DragListener dragListener);
}
