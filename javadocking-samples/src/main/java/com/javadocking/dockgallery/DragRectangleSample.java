package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.drag.StaticDraggerFactory;
import com.javadocking.drag.painter.DefaultRectanglePainter;
import com.javadocking.drag.painter.SwDockableDragPainter;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DragRectangleSample extends JPanel {

	public DragRectangleSample(@NotNull JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// The drag rectangles have to be rubberbands.
		float[] pattern = {1.0f, 1.0f};
		Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, pattern, 0.0f);
		DefaultRectanglePainter borderPainter = new DefaultRectanglePainter();
		borderPainter.setStroke(stroke);
		borderPainter.setBorderColor(Color.black);
		borderPainter.setBorderCount(4);
		borderPainter.setBorderShift(1);
		borderPainter.setFillColor(null);
		borderPainter.setArcHeight(0);
		borderPainter.setArcWidth(0);
		SwDockableDragPainter dockableDragPainter = new SwDockableDragPainter(borderPainter);
		StaticDraggerFactory draggerFactory = new StaticDraggerFactory(dockableDragPainter);
		DockingManager.setDraggerFactory(draggerFactory);

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel();
		SmallPanel smallText2 = new SmallPanel();
		SmallPanel smallText3 = new SmallPanel();
		SmallPanel smallText4 = new SmallPanel();

		// Create the dockable with the content.
		// Our dockables can only be docked in tabbed docks.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null);
		DefaultDockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null);
		DefaultDockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null);

		// Create the child tab docks.
		TabDock leftTabDock = new TabDock();
		TabDock rightTabDock = new TabDock();

		// Add the dockables to the tab dock.
		leftTabDock.addDockable(dockable1, new Position(0));
		leftTabDock.addDockable(dockable2, new Position(1));
		rightTabDock.addDockable(dockable3, new Position(0));
		rightTabDock.addDockable(dockable4, new Position(1));

		// Create the split dock.
		SplitDock splitDock = new SplitDock();

		// Add the child docks to the split dock at the left and right.
		splitDock.addChildDock(leftTabDock, new Position(Position.LEFT));
		splitDock.addChildDock(rightTabDock, new Position(Position.RIGHT));
		splitDock.setDividerLocation(290);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		add(splitDock, BorderLayout.CENTER);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 400;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}
}
