package com.javadocking.event;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * This kind of event is broad-casted when a dockable is added to a dock, moved in a dock,
 * moved to another dock, or removed from a dock.
 * It is broad-casted before the docking change and after the docking change.
 *
 * @author Heidi Rakels.
 */
public class DockableEvent extends DockingEvent {


	// Fields.

	/**
	 * The dockable that is added to a dock, moved in a dock, moved to another dock, or removed from a dock.
	 */
	@Nullable
	private Dockable dockable;


	/**
	 * Constructs a docking event for the given dock and dockable.
	 *
	 * @param source          The object on which the event initially occurred.
	 * @param originDock      The dock from which the dockable is removed or moved.
	 *                        Null when the dockable was closed before.
	 * @param destinationDock The dock to which the dockable is added or moved.
	 *                        Null when the dockable is closed.
	 * @param dockable        The dockable that is added to a dock, moved in a dock, moved to another dock,
	 *                        or removed from a dock.
	 * @throws IllegalArgumentException If the dock or dockable are null.
	 */
	public DockableEvent(@NotNull Object source, Dock originDock, Dock destinationDock, @Nullable Dockable dockable) {

		super(source, originDock, destinationDock);

		// Check the arguments.
		if (dockable == null) {
			throw new IllegalArgumentException("The dockable is null.");
		}

		this.dockable = dockable;

	}

	// Getters / Setters.

	/**
	 * Gets the dockable that is added to a dock, moved in a dock, moved to another dock,
	 * or removed from a dock.
	 *
	 * @return The dockable that is added to a dock, moved in a dock, moved to another dock,
	 * or removed from a dock.
	 */
	@Nullable
	public Dockable getDockable() {
		return dockable;
	}

}
