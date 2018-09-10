package com.javadocking.drag.dockretriever;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.Priority;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockModelUtil;
import com.javadocking.util.CollectionUtil;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * This is the default dock retriever.
 * The dock is searched in the {@link com.javadocking.model.DockModel} of the {@link com.javadocking.DockingManager}.
 * First a dock is searched that has the same owner window as the dock where the dockable comes from.
 * If there is not such a dock under the mouse location, docks with other owner windows are tried.
 * </p>
 * <p>
 * The possible docks that are used in the dock model:
 * <ul>
 * <li>should inherit from the java.awt.Component class,
 * <li>or should be a {@link com.javadocking.dock.FloatDock}.
 * </ul>
 * Other type of docks will not be found by this retriever.
 *
 * @author Heidi Rakels.
 */
public class DynamicDockRetriever implements DockRetriever {

	/**
	 * The child of the root dock of the dock, where the dockable is currently docked.
	 */
	private Dock currentChildOfRootDock;
	/**
	 * The current dockable.
	 */
	private Dockable currentDockable;

	// Implementations of DockRetriever.

	public Dock[] retrieveHighestPriorityDock(Point screenLocation, Dockable dockable) {

		currentDockable = dockable;

		// Get the list with the docks under this position. The deepest docks first.
		DockPossibilities dockPossibilities = retrieveDocksOfPosition(screenLocation, dockable);

		// Did we find docks?
		if (dockPossibilities != null) {
			Dock preferenceDock = getHighestPriorityDock(screenLocation, dockable, dockPossibilities.preferenceDocks);
			Dock reserveDock = getHighestPriorityDock(screenLocation, dockable, dockPossibilities.reserveDocks);

			if ((preferenceDock == null) && (reserveDock == null)) {
				return null;
			}
			if ((preferenceDock != null) && (reserveDock != null)) {
				Dock[] docks = new Dock[2];
				docks[0] = preferenceDock;
				docks[1] = reserveDock;
				return docks;
			} else {
				Dock[] docks = new Dock[1];
				if (preferenceDock != null) {
					docks[0] = preferenceDock;
				} else {
					docks[0] = reserveDock;
				}
				return docks;
			}
		}

		// No dock found.
		return null;
	}

	// Private metods.

	private Dock getHighestPriorityDock(Point screenLocation, Dockable dockable, List possibleDocks) {
		// Did we find docks?
		if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
			// The dock that has the highest priority of the docks that are scanned already.
			Dock dockWithHighestPriority = null;
			int highestPriority = Priority.CANNOT_DOCK;

			// Get the first dock in the list with highest priority.
			// Iterate over the docks.
			for (Object possibleDock1 : possibleDocks) {
				Dock possibleDock = (Dock) possibleDock1;

				// Get the mouse location for the possible dock.
				Point relativeLocation = new Point(screenLocation.x, screenLocation.y);
				if (possibleDock instanceof Component) {
					SwingUtilities.convertPointFromScreen(relativeLocation, (Component) possibleDock);
				}

				// Get the priority for adding the dockable to this dock.
				int dockPriority = possibleDock.getDockPriority(dockable, relativeLocation);

				// Is this priority higher than the priorities we found already?
				if (dockPriority > highestPriority) {
					// This is the dock with the highest priority until now.
					dockWithHighestPriority = possibleDock;
					highestPriority = dockPriority;
				}
			}

			// The dock with the highest priority.
			return dockWithHighestPriority;
		}

		// No dock found.
		return null;

	}


	private DockPossibilities retrieveDocksOfPosition(Point screenLocation, Dockable dockable) {

		// TODO when a dock is floating, it cannot be docked in a dock of another owner window.

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// Get the owner window ID where this dockable is currently docked.
		Window currentOwner = null;
		if (dockable.getDock() != null) {
			Dock rootDock = DockingUtil.getRootDock(dockable.getDock());
			String rootDockKey = DockingUtil.getRootDockKey(rootDock);
			currentOwner = DockingUtil.getWindowOwner(rootDockKey);
		}

		// Try to find a non empty list of docks in the current owner window.
		if (currentOwner != null) {
			DockPossibilities possibleDocks = retrieveDocksOfPosition(screenLocation, currentOwner, dockable, true);
			if (possibleDocks != null) {
				return possibleDocks;
			}
		}

		// Iterate over the other owner windows until we find a non empty list of docks.
		for (int ownerIndex = 0; ownerIndex < dockModel.getOwnerCount(); ownerIndex++) {
			Window owner = dockModel.getOwner(ownerIndex);
			if (!owner.equals(currentOwner)) {
				DockPossibilities possibleDocks = retrieveDocksOfPosition(screenLocation, owner, dockable, false);
				if (possibleDocks != null) {
					return possibleDocks;
				}
			}
		}

		// Create a list with only the float dock of the owner window.
		// Check first if this dock may float.
		if ((dockable.getDockingModes() & DockingMode.FLOAT) != 0) {
			if (currentOwner == null) {
				if (dockModel.getOwnerCount() > 0) {
					currentOwner = dockModel.getOwner(0);
				}
			}
			if (currentOwner != null) {
				Set floatDocks = DockModelUtil.getVisibleFloatDocks(dockModel, currentOwner);
				if (floatDocks.size() != 0) {
					List docks = new ArrayList(floatDocks);
					DockPossibilities dockPossibilities = new DockPossibilities();
					dockPossibilities.preferenceDocks = docks;
					return dockPossibilities;
				}

				return null;
			}
		}

		// We couldn't find a dock.
		return null;
	}

	// Private metods.

	/**
	 * Retrieves the docks that are under the given screen location.
	 * The deeper docks are first in the list. Only docks of the given owner
	 * are added to the list.
	 *
	 * @param screenLocation The screen location where the docks are searched.
	 * @param ownerWindow    Only docks with this window as owner are added to the list.
	 * @param dockable       The dockable that has to be added to a new dock.
	 * @param inOwner        The owner window is the owner of the dockable.
	 * @return The list of docks under the given screen location. The deepest docks are first.
	 */
	private DockPossibilities retrieveDocksOfPosition(Point screenLocation, Window ownerWindow, Dockable dockable, boolean inOwner) {

		// Create the result.
		DockPossibilities dockPossibilities = new DockPossibilities();

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		Set floatDocks = DockModelUtil.getVisibleFloatDocks(dockModel, ownerWindow);
		for (Object floatDock1 : floatDocks) {
			FloatDock floatDock = (FloatDock) floatDock1;

			// Iterate first over the children of the float dock. These children are the floating windows.
			// Check first if this dock may float.
			if ((dockable.getDockingModes() & DockingMode.FLOAT) != 0) {

				for (int index = 0; index < floatDock.getChildDockCount(); index++) {
					Dock childDock = floatDock.getChildDock(index);

					// Retrieve the possible docks for this child dock.
					List possibleDocks = retrieveDocksOfPosition(screenLocation, childDock);

					// Did we find docks to add the dockable?
					if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
						// Add also the float dock as last possible dock.
						possibleDocks.add(floatDock);


						// Is the dockable floating alone?
						if (inOwner && isFloating()) {
							if (childDock.equals(currentChildOfRootDock)) {
								dockPossibilities.reserveDocks = possibleDocks;
								// Continue.
							} else {
								dockPossibilities.preferenceDocks = possibleDocks;
								return dockPossibilities;
							}
						} else {
							dockPossibilities.preferenceDocks = possibleDocks;
							return dockPossibilities;
						}
					}
				}
			}
		}

		// Iterate over the root docks of the owner that are not the float dock.
		Iterator dockIdIterator = dockModel.getRootKeys(ownerWindow);
		List listWithPossibleDocksLists = new ArrayList();
		while (dockIdIterator.hasNext()) {
			// Get the next root dock ID and verify it is not the ID of the float dock.
			String rootDockId = (String) dockIdIterator.next();
			Dock rootDock = dockModel.getRootDock(rootDockId);
			if (rootDock instanceof Component) {
				// Check if the root dock is visible.
				boolean visible = SwingUtil.locationInComponentVisible(screenLocation, (Component) rootDock);
				if (visible) {
					// Retrieve the possible docks for this root dock.
					List possibleDocks = retrieveDocksOfPosition(screenLocation, rootDock);

					// Did we find docks to add the dockable?
					if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
						// Stop searching. Add also the float dock as last possible dock.
						possibleDocks.addAll(floatDocks);

						// Add the list of docks.
						listWithPossibleDocksLists.add(possibleDocks);
//						dockPossibilities.preferenceDocks = possibleDocks;
//						return dockPossibilities;
					}
				}
			}
		}

		// Did we find exactly one list?
		if (listWithPossibleDocksLists.size() == 1) {
			dockPossibilities.preferenceDocks = (List) listWithPossibleDocksLists.get(0);
			return dockPossibilities;
		}

		// Did we find more lists?
		if (listWithPossibleDocksLists.size() > 0) {
			// Search the list with the deepest components.
			List result = (List) listWithPossibleDocksLists.get(0);
			Component resultComponent = (Component) result.get(0);
			int nextListIndex = 1;
			while (nextListIndex < listWithPossibleDocksLists.size()) {
				List nextList = (List) listWithPossibleDocksLists.get(nextListIndex);
				Component nextListComponent = (Component) nextList.get(0);
				if (SwingUtilities.isDescendingFrom(nextListComponent, resultComponent)) {
					result = nextList;
					resultComponent = nextListComponent;
				}
				nextListIndex++;
			}
			dockPossibilities.preferenceDocks = result;
			return dockPossibilities;
		}


		// Do we have reserve docks?
		if (dockPossibilities.reserveDocks != null) {
			// Try to add the float dock.
			if (floatDocks.size() > 0) {
				List possibleDocks = new ArrayList(floatDocks);
				dockPossibilities.preferenceDocks = possibleDocks;
			}
			return dockPossibilities;
		}

		return null;
	}

	/**
	 * Retrieves the docks that are under the given screen location.
	 * The deeper docks are first in the list. Only child docks of the given root dock or the root dock itself
	 * are added to the list.
	 *
	 * @param screenLocation The screen location where the docks are searched.
	 * @param rootDock       Only child docks of this dock or the dock itself are added to the list.
	 * @return The list of docks under the given screen location. The deepest docks are first.
	 */
	private List retrieveDocksOfPosition(Point screenLocation, Dock rootDock) {
		// Get the relative mouse location.
		Point rootLocation = new Point(screenLocation.x, screenLocation.y);
		SwingUtilities.convertPointFromScreen(rootLocation, (Component) rootDock);

		// Get the deepest child under this location, if it exists.
		Component component = SwingUtilities.getDeepestComponentAt((Component) rootDock, rootLocation.x, rootLocation.y);
		if (component != null) {
			// Return the ancestors of type Dock.
			return getDockAncestors(component);
		}

		return null;
	}

	/**
	 * Creates the list with the dock components that contain the given component. Only the components of type
	 * {@link Dock} are added to the list. The deepest docks are first in the list. The given component is also
	 * added to the list if its a dock.
	 *
	 * @param component The component of which the dock ancestors are searched.
	 * @return The list with the components of type {@link Dock} that contain the given component.
	 */
	private List getDockAncestors(Component component) {
		// Create the list for the ancestors.
		List dockAncestors = new ArrayList();

		// Add the component itself if it is a Dock class.
		if (component instanceof Dock) {
			dockAncestors.add(component);
		}

		// Get the first ancestor of type Dock.
		Component dockAncestor = SwingUtilities.getAncestorOfClass(Dock.class, component);
		while (dockAncestor != null) {
			// Add to the list of ancestors.
			dockAncestors.add(dockAncestor);

			// Get the next ancestor of type Dock.
			dockAncestor = SwingUtilities.getAncestorOfClass(Dock.class, dockAncestor);
		}

		return dockAncestors;
	}

	/**
	 * Determines if the dragged dockable is currently floating.
	 * It is floating, when its root dock is a {@link FloatDock} and
	 * if the dragged dockable is the only dockable in the child docks of the float dock.
	 *
	 * @return True if the dragged dockable is currently floating, false otherwise.
	 */
	private boolean isFloating() {

		// Get the root dock and the dock under the root.
		/**
		 * The root dock of the dock, where the dockable is currently docked.
		 */
		Dock currentRootDock = currentDockable.getDock();
		currentChildOfRootDock = null;
		while (currentRootDock.getParentDock() != null) {
			currentChildOfRootDock = currentRootDock;
			currentRootDock = currentRootDock.getParentDock();
		}

		// Is the root dock the float dock?
		if (currentRootDock instanceof FloatDock) {
			// Is the dockable already in this dock and are there no others?
			List childrenOfDockable = new ArrayList();
			List childrenOfDock = new ArrayList();
			DockingUtil.retrieveDockables(currentDockable, childrenOfDockable);
			DockingUtil.retrieveDockables(currentChildOfRootDock, childrenOfDock);
			if (CollectionUtil.sameElements(childrenOfDockable, childrenOfDock)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * This class keeps 2 lists with docks, where a dockable can be docked.
	 * There is a list with the preference docks.
	 * There is another list with the docks, that will be used if the preference docks
	 * cannot be used.
	 *
	 * @author Heidi Rakels.
	 */
	private static class DockPossibilities {
		List preferenceDocks;
		List reserveDocks;
	}

}
