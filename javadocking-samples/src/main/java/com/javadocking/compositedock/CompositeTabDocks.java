package com.javadocking.compositedock;

import com.javadocking.DockingManager;
import com.javadocking.dock.CompositeTabDock;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows composite tab docks.
 *
 * @author Heidi Rakels
 */
public class CompositeTabDocks extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public CompositeTabDocks(JFrame frame) {
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
		TextPanel textPanel7 = new TextPanel("I am window 7.");
		TextPanel textPanel8 = new TextPanel("I am window 8.");

		// Create the dockables around the content components.
		DefaultDockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1");
		DefaultDockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2");
		DefaultDockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3");
		DefaultDockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4");
		DefaultDockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5");
		DefaultDockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6");
		DefaultDockable dockable7 = new DefaultDockable("Window7", textPanel7, "Window 7");
		DefaultDockable dockable8 = new DefaultDockable("Window8", textPanel8, "Window 8");
		dockable1.setWithHeader(false);
		dockable2.setWithHeader(false);
		dockable3.setWithHeader(false);
		dockable4.setWithHeader(false);
		dockable5.setWithHeader(false);
		dockable6.setWithHeader(false);
		dockable7.setWithHeader(false);
		dockable8.setWithHeader(false);

		// Create the composite tab docks.
		CompositeTabDock compositeTabDock1 = new CompositeTabDock();
		CompositeTabDock compositeTabDock2 = new CompositeTabDock();
		CompositeTabDock compositeTabDock3 = new CompositeTabDock();

		// Get the child dock factory.
		DockFactory dockFactory = compositeTabDock1.getChildDockFactory();

		// Create the deepest single docks.
		Dock dock1 = dockFactory.createDock(dockable1, DockingMode.SINGLE);
		Dock dock2 = dockFactory.createDock(dockable2, DockingMode.SINGLE);
		Dock dock3 = dockFactory.createDock(dockable3, DockingMode.SINGLE);
		Dock dock4 = dockFactory.createDock(dockable4, DockingMode.SINGLE);
		Dock dock5 = dockFactory.createDock(dockable5, DockingMode.SINGLE);
		Dock dock6 = dockFactory.createDock(dockable6, DockingMode.SINGLE);
		Dock dock7 = dockFactory.createDock(dockable7, DockingMode.SINGLE);
		Dock dock8 = dockFactory.createDock(dockable8, DockingMode.SINGLE);

		// Add the dockables to these tab docks.
		Point position = new Point(0, 0);
		dock1.addDockable(dockable1, position, position);
		dock2.addDockable(dockable2, position, position);
		dock3.addDockable(dockable3, position, position);
		dock4.addDockable(dockable4, position, position);
		dock5.addDockable(dockable5, position, position);
		dock6.addDockable(dockable6, position, position);
		dock7.addDockable(dockable7, position, position);
		dock8.addDockable(dockable8, position, position);

		// Add the child docks to the composite dock.
		compositeTabDock1.addChildDock(dock1, new Position(0));
		compositeTabDock1.addChildDock(dock2, new Position(1));
		compositeTabDock1.addChildDock(dock3, new Position(2));
		compositeTabDock2.addChildDock(dock4, new Position(0));
		compositeTabDock2.addChildDock(dock5, new Position(1));
		compositeTabDock3.addChildDock(dock6, new Position(0));
		compositeTabDock3.addChildDock(dock7, new Position(1));
		compositeTabDock3.addChildDock(dock8, new Position(2));
		compositeTabDock2.addChildDock(compositeTabDock3, new Position(2));


		// Add the 2 root docks to the dock model.
		dockModel.addRootDock("compositeTabDock1", compositeTabDock1, frame);
		dockModel.addRootDock("compositeTabDock2", compositeTabDock2, frame);

		// Create the split panes.
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(300);

		// Add the root docks to the split pane.
		splitPane.setLeftComponent(compositeTabDock1);
		splitPane.setRightComponent(compositeTabDock2);

		// Add the split pane to the panel.
		add(splitPane, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Composite Tab Docks");

		// Create the panel and add it to the frame.
		CompositeTabDocks panel = new CompositeTabDocks(frame);
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
		Runnable doCreateAndShowGUI = CompositeTabDocks::createAndShowGUI;
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

