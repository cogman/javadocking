package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class TabDockSample extends JPanel {

	public TabDockSample(@NotNull JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel();
		SmallPanel smallText2 = new SmallPanel();
		SmallPanel smallText3 = new SmallPanel();
		SmallPanel smallText4 = new SmallPanel();

		// Create the dockable with the content.
		// Our dockables can only be docked in tabbed docks.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.TAB);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.TAB);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.TAB);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.TAB);

		// Create the docks.
		TabDock dock1 = new TabDock();
		TabDock dock2 = new TabDock();

		// Add the dockable.
		dock1.addDockable(dockable1, new Position(0));
		dock1.addDockable(dockable2, new Position(1));
		dock2.addDockable(dockable3, new Position(0));
		dock2.addDockable(dockable4, new Position(1));

		// Create the split pane.
		JSplitPane splitPane = DockingManager.getComponentFactory().createJSplitPane();
		add(splitPane, BorderLayout.CENTER);

		// Add the docks.
		splitPane.setLeftComponent(dock1);
		splitPane.setRightComponent(dock2);
		splitPane.setDividerLocation(300);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		// Add the root docks to the dock model.
		dockModel.addRootDock("dock1", dock1, frame);
		dockModel.addRootDock("dock2", dock2, frame);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 400;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}
}
