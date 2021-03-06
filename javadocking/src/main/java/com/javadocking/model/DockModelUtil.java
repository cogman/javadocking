package com.javadocking.model;

import com.javadocking.dock.Dock;
import com.javadocking.dock.HidableFloatDock;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains a collection of static utility methods for dock models.
 *
 * @author Heidi Rakels.
 */
public class DockModelUtil {

	@NotNull
	public static Set getVisibleFloatDocks(DockModel dockModel, Window ownerWindow) {
		Set keys = dockModel.getFloatDockKeys(ownerWindow);
		Set visibleDocks = new HashSet();
		for (Object key1 : keys) {
			String key = (String) key1;
			Dock dock = dockModel.getRootDock(key);
			if (dock instanceof HidableFloatDock) {
				if (!((HidableFloatDock) dock).isHidden()) {
					visibleDocks.add(dock);
				}
			} else {
				visibleDocks.add(dock);
			}
		}
		return visibleDocks;
	}

}
