package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.drag.StaticDraggerFactory;
import com.javadocking.drag.painter.*;
import com.javadocking.util.LookAndFeelUtil;
import com.javadocking.util.SampleComponentFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DockGallery extends JFrame {

	private JFrame dockFrame;

	public DockGallery(String title) throws HeadlessException {
		super(title);

		// Create the panel for the buttons to start the demo's for every kind of dock.
		JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

		DockAction[] startDemoActions = new DockAction[17];
		startDemoActions[0] = new DragPainterAction();
		startDemoActions[1] = new DynamicAction();
		startDemoActions[2] = new TabDockAction();
		startDemoActions[3] = new SplitDockAction();
		startDemoActions[4] = new SplitAndTabDockAction();
		startDemoActions[5] = new FloatDockAction();
		startDemoActions[6] = new TabFloatDockAction();
		startDemoActions[7] = new LineDockAction();
		startDemoActions[8] = new CompositeLineDockAction();
		startDemoActions[9] = new GridDockAction();
		startDemoActions[10] = new CompositeGridDockAction();
		startDemoActions[11] = new BorderDockAction();
		startDemoActions[12] = new DragRectangleAction();
		startDemoActions[13] = new DecoratedDockableAction();
		startDemoActions[14] = new VisualizerAction();
		startDemoActions[15] = new ImageMinimizeHeaderAction();
		startDemoActions[16] = new ButtonAction();

		for (int index = 0; index < startDemoActions.length; index++) {
			JButton button = new JButton(startDemoActions[index]);
			button.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
			button.setToolTipText(startDemoActions[index].getDescription());
			buttonPanel.add(button);
		}

		JPanel flowPanel = new JPanel(new FlowLayout());
		flowPanel.add(buttonPanel);
		getContentPane().add(flowPanel);
	}

	public static void createAndShowGUI() {
		// Create the frame and show it.
		DockGallery frame = new DockGallery("Dock gallery");
		frame.setLocation(100, 50);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Remove the borders from the split panes and the split pane dividers.
		LookAndFeelUtil.removeAllSplitPaneBorders();

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

	private void resetDockingProperties() {

		// Set the default dragger factory.
		CompositeDockableDragPainter dockableDragPainter = new CompositeDockableDragPainter();
		dockableDragPainter.addPainter(new SwDockableDragPainter(new RectangleDragComponentFactory(new DefaultRectanglePainter(), true)));
		dockableDragPainter.addPainter(new WindowDockableDragPainter(new DefaultRectanglePainter(), true));
		DockingManager.setDraggerFactory(new StaticDraggerFactory(dockableDragPainter));

		// Set the default component factory.
		DockingManager.setComponentFactory(new SampleComponentFactory());

	}

	/**
	 * This action displays a frame with docks and dockables.
	 *
	 * @author Heidi Rakels.
	 */
	private abstract class DockAction extends AbstractAction {

		public DockAction() {
			putValue(Action.SHORT_DESCRIPTION, getTitle());
			putValue(Action.LONG_DESCRIPTION, getDescription());
			putValue(Action.NAME, getTitle());
		}

		public abstract String getTitle();

		public abstract String getDescription();

		/**
		 * Creates the content pane with docks and dockables for the given frame.
		 *
		 * @param frame
		 * @return The content pane with docks and dockables for the given frame.
		 */
		public abstract Component createContentPane(JFrame frame);

		public void actionPerformed(ActionEvent actionEvent) {
			// Destroy the previous frame.
			if (dockFrame != null) {
				dockFrame.setVisible(false);
				dockFrame.dispose();
			}

			// Reset the docking properties to the defaults.
			resetDockingProperties();

			// Create the frame.
			dockFrame = new JFrame(getDescription());
			dockFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			// Create the panel.
			createContentPane(dockFrame);

			// Create the frame.
			dockFrame.setVisible(true);
		}

	}

	private class TabDockAction extends DockAction {
		public String getTitle() {
			return "Tab docks";
		}

		public String getDescription() {
			return "Simple demo with tab docks";
		}

		public Component createContentPane(JFrame frame) {
			return new TabDockSample(frame);
		}
	}

	private class SplitDockAction extends DockAction {
		public String getTitle() {
			return "Split docks";
		}

		public String getDescription() {
			return "Simple demo with split docks";
		}

		public Component createContentPane(JFrame frame) {
			return new SplitDockSample(frame);
		}
	}

	private class SplitAndTabDockAction extends DockAction {
		public String getTitle() {
			return "Split and tab docks";
		}

		public String getDescription() {
			return "Simple demo with split docks and tab docks";
		}

		public Component createContentPane(JFrame frame) {
			return new SplitAndTabDockSample(frame);
		}
	}

	private class TabFloatDockAction extends DockAction {
		public String getTitle() {
			return "Float docks with tabs";
		}

		public String getDescription() {
			return "Simple demo with float docks that use tab docks";
		}

		public Component createContentPane(JFrame frame) {
			return new TabFloatDockSample(frame);
		}
	}

	private class FloatDockAction extends DockAction {
		public String getTitle() {
			return "Float docks";
		}

		public String getDescription() {
			return "Simple demo with float docks that use single docks";
		}

		public Component createContentPane(JFrame frame) {
			return new FloatDockSample(frame);
		}
	}

	private class LineDockAction extends DockAction {
		public String getTitle() {
			return "Line dock";
		}

		public String getDescription() {
			return "Simple demo with a line dock";
		}

		public Component createContentPane(JFrame frame) {
			return new LineDockSample(frame);
		}
	}

	private class CompositeLineDockAction extends DockAction {
		public String getTitle() {
			return "Composite line dock";
		}

		public String getDescription() {
			return "Simple demo with a composite line dock";
		}

		public Component createContentPane(JFrame frame) {
			return new CompositeLineDockSample(frame);
		}
	}

	private class GridDockAction extends DockAction {
		public String getTitle() {
			return "Grid dock";
		}

		public String getDescription() {
			return "Simple demo with a grid dock";
		}

		public Component createContentPane(JFrame frame) {
			return new GridDockSample(frame);
		}
	}

	private class CompositeGridDockAction extends DockAction {
		public String getTitle() {
			return "Composite grid dock";
		}

		public String getDescription() {
			return "Simple demo with a composite grid dock";
		}

		public Component createContentPane(JFrame frame) {
			return new CompositeGridDockSample(frame);
		}
	}

	private class BorderDockAction extends DockAction {
		public String getTitle() {
			return "Border dock";
		}

		public String getDescription() {
			return "Simple demo with a border dock";
		}

		public Component createContentPane(JFrame frame) {
			return new BorderDockSample(frame);
		}
	}

	private class DynamicAction extends DockAction {
		public String getTitle() {
			return "Dynamic dragging";
		}

		public String getDescription() {
			return "Simple demo with dynamic dragging";
		}

		public Component createContentPane(JFrame frame) {
			return new DynamicSample(frame);
		}
	}

	private class DragRectangleAction extends DockAction {
		public String getTitle() {
			return "Drag Rectangle";
		}

		public String getDescription() {
			return "Simple demo with custom drag rectangles";
		}

		public Component createContentPane(JFrame frame) {
			return new DragRectangleSample(frame);
		}
	}

	private class DragPainterAction extends DockAction {
		public String getTitle() {
			return "Drag Painters";
		}

		public String getDescription() {
			return "Simple demo with multiple drag painters";
		}

		public Component createContentPane(JFrame frame) {
			return new DragPainterSample(frame);
		}
	}

	private class DecoratedDockableAction extends DockAction {
		public String getTitle() {
			return "Decorated Dockable";
		}

		public String getDescription() {
			return "Simple demo with decorated dockables";
		}

		public Component createContentPane(JFrame frame) {
			return new DecoratedDockableSample(frame);
		}
	}

	private class VisualizerAction extends DockAction {
		public String getTitle() {
			return "Visualizers";
		}

		public String getDescription() {
			return "Simple demo with a minimizer, a maximizer, and an externalizer";
		}

		public Component createContentPane(JFrame frame) {
			return new VisualizerSample(frame);
		}
	}

	// Main method.

	private class ImageMinimizeHeaderAction extends DockAction {
		public String getTitle() {
			return "Minimizer with images";
		}

		public String getDescription() {
			return "Simple demo with images for the minimized dockables";
		}

		public Component createContentPane(JFrame frame) {
			return new ImageMinimizeHeaderSample(frame);
		}
	}

	private class ButtonAction extends DockAction {
		public String getTitle() {
			return "Buttons and Tool Bars";
		}

		public String getDescription() {
			return "Simple demo with a draggable buttons and draggable tool bars";
		}

		public Component createContentPane(JFrame frame) {
			return new ButtonSample(frame);
		}
	}

}
