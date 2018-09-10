package com.javadocking.model;

import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.visualizer.FloatExternalizer;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This dock model adds for every owner window automatically a {@link FloatDock}
 * and a {@link FloatExternalizer}.
 * </p>
 * <p>
 * Information on using float dock models is in
 * <a href="http://www.javadocking.com/developerguide/dockmodel.html#FloatDockModel" target="_blank">How to Use Dock Models</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * If another float dock is added to the dock model, the key for the float dock of that owner has to
 * be used. This key can be retrieved with {@link #getFloatDockKey(Window)}.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class FloatDockModel extends DefaultDockModel {

	// Static fields.

	/**
	 * The suffix for the keys of the float root docks.
	 */
	public static final String FLOAT_DOCK_KEY = "FloatDock";

	// Fields.

	/**
	 * This factory creates the default float docks for this dock model.
	 */
	private FloatDockFactory floatDockFactory;

	// Constructors.

	/**
	 * Constructs a float dock model.
	 */
	public FloatDockModel() {
		this(null, new DefaultFloatDockFactory());
	}

	/**
	 * Constructs a float dock model with the given source.
	 *
	 * @param source The name of the data source for this dock model.
	 */
	public FloatDockModel(String source) {
		this(source, new DefaultFloatDockFactory());
	}

	/**
	 * Constructs a float dock model with the given factory for creating the default float docks of this model.
	 *
	 * @param floatDockFactory This factory creates the default float docks for this dock model.
	 */
	public FloatDockModel(FloatDockFactory floatDockFactory) {
		this(null, floatDockFactory);

	}

	/**
	 * Constructs a float dock model with the given source
	 * and the given factory for creating the default float docks of this model.
	 *
	 * @param source           The name of the data source for this dock model.
	 * @param floatDockFactory This factory creates the default float docks for this dock model.
	 */
	public FloatDockModel(String source, FloatDockFactory floatDockFactory) {

		super(source);

		this.floatDockFactory = floatDockFactory;

	}


	// Overwritten methods of DefaultDockModel.

	/**
	 * Adds the owner window and its ID.
	 * Adds also a root dock for this owner window that is a {@link FloatDock}
	 * created with the <code>floatDockFactory</code>.
	 */
	public void addOwner(String ownerId, Window ownerWindow) {

		super.addOwner(ownerId, ownerWindow);

		// Create the float dock for this owner.
		FloatDock floatDock = floatDockFactory.createFloatDock(ownerWindow);
		super.addRootDock(getFloatDockKey(ownerWindow), floatDock, ownerWindow);

	}

	/**
	 * Checks that the appropriate key is used ({@link #getFloatDockKey(Window)}), when the root dock is a {@link FloatDock}.
	 */
	public void addRootDock(String rootKey, Dock dock, Window owner) {

		// Check that the key is valid, if the dock is a float dock.
		if (dock instanceof FloatDock) {
			if (!getFloatDockKey(owner).equals(rootKey)) {
				throw new IllegalArgumentException("Invalid key for a float dock. Use getFloatDockKey(String).");
			}
		}
		super.addRootDock(rootKey, dock, owner);

	}

	public FloatDock getFloatDock(Window owner) {
		return (FloatDock) getRootDock(getFloatDockKey(owner));
	}

	/**
	 * Creates the key for a float dock with the given owner ID.
	 * This is the concatenation of the <code>ownerId</code> and <code>FLOAT_DOCK_KEY</code>.
	 */
	public String getFloatDockKey(Window owner) {
		return getOwnerID(owner) + FLOAT_DOCK_KEY;
	}

	// Getters / Setters.

	/**
	 * Gets the factory of the float docks.
	 * The default is a {@link DefaultFloatDockFactory}.
	 *
	 * @return The factory of the float docks.
	 */
	public FloatDockFactory getFloatDockFactory() {
		return floatDockFactory;
	}

	/**
	 * Sets the factory of the float docks.
	 *
	 * @param floatDockProvider The factory of the float docks.
	 */
	public void setFloatDockFactory(FloatDockFactory floatDockProvider) {
		this.floatDockFactory = floatDockProvider;
	}

	public void loadProperties(String sourceName, String prefix, Properties properties, Map dockablesMap, Map ownersMap, Map docks, Map visualizersMap) throws IOException {

		// Load the class and properties of the child dock factory.
		try {
			String className = SingleDockFactory.class.getName();
			className = PropertiesUtil.getString(properties, prefix + "floatDockFactory", className);
			Class clazz = Class.forName(className);
			floatDockFactory = (FloatDockFactory) clazz.newInstance();
			floatDockFactory.loadProperties(prefix + "floatDockFactory.", properties);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the float dock factory.");
			exception.printStackTrace();
			floatDockFactory = new DefaultFloatDockFactory();
		}

		super.loadProperties(sourceName, prefix, properties, dockablesMap, ownersMap, docks, visualizersMap);

	}

	public void saveProperties(String prefix, Properties properties, Map docks) {

		// Save the class of the float dock factory and its properties.
		String className = floatDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "floatDockFactory", className);
		floatDockFactory.saveProperties(prefix + "floatDockFactory.", properties);

		super.saveProperties(prefix, properties, docks);

	}


}
