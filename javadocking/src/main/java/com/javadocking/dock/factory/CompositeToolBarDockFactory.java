package com.javadocking.dock.factory;

import com.javadocking.dock.CompositeLineDock;
import com.javadocking.dock.Dock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.DockingUtil;

import java.awt.*;
import java.util.Properties;

/**
 * This dock factory creates always a composite tool bar dock.
 *
 * @author Heidi Rakels
 */
public class CompositeToolBarDockFactory implements DockFactory {

	// Implementations of DockFactory.

	/**
	 * Creates a composite tool bar dock.
	 *
	 * @param    dockingMode        The docking mode should be DockingMode.HORIZONTAL_TOOLBAR or DockingMode.VERTICAL_TOOLBAR,
	 * otherwise null is returned.
	 */
	public Dock createDock(Dockable dockable, int dockingMode) {

		// Test if the docking mode is horizontal tool bar.
		if ((dockingMode & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
			// Return a horizontal composite line dock.
			return new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		}

		// Test if the docking mode is vertical tool bar.
		if ((dockingMode & DockingMode.VERTICAL_TOOLBAR) != 0) {
			// Return a vertical composite line dock.
			return new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		}

		// Test if the docking mode is horizontal minimize bar.
		if ((dockingMode & DockingMode.HORIZONTAL_MINIMIZE) != 0) {
			// Return a horizontal composite line dock.
			return new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
		}

		// Test if the docking mode is vertical minimize bar.
		if ((dockingMode & DockingMode.VERTICAL_MINIMIZE) != 0) {
			// Return a vertical composite line dock.
			return new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
		}

		return null;
	}

	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode) {

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			// Test if the docking mode is horizontal tool bar.
			if ((dockingMode & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
			}

			// Test if the docking mode is vertical tool bar.
			if ((dockingMode & DockingMode.VERTICAL_TOOLBAR) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
			}

		}

		// We have a sinmple dockable. Get the size of the component.
		if (dockable.getContent() != null) {
			return dockable.getContent().getPreferredSize();
		}

		return new Dimension(0, 0);

	}

	public void saveProperties(String prefix, Properties properties) {
		// There are no properties.
	}


	public void loadProperties(String prefix, Properties properties) {
		// There are no properties.
	}

}
