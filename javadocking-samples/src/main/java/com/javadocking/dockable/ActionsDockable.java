package com.javadocking.dockable;

import com.javadocking.DockingManager;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This example shows 1 window with a simple dockable that has actions.
 *
 * @author Heidi Rakels
 */
public class ActionsDockable extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 500;
	public static final int FRAME_HEIGHT = 400;

	// Constructor.

	public ActionsDockable(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content component.
		TextPanel textPanel = new TextPanel("I am window 1.");

		// Create the dockable around the content component.
		Dockable dockable = new DefaultDockable("Window1", textPanel, "Window", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")));

		// Decorate the dockable with a close action.		
		dockable = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), DockableState.statesClosed());

		// Decorate the dockable other actions.
		MessageAction helloAction = new MessageAction(this, "Hello", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/hello12.gif")), "Hello world!");
		MessageAction cautionAction = new MessageAction(this, "Caution", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/caution12.gif")), "Be Careful!");
		Action[][] actions = new Action[1][];
		actions[0] = new Action[2];
		actions[0][0] = helloAction;
		actions[0][1] = cautionAction;
		dockable = new ActionDockable(dockable, actions);

		// Create the single dock.
		SingleDock singleDock = new SingleDock();

		// Add the dockables to these tab docks.
		singleDock.addDockable(dockable, SingleDock.SINGLE_POSITION);

		// Add the root dock to the dock model.
		dockModel.addRootDock("singledock", singleDock, frame);

		// Add the dock to the panel.
		add(singleDock, BorderLayout.CENTER);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Dockable with Actions");

		// Create the panel and add it to the frame.
		ActionsDockable panel = new ActionsDockable(frame);
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
			this.addMouseListener(dragListener);
			this.addMouseMotionListener(dragListener);
			label.addMouseListener(dragListener);
			label.addMouseMotionListener(dragListener);
		}

	}

	private class MessageAction extends AbstractAction {

		private Component parentComponent;
		private String message;
		private String name;

		public MessageAction(Component parentComponent, String name, Icon icon, String message) {
			super(name, icon);
			this.message = message;
			this.name = name;
			this.parentComponent = parentComponent;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			JOptionPane.showMessageDialog(parentComponent,
					message, name, JOptionPane.INFORMATION_MESSAGE);
		}

	}

}

