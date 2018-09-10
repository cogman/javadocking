package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.Priority;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.DefaultFloatDockFactory;
import com.javadocking.model.FloatDockFactory;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;

import javax.swing.*;
import java.awt.*;

public class TabFloatDockSample extends JPanel {

	private static final int WINDOW_OFFSET = 50;
	private static final int FLOAT_WINDOW_X = 150;
	private static final int FLOAT_WINDOW_Y = 60;

	public TabFloatDockSample(JFrame frame) {
		super(new BorderLayout());

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 400;
		int locationX = (screenSize.width - width) / 2;
		int locationY = (screenSize.height - height) / 2;
		frame.setLocation(locationX, locationY);
		frame.setSize(width, height);
		frame.getContentPane().add(this);

		// Create the factory for the float docks.
		FloatDockFactory floatDockFactory = new MyFloatDockFactory();

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel(floatDockFactory);
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

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
		// These dockables can float and docked in all other positions.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL);
		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL);
		DefaultDockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL);
		DefaultDockable dockable7 = new DefaultDockable("Window7", smallText7, "Window 7", null, DockingMode.ALL);
		DefaultDockable dockable8 = new DefaultDockable("Window8", smallText8, "Window 8", null, DockingMode.ALL);

		// Create the tab and single leaf docks.
		TabDock rootDock = new TabDock();
		TabDock tabDock5 = new TabDock();
		TabDock tabDock6 = new TabDock();
		TabDock tabDock7 = new TabDock();
		TabDock tabDock8 = new TabDock();

		// Add the root dock to the panel.
		add(rootDock);

		// Add the dockables.
		rootDock.addDockable(dockable1, new Position(0));
		rootDock.addDockable(dockable2, new Position(1));
		rootDock.addDockable(dockable3, new Position(2));
		rootDock.addDockable(dockable4, new Position(3));
		tabDock5.addDockable(dockable5, new Position(0));
		tabDock6.addDockable(dockable6, new Position(0));
		tabDock7.addDockable(dockable7, new Position(0));
		tabDock8.addDockable(dockable8, new Position(0));

		// Add the root dock to the dock model.
		dockModel.addRootDock("dock", rootDock, frame);

		// Get the float dock. This is a standard dock of the floating dock model.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		int floatLocationX = locationX + FLOAT_WINDOW_X;
		int floatLocationY = locationY + FLOAT_WINDOW_Y;
		floatDock.addChildDock(tabDock5, new Position(floatLocationX, floatLocationY, 0));
		floatDock.addChildDock(tabDock6, new Position(floatLocationX + WINDOW_OFFSET, floatLocationY + WINDOW_OFFSET, 1));
		floatDock.addChildDock(tabDock7, new Position(floatLocationX + WINDOW_OFFSET * 2, floatLocationY + WINDOW_OFFSET * 2, 2));
		floatDock.addChildDock(tabDock8, new Position(floatLocationX + WINDOW_OFFSET * 3, floatLocationY + WINDOW_OFFSET * 3, 3));

		frame.setVisible(true);

	}

	private class MyFloatDockFactory extends DefaultFloatDockFactory {

		public FloatDock createFloatDock(Window owner) {
			FloatDock floatDock = super.createFloatDock(owner);

			// We want a higher priority for making dockables float.
			floatDock.setDockPriority(Priority.CAN_DOCK_WITH_PRIORITY);

			return floatDock;
		}

	}

}
