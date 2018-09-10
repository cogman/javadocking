package com.javadocking.drag;


import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

/**
 * This is an interface for a factory that creates {@link com.javadocking.drag.Dragger}s
 * for the dockables of a given {@link com.javadocking.dock.Dock} or for a given {@link Dockable}.
 *
 * @author Heidi Rakels.
 */
public interface DraggerFactory {
	/**
	 * Creates a dragger for the dockables of the given dock.
	 *
	 * @param dock The dock for which a dragger should be created.
	 * @return The created dragger.
	 */
	Dragger createDragger(Dock dock);

	/**
	 * Creates a dragger for the given dockable.
	 *
	 * @param dockable The dockable for which a dragger should be created.
	 * @return The created dragger.
	 */
	Dragger createDragger(Dockable dockable);
}
