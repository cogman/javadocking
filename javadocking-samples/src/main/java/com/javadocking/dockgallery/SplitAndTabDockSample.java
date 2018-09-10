package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;

import javax.swing.*;
import java.awt.*;

public class SplitAndTabDockSample extends JPanel {

	public SplitAndTabDockSample(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 800;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);
		frame.getContentPane().add(this);

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel();
		SmallPanel smallText2 = new SmallPanel();
		SmallPanel smallText3 = new SmallPanel();
		SmallPanel smallText4 = new SmallPanel();
		SmallPanel smallText5 = new SmallPanel();
		SmallPanel smallText6 = new SmallPanel();
		SmallPanel smallText7 = new SmallPanel();
		SmallPanel smallText8 = new SmallPanel();

		// Create the dockable with the content.
		// Our dockables can not float.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable7 = new DefaultDockable("Window7", smallText7, "Window 7", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable8 = new DefaultDockable("Window8", smallText8, "Window 8", null, DockingMode.ALL - DockingMode.FLOAT);

		// Create the split docks.
		SplitDock northEastDock = new SplitDock();
		SplitDock southEastDock = new SplitDock();
		SplitDock eastDock = new SplitDock();
		SplitDock rootDock = new SplitDock();

		// Add the root dock to the panel.
		add(rootDock);

		// A TabDockFactory is the default dock factory for a split dock.

		// Create the tab docks.
		TabDock tabDock1 = new TabDock();
		TabDock tabDock2 = new TabDock();
		TabDock tabDock3 = new TabDock();
		TabDock tabDock4 = new TabDock();
		TabDock tabDock5 = new TabDock();

		// Add the dockables
		tabDock1.addDockable(dockable1, new Position(0));
		tabDock1.addDockable(dockable2, new Position(1));
		tabDock1.addDockable(dockable3, new Position(2));
		tabDock1.addDockable(dockable4, new Position(3));
		tabDock2.addDockable(dockable5, new Position(0));
		tabDock3.addDockable(dockable6, new Position(0));
		tabDock4.addDockable(dockable7, new Position(0));
		tabDock5.addDockable(dockable8, new Position(0));

		// Add the single leaf docks to the split docks.
		rootDock.addChildDock(tabDock1, new Position(Position.LEFT));
		rootDock.addChildDock(eastDock, new Position(Position.RIGHT));
		rootDock.setDividerLocation(width / 2);
		eastDock.addChildDock(northEastDock, new Position(Position.TOP));
		eastDock.addChildDock(southEastDock, new Position(Position.BOTTOM));
		eastDock.setDividerLocation(height / 2);
		northEastDock.addChildDock(tabDock2, new Position(Position.TOP));
		northEastDock.addChildDock(tabDock3, new Position(Position.BOTTOM));
		northEastDock.setDividerLocation(height / 4);
		southEastDock.addChildDock(tabDock4, new Position(Position.LEFT));
		southEastDock.addChildDock(tabDock5, new Position(Position.RIGHT));
		southEastDock.setDividerLocation(width / 4);

		// Add the root docks to the dock model.
		dockModel.addRootDock("dock", rootDock, frame);

	}
}
