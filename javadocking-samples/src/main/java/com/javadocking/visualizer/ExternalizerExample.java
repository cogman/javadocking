package com.javadocking.visualizer;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows the usage of a com.javadocking.visualizer.Externalizer.
 * The externalizer is a com.javadocking.visualizer.FloatExternalizer.
 * 2 dockables are already externalized, when the application is started.
 *
 * @author Heidi Rakels
 */
public class ExternalizerExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public ExternalizerExample(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");
		TextPanel textPanel5 = new TextPanel("I am window 5.");
		TextPanel textPanel6 = new TextPanel("I am window 6.");

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", icon);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", icon);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", icon);

		// Add minimize and maximize actions to the dockables.
		dockable1 = addActions(dockable1);
		dockable2 = addActions(dockable2);
		dockable3 = addActions(dockable3);
		dockable4 = addActions(dockable4);
		dockable5 = addActions(dockable5);
		dockable6 = addActions(dockable6);

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
		splitDock.setDividerLocation(395);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		this.add(splitDock, BorderLayout.CENTER);

		// Add an externalizer to the dock model.
		FloatExternalizer externalizer = new FloatExternalizer(frame);
		dockModel.addVisualizer("externalizer", externalizer, frame);

		// Externalize dockables.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point location = new Point((screenSize.width - 200) / 2, (screenSize.height - 200) / 2);
		externalizer.externalizeDockable(dockable5, location);
		location.move(location.x + 60, location.y + 60);
		externalizer.externalizeDockable(dockable6, location);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Split dock");

		// Set the frame properties.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		ExternalizerExample panel = new ExternalizerExample(frame);
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
		int[] states = {DockableState.NORMAL, DockableState.EXTERNALIZED};
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

