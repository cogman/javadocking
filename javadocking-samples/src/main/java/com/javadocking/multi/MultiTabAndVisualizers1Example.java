package com.javadocking.multi;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows root docks in different tabs of a javax.swing.JTabbedPane.
 * <p>
 * This example shows the usage of a minimizer, a maximizer and an externalizer.
 * The minimizer is a com.javadocking.visualizer.LineMinimizer.
 * The maximizer is a com.javadocking.visualizer.SingleMaximizer.
 * The externalizer is a com.javadocking.visualizer.FloatExternalizer.
 * The dockables have actions to minimize, maximize, and restore itself.
 * Some dockables are already minimized, when the application is started.
 *
 * @author Heidi Rakels
 */
public class MultiTabAndVisualizers1Example extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public MultiTabAndVisualizers1Example(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model and dock model group.
		FloatDockModel dockModel = new FloatDockModel("no source");
		dockModel.addOwner("frame", frame);

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

		// Create the dockables around the content components.
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", null, DockingMode.ALL);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", null, DockingMode.ALL);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", null, DockingMode.ALL);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", null, DockingMode.ALL);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", null, DockingMode.ALL);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", null, DockingMode.ALL);
		Dockable dockable7 = new DefaultDockable("Window7", textPanel7, "Window 7", null, DockingMode.ALL);
		Dockable dockable8 = new DefaultDockable("Window8", textPanel8, "Window 8", null, DockingMode.ALL);
		Dockable dockable9 = new DefaultDockable("Window9", textPanel8, "Window 9", null, DockingMode.ALL);
		Dockable dockable10 = new DefaultDockable("Window10", textPanel8, "Window 10", null, DockingMode.ALL);
		Dockable dockable11 = new DefaultDockable("Window11", textPanel8, "Window 11", null, DockingMode.ALL);
		Dockable dockable12 = new DefaultDockable("Window12", textPanel8, "Window 12", null, DockingMode.ALL);
		Dockable dockable13 = new DefaultDockable("Window13", textPanel8, "Window 13", null, DockingMode.ALL);

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
		leftTabDock2.addDockable(dockable5, new Point(), new Point());
		leftTabDock2.addDockable(dockable6, new Point(), new Point());
		rightTabDock2.addDockable(dockable7, new Point(), new Point());
		rightTabDock2.addDockable(dockable8, new Point(), new Point());

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

		// Add the root docks to the UI.
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Workspace 1", splitDock1);
		tabbedPane.addTab("Workspace 2", splitDock2);

//		// Create the dock model for the docks.
//		FloatDockModel dockModel = new FloatDockModel();
//		dockModel.addOwner("frame0", frame);
//
//		// Give the dock model to the docking manager.
//		DockingManager.setDockModel(dockModel);
//
//		// Create the content components.
//		TextPanel textPanel1 = new TextPanel("I am window 1.");
//		TextPanel textPanel2 = new TextPanel("I am window 2.");
//		TextPanel textPanel3 = new TextPanel("I am window 3.");
//		TextPanel textPanel4 = new TextPanel("I am window 4.");
//		TextPanel textPanel5 = new TextPanel("I am window 5.");
//		TextPanel textPanel6 = new TextPanel("I am window 6.");
//		TextPanel textPanel7 = new TextPanel("I am window 7.");
//		TextPanel textPanel8 = new TextPanel("I am window 8.");
//		TextPanel textPanel9 = new TextPanel("I am window 9.");
//		
//		// Create the dockables around the content components.
//		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
//		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon);
//		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon);
//		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon);
//		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", icon);
//		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", icon);
//		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", icon);
//		Dockable dockable7 = new DefaultDockable("Window7", textPanel7, "Window 7", icon);
//		Dockable dockable8 = new DefaultDockable("Window8", textPanel8, "Window 8", icon);
//		Dockable dockable9 = new DefaultDockable("Window9", textPanel9, "Window 9", icon);
//
//		// Add minimize, maximize, externalize, and close actions to the dockables.
//		dockable1 = addActions(dockable1);
//		dockable2 = addActions(dockable2);
//		dockable3 = addActions(dockable3);
//		dockable4 = addActions(dockable4);
//		dockable5 = addActions(dockable5);
//		dockable6 = addActions(dockable6);
//		dockable7 = addActions(dockable7);
//		dockable8 = addActions(dockable8);
//		dockable9 = addActions(dockable9);
//
//		// Create the child tab dock.
//		TabDock leftTabDock = new TabDock();
//		TabDock rightTabDock = new TabDock();
//		
//		// Add the dockables to the tab dock.
//		leftTabDock.addDockable(dockable1, new Position(0));
//		leftTabDock.addDockable(dockable2, new Position(1));
//		rightTabDock.addDockable(dockable3, new Position(0));
//		rightTabDock.addDockable(dockable4, new Position(1));
//
//		// Create the split dock.
//		SplitDock splitDock = new SplitDock();
//		
//		// Add the child docks to the split dock at the left and right.
//		splitDock.addChildDock(leftTabDock, new Position(Position.LEFT));
//		splitDock.addChildDock(rightTabDock, new Position(Position.RIGHT));
//		splitDock.setDividerLocation(395);
//
//		// Add the root dock to the dock model.
//		dockModel.addRootDock("splitDock", splitDock, frame);

		// Create an externalizer.
		FloatExternalizer externalizer = new FloatExternalizer(frame);
		dockModel.addVisualizer("externalizer", externalizer, frame);

		// Create a minimizer.
		LineMinimizer minimizer = new LineMinimizer(tabbedPane);
		dockModel.addVisualizer("minimizer", minimizer, frame);

		// Create a maximizer.
		SingleMaximizer maximizer = new SingleMaximizer(minimizer);
		dockModel.addVisualizer("maximizer", maximizer, frame);

		// Add the maximizer to the panel.
		this.add(maximizer, BorderLayout.CENTER);

		// Minimize dockables.
		minimizer.visualizeDockable(dockable9);
		minimizer.visualizeDockable(dockable10);
		minimizer.visualizeDockable(dockable11);
		minimizer.visualizeDockable(dockable12);

		// Externalize dockable.
		//externalizer.visualizeDockable(dockable13);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point location = new Point((screenSize.width - 200) / 2, (screenSize.height - 200) / 2);
		externalizer.externalizeDockable(dockable13, location);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Docks in multiple tabs and visualizers");

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		MultiTabAndVisualizers1Example panel = new MultiTabAndVisualizers1Example(frame);
		frame.getContentPane().add(panel);

		// Show.
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		};
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	// Main method.

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

