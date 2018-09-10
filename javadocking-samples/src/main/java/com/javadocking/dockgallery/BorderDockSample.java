package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.factory.TabDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BorderDockSample extends JPanel {

	public BorderDockSample(@NotNull JFrame frame) {
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
		SmallPanel smallText5 = new SmallPanel();

		// Create the dockable with the content.
		Dockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL);
		Dockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL);
		Dockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL);
		Dockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL);
		Dockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL);

		// Create the docks.
		BorderDock borderDock = new BorderDock();
		borderDock.setChildDockFactory(new TabDockFactory());
		SingleDock singleDock1 = new SingleDock();
		TabDock tabDock2 = new TabDock();
		TabDock tabDock3 = new TabDock();
		TabDock tabDock4 = new TabDock();
		TabDock tabDock5 = new TabDock();

		// Add the dockable.
		singleDock1.addDockable(dockable1, SingleDock.SINGLE_POSITION);
		tabDock2.addDockable(dockable2, new Position(0));
		tabDock3.addDockable(dockable3, new Position(0));
		tabDock4.addDockable(dockable4, new Position(0));
		tabDock5.addDockable(dockable5, new Position(0));
		borderDock.addChildDock(singleDock1, new Position(Position.CENTER));
		borderDock.addChildDock(tabDock2, new Position(Position.TOP));
		borderDock.addChildDock(tabDock3, new Position(Position.LEFT));
		borderDock.addChildDock(tabDock4, new Position(Position.RIGHT));
		borderDock.addChildDock(tabDock5, new Position(Position.BOTTOM));

		// Add the root dock to the GUI.
		add(borderDock);

		// Add the only root dock to the dock model.
		dockModel.addRootDock("borderDock", borderDock, frame);

		// Give more priority for floating.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setDockPriority(Priority.CAN_DOCK_WITH_PRIORITY);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 800;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}

}
