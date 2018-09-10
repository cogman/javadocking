package com.javadocking.visualizer;

import com.javadocking.DockingManager;
import com.javadocking.component.DefaultSwComponentFactory;
import com.javadocking.component.ImageMinimzeHeader;
import com.javadocking.component.SelectableHeader;
import com.javadocking.dock.BorderDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dock.docker.BorderDocker;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.StateActionDockable;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.Book;
import com.javadocking.util.DeBelloGallico;

import javax.swing.*;
import java.awt.*;

/**
 * This example shows the usage of a minimizer and a maximizer.
 * The minimize headers are {@link com.javadocking.component.ImageMinimzeHeader}s.
 * They show a little image of the dockable component, when the dockable is minimized.
 * The minimizer is a com.javadocking.visualizer.DockingMinimizer.
 * The maximizer is a com.javadocking.visualizer.SingleMaximizer.
 * The minimized dockables can be dragged and docked in other special docks for minimizers.
 * The dockables have actions to minimize, maximize, and restore itself.
 * Some dockables are already minimized, when the application is started.
 *
 * @author Heidi Rakels
 */
public class ImageDockingMinimizerExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 550;

	// Constructor.

	public ImageDockingMinimizerExample(JFrame frame) {
		super(new BorderLayout());

		// Set our custom component factory that creates headers for minimized dockables with an image.
		DockingManager.setComponentFactory(new CustomComponentFactory());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

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
		Book book4 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER4.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER4),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapbelgae150.gif")));
		Book book5 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER5.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER5),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapmaas150.gif")));
		Book book6 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER6.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER6),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/caesar100.jpg")));
		Book book7 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER7.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER7),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapbelgae150.gif")));
		Book book8 = new Book("De Bello Gallico: " + DeBelloGallico.LIBER8.getTitle(),
				DeBelloGallico.getText(DeBelloGallico.LIBER8),
				new ImageIcon(getClass().getResource("/com/javadocking/resources/images/mapmaas150.gif")));

		// Create the dockables around the content components.
		Icon icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif"));
		Dockable dockable1 = new DefaultDockable("Window1", book1, "Book 1", icon);
		Dockable dockable2 = new DefaultDockable("Window2", book2, "Book 2", icon);
		Dockable dockable3 = new DefaultDockable("Window3", book3, "Book 3", icon);
		Dockable dockable4 = new DefaultDockable("Window4", book4, "Book 4", icon);
		Dockable dockable5 = new DefaultDockable("Window5", book5, "Book 5", icon);
		Dockable dockable6 = new DefaultDockable("Window6", book6, "Book 6", icon);
		Dockable dockable7 = new DefaultDockable("Window7", book7, "Book 7", icon);
		Dockable dockable8 = new DefaultDockable("Window8", book8, "Book 8", icon);

		// Add descriptions for the tooltip.
		((DefaultDockable) dockable1).setDescription("<html>De Bello Gallico: Liber 1<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable2).setDescription("<html>De Bello Gallico: Liber 2<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable3).setDescription("<html>De Bello Gallico: Liber 3<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable4).setDescription("<html>De Bello Gallico: Liber 4<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable5).setDescription("<html>De Bello Gallico: Liber 5<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable6).setDescription("<html>De Bello Gallico: Liber 6<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable7).setDescription("<html>De Bello Gallico: Liber 7<br><i>Gaius Julius Caesar</i><html>");
		((DefaultDockable) dockable8).setDescription("<html>De Bello Gallico: Liber 8<br><i>Gaius Julius Caesar</i><html>");

		// Add minimize and maximize actions to the dockables.
		dockable1 = addActions(dockable1);
		dockable2 = addActions(dockable2);
		dockable3 = addActions(dockable3);
		dockable4 = addActions(dockable4);
		dockable5 = addActions(dockable5);
		dockable6 = addActions(dockable6);
		dockable7 = addActions(dockable7);
		dockable8 = addActions(dockable8);

		// Create the child tab dock.
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
		splitDock.setDividerLocation(395);

		// Add the root dock to the dock model.
		dockModel.addRootDock("splitDock", splitDock, frame);

		// Create a maximizer and add it to the dock model.
		SingleMaximizer maximizer = new SingleMaximizer(splitDock);
		dockModel.addVisualizer("maximizer", maximizer, frame);

		// Create a docking minimizer.
		BorderDock borderDock = new BorderDock(new ToolBarDockFactory());
		borderDock.setMode(BorderDock.MODE_MINIMIZE_BAR);
		borderDock.setCenterComponent(maximizer);
		BorderDocker borderDocker = new BorderDocker();
		borderDocker.setBorderDock(borderDock);
		DockingMinimizer minimizer = new DockingMinimizer(borderDocker);

		// Add the minimizer to the dock model, add also the border dock used by the minimizer to the dock model.
		dockModel.addVisualizer("minimizer", minimizer, frame);
		dockModel.addRootDock("minimizerBorderDock", borderDock, frame);

		// Add the border dock of the minimizer to this panel.
		this.add(borderDock, BorderLayout.CENTER);

		// Minimize dockables.
		minimizer.visualizeDockable(dockable5);
		minimizer.visualizeDockable(dockable6);
		minimizer.visualizeDockable(dockable7);
		minimizer.visualizeDockable(dockable8);

	}

	public static void createAndShowGUI() {

		// Create the frame.
		JFrame frame = new JFrame("Window Minimizer");

		// Create the panel and add it to the frame.
		ImageDockingMinimizerExample panel = new ImageDockingMinimizerExample(frame);
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
	 * Decorates the given dockable with a state actions.
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
		int[] states = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.MAXIMIZED};
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);
		return wrapper;

	}

	/**
	 * The header of a minimized dockable will be an image of the dockable component.
	 */
	private class CustomComponentFactory extends DefaultSwComponentFactory {

		// Overwritten methods.

		public SelectableHeader createMinimizeHeader(Dockable dockable, int position) {
			return new ImageMinimzeHeader(dockable, position);
		}

	}

}

