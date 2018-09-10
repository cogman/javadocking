package com.javadocking.model;

import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.visualizer.Visualizer;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This is a basic dock model implementation.
 *
 * @author Heidi Rakels.
 */
public class DefaultDockModel implements DockModel {

	// Fields.

	/**
	 * The name of the data source for this dock model.
	 */
	private String source;

	// For the owner windows.
	/**
	 * The mapping between the owner windows and their IDs.
	 */
	private Map ownerIDsMap = new HashMap();
	/**
	 * The owner windows.
	 */
	private List owners = new ArrayList();

	// For the root docks.
	/**
	 * The mapping between keys and root docks.
	 */
	private Map rootDockKeys = new HashMap();
	/**
	 * The mapping between the owner keys and their set of root dock keys.
	 */
	private Map ownerRootDockKeys = new HashMap();
	/**
	 * The mapping between the owner windows and their window focus listeners.
	 */
	private Map ownerFocusListeners = new HashMap();

	// For the visualizers.
	/**
	 * The mapping between keys and visualizers.
	 */
	private Map visualizerKeys = new HashMap();
	/**
	 * The mapping between the owner keys and their set of visualizer keys.
	 */
	private Map ownerVisualizerKeys = new HashMap();


	// Other properties.
	/**
	 * When true, the position and size of every owner window of a dock model are decoded and
	 * the properties are set on the owner window of the dock model.
	 */
	private boolean loadOwnerRectangle = true;

	// Constructors.

	/**
	 * Constructs a dock model.
	 */
	public DefaultDockModel() {
	}

	/**
	 * Constructs a dock model with the given source.
	 *
	 * @param    source        The name of the data source for this dock model; typically a file name or a URL.
	 */
	public DefaultDockModel(String source) {
		this.source = source;
	}

	// Implementations of DockModel.

	public String getSource() {
		return source;
	}

	public void addOwner(String ownerId, Window window) {

		if (owners.contains(ownerId)) {
			throw new IllegalArgumentException("There is already an owner window with ID [" + ownerId + "].");
		}

		if (!owners.contains(window)) {
			owners.add(window);
			ownerIDsMap.put(window, ownerId);
			ownerRootDockKeys.put(window, new HashSet());
			ownerVisualizerKeys.put(window, new HashSet());

			OwnerToFrontListener ownerToFrontListener = new OwnerToFrontListener(window);
			ownerFocusListeners.put(window, ownerToFrontListener);
			window.addWindowFocusListener(ownerToFrontListener);
		}

	}

	public String getOwnerID(Window owner) {
		return (String) ownerIDsMap.get(owner);
	}

	public int getOwnerCount() {
		return owners.size();
	}

	public Window getOwner(int index) {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getOwnerCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		// Return the owner with the given index.
		return (Window) owners.get(index);

	}

	public void removeOwner(Window owner) {

		// Check if the window is an owner.
		if (!owners.contains(owner)) {
			throw new IllegalArgumentException("The window is not an owner window of the dock model.");
		}

		// Iterate over the root docks of the owner.
		Iterator rootDockIterator = getRootKeys(owner);
		while (rootDockIterator.hasNext()) {
			String rootDockKey = (String) rootDockIterator.next();
			rootDockKeys.remove(rootDockKey);
		}
		owners.remove(owner);
		ownerIDsMap.remove(owner);
		ownerRootDockKeys.remove(owner);
		ownerVisualizerKeys.remove(owner);

	}

	public void addRootDock(String rootKey, Dock dock, Window owner) {

		// Check if the owner is a legal owner.
		if (!owners.contains(owner)) {
			throw new IllegalArgumentException("Owner [" + owner + "] is not an owner window of this dock model.");
		}

		// Check if the dock is a root dock.
		if (dock.getParentDock() != null) {
			throw new IllegalArgumentException("The dock is not a root dock. Its parent should be null.");
		}

		// Check if this root dock is not already in the model.
		if (rootDockKeys.containsValue(dock)) {
			throw new IllegalArgumentException("There is already a root dock with key [" + rootKey + "] in this dock model.");
		}

		// Add the root dock.
		rootDockKeys.put(rootKey, dock);
		Set rootDockKeysOfOwner = (Set) ownerRootDockKeys.get(owner);
		rootDockKeysOfOwner.add(rootKey);

		// Add the owner to front listener to a float dock.
		if (dock instanceof FloatDock) {
			OwnerToFrontListener ownerToFrontListener = (OwnerToFrontListener) ownerFocusListeners.get(owner);
			((FloatDock) dock).addWindowFocusListener(ownerToFrontListener);
		}

	}

	public Dock getRootDock(String rootKey) {
		return (Dock) rootDockKeys.get(rootKey);
	}

	public void removeRootDock(Dock dock) {

		// Get the key of the root dock.
		Iterator rootDockKeyIterator = rootDockKeys.keySet().iterator();
		while (rootDockKeyIterator.hasNext()) {
			// Get the key and its root dock.
			String rootDockKey = (String) rootDockKeyIterator.next();
			Dock rootDock = getRootDock(rootDockKey);

			// Is the root dock the given dock?
			if (rootDock.equals(dock)) {
				// Remove the key and root dock.
				rootDockKeys.remove(rootDockKey);

				// Iterate over the owner windows.
				Iterator ownerRootDocksIterator = ownerRootDockKeys.values().iterator();
				while (ownerRootDocksIterator.hasNext()) {
					// Try to remove the key of the root dock from this owner.
					Set ownerRootDocks = (Set) ownerRootDocksIterator.next();
					if (ownerRootDocks.remove(rootDockKey)) {
						// Success, we could remove the key.
						return;
					}
				}
			}
		}

		throw new IllegalArgumentException("The dock is not a root dock of this dock model.");
	}

	public Iterator getRootKeys(Window owner) {

		Set rootDockKeysOfOwner = (Set) ownerRootDockKeys.get(owner);
		return rootDockKeysOfOwner.iterator();

	}

	public void addVisualizer(String key, Visualizer visualizer, Window owner) {

		// Check if the owner is a legal owner.
		if (!owners.contains(owner)) {
			throw new IllegalArgumentException("Owner [" + owner + "] is not an owner window of this dock model.");
		}

		// Check if this visualizer is not already in the model.
		if (visualizerKeys.containsValue(visualizer)) {
			throw new IllegalArgumentException("There is already a visualizer with key [" + key + "] in this dock model.");
		}

		// Add the visualizer.
		visualizerKeys.put(key, visualizer);
		Set visualizerKeysOfOwner = (Set) ownerVisualizerKeys.get(owner);
		visualizerKeysOfOwner.add(key);

	}

	public Visualizer getVisualizer(String key) {
		return (Visualizer) visualizerKeys.get(key);
	}

	public Iterator getVisualizerKeys(Window owner) {

		Set visualizerKeysOfOwner = (Set) ownerVisualizerKeys.get(owner);
		return visualizerKeysOfOwner.iterator();

	}

	public void removeVisualizer(Visualizer visualizerToRemove) {

		// Get the key of the visualizer.
		Iterator visualizerKeyIterator = visualizerKeys.keySet().iterator();
		while (visualizerKeyIterator.hasNext()) {
			// Get the key and its visualizer.
			String visualizerKey = (String) visualizerKeyIterator.next();
			Visualizer visualizer = getVisualizer(visualizerKey);

			// Is the visualizer the given visualizer?
			if (visualizer.equals(visualizerToRemove)) {
				// Remove the key and root dock.
				visualizerKeys.remove(visualizerKey);

				// Iterate over the owner windows.
				Iterator ownerVisualizerIterator = ownerVisualizerKeys.values().iterator();
				while (ownerVisualizerIterator.hasNext()) {
					// Try to remove the key of the visualizer from this owner.
					Set ownerVisualizers = (Set) ownerVisualizerIterator.next();
					if (ownerVisualizers.remove(visualizerKey)) {
						// Success, we could remove the key.
						return;
					}
				}
			}
		}

		throw new IllegalArgumentException("The visualizer is not a visualizer of this dock model.");

	}

	public Set getFloatDockKeys(Window owner) {

		Set keys = new HashSet();

		// Iterate over the root docks of the given owner.
		Iterator rootDockKeyIterator = getRootKeys(owner);
		while (rootDockKeyIterator.hasNext()) {
			String rootDockKey = (String) rootDockKeyIterator.next();

			// Get the root dock.
			Dock rootDock = getRootDock(rootDockKey);
			if (rootDock instanceof FloatDock) {
				keys.add(rootDockKey);
			}
		}

		return keys;

	}

	public void loadProperties(String sourceName, String prefix, Properties properties, Map dockablesMap, Map ownersMap, Map docksMap, Map visualizersMap) throws IOException {

		//	Set the loadOwnerRectangle property.
		loadOwnerRectangle = PropertiesUtil.getBoolean(properties, prefix + "loadOwnerRectangle", loadOwnerRectangle);

		this.source = sourceName;
		DockModelPropertiesUtil.loadDockModelProperties(this, prefix, properties, dockablesMap, ownersMap, docksMap, visualizersMap, isLoadOwnerRectangle());

	}

	public void saveProperties(String prefix, Properties properties, Map docks) {

		// Save the loadOwnerRectangle property.
		PropertiesUtil.setBoolean(properties, prefix + "loadOwnerRectangle", loadOwnerRectangle);

		// Save the dock model.
		DockModelPropertiesUtil.saveDockModelProperties(this, prefix, properties, docks);
	}

	// Getters / Setters.

	/**
	 * Determines if the position and size of every owner window of a dock model are decoded and
	 * the properties are set on the owner window of the dock model.
	 *
	 * @return True if the position and size of every owner window of a dock model are decoded and
	 * the properties are set on the owner window of the dock model.
	 */
	public boolean isLoadOwnerRectangle() {
		return loadOwnerRectangle;
	}

	/**
	 * Sets if the position and size of every owner window of a dock model are decoded and
	 * the properties are set on the owner window of the dock model.
	 *
	 * @param loadFrameRectangle True if the position and size of every owner window of a dock model are decoded and
	 *                           the properties are set on the owner window of the dock model.
	 */
	public void setLoadOwnerRectangle(boolean loadFrameRectangle) {
		this.loadOwnerRectangle = loadFrameRectangle;
	}

	// Private classes.

	/**
	 * This class listens when windows get the focus.
	 * The owner window of that window will be set to the front of the list with owners.
	 */
	private class OwnerToFrontListener implements WindowFocusListener {

		private Window ownerWindow;

		// Constructors.

		public OwnerToFrontListener(Window ownerWindow) {
			this.ownerWindow = ownerWindow;
		}

		// Implementations of WindowFocusListener.

		public void windowGainedFocus(WindowEvent windowEvent) {

			if (!owners.contains(ownerWindow)) {
				throw new IllegalStateException("The window is not an owner window of the dock model.");
			}

			if (owners.indexOf(ownerWindow) != 0) {
				owners.remove(ownerWindow);
				owners.add(0, ownerWindow);
			}
		}

		public void windowLostFocus(WindowEvent windowEvent) {
			// Do nothing.
		}

	}
}
