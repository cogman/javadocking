package com.javadocking.multi;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.factory.CompositeToolBarDockFactory;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.model.DefaultDockModel;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPath;
import com.javadocking.model.codec.DockModelPropertiesDecoder;
import com.javadocking.model.codec.DockModelPropertiesEncoder;
import com.javadocking.util.ToolBarButton;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows 2 completely separated dockable workspaces with toolbars, float docks, vizualizers.
 * The application can be saved and reloaded.
 * <p>
 * There are 2 workspaces. Dockables 1-7 belong to workspace 1.
 * Dockables 8-14 belong to workspace 2.
 *
 * @author Heidi Rakels
 */
public class MultiWorkspaceExample extends JPanel {

	// Static fields.

	public static final String SOURCE = "multiworkspace_1_5.dck";

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	/**
	 * The model with the docks, dockables, and visualizers (a minimizer and a maximizer).
	 */
	private DockModel dockModel;


	// Constructor.

	public MultiWorkspaceExample(JFrame frame) {
		super(new BorderLayout());

		// Create the content components.
		TextPanel textPanel1 = new TextPanel("I am window 1.");
		TextPanel textPanel2 = new TextPanel("I am window 2.");
		TextPanel textPanel3 = new TextPanel("I am window 3.");
		TextPanel textPanel4 = new TextPanel("I am window 4.");
		TextPanel textPanel5 = new TextPanel("I am window 5.");
		TextPanel textPanel6 = new TextPanel("I am window 6.");
		TextPanel textPanel7 = new TextPanel("I am window 7.");
		TextPanel textPanel8 = new TextPanel("I am window 8.");
		TextPanel textPanel9 = new TextPanel("I am window 9.");
		TextPanel textPanel10 = new TextPanel("I am window 10.");
		TextPanel textPanel11 = new TextPanel("I am window 11.");
		TextPanel textPanel12 = new TextPanel("I am window 12.");
		TextPanel textPanel13 = new TextPanel("I am window 13.");
		TextPanel textPanel14 = new TextPanel("I am window 14.");

		// Create the dockables around the content components.
		Dockable[] dockables = new Dockable[14];
		dockables[0] = new DefaultDockable("Window1", textPanel1, "Window 1", null, DockingMode.ALL);
		dockables[1] = new DefaultDockable("Window2", textPanel2, "Window 2", null, DockingMode.ALL);
		dockables[2] = new DefaultDockable("Window3", textPanel3, "Window 3", null, DockingMode.ALL);
		dockables[3] = new DefaultDockable("Window4", textPanel4, "Window 4", null, DockingMode.ALL);
		dockables[4] = new DefaultDockable("Window5", textPanel5, "Window 5", null, DockingMode.ALL);
		dockables[5] = new DefaultDockable("Window6", textPanel6, "Window 6", null, DockingMode.ALL);
		dockables[6] = new DefaultDockable("Window7", textPanel7, "Window 7", null, DockingMode.ALL);
		dockables[7] = new DefaultDockable("Window8", textPanel8, "Window 8", null, DockingMode.ALL);
		dockables[8] = new DefaultDockable("Window9", textPanel9, "Window 9", null, DockingMode.ALL);
		dockables[9] = new DefaultDockable("Window10", textPanel10, "Window 10", null, DockingMode.ALL);
		dockables[10] = new DefaultDockable("Window11", textPanel11, "Window 11", null, DockingMode.ALL);
		dockables[11] = new DefaultDockable("Window12", textPanel12, "Window 12", null, DockingMode.ALL);
		dockables[12] = new DefaultDockable("Window13", textPanel13, "Window 13", null, DockingMode.ALL);
		dockables[13] = new DefaultDockable("Window14", textPanel14, "Window 14", null, DockingMode.ALL);

		// Add minimize, maximize, externalize, and close actions to the dockables.
		dockables[0] = addActions(dockables[0]);
		dockables[1] = addActions(dockables[1]);
		dockables[2] = addActions(dockables[2]);
		dockables[3] = addActions(dockables[3]);
		dockables[4] = addActions(dockables[4]);
		dockables[5] = addActions(dockables[5]);
		dockables[6] = addActions(dockables[6]);
		dockables[7] = addActions(dockables[7]);
		dockables[8] = addActions(dockables[8]);
		dockables[9] = addActions(dockables[9]);
		dockables[10] = addActions(dockables[10]);
		dockables[11] = addActions(dockables[11]);
		dockables[12] = addActions(dockables[12]);
		dockables[13] = addActions(dockables[13]);

		// Create the buttons with a dockable around.
		Dockable[] buttonDockables = createButtonDockables();

		// Create a minimizer.
		LineMinimizer minimizer1 = new LineMinimizer();
		LineMinimizer minimizer2 = new LineMinimizer();

		// Create a maximizer.
		SingleMaximizer maximizer1 = new SingleMaximizer(minimizer1);
		SingleMaximizer maximizer2 = new SingleMaximizer(minimizer2);

		// Create an externalizer.
		FloatExternalizer externalizer1 = new FloatExternalizer(frame);
		FloatExternalizer externalizer2 = new FloatExternalizer(frame);
		externalizer2.setHidden(true);

		// The UI.
		JTabbedPane tabbedPane = new JTabbedPane();
		this.add(tabbedPane, BorderLayout.CENTER);

		// Try to decode the dock model from file.
		DockModelPropertiesDecoder dockModelDecoder = new DockModelPropertiesDecoder();
		if (dockModelDecoder.canDecodeSource(SOURCE)) {
			try {
				// Create the map with the dockables, that the decoder needs.
				Map dockablesMap = new HashMap();
				for (final Dockable dockable : dockables) {
					dockablesMap.put(dockable.getID(), dockable);
				}
				for (final Dockable buttonDockable : buttonDockables) {
					dockablesMap.put(buttonDockable.getID(), buttonDockable);
				}

				// Create the map with the owner windows, that the decoder needs.
				Map ownersMap = new HashMap();
				/**
				 * The ID for the owner window.
				 */
				final String frameId = "frame";
				ownersMap.put(frameId, frame);

				// Create the map with the visualizers, that the decoder needs.
				Map visualizersMap = new HashMap();
				visualizersMap.put("maximizer1", maximizer1);
				visualizersMap.put("maximizer2", maximizer2);
				visualizersMap.put("minimizer1", minimizer1);
				visualizersMap.put("minimizer2", minimizer2);
				visualizersMap.put("externalizer1", externalizer1);
				visualizersMap.put("externalizer2", externalizer2);

				// Decode the file.
				dockModel = dockModelDecoder.decode(SOURCE, dockablesMap, ownersMap, visualizersMap);
			} catch (FileNotFoundException fileNotFoundException) {
				System.out.println("Could not find the file [" + SOURCE + "] with the saved dock model.");
				System.out.println("Continuing with the default dock model.");
			} catch (IOException ioException) {
				System.out.println("Could not decode a dock model: [" + ioException + "].");
				ioException.printStackTrace();
				System.out.println("Continuing with the default dock model.");
			}
		}

		// These are the root docks of the dock model.
		SplitDock splitDock1 = null;
		SplitDock splitDock2 = null;
		BorderDock toolBarBorderDock1 = null;
		BorderDock toolBarBorderDock2 = null;
		HidableFloatDock float1 = null;
		HidableFloatDock float2 = null;

		// Could we decode a dock model?
		if (dockModel == null) {
			// Create the dock model and dock model group.
			dockModel = new DefaultDockModel(SOURCE);
			dockModel.addOwner("frame", frame);

			// Give the dock model to the docking manager.
			DockingManager.setDockModel(dockModel);

			// Add visualizers.
			dockModel.addVisualizer("minimizer1", minimizer1, frame);
			dockModel.addVisualizer("minimizer2", minimizer2, frame);
			dockModel.addVisualizer("maximizer1", maximizer1, frame);
			dockModel.addVisualizer("maximizer2", maximizer2, frame);
			dockModel.addVisualizer("externalizer1", externalizer1, frame);
			dockModel.addVisualizer("externalizer2", externalizer2, frame);

			// Create the float docks.
			float1 = new HidableFloatDock(frame);
			float2 = new HidableFloatDock(frame);
			float2.setHidden(true);

			// Create the child tab docks.
			TabDock leftTabDock1 = new TabDock();
			TabDock rightTabDock1 = new TabDock();
			TabDock leftTabDock2 = new TabDock();
			TabDock rightTabDock2 = new TabDock();

			// Add the dockables to these tab docks.
			leftTabDock1.addDockable(dockables[0], new Position(0));
			leftTabDock1.addDockable(dockables[1], new Point(), new Point());
			rightTabDock1.addDockable(dockables[2], new Point(), new Point());
			rightTabDock1.addDockable(dockables[3], new Point(), new Point());
			leftTabDock2.addDockable(dockables[7], new Point(), new Point());
			leftTabDock2.addDockable(dockables[8], new Point(), new Point());
			rightTabDock2.addDockable(dockables[9], new Point(), new Point());
			rightTabDock2.addDockable(dockables[10], new Point(), new Point());

			// Create the split docks.
			splitDock1 = new SplitDock();
			splitDock2 = new SplitDock();

			// Add the child docks to the split dock at the left and right.
			splitDock1.addChildDock(leftTabDock1, new Position(Position.LEFT));
			splitDock1.addChildDock(rightTabDock1, new Position(Position.RIGHT));
			splitDock1.setDividerLocation(FRAME_WIDTH / 2);
			splitDock2.addChildDock(leftTabDock2, new Position(Position.LEFT));
			splitDock2.addChildDock(rightTabDock2, new Position(Position.RIGHT));
			splitDock2.setDividerLocation(FRAME_WIDTH / 2);

			// Create the border dock for the buttons.
			toolBarBorderDock1 = new BorderDock(new CompositeToolBarDockFactory());
			toolBarBorderDock2 = new BorderDock(new CompositeToolBarDockFactory());
			toolBarBorderDock1.setMode(BorderDock.MODE_TOOL_BAR);
			toolBarBorderDock2.setMode(BorderDock.MODE_TOOL_BAR);
			CompositeLineDock compositeToolBarDock1 = new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			CompositeLineDock compositeToolBarDock2 = new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			CompositeLineDock compositeToolBarDock3 = new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			CompositeLineDock compositeToolBarDock4 = new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
					new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			toolBarBorderDock1.setDock(compositeToolBarDock1, Position.TOP);
			toolBarBorderDock1.setDock(compositeToolBarDock2, Position.LEFT);
			toolBarBorderDock2.setDock(compositeToolBarDock3, Position.TOP);
			toolBarBorderDock2.setDock(compositeToolBarDock4, Position.LEFT);

			// The line docks for the buttons.
			LineDock toolBarDock1 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock2 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock3 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock4 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock5 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock6 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock7 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);

			LineDock toolBarDock8 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock9 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock10 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock11 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			LineDock toolBarDock12 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);

			GridDock toolGridDock1 = new GridDock(DockingMode.TOOL_GRID);
			GridDock toolGridDock2 = new GridDock(DockingMode.TOOL_GRID);

			// Add the dockables to the line docks and the grid dock.
			toolBarDock1.addDockable(buttonDockables[0], new Position(0));
			toolBarDock1.addDockable(buttonDockables[1], new Position(1));
			toolBarDock1.addDockable(buttonDockables[2], new Position(2));
			toolBarDock1.addDockable(buttonDockables[3], new Position(3));

			toolBarDock2.addDockable(buttonDockables[4], new Position(4));
			toolBarDock2.addDockable(buttonDockables[5], new Position(5));
			toolBarDock2.addDockable(buttonDockables[6], new Position(6));

			toolBarDock3.addDockable(buttonDockables[7], new Position(0));
			toolBarDock3.addDockable(buttonDockables[8], new Position(1));
			toolBarDock3.addDockable(buttonDockables[9], new Position(2));

			toolBarDock4.addDockable(buttonDockables[10], new Position(0));
			toolBarDock4.addDockable(buttonDockables[11], new Position(1));
			toolBarDock4.addDockable(buttonDockables[12], new Position(2));

			toolBarDock5.addDockable(buttonDockables[13], new Position(0));
			toolBarDock5.addDockable(buttonDockables[14], new Position(1));
			toolBarDock5.addDockable(buttonDockables[15], new Position(2));
			toolBarDock6.addDockable(buttonDockables[16], new Position(3));
			toolBarDock6.addDockable(buttonDockables[17], new Position(4));

			toolBarDock7.addDockable(buttonDockables[18], new Position(0));
			toolBarDock7.addDockable(buttonDockables[19], new Position(1));

			toolGridDock1.addDockable(buttonDockables[20], new Position(0));
			toolGridDock1.addDockable(buttonDockables[21], new Position(1));
			toolGridDock1.addDockable(buttonDockables[22], new Position(2));
			toolGridDock1.addDockable(buttonDockables[23], new Position(3));
			toolGridDock1.addDockable(buttonDockables[24], new Position(4));
			toolGridDock2.addDockable(buttonDockables[25], new Position(5));
			toolGridDock2.addDockable(buttonDockables[26], new Position(6));
			toolGridDock2.addDockable(buttonDockables[27], new Position(7));
			toolGridDock2.addDockable(buttonDockables[28], new Position(8));

			toolBarDock8.addDockable(buttonDockables[29], new Position(0));
			toolBarDock8.addDockable(buttonDockables[30], new Position(1));
			toolBarDock8.addDockable(buttonDockables[31], new Position(2));

			toolBarDock9.addDockable(buttonDockables[32], new Position(3));
			toolBarDock9.addDockable(buttonDockables[33], new Position(4));
			toolBarDock10.addDockable(buttonDockables[34], new Position(5));
			toolBarDock10.addDockable(buttonDockables[35], new Position(6));
			toolBarDock10.addDockable(buttonDockables[36], new Position(6));

			toolBarDock11.addDockable(buttonDockables[37], new Position(0));
			toolBarDock11.addDockable(buttonDockables[38], new Position(1));
			toolBarDock12.addDockable(buttonDockables[39], new Position(2));
			toolBarDock12.addDockable(buttonDockables[40], new Position(3));
			toolBarDock12.addDockable(buttonDockables[41], new Position(4));

			// Add the line docks and grid to their composite parents.
			compositeToolBarDock1.addChildDock(toolBarDock1, new Position(0));
			compositeToolBarDock1.addChildDock(toolBarDock2, new Position(1));
			compositeToolBarDock1.addChildDock(toolBarDock3, new Position(2));
			compositeToolBarDock1.addChildDock(toolBarDock4, new Position(3));
			compositeToolBarDock2.addChildDock(toolBarDock5, new Position(0));
			compositeToolBarDock2.addChildDock(toolBarDock6, new Position(1));
			compositeToolBarDock2.addChildDock(toolBarDock7, new Position(2));

			compositeToolBarDock3.addChildDock(toolBarDock8, new Position(0));
			compositeToolBarDock3.addChildDock(toolBarDock9, new Position(1));
			compositeToolBarDock3.addChildDock(toolBarDock10, new Position(2));
			compositeToolBarDock4.addChildDock(toolBarDock11, new Position(0));
			compositeToolBarDock4.addChildDock(toolBarDock12, new Position(1));

			float1.addChildDock(toolGridDock1, new Position(400, 300));
			float2.addChildDock(toolGridDock2, new Position(450, 350));

			// Minimize dockables.
			minimizer1.visualizeDockable(dockables[4]);
			minimizer1.visualizeDockable(dockables[5]);
			minimizer2.visualizeDockable(dockables[11]);
			minimizer2.visualizeDockable(dockables[12]);

			// Externalize dockable.
			//externalizer.visualizeDockable(dockable13);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Point location = new Point((screenSize.width - 200) / 2, (screenSize.height - 200) / 2);
			externalizer1.externalizeDockable(dockables[6], location);
			externalizer2.externalizeDockable(dockables[13], location);

			// Add the root docks to the dock model.
			dockModel.addRootDock("dock1", splitDock1, frame);
			dockModel.addRootDock("dock2", splitDock2, frame);
			dockModel.addRootDock("toolBarBorderDock1", toolBarBorderDock1, frame);
			dockModel.addRootDock("toolBarBorderDock2", toolBarBorderDock2, frame);
			dockModel.addRootDock("float1", float1, frame);
			dockModel.addRootDock("float2", float2, frame);

			// Add docking paths.
			addDockingPath(dockables[0]);
			addDockingPath(dockables[1]);
			addDockingPath(dockables[2]);
			addDockingPath(dockables[3]);
			addDockingPath(dockables[4]);
			addDockingPath(dockables[5]);
			addDockingPath(dockables[7]);
			addDockingPath(dockables[8]);
			addDockingPath(dockables[9]);
			addDockingPath(dockables[10]);
			addDockingPath(dockables[11]);
			addDockingPath(dockables[12]);

			DockingPath dockingPathToCopy1 = DockingManager.getDockingPathModel().getDockingPath(dockables[0].getID());
			DockingPath dockingPathToCopy2 = DockingManager.getDockingPathModel().getDockingPath(dockables[9].getID());
			DockingPath dockingPath1 = DefaultDockingPath.copyDockingPath(dockables[6], dockingPathToCopy1);
			DockingPath dockingPath2 = DefaultDockingPath.copyDockingPath(dockables[13], dockingPathToCopy2);
			DockingManager.getDockingPathModel().add(dockingPath1);
			DockingManager.getDockingPathModel().add(dockingPath2);

		} else {

			// Get the root dock from the dock model.
			splitDock1 = (SplitDock) dockModel.getRootDock("dock1");
			splitDock2 = (SplitDock) dockModel.getRootDock("dock2");
			toolBarBorderDock1 = (BorderDock) dockModel.getRootDock("toolBarBorderDock1");
			toolBarBorderDock2 = (BorderDock) dockModel.getRootDock("toolBarBorderDock2");
			float1 = (HidableFloatDock) dockModel.getRootDock("float1");
			float2 = (HidableFloatDock) dockModel.getRootDock("float2");

		}

		// Listen when the frame is closed. The workspace should be saved.
		frame.addWindowListener(new WorkspaceSaver());

		// General code.
		tabbedPane.addTab("Workspace 1", toolBarBorderDock1);
		tabbedPane.addTab("Workspace 2", toolBarBorderDock2);

		toolBarBorderDock1.setCenterComponent(maximizer1);
		toolBarBorderDock2.setCenterComponent(maximizer2);

		minimizer1.setContent(splitDock1);
		minimizer2.setContent(splitDock2);

		// Listen to the selections of the tabs.
		SingleSelectionModel selectionModel = tabbedPane.getModel();
		selectionModel.addChangeListener(new TabChangelistener(tabbedPane, float1, float2, externalizer1, externalizer2));

	}

	// Private classes.

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Docks in multiple tabs and visualizers");

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		MultiWorkspaceExample panel = new MultiWorkspaceExample(frame);
		frame.getContentPane().add(panel);

		// Show.
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = MultiWorkspaceExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
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
	 * Creates a docking path for the given dockable. It contains the information
	 * how the dockable is docked now. The docking path is added to the docking path
	 * model of the docking manager.
	 *
	 * @param     dockable    The dockable for which a docking path has to be created.
	 * @return The docking path model. Null if the dockable is not docked.
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

	private Dockable[] createButtonDockables() {
		// Create the buttons with a dockable around.
		Dockable[] buttonDockables = new Dockable[42];
		buttonDockables[0] = createButtonDockable("ButtonDockableAdd", "Add", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/add.png")), "Add!");
		buttonDockables[1] = createButtonDockable("ButtonDockableAccept", "Accept", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/accept.png")), "Accept!");
		buttonDockables[2] = createButtonDockable("ButtonDockableCancel", "Cancel", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/cancel.png")), "Cancel!");
		buttonDockables[3] = createButtonDockable("ButtonDockableUndo", "Undo", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/undo.png")), "Undo!");
		buttonDockables[4] = createButtonDockable("ButtonDockableRedo", "Redo", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/redo.png")), "Redo!");
		buttonDockables[5] = createButtonDockable("ButtonDockableRefresh", "Refresh", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/refresh.png")), "Refresh!");
		buttonDockables[6] = createButtonDockable("ButtonDockableBin", "Bin", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/bin.png")), "Bin!");
		buttonDockables[7] = createButtonDockable("ButtonDockableIcons", "Icons", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/icons.png")), "Icons!");
		buttonDockables[8] = createButtonDockable("ButtonDockableList", "List", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/list.png")), "List!");
		buttonDockables[9] = createButtonDockable("ButtonDockableImages", "Images", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/images.png")), "Images!");
		buttonDockables[10] = createButtonDockable("ButtonDockableDivide", "Divide", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/divide.png")), "Divide!");
		buttonDockables[11] = createButtonDockable("ButtonDockableJoin", "Join", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/join.png")), "Join!");
		buttonDockables[12] = createButtonDockable("ButtonDockableSwitch", "Switch", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/switch.png")), "Switch!");
		buttonDockables[13] = createButtonDockable("ButtonDockableAsterisk", "Asterisk", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/asterisk.png")), "Asterisk!");
		buttonDockables[14] = createButtonDockable("ButtonDockableAnchor", "Anchor", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/anchor.png")), "Anchor!");
		buttonDockables[15] = createButtonDockable("ButtonDockableTerminal", "Terminal", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/terminal.png")), "Terminal!");
		buttonDockables[16] = createButtonDockable("ButtonDockableStar", "Well Done", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/star.png")), "Well Done!");
		buttonDockables[17] = createButtonDockable("ButtonDockableWakeUp", "Wake Up", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/wake_up.png")), "Wake up!");
		buttonDockables[18] = createButtonDockable("ButtonDockableAddToBasket", "Add to Basket", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/add_to_basket.png")), "Add to Basket!");
		buttonDockables[19] = createButtonDockable("ButtonDockableRemoveFromBasket", "Remove from Basket", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/remove_from_basket.png")), "Remove from Basket!");
		buttonDockables[20] = createButtonDockable("ButtonDockableBook", "Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book.png")), "Book!");
		buttonDockables[21] = createButtonDockable("ButtonDockableBookPrevious", "Previous Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_previous.png")), "Previous Book!");
		buttonDockables[22] = createButtonDockable("ButtonDockableBookNext", "Next Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_next.png")), "Next Book!");
		buttonDockables[23] = createButtonDockable("ButtonDockableBookOpen", "Open Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_open.png")), "Open Book!");
		buttonDockables[24] = createButtonDockable("ButtonDockableBookEdit", "Edit Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_edit.png")), "Edit Book!");
		buttonDockables[25] = createButtonDockable("ButtonDockableBookAdd", "Add Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_add.png")), "Add Book!");
		buttonDockables[26] = createButtonDockable("ButtonDockableBookDelete", "Delete Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_delete.png")), "Delete Book!");
		buttonDockables[27] = createButtonDockable("ButtonDockableBookLink", "Link Book", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/book_link.png")), "Link Book!");
		buttonDockables[28] = createButtonDockable("ButtonDockableAttach", "Attach", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/attach.png")), "Attach!");
		buttonDockables[29] = createButtonDockable("ButtonDockableCalculator", "Calculator", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/calculator.png")), "Calculator!");
		buttonDockables[30] = createButtonDockable("ButtonDockableBriefcase", "Briefcase", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/briefcase.png")), "Briefcase!");
		buttonDockables[31] = createButtonDockable("ButtonDockableCalendar", "Calendar", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/calendar.png")), "Calendar!");
		buttonDockables[32] = createButtonDockable("ButtonDockableCamera", "Camera", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/camera.png")), "Camera!");
		buttonDockables[33] = createButtonDockable("ButtonDockableCar", "Car", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/car.png")), "Car!");
		buttonDockables[34] = createButtonDockable("ButtonDockableCD", "CD", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/cd.png")), "CD!");
		buttonDockables[35] = createButtonDockable("ButtonDockableClock", "Clock", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/clock.png")), "Clock!");
		buttonDockables[36] = createButtonDockable("ButtonDockableCoins", "Coins", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/coins.png")), "Coins!");
		buttonDockables[37] = createButtonDockable("ButtonDockableChartBar", "Bar Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/chart_bar.png")), "Bar Chart!");
		buttonDockables[38] = createButtonDockable("ButtonDockableChartCurve", "Curve Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/chart_curve.png")), "Curve Chart!");
		buttonDockables[39] = createButtonDockable("ButtonDockableChartLine", "Line Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/chart_line.png")), "Chart!");
		buttonDockables[40] = createButtonDockable("ButtonDockableChartOrganisation", "Organisation Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/chart_organisation.png")), "Oraganisation Chart!");
		buttonDockables[41] = createButtonDockable("ButtonDockableChartPie", "Pie Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/icons/chart_pie.png")), "Pie Chart!");
		return buttonDockables;
	}

	/**
	 * Creates a dockable with a button as content.
	 *
	 * @param id      The ID of the dockable that has to be created.
	 * @param title   The title of the dialog that will be displayed.
	 * @param icon    The icon that will be put on the button.
	 * @param message The message that will be displayed when the action is performed.
	 * @return The dockable with a button as content.
	 */
	private Dockable createButtonDockable(String id, String title, Icon icon, String message) {

		// Create the action.
		MessageAction action = new MessageAction(this, title, icon, message);

		// Create the button.
		ToolBarButton button = new ToolBarButton(action);

		// Create the dockable with the button as component.
		ButtonDockable buttonDockable = new ButtonDockable(id, button);

		// Add a dragger to the individual dockable.
		createDockableDragger(buttonDockable);

		return buttonDockable;

	}

	/**
	 * Adds a drag listener on the content component of a dockable.
	 */
	private void createDockableDragger(Dockable dockable) {

		// Create the dragger for the dockable.
		DragListener dragListener = DockingManager.getDockableDragListenerFactory().createDragListener(dockable);
		dockable.getContent().addMouseListener(dragListener);
		dockable.getContent().addMouseMotionListener(dragListener);
	}

	/**
	 * Decorates the given dockable with a state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
		int[] states = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.MAXIMIZED, DockableState.EXTERNALIZED};
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);
		return wrapper;

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

	private class TabChangelistener implements ChangeListener {

		JTabbedPane tabbedPane;
		HidableFloatDock float1;
		HidableFloatDock float2;
		FloatExternalizer externalizer1;
		FloatExternalizer externalizer2;

		public TabChangelistener(JTabbedPane tabbedPane, HidableFloatDock float1, HidableFloatDock float2,
								 FloatExternalizer externalizer1, FloatExternalizer externalizer2) {
			this.tabbedPane = tabbedPane;
			this.float1 = float1;
			this.float2 = float2;
			this.externalizer1 = externalizer1;
			this.externalizer2 = externalizer2;
		}

		// Implementations of ChangeListener.

		public void stateChanged(ChangeEvent changeEvent) {
			int index = tabbedPane.getSelectedIndex();
			if (index == 0) {
				float2.setHidden(true);
				float1.setHidden(false);
				externalizer2.setHidden(true);
				externalizer1.setHidden(false);
			} else {
				float1.setHidden(true);
				float2.setHidden(false);
				externalizer1.setHidden(true);
				externalizer2.setHidden(false);
			}
		}

	}

	// Main method.

	/**
	 * An action that shows a message in a dialog.
	 */
	private class MessageAction extends AbstractAction {

		private Component parentComponent;
		private String message;
		private String name;

		public MessageAction(Component parentComponent, String name, Icon icon, String message) {
			super(null, icon);
			putValue(Action.SHORT_DESCRIPTION, name);
			this.message = message;
			this.name = name;
			this.parentComponent = parentComponent;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			JOptionPane.showMessageDialog(parentComponent,
					message, name, JOptionPane.INFORMATION_MESSAGE);
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

