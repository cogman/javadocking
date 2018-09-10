package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dock.docker.BorderDocker;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import com.javadocking.visualizer.DockingMinimizer;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import java.awt.*;

public class VisualizerSample extends JPanel {

	public VisualizerSample(JFrame frame) {
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
		Dockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL);
		Dockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL);
		Dockable dockable3 = new DefaultDockable("Window3", smallText3, "Window 3", null, DockingMode.ALL);
		Dockable dockable4 = new DefaultDockable("Window4", smallText4, "Window 4", null, DockingMode.ALL);
		Dockable dockable5 = new DefaultDockable("Window5", smallText5, "Window 5", null, DockingMode.ALL);
		Dockable dockable6 = new DefaultDockable("Window6", smallText6, "Window 6", null, DockingMode.ALL);
		Dockable dockable7 = new DefaultDockable("Window7", smallText7, "Window 7", null, DockingMode.ALL);
		Dockable dockable8 = new DefaultDockable("Window8", smallText8, "Window 8", null, DockingMode.ALL);

		// Add minimize and maximize actions to the dockables.
		dockable1 = addActions(dockable1);
		dockable2 = addActions(dockable2);
		dockable3 = addActions(dockable3);
		dockable4 = addActions(dockable4);
		dockable5 = addActions(dockable5);
		dockable6 = addActions(dockable6);
		dockable7 = addActions(dockable7);
		dockable8 = addActions(dockable8);

		// Create the child tab dock.
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
		splitDock.setDividerLocation(295);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Create a docking minimizer.
		BorderDock borderDock = new BorderDock(new ToolBarDockFactory());
		borderDock.setMode(BorderDock.MODE_MINIMIZE_BAR);
		borderDock.setCenterComponent(splitDock);
		BorderDocker borderDocker = new BorderDocker();
		borderDocker.setBorderDock(borderDock);
		DockingMinimizer minimizer = new DockingMinimizer(borderDocker);

		// Create an externalizer.
		FloatExternalizer externalizer = new FloatExternalizer(frame);
		dockModel.addVisualizer("externalizer", externalizer, frame);

		// Create a maximizer and add it to the dock model.
		SingleMaximizer maximizePanel = new SingleMaximizer(borderDock);
		dockModel.addVisualizer("maximizePanel", maximizePanel, frame);

		// Add the minimizer to the dock model, add also the border dock used by the minimizer to the dock model.
		dockModel.addVisualizer("minimizePanel", minimizer, frame);
		dockModel.addRootDock("minimizerBorderDock", borderDock, frame);

		// Add the border dock of the minimizer to this panel.
		this.add(maximizePanel, BorderLayout.CENTER);

		// Minimize dockables.
		minimizer.visualizeDockable(dockable5);
		minimizer.visualizeDockable(dockable6);
		minimizer.visualizeDockable(dockable7);
		minimizer.visualizeDockable(dockable8);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 400;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);
	}

	/**
	 * Decorates the given dockable with a state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), DockableState.statesAll());
		return wrapper;

	}

}
