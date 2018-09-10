package com.javadocking.mainandtool;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.factory.CompositeDockFactory;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.PropertiesUtil;

import java.awt.*;
import java.util.Properties;

/**
 * This dock factory creates always a split dock.
 * Only when the dockable cannot be added to a split dock it
 * delegates the creation of the dock to the alternative dock factory.
 *
 * @author Heidi Rakels
 */
public class TypeSplitDockFactory implements CompositeDockFactory {

	/**
	 * When the dockable cannot be added to a split dock the creation of the dock is delegated
	 * to this alternative dock factory.
	 */
	private TypeLeafDockFactory alternativeDockFactory = new TypeLeafDockFactory(true);
	/**
	 * This is the factory that is used in the constructor of the {@link SplitDock#SplitDock(DockFactory, CompositeDockFactory)}
	 * for creating the leaf docks of the split dock.
	 */
	private TypeLeafDockFactory childDockFactory = new TypeLeafDockFactory(true);

	private boolean main = true;

	public TypeSplitDockFactory(boolean main) {
		this.main = main;
		alternativeDockFactory = new TypeLeafDockFactory(main);
		childDockFactory = new TypeLeafDockFactory(main);
	}

	// Implementations of DockFactory.

	public Dock createDock(Dockable dockable, int dockingMode) {

		// Don't remove. In SplitDock we create a dock with dockable null.
		if (dockable == null) {
			return new TypeSplitDock(childDockFactory, this, main);
		}

		// Check if the docking modes of the dockable contain LEFT, RIGHT, TOP or BOTTOM. 
		int dockPositions = dockable.getDockingModes();
		if (((dockPositions & DockingMode.LEFT) != 0) ||
				((dockPositions & DockingMode.RIGHT) != 0) ||
				((dockPositions & DockingMode.TOP) != 0) ||
				((dockPositions & DockingMode.BOTTOM) != 0)) {
			// Create the split dock.
			return new TypeSplitDock(childDockFactory, this, main);
		}

		// This factory could not create a dock. Let the alternative factory try to create one.
		return alternativeDockFactory.createDock(dockable, dockingMode);

	}

	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode) {

		// Check if the positions of the dockable contains LEFT, RIGHT, TOP or BOTTOM. 
		int dockPositions = dockable.getDockingModes();
		if (((dockPositions & DockingMode.LEFT) != 0) ||
				((dockPositions & DockingMode.RIGHT) != 0) ||
				((dockPositions & DockingMode.TOP) != 0) ||
				((dockPositions & DockingMode.BOTTOM) != 0)) {
			// Create the split dock.
			return childDockFactory.getDockPreferredSize(dockable, dockingMode);
		}

		// This factory could not create a dock. Let the alternative factory try to create one.
		return alternativeDockFactory.getDockPreferredSize(dockable, dockingMode);

	}

	public void saveProperties(String prefix, Properties properties) {

		// Save the class of the leaf child dock factory and its properties.
		String leafChildDockFactoryClassName = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", leafChildDockFactoryClassName);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Save the class of the alternative child dock factory and its properties.
		String alternativeDockFactoryClassName = alternativeDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
		alternativeDockFactory.saveProperties(prefix + "alternativeDockFactory.", properties);

		PropertiesUtil.setBoolean(properties, prefix + "main", main);
	}

	public void loadProperties(String prefix, Properties properties) {

		main = PropertiesUtil.getBoolean(properties, prefix + "main", main);

		// Load the class and properties of the leaf child dock factory.
		try {
			String leafChildDockFactoryClassName = LeafDockFactory.class.getName();
			leafChildDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "childDockFactory", leafChildDockFactoryClassName);
			Class leafChildDockFactoryClazz = Class.forName(leafChildDockFactoryClassName);
			childDockFactory = (TypeLeafDockFactory) leafChildDockFactoryClazz.newInstance();
			childDockFactory.loadProperties(prefix + "childDockFactory.", properties);
		} catch (ClassNotFoundException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TypeLeafDockFactory(main);
		} catch (IllegalAccessException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TypeLeafDockFactory(main);
		} catch (InstantiationException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new TypeLeafDockFactory(main);
		}

		// Load the class and properties of the alternative dock factory.
		try {
			String alternativeDockFactoryClassName = LeafDockFactory.class.getName();
			alternativeDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
			Class alternativeDockFactoryClazz = Class.forName(alternativeDockFactoryClassName);
			alternativeDockFactory = (TypeLeafDockFactory) alternativeDockFactoryClazz.newInstance();
			alternativeDockFactory.loadProperties(prefix + "alternativeDockFactory.", properties);
		} catch (ClassNotFoundException exception) {
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new TypeLeafDockFactory(main);
		} catch (IllegalAccessException exception) {
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new TypeLeafDockFactory(main);
		} catch (InstantiationException exception) {
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new TypeLeafDockFactory(main);
		}

	}

	// Getters / Setters.

	/**
	 * Gets the alternative dock factory. When the dockable cannot be added to a split dock,
	 * the creation of the dock is delegated to this alternative dock factory.
	 *
	 * @return The alternative dock factory.
	 */
	public TypeLeafDockFactory getAlternativeDockFactory() {
		return alternativeDockFactory;
	}

	/**
	 * Sets the alternative dock factory. When the dockable cannot be added to a split dock,
	 * the creation of the dock is delegated to this alternative dock factory.
	 *
	 * @param alternativeDockFactory The alternative dock factory. Should not be null.
	 * @throws IllegalArgumentException When the alternative dock factory is null.
	 */
	public void setAlternativeDockFactory(TypeLeafDockFactory alternativeDockFactory) {

		if (alternativeDockFactory == null) {
			throw new IllegalArgumentException("The alternative dock factory cannot be null.");
		}

		this.alternativeDockFactory = alternativeDockFactory;

	}

	/**
	 * Gets the leaf child dock factory. This is the factory that is used in the constructor of the {@link SplitDock#SplitDock(DockFactory, CompositeDockFactory)}
	 * for creating the leaf docks of the split dock.
	 *
	 * @return The leaf child dock factory.
	 */
	public DockFactory getChildDockFactory() {
		return childDockFactory;
	}

	/**
	 * Sets the leaf child dock factory. This is the factory that is used in the constructor of the {@link SplitDock#SplitDock(DockFactory, CompositeDockFactory)}
	 * for creating the leaf docks of the split dock.
	 *
	 * @param leafChildDockFactory The leaf child dock factory. Should not be null.
	 * @throws IllegalArgumentException When the alternative dock factory is null.
	 */
	public void setChildDockFactory(DockFactory leafChildDockFactory) {

		if (leafChildDockFactory == null) {
			throw new IllegalArgumentException("The leaf dock factory cannot be null.");
		}
		if (leafChildDockFactory instanceof TypeLeafDockFactory) {
			throw new IllegalArgumentException("The leaf dock factory has to be a TypeLeafDockFactory.");
		}

		this.childDockFactory = (TypeLeafDockFactory) leafChildDockFactory;

	}

	public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}
}
