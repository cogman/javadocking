package com.javadocking.model;

import com.javadocking.util.PropertiesUtil;

import java.io.IOException;
import java.util.*;

/**
 * This class helps a {@link com.javadocking.model.DockingPathModel} implementation loading and saving its properties.
 *
 * @author Heidi Rakels.
 */
class DockingPathModelPropertiesUtil {

	// Static fields.

	/**
	 * The name of the property that contains the docking paths.
	 */
	private static final String PROPERTY_DOCKING_PATH = "dockingPath";
	/**
	 * The name of the property that contains the dockables for which there is a history.
	 */
	private static final String PROPERTY_DOCKING_PATH_IDS = "dockingPathIds";

	// Public static methods.

	private DockingPathModelPropertiesUtil() {
	}

	/**
	 * <p>
	 * Loads the properties for this docking path model. The properties can be found in the given properties object
	 * and the property names for this dock model start with the given prefix.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the docking path model. Don't call
	 * this method for a docking path model that already has a content.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object from which the properties can be retrieved.
	 * @throws IOException If an error occurs while decoding the data.
	 * @param    dockingPathModel    The docking path model whose properties have to be loaded.
	 * @param    docks                The mapping with the keys that are used for the docks.
	 * The keys are the keys that were used for encoding the docks.
	 * The values are the docks.
	 */
	public static void loadDockingProperties(DockingPathModel dockingPathModel, String prefix, Properties properties, Map docks) {

		// Get the IDs of the docking paths.
		String[] idsArray = null;
		idsArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_DOCKING_PATH_IDS, idsArray);
		if ((idsArray != null) && (idsArray.length > 0)) {
			for (String pathId : idsArray) {
				// Load the properties of the docking path.
				//TODO general class
				String pathPrefix = prefix + PROPERTY_DOCKING_PATH + "." + pathId;
				DockingPath dockingPath = new DefaultDockingPath();
				dockingPath.loadProperties(pathPrefix, properties, docks);

				dockingPathModel.add(dockingPath);
			}
		}

	}

	// Private constructor.

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
	 * @param dockKeys   The mapping with the keys that are used for the docks.
	 *                   The keys are the docks.
	 *                   The values are the keys that are used for encoding the docks.
	 * @param    dockingPathModel    The docking path model whose properties have to be saved.
	 */
	public static void saveDockingProperties(DockingPathModel dockingPathModel, String prefix, Properties properties, Map dockKeys) {

		// Iterate over the docking paths.
		List ids = new ArrayList();
		Iterator iterator = dockingPathModel.getIDs();
		while (iterator.hasNext()) {

			// Get the docking path.
			String id = (String) iterator.next();
			DockingPath dockingPath = dockingPathModel.getDockingPath(id);
			ids.add(id);

			// Save the properties of the docking path.
			String pathPrefix = prefix + PROPERTY_DOCKING_PATH + "." + id;
			dockingPath.saveProperties(pathPrefix, properties, dockKeys);
		}

		// Save the IDs for which there is a docking path.
		String[] idsArray = new String[ids.size()];
		idsArray = (String[]) ids.toArray(idsArray);
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKING_PATH_IDS, idsArray);

	}

}
