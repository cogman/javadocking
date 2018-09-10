package com.javadocking.dock.docker;

import com.javadocking.DockingManager;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.javadocking.dock.Priority;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.PropertiesUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * This docker tries to dock the {@link com.javadocking.dockable.Dockable}
 * in the borders of an {@link com.javadocking.dock.BorderDock}.
 * If the dockables that are docked by this docker are moved to other docks,
 * it tries to add the dockables to those other docks.
 *
 * @author Heidi Rakels.
 */
public class BorderDocker implements Docker {

	private static final int[] DEFAULT_CHILD_POSITIONS;
	private static final Point DEFAULT_LOCATION = new Point(0, 0);


	static {
		DEFAULT_CHILD_POSITIONS = new int[3];
		DEFAULT_CHILD_POSITIONS[0] = Position.BOTTOM;
		DEFAULT_CHILD_POSITIONS[1] = Position.LEFT;
		DEFAULT_CHILD_POSITIONS[2] = Position.RIGHT;
	}

	/**
	 * The positions where the child docks with dockables will be first put.
	 */
	private int[] childPositions = DEFAULT_CHILD_POSITIONS;
	/**
	 * The border dock, where the dockables will be placed.
	 */
	private BorderDock borderDock;
	/**
	 * The last dockables that were docked by this object.
	 */
	private LastDockables lastDockables = new LastDockables();

	// Implementations of Docker.

	/**
	 * Adds the dockable to the border dock.
	 *
	 * @throws NullPointerException If the border dock is null.
	 * @return True if the dockable was added, false otherwise.
	 */
	public boolean dock(Dockable dockable) {

		// Check if the border dock is set.
		if (borderDock == null) {
			throw new NullPointerException("Border dock null.");
		}

		// Get the dockable that was last docked by this object.
		Dockable lastDockedDockable = lastDockables.getLastValidDockable();
		if (lastDockedDockable != null) {
			Dock lastDock = lastDockedDockable.getDock();
			if (lastDock != null) {
				// Try to add the dockable to this dock.
				if (lastDock.getDockPriority(dockable, DEFAULT_LOCATION) != Priority.CANNOT_DOCK) {
					//boolean succes = lastDock.addDockable(dockable, DEFAULT_LOCATION, DEFAULT_LOCATION);
					boolean succes = DockingManager.getDockingExecutor().changeDocking(dockable, lastDock, DEFAULT_LOCATION, DEFAULT_LOCATION);
					if (succes) {
						lastDockables.add(dockable);
						return true;
					}
				}
			}
		}

		// Try to add the dockable to a child toolbar.
		Dock childDock = null;
		for (int index = 0; index < childPositions.length; index++) {
			Dock dockInPosition = borderDock.getChildDockOfPosition(childPositions[index]);
			if (dockInPosition != null) {
				childDock = dockInPosition;
				break;
			}
		}
		if (childDock != null) {
			boolean added = childDock.addDockable(dockable, new Point(), new Point());
			if (added) {
				lastDockables.add(dockable);
				return true;
			}
		}

		// Check if there is still free place in the border dock.
		if (!borderDock.isFull()) {
			for (int index = 0; index < childPositions.length; index++) {
				// Get the docking mode.
				int positionToAdd = childPositions[index];
				int dockingMode = borderDock.getDockingMode(positionToAdd);

				// Create a new child toolbar.
				childDock = borderDock.getChildDockFactory().createDock(dockable, dockingMode);
				if (childDock == null) {
					throw new IllegalStateException("Cannot create a child dock with the child dock factory for docking mode [" +
							DockingMode.getDescription(dockingMode) + "].");
				}
				boolean added = childDock.addDockable(dockable, new Point(), new Point());
				if (added) {
					borderDock.addChildDock(childDock, new Position(positionToAdd));
					lastDockables.add(dockable);
					return true;
				}
			}
		}

		return false;

	}

	public void saveProperties(String prefix, Properties properties) {

		// Save the child positions.
		PropertiesUtil.setIntegerArray(properties, prefix + "childPositions", childPositions);

		// Save the properties of the last dockables.
		lastDockables.saveProperties(prefix + "lastDockables.", properties);

	}

	public void loadProperties(String prefix, Properties properties, Map dockablesMap) throws IOException {

		// Load the child positions.
		childPositions = DEFAULT_CHILD_POSITIONS;
		childPositions = PropertiesUtil.getIntegerArray(properties, prefix + "childPositions", childPositions);

		// Load the properties of the last dockables.
		lastDockables.loadProperties(prefix + "lastDockables.", properties, dockablesMap);

	}

	// Getters / Setters.


	/**
	 * Gets the positions where the child docks with dockables will be first put.
	 *
	 * @return The positions where the child docks with dockables will be first put.
	 */
	public int[] getChildPositions() {
		return childPositions;
	}

	/**
	 * Sets the positions where the child docks with dockables will be first put.
	 * The possible values in the array are:
	 * <ul>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * <li>{@link Position#TOP}</li>
	 * </ul>
	 *
	 * @param childPositions The positions where the child docks with dockables will be first put.
	 */
	public void setChildPositions(int[] childPositions) {

		// Check if the positions are valid and make a copy of the positions.
		int[] childPositionsCopy = new int[childPositions.length];
		for (int index = 0; index < childPositions.length; index++) {
			int position = childPositions[index];
			if ((position != Position.BOTTOM) &&
					(position != Position.LEFT) &&
					(position != Position.RIGHT) &&
					(position != Position.TOP)) {
				throw new IllegalArgumentException("invalid child position at ndex [" + index + "].");
			}
			childPositionsCopy[index] = position;
		}

		this.childPositions = childPositionsCopy;

	}

	/**
	 * Gets the dock in which the dockables with the minimized headers are docked.
	 *
	 * @return The dock in which the dockables with the minimized headers are docked.
	 */
	public BorderDock getBorderDock() {
		return borderDock;
	}

	/**
	 * Sets the dock in which the dockables are docked.
	 *
	 * @param dock The dock in which the dockables are docked.
	 */
	public void setBorderDock(BorderDock dock) {
		this.borderDock = dock;
	}
}
