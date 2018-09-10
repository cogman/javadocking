package com.javadocking.dock.factory;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;

import java.awt.*;
import java.util.Properties;

/**
 * <p>
 * This is a factory that creates a {@link Dock} for a given {@link Dockable}
 * and a given docking mode.
 * </p>
 * <p>
 * Information on using the dock factory is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html#ChildDockFactory" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockFactory {

	// Interface methods.

	/**
	 * Creates a dock for the given dockable.
	 *
	 * @param dockable The dockable for which the dock is created.
	 * @return A dock for the given dockable.
	 * @param    dockingMode    The docking mode that is used for docking the dockable.
	 * This integer should be a constant defined by {@link DockingMode}.
	 */
	Dock createDock(Dockable dockable, int dockingMode);

	/**
	 * Gets the preferred size for the dock that will be created by this factory.
	 *
	 * @param dockable The dockable for which the dock is created.
	 * @return A dock for the given dockable.
	 * @param    dockingMode    The docking mode that is used for docking the dockable.
	 * This integer should be a constant defined by {@link DockingMode}.
	 */
	Dimension getDockPreferredSize(Dockable dockable, int dockingMode);

	/**
	 * Saves the properties of this dock factory in the given properties object. The property names for this dock factory
	 * should start with the given prefix.
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 */
	void saveProperties(String prefix, Properties properties);

	/**
	 * Sets the properties for this dock factory. The properties can be found in the given properties object
	 * and the property names for this dock factory start with the given prefix.
	 *
	 * @param prefix     The prefix of the names of the properties that have been intended for this dock factory.
	 * @param properties The properties object that contains the properties for this dock factory. It can contain also
	 *                   properties for other objects, but they will have another prefix.
	 */
	void loadProperties(String prefix, Properties properties);

}
