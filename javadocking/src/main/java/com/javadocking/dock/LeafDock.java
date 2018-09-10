package com.javadocking.dock;

import com.javadocking.dockable.Dockable;

import java.awt.*;

/**
 * <p>
 * This is a dock that contains dockables. This kind of docks are the leaves in the dock trees.
 * </p>
 * <p>
 * Information on using leaf docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html" target="_blank">How to Use Laef Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface LeafDock extends Dock {

	// Interface methods.

	/**
	 * <p>
	 * Adds the given dockable to this dock at the given position.
	 * </p>
	 * <p>
	 * If there is already a dockable at the given position, or if the position is invalid,
	 * then the dockable is added at another position.
	 * <p>
	 *
	 * @param dockableToAdd The dockable to add to this dock.
	 * @param position      The position of the dockable.
	 * @throws IllegalStateException If the dock is full.
	 */
	public void addDockable(Dockable dockableToAdd, Position position);


	/**
	 * Determines if the specified dockable can be removed from this dock.
	 *
	 * @param dockable The dockable that will be removed.
	 * @return True if the specified dockable can be removed from this dock, false otherwise.
	 */
	public boolean canRemoveDockable(Dockable dockable);

	/**
	 * Removes the specified dockable from this dock.
	 *
	 * @param dockable The dockable that will be removed.
	 * @return True if the specified dockable was removed from this dock, false otherwise.
	 */
	public boolean removeDockable(Dockable dockable);

	/**
	 * Gets the number of dockables that are docked in this dock.
	 *
	 * @return The number of dockables that are docked in this dock.
	 */
	public int getDockableCount();

	/**
	 * Gets the dockable with the specified index, that is docked in this dock.
	 *
	 * @param index The index of the dockable
	 * @return The dockable with the specified index that is docked in this dock.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getDockableCount()).
	 */
	public Dockable getDockable(int index) throws IndexOutOfBoundsException;

	/**
	 * Determines if the given dockable is docked in this dock.
	 *
	 * @param dockable The dockable.
	 * @return True if if the given dockable is docked in this dock, false otherwise.
	 */
	public boolean containsDockable(Dockable dockable);

	/**
	 * Moves a dockable to a new position in this dock.
	 *
	 * @param dockable         The dockable that will be moved.
	 * @param relativeLocation The location to which the dockable will be moved, relatively to the dock.
	 * @return True if the position of the dockable changed, false otherwise.
	 * @throws IllegalArgumentException    If the given dockable is not docked in this dock.
	 */
	public boolean moveDockable(Dockable dockable, Point relativeLocation);

	/**
	 * Gets the position where the dockable is docked in this dock.
	 *
	 * @param dockable The dockable that is docked in this dock.
	 * @return The position where the dockable is docked in this dock.
	 * Not null.
	 * @throws IllegalArgumentException    If the given dockable is not docked in this dock.
	 */
	public Position getDockablePosition(Dockable dockable) throws IllegalArgumentException;

}
