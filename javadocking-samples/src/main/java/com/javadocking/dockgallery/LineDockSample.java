package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.LineDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;

import javax.swing.*;
import java.awt.*;

public class LineDockSample extends JPanel {

	public LineDockSample(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel("I am window 1");
		SmallPanel smallText2 = new SmallPanel("I am window 2");
		SmallPanel smallText3 = new SmallPanel("I am window 3");
		SmallPanel smallText4 = new SmallPanel("I am window 4");
		SmallPanel smallText5 = new SmallPanel("I am window 5");
		SmallPanel smallText6 = new SmallPanel("I am window 6");
		SmallPanel smallText7 = new SmallPanel("I am window 7");
		SmallPanel smallText8 = new SmallPanel("I am window 8");

		// Create the dockable with the content.
		// Our dockables can not float.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL);
		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL);
		DefaultDockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL);
		DefaultDockable dockable7 = new DefaultDockable("Window7", smallText7, "Window 7", null, DockingMode.ALL);
		DefaultDockable dockable8 = new DefaultDockable("Window8", smallText8, "Window 8", null, DockingMode.ALL);

		// Create the docks.
		TabDock tabDock = new TabDock();
		LineDock lineDock = new LineDock(LineDock.ORIENTATION_VERTICAL, false);
		lineDock.setRealSizeRectangle(false);

		// Add the dockable.
		tabDock.addDockable(dockable1, new Position(0));
		tabDock.addDockable(dockable2, new Position(1));
		tabDock.addDockable(dockable3, new Position(2));
		tabDock.addDockable(dockable4, new Position(3));

		// Add the dockable.
		lineDock.addDockable(dockable5, new Position(0));
		lineDock.addDockable(dockable6, new Position(1));
		lineDock.addDockable(dockable7, new Position(2));
		lineDock.addDockable(dockable8, new Position(3));

		// Create the split pane.
		JSplitPane splitPane = DockingManager.getComponentFactory().createJSplitPane();
		add(splitPane, BorderLayout.CENTER);

		// Add the docks.
		splitPane.setLeftComponent(tabDock);
		splitPane.setRightComponent(lineDock);
		splitPane.setDividerLocation(400);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		// Add the root docks to the dock model.
		dockModel.addRootDock("tabDock", tabDock, frame);
		dockModel.addRootDock("lineDock", lineDock, frame);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}
}
