package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.CompositeGridDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dock.factory.TabDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;

import javax.swing.*;
import java.awt.*;


public class CompositeGridDockSample extends JPanel {

	public CompositeGridDockSample(JFrame frame) {
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
		SmallPanel smallText9 = new SmallPanel("I am window 9");
		SmallPanel smallText10 = new SmallPanel("I am window 10");

		// Create the dockable with the content.
		// Our dockables can not float.
		Dockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable7 = new DefaultDockable("Window7", smallText7, "Window 7", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable8 = new DefaultDockable("Window8", smallText8, "Window 8", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable9 = new DefaultDockable("Window9", smallText9, "Window 9", null, DockingMode.ALL - DockingMode.FLOAT);
		Dockable dockable10 = new DefaultDockable("Window10", smallText10, "Window 10", null, DockingMode.ALL - DockingMode.FLOAT);

		// Create the docks.
		TabDock tabDock = new TabDock();
		CompositeGridDock gridDock = new CompositeGridDock();
		gridDock.setChildDockFactory(new TabDockFactory());
		gridDock.setFillMode(CompositeGridDock.FILL_SQUARE_VERTICAL);
		TabDock tabChildDock1 = new TabDock();
		TabDock tabChildDock2 = new TabDock();
		TabDock tabChildDock3 = new TabDock();
		TabDock tabChildDock4 = new TabDock();

		// Add the DockingMode.
		tabChildDock1.addDockable(dockable1, new Position(0));
		tabChildDock2.addDockable(dockable2, new Position(0));
		tabChildDock3.addDockable(dockable3, new Position(0));
		tabChildDock4.addDockable(dockable4, new Position(0));

		// Add the  child dock.
		gridDock.addChildDock(tabChildDock1, new Position(0));
		gridDock.addChildDock(tabChildDock2, new Position(1));
		gridDock.addChildDock(tabChildDock3, new Position(2));
		gridDock.addChildDock(tabChildDock4, new Position(3));

		// Add the dockable.
		tabDock.addDockable(dockable5, new Position(0));
		tabDock.addDockable(dockable6, new Position(1));
		tabDock.addDockable(dockable7, new Position(2));
		tabDock.addDockable(dockable8, new Position(3));
		tabDock.addDockable(dockable9, new Position(4));
		tabDock.addDockable(dockable10, new Position(5));

		// Create the split pane.
		JSplitPane splitPane = DockingManager.getComponentFactory().createJSplitPane();
		add(splitPane, BorderLayout.CENTER);

		// Add the docks to the GUI.
		splitPane.setLeftComponent(gridDock);
		splitPane.setRightComponent(tabDock);
		splitPane.setDividerLocation(400);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

		// Add the root docks to the dock model.
		dockModel.addRootDock("tabDock", tabDock, frame);
		dockModel.addRootDock("gridDock", gridDock, frame);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}

}
