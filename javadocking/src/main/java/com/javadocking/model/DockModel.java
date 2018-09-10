package com.javadocking.model;

import com.javadocking.dock.Dock;
import com.javadocking.visualizer.Visualizer;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * A dock model represents a collection of docks that are used in an application.
 * </p>
 * <p>
 * Information on using dock models is in
 * <a href="http://www.javadocking.com/developerguide/dockmodel.html" target="_blank">How to Use Dock Models</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * The dock model should be given to the docking manager with the method:
 * {@link com.javadocking.DockingManager#setDockModel(DockModel)}.
 * </p>
 * <p>
 * This model is a collection of trees of docks: <ul>
 * <li>The deepest docks of every tree are {@link com.javadocking.dock.LeafDock}s.</li>
 * <li>The leaf docks contain the {@link com.javadocking.dockable.Dockable}s.</li>
 * <li>The ancestors of these leaf docks are {@link com.javadocking.dock.CompositeDock}s.</li>
 * <li>The composite docks whose parents are null, are the root docks.</li>
 * </ul>
 *
 * <p>
 * This dock model contains the root docks. All docks of the model have one of the root docks as root.
 * Every root dock of the model can be retrieved with a key.
 * </p>
 * <p>
 * For every root dock the owner window has to be specified.
 * If the dock is a java.awt.Component, then the root dock should be its window ancestor.
 * If the dock is a {@link com.javadocking.dock.FloatDock}, then the owner is the window that will own
 * the floating windows.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockModel {

	// Interface methods.

	/**
	 * Adds the specified window as owner to this dock model.
	 *
	 * @param ownerId The ID for the owner. All the owner window IDs have to be different.
	 * @param window  The owner window.
	 * @throws IllegalArgumentException If there is already a window with this owner ID.
	 */
	public void addOwner(String ownerId, Window window);

	/**
	 * Gets the ID of the given owner window.
	 *
	 * @return The ID of the given owner window.
	 */
	public String getOwnerID(Window ownerWindow);

	/**
	 * Gets the number of owner windows in this dock model.
	 *
	 * @return The number of owner windows in this dock model.
	 */
	public int getOwnerCount();

	/**
	 * Gets the owner window with the specified index in the dock model.
	 * The windows with a lower index are more to the front.
	 *
	 * @param index The index of the owner window to retrieve.
	 * @return The owner with the specified index in the dock model.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getOwnerCount()).
	 */
	public Window getOwner(int index);

	/**
	 * Removes the specified window as owner from this dock model.
	 * The root docks that correspond with this owner are also removed.
	 *
	 * @param owner The owner window to be removed.
	 * @throws IllegalArgumentException If the given window is not an owner window.
	 */
	public void removeOwner(Window owner);

	/**
	 * Adds a root dock with its key to this dock model.
	 *
	 * @param rootKey The key for the root dock. These keys should be different for all root docks and visualizers.
	 * @param dock    The root dock that is added.
	 * @param    owner The window that owns this dock.
	 * @throws IllegalArgumentException    If the specified owner is not an owner window of this model.
	 * @throws IllegalArgumentException    If the specified dock is already a root dock in the model.
	 * @throws IllegalArgumentException    If the specified dock is not a root dock.
	 */
	public void addRootDock(String rootKey, Dock dock, Window owner);

	/**
	 * Gets an iterator that iterates over the keys of all the root docks of the given owner window.
	 *
	 * @return An iterator that iterates over the keys of all the root docks of the given owner window.
	 * The entries of the iterator are java.lang.String objects.
	 */
	public Iterator getRootKeys(Window owner);

	/**
	 * Gets the root dock of this dock model that has the given key associated to it.
	 *
	 * @param rootKey The key of the root dock that is retrieved.
	 * @return The root dock of this dock model that has the given key associated to it.
	 * If there is no root dock for this key, null is returned.
	 */
	public Dock getRootDock(String rootKey);

	/**
	 * Removes the root dock from the dock model.
	 *
	 * @param dock The root dock to remove.
	 * @throws IllegalArgumentException    If the specified dock is not a root dock of this dock model.
	 */
	public void removeRootDock(Dock dock);

	/**
	 * Adds a visualizer with its key to this dock model.
	 *
	 * @param key        The key for the visualizer. These keys should be different for all root docks and visualizers.
	 * @param visualizer The visualizer that is added.
	 * @param    owner The window that owns this visualizer.
	 * @throws IllegalArgumentException    If the specified owner is not an owner window of this model.
	 * @throws IllegalArgumentException    If the specified visualizer is already a visualizer in the model.
	 */
	public void addVisualizer(String key, Visualizer visualizer, Window owner);

	/**
	 * Gets an iterator that iterates over the keys of all the visualizers of the given owner window.
	 *
	 * @return An iterator that iterates over the keys of all the visualizers of the given owner window.
	 * The entries of the iterator are java.lang.String objects.
	 */
	public Iterator getVisualizerKeys(Window owner);

	/**
	 * Gets the visualizers of this dock model that has the given key associated to it.
	 *
	 * @param key The key of the visualier that is retrieved.
	 * @return The visualizer of this dock model that has the given key associated to it.
	 * If there is no visualier for this key, null is returned.
	 */
	public Visualizer getVisualizer(String key);

	/**
	 * Removes the visualizer from the dock model.
	 *
	 * @param visualizer The visualizer to remove.
	 * @throws IllegalArgumentException    If the specified visualizer is not a visualizer of this dock model.
	 */
	public void removeVisualizer(Visualizer visualizer);

	/**
	 * Gets the keys of the root docks that are float dock of the given owner window.
	 *
	 * @param owner The owner window of the float docks.
	 * @return The keys of the root docks that are float docks of the given owner window.
	 * If there are no keys, an empty set is returned.
	 */
	public Set getFloatDockKeys(Window owner);

	/**
	 * Gets the name of the data source for this dock model; typically a file name or a URL.
	 *
	 * @return The name of the data source for this dock model; typically a file name or a URL.
	 */
	public String getSource();


	/**
	 * <p>
	 * Loads the properties for this dock model. The properties can be found in the given properties object
	 * and the property names for this dock model start with the given prefix.
	 * </p>
	 * <p>
	 * The dockables that should be docked in this dock model are added to this dock model.
	 * They can be found in the given dockables mapping.
	 * The owner windows with their IDs can be found in the given owners mapping.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the dock model. Don't call
	 * this method for a dock model that already has a content.
	 * </p>
	 *
	 * @param sourceName   The name of a data source; typically a file name or a URL.
	 * @param prefix       The prefix for the property names.
	 * @param properties   The properties object that contains the properties for this dock model. It can contain also
	 *                     properties for other objects, but they will have another prefix.
	 * @param dockablesMap A map with the dockables for the model.
	 *                     <ul>
	 *                     <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                     <li>map value: the dockable ({@link com.javadocking.dockable.Dockable}).</li>
	 *                     </ul>
	 * @param ownersMap    A map with the owner windows.
	 *                     <ul>
	 *                     <li>map key: the ID of the owner window (java.lang.String).</li>
	 *                     <li>map value: the owner window (java.awt.window).</li>
	 *                     </ul>
	 * @throws IOException If an error occurs while decoding the data.
	 * @param    docksMap        The decoded docks should be added to this map. The keys are the keys that were used for encoding the docks.
	 * <ul>
	 * <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 * <li>map value: a dock that is already loaded ({@link Dock}).</li>
	 * </ul>
	 * @param    visualizersMap    A map with the visualizers.
	 * <ul>
	 * <li>map key: the key of the visualizer (java.lang.String).</li>
	 * <li>map value: the visualizer (java.awt.window).</li>
	 * </ul>
	 */
	public void loadProperties(String sourceName, String prefix, Properties properties, Map dockablesMap, Map ownersMap, Map docksMap, Map visualizersMap) throws IOException;

	/**
	 * <p>
	 * Saves the properties of this dock model in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this dock model should start with the given prefix.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 * @param dockKeys   A mapping between the docks that are already saved and the keys that are used for the save.
	 *                   The encoded docks should be added to this map.
	 *                   <ul>
	 *                   <li>map key: a dock that is already saved ({@link Dock}).</li>
	 *                   <li>map value: the key that is used for saving the dock (java.lang.String).</li>
	 *                   </ul>
	 */
	public void saveProperties(String prefix, Properties properties, Map dockKeys);

}
