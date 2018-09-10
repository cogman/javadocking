package com.javadocking.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that can be used by classes that have docking listeners attached to it.
 * You can use an instance of this class as a member field and delegate docking event work to it.
 *
 * @author Heidi Rakels.
 */
public class DockingEventSupport {

	// Fields.

	/**
	 * The listeners that will be notified when a dockable or child dock is added, moved or removed from a dock.
	 */
	private List dockingListeners = new ArrayList();

	// Public methods.

	/**
	 * Adds a listener for docking events.
	 *
	 * @param listener A docking listener that will be notified when a dockable or child dock is added, moved or removed from a dock.
	 */
	public void addDockingListener(DockingListener listener) {
		dockingListeners.add(listener);
	}

	/**
	 * Removes a listener for docking events.
	 *
	 * @param listener A docking listener to remove.
	 */
	public void removeDockingListener(DockingListener listener) {
		dockingListeners.remove(listener);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 *
	 * @param    dockingEvent    Gives more information about the origin dock, the destination dock,
	 * and the object whose docking state changed.
	 */
	public void fireDockingWillChange(DockingEvent dockingEvent) {

		Iterator iterator = dockingListeners.iterator();
		while (iterator.hasNext()) {
			DockingListener dockingListener = (DockingListener) iterator.next();
			dockingListener.dockingWillChange(dockingEvent);
		}

	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type.
	 *
	 * @param    dockingEvent    Gives more information about the origin dock, the destination dock,
	 * and the object whose docking state changed.
	 */
	public void fireDockingChanged(DockingEvent dockingEvent) {

		Iterator iterator = dockingListeners.iterator();
		while (iterator.hasNext()) {
			DockingListener dockingListener = (DockingListener) iterator.next();
			dockingListener.dockingChanged(dockingEvent);
		}

	}

}
