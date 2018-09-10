package com.javadocking.dock.factory;

import com.javadocking.dock.Dock;
import com.javadocking.dock.LineDock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.DockingUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Properties;

/**
 * This dock factory creates always a line dock with the docking modes:
 * <ul>
 * <li>{@link DockingMode#HORIZONTAL_TOOLBAR} and {@link DockingMode#VERTICAL_TOOLBAR}, or</li>
 * <li>{@link DockingMode#HORIZONTAL_MINIMIZE} and {@link DockingMode#VERTICAL_MINIMIZE}.</li>
 * </ul>
 *
 * @author Heidi Rakels
 */
public class ToolBarDockFactory implements DockFactory {

	// Implementations of DockFactory.

	/**
	 * Creates a tool bar dock.
	 *
	 * @param    dockingMode        The docking mode should be
	 * <ul>
	 * <li>{@link DockingMode#HORIZONTAL_TOOLBAR},</li>
	 * <li>{@link DockingMode#VERTICAL_TOOLBAR},</li>
	 * <li>{@link DockingMode#HORIZONTAL_MINIMIZE}, or</li>
	 * <li>{@link DockingMode#VERTICAL_MINIMIZE}.</li>
	 * </ul>
	 * Otherwise null is returned.
	 */
	@Nullable
	public Dock createDock(Dockable dockable, int dockingMode) {

		// Test if the docking mode is horizontal tool bar.
		if ((dockingMode & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
			// Return a horizontal tool bar dock.
			return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		}

		// Test if the docking mode is vertical tool bar.
		if ((dockingMode & DockingMode.VERTICAL_TOOLBAR) != 0) {
			// Return a vertical tool bar dock.
			return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		}

		// Test if the docking mode is horizontal minimize bar.
		if ((dockingMode & DockingMode.HORIZONTAL_MINIMIZE) != 0) {
			// Return a horizontal tool bar dock.
			return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
		}

		// Test if the docking mode is vertical minimize bar.
		if ((dockingMode & DockingMode.VERTICAL_MINIMIZE) != 0) {
			// Return a vertical tool bar dock.
			return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
		}


		return null;
	}

	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode) {

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			// Test if the docking mode is horizontal tool bar.
			if (((dockingMode & DockingMode.HORIZONTAL_TOOLBAR) != 0) ||
					((dockingMode & DockingMode.HORIZONTAL_MINIMIZE) != 0)) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
			}

			// Test if the docking mode is vertical tool bar.
			if (((dockingMode & DockingMode.VERTICAL_TOOLBAR) != 0) ||
					((dockingMode & DockingMode.VERTICAL_MINIMIZE) != 0)) {
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
