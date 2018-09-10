package com.javadocking.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * This is an implementation for {@link DockingPathModel} that uses a java.util.HashMap.
 *
 * @author Heidi Rakels.
 */
public class DefaultDockingPathModel implements DockingPathModel {

	// Fields.

	public Map dockingPaths = new HashMap();

	// Implementations of DockingPathModel.


	public void add(DockingPath dockingPath) {
		dockingPaths.put(dockingPath.getID(), dockingPath);
	}

	public Iterator getIDs() {
		return dockingPaths.keySet().iterator();
	}

	public DockingPath getDockingPath(String id) {
		return (DockingPath) dockingPaths.get(id);
	}

	public void remove(DockingPath dockingPath) {
		dockingPaths.remove(dockingPath.getID());
	}

	public void loadProperties(String prefix, Properties properties, Map docks) {
		DockingPathModelPropertiesUtil.loadDockingProperties(this, prefix, properties, docks);
	}

	public void saveProperties(String prefix, Properties properties, Map dockKeys) {
		DockingPathModelPropertiesUtil.saveDockingProperties(this, prefix, properties, dockKeys);
	}

}
