package com.javadocking.drag.dockretriever;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.Priority;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockModelUtil;
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
public class StaticDockRetriever implements DockRetriever {

	// Static fields.

	private static final boolean TEST = false;

	// Implementations of DockRetriever.

	public Dock[] retrieveHighestPriorityDock(Point screenLocation, Dockable dockable) {

		// Get the list with the docks under this position. The deepest docks first.
		List possibleDocks = retrieveDocksOfPosition(screenLocation, dockable);

		// Did we find docks?
		if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
			// The dock that has the highest priority of the docks that are scanned already.
			Dock dockWithHighestPriority = null;
			int highestPriority = Priority.CANNOT_DOCK;

			// Get the first dock in the list with highest priority.
			// Iterate over the docks.
			for (int index = 0; index < possibleDocks.size(); index++) {
				Dock possibleDock = (Dock) possibleDocks.get(index);

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
			Dock[] docks = new Dock[1];
			docks[0] = dockWithHighestPriority;
			return docks;
		}

		// No dock found.
		return null;
	}

	// Private metods.

	private List retrieveDocksOfPosition(Point screenLocation, Dockable dockable) {

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
			List possibleDocks = retrieveDocksOfPosition(screenLocation, currentOwner, dockable);
			if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
				return possibleDocks;
			}
		}

		// Iterate over the other owner windows until we find a non empty list of docks.
		for (int ownerIndex = 0; ownerIndex < dockModel.getOwnerCount(); ownerIndex++) {
			Window owner = dockModel.getOwner(ownerIndex);
			if (!owner.equals(currentOwner)) {
				List possibleDocks = retrieveDocksOfPosition(screenLocation, owner, dockable);
				if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
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
					return docks;
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
	 * @return The list of docks under the given screen location. The deepest docks are first.
	 */
	private List retrieveDocksOfPosition(Point screenLocation, Window ownerWindow, Dockable dockable) {

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}


		// Get the float root dock.
		Set floatDocks = DockModelUtil.getVisibleFloatDocks(dockModel, ownerWindow);
		Iterator floatDockIterator = floatDocks.iterator();
		while (floatDockIterator.hasNext()) {
			FloatDock floatDock = (FloatDock) floatDockIterator.next();

			// Iterate first over the children of the float dock. These children are the floating windows.
			// Check first if this dock may float.
			if ((dockable.getDockingModes() & DockingMode.FLOAT) != 0) {

				for (int index = 0; index < floatDock.getChildDockCount(); index++) {
					Dock childDock = floatDock.getChildDock(index);

					// Retrieve the possible docks for this child dock.
					List possibleDocks = retrieveDocksOfPosition(screenLocation, childDock);

					// Did we find docks to add the dockable?
					if ((possibleDocks != null) && (possibleDocks.size() > 0)) {
						// Stop searching. Add also the float dock as last possible dock.
						possibleDocks.add(floatDock);

						// Return the list of docks.
						if (TEST) System.out.println("in float child");
						return possibleDocks;

					}
				}
			}
		}

		// Iterate over the root docks of the owner that are not the float dock.
		List listWithPossibleDocksLists = new ArrayList();
		Iterator dockIdIterator = dockModel.getRootKeys(ownerWindow);
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
					}
				}
			}
		}

		// Did we find exactly one list?
		if (listWithPossibleDocksLists.size() == 1) {
			return (List) listWithPossibleDocksLists.get(0);
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
			return result;
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
		Component dockAncestor = (Component) SwingUtilities.getAncestorOfClass(Dock.class, component);
		while (dockAncestor != null) {
			// Add to the list of ancestors.
			dockAncestors.add(dockAncestor);

			// Get the next ancestor of type Dock.
			dockAncestor = (Component) SwingUtilities.getAncestorOfClass(Dock.class, dockAncestor);
		}

		return dockAncestors;
	}

}
