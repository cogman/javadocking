package com.javadocking.dock.docker;

import com.javadocking.dockable.Dockable;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * An interface for an object that docks a {@link com.javadocking.dockable.Dockable}
 * in a {@link com.javadocking.dock.Dock}.
 * </p>
 * <p>
 * The implementations can decide by themselves which strategy they will use
 * to find or create a dock and to position the dockable in the dock.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface Docker {

	/**
	 * Docks the dockable in a {@link com.javadocking.dock.Dock}.
	 *
	 * @param dockable The dockable that has to be docked.
	 * @return True if the dockable could be docked, false otherwise.
	 */
	boolean dock(Dockable dockable);

	/**
	 * <p>
	 * Saves the properties of this docker in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this docker should start with the given prefix.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 */
	void saveProperties(String prefix, Properties properties);

	/**
	 * <p>
	 * Loads the properties for this docker. The properties can be found in the given properties object.
	 * The property names for this docker start with the given prefix.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the docker.
	 * Don't call this method for a visualizer that already has a content.
	 * </p>
	 *
	 * @param prefix       The prefix of the names of the properties that have been intended for this docker.
	 * @param properties   The properties object that contains the properties for this docker. It can contain also
	 *                     properties for other objects, but they will have another prefix.
	 * @param dockablesMap A mapping that contains the available dockables.
	 *                     <ul>
	 *                     <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                     <li>map value: the dockable ({@link Dockable}).</li>
	 *                     </ul>
	 * @throws IOException        If an error occures while decoding the properties.
	 */
	void loadProperties(String prefix, Properties properties, Map dockablesMap) throws IOException;

}
