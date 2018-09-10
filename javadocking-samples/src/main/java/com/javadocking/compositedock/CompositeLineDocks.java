package com.javadocking.compositedock;

import com.javadocking.DockingManager;
import com.javadocking.dock.CompositeLineDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dock.factory.TabDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows 2 composite line docks.
 *
 * @author Heidi Rakels
 */
public class CompositeLineDocks extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 450;

	// Constructor.

	public CompositeLineDocks(JFrame frame) {
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
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1");
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2");
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3");
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4");
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5");
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6");

		// Create the tab docks.
		TabDock tabDock1 = new TabDock();
		TabDock tabDock2 = new TabDock();
		TabDock tabDock3 = new TabDock();
		TabDock tabDock4 = new TabDock();
		TabDock tabDock5 = new TabDock();
		TabDock tabDock6 = new TabDock();

		// Add the dockables to these tab docks.
		tabDock1.addDockable(dockable1, new Position(0));
		tabDock2.addDockable(dockable2, new Position(0));
		tabDock3.addDockable(dockable3, new Position(0));
		tabDock4.addDockable(dockable4, new Position(0));
		tabDock5.addDockable(dockable5, new Position(0));
		tabDock6.addDockable(dockable6, new Position(0));

		// Create the composite line docks.
		CompositeLineDock lineDock1 = new CompositeLineDock(
				CompositeLineDock.ORIENTATION_HORIZONTAL, true, new TabDockFactory());
		CompositeLineDock lineDock2 = new CompositeLineDock(
				CompositeLineDock.ORIENTATION_HORIZONTAL, true, new TabDockFactory());
		lineDock1.setRealSizeRectangle(false);
		lineDock2.setRealSizeRectangle(false);

		// Add the child docks to the composite dock.
		lineDock1.addChildDock(tabDock1, new Position(0));
		lineDock1.addChildDock(tabDock2, new Position(1));
		lineDock1.addChildDock(tabDock3, new Position(2));
		lineDock2.addChildDock(tabDock4, new Position(0));
		lineDock2.addChildDock(tabDock5, new Position(1));
		lineDock2.addChildDock(tabDock6, new Position(2));

		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("lineDock1", lineDock1, frame);
		dockModel.addRootDock("bottomDock", lineDock2, frame);

		// Create the split panes.
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(190);

		// Add the root docks to the split pane.
		splitPane.setLeftComponent(lineDock1);
		splitPane.setRightComponent(lineDock2);

		// Add the split pane to the panel.
		add(splitPane, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Composite Line Docks with Tab Docks");

		// Create the panel and add it to the frame.
		CompositeLineDocks panel = new CompositeLineDocks(frame);
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
		Runnable doCreateAndShowGUI = CompositeLineDocks::createAndShowGUI;
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

