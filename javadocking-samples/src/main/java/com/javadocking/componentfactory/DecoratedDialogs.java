package com.javadocking.componentfactory;

import com.javadocking.DockingManager;
import com.javadocking.component.DefaultSwComponentFactory;
import com.javadocking.dock.*;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DraggableContent;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows 2 floating dockables in decorated dialogs.
 */
public class DecoratedDialogs extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 450;

	// Constructor.

	public DecoratedDialogs(JFrame frame) {
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

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon);

		// Create the single child docks for the float dock.
		SingleDock singleDock1 = new SingleDock();
		SingleDock singleDock2 = new SingleDock();

		// Add the dockables to the single docks.
		singleDock1.addDockable(dockable1, SingleDock.SINGLE_POSITION);
		singleDock2.addDockable(dockable2, SingleDock.SINGLE_POSITION);

		// Create the tab dock.
		TabDock tabDock = new TabDock();

		// Add the dockable to the tab dock.
		tabDock.addDockable(dockable3, new Position(0));

		// The position for the float child docks.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = screenSize.width / 2 - 100;
		int y = screenSize.height / 2 - 100;

		// Get the float dock. This is a standard dock of the floating dock model.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new SingleDockFactory());
		floatDock.setDockPriority(Priority.CAN_DOCK_WITH_PRIORITY);

		// Add the child docks to the float dock.
		floatDock.addChildDock(singleDock1, new Position(x, y, 0));
		floatDock.addChildDock(singleDock2, new Position(x + 50, y + 50, 1));

		// Add the 1 root dock to the dock model.
		dockModel.addRootDock("tabDock", tabDock, frame);

		// Add the split pane to the panel.
		add(tabDock, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Float dock");

		// Create the panel and add it to the frame.
		DecoratedDialogs panel = new DecoratedDialogs(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = DecoratedDialogs::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	// Main method.

	/**
	 * This component factory creates decorated dialogs for the floating windows.
	 *
	 * @author Heidi Rakels.
	 */
	private class MyComponentFactory extends DefaultSwComponentFactory {

		public JDialog createJDialog(Window owner) {

			// Create the dialog.
			JDialog dialog = null;
			if (owner instanceof JDialog) {
				dialog = new JDialog((JDialog) owner, ((JDialog) owner).getTitle());
			} else if (owner instanceof JFrame) {
				dialog = new JDialog((JFrame) owner, ((JFrame) owner).getTitle());
			} else {
				dialog = new JDialog((JFrame) null, "");
			}

			return dialog;

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

