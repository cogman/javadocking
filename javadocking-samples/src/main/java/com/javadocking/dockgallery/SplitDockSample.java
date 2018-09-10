package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SingleDock;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;

import javax.swing.*;
import java.awt.*;

public class SplitDockSample extends JPanel {

	public SplitDockSample(JFrame frame) {
		super(new BorderLayout());

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 800;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);
		frame.getContentPane().add(this);

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
		SmallPanel smallText5 = new SmallPanel();

		// Create the dockable with the content.
		// Our dockables can not float.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL - DockingMode.FLOAT);

		// Create the split docks.
		SplitDock northEastDock = new SplitDock();
		SplitDock southEastDock = new SplitDock();
		SplitDock eastDock = new SplitDock();
		SplitDock rootDock = new SplitDock();

		// Add the root dock to the panel.
		add(rootDock);

		// Create a single dock factory as leaf child dock factory for the split docks (because you don't want tabs).
		DockFactory leafChildDockFactory = new SingleDockFactory();
		northEastDock.setChildDockFactory(leafChildDockFactory);
		northEastDock.getCompositeChildDockFactory().setChildDockFactory(leafChildDockFactory);
		southEastDock.setChildDockFactory(leafChildDockFactory);
		southEastDock.getCompositeChildDockFactory().setChildDockFactory(leafChildDockFactory);
		eastDock.setChildDockFactory(leafChildDockFactory);
		eastDock.getCompositeChildDockFactory().setChildDockFactory(leafChildDockFactory);
		rootDock.setChildDockFactory(leafChildDockFactory);
		rootDock.getCompositeChildDockFactory().setChildDockFactory(leafChildDockFactory);

		// Create the leaf docks for the dockables.
		SingleDock singleDock1 = new SingleDock();
		SingleDock singleDock2 = new SingleDock();
		SingleDock singleDock3 = new SingleDock();
		SingleDock singleDock4 = new SingleDock();
		SingleDock singleDock5 = new SingleDock();
		singleDock1.addDockable(dockable1, SingleDock.SINGLE_POSITION);
		singleDock2.addDockable(dockable2, SingleDock.SINGLE_POSITION);
		singleDock3.addDockable(dockable3, SingleDock.SINGLE_POSITION);
		singleDock4.addDockable(dockable4, SingleDock.SINGLE_POSITION);
		singleDock5.addDockable(dockable5, SingleDock.SINGLE_POSITION);

		// Add the single leaf docks to the split docks.
		rootDock.addChildDock(singleDock1, new Position(Position.LEFT));
		rootDock.addChildDock(eastDock, new Position(Position.RIGHT));
		rootDock.setDividerLocation(width / 2);
		eastDock.addChildDock(northEastDock, new Position(Position.TOP));
		eastDock.addChildDock(southEastDock, new Position(Position.BOTTOM));
		eastDock.setDividerLocation(height / 2);
		northEastDock.addChildDock(singleDock2, new Position(Position.TOP));
		northEastDock.addChildDock(singleDock3, new Position(Position.BOTTOM));
		northEastDock.setDividerLocation(height / 4);
		southEastDock.addChildDock(singleDock4, new Position(Position.LEFT));
		southEastDock.addChildDock(singleDock5, new Position(Position.RIGHT));
		southEastDock.setDividerLocation(width / 4);

		// Add the root docks to the dock model.
		dockModel.addRootDock("dock", rootDock, frame);

	}
}
