package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.LineDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DynamicDraggerFactory;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DynamicSample extends JPanel {

	public DynamicSample(@NotNull JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// We want dynamic dockable draggers.
		DockingManager.setDraggerFactory(new DynamicDraggerFactory());

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel("I am window 1");
		SmallPanel smallText2 = new SmallPanel("I am window 2");
		SmallPanel smallText3 = new SmallPanel("I am window 3");
		SmallPanel smallText4 = new SmallPanel("I am window 4");
		SmallPanel smallText5 = new SmallPanel("I am window 5");
		SmallPanel smallText6 = new SmallPanel("I am window 6");

		// Create the dockable with the content.
		// Our dockables can not float.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL - DockingMode.FLOAT);
		DefaultDockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL - DockingMode.FLOAT);
//		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL );
//		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL );
//		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL );
//		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL);
//		DefaultDockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL );
//		DefaultDockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL );

		// Create the docks.
		TabDock leftDock = new TabDock();
		TabDock rightDock = new TabDock();
		LineDock topDock = new LineDock();
		topDock.setRealSizeRectangle(false);

		// Add the dockables to the tab docks.
		leftDock.addDockable(dockable1, new Position(0));
		rightDock.addDockable(dockable2, new Position(0));
		topDock.addDockable(dockable3, new Position(0));
		topDock.addDockable(dockable4, new Position(1));
		topDock.addDockable(dockable5, new Position(2));
		topDock.addDockable(dockable6, new Position(3));

		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("leftDock", leftDock, frame);
		dockModel.addRootDock("rightDock", rightDock, frame);
		dockModel.addRootDock("topDock", topDock, frame);

		// Create the split panes.
		JSplitPane tabSplitPane = DockingManager.getComponentFactory().createJSplitPane();
		tabSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		tabSplitPane.setDividerLocation(300);
		JSplitPane splitPane = DockingManager.getComponentFactory().createJSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(160);

		// Add the root docks to the split panes.
		tabSplitPane.setLeftComponent(leftDock);
		tabSplitPane.setRightComponent(rightDock);
		splitPane.setLeftComponent(topDock);
		splitPane.setRightComponent(tabSplitPane);

		// Add the split pane to the panel.
		add(splitPane, BorderLayout.CENTER);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 600;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}
}
