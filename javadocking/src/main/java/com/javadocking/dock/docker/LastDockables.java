package com.javadocking.dock.docker;

import com.javadocking.dockable.Dockable;
import com.javadocking.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * This is a weak collection of dockables.
 * This means that dockables that are added to this class, will not be prevented from being
 * discarded by the garbage collector.
 * Dockables that don't have a dock any more, are not kept in the collection.
 * The last dockable that is added to this class, and that is still docked in a dock, can be retrieved.
 *
 * @author Heidi Rakels.
 */
class LastDockables {

	private static int counter = Integer.MIN_VALUE + 1;

	@NotNull
	private Map dockables = new WeakHashMap();

	/**
	 * Adds the dockable.
	 *
	 * @param dockable The dockable to add.
	 */
	public void add(Dockable dockable) {
		dockables.put(dockable, counter);

		if (counter < Integer.MAX_VALUE) {
			counter++;
		} else {
			counter = Integer.MIN_VALUE + 1;
		}
	}

	/**
	 * Gets the last dockable that was added and that still has a dock.
	 *
	 * @return The last dockable that was added and that still has a dock.
	 */
	@Nullable
	public Dockable getLastValidDockable() {

		// The list with dockables that can be removed, because they have no dock anymore.
		List dockablesToRemove = new ArrayList();
		Dockable lastDockable = null;
		Integer lastDockableValue = Integer.MIN_VALUE;

		// Iterate over all the dockables.
		for (Object o : dockables.keySet()) {
			Dockable dockable = (Dockable) o;
			if (dockable.getDock() == null) {
				// The dock of the dockable is null, so it can be removed.
				dockablesToRemove.add(dockable);
			} else if (lastDockableValue.compareTo((Integer) dockables.get(dockable)) <= 0) {
				// This dockable was added later.
				lastDockable = dockable;
				lastDockableValue = (Integer) dockables.get(dockable);
			}
		}

		// Remove the dockables, we don't need anymore.
		for (Object aDockablesToRemove : dockablesToRemove) {
			dockables.remove(aDockablesToRemove);
		}

		return lastDockable;

	}

	public void saveProperties(String prefix, @NotNull Properties properties) {

		// Save the dockables in the mapping.
		int count = 0;
		for (Object o : dockables.keySet()) {
			Dockable dockable = (Dockable) o;
			Integer value = (Integer) dockables.get(dockable);
			if (value != null) {
				PropertiesUtil.setString(properties, prefix + "dockable" + count, dockable.getID());
				PropertiesUtil.setInteger(properties, prefix + "value" + count, value);
				count++;
			}
		}

		// Save the number of dockables.
		PropertiesUtil.setInteger(properties, prefix + "count", count);

	}

	public void loadProperties(String prefix, @NotNull Properties properties, @NotNull Map dockablesMap) throws IOException {

		// Get the number of dockables.
		int count = 0;
		count = PropertiesUtil.getInteger(properties, prefix + "count", count);

		// Load the dockables.
		int maxValue = Integer.MIN_VALUE + 1;
		for (int index = 0; index < count; index++) {
			String id = PropertiesUtil.getString(properties, prefix + "dockable" + index, null);
			int value = PropertiesUtil.getInteger(properties, prefix + "value" + index, Integer.MIN_VALUE + 1);
			if (id != null) {
				Dockable dockable = (Dockable) dockablesMap.get(id);
				if (dockable != null) {
					dockables.put(dockable, value);
				}
				if (value > maxValue) {
					maxValue = value;
				}
			}
		}

		counter = maxValue + 1;

	}

	// Test.

//	public static void main(String[] args)
//	{
//		
//		Dockable dockable1 = new DefaultDockable("ID1", new JButton());
//		Dockable dockable2 = new DefaultDockable("ID2", new JButton());
//		Dockable dockable3 = new DefaultDockable("ID3", new JButton());
//		Dockable dockable4 = new DefaultDockable("ID4", new JButton());
//
//		LeafDock dock1 = new SingleDock();
//		LeafDock dock2 = new SingleDock();
//		LeafDock dock3 = new SingleDock();
//		LeafDock dock4 = new SingleDock();
//	
//		dockable1.setDock(dock1);
//		dockable2.setDock(dock2);
//		dockable3.setDock(dock3);
//		dockable4.setDock(dock4);
//		
//		LastDockables lastDockables = new LastDockables();
//		System.out.println(lastDockables.getLastValidDockable());
//		lastDockables.add(dockable1);
//		System.out.println(lastDockables.getLastValidDockable());
//		lastDockables.add(dockable2);
//		System.out.println(lastDockables.getLastValidDockable());
//		dockable2.setDock(null);
//		System.out.println(lastDockables.getLastValidDockable());
//		dockable1.setDock(null);
//		System.out.println(lastDockables.getLastValidDockable());
//		lastDockables.add(dockable3);
//		lastDockables.add(dockable4);
//		System.out.println(lastDockables.getLastValidDockable());
//		System.out.println("size " + lastDockables.dockables.size());
//
//	}

}
