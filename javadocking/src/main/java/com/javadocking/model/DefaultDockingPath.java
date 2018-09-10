package com.javadocking.model;

import com.javadocking.dock.CompositeDock;
import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This is the default implementation for a docking path.
 *
 * @author Heidi Rakels.
 */
public class DefaultDockingPath implements DockingPath {

	// Fields.

	/**
	 * The ID of the docking path.
	 */
	private String id;
	/**
	 * The key of the root dock of this path.
	 */
	private String rootDockKey;
	/**
	 * The docks of the path. The dock with index 0 is the root dock.
	 */
	private Dock[] docks;
	/**
	 * The positions of the docks or the in this path.
	 * The last position can be the position of a dockable in a leaf dock.
	 */
	private Position[] positions;

	// Constructors.

	/**
	 * Constructs a docking path, which is not initialized.
	 */
	public DefaultDockingPath() {
	}

	/**
	 * Constructs a docking path with the given properties.
	 *
	 * @param id          The ID of the docking path.
	 * @param rootDockKey The key of the root dock of this path.
	 * @param docks       The docks of the path. The dock with index 0 is the root dock. Cannot be null.
	 * @param positions   The positions of the docks or the in this path. Cannot be null.
	 *                    The last position can be the position of a dockable in a leaf dock.
	 * @throws IllegalArgumentException    If the lengths of the given array of docks and the given array
	 * of positions are different.
	 * @throws NullPointerException        If one of the docks or one of the positions in the array is null.
	 */
	public DefaultDockingPath(String id, String rootDockKey, Dock[] docks, Position[] positions) {

		// Check the arguments.
		if (docks == null) {
			throw new NullPointerException("Docks null.");
		}
		if (positions == null) {
			throw new NullPointerException("Positions null.");
		}
		if (docks.length == 0) {
			throw new IllegalArgumentException("A docking path has at least one roo dock.");
		}
		if (docks.length != positions.length) {
			throw new IllegalArgumentException("Different number of docks and positions.");
		}
		for (int index = 0; index < docks.length; index++) {
			if (docks[index] == null) {
				throw new NullPointerException("Dock with index [" + index + "] is null. ");
			}
			if (positions[index] == null) {
				throw new NullPointerException("Position with index [" + index + "] is null. ");
			}
		}

		this.id = id;
		this.rootDockKey = rootDockKey;
		this.docks = docks;
		this.positions = positions;

	}

	// Public static methods.

	/**
	 * Creates a docking path with the information how the given dockable is docked now in the dock model of the docking manager.
	 * The ID of the docking path is the ID of the dockable.
	 *
	 * @throws IllegalArgumentException If the given dockable is a composite dockable.
	 * @param    dockable    The dockable for which a docking path is created about its current position in a dock model.
	 * This may not be a composite dockable.
	 * @return The docking path with the information how the given dockable is docked now in the dock model.
	 * @throws IllegalArgumentException    If the dockable is not docked in a dock, or if the dockable does not belong
	 * to the given dock model.
	 */
	public static DefaultDockingPath createDockingPath(Dockable dockable) {

		// Check that the dockable is not a composite.
		if (dockable instanceof CompositeDockable) {
			throw new IllegalArgumentException("Can not create a docking path for a composite dockable.");
		}

		// Construct the path object.
		DefaultDockingPath dockingPath = new DefaultDockingPath();

		// Set the ID of the path and the ID.
		dockingPath.id = dockable.getID();

		// Create the lists for the docks and the positions.
		List positionsList = new ArrayList();
		List docksList = new ArrayList();

		// Add the position of the dockable in its leaf dock.
		LeafDock leafDock = dockable.getDock();
		if (leafDock == null) {
			throw new IllegalArgumentException("The dockable is not docked in a dock.");
		}
		docksList.add(0, leafDock);
		Position position = leafDock.getDockablePosition(dockable);
		positionsList.add(0, position);

		// Add the positions of the docks in its composite parent docks.
		Dock childDock = leafDock;
		CompositeDock parentDock = leafDock.getParentDock();
		while (parentDock != null) {
			// Add the dock and the position.
			docksList.add(0, parentDock);
			position = parentDock.getChildDockPosition(childDock);
			positionsList.add(0, position);

			// Go to the next level.
			childDock = parentDock;
			parentDock = childDock.getParentDock();
		}

		// Create the array with the docks.
		dockingPath.docks = new Dock[docksList.size()];
		dockingPath.docks = (Dock[]) docksList.toArray(dockingPath.docks);

		// Create the array with the positions.
		dockingPath.positions = new Position[positionsList.size()];
		dockingPath.positions = (Position[]) positionsList.toArray(dockingPath.positions);

		// Get the key of the root dock in the dock model.
		dockingPath.rootDockKey = DockingUtil.getRootDockKey(childDock);
		if (dockingPath.rootDockKey == null) {
			throw new IllegalArgumentException("The dockable is not docked in the given dock model.");
		}

		return dockingPath;

	}

	/**
	 * Creates a docking path for the given dockable, that is almost a copy of the given docking path.
	 * Only the ID of the docking path will be different.
	 *
	 * @throws IllegalArgumentException If one of the given dockables is a composite dockable.
	 * @param    dockableForPath        The dockable for which a docking path is created.
	 * This may not be a composite dockable.
	 * @param    dockingPathToCopy    The docking path that will be used to create a new path.
	 * @return The docking path with the information how the given dockable has to be docked in the dock model.
	 * @throws IllegalArgumentException    If there does not exist a path in the docking path model
	 * of the docking manager for the dockable whose path is used.
	 */
	public static DefaultDockingPath copyDockingPath(Dockable dockableForPath, DockingPath dockingPathToCopy) {

		// Check that the dockable is not a composite.
		if (dockableForPath instanceof CompositeDockable) {
			throw new IllegalArgumentException("Can not create a docking path for a composite dockable.");
		}

		return copyDockingPath(dockableForPath.getID(), dockingPathToCopy);

	}

	/**
	 * Creates a docking path for the given id, that is almost a copy of the given docking path.
	 * Only the ID of the docking path will be different.
	 *
	 * @throws IllegalArgumentException If one of the given dockables is a composite dockable.
	 * @param    id                    The id for the path.
	 * @param    dockingPathToCopy    The docking path that will be used to create a new path.
	 * @return The docking path with the information how the given dockable has to be docked in the dock model.
	 * @throws IllegalArgumentException    If there does not exist a path in the docking path model
	 * of the docking manager for the dockable whose path is used.
	 */
	public static DefaultDockingPath copyDockingPath(String id, DockingPath dockingPathToCopy) {
		// Check that the id is not null.
		if (id == null) {
			throw new NullPointerException("id");
		}
		// Check that the docking path is not null.
		if (dockingPathToCopy == null) {
			throw new NullPointerException("Could not find the docking path to copy in the docking path model.");
		}

		// Construct the path object.
		DefaultDockingPath dockingPath = new DefaultDockingPath();

		// Set the ID of the path.
		dockingPath.id = id;

		// Create the array with the docks.
		dockingPath.docks = new Dock[dockingPathToCopy.getDockCount()];
		for (int index = 0; index < dockingPathToCopy.getDockCount(); index++) {
			dockingPath.docks[index] = dockingPathToCopy.getDock(index);
		}

		// Create the array with the positions.
		dockingPath.positions = new Position[dockingPathToCopy.getDockCount()];
		for (int index = 0; index < dockingPathToCopy.getDockCount(); index++) {
			dockingPath.positions[index] = dockingPathToCopy.getPositionInDock(index);
		}


		// Get the key of the root dock in the dock model.
		dockingPath.rootDockKey = dockingPathToCopy.getRootDockKey();

		return dockingPath;

	}

	// Implementations of DockingPath.

	public String getID() {
		return id;
	}

	public String getRootDockKey() {
		return rootDockKey;
	}

	public Position getPositionInDock(int index) {
		return positions[index];
	}

	public Dock getDock(int index) {
		return docks[index];
	}

	public int getDockCount() {
		return docks.length;
	}

	public void saveProperties(String prefix, Properties properties, Map dockIds) {

		// Save the ID.
		PropertiesUtil.setString(properties, prefix + ".id", id);

		// Save the root dock key.
		PropertiesUtil.setString(properties, prefix + ".rootDockKey", rootDockKey);

		// Save the dock count.
		PropertiesUtil.setInteger(properties, prefix + ".dockCount", getDockCount());

		// Iterate over the docks.
		for (int index = 0; index < getDockCount(); index++) {

			// Save the dock ID.
			PropertiesUtil.setString(properties, prefix + ".dockId." + index + ".", (String) dockIds.get(getDock(index)));

			// Save the position in the dock.
			Position positionInDock = getPositionInDock(index);
			Position.setPositionProperty(properties, prefix + ".positionInDock." + index, positionInDock);
		}

	}

	public void loadProperties(String prefix, Properties properties, Map idDockMap) {

		// Load the ID.
		id = PropertiesUtil.getString(properties, prefix + ".id", id);

		// Load the root dock key.
		rootDockKey = PropertiesUtil.getString(properties, prefix + ".rootDockKey", rootDockKey);

		// Load the dock count.
		int count = PropertiesUtil.getInteger(properties, prefix + ".dockCount", 0);

		// The list with docks and positions.
		List docksList = new ArrayList();
		List positionsList = new ArrayList();

		// Iterate over the docks.
		for (int index = 0; index < count; index++) {
			// Load the dock ID.
			String dockId = PropertiesUtil.getString(properties, prefix + ".dockId." + index + ".", null);
			Dock dock = (Dock) idDockMap.get(dockId);

			// Load the position in the dock.
			Position position = Position.getPositionProperty(properties, prefix + ".positionInDock." + index, null);

			// Check that we arrive not in an illegal state.
			if (dock == null) {
				break;
			}
			if (position == null) {
				throw new IllegalStateException("Could not decode the position: index [" + index + "]");
			}

			// Add the dock and position.
			docksList.add(dock);
			positionsList.add(position);
		}

		// Fill the arrays of docks and posiions.
		docks = new Dock[docksList.size()];
		positions = new Position[docks.length];
		docks = (Dock[]) docksList.toArray(docks);
		positions = (Position[]) positionsList.toArray(positions);

	}

	// Overwritten methods.

	/**
	 * Returns true if the given object is a {@link DockingPath} with the same ID
	 * as this docking path.
	 *
	 * @param    object
	 * @return True if the given object is a {@link DockingPath} with the same ID
	 * as this docking path, false otherwise.
	 */
	public boolean equals(Object object) {

		if (!(object instanceof DockingPath)) {
			return false;
		}

		DockingPath other = (DockingPath) object;
		return this.getID().equals(other.getID());

	}

	public int hashCode() {
		return getID().hashCode();
	}
}
