package com.javadocking.dynamic;

import com.javadocking.DockingManager;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.StateActionDockable;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DynamicDraggerFactory;
import com.javadocking.event.DockingEvent;
import com.javadocking.event.DockingListener;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockingPath;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.*;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * In this example dockables can be moved to different docks.
 * Every dockable can be removed. When the dockable is added again later,
 * the dockable is docked as good as possible like it was docked before.
 * <p>
 * This example uses a float dock model and docking path model.
 *
 * @author Heidi Rakels
 */
public class DynamicExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 650;
	public static final int FRAME_HEIGHT = 500;

	// Fields.

	// Constructors.

	public DynamicExample(JFrame frame) {
		super(new BorderLayout());

		// We want dynamic dockable draggers.
		DockingManager.setDraggerFactory(new DynamicDraggerFactory());

		// Set our custom component factory.
		DockingManager.setComponentFactory(new SampleComponentFactory());

		// Create the content components.
		Book book1 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER1.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER1),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapbelgae150.gif")));
		Book book2 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER2.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER2),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapmaas150.gif")));
		Book book3 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER3.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER3),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/caesar100.jpg")));
		Table table = new Table(Table.TABLE);
		WordList wordList = new WordList();
		Find find = new Find();
		Picture sales = new Picture(new ImageIcon(getClass().getResource("/com/javadocking/resources/images/salesSm.gif")));

		// Create the dockables around the content components.
		Dockable dockable1 = new DefaultDockable("Book1", book1, "Book 1", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")));
		Dockable dockable2 = new DefaultDockable("Book2", book2, "Book 2", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")));
		Dockable dockable3 = new DefaultDockable("Book3", book3, "Book 3", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")));
		Dockable dockable4 = new DefaultDockable("Table", table, "Table", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/table12.gif")));
		Dockable dockable5 = new DefaultDockable("Words", wordList, "Words", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/table12.gif")));
		Dockable dockable6 = new DefaultDockable("Find", find, "Find", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/binocular12.gif")));
		Dockable dockable7 = new DefaultDockable("Sales", sales, "Sales", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/binocular12.gif")));

		// Add a description to the dockable. It will be displayed in the tool tip.
		((DefaultDockable) dockable1).setDescription("<html>De Bello Gallico: Liber 1<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable2).setDescription("<html>De Bello Gallico: Liber 2<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable3).setDescription("<html>De Bello Gallico: Liber 3<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable4).setDescription("Table with hello");
		((DefaultDockable) dockable5).setDescription("Roman Gods");
		((DefaultDockable) dockable6).setDescription("Find a text");
		((DefaultDockable) dockable7).setDescription("Sales of books and CDs");

		// Add close actions to the dockables.
		dockable1 = addActions(dockable1);
		dockable2 = addActions(dockable2);
		dockable3 = addActions(dockable3);
		dockable4 = addActions(dockable4);
		dockable5 = addActions(dockable5);
		dockable6 = addActions(dockable6);
		dockable7 = addActions(dockable7);

		// Create the dock model for the docks because they could not be
		// retrieved from a file.
		/**
		 * The model with the docks and dockables that are visible.
		 */
		final FloatDockModel dockModel = new FloatDockModel("workspace.dck");
		/**
		 * The ID for the owner window.
		 */
		final String frameId = "frame";
		dockModel.addOwner(frameId, frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the tab docks.
		TabDock topTabbedDock = new TabDock();
		TabDock bottomTabbedDock = new TabDock();
		TabDock rightTabbedDock = new TabDock();

		// Add the dockables to these tab docks.
		topTabbedDock.addDockable(dockable1, new Position(0));
		topTabbedDock.addDockable(dockable2, new Position(1));
		topTabbedDock.addDockable(dockable3, new Position(2));
		bottomTabbedDock.addDockable(dockable7, new Position(0));
		rightTabbedDock.addDockable(dockable5, new Position(0));

		// Add the 3 root docks to the dock model.
		dockModel.addRootDock("topdock", topTabbedDock, frame);
		dockModel.addRootDock("bottomdock", bottomTabbedDock, frame);
		dockModel.addRootDock("rightdock", rightTabbedDock, frame);

		// Dockable 6 should float. Add dockable 6 to the float dock of the dock
		// model (this is a default root dock).
		FloatDock floatDock = dockModel.getFloatDock(frame);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		floatDock.addDockable(dockable6, new Point(screenSize.width / 2, screenSize.height / 2), new Point());

		// Add the paths of the dockables to the mapping with the docking paths.
		addDockingPath(dockable1);
		addDockingPath(dockable2);
		addDockingPath(dockable3);
		addDockingPath(dockable5);
		addDockingPath(dockable6);
		addDockingPath(dockable7);

		// Add the path of the dockable 4 that is not docked already.
		// We want it to be docked, when it is made visible, where dockable 7 is docked.
		DockingPath dockingPathToCopy = DockingManager.getDockingPathModel().getDockingPath(dockable7.getID());
		DockingPath dockingPath = DefaultDockingPath.copyDockingPath(dockable4, dockingPathToCopy);
		DockingManager.getDockingPathModel().add(dockingPath);

		// Add the root docks to split panes.
		JSplitPane leftSplitPane = new JSplitPane();
		leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		leftSplitPane.setLeftComponent(topTabbedDock);
		leftSplitPane.setRightComponent(bottomTabbedDock);
		leftSplitPane.setDividerLocation(280);
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftSplitPane);
		splitPane.setRightComponent(rightTabbedDock);
		splitPane.setDividerLocation(450);
		leftSplitPane.setDividerSize(SampleComponentFactory.DEFAULT_DIVIDER_SIZE);
		splitPane.setDividerSize(SampleComponentFactory.DEFAULT_DIVIDER_SIZE);

		// Create a minimizer.
		LineMinimizer minimizePanel = new LineMinimizer(splitPane);
		dockModel.addVisualizer("minimizePanel", minimizePanel, frame);

		// Create a maximizer.
		SingleMaximizer maximizePanel = new SingleMaximizer(minimizePanel);
		dockModel.addVisualizer("maximizePanel", maximizePanel, frame);
		this.add(maximizePanel, BorderLayout.CENTER);

		// Add an externalizer to the dock model.
		dockModel.addVisualizer("externalizer", new FloatExternalizer(frame), frame);

		// Create the menubar.
		// Create the list with the dockables.
		List dockablesList = new ArrayList();
		dockablesList.add(dockable1);
		dockablesList.add(dockable2);
		dockablesList.add(dockable3);
		dockablesList.add(dockable4);
		dockablesList.add(dockable5);
		dockablesList.add(dockable6);
		dockablesList.add(dockable7);
		JMenuBar menuBar = createMenu(dockablesList);
		frame.setJMenuBar(menuBar);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Dynamic Example");

		// Set the default location and size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		DynamicExample panel = new DynamicExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = DynamicExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	/**
	 * Decorates the given dockable with a state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), DockableState.statesAllExceptClosed());
		return wrapper;

	}

	/**
	 * Creates the menubar with two menus: File and Window.
	 * File has the Exit menu item. Window has check boxes for the dockables.
	 *
	 * @param dockables The dockables for which a menu item has to be created.
	 * @return The created menu bar.
	 */
	private JMenuBar createMenu(List dockables) {
		// Create the menu bar.
		JMenuBar menuBar = new JMenuBar();

		// Build the File menu.
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("The file menu");
		menuBar.add(fileMenu);

		// Build the Window menu.
		JMenu windowMenu = new JMenu("Window");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.getAccessibleContext().setAccessibleDescription("The window menu");
		menuBar.add(windowMenu);

		// The JMenuItem for File
		JMenuItem menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Exit te application");
		menuItem.addActionListener(arg0 -> System.exit(0));
		fileMenu.add(menuItem);

		// Iterate over the dockables.
		for (Object dockable1 : dockables) {
			// Get the dockable.
			Dockable dockable = (Dockable) dockable1;

			// Create the checkboxmenu for the dockable.
			JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(dockable);
			windowMenu.add(cbMenuItem);


		}

		return menuBar;
	}

	private DockingPath addDockingPath(Dockable dockable) {

		if (dockable.getDock() != null) {
			// Create the docking path of the dockable.
			DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
			DockingManager.getDockingPathModel().add(dockingPath);
			return dockingPath;
		}

		return null;

	}

	// Main method.

	/**
	 * This is the check box menu item to add or remove the dockable.
	 */
	private class DockableMenuItem extends JCheckBoxMenuItem {
		public DockableMenuItem(Dockable dockable) {
			super(dockable.getTitle(), dockable.getIcon());

			setSelected(dockable.getDock() != null);

			DockableMediator dockableMediator = new DockableMediator(dockable, this);
			dockable.addDockingListener(dockableMediator);
			addItemListener(dockableMediator);
		}
	}

	private class DockableMediator implements ItemListener, DockingListener {

		private Dockable dockable;
		private Action closeAction;
		private Action restoreAction;
		private JMenuItem dockableMenuItem;

		public DockableMediator(Dockable dockable, JMenuItem dockableMenuItem) {

			this.dockable = dockable;
			this.dockableMenuItem = dockableMenuItem;
			closeAction = new DefaultDockableStateAction(dockable, DockableState.CLOSED);
			restoreAction = new DefaultDockableStateAction(dockable, DockableState.NORMAL);

		}

		public void itemStateChanged(ItemEvent itemEvent) {

			dockable.removeDockingListener(this);
			if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
				// Close the dockable.
				closeAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Close"));
			} else {
				// Restore the dockable.
				restoreAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Restore"));
			}
			dockable.addDockingListener(this);

		}

		public void dockingChanged(DockingEvent dockingEvent) {
			if (dockingEvent.getDestinationDock() != null) {
				dockableMenuItem.removeItemListener(this);
				dockableMenuItem.setSelected(true);
				dockableMenuItem.addItemListener(this);
			} else {
				dockableMenuItem.removeItemListener(this);
				dockableMenuItem.setSelected(false);
				dockableMenuItem.addItemListener(this);
			}
		}

		public void dockingWillChange(DockingEvent dockingEvent) {
		}

	}

}

