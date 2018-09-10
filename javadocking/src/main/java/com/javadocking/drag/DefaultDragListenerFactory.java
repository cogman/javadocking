package com.javadocking.drag;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

/**
 * This drag listener factory returns a {@link DefaultDragListener} as drag listener.
 *
 * @author Heidi Rakels.
 */
public class DefaultDragListenerFactory implements DragListenerFactory {

	// Implementations of DragListenerFactory.

	public DragListener createDragListener(Dock dock) {
		return new DefaultDragListener(dock);
	}

	public DragListener createDragListener(Dockable dockable) {
		return new DefaultDragListener(dockable);
	}

}
