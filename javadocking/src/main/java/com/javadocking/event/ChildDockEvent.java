package com.javadocking.event;

import com.javadocking.dock.CompositeDock;
import com.javadocking.dock.Dock;

/**
 * <p>
 * This kind of event is broad-casted when a child dock is added, moved, or removed from a composite dock.
 * It is broad-casted:
 * <ul>
 * <li>before the adding</li>
 * <li>after the adding</li>
 * <li>before the move</li>
 * <li>after the move</li>
 * <li>before the removing</li>
 * <li>after the removing</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class ChildDockEvent extends DockingEvent {

	// Fields.

	/**
	 * The child dock that is added, move, or removed.
	 */
	private Dock childDock;


	/**
	 * Constructs a docking event for the given composite dock and child dock.
	 *
	 * @param source          The object on which the event initially occurred.
	 * @param originDock      The composite dock from which the child dock is removed or moved.
	 *                        Null when the child dock is added.
	 * @param destinationDock The dock to which the child dock is added or moved.
	 *                        Null when the child dock is removed.
	 * @param childDock       The child dock that is added, moved, or removed.
	 * @throws IllegalArgumentException If the child dock is null.
	 */
	public ChildDockEvent(Object source, CompositeDock originDock, CompositeDock destinationDock, Dock childDock) {

		super(source, originDock, destinationDock);

		// Check the arguments.
		if (childDock == null) {
			throw new IllegalArgumentException("The child dock is null.");
		}

		this.childDock = childDock;

	}

	// Getters / Setters.

	/**
	 * Gets the child dock that is added, moved, or removed.
	 *
	 * @return The child dock that is added, moved, or removed.
	 */
	public Dock getChildDock() {
		return childDock;
	}

}
