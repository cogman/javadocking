package com.javadocking.event;

/**
 * <p>
 * This is an interface for classes that want to be informed,
 * when a dockable or child dock is added to a dock, moved in a dock, moved to another dock, or removed from a dock.
 * </p>
 * <p>
 * They are informed before the docking change starts and after the docking change finished.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockingListener {

	/**
	 * Invoked whenever a dockable or child dock is about to be added to a dock,
	 * moved in a dock, moved to another dock, or removed from a dock.
	 *
	 * @param dockingEvent Gives more information about the origin dock, the destination dock,
	 *                     and the object whose docking state changed.
	 */
	void dockingWillChange(DockingEvent dockingEvent);

	/**
	 * Invoked whenever a dockable has been added to a dock,
	 * moved in a dock, moved to another dock, or removed from a dock.
	 *
	 * @param dockingEvent Gives more information about the origin dock, the destination dock,
	 *                     and the object whose docking state changed.
	 */
	void dockingChanged(DockingEvent dockingEvent);

}
