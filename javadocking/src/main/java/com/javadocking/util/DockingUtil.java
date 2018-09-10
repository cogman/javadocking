package com.javadocking.util;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dockable.*;
import com.javadocking.model.DockModel;
import com.javadocking.visualizer.Visualizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains a collection of static utility methods for docking.
 *
 * @author Heidi Rakels.
 */
public class DockingUtil {

	private DockingUtil() {
	}

	/**
	 * Determines if the given dock is a root dock of the dock model.
	 *
	 * @param dockModel The dock model.
	 * @param rootDock  The dock.
	 * @return True if the given dock is a root dock of the dock model, false otherwise.
	 */
	public static boolean containsRootDock(DockModel dockModel, Dock rootDock) {
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);
			Iterator iterator = dockModel.getRootKeys(owner);
			while (iterator.hasNext()) {
				String rootKey = (String) iterator.next();
				Dock dock = dockModel.getRootDock(rootKey);
				if (rootDock.equals(dock)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if the given dockable is docked in the given leaf dock.
	 *
	 * @param dockabe The dockable. May be a {@link CompositeDockable}, even a deeper nested.
	 * @param    leafDock    The leaf dock.
	 * @return True if the given dockable is docked in this leaf dock, false otherwise.
	 * If the given dockable is a composite dockable, all of its child dockables
	 * have to be docked in the leaf dock, otherwise false is returned.
	 */
	public static boolean contains(LeafDock leafDock, Dockable dockabe) {

		// Is the dockable a composite?
		if (dockabe instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockabe;
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				if (contains(leafDock, compositeDockable.getDockable(index))) {
					return true;
				}
			}
			return false;
		}

		// Iterate over the dockables of the leaf dock.
		for (int index = 0; index < leafDock.getDockableCount(); index++) {
			if (leafDock.getDockable(index).equals(dockabe)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the key of the root dock that is a {@link FloatDock} of the given owner window in the dock model.
	 *
	 * @param    owner            The owner of the float dock in the dock model model.
	 * @return The root dock that is a float dock of the given owner window.
	 * If there is no float root dock for the given owner, null is returned.
	 */
	public static String getFloatRootDockKey(Window owner) {

		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// Iterate over the root docks of the given owner.
		Iterator rootDockKeyIterator = dockModel.getRootKeys(owner);
		while (rootDockKeyIterator.hasNext()) {
			String rootDockKey = (String) rootDockKeyIterator.next();

			// Get the root dock.
			Dock rootDock = dockModel.getRootDock(rootDockKey);
			if (rootDock instanceof FloatDock) {
				return rootDockKey;
			}
		}

		// No float dock could be find.
		return null;
	}

	/**
	 * Gets the dockable with the given ID, that is docked in a dock of the {@link DockModel}
	 * or that is visualized in a visualizer of the dock model.
	 *
	 * @param dockableID The ID of the dockable.
	 * @return The dockable with the given ID, that is docked in a dock of the dock model.
	 * Null if there is no dockable with the given ID.
	 */
	public static Dockable retrieveDockableOfDockModel(String dockableID) {

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}


		// Iterate over the owner windows.
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);

			// Iterate over the root docks.
			Iterator rootKeys = dockModel.getRootKeys(owner);
			while (rootKeys.hasNext()) {
				String rootKey = (String) rootKeys.next();
				Dock rootDock = dockModel.getRootDock(rootKey);

				// Retrieve all the dockables that are in a dock defined by this dock tree.
				List dockablesOfTree = new ArrayList();
				retrieveDockables(rootDock, dockablesOfTree);

				// Is there a dockable in the list with the given ID?
				for (Object aDockablesOfTree : dockablesOfTree) {
					Dockable dockableOfTree = (Dockable) aDockablesOfTree;
					if (dockableOfTree.getID().equals(dockableID)) {
						// Succes, we found the dockable with the given ID.
						return dockableOfTree;
					}
				}
			}

			// Iterate over the visualizers.
			Iterator visualizerKeys = dockModel.getVisualizerKeys(owner);
			while (visualizerKeys.hasNext()) {
				String visualizerKey = (String) visualizerKeys.next();
				Visualizer visualizer = dockModel.getVisualizer(visualizerKey);

				// Iterate over the dockables that are visualized in this visualizer.
				for (int dockableIndex = 0; dockableIndex < visualizer.getVisualizedDockableCount(); dockableIndex++) {
					Dockable dockableOfViualizer = visualizer.getVisualizedDockable(dockableIndex);
					if (dockableOfViualizer.getID().equals(dockableID)) {
						// Succes, we found the dockable with the given ID.
						return dockableOfViualizer;
					}
				}
			}
		}

		// We could not find the dockable in the dock model.
		return null;

	}

	/**
	 * Removes the empty child docks from the tree with the given dock as root. The empty children of deeper child docks
	 * are also removed. The dock has only children if it is a {@link CompositeDock}.
	 *
	 * @param compositeDock The dock from which the empty children will be removed.
	 */
	public static void removeEmptyChildren(CompositeDock compositeDock) {

		// Remove all the empty children of the composite child docks.
		for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
			Dock childDock = compositeDock.getChildDock(index);
			if (childDock instanceof CompositeDock) {
				removeEmptyChildren((CompositeDock) childDock);
			}
		}

		// Make a list of the empty child docks.
		List emptyChildDockList = new ArrayList();
		for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
			if (compositeDock.getChildDock(index).isEmpty()) {
				if ((!(compositeDock instanceof SplitDock) || (((SplitDock) compositeDock).isRemoveLastEmptyChild()))) {
					emptyChildDockList.add(compositeDock.getChildDock(index));
				}
			}
		}

		// Remove all the child docks that are empty.
		for (Object anEmptyChildDockList : emptyChildDockList) {
			compositeDock.emptyChild((Dock) anEmptyChildDockList);
		}

	}

	/**
	 * Tries to find the dock that contains the given dockable in the dock tree with the given dock as root.
	 *
	 * @param rootDock The root dock of the tree of docks where the dockable will be searched.
	 * @param dockable The dockable that is searched.
	 * @return The dock in the dock tree in which the given dockable is docked, if it exists, null otherwise.
	 */
	public static Dock searchDock(Dock rootDock, Dockable dockable) {
		// Do we have a leafdock?
		if (rootDock instanceof LeafDock) {
			// Is the given dockable one of the children?
			LeafDock leafDock = (LeafDock) rootDock;
			for (int index = 0; index < leafDock.getDockableCount(); index++) {
				if (dockable.equals(leafDock.getDockable(index))) {
					return leafDock;
				}
			}
		}

		// Do we have a composite dock?
		if (rootDock instanceof CompositeDock) {
			// Try to find the dockable in one of the children.
			CompositeDock compositeDock = (CompositeDock) rootDock;
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				// Do the search in the dock tree with this child as root.
				Dock searchedDock = searchDock(compositeDock.getChildDock(index), dockable);
				if (searchedDock != null) {
					// Succes.
					return searchedDock;
				}
			}
		}

		// We couldn't find the dock of the dockable.
		return null;
	}

	/**
	 * Retrieves the dockables that are contained by all the docks that are in the dock tree with the
	 * given dock as root dock. It adds this dockables to the given list with dockables.
	 *
	 * @param rootDock  The root dock of the dock tree.
	 * @param dockables The list to which all the dockables are added.
	 */
	public static void retrieveDockables(Dock rootDock, List dockables) {

		// Do we have a leaf dock?
		if (rootDock instanceof LeafDock) {
			LeafDock leafDock = (LeafDock) rootDock;

			// Add all the dockables of this leaf dock to the list of dockables.
			for (int index = 0; index < leafDock.getDockableCount(); index++) {
				dockables.add(leafDock.getDockable(index));
			}
		}

		// Do we have a composite dock?
		if (rootDock instanceof CompositeDock) {
			CompositeDock compositeDock = (CompositeDock) rootDock;

			// Add all the dockables of all the child docks.
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				// Add all the children of the child dock.
				retrieveDockables(compositeDock.getChildDock(index), dockables);
			}
		}
	}

	/**
	 * Retrieves all the leaf dockables of the dockable tree with the
	 * given dockable as root dockable. The retrieved dockables are added to the given list.
	 * The composite dockables are not added to the list.
	 *
	 * @param rootDockable The root dockable of the dockable tree.
	 * @param dockables    The list to which all the leaf dockables are added.
	 */
	public static void retrieveDockables(Dockable rootDockable, List dockables) {

		// Do we have a composite dockable?
		if (rootDockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) rootDockable;

			// Iterate over the child dockables.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Add all the children of the child dockable.
				retrieveDockables(compositeDockable.getDockable(index), dockables);

			}
		} else {
			// This is a leaf dockable. Add it.
			dockables.add(rootDockable);
		}
	}

	/**
	 * Gets the root dock of the dock tree that contains the given dock.
	 *
	 * @return The root dock of the dock tree that contains the given dock. If the parent of the given dock is null,
	 * the dock itself is returned.
	 * @param    dock    The given dock.
	 */
	public static Dock getRootDock(Dock dock) {

		// Get the parent of the dock.
		Dock parentDock = dock.getParentDock();

		// Return the dock if the parent is null.
		if (parentDock == null) {
			return dock;
		}

		// Get the root dock of the parent.
		return getRootDock(parentDock);

	}

	/**
	 * Searches the ancestor of the given dock that is the child of the root {@link FloatDock}.
	 * If the root dock of the given dock is not a float dock, null is returned.
	 *
	 * @return If the root dock of the given dock is not a float dock, null is returned.
	 * Otherwise, if the given dock is a child dock of the root dock, the given dock is returned.
	 * Otherwise, the ancestor of the given dock that is the child of the float dock is returned.
	 * @param    dock    The given dock.
	 */
	public static Dock getFloatChildDock(Dock dock) {

		if (dock == null) {
			return null;
		}

		// Get the parent of the dock.
		Dock parentDock = dock.getParentDock();

		// Return the dock if the parent is null.
		if (parentDock == null) {
			return null;
		} else if (parentDock instanceof FloatDock) {
			return dock;
		}

		// Get the root dock of the parent.
		return getFloatChildDock(parentDock);

	}

	/**
	 * <p>
	 * Creates one dockable with the dockables of a dock.
	 * </p>
	 * <p>
	 * there are the following possible results:
	 * <ul>
	 * <li>If the dock is a {@link LeafDock} with no children, null is returned.</li>
	 * <li>If the dock is a {@link LeafDock} with 1 child, this child is returned.</li>
	 * <li>If the dock is a {@link LeafDock} with more than 1 child, a {@link CompositeDockable} is created
	 * with the given dock as parent.</li>
	 * <li>If the dock is a {@link CompositeDock}, a {@link CompositeDockable} is created. If all
	 * the dockables have the same parent dock, this composite dockable will have this parent.
	 * Otherwise the parent will be set to null. </li>
	 * <li>If the dock is a {@link CompositeDock}, but there is no child dock that has dockables,
	 * in that case null is returned. </li>
	 * </ul>
	 *
	 * @param    dock    The dock for which a dockable is created.
	 * @return One dockable that contains the dockables of the given dock.
	 */
	public static Dockable createDockable(Dock dock) {

		// Do we have a composite dock?
		if (dock instanceof CompositeDock) {
			// Get the list with the deeper child dockables.
			List dockablesList = new ArrayList();
			retrieveDockables(dock, dockablesList);

			// Are there no dockables?
			if (dockablesList.size() == 0) {
				return null;
			}

			// Multiple child docks.
			Dockable[] dockables = new Dockable[dockablesList.size()];
			dockablesList.toArray(dockables);
			DefaultCompositeDockable compositeDockable = new DefaultCompositeDockable(dockables);

			// Set the parent dock.
			LeafDock parent = null;
			if (dockables.length > 0) {
				if (dockables[0].getDock() != null) {
					parent = dockables[0].getDock();
					for (int index = 1; index < dockables.length; index++) {
						if (!parent.equals(dockables[index].getDock())) {
							parent = null;
							break;
						}
					}
				}
			}
			compositeDockable.setState(DockableState.NORMAL, parent);

			return compositeDockable;

		} else if (dock instanceof LeafDock) {

			LeafDock leafDock = (LeafDock) dock;
			int childCount = leafDock.getDockableCount();

			// No child dockables.
			if (childCount == 0) {
				return null;
			}

			// One dockable.
			if (childCount == 1) {
				return leafDock.getDockable(0);
			}

			// Multiple child docks.
			Dockable[] dockables = new Dockable[childCount];
			for (int index = 0; index < childCount; index++) {
				dockables[index] = leafDock.getDockable(index);
			}
			CompositeDockable compositeDockable = new DefaultCompositeDockable(dockables);
			compositeDockable.setDock(leafDock);
			return compositeDockable;

		}

		return null;
	}

	/**
	 * <p>
	 * Computes the preferred size of a composite dockable.
	 * </p>
	 * <p>
	 * When the dockable is a composite dockable this size depends on the docking mode.
	 * <ul>
	 * <li>For docking mode DockingMode.TAB the size is the union of the composing dockebles preferred size.
	 * <li>For docking mode DockingMode.HORIZONTAL_LINE the size is the union of the composing dockables preferred size, when they are set in a horizontal line.
	 * <li>For docking mode DockingMode.VERTICAL_LINE the size is the union of the composing dockables preferred size, when they are set in a vertical line.
	 * <li>For docking mode DockingMode.GRID the size is the sum of the composing dockables preferred size, when they are set in a grid.
	 * </ul>
	 * <p>
	 *
	 * @param dockable    The dockable whose preferred size is computed.
	 * @param dockingMode This should be DockingMode.TAB, DockingMode.GRID, DockingMode.HORIZONTAL_LINE or DockingMode.VERTICAL_LINE.
	 * @return The preferred size of a dockable.
	 */
	public static Dimension getDockablePreferredSize(Dockable dockable, int dockingMode) {

		Dimension size = null;
		if (dockable.getContent() != null) {
			size = dockable.getContent().getPreferredSize();
		} else if (dockable instanceof CompositeDockable) {
			size = getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.TAB);
		}
		return size;

	}

	/**
	 * Computes the preferred size of a composite dockable. This size depends on the docking mode.
	 * <ul>
	 * <li>For docking mode DockingMode.TAB the size is the union of the composing dockebles preferred size.
	 * <li>For docking mode DockingMode.HORIZONTAL_LINE the size is the union of the composing dockables preferred size, when they are set in a horizontal line.
	 * <li>For docking mode DockingMode.VERTICAL_LINE the size is the union of the composing dockables preferred size, when they are set in a vertical line.
	 * <li>For docking mode DockingMode.GRID the size is the sum of the composing dockables preferred size, when they are set in a grid.
	 * </ul>
	 *
	 * @param compositeDockable The composite dockable whose preferred size is computed.
	 * @param dockingMode       This should be DockingMode.TAB, DockingMode.GRID, DockingMode.HORIZONTAL_LINE or DockingMode.VERTICAL_LINE.
	 * @return The preferred size of a composite dockable.
	 */
	public static Dimension getCompositeDockablePreferredSize(CompositeDockable compositeDockable, int dockingMode) {

		if (dockingMode == DockingMode.TAB) {
			// Compute the union of the preferred sizes of the children.
			Dimension unionSize = new Dimension(0, 0);
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Get the preferred size of the child.
				Dimension childSize = compositeDockable.getDockable(index).getContent().getPreferredSize();

				// Adjust the union size.
				unionSize.setSize(Math.max(unionSize.width, childSize.width), Math.max(unionSize.height, childSize.height));
			}

			return unionSize;
		}

		if (dockingMode == DockingMode.HORIZONTAL_LINE) {
			// Compute the union of the preferred sizes of the children.
			Dimension unionSize = new Dimension(0, 0);
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Get the preferred size of the child.
				Dimension childSize = compositeDockable.getDockable(index).getContent().getPreferredSize();

				// Adjust the union size.
				unionSize.setSize(unionSize.width + childSize.width, Math.max(unionSize.height, childSize.height));
			}

			return unionSize;
		}

		if (dockingMode == DockingMode.VERTICAL_LINE) {
			// Compute the union of the preferred sizes of the children.
			Dimension unionSize = new Dimension(0, 0);
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Get the preferred size of the child.
				Dimension childSize = compositeDockable.getDockable(index).getContent().getPreferredSize();

				// Adjust the union size.
				unionSize.setSize(Math.max(unionSize.width, childSize.width), unionSize.height + childSize.height);
			}

			return unionSize;
		}

		if (dockingMode == DockingMode.GRID) {
			// Calculate the number of columns.
			int columnCount = (int) Math.ceil(Math.sqrt((double) (compositeDockable.getDockableCount())));
			if (columnCount <= 0) {
				columnCount = 1;
			}

			// Compute the union of the preferred sizes of the children.
			Dimension unionSize = new Dimension(0, 0);
			Dimension lineUnionSize = new Dimension(0, 0);
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				// Are we at a new line?
				if (index % columnCount == 0) {
					// Adjust the total union size.
					unionSize.setSize(Math.max(unionSize.width, lineUnionSize.width), unionSize.height + lineUnionSize.height);
					lineUnionSize = new Dimension(0, 0);
				}

				// Get the preferred size of the child.
				Dimension childSize = compositeDockable.getDockable(index).getContent()
						.getPreferredSize();

				// Adjust the line union size.
				lineUnionSize.setSize(lineUnionSize.width + childSize.width, Math.max(lineUnionSize.height, childSize.height));
			}

			// Adjust the total union size.
			unionSize.setSize(Math.max(unionSize.width, lineUnionSize.width), unionSize.height + lineUnionSize.height);

			return unionSize;
		}


		throw new IllegalArgumentException("The docking mode [" + dockingMode + "] is not allowed.");
	}

	/**
	 * Gets the key of the given root dock in the dock model of the docking manager.
	 * Can be null, when the dock is used by a visualizer.
	 *
	 * @param rootDock A root dock of the dock model for which the key is searched.
	 * @return The key of the given root dock in the given dock model.
	 * @throws IllegalArgumentException    If the given dock is not a root dock.
	 */
	public static String getRootDockKey(Dock rootDock) {

		if (rootDock.getParentDock() != null) {
			throw new IllegalArgumentException("The dock is not a root dock. It's parent is not null.");
		}

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// Iterate over the owner IDs.
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);

			// Iterate over the root dock keys of the owner.
			Iterator rootDockKeyIterator = dockModel.getRootKeys(owner);
			while (rootDockKeyIterator.hasNext()) {
				String key = (String) rootDockKeyIterator.next();

				// Get the root dock for this key.
				Dock rootDockForKey = dockModel.getRootDock(key);

				// Is it the given dock?
				if (rootDockForKey.equals(rootDock)) {
					return key;
				}
			}
		}


		return null;

	}

	/**
	 * Gets the owner window that owns the root dock with the specified key.
	 *
	 * @param rootDockKey The key of the root dock for which the owner is retrieved.
	 * @return The owner window that owns the root dock with the specified key.
	 * Null when the key is not a key of a root dock.
	 */
	public static Window getWindowOwner(String rootDockKey) {

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}


		// Iterate over the owner IDs.
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);

			// Iterate over the root dock keys of the owner.
			Iterator rootDockKeyIterator = dockModel.getRootKeys(owner);
			while (rootDockKeyIterator.hasNext()) {
				// Is the root dock key the given root dock key?
				if (rootDockKeyIterator.next().equals(rootDockKey)) {
					return owner;
				}
			}
		}

		return null;

	}

	// Private constructor.

	/**
	 * Tries to find the dock in the dock tree with the given dock as root.
	 *
	 * @param rootDock The root dock of the tree of docks where the dock will be searched.
	 * @param dock     The dock that is searched.
	 * @return True if it exists in the dock tree, false otherwise.
	 */
	public static boolean containsDock(Dock rootDock, Dock dock) {

		// Do we have a leaf dock?
		if (rootDock instanceof LeafDock) {
			if (rootDock.equals(dock)) {
				return true;
			}
		}

		// Do we have a composite dock?
		if (rootDock instanceof CompositeDock) {

			// Is this root dock the searched dock.
			if (rootDock.equals(dock)) {
				return true;
			}

			// Try to find the dock in one of the child trees.
			CompositeDock compositeDock = (CompositeDock) rootDock;
			for (int index = 0; index < compositeDock.getChildDockCount(); index++) {
				// Do the search in the dock tree with this child as root.
				boolean found = containsDock(compositeDock.getChildDock(index), dock);
				if (found) {
					// Succes.
					return true;
				}
			}
		}

		// We couldn't find the dock of the dockable.
		return false;
	}
}
