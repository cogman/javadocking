package com.javadocking.drag;


import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.Dockable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * This dragger factory creates draggers that remove and add the dragged {@link com.javadocking.dockable.Dockable}s
 * to new {@link com.javadocking.dock.Dock}s dynamically during dragging.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DynamicDraggerFactory implements DraggerFactory {

	// Implementations of DockableDraggerFactory.

	/**
	 * It creates for a {@link com.javadocking.dock.TabDock} a {@link DynamicTabDragger}.
	 * For other types of docks a {@link DynamicDragger} is returned.
	 */
	@Nullable
	public Dragger createDragger(Dock dock) {
		if (dock instanceof TabDock) {
			return new DynamicTabDragger();
		}
		if (dock instanceof LeafDock) {
			return new DynamicDragger();
		}
		return null;
	}

	/**
	 * It creates a {@link DynamicDragger}.
	 */
	@NotNull
	public Dragger createDragger(Dockable dockable) {
		return new DynamicDockableDragger(dockable);
	}

}
