package com.javadocking.dockablecreation;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.docker.BorderDocker;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.StateActionDockable;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.event.DockingEvent;
import com.javadocking.event.DockingListener;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockingPath;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.LookAndFeelUtil;
import com.javadocking.visualizer.DockingMinimizer;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.SingleMaximizer;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This example shows dockables that are created when the user presses a button.
 *
 * @author Heidi Rakels
 */
public class MainChartDockingExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 900;
	public static final int FRAME_HEIGHT = 650;

	// Fields.

	/**
	 * All the dockables of the application.
	 */
	private Dockable[] dockables;
	private int chartCount = 0;
	private DockingPath dockingPath;
	private JMenu windowMenu;

	// Constructors.

	public MainChartDockingExample(JFrame frame) {

		super(new BorderLayout());

		// Create the dock model for the docks, minimizer and maximizer.
		FloatDockModel dockModel = new FloatDockModel("workspace.dck");
		String frameId = "frame";
		dockModel.addOwner(frameId, frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Create the content components.


		// The dockables.
		dockables = new Dockable[5];
		JPanel buttonPanel = createButtonPanel();
		JPanel helloPanel = createHelloPanel();
		JPanel chartPanel1 = new DynamicChart();
		JPanel chartPanel2 = new DynamicChart();
		JPanel chartPanel3 = new DynamicChart();

		// Create the dockables around the content components.
		dockables[0] = createDockable("buttonPanel", buttonPanel, "Create Chart", null, "Create new charts");
		dockables[1] = createDockable("helloPanel", helloPanel, "Hello", null, "Hello, I am another dockable");
		dockables[2] = createDockable("chartPanel1", chartPanel1, "Chart 1", null, "Chart number 1");
		dockables[3] = createDockable("chartPanel2", chartPanel2, "Chart 2", null, "Chart number 2");
		dockables[4] = createDockable("chartPanel3", chartPanel3, "Chart 3", null, "Chart number 3");

		// Give the float dock a different child dock factory.
		// We don't want the floating docks to be splittable.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new LeafDockFactory(false));

		// Create the tab docks.
		TabDock centerTabbedDock = new TabDock();
		TabDock leftTabbedDock = new TabDock();
		TabDock bottomTabbedDock = new TabDock();

		// Add the dockables to these tab docks.
		centerTabbedDock.addDockable(dockables[2], new Position(0));
		centerTabbedDock.addDockable(dockables[3], new Position(1));
		centerTabbedDock.addDockable(dockables[4], new Position(2));
		centerTabbedDock.setSelectedDockable(dockables[2]);
		leftTabbedDock.addDockable(dockables[0], new Position(0));
		bottomTabbedDock.addDockable(dockables[1], new Position(1));


		SplitDock rightSplitDock = new SplitDock();
		rightSplitDock.addChildDock(bottomTabbedDock, new Position(Position.BOTTOM));
		rightSplitDock.addChildDock(centerTabbedDock, new Position(Position.TOP));
		rightSplitDock.setDividerLocation(400);
		SplitDock leftSplitDock = new SplitDock();
		leftSplitDock.addChildDock(leftTabbedDock, new Position(Position.CENTER));
		SplitDock totalSplitDock = new SplitDock();
		totalSplitDock.addChildDock(leftSplitDock, new Position(Position.LEFT));
		totalSplitDock.addChildDock(rightSplitDock, new Position(Position.RIGHT));
		totalSplitDock.setDividerLocation(180);

		// Add the root dock to the dock model.
		dockModel.addRootDock("totalDock", totalSplitDock, frame);

		// Create a maximizer and add it to the dock model.
		SingleMaximizer maximizePanel = new SingleMaximizer(totalSplitDock);
		dockModel.addVisualizer("maximizer", maximizePanel, frame);

		// Create a docking minimizer and add it to the dock model.
		BorderDock minimizerBorderDock = new BorderDock(new ToolBarDockFactory());
		minimizerBorderDock.setMode(BorderDock.MODE_MINIMIZE_BAR);
		minimizerBorderDock.setCenterComponent(maximizePanel);
		BorderDocker borderDocker = new BorderDocker();
		borderDocker.setBorderDock(minimizerBorderDock);
		DockingMinimizer minimizer = new DockingMinimizer(borderDocker);
		dockModel.addVisualizer("minimizer", minimizer, frame);

		// Add an externalizer to the dock model.
		dockModel.addVisualizer("externalizer", new FloatExternalizer(frame), frame);

		// Add this dock also as root dock to the dock model.
		dockModel.addRootDock("minimizerBorderDock", minimizerBorderDock, frame);

		// Add the border dock to this panel.
		this.add(minimizerBorderDock, BorderLayout.CENTER);

		// Add the paths of the docked dockables to the model with the docking paths.
		addDockingPath(dockables[0]);
		addDockingPath(dockables[1]);
		addDockingPath(dockables[2]);
		addDockingPath(dockables[3]);
		addDockingPath(dockables[4]);

		dockingPath = DockingManager.getDockingPathModel().getDockingPath(dockables[2].getID());

		// Create the menubar.
		JMenuBar menuBar = createMenu(dockables);
		frame.setJMenuBar(menuBar);

	}

	public static void createAndShowGUI() {

		// Remove the borders from the split panes and the split pane dividers.
		LookAndFeelUtil.removeAllSplitPaneBorders();

		// Create the frame.
		JFrame frame = new JFrame("Main Example");

		// Set the default location and size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		MainChartDockingExample panel = new MainChartDockingExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public static void main(String args[]) {
		Runnable doCreateAndShowGUI = MainChartDockingExample::createAndShowGUI;
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		JButton button = new JButton("Create new chart");
		button.addActionListener(new AddChartListener());
		panel.add(button);
		return panel;
	}

	private JPanel createHelloPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		JButton button = new JButton("Hello, I am another content of a dockable");
		button.addActionListener(new HelloListener());
		panel.add(button);
		return panel;
	}

	/**
	 * Decorates the given dockable with all state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addAllActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), DockableState.statesAllExceptClosed());
		return wrapper;

	}


	/**
	 * Creates a dockable for a given content component.
	 *
	 * @param id      The ID of the dockable. The IDs of all dockables should be different.
	 * @param content The content of the dockable.
	 * @param title   The title of the dockable.
	 * @param icon    The icon of the dockable.
	 * @throws IllegalArgumentException If the given ID is null.
	 * @return The created dockable.
	 */
	private Dockable createDockable(String id, Component content, String title, Icon icon, String description) {

		// Create the dockable.
		DefaultDockable dockable = new DefaultDockable(id, content, title, icon);

		// Add a description to the dockable. It will be displayed in the tool tip.
		dockable.setDescription(description);

		Dockable dockableWithActions = addAllActions(dockable);

		return dockableWithActions;

	}

	/**
	 * Creates the menubar with menus: File, Window, Look and Feel, and Drag Painting.
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
		fileMenu.getAccessibleContext().setAccessibleDescription("The File Menu");
		menuBar.add(fileMenu);

		// Build the Window menu.
		windowMenu = new JMenu("Window");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.getAccessibleContext().setAccessibleDescription("The Window Menu");
		menuBar.add(windowMenu);

		// The JMenuItem for File
		JMenuItem menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Exit te application");
		menuItem.addActionListener(arg0 -> System.exit(0));
		fileMenu.add(menuItem);

		// The JMenuItems for the dockables.
		for (int index = 0; index < dockables.length; index++) {
			// Create the check box menu for the dockable.
			JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(dockables[index]);
			windowMenu.add(cbMenuItem);
		}

		return menuBar;

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

	// Private classes.

	private class AddChartListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {

			// Create the chart.
			JPanel chartPanel = new DynamicChart();

			// Create the dockable for the chart.
			Dockable dockable = createDockable("chartPanel" + chartCount, chartPanel, "Chart " + chartCount, null, "Chart number " + chartCount);

			// Where do we want the dockable to be placed?
			DockingPath newDockingPath = DefaultDockingPath.copyDockingPath(dockable, dockingPath);
			DockingManager.getDockingPathModel().add(newDockingPath);

			// Add the dockable.
			DockingManager.getDockingExecutor().changeDocking(dockable, dockingPath);

			JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(dockable);
			windowMenu.add(cbMenuItem);

		}
	}

	private class HelloListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(MainChartDockingExample.this, "Hello");
		}
	}

	// Main method.

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

	private class DynamicChart extends JPanel {

		private ChartPanel chartPanel = null;

		public DynamicChart() {
			super(new BorderLayout());

			Plot plot = createPlot();

			chartCount++;
			JFreeChart chart = new JFreeChart("Chart " + chartCount,
					new Font("SansSerif", Font.BOLD, 24), plot, true);

			ChartUtilities.applyCurrentTheme(chart);

			chartPanel = new ChartPanel(chart, true);
			chartPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createLineBorder(Color.black)));
			add(chartPanel);

		}

		public JPanel get_chart_panel() {
			return chartPanel;
		}

		private Plot createPlot() {
			// create subplot 1...
			final XYDataset data1 = createDataset1();
			final XYItemRenderer renderer1 = new StandardXYItemRenderer();
			final NumberAxis rangeAxis1 = new NumberAxis("Range 1");
			final XYPlot subplot1 = new XYPlot(data1, null, rangeAxis1, renderer1);
			subplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

			// add secondary axis
			subplot1.setDataset(1, createDataset2());
			final NumberAxis axis2 = new NumberAxis("Range Axis 2");
			axis2.setAutoRangeIncludesZero(false);
			subplot1.setRangeAxis(1, axis2);
			subplot1.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
			subplot1.setRenderer(1, new StandardXYItemRenderer());
			subplot1.mapDatasetToRangeAxis(1, 1);

			final XYTextAnnotation annotation = new XYTextAnnotation("Hello!", 50.0, 10000.0);
			annotation.setFont(new Font("SansSerif", Font.PLAIN, 9));
			annotation.setRotationAngle(Math.PI / 4.0);
			subplot1.addAnnotation(annotation);

			// create subplot 2...
			final XYDataset data2 = createDataset2();
			final XYItemRenderer renderer2 = new StandardXYItemRenderer();
			final NumberAxis rangeAxis2 = new NumberAxis("Range 2");
			rangeAxis2.setAutoRangeIncludesZero(false);
			final XYPlot subplot2 = new XYPlot(data2, null, rangeAxis2, renderer2);
			subplot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);

			// parent plot...
			final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Domain"));
			plot.setGap(10.0);

			// add the subplots...
			plot.add(subplot1, 1);
			plot.add(subplot2, 1);
			plot.setOrientation(PlotOrientation.VERTICAL);
			return plot;
		}

		/**
		 * Creates a sample dataset.
		 *
		 * @return Series 1.
		 */
		private XYDataset createDataset1() {

			// create dataset 1...
			final XYSeries series1 = new XYSeries("Series 1a");
			series1.add(10.0, 12353.3);
			series1.add(20.0, 13734.4);
			series1.add(30.0, 14525.3);
			series1.add(40.0, 13984.3);
			series1.add(50.0, 12999.4);
			series1.add(60.0, 14274.3);
			series1.add(70.0, 15943.5);
			series1.add(80.0, 14845.3);
			series1.add(90.0, 14645.4);
			series1.add(100.0, 16234.6);
			series1.add(110.0, 17232.3);
			series1.add(120.0, 14232.2);
			series1.add(130.0, 13102.2);
			series1.add(140.0, 14230.2);
			series1.add(150.0, 11235.2);

			final XYSeries series1b = new XYSeries("Series 1b");
			series1b.add(10.0, 15000.3);
			series1b.add(20.0, 11000.4);
			series1b.add(30.0, 17000.3);
			series1b.add(40.0, 15000.3);
			series1b.add(50.0, 14000.4);
			series1b.add(60.0, 12000.3);
			series1b.add(70.0, 11000.5);
			series1b.add(80.0, 12000.3);
			series1b.add(90.0, 13000.4);
			series1b.add(100.0, 12000.6);
			series1b.add(110.0, 13000.3);
			series1b.add(120.0, 17000.2);
			series1b.add(130.0, 18000.2);
			series1b.add(140.0, 16000.2);
			series1b.add(150.0, 17000.2);

			final XYSeriesCollection collection = new XYSeriesCollection();
			collection.addSeries(series1);
			collection.addSeries(series1b);
			return collection;

		}

		/**
		 * Creates a sample dataset.
		 *
		 * @return A sample dataset.
		 */
		private XYDataset createDataset2() {

			// create dataset 2...
			final XYSeries series2 = new XYSeries("Series 2");

			series2.add(10.0, 16853.2);
			series2.add(20.0, 19642.3);
			series2.add(30.0, 18253.5);
			series2.add(40.0, 15352.3);
			series2.add(50.0, 13532.0);
			series2.add(100.0, 12635.3);
			series2.add(110.0, 13998.2);
			series2.add(120.0, 11943.2);
			series2.add(130.0, 16943.9);
			series2.add(140.0, 17843.2);
			series2.add(150.0, 16495.3);
			series2.add(160.0, 17943.6);
			series2.add(170.0, 18500.7);
			series2.add(180.0, 19595.9);

			return new XYSeriesCollection(series2);

		}

	}
}

