package com.javadocking.event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
	@NotNull
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

		for (Object dockingListener1 : dockingListeners) {
			DockingListener dockingListener = (DockingListener) dockingListener1;
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

		for (Object dockingListener1 : dockingListeners) {
			DockingListener dockingListener = (DockingListener) dockingListener1;
			dockingListener.dockingChanged(dockingEvent);
		}

	}

}
