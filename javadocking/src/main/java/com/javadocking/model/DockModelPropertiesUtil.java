package com.javadocking.model;

import com.javadocking.dock.CompositeDock;
import com.javadocking.dock.Dock;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.visualizer.Visualizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * This class helps a {@link com.javadocking.model.DockModel} implementation loading and saving its properties.
 *
 * @author Heidi Rakels.
 */
class DockModelPropertiesUtil {


	// Static fields.

	/**
	 * The name of the <code>class</code> property of a dock.
	 */
	public static final String DOCK_CLASS = "class";
	/**
	 * The name of the <code>class</code> property of a visualizer.
	 */
	public static final String VISUALIZER_CLASS = "class";
	/**
	 * The name of the property that contains the IDs of the child docks of a dock.
	 */
	private static final String PROPERTY_CHILD_DOCK_KEYS = "childKeys";
	/**
	 * The name of the property that contains the keys of the root docks of a dock model.
	 */
	private static final String PROPERTY_ROOT_DOCK_KEYS = "rootDockKeys";
	/**
	 * The name of the property that contains the keys of the visualizers of a dock model.
	 */
	private static final String PROPERTY_VISUALIZER_KEYS = "visualizerKeys";
	/**
	 * The name of the property that contains the IDs of the dock models of the dock model group.
	 */
	private static final String PROPERTY_OWNER_IDS = "dockModelIds";
	/**
	 * The name of the property that contains the key of a root dock of the dock model.
	 */
	private static final String PROPERTY_ROOT_DOCK_KEY = "rootDockKey";
	/**
	 * The name of the property that contains the position and size of the owner window of a dock model.
	 */
	private static final String PROPERTY_WINDOW_RECTANGLE = "windowRectangle";


	// Public static methods.

	private DockModelPropertiesUtil() {
	}

	/**
	 * <p>
	 * Saves the properties of the dock dock model in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this dock model should start with the given prefix.
	 * </p>
	 *
	 * @param dockModel  The dock model whose properties have to be saved.
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 * @param docks      The encoded docks should also be added to this map. The keys are the key that are used for encoding the docks.
	 */
	public static void saveDockModelProperties(DockModel dockModel, String prefix, @NotNull Properties properties, Map docks) {

		// Create the id manager.
		DockKeyManager dockKeyManager = new DockKeyManager(docks);

		// Save the IDs of the owner windows.
		String[] dockModelOwnerIdsArray = new String[dockModel.getOwnerCount()];
		for (int ownerIndex = 0; ownerIndex < dockModel.getOwnerCount(); ownerIndex++) {
			Window ownerWindow = dockModel.getOwner(ownerIndex);
			dockModelOwnerIdsArray[ownerIndex] = dockModel.getOwnerID(ownerWindow);
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_OWNER_IDS, dockModelOwnerIdsArray);

		// Iterate over the owners.
		for (int ownerIndex = 0; ownerIndex < dockModel.getOwnerCount(); ownerIndex++) {
			// Get the owner ID.
			Window ownerWindow = dockModel.getOwner(ownerIndex);
			String ownerId = dockModel.getOwnerID(ownerWindow);

			// Save the position and size of the owner window.
			int[] rectangle = new int[4];
			rectangle[0] = ownerWindow.getLocation().x;
			rectangle[1] = ownerWindow.getLocation().y;
			rectangle[2] = ownerWindow.getSize().width;
			rectangle[3] = ownerWindow.getSize().height;
			PropertiesUtil.setIntegerArray(properties, prefix + ownerId + "." + PROPERTY_WINDOW_RECTANGLE, rectangle);

			// Iterate over the root docks.
			Iterator rootDockKeyIterator = dockModel.getRootKeys(ownerWindow);
			int rootDockCount = 0;
			while (rootDockKeyIterator.hasNext()) {
				String rootDockKey = (String) rootDockKeyIterator.next();
				Dock rootDock = dockModel.getRootDock(rootDockKey);
				rootDockCount++;

				// Save the properties of the root dock and its children.
				saveDock(rootDock, prefix, properties, dockKeyManager, rootDockKey);
			}

			// Save the internal keys and external keys of the root docks.
			String[] rootDockIdsArray = new String[rootDockCount];
			rootDockKeyIterator = dockModel.getRootKeys(ownerWindow);
			int rootDockIndex = 0;
			while (rootDockKeyIterator.hasNext()) {
				String rootDockExternalId = (String) rootDockKeyIterator.next();
				Dock rootDock = dockModel.getRootDock(rootDockExternalId);
				rootDockIdsArray[rootDockIndex] = dockKeyManager.getKey(rootDock);
				rootDockIndex++;
			}
			PropertiesUtil.setStringArray(properties, prefix + ownerId + "." + PROPERTY_ROOT_DOCK_KEYS, rootDockIdsArray);

			// Iterate over the visualizers.
			Iterator visualizerKeyIterator = dockModel.getVisualizerKeys(ownerWindow);
			int visualizerCount = 0;
			while (visualizerKeyIterator.hasNext()) {
				String visualizerKey = (String) visualizerKeyIterator.next();
				Visualizer visualizer = dockModel.getVisualizer(visualizerKey);
				visualizerCount++;

				// Save the properties of the visualizer.
				saveVisualizer(visualizer, prefix, properties, visualizerKey, dockKeyManager.getDockKeys());
			}

			// Save the keys of the visualizers.
			String[] visualizerKeysArray = new String[visualizerCount];
			visualizerKeyIterator = dockModel.getVisualizerKeys(ownerWindow);
			int visualizerIndex = 0;
			while (visualizerKeyIterator.hasNext()) {
				String visualizerKey = (String) visualizerKeyIterator.next();
				visualizerKeysArray[visualizerIndex] = visualizerKey;
				visualizerIndex++;
			}
			PropertiesUtil.setStringArray(properties, prefix + ownerId + "." + PROPERTY_VISUALIZER_KEYS, visualizerKeysArray);

		}

	}

	// Private static metods.

	/**
	 * <p>
	 * Saves the properties of this dock dock model in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this dock model should start with the given prefix.
	 * </p>
	 *
	 * @param dockModel    The dock model whose properties have to be loaded.
	 * @param prefix       The prefix for the property names.
	 * @param properties   The properties object to which the properties should be added.
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
	 * @param docksMap     The decoded docks should be added to this map. The keys are the keys that were used for encoding the docks.
	 *                     <ul>
	 *                     <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 *                     <li>map value: a dock that is already loaded ({@link Dock}).</li>
	 *                     </ul>
	 * @param    visualizersMap        A map with the visualizers.
	 * <ul>
	 * <li>map key: the key of the visualizer (java.lang.String).</li>
	 * <li>map value: the visualizer (java.awt.window).</li>
	 * </ul>
	 */
	public static void loadDockModelProperties(@NotNull DockModel dockModel, String prefix, @NotNull Properties properties, Map dockablesMap, @NotNull Map ownersMap, @NotNull Map docksMap, @NotNull Map visualizersMap, boolean loadOwnerRectangle) throws IOException {

		// Get the owner IDs.
		String[] ownerIdsArray = null;
		ownerIdsArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_OWNER_IDS, ownerIdsArray);

		// Iterate over the owner windows.
		for (String ownerId : ownerIdsArray) {
			// Add the owner to the dock model.
			Object ownerObject = ownersMap.get(ownerId);
			if (!(ownerObject instanceof Window)) {
				throw new IOException("The values in the owner windows mapping should be of type java.awt.Window.");
			}
			Window owner = (Window) ownerObject;
			dockModel.addOwner(ownerId, owner);

			// Get the rectangle of the owner.
			if (loadOwnerRectangle) {
				int[] rectangle = null;
				rectangle = PropertiesUtil.getIntegerArray(properties, prefix + ownerId + "." + PROPERTY_WINDOW_RECTANGLE, rectangle);
				if ((rectangle != null) && (rectangle.length == 4)) {
					owner.setLocation(rectangle[0], rectangle[1]);
					owner.setSize(rectangle[2], rectangle[3]);
				}
			}

			// Get the visualizer keys.
			String[] visualizerKeysArray = null;
			visualizerKeysArray = PropertiesUtil.getStringArray(properties, prefix + ownerId + "." + PROPERTY_VISUALIZER_KEYS, visualizerKeysArray);
			if ((visualizerKeysArray != null) && (visualizerKeysArray.length > 0)) {
				// Iterate over the visualizer keys.
				for (String visualizerKey : visualizerKeysArray) {
					// Create the visualizer with its dockables for the key with the properties.
					Visualizer visualizer = loadVisualizer(visualizerKey, prefix, properties, dockablesMap, owner, visualizersMap);
					if (visualizer != null) {
						// Add the root dock to the model.
						dockModel.addVisualizer(visualizerKey, visualizer, owner);
					}

				}
			}

			// Get the root dock keys.
			String[] rootDockIdsArray = null;
			rootDockIdsArray = PropertiesUtil.getStringArray(properties, prefix + ownerId + "." + PROPERTY_ROOT_DOCK_KEYS, rootDockIdsArray);
			if ((rootDockIdsArray != null) && (rootDockIdsArray.length > 0)) {

				// Iterate over the root dock keys.
				for (String rootDockId : rootDockIdsArray) {
					// Create the dock with its children and dockables for the ID with the properties.
					DockWithKey rootDockWithId = loadDock(rootDockId, prefix, properties, dockablesMap, owner, docksMap);
					Dock rootDock = rootDockWithId.getDock();

//					// Remove the empty children from the dock.
//					if (rootDock instanceof CompositeDock)
//					{
//						DockingUtil.removeEmptyChildren((CompositeDock)rootDock);
//					}

					// Add the root dock to the model.
					dockModel.addRootDock(rootDockWithId.getKey(), rootDock, owner);

				}
			}
		}
	}

	/**
	 * Saves the properties of the visualizer in the properties object.
	 */
	private static void saveVisualizer(Visualizer visualizer, String prefix, @NotNull Properties properties, String visualizerKey, Map dockKeys) {

		String propertiesPrefix = visualizerKey + ".";

		// Save the class of the visualizer.
		PropertiesUtil.setString(properties, propertiesPrefix + VISUALIZER_CLASS, visualizer.getClass().getName());

		// Save the properties of the dock.
		visualizer.saveProperties(propertiesPrefix, properties);

	}

	/**
	 * Sets the properties of the visualizer with the given key using the given properties. Adds also the dockables to the visualizer.
	 *
	 * @param visualizerKey The key of te visualizer.
	 * @param properties    The properties retrieved from the decoded file. They are used to set the properties of the visualizer and add the dockables.
	 * @param dockablesMap  A map with the dockables for the model.
	 *                      <ul>
	 *                      <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                      <li>map value: the dockable ({@link com.javadocking.dockable.Dockable}).</li>
	 *                      </ul>
	 * @throws IOException If an error occurs while decoding the data.
	 * @param    owner            A map with the owner windows.
	 * <ul>
	 * <li>map key: the ID of the owner window (java.lang.String).</li>
	 * <li>map value: the owner window (java.awt.window).</li>
	 * </ul>
	 * @param    visualizersMap        A map with the visualizers.
	 * <ul>
	 * <li>map key: the key of the visualizer (java.lang.String).</li>
	 * <li>map value: the visualizer (java.awt.window).</li>
	 * </ul>
	 * @return The visualizer.
	 */
	private static Visualizer loadVisualizer(String visualizerKey, String prefix, Properties properties, Map dockablesMap, Window owner, Map visualizersMap) throws IOException {

		String propertiesPrefix = visualizerKey + ".";

		// Try to get the visualizer.
		Object visualizerObject = visualizersMap.get(visualizerKey);
		if (visualizerObject != null) {
			if (visualizerObject instanceof Visualizer) {
				Visualizer visualizer = (Visualizer) visualizerObject;

				// Load the properties of the visualizer.
				visualizer.loadProperties(propertiesPrefix, properties, dockablesMap, owner);
				return visualizer;
			} else {
				throw new IOException("The values in the dockables mapping should be of type com.javadocking.Dockable.");
			}
		}

		return null;

	}

	/**
	 * Saves the properties of the dock and its children in the properties object.
	 */
	private static void saveDock(Dock dock, String prefix, @NotNull Properties properties, DockKeyManager dockKeyManager, @Nullable String rootDockKey) {
		// Generate an id for this dock.
		String dockId = dockKeyManager.createKey(dock);
		String propertiesPrefix = dockId + ".";

		// The map with the IDs of the child docks.
		Map childDockIds = new HashMap();

		// Do we have a composite dock?
		if (dock instanceof CompositeDock) {
			CompositeDock parentDock = (CompositeDock) dock;

			// Save the children.
			String[] childKeyArray = new String[parentDock.getChildDockCount()];
			for (int index = 0; index < parentDock.getChildDockCount(); index++) {
				Dock childDock = parentDock.getChildDock(index);
				saveDock(childDock, prefix, properties, dockKeyManager, null);
				String childDockKey = dockKeyManager.getKey(childDock);
				childKeyArray[index] = childDockKey;
				childDockIds.put(childDock, childDockKey);

			}

			// Save the ids of the children.
			PropertiesUtil.setStringArray(properties, propertiesPrefix + PROPERTY_CHILD_DOCK_KEYS, childKeyArray);
		}

		// Save the class of the dock.
		PropertiesUtil.setString(properties, propertiesPrefix + DOCK_CLASS, dock.getClass().getName());

		// Save the external ID of the dock.
		if (rootDockKey != null) {
			PropertiesUtil.setString(properties, propertiesPrefix + PROPERTY_ROOT_DOCK_KEY, rootDockKey);
		}

		// Save the properties of the dock.
		dock.saveProperties(propertiesPrefix, properties, childDockIds);

	}

	// Private classes.

	/**
	 * Creates the dock with the given ID using the given properties. Creates and adds also the child docks
	 * and adds the dockables for this dock.
	 *
	 * @param dockId       The ID of te dock to create.
	 * @param properties   The properties retrieved from the decoded file. They are used to create the dock.
	 * @param dockablesMap A map with the dockables for the model.
	 *                     <ul>
	 *                     <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                     <li>map value: the dockable ({@link com.javadocking.dockable.Dockable}).</li>
	 *                     </ul>
	 * @throws IOException If an error occurs while decoding the data.
	 * @param    ownersMap            A map with the owner windows.
	 * <ul>
	 * <li>map key: the ID of the owner window (java.lang.String).</li>
	 * <li>map value: the owner window (java.awt.window).</li>
	 * </ul>
	 * @param    docksMap            The decoded docks should be added to this map. The keys are the keys that were used for encoding the docks.
	 * <ul>
	 * <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 * <li>map value: a dock that is already loaded ({@link Dock}).</li>
	 * </ul>
	 * @return The created dock with its key in the dock model if it is a root dock.
	 */
	private static DockWithKey loadDock(String dockId, String prefix, @NotNull Properties properties, Map dockablesMap, Window ownersMap, @NotNull Map docksMap) throws IOException {
		String propertiesPrefix = dockId + ".";

		// Create the dock object with the class name property.
		String className = null;
		className = PropertiesUtil.getString(properties, propertiesPrefix + DOCK_CLASS, className);
		Class clazz;
		Dock dock;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException classNotFoundException) {
			throw new IOException("Could not find class [" + className + "] (ClassNotFoundException).");
		}
		try {
			dock = (Dock) clazz.newInstance();
		} catch (IllegalAccessException illegalAccessException) {
			throw new IOException("Illegal acces to class [" + className + "] (IllegalAccessException).");
		} catch (InstantiationException instantiationException) {
			throw new IOException("Could not instantiate class [" + className + "] (InstantiationException).");
		} catch (ClassCastException classCastException) {
			throw new IOException("Class [" + className + "] is not a Dock. (ClassCastException).");
		}

		// Get the external ID of the dock.
		String externalId = null;
		externalId = PropertiesUtil.getString(properties, propertiesPrefix + PROPERTY_ROOT_DOCK_KEY, externalId);

		// Do we have a composite dock?
		Map childDocks = new HashMap();
		if (dock instanceof CompositeDock) {
			CompositeDock parentDock = (CompositeDock) dock;

			// Load the children.
			String[] childIdArray = new String[0];
			childIdArray = PropertiesUtil.getStringArray(properties, propertiesPrefix + PROPERTY_CHILD_DOCK_KEYS, childIdArray);
			for (final String aChildIdArray : childIdArray) {
				DockWithKey childDockWithId = loadDock(aChildIdArray, prefix, properties, dockablesMap, ownersMap, docksMap);
				Dock childDock = childDockWithId.getDock();
				if (childDock != null) {
					childDock.setParentDock(parentDock);
					childDocks.put(aChildIdArray, childDock);
				}
			}

		}

		// Load the properties of the dock.
		dock.loadProperties(propertiesPrefix, properties, childDocks, dockablesMap, ownersMap);

		// Add this dock and key to the mapping.
		docksMap.put(dockId, dock);

		return new DockWithKey(dock, externalId);
	}


	// Private classes.

	/**
	 * This class contains a dock. If it is a root dock of a dock model, it contains also its key,
	 * otherwise the key is null.
	 */
	private static class DockWithKey {

		// Fields.

		/**
		 * The dock.
		 */
		private Dock dock;
		/**
		 * The key of the root dock in the dock model if dock is a root dock, otherwise null.
		 */
		private String key;

		// Constructors.

		/**
		 * Constructs a dock with its key in the dock model.
		 *
		 * @param    dock    The dock.
		 * @param    key        The key of the root dock in the dock model if dock is a root dock, otherwise null.
		 */
		public DockWithKey(Dock dock, String key) {
			this.dock = dock;
			this.key = key;
		}

		// Getters / Setters.

		/**
		 * Gets the dock.
		 *
		 * @return The dock.
		 */
		public Dock getDock() {
			return dock;
		}

		/**
		 * Gets the key of the root dock in the dock model if dock is a root dock, otherwise null.
		 *
		 * @return The key of the root dock in the dock model if dock is a root dock, otherwise null.
		 */
		public String getKey() {
			return key;
		}

	}

	// Private constructor.

	/**
	 * This class generates id's for docks and keeps the id information of the docks.
	 */
	private static class DockKeyManager {
		// Static fields.

		private static final String KEY_PREFIX = "Dock";

		// Fields.

		private int count = 0;
		private Map dockKeys;

		// Constructors.

		public DockKeyManager(Map dockKeys) {
			this.dockKeys = dockKeys;
		}

		// Public methods.

		@NotNull
		public String createKey(Dock dock) {
			String key = KEY_PREFIX + count++;
			dockKeys.put(dock, key);
			return key;
		}

		@NotNull
		public String getKey(Dock dock) {
			return (String) dockKeys.get(dock);
		}

		public Map getDockKeys() {
			return dockKeys;
		}

	}

}
