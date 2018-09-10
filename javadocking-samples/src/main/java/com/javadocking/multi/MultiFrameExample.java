package com.javadocking.multi;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows root docks in different windows. The dockables can be dragged from one frame
 * to the other.
 *
 * @author Heidi Rakels
 */
public class MultiFrameExample {

	// Static fields.

	public static final int FRAME_WIDTH = 440;
	public static final int FRAME_HEIGHT = 300;

	// Fields.

	private Component component1;
	private Component component2;

	// Public methods.

	public MultiFrameExample(JFrame frame1, JFrame frame2) {

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel("no source");

		// There are 2 owner windows.
		dockModel.addOwner("frame1", frame1);
		dockModel.addOwner("frame2", frame2);

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

		// Create the dockables around the content components.
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", null, DockingMode.ALL);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", null, DockingMode.ALL);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", null, DockingMode.ALL);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", null, DockingMode.ALL);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", null, DockingMode.ALL);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", null, DockingMode.ALL);
		Dockable dockable7 = new DefaultDockable("Window7", textPanel7, "Window 7", null, DockingMode.ALL);
		Dockable dockable8 = new DefaultDockable("Window8", textPanel8, "Window 8", null, DockingMode.ALL);

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

		// Add the root docks.
		JPanel panel1 = new JPanel(new BorderLayout());
		JPanel panel2 = new JPanel(new BorderLayout());
		panel1.add(splitDock1, BorderLayout.CENTER);
		panel2.add(splitDock2, BorderLayout.CENTER);
		component1 = panel1;
		component2 = panel2;

		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("dock1", splitDock1, frame1);
		dockModel.addRootDock("dock2", splitDock2, frame2);

	}

	public static void createAndShowGUI() {
		// Create the frame.
		JFrame frame1 = new JFrame("Frame 1");
		JFrame frame2 = new JFrame("Frame 2");

		// Create the split panel.
		MultiFrameExample test = new MultiFrameExample(frame1, frame2);

		// Get the screen size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Create the frame 1.
		frame1.getContentPane().add(test.component1);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setLocation(screenSize.width / 2 - FRAME_WIDTH - 10, screenSize.height / 2 - FRAME_HEIGHT / 2);
		frame1.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame1.setVisible(true);

		// Create the frame 2.
		frame2.getContentPane().add(test.component2);
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame2.setLocation(screenSize.width / 2 + 10, screenSize.height / 2 - FRAME_HEIGHT / 2);
		frame2.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame2.setVisible(true);
	}

	// Main method.

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = MultiFrameExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
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

