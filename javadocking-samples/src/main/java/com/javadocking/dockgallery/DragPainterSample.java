package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.drag.DraggerFactory;
import com.javadocking.drag.StaticDraggerFactory;
import com.javadocking.drag.painter.*;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.Book;
import com.javadocking.util.DeBelloGallico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class DragPainterSample extends JPanel {

	public DragPainterSample(JFrame frame) {
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


		// Create the dockable with the content.
		// Our dockables can only be docked in tabbed docks.
		DefaultDockable dockable1 = new DefaultDockable("Window1", book1, "Book 1", null);
		DefaultDockable dockable2 = new DefaultDockable("Window2", book2, "Book 2", null);
		DefaultDockable dockable3 = new DefaultDockable("Window3", book3, "Book 3", null);
		DefaultDockable dockable4 = new DefaultDockable("Window4", book4, "Book 4", null);

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

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Add the split dock to the panel.
		add(splitDock, BorderLayout.CENTER);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 800;
		int height = 500;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);

		// Create the menubar.
		JMenuBar menuBar = createMenu();
		frame.setJMenuBar(menuBar);
	}

	/**
	 * Creates the menubar with menus: File and Drag Painting.
	 *
	 * @return The created menu bar.
	 */
	private JMenuBar createMenu() {
		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		// Build the File menu.
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("The File Menu");
		menuBar.add(fileMenu);

		// Build the Dragging menu.
		JMenu draggingMenu = new JMenu("Drag Painting");
		draggingMenu.setMnemonic(KeyEvent.VK_D);
		draggingMenu.getAccessibleContext().setAccessibleDescription("The Dragging Menu");
		menuBar.add(draggingMenu);

		// The JMenuItem for File
		JMenuItem menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Exit te application");
		menuItem.addActionListener(arg0 -> System.exit(0));
		fileMenu.add(menuItem);

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
		for (int index = 0; index < draggingMenuItems.length; index++) {
			draggingMenu.add(draggingMenuItems[index]);
			group.add(draggingMenuItems[index]);
		}

		return menuBar;

	}

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
