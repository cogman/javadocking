package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.drag.painter.*;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.Book;
import com.javadocking.util.DeBelloGallico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * This example shows combinations of DockableDragPainters.
 *
 * @author Heidi Rakels
 */
public class DragPainter extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public DragPainter(JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content for the dockable.
		Book book1 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER1.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER1),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapbelgae150.gif")));
		Book book2 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER2.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER2),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapmaas150.gif")));
		Book book3 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER3.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER3),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/caesar100.jpg")));
		Book book4 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER4.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER4),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapbelgae150.gif")));

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", book1, "Book 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", book2, "Book 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", book3, "Book 3", icon);
		Dockable dockable4 = new DefaultDockable("Window4", book4, "Book 4", icon);

		// Create the child tab docks.
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
		splitDock.setDividerLocation(390);

		// Get the float dock. This is a standard dock of the floating dock model.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new SingleDockFactory());
		floatDock.setDockPriority(Priority.CAN_DOCK_WITH_PRIORITY);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		add(splitDock, BorderLayout.CENTER);

		// Create the menubar.
		JMenuBar menuBar = createMenu();
		frame.setJMenuBar(menuBar);

	}

	public static void main(String[] args) {

		// Create the frame.
		JFrame frame = new JFrame("Split dock");

		// Create the panel and add it to the frame.
		DragPainter panel = new DragPainter(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	public static void createAndShowGUI() {
		Runnable doCreateAndShowGUI = DragPainter::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	/**
	 * Creates the menubar with menu: Drag Painting.
	 *
	 * @return The created menu bar.
	 */
	private JMenuBar createMenu() {
		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		// Build the Dragging menu.
		JMenu draggingMenu = new JMenu("Drag Painting");
		draggingMenu.setMnemonic(KeyEvent.VK_D);
		draggingMenu.getAccessibleContext().setAccessibleDescription("The Dragging Menu");
		menuBar.add(draggingMenu);

		// The JMenuItems for the draggers.
		DockableDragPainter swDockableDragPainterWithoutLabel = new SwDockableDragPainter(new DefaultRectanglePainter(), false);
		DockableDragPainter swDockableDragPainterWithLabel = new SwDockableDragPainter(new RectangleDragComponentFactory(new DefaultRectanglePainter(), true), false);
		DockableDragPainter swDockableDragPainterWithoutLabelNoFloat = new SwDockableDragPainter(new DefaultRectanglePainter());
		DockableDragPainter swDockableDragPainterWithLabelNoFloat = new SwDockableDragPainter(new RectangleDragComponentFactory(new DefaultRectanglePainter(), true));
		DockableDragPainter labelDockableDragPainter = new LabelDockableDragPainter();
		DockableDragPainter imageDockableDragPainter = new ImageDockableDragPainter();
		DockableDragPainter windowDockableDragPainterWithoutLabel = new WindowDockableDragPainter(new DefaultRectanglePainter());
		DockableDragPainter windowDockableDragPainterWithLabel = new WindowDockableDragPainter(new DefaultRectanglePainter(), true);
		DockableDragPainter transparentWindowDockableDragPainterWithoutLabel = new TransparentWindowDockableDragPainter(new DefaultRectanglePainter());
		DockableDragPainter transparentWindowDockableDragPainterWithLabel = new TransparentWindowDockableDragPainter(new DefaultRectanglePainter(), true);
		ButtonGroup group = new ButtonGroup();
		DraggingMenuItem[] draggingMenuItems = new DraggingMenuItem[8];
		draggingMenuItems[0] = new DraggingMenuItem("Rectangle", swDockableDragPainterWithoutLabel, null, false);
		draggingMenuItems[1] = new DraggingMenuItem("Rectangle with image", swDockableDragPainterWithoutLabel, imageDockableDragPainter, true);
		draggingMenuItems[2] = new DraggingMenuItem("Labeled rectangle", swDockableDragPainterWithLabel, null, false);
		draggingMenuItems[3] = new DraggingMenuItem("Rectangle with dragged label", swDockableDragPainterWithoutLabel, labelDockableDragPainter, false);
		draggingMenuItems[4] = new DraggingMenuItem("Rectangle with window", swDockableDragPainterWithoutLabelNoFloat, windowDockableDragPainterWithoutLabel, false);
		draggingMenuItems[5] = new DraggingMenuItem("Labeled rectangle with labeled window", swDockableDragPainterWithLabelNoFloat, windowDockableDragPainterWithLabel, false);
		draggingMenuItems[6] = new DraggingMenuItem("Rectangle with transparent window (only fast computers)", swDockableDragPainterWithoutLabelNoFloat, transparentWindowDockableDragPainterWithoutLabel, false);
		draggingMenuItems[7] = new DraggingMenuItem("Labeled rectangle with labeled transparent window (only fast computers)", swDockableDragPainterWithLabelNoFloat, transparentWindowDockableDragPainterWithLabel, false);
		for (final DraggingMenuItem draggingMenuItem : draggingMenuItems) {
			draggingMenu.add(draggingMenuItem);
			group.add(draggingMenuItem);
		}

		return menuBar;
	}

	// Main method.

	/**
	 * A check box menu item to enable a dragger.
	 */
	private class DraggingMenuItem extends JRadioButtonMenuItem {

		// Constructor.

		public DraggingMenuItem(String title, DockableDragPainter basicDockableDragPainter, DockableDragPainter additionalDockableDragPainter, boolean selected) {
			super(title);

			// Create the dockable drag painter and dragger factory.
			CompositeDockableDragPainter compositeDockableDragPainter = new CompositeDockableDragPainter();
			compositeDockableDragPainter.addPainter(basicDockableDragPainter);
			if (additionalDockableDragPainter != null) {
				compositeDockableDragPainter.addPainter(additionalDockableDragPainter);
			}
			DraggerFactory draggerFactory = new StaticDraggerFactory(compositeDockableDragPainter);

			// Give this dragger factory to the docking manager.
			if (selected) {
				DockingManager.setDraggerFactory(draggerFactory);
				setSelected(true);
			}

			// Add a dragging listener as action listener.
			addActionListener(new DraggingListener(draggerFactory));

		}

	}

	/**
	 * A listener that installs a dragger factory.
	 */
	private class DraggingListener implements ActionListener {

		// Fields.

		private DraggerFactory draggerFactory;

		// Constructor.

		public DraggingListener(DraggerFactory draggerFactory) {
			this.draggerFactory = draggerFactory;
		}

		// Implementations of ItemListener.

		public void actionPerformed(ActionEvent actionEvent) {
			DockingManager.setDraggerFactory(draggerFactory);
		}

	}

}

