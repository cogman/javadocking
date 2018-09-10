package com.javadocking.multi;

import com.javadocking.DockingManager;
import com.javadocking.dock.HidableFloatDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.DefaultDockModel;
import com.javadocking.model.DockModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * This example shows root docks in different tabs of a javax.swing.JTabbedPane.
 * It shows also the use of 2 HidableFloatDock.
 * One is only visible when the first tab is selected.
 * The other is only visible when the second tab is selected.
 *
 * @author Heidi Rakels
 */
public class MultiTabAndFloatDockExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 450;

	// Public methods.

	public MultiTabAndFloatDockExample(@NotNull JFrame frame) {
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

		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("dock1", splitDock1, frame);
		dockModel.addRootDock("dock2", splitDock2, frame);

		// Add the root docks to the UI.
		JTabbedPane tabbedPane = new JTabbedPane();
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Workspace 1", splitDock1);
		tabbedPane.addTab("Workspace 2", splitDock2);

		// Listen to the selections of the tabs.
		SingleSelectionModel selectionModel = tabbedPane.getModel();
		selectionModel.addChangeListener(new TabChangelistener(tabbedPane, float1, float2));

	}

	// Private classes.

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Docks in multiple tabs");

		// Create the panel and add it to the frame.
		MultiTabAndFloatDockExample panel = new MultiTabAndFloatDockExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = MultiTabAndFloatDockExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	// Main method.

	private class TabChangelistener implements ChangeListener {

		JTabbedPane tabbedPane;
		HidableFloatDock float1;
		HidableFloatDock float2;

		public TabChangelistener(JTabbedPane tabbedPane, HidableFloatDock float1, HidableFloatDock float2) {
			this.tabbedPane = tabbedPane;
			this.float1 = float1;
			this.float2 = float2;
		}

		// Implementations of ChangeListener.

		public void stateChanged(ChangeEvent changeEvent) {
			int index = tabbedPane.getSelectedIndex();
			if (index == 0) {
				float2.setHidden(true);
				float1.setHidden(false);
			} else {
				float1.setHidden(true);
				float2.setHidden(false);
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

