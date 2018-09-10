package com.javadocking.drag;

import com.javadocking.dock.LeafDock;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.visualizer.ExternalizeDock;
import com.javadocking.visualizer.Externalizer;

import java.awt.*;

class ExternalizedDraggerSupport {

	/**
	 * Moves an externalized dockable to the given position with the given offset.
	 *
	 * @param draggedDockable The externalized dockable that has to be moved.
	 * @param location        The location for the externalized dockable.
	 * @param offset          The location offset.
	 */
	public static void moveExternalizedDockable(Dockable draggedDockable, Point location, Point offset) {

		// Check if the dockable is externalized.
		if (draggedDockable.getState() != DockableState.EXTERNALIZED) {
			throw new IllegalStateException("The dockable state has to be com.javadocking.dockable.DockableState.EXTERNALIZED");
		}
		LeafDock leafDock = draggedDockable.getDock();
		if (!(leafDock instanceof ExternalizeDock)) {
			throw new IllegalStateException("The dock of the externalized dockable is a [" + leafDock.getClass() + "]. It should be a [com.javadocking.visualizer.Externalizer].");
		}
		ExternalizeDock childDock = (ExternalizeDock) leafDock;
		Externalizer externalizer = childDock.getExternalizer();
		externalizer.moveExternalizedDockable(draggedDockable, location, offset);

	}
}
