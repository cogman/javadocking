package com.javadocking.event;

import com.javadocking.dock.Dock;

import java.util.EventObject;

/**
 * <p>
 * This kind of event is broad-casted when the structure of docks and dockables change in a dock model.
 * </p>
 * <p>
 * Dockables or child docks can be added, moved or removed. An event will be broad-casted before and
 * after the change.
 * </p>
 * <p>
 * There are 4 different kinds of docking events:
 * <ul>
 * <li>When a dockable or child dock is added, the origin dock is null.</li>
 * <li>When a dockable or child dock is removed, the destination dock is null.</li>
 * <li>When a dockable or child dock is moved in its dock, the origin dock and destination dock are equal.</li>
 * <li>When a dockable or child dock is moved to another dock, the origin dock and destination dock are different.</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class DockingEvent extends EventObject {

	// Fields.

	/**
	 * The dock from which the dockable or child dock is removed or moved.
	 * Null when the dockable was closed before or the child dock is added.
	 */
	private Dock originDock;
	/**
	 * The dock to which the dockable or child dock is added or moved.
	 * Null when the dockable is closed or the child dock is removed.
	 */
	private Dock destinationDock;

	/**
	 * Constructs a docking event for the given dock.
	 *
	 * @param source          The object on which the event initially occurred.
	 * @param originDock      The dock from which the dockable or child dock is removed or moved.
	 *                        Null when the dockable was closed before or the child dock is added.
	 * @param destinationDock The dock to which the dockable or child dock is added or moved.
	 *                        Null when the dockable is closed or the child dock is removed.
	 * @throws IllegalArgumentException If the dock is null.
	 */
	public DockingEvent(Object source, Dock originDock, Dock destinationDock) {

		super(source);

		this.originDock = originDock;
		this.destinationDock = destinationDock;

	}

	// Getters / Setters.

	/**
	 * Gets the dock from which the dockable or child dock is removed or moved.
	 *
	 * @return The dock from which the dockable or child dock is removed or moved.
	 * Null when the dockable was closed before or the child dock is added.
	 */
	public Dock getOriginDock() {
		return originDock;
	}

	/**
	 * Gets the dock to which the dockable or child dock is added or moved.
	 *
	 * @return The dock to which the dockable or child dock is added or moved.
	 * Null when the dockable is closed or the child dock is removed.
	 */
	public Dock getDestinationDock() {
		return destinationDock;
	}

}
