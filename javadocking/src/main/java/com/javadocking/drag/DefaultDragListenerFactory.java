package com.javadocking.drag;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;
import org.jetbrains.annotations.NotNull;

/**
 * This drag listener factory returns a {@link DefaultDragListener} as drag listener.
 *
 * @author Heidi Rakels.
 */
public class DefaultDragListenerFactory implements DragListenerFactory {

	// Implementations of DragListenerFactory.

	@NotNull
	public DragListener createDragListener(Dock dock) {
		return new DefaultDragListener(dock);
	}

	@NotNull
	public DragListener createDragListener(Dockable dockable) {
		return new DefaultDragListener(dockable);
	}

}
