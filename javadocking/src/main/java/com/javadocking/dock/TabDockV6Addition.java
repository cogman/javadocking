package com.javadocking.dock;

import com.javadocking.component.SelectableHeader;

import javax.swing.*;
import java.awt.*;


/**
 * <b>WARNING:</b> This class can only be used when the JVM version is 6.0 or later.
 * This class helps {@link com.javadocking.dock.TabDock} and {@link com.javadocking.dock.CompositeTabDock}
 * to use functionality that is only available from java version 6.0.
 *
 * @author Heidi Rakels.
 */
class TabDockV6Addition {

	// Public static methods.

	/**
	 * Adds a tab for the given dockable to the tabbed pane.
	 *
	 * @param tabbedPane The tabbed pane to which the content of the dockable is added.
	 * @param    tabComponent    The component that will be set on the tab.
	 */
	public static void addTab(JTabbedPane tabbedPane, Component dockableComponent, Component tabComponent) {

		//tabbedPane.addTab(dockable.getTitle(), dockable.getIcon(), dockableComponent);
		tabbedPane.addTab("", null, dockableComponent);

		tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabComponent);

		repaintTabComponents(tabbedPane);

	}

	/**
	 * Inserts a tab for the given dockable to the tabbed pane.
	 *
	 * @param tabbedPane The tabbed pane to which the content of the dockable is added.
	 * @param tabIndex   The index of the tab for the dockable.
	 * @param    tabComponent    The component that will be set on the tab.
	 */
	public static void insertTab(JTabbedPane tabbedPane, int tabIndex, Component dockableComponent, Component tabComponent) {

		//tabbedPane.insertTab(dockable.getTitle(), dockable.getIcon(), dockableComponent, "", tabIndex);
		tabbedPane.insertTab("", null, dockableComponent, "", tabIndex);

		tabbedPane.setTabComponentAt(tabIndex, tabComponent);

		repaintTabComponents(tabbedPane);

	}

	/**
	 * Sets a tab component for the given dockable in the tabbed pane.
	 *
	 * @param tabbedPane The tabbed pane to which the content of the dockable is added.
	 * @param tabIndex   The index of the tab for the dockable.
	 * @param    tabComponent    The component that will be set on the tab.
	 */
	public static void setTabComponentAt(JTabbedPane tabbedPane, int tabIndex, Component tabComponent) {

		tabbedPane.setTabComponentAt(tabIndex, tabComponent);

		repaintTabComponents(tabbedPane);

	}

	public static void repaintTabComponents(JTabbedPane tabbedPane) {

		int count = tabbedPane.getTabCount();
		int selectedIndex = tabbedPane.getModel().getSelectedIndex();
		if ((selectedIndex >= 0) && (selectedIndex < count)) {
			Component component = tabbedPane.getTabComponentAt(selectedIndex);
			if (component instanceof SelectableHeader) {
				((SelectableHeader) component).setSelected(true);
			}
		}
		for (int index = 0; index < count; index++) {
			if (index != selectedIndex) {
				Component component = tabbedPane.getTabComponentAt(index);
				if (component instanceof SelectableHeader) {
					((SelectableHeader) component).setSelected(false);
				}
			}
		}

	}
}
