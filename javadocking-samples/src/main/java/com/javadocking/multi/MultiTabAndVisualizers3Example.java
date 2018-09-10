package com.javadocking.multi;

import com.javadocking.DockingManager;
import com.javadocking.dock.HidableFloatDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.model.DefaultDockModel;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPath;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * This example shows root docks in different tabs of a javax.swing.JTabbedPane.
 * <p>
 * There are 2 workspaces. Dockables 1-7 belong to workspace 1.
 * Dockables 8-14 belong to workspace 2.
 *
 * @author Heidi Rakels
 */
public class MultiTabAndVisualizers3Example extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public MultiTabAndVisualizers3Example(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model and dock model group.
		DockModel dockModel = new DefaultDockModel("no source");
		dockModel.addOwner("frame", frame);

		HidableFloatDock float1 = new HidableFloatDock(frame);
		HidableFloatDock float2 = new HidableFloatDock(frame);
		float2.setHidden(true);
		dockModel.addRootDock("float1", float1, frame);
		dockModel.addRootDock("float2", float2, frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");
		TextPanel textPanel5 = new TextPanel("I am window 5.");
		TextPanel textPanel6 = new TextPanel("I am window 6.");
		TextPanel textPanel7 = new TextPanel("I am window 7.");
		TextPanel textPanel8 = new TextPanel("I am window 8.");
		TextPanel textPanel9 = new TextPanel("I am window 9.");
		TextPanel textPanel10 = new TextPanel("I am window 10.");
		TextPanel textPanel11 = new TextPanel("I am window 11.");
		TextPanel textPanel12 = new TextPanel("I am window 12.");
		TextPanel textPanel13 = new TextPanel("I am window 13.");
		TextPanel textPanel14 = new TextPanel("I am window 14.");

		// Create the dockables around the content components.
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", null, DockingMode.ALL);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", null, DockingMode.ALL);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", null, DockingMode.ALL);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", null, DockingMode.ALL);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", null, DockingMode.ALL);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", null, DockingMode.ALL);
		Dockable dockable7 = new DefaultDockable("Window7", textPanel7, "Window 7", null, DockingMode.ALL);
		Dockable dockable8 = new DefaultDockable("Window8", textPanel8, "Window 8", null, DockingMode.ALL);
		Dockable dockable9 = new DefaultDockable("Window9", textPanel9, "Window 9", null, DockingMode.ALL);
		Dockable dockable10 = new DefaultDockable("Window10", textPanel10, "Window 10", null, DockingMode.ALL);
		Dockable dockable11 = new DefaultDockable("Window11", textPanel11, "Window 11", null, DockingMode.ALL);
		Dockable dockable12 = new DefaultDockable("Window12", textPanel12, "Window 12", null, DockingMode.ALL);
		Dockable dockable13 = new DefaultDockable("Window13", textPanel13, "Window 13", null, DockingMode.ALL);
		Dockable dockable14 = new DefaultDockable("Window14", textPanel13, "Window 14", null, DockingMode.ALL);

		// Add minimize, maximize, externalize, and close actions to the dockables.
		dockable1 = addActions(dockable1);
		dockable2 = addActions(dockable2);
		dockable3 = addActions(dockable3);
		dockable4 = addActions(dockable4);
		dockable5 = addActions(dockable5);
		dockable6 = addActions(dockable6);
		dockable7 = addActions(dockable7);
		dockable8 = addActions(dockable8);
		dockable9 = addActions(dockable9);
		dockable10 = addActions(dockable10);
		dockable11 = addActions(dockable11);
		dockable12 = addActions(dockable12);
		dockable13 = addActions(dockable13);
		dockable14 = addActions(dockable14);

		// Create the child tab docks.
		TabDock leftTabDock1 = new TabDock();
		TabDock rightTabDock1 = new TabDock();
		TabDock leftTabDock2 = new TabDock();
		TabDock rightTabDock2 = new TabDock();

		// Add the dockables to these tab docks.
		leftTabDock1.addDockable(dockable1, new Position(0));
		leftTabDock1.addDockable(dockable2, new Point(), new Point());
		rightTabDock1.addDockable(dockable3, new Point(), new Point());
		rightTabDock1.addDockable(dockable4, new Point(), new Point());
		leftTabDock2.addDockable(dockable8, new Point(), new Point());
		leftTabDock2.addDockable(dockable9, new Point(), new Point());
		rightTabDock2.addDockable(dockable10, new Point(), new Point());
		rightTabDock2.addDockable(dockable11, new Point(), new Point());

		// Create the split docks.
		SplitDock splitDock1 = new SplitDock();
		SplitDock splitDock2 = new SplitDock();

		// Add the child docks to the split dock at the left and right.
		splitDock1.addChildDock(leftTabDock1, new Position(Position.LEFT));
		splitDock1.addChildDock(rightTabDock1, new Position(Position.RIGHT));
		splitDock1.setDividerLocation(FRAME_WIDTH / 2);
		splitDock2.addChildDock(leftTabDock2, new Position(Position.LEFT));
		splitDock2.addChildDock(rightTabDock2, new Position(Position.RIGHT));
		splitDock2.setDividerLocation(FRAME_WIDTH / 2);

		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("dock1", splitDock1, frame);
		dockModel.addRootDock("dock2", splitDock2, frame);

		// Create a minimizer.
		LineMinimizer minimizer1 = new LineMinimizer(splitDock1);
		LineMinimizer minimizer2 = new LineMinimizer(splitDock2);
		dockModel.addVisualizer("minimizer1", minimizer1, frame);
		dockModel.addVisualizer("minimizer2", minimizer2, frame);

		// Create a maximizer.
		SingleMaximizer maximizer1 = new SingleMaximizer(minimizer1);
		SingleMaximizer maximizer2 = new SingleMaximizer(minimizer2);
		dockModel.addVisualizer("maximizer1", maximizer1, frame);
		dockModel.addVisualizer("maximizer2", maximizer2, frame);

		// Add the root docks to the UI.
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Workspace 1", maximizer1);
		tabbedPane.addTab("Workspace 2", maximizer2);

		// Create an externalizer.
		FloatExternalizer externalizer1 = new FloatExternalizer(frame);
		FloatExternalizer externalizer2 = new FloatExternalizer(frame);
		dockModel.addVisualizer("externalizer1", externalizer1, frame);
		dockModel.addVisualizer("externalizer2", externalizer2, frame);
		externalizer2.setHidden(true);

		// Add the maximizer to the panel.
		this.add(tabbedPane, BorderLayout.CENTER);

		// Minimize dockables.
		minimizer1.visualizeDockable(dockable5);
		minimizer1.visualizeDockable(dockable6);
		minimizer2.visualizeDockable(dockable12);
		minimizer2.visualizeDockable(dockable13);

		// Externalize dockable.
		//externalizer.visualizeDockable(dockable13);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point location = new Point((screenSize.width - 200) / 2, (screenSize.height - 200) / 2);
		externalizer1.externalizeDockable(dockable7, location);
		externalizer2.externalizeDockable(dockable14, location);

		// Add docking paths.
		addDockingPath(dockable1);
		addDockingPath(dockable2);
		addDockingPath(dockable3);
		addDockingPath(dockable4);
		addDockingPath(dockable5);
		addDockingPath(dockable6);
		addDockingPath(dockable8);
		addDockingPath(dockable9);
		addDockingPath(dockable10);
		addDockingPath(dockable11);
		addDockingPath(dockable12);
		addDockingPath(dockable13);

		// Listen to the selections of the tabs.
		SingleSelectionModel selectionModel = tabbedPane.getModel();
		selectionModel.addChangeListener(new TabChangelistener(tabbedPane, float1, float2, externalizer1, externalizer2));

	}

	// Private classes.

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Docks in multiple tabs and visualizers");

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		MultiTabAndVisualizers3Example panel = new MultiTabAndVisualizers3Example(frame);
		frame.getContentPane().add(panel);

		// Show.
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = MultiTabAndVisualizers3Example::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	/**
	 * Creates a docking path for the given dockable. It contains the information
	 * how the dockable is docked now. The docking path is added to the docking path
	 * model of the docking manager.
	 *
	 * @param     dockable    The dockable for which a docking path has to be created.
	 * @return The docking path model. Null if the dockable is not docked.
	 */
	private DockingPath addDockingPath(Dockable dockable) {

		if (dockable.getDock() != null) {
			// Create the docking path of the dockable.
			DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
			DockingManager.getDockingPathModel().add(dockingPath);
			return dockingPath;
		}

		return null;

	}

	/**
	 * Decorates the given dockable with a state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
		int[] states = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.MAXIMIZED, DockableState.EXTERNALIZED};
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);
		return wrapper;

	}

	// Main method.

	private class TabChangelistener implements ChangeListener {

		JTabbedPane tabbedPane;
		HidableFloatDock float1;
		HidableFloatDock float2;
		FloatExternalizer externalizer1;
		FloatExternalizer externalizer2;

		public TabChangelistener(JTabbedPane tabbedPane, HidableFloatDock float1, HidableFloatDock float2,
								 FloatExternalizer externalizer1, FloatExternalizer externalizer2) {
			this.tabbedPane = tabbedPane;
			this.float1 = float1;
			this.float2 = float2;
			this.externalizer1 = externalizer1;
			this.externalizer2 = externalizer2;
		}

		// Implementations of ChangeListener.

		public void stateChanged(ChangeEvent changeEvent) {
			int index = tabbedPane.getSelectedIndex();
			if (index == 0) {
				float2.setHidden(true);
				float1.setHidden(false);
				externalizer2.setHidden(true);
				externalizer1.setHidden(false);
			} else {
				float1.setHidden(true);
				float2.setHidden(false);
				externalizer1.setHidden(true);
				externalizer2.setHidden(false);
			}
		}

	}

	/**
	 * This is the class for the content.
	 */
	private class TextPanel extends JPanel implements DraggableContent {

		private JLabel label;

		public TextPanel(String text) {
			super(new FlowLayout());

			// The panel.
			setMinimumSize(new Dimension(80, 80));
			setPreferredSize(new Dimension(150, 150));
			setBackground(Color.white);
			setBorder(BorderFactory.createLineBorder(Color.lightGray));

			// The label.
			label = new JLabel(text);
			label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(label);
		}

		// Implementations of DraggableContent.

		public void addDragListener(DragListener dragListener) {
			addMouseListener(dragListener);
			addMouseMotionListener(dragListener);
			label.addMouseListener(dragListener);
			label.addMouseMotionListener(dragListener);
		}
	}

}

