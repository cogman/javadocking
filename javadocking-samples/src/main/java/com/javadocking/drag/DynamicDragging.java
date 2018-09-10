package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.LineDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * The dockables in this example can be dragged dynamically.
 * While dragging they are already docked in the most appropriate dock
 * under the mouse position.
 *
 * @author Heidi Rakels
 */
public class DynamicDragging extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 500;

	// Constructor.

	public DynamicDragging(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// We want dynamic dockable draggers.
		DockingManager.setDraggerFactory(new DynamicDraggerFactory());

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");
		TextPanel textPanel5 = new TextPanel("I am window 5.");
		TextPanel textPanel6 = new TextPanel("I am window 6.");

		// We don't want the dockables to float.
		int dockingModes = DockingMode.ALL - DockingMode.FLOAT;

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon, dockingModes);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon, dockingModes);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon, dockingModes);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", icon, dockingModes);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", icon, dockingModes);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", icon, dockingModes);

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
		JSplitPane tabSplitPane = new JSplitPane();
		tabSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		tabSplitPane.setDividerLocation(300);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(160);

		// Add the root docks to the split panes.
		tabSplitPane.setLeftComponent(leftDock);
		tabSplitPane.setRightComponent(rightDock);
		splitPane.setLeftComponent(topDock);
		splitPane.setRightComponent(tabSplitPane);

		// Add the split pane to the panel.
		add(splitPane, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Dynamically dragging Dockables");

		// Create the panel and add it to the frame.
		DynamicDragging panel = new DynamicDragging(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	// Main method.

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		};
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

