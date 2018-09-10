package com.javadocking.dock.factory;

import com.javadocking.dock.Dock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Properties;

/**
 * <p>
 * This dock factory creates a {@link com.javadocking.dock.SingleDock} if the given dockable
 * has {@link DockingMode#SINGLE} as one of its possible docking modes.
 * </p>
 * <p>
 * When the dockable cannot be added to a single dock, the creation of the dock can be delegated
 * to an alternative dock factory. If this factory is null, then null will be returned.
 * </p>
 *
 * @author Heidi Rakels
 */
public class SingleDockFactory implements DockFactory {

	// Fields.

	/**
	 * When the dockable cannot be added to a single dock, because it has not D{@link DockingMode#SINGLE} as possible
	 * docking mode, the creation of the dock is delegated to this alternative dock factory.
	 * This factory can be null.
	 */
	@Nullable
	private DockFactory alternativeDockFactory = new LeafDockFactory();

	// Constructors.

	/**
	 * Constructs a dock factory that creates {@link SingleDock}s.
	 * If no single dock can be created, a {@link LeafDockFactory} will be used to create the dock.
	 */
	public SingleDockFactory() {
		this(new LeafDockFactory());
	}

	/**
	 * Constructs a dock factory that creates {@link SingleDock}s.
	 * If no single dock can be created, the given alternative factory will be used to create the dock.
	 *
	 * @param alternativeDockFactory If no single dock can be created, this alternative factory will be used to create the dock.
	 *                               May be null.
	 */
	public SingleDockFactory(DockFactory alternativeDockFactory) {
		this.alternativeDockFactory = alternativeDockFactory;
	}

	// Implementations of DockFactory.

	@Nullable
	public Dock createDock(@NotNull Dockable dockable, int dockingMode) {

		// Get the allowed docking modes of the dockable.
		int dockingModes = dockable.getDockingModes();
		if ((dockingModes & DockingMode.SINGLE) != 0) {
			if (dockable instanceof CompositeDockable) {
				CompositeDockable compositeDockable = (CompositeDockable) dockable;
				if (compositeDockable.getDockableCount() == 1) {
					return new SingleDock();
				}
			} else {
				return new SingleDock();
			}
		}

		// Can we create a dock with the alternative dock factory?
		if (alternativeDockFactory != null) {
			return alternativeDockFactory.createDock(dockable, dockingMode);
		}

		return null;

	}

	public Dimension getDockPreferredSize(@NotNull Dockable dockable, int dockingMode) {

		// Get the allowed docking modes of the dockable.
		int dockingModes = dockable.getDockingModes();
		if ((dockingModes & DockingMode.SINGLE) != 0) {
			if (dockable instanceof CompositeDockable) {
				CompositeDockable compositeDockable = (CompositeDockable) dockable;
				if (compositeDockable.getDockableCount() == 1) {
					return compositeDockable.getDockable(0).getContent().getPreferredSize();
				}
			} else {
				return dockable.getContent().getPreferredSize();
			}
		}

		// Can we create a dock with the alternative dock factory?
		if (alternativeDockFactory != null) {
			return alternativeDockFactory.getDockPreferredSize(dockable, dockingMode);
		}

		return new Dimension(0, 0);


	}

	public void saveProperties(String prefix, Properties properties) {

		if (alternativeDockFactory != null) {
			// Save the class of the alternative child dock factory and its properties.
			String alternativeDockFactoryClassName = alternativeDockFactory.getClass().getName();
			PropertiesUtil.setString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
			alternativeDockFactory.saveProperties(prefix + "alternativeDockFactory.", properties);
		}
	}


	public void loadProperties(String prefix, @NotNull Properties properties) {

		// Load the class and properties of the alternative dock factory.
		try {
			String alternativeDockFactoryClassName = LeafDockFactory.class.getName();
			if (alternativeDockFactoryClassName != null) {
				alternativeDockFactoryClassName = PropertiesUtil.getString(properties, prefix + "alternativeDockFactory", alternativeDockFactoryClassName);
				Class alternativeDockFactoryClazz = Class.forName(alternativeDockFactoryClassName);
				alternativeDockFactory = (DockFactory) alternativeDockFactoryClazz.newInstance();
				alternativeDockFactory.loadProperties(prefix + "alternativeDockFactory.", properties);
			} else {
				alternativeDockFactory = null;
			}
		} catch (@NotNull ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the alternative dock factory.");
			exception.printStackTrace();
			alternativeDockFactory = new LeafDockFactory();
		}

	}

	// Getters / Setters.

	/**
	 * Gets the alternative dock factory. When the dockable cannot be added to a single dock,
	 * the creation of the dock is delegated to this alternative dock factory.
	 *
	 * @return The alternative dock factory. Can be null.
	 */
	@Nullable
	public DockFactory getAlternativeDockFactory() {
		return alternativeDockFactory;
	}

	/**
	 * Sets the alternative dock factory. When the dockable cannot be added to a single dock,
	 * the creation of the dock is delegated to this alternative dock factory.
	 *
	 * @param alternativeDockFactory The alternative dock factory. Can be null.
	 * @throws IllegalArgumentException When the alternative dock factory is null.
	 */
	public void setAlternativeDockFactory(@Nullable DockFactory alternativeDockFactory) {

		if (alternativeDockFactory == null) {
			throw new IllegalArgumentException("The alternative dock factory cannot be null.");
		}

		this.alternativeDockFactory = alternativeDockFactory;

	}

}
