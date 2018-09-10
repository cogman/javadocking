package com.javadocking.model;

import com.javadocking.dock.Dock;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This is an interface for a collection of {@link com.javadocking.model.DockingPath}s
 * that are used in an application.
 * </p>
 * <p>
 * Information on using docking path models is in
 * <a href="http://www.javadocking.com/developerguide/dockingpath.html" target="_blank">How to Use Docking Paths</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * The docking path model should be given to the docking manager with the method:
 * {@link com.javadocking.DockingManager#setDockingPathModel(DockingPathModel)}.
 * </p>
 * <p>
 * All the docking paths should have a different ID. If a docking path is added, that has an
 * ID for which there is already a docking path. Then the existing docking path is replaced
 * by the new one.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockingPathModel {

	/**
	 * Adds the given docking path to the model. If there is already a docking path
	 * in the model, with the same ID, then the existing docking path is first removed.
	 *
	 * @param dockingPath The docking path that is added to the model.
	 */
	void add(DockingPath dockingPath);

	/**
	 * Gets the docking path of this model that has the given id as ID.
	 *
	 * @param id The ID of the docking path that is retrieved.
	 * @return The docking path of this model that has the given id as ID.
	 * If there is no docking path for this id, null is returned.
	 */
	DockingPath getDockingPath(String id);

	/**
	 * Gets an iterator that iterates over the IDs of all the docking paths of the model.
	 *
	 * @return An iterator that iterates over the IDs of the docking paths.
	 * The entries of the iterator are java.lang.String objects.
	 */
	Iterator getIDs();

	/**
	 * Removes the given docking path from the model.
	 *
	 * @param dockingPath The docking path that is removed from the model.
	 */
	void remove(DockingPath dockingPath);

	/**
	 * <p>
	 * Loads the properties for this docking path model. The properties can be found in the given properties object
	 * and the property names for this dock model start with the given prefix.
	 * </p>
	 * <p>
	 * The docks that should be used inside this docking path can be found in the given docks mapping.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the docking path model. Don't call
	 * this method for a docking path model that already has a content.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names that have been intended for this docking path model.
	 * @param properties The properties object that contains the properties for this docking path model. It can contain also
	 *                   properties for other objects, but they will have another prefix.
	 * @param    docks                A mapping between the dock keys of the docks that are already loaded and the docks.
	 * <ul>
	 * <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 * <li>map value: a dock that is already loaded ({@link Dock}).</li>
	 * </ul>
	 */
	void loadProperties(String prefix, Properties properties, Map docks);

	/**
	 * <p>
	 * Saves the properties of this docking path model in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this docking path model should start with the given prefix.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 * @param dockKeys   A mapping between the docks that are already saved and the keys that are used for the save.
	 *                   <ul>
	 *                   <li>map key: a dock that is already saved ({@link Dock}).</li>
	 *                   <li>map value: the key that is used for saving the dock (java.lang.String).</li>
	 *                   </ul>
	 */
	void saveProperties(String prefix, Properties properties, Map dockKeys);

}
