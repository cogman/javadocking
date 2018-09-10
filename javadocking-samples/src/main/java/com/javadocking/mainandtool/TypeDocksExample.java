package com.javadocking.mainandtool;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows a centralized dock, in which the main dockables can be docked.
 * There are also tool docks at the borders in which the tool dockables can be docked.
 *
 * @author Heidi Rakels
 */
public class TypeDocksExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 600;

	// Constructor.

	public TypeDocksExample(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am main window 1.");
		TextPanel textPanel2 = new TextPanel("I am main window 2.");
		TextPanel textPanel3 = new TextPanel("I am main window 3.");
		TextPanel textPanel4 = new TextPanel("I am main window 4.");
		TextPanel textPanel5 = new TextPanel("I am tool window 1.");
		TextPanel textPanel6 = new TextPanel("I am tool window 2.");
		TextPanel textPanel7 = new TextPanel("I am tool window 3.");
		TextPanel textPanel8 = new TextPanel("I am tool window 4.");

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		DefaultDockable dockable1 = new TypeDockable("Main1", textPanel1, "Main 1", icon, true);
		DefaultDockable dockable2 = new TypeDockable("Main2", textPanel2, "Main 2", icon, true);
		DefaultDockable dockable3 = new TypeDockable("Main3", textPanel3, "Main 3", icon, true);
		DefaultDockable dockable4 = new TypeDockable("Main4", textPanel4, "Main 4", icon, true);
		DefaultDockable dockable5 = new TypeDockable("Tool1", textPanel5, "Tool 1", false);
		DefaultDockable dockable6 = new TypeDockable("Tool2", textPanel6, "Tool 2", false);
		DefaultDockable dockable7 = new TypeDockable("Tool3", textPanel7, "Tool 3", false);
		DefaultDockable dockable8 = new TypeDockable("Tool4", textPanel8, "Tool 4", false);

		// Create the child tab docks.
		TabDock leftTabDock = new TypeTabDock(false);
		TabDock southTabDock = new TypeTabDock(false);
		TabDock centerTabDock = new TypeTabDock(true);

		// Add the dockables to the tab dock.
		centerTabDock.addDockable(dockable1, new Position(0));
		centerTabDock.addDockable(dockable2, new Position(1));
		centerTabDock.addDockable(dockable3, new Position(2));
		centerTabDock.addDockable(dockable4, new Position(3));
		leftTabDock.addDockable(dockable5, new Position(0));
		leftTabDock.addDockable(dockable6, new Position(1));
		southTabDock.addDockable(dockable7, new Position(0));
		southTabDock.addDockable(dockable8, new Position(1));

		// Create the split dock.
		SplitDock splitDock = new TypeSplitDock(false);
		SplitDock rightSplitDock = new TypeSplitDock(false);
		SplitDock centerSplitDock = new TypeSplitDock(true);
		centerSplitDock.setRemoveLastEmptyChild(false);

		// Add the child docks to the split dock at the left and right.
		centerSplitDock.addChildDock(centerTabDock, new Position(Position.CENTER));
		rightSplitDock.addChildDock(centerSplitDock, new Position(Position.TOP));
		rightSplitDock.addChildDock(southTabDock, new Position(Position.BOTTOM));
		rightSplitDock.setDividerLocation(400);
		splitDock.addChildDock(leftTabDock, new Position(Position.LEFT));
		splitDock.addChildDock(rightSplitDock, new Position(Position.RIGHT));
		splitDock.setDividerLocation(290);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		add(splitDock, BorderLayout.CENTER);

	}

	public static void main(String[] args) {

		// Create the frame.
		JFrame frame = new JFrame("Split dock");

		// Create the panel and add it to the frame.
		TypeDocksExample panel = new TypeDocksExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	// Main method.

	public static void createAndShowGUI() {
		Runnable doCreateAndShowGUI = TypeDocksExample::createAndShowGUI;
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

