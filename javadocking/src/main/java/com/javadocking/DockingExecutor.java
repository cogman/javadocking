package com.javadocking;

import com.javadocking.dock.*;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.event.DockableEvent;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPath;
import com.javadocking.model.DockingPathModel;
import com.javadocking.util.DockingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * The docking executor is used to:
 * <ul>
 * <li>add dockables to docks.</li>
 * <li>move dockables in docks.</li>
 * <li>move dockables to other docks.</li>
 * <li>remove dockables from docks.</li>
 * </ul>
 *
 * <p>
 * It does not only execute these actions, but it also informs the listeners of the dockable
 * about the docking changes.
 * </p>
 * <p>
 * Information on using the docking executor is in
 * <a href="http://www.javadocking.com/developerguide/adddockable.html" target="_blank">How to Add, Move, and Remove Dockables</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DockingExecutor {

	/**
	 * <p>
	 * Changes the docking of the dockable to the given location in the given destination dock.
	 * If the dockable is already docked in a different dock, the dockable is first removed.
	 * </p>
	 * <p>
	 * No tests are performed, if these operations are allowed. These tests should have been done before calling this method.
	 * </p>
	 * <p>
	 * First a {@link DockableEvent} is fired that informs the listeners of the dockable that the docking will change.
	 * </p>
	 * <p>
	 * There are different possibilities:
	 * <ul>
	 * <li>The dockable is already docked in a dock, the dock of the dockable is not the destination dock, and the destination dock is not null:
	 * <ul>
	 * <li>The dockable is first removed from its origin dock.</li>
	 * <li>Then the dockable is added to the destination dock by using {@link Dock#addDockable(Dockable, Point, Point)} of the destination dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is already docked in a dock and the destination dock is null:
	 * <ul>
	 * <li>The dockable is removed from its origin dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is already docked in a dock and the dock of the dockable is the same as the destination dock:
	 * <ul>
	 * <li>The dockable is moved in the dock by using {@link LeafDock#moveDockable(Dockable, Point)} of the dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is not already docked in a dock and the destination dock is not null:
	 * <ul>
	 * <li>The dockable is added to the destination dock by using {@link Dock#addDockable(Dockable, Point, Point)} of the destination dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is not already docked in a dock and the destination dock is null:
	 * <ul>
	 * <li>Nothing is done.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * <p>
	 * Finally, a dockable event is fired that informs the listeners of the dockable that the docking has changed.
	 * </p>
	 *
	 * @param dockable         The dockable whose docking will be changed. Not null.
	 * @param destinationDock  The dock to which the dockable will be added. Can be null, if the dockable has to be closed.
	 * @param relativeLocation The location where the dockable will be added. Can be null, if the destination dock is also null.
	 * @param dockableOffset   The mouse location where the dragging started, relatively to the previous dock of the dockable. Can be null, if the destination dock is also null.
	 * @return True if the docking change was successfull, false otherwise.
	 */
	public boolean changeDocking(Dockable dockable, Dock destinationDock, Point relativeLocation, Point dockableOffset) {

		// Remember if the change was successfull.
		boolean result = true;
		boolean removalSuccessFull = false;

		// Get the current dock of the dockable.
		LeafDock originDock = dockable.getDock();

		// Fire the event that the docking will change.
		DockableEvent dockingWillChangeEvent = new DockableEvent(this, originDock, destinationDock, dockable);
		dockable.fireDockingWillChange(dockingWillChangeEvent);

		// Do we have to move the dockable in its current dock?
		if ((originDock != null) && (originDock.equals(destinationDock))) {
			// Move the dockable.
			result = originDock.moveDockable(dockable, relativeLocation);
		} else {
			// Remove the dockable.
			removalSuccessFull = removeDockable(dockable);
			result = removalSuccessFull;

			// Do we have to add the dockable to a new dock?
			if ((result) && (destinationDock != null) && (!destinationDock.equals(originDock))) {
				// Add the dockable.
				result = destinationDock.addDockable(dockable, relativeLocation, dockableOffset);

				if (result) {
					// Adapt the size of a float child that received a dockable.
					Dock floatChildDock = DockingUtil.getFloatChildDock(dockable.getDock());
					if (floatChildDock != null) {
						Window window = SwingUtilities.getWindowAncestor((Component) floatChildDock);
						window.pack();
					}

				}
			}
		}

		// Fire event if the change was successfull.
		if (result) {
			// Fire the event that the docking has been changed.
			DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, destinationDock, dockable);
			dockable.fireDockingChanged(dockingChangedEvent);
		} else if (removalSuccessFull) {
			// Only the removal was successfull. Should not happen!
			// Fire the event that the docking has been changed.
			DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, null, dockable);
			dockable.fireDockingChanged(dockingChangedEvent);
		}

		// Return if the change was successful.
		return result;

	}

	/**
	 * <p>
	 * Changes the docking of the dockable to the given position in the given destination leaf dock.
	 * If the dockable is already docked in a different dock, the dockable is first removed.
	 * </p>
	 * <p>
	 * No tests are performed, if these operations are allowed. These tests should have been done before calling this method.
	 * </p>
	 * <p>
	 * First a {@link DockableEvent} is fired that informs the listeners of the dockable that the docking will change.
	 * </p>
	 * <p>
	 * There are different possibilities:
	 * <ul>
	 * <li>The dockable is already docked in a dock and the destination dock is not null:
	 * <ul>
	 * <li>The dockable is first removed from its origin dock.</li>
	 * <li>Then the dockable is added to the destination dock by using {@link LeafDock#addDockable(Dockable, Position)} of the given destination dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is already docked in a dock and the destination dock is null:
	 * <ul>
	 * <li>The dockable is removed from its origin dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is not already docked in a dock and the destination dock is not null:
	 * <ul>
	 * <li>The dockable is added to the destination dock by using {@link LeafDock#addDockable(Dockable, Position)} of the given destination dock.</li>
	 * </ul>
	 * </li>
	 * <li>The dockable is not already docked in a dock and the destination dock is null:
	 * <ul>
	 * <li>Nothing is done.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 *
	 * <p>
	 * Finally, a dockable event is fired that informs the listeners of the dockable that the docking has changed.
	 * </p>
	 *
	 * @param dockable        The dockable whose docking will be changed. Not null.
	 * @param destinationDock The dock to which the dockable will be added. Can be null, if the dockable has to be closed.
	 * @param position        The position where the dockable will be added. Can be null, if the destination dock is also null.
	 * @return True if the docking change was successfull, false otherwise.
	 */
	public boolean changeDocking(Dockable dockable, LeafDock destinationDock, Position position) {

		// Get the current dock of the dockable.
		LeafDock originDock = dockable.getDock();

		// Fire the event that the docking will change.
		DockableEvent dockingWillChangeEvent = new DockableEvent(this, originDock, destinationDock, dockable);
		dockable.fireDockingWillChange(dockingWillChangeEvent);

		// Remove the dockable.
		boolean removalSuccessFull = removeDockable(dockable);
		boolean success = removalSuccessFull;

		// Do we have to add the dockable to a dock?
		if ((success) && (destinationDock != null)) {
			// Add the dockable.
			destinationDock.addDockable(dockable, position);

			if (success) {
				// Adapt the size of a float child that received a dockable.
				Dock floatChildDock = DockingUtil.getFloatChildDock(dockable.getDock());
				if (floatChildDock != null) {
					Window window = SwingUtilities.getWindowAncestor((Component) floatChildDock);
					window.pack();
				}

			}
		}

		// Fire event if the change was successfull.
		if (success) {
			// Fire the event that the docking has been changed.
			DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, destinationDock, dockable);
			dockable.fireDockingChanged(dockingChangedEvent);
		} else if (removalSuccessFull) {
			// Only the removal was successfull. Should not happen!
			// Fire the event that the docking has been changed.
			DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, null, dockable);
			dockable.fireDockingChanged(dockingChangedEvent);
		}

		// Return if the change was successful.
		return success;
	}

	/**
	 * <p>
	 * Tries to dock the dockable in the tree of docks that has the given dock as
	 * root dock. The dockable will be docked in a dock that is not already full.
	 * The dockable will be docked in a position that is valid for this dockable.
	 * If the dockable is already docked in a dock, the dockable is removed, before it is added to a new dock.
	 * </p>
	 * <p>
	 * If the root is a {@link CompositeDock}, it tries to add the dockable to
	 * a child tree that has a free place in a dock. If this didn't work, it
	 * tries to add the dockable to the root dock itself.
	 * </p>
	 * <p>
	 * A {@link DockableEvent} is fired before and after the addition that informs the listeners of the dockable
	 * about the addition.
	 * </p>
	 *
	 * @param dockable The dockable that will be docked in the dock or in one of its deeper children.
	 * @param rootDock The root of the docks where the dockable will be docked. Can be null, when the dockable only has to be removed.
	 * @return True if the dockable could be docked in the root dock or in one of its children, false otherwise.
	 */
	public boolean changeDocking(Dockable dockable, Dock rootDock) {

		// Do we only have to remove the dockable?
		if (rootDock == null) {
			LeafDock originDock = dockable.getDock();
			if (originDock != null) {
				// Fire the event about the docking change.
				DockableEvent dockingWillChangeEvent = new DockableEvent(this, originDock, null, dockable);
				dockable.fireDockingWillChange(dockingWillChangeEvent);

				// Remove the dockable.
				if (removeDockable(dockable)) {
					// Fire the event about the docking change.
					DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, null, dockable);
					dockable.fireDockingChanged(dockingChangedEvent);
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		}

		// Do we have a composite dock?
		if (rootDock instanceof CompositeDock) {
			CompositeDock compositeDock = (CompositeDock) rootDock;

			// Try to add to one of the children.
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				// Try to add to the child.
				if (changeDocking(dockable, compositeDock.getChildDock(index))) {
					// Succes.
					return true;
				}
			}
		}

		// If we are here, we couldn't add the dockable already.
		// Try to add the dockable to this dock with location the middle of the dock.
		// Get the middle of the dock.
		Point location = new Point();
		if (rootDock instanceof Component) {
			Dimension size = ((Component) rootDock).getSize();
			location.move(size.width / 2, size.height / 2);
		}

		// Can we add the dockable?
		if (rootDock.getDockPriority(dockable, location) != Priority.CANNOT_DOCK) {
			if (DockingManager.getDockingExecutor().changeDocking(dockable, rootDock, location, new Point())) {
				// Succes.
				return true;
			}
		}

		// We couldn't add the dockable.
		return false;

	}

	/**
	 * <p>
	 * Changes the docking of the dockable as good as possible with the information in the given docking path.
	 * If the dockable is already docked in a dock, the dockable is removed, before it is added to a new dock.
	 * </p>
	 * <p>
	 * If the root dock of the path is not in the dock model any more, the dockable is not added.
	 * </p>
	 * <p>
	 * If the root dock of the path is still in the dock model, and there is somewhere a free place
	 * in a dock of the tree defined by this root, the dockable will always be added.
	 * </p>
	 * <p>
	 * A {@link DockableEvent} is fired before and after the addition that informs the listeners of the dockable
	 * about the addition.
	 * </p>
	 *
	 * @param dockable    The dockable whose docking will be changed. Not null.
	 * @param dockingPath Contains the model, docks, and positions that define where the dockable should be docked. Not null.
	 * @return True if the docking change was successfull, false otherwise.
	 */
	public boolean changeDocking(Dockable dockable, DockingPath dockingPath) {

		if (dockingPath == null) {
			throw new NullPointerException("Docking path null.");
		}

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// Check if the root dock still exists in the dock model.
		String rootDockKey = dockingPath.getRootDockKey();
		Dock rootDock = dockModel.getRootDock(rootDockKey);
		if (rootDock == null) {
			return false;
		}
		if (!dockingPath.getDock(0).equals(rootDock)) {
			return false;
		}

		// Find the path from the root dock that still exists.
		int dockIndex = 0;
		while (dockIndex < dockingPath.getDockCount()) {

			Dock currentDock = dockingPath.getDock(dockIndex);

			// Do we have a composite dock?
			if (currentDock instanceof CompositeDock) {
				CompositeDock compositeDock = (CompositeDock) currentDock;

				// Get the dock at the given position.
				boolean found = false;
				if (dockIndex < dockingPath.getDockCount() - 1) {
					Dock childDockToSearch = dockingPath.getDock(dockIndex + 1);
					for (int childIndex = 0; childIndex < compositeDock.getChildDockCount(); childIndex++) {
						Dock childDock = compositeDock.getChildDock(childIndex);
						if (childDock.equals(childDockToSearch)) {
							found = true;
							break;
						}
					}
				}

				if (found) {
					dockIndex++;
				} else {
					break;
				}
			} else if (currentDock instanceof LeafDock) {
				LeafDock leafDock = (LeafDock) currentDock;
				DockingManager.getDockingExecutor().changeDocking(dockable, leafDock, dockingPath.getPositionInDock(dockIndex));
				return true;

			}
		}

		Dock currentDock = dockingPath.getDock(dockIndex);
		if (currentDock instanceof CompositeDock) {
			Position currentPosition = dockingPath.getPositionInDock(dockIndex);
			CompositeDock compositeDock = (CompositeDock) currentDock;

			// Is there already a child dock at the current position?
			Dock currentPositionChildDock = null;
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				Dock childDock = compositeDock.getChildDock(index);
				Position childPosition = compositeDock.getChildDockPosition(childDock);
				if (childPosition.equals(currentPosition)) {
					currentPositionChildDock = childDock;
					break;
				}
			}

			// Control if there is no dock at the current position and the dock is not full.
			if ((currentPositionChildDock == null) && (!compositeDock.isFull())) {
				// Create a child dock and add the child dock at the current position.
				Dock childDock = compositeDock.getChildDockFactory().createDock(dockable, Integer.MAX_VALUE);
				if (childDock != null) {

					// Get the current dock of the dockable.
					LeafDock originDock = dockable.getDock();

					// Fire the event that the docking will change.
					DockableEvent dockingWillChangeEvent = new DockableEvent(this, originDock, compositeDock, dockable);
					dockable.fireDockingWillChange(dockingWillChangeEvent);

					// Do we have to remove the dockable?
					boolean removalSuccessFull = removeDockable(dockable);
					if (!removalSuccessFull) {
						return false;
					}

					// Add the dockable.
					boolean succes = childDock.addDockable(dockable, new Point(0, 0), new Point(0, 0));
					if (succes) {
						compositeDock.addChildDock(childDock, currentPosition);
						DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, compositeDock, dockable);
						dockable.fireDockingChanged(dockingChangedEvent);
						return true;
					} else {
						DockableEvent dockingChangedEvent = new DockableEvent(this, originDock, null, dockable);
						dockable.fireDockingChanged(dockingChangedEvent);
					}
				}
			}

			// Control if there is a dock at the current position.
			boolean succes = false;
			if (currentPositionChildDock != null) {
				succes = changeDocking(dockable, currentPositionChildDock);
			}

			Dock parentDock = compositeDock;
			while ((!succes) && (parentDock != null)) {
				succes = changeDocking(dockable, parentDock);
				parentDock = parentDock.getParentDock();
			}
			return succes;
		} else if (currentDock instanceof LeafDock) {
			LeafDock leafDock = (LeafDock) currentDock;
			DockingManager.getDockingExecutor().changeDocking(dockable, leafDock, dockingPath.getPositionInDock(dockIndex));
			return true;
		}

		return false;

	}

	/**
	 * The dock is cleaned up, if it is empty.
	 *
	 * @param    dock    The dock to clean. Not null.
	 * @param    ghost    When true, there can be busy listeners on the dock that may not be removed.
	 * The dock may not be completely deleted. When false, the dock may be deleted, if empty.
	 * @return A dock with ghosts. Null when there are no ghosts.
	 */
	public CompositeDock cleanDock(Dock dock, boolean ghost) {

		CompositeDock dockWithGhosts = null;
		if ((dock.isEmpty()) && (dock.getParentDock() != null)) {
			if (ghost) {
				dock.getParentDock().ghostChild(dock);
				dockWithGhosts = dock.getParentDock();
			} else {
				dock.getParentDock().emptyChild(dock);
			}
		}

		if (dock instanceof CompositeDock) {
			CompositeDock compositeDock = (CompositeDock) dock;
			List emptyChildren = new ArrayList();
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				Dock childDock = compositeDock.getChildDock(index);
				if (!hasDockables(childDock)) {
					emptyChildren.add(childDock);
				}
			}
			for (int index = 0; index < emptyChildren.size(); index++) {
				compositeDock.emptyChild((Dock) emptyChildren.get(index));
			}
		}

		// Adapt the size of the float child dock.
		Dock floatChildDock = DockingUtil.getFloatChildDock(dock);
		if (floatChildDock != null) {
			FloatDock floatDock = (FloatDock) floatChildDock.getParentDock();
			for (int index = 0; index < floatDock.getChildDockCount(); index++) {
				if (floatDock.getChildDock(index).equals(floatChildDock)) {
					Window window = SwingUtilities.getWindowAncestor((Component) floatChildDock);
					window.pack();
					break;
				}
			}

		}

		return dockWithGhosts;
	}

	// Private metods.

	/**
	 * Creates a docking path for the dockable and saves it in the docking path model.
	 * If the dockable is a composite, this is done for every child dockable.
	 *
	 * @param dockable The dockable whose docking path has to be saved.
	 */
	private void saveDockingPath(Dockable dockable) {

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;

			// Save every dockable in the composite.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				saveDockingPath(compositeDockable.getDockable(index));
			}

			return;
		}

		if (dockable.getDock() != null) {
			// Don't save the docking path when the dockable is externalized.
			if (dockable.getState() != DockableState.EXTERNALIZED) {
				// Get the dock model.
				DockModel dockModel = DockingManager.getDockModel();
				if (dockModel == null) {
					throw new NullPointerException("Dock model of docking manager null.");
				}
				// Get the docking path model.
				DockingPathModel dockingPathModel = DockingManager.getDockingPathModel();
				if (dockingPathModel == null) {
					throw new NullPointerException("Docking path model of docking manager null.");
				}

				// Create the docking path of the dockable.
				DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
				dockingPathModel.add(dockingPath);
			}

		}

	}

	/**
	 * Determines if the dock or one of its deeper child docks contains a dockable.
	 *
	 * @param dock The dock. Can be a leaf dock or a composite dock.
	 * @return True when the dock or one of its deeper child docks contains a dockable.
	 */
	private boolean hasDockables(Dock dock) {

		if (dock instanceof LeafDock) {
			LeafDock leafDock = (LeafDock) dock;
			return leafDock.getDockableCount() != 0;
		}

		if (dock instanceof CompositeDock) {
			CompositeDock compositeDock = (CompositeDock) dock;
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				if (hasDockables(compositeDock.getChildDock(index))) {
					return true;
				}
			}
			return false;
		}

		return false;
	}

	/**
	 * <p>
	 * Removes the specified dockable from its dock, if it is docked in a dock.
	 * It is possible that the dockable is a composite dockable and that the child dockables are
	 * docked in different docks. In that case all the child dockables are removed from their docks.
	 * The dockable is removed from the dock by using {@link LeafDock#removeDockable(Dockable)} of the dock.
	 * </p>
	 *
	 * @param dockable The dockable that will be removed.
	 * @return True if the specified dockable was removed from its dock, false otherwise.
	 * False if the dockable was not docked in a dock.
	 */
	private boolean removeDockable(Dockable dockable) {

		// Get the dock of the dockable.
		LeafDock originDock = dockable.getDock();
		if (originDock != null) {
			return removeSingleDockable(dockable);
		} else if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;
			boolean success = true;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				if (!removeDockable(compositeDockable.getDockable(index))) {
					success = false;
				}
			}
			return success;
		}

		return true;

	}

	/**
	 * Removes the dockable from its leaf dock.
	 * The dockable is removed from the dock by using {@link LeafDock#removeDockable(Dockable)} of the dock.
	 *
	 * @param dockable The dockable. It should be docked in a LeafDock.
	 * @return True if the removal was successful, false otherwise.
	 */
	private boolean removeSingleDockable(Dockable dockable) {

		// Check if the dockable is docked.
		LeafDock originDock = dockable.getDock();
		if (originDock == null) {
			throw new IllegalArgumentException("The dockable [" + dockable.getTitle() + "] is not docked. It should be docked in a LeafDock.");
		}

		// Save the docking path of the dockable.
		saveDockingPath(dockable);

		// Remove the dockable.
		return originDock.removeDockable(dockable);

	}
}

