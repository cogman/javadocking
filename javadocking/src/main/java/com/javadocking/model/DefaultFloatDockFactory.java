package com.javadocking.model;

import com.javadocking.dock.FloatDock;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Properties;

/**
 * This class creates a normal float dock with the constructor
 * {@link com.javadocking.dock.FloatDock#FloatDock(Window, DockFactory)}
 * if the <code>childDockFactory</code> is not null. Otherwise
 * the constructor {@link com.javadocking.dock.FloatDock#FloatDock(Window)}
 * is used.
 *
 * @author Heidi Rakels.
 */
public class DefaultFloatDockFactory implements FloatDockFactory {

	// Fields.

	/**
	 * The factory that will create child docks for the float docks
	 * created by this factory.
	 */
	@Nullable
	private DockFactory childDockFactory;

	// Constructors.

	/**
	 * Constructs a factory for creating {@link FloatDock}s.
	 */
	public DefaultFloatDockFactory() {
	}

	/**
	 * Constructs a factory for creating {@link FloatDock}s.
	 * The specified factory for creating child docks
	 * is given to the created float docks.
	 *
	 * @param    childDockFactory    The factory that will create child docks for the float docks
	 * created by this factory.
	 */
	public DefaultFloatDockFactory(DockFactory childDockFactory) {
		this.childDockFactory = childDockFactory;
	}

	// Implementations of FloatDockProvider.

	@Nullable
	public FloatDock createFloatDock(@NotNull Window owner) {

		if (childDockFactory != null) {
			return new FloatDock(owner, childDockFactory);
		} else {
			return new FloatDock(owner);
		}
	}

	public void loadProperties(String prefix, @NotNull Properties properties) {

		// Load the class and properties of the child dock factory.
		try {
			String className = null;
			className = PropertiesUtil.getString(properties, prefix + "childDockFactory", className);
			if (className != null) {
				Class clazz = Class.forName(className);
				childDockFactory = (DockFactory) clazz.newInstance();
				childDockFactory.loadProperties(prefix + "childDockFactory.", properties);
			}
		} catch (@NotNull ClassNotFoundException | IllegalAccessException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new SingleDockFactory();
		} catch (InstantiationException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = null;
		}

	}

	public void saveProperties(String prefix, @NotNull Properties properties) {

		if (childDockFactory != null) {
			// Save the class of the child dock factory and its properties.
			String className = childDockFactory.getClass().getName();
			PropertiesUtil.setString(properties, prefix + "childDockFactory", className);
			childDockFactory.saveProperties(prefix + "childDockFactory.", properties);
		}

	}

	// Getters / Setters.

	/**
	 * Gets the factory that will create child docks for the float docks
	 * created by this factory. If this factory is null, the default factory of the float dock will be used.
	 *
	 * @return The factory that will create child docks for the float docks
	 * created by this factory.
	 */
	@Nullable
	public DockFactory getChildDockFactory() {
		return childDockFactory;
	}

	/**
	 * Sets the factory that will create child docks for the float docks
	 * created by this factory.
	 *
	 * @param childDockFactory The factory that will create child docks for the float docks
	 *                         created by this factory.
	 */
	public void setChildDockFactory(DockFactory childDockFactory) {

		this.childDockFactory = childDockFactory;

	}

}
