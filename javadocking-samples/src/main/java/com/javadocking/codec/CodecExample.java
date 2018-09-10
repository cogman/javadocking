package com.javadocking.codec;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.event.DockingEvent;
import com.javadocking.event.DockingListener;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockingPath;
import com.javadocking.model.FloatDockModel;
import com.javadocking.model.codec.DockModelPropertiesDecoder;
import com.javadocking.model.codec.DockModelPropertiesEncoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * In this example every dockable can be closed. When the dockable is added again later,
 * the dockable is docked as good as possible like it was docked before.
 * <p>
 * When the application is stopped, the workspace is saved.
 * When the application is restarted again, the workspace is recovered.
 * The dockables and docks are showed in the same state as when they were closed.
 * <p>
 * This example uses a float dock model.
 * The model can be saved with a dock model encoder.
 * The model can be reloaded with a dock model decoder.
 *
 * @author Heidi Rakels
 */
public class CodecExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 600;
	public static final int FRAME_HEIGHT = 450;
	public static final String SOURCE = "codec_1_5.dck";

	// Fields.

	/**
	 * The ID for the owner window.
	 */
	private String frameId = "frame";
	/**
	 * The model with the docks and dockables.
	 */
	private FloatDockModel dockModel;

	// Constructors.

	public CodecExample(JFrame frame) {
		super(new BorderLayout());

		// Listen when the frame is closed. The workspace should be saved.
		frame.addWindowListener(new WorkspaceSaver());

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");
		TextPanel textPanel5 = new TextPanel("I am window 5.");
		TextPanel textPanel6 = new TextPanel("I am window 6.");

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", textPanel1, "Window 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", textPanel2, "Window 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", textPanel3, "Window 3", icon);
		Dockable dockable4 = new DefaultDockable("Window4", textPanel4, "Window 4", icon);
		Dockable dockable5 = new DefaultDockable("Window5", textPanel5, "Window 5", icon);
		Dockable dockable6 = new DefaultDockable("Window6", textPanel6, "Window 6", icon);

		// Add close actions to the dockables.
		dockable1 = new StateActionDockable(dockable1, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		dockable2 = new StateActionDockable(dockable2, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		dockable3 = new StateActionDockable(dockable3, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		dockable4 = new StateActionDockable(dockable4, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		dockable5 = new StateActionDockable(dockable5, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		dockable6 = new StateActionDockable(dockable6, new DefaultDockableStateActionFactory(), DockableState.statesClosed());

		// Try to decode the dock model from file.
		DockModelPropertiesDecoder dockModelDecoder = new DockModelPropertiesDecoder();
		if (dockModelDecoder.canDecodeSource(SOURCE)) {
			try {
				// Create the map with the dockables, that the decoder needs.
				Map dockablesMap = new HashMap();
				dockablesMap.put(dockable1.getID(), dockable1);
				dockablesMap.put(dockable2.getID(), dockable2);
				dockablesMap.put(dockable3.getID(), dockable3);
				dockablesMap.put(dockable4.getID(), dockable4);
				dockablesMap.put(dockable5.getID(), dockable5);
				dockablesMap.put(dockable6.getID(), dockable6);

				// Create the map with the owner windows, that the decoder needs.
				Map ownersMap = new HashMap();
				ownersMap.put(frameId, frame);

				// Create the map with the visualizers, that the decoder needs.
				Map visualizersMap = null;
//				visualizersMap.put("maximizer", maximizer);
//				visualizersMap.put("minimizer", minimizer);

				// Decode the file.
				dockModel = (FloatDockModel) dockModelDecoder.decode(SOURCE, dockablesMap, ownersMap, visualizersMap);
			} catch (FileNotFoundException fileNotFoundException) {
				System.out.println("Could not find the file [" + SOURCE + "] with the saved dock model.");
				System.out.println("Continuing with the default dock model.");
			} catch (IOException ioException) {
				System.out.println("Could not decode a dock model: [" + ioException + "].");
				ioException.printStackTrace();
				System.out.println("Continuing with the default dock model.");
			}
		}

		// These are the root docks.
		SplitDock splitDock = null;

		if (dockModel == null) {

			// Create the dock model for the docks because they could not be retrieved from a file.
			dockModel = new FloatDockModel(SOURCE);
			dockModel.addOwner(frameId, frame);

			// Give the dock model to the docking manager.
			DockingManager.setDockModel(dockModel);

			// Create the child tab docks.
			TabDock leftTabDock = new TabDock();
			TabDock rightTabDock = new TabDock();

			// Add the dockables to the tab dock.
			leftTabDock.addDockable(dockable1, new Position(0));
			leftTabDock.addDockable(dockable2, new Position(1));
			rightTabDock.addDockable(dockable3, new Position(0));
			rightTabDock.addDockable(dockable4, new Position(1));

			// Create the split dock.
			splitDock = new SplitDock();

			// Add the child docks to the split dock at the left and right.
			splitDock.addChildDock(leftTabDock, new Position(Position.LEFT));
			splitDock.addChildDock(rightTabDock, new Position(Position.RIGHT));
			splitDock.setDividerLocation(290);

			// Add the root dock to the dock model.
			dockModel.addRootDock("splitDock", splitDock, frame);

			// Add the paths of the docked dockables to the model with the docking paths.
			addDockingPath(dockable1);
			addDockingPath(dockable2);
			addDockingPath(dockable3);
			addDockingPath(dockable4);

			// Add the path of the dockables that are not docked already.
			// We want dockable 5 to be docked, when it is made visible, where dockable 1 is docked.
			DockingPath dockingPathToCopy1 = DockingManager.getDockingPathModel().getDockingPath(dockable1.getID());
			DockingPath dockingPath5 = DefaultDockingPath.copyDockingPath(dockable5, dockingPathToCopy1);
			DockingManager.getDockingPathModel().add(dockingPath5);
			// We want dockable 6 to be docked, when it is made visible, where dockable 3 is docked.
			DockingPath dockingPathToCopy3 = DockingManager.getDockingPathModel().getDockingPath(dockable3.getID());
			DockingPath dockingPath6 = DefaultDockingPath.copyDockingPath(dockable6, dockingPathToCopy3);
			DockingManager.getDockingPathModel().add(dockingPath6);

		} else {

			// Get the root dock from the dock model.
			splitDock = (SplitDock) dockModel.getRootDock("splitDock");

		}

		// Add the root dock to the panel.
		add(splitDock, BorderLayout.CENTER);

		// Create the menubar.
		Dockable[] dockables = new Dockable[6];
		dockables[0] = dockable1;
		dockables[1] = dockable2;
		dockables[2] = dockable3;
		dockables[3] = dockable4;
		dockables[4] = dockable5;
		dockables[5] = dockable6;
		JMenuBar menuBar = createMenu(dockables);
		frame.setJMenuBar(menuBar);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Encoder and Decoder");

		// Create the panel and add it to the frame.
		CodecExample panel = new CodecExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = CodecExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	/**
	 * Creates the menubar with two menus: File and Window.
	 * File has the Exit menu item. Window has check boxes for the dockables.
	 *
	 * @param dockables The dockables for which a menu item has to be created.
	 * @return The created menu bar.
	 */
	private JMenuBar createMenu(Dockable[] dockables) {

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
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveWorkspace();
				System.exit(0);
			}
		});
		fileMenu.add(menuItem);

		// Iterate over the dockables.
		for (int index = 0; index < dockables.length; index++) {
			// Create the check box menu for the dockable.
			JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(dockables[index]);
			windowMenu.add(cbMenuItem);
		}

		return menuBar;

	}

	/**
	 * Creates a docking path for the dockable. This path is added to the docking path model of the docking
	 * manager.
	 * To create a docking path, the dock model should already be given to the docking manager.
	 *
	 * @param dockable The dockable for which to create a docking path.
	 * @return The created docking path.
	 */
	private DockingPath addDockingPath(Dockable dockable) {

		if (dockable.getDock() != null) {
			// Create the docking path of the dockable.
			DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
			DockingManager.getDockingPathModel().add(dockingPath);
			return dockingPath;
		}

		return null;

	}

	private void saveWorkspace() {

		// Save the dock model.
		DockModelPropertiesEncoder encoder = new DockModelPropertiesEncoder();
		if (encoder.canSave(dockModel)) {
			try {
				encoder.save(dockModel);
			} catch (Exception e) {
				System.out.println("Error while saving the dock model.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Could not save the dock model.");
		}

	}

	/**
	 * A listener for window closing events. Saves the workspace, when the application window is closed.
	 *
	 * @author Heidi Rakels.
	 */
	private class WorkspaceSaver implements WindowListener {

		public void windowClosing(WindowEvent windowEvent) {
			saveWorkspace();
		}

		public void windowDeactivated(WindowEvent windowEvent) {
		}

		public void windowDeiconified(WindowEvent windowEvent) {
		}

		public void windowIconified(WindowEvent windowEvent) {
		}

		public void windowOpened(WindowEvent windowEvent) {
		}

		public void windowActivated(WindowEvent windowEvent) {
		}

		public void windowClosed(WindowEvent windowEvent) {
		}

	}

	/**
	 * A check box menu item to add or remove the dockable.
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

	// Main method.

	/**
	 * A listener that listens when menu items with dockables are selected and deselected.
	 * It also listens when dockables are closed or docked.
	 */
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

