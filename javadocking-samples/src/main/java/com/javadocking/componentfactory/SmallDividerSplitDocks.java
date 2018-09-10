package com.javadocking.componentfactory;

import com.javadocking.DockingManager;
import com.javadocking.component.DefaultSwComponentFactory;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows a split docks with small dividers.
 *
 * @author Heidi Rakels
 */
public class SmallDividerSplitDocks extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 450;

	// Constructor.

	public SmallDividerSplitDocks(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Give our component factory to the docking manager.
		DockingManager.setComponentFactory(new MyComponentFactory());

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", icon);

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
		splitDock.setDividerLocation(290);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		add(splitDock, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Split dock");

		// Create the panel and add it to the frame.
		SmallDividerSplitDocks panel = new SmallDividerSplitDocks(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
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

	/**
	 * This component factory creates split panes with small dividers.
	 *
	 * @author Heidi Rakels.
	 */
	private class MyComponentFactory extends DefaultSwComponentFactory {

		public JSplitPane createJSplitPane() {

			JSplitPane splitPane = super.createJSplitPane();
			splitPane.setDividerSize(3);
			return splitPane;
		}

	}

}

