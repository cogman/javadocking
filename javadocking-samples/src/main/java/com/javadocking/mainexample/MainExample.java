package com.javadocking.mainexample;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.docker.BorderDocker;
import com.javadocking.dock.factory.CompositeToolBarDockFactory;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.drag.DragListener;
import com.javadocking.drag.DraggerFactory;
import com.javadocking.drag.StaticDraggerFactory;
import com.javadocking.drag.painter.*;
import com.javadocking.event.DockingEvent;
import com.javadocking.event.DockingListener;
import com.javadocking.model.*;
import com.javadocking.util.*;
import com.javadocking.visualizer.DockingMinimizer;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.SingleMaximizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Set;

/**
 * In this example graphical components are put in dockables.
 * The dockables can be dragged and docked in different docks.
 * <p>
 * Every dockable can be closed. When the dockable is added again later,
 * the dockable is docked as good as possible like it was docked before.
 * <p>
 * Most of the dockables can be maximized, except the 'Find' panel.
 * <p>
 * All the dockables can be minimized. The minimized components are also
 * put in dockables. They can be dragged and docked in other docks.
 * They can also be moved in their bar.
 * <p>
 * There are some tool bars with buttons. The buttons can be moved in their
 * tool bar or dragged to other tool bars. The tool bars can also be dragged
 * to other borders or they can be made floating.
 * <p>
 * The structure of the application window is like this:
 * First there is a border dock for tool bars with buttons.
 * Inside that border dock is a minimizer that minimizes the dockables at the borders.
 * Inside the minimizer is a maximizer.
 * Inside the maximizer is the root dock for all the normal docks.
 *
 * @author Heidi Rakels
 */
public class MainExample extends JPanel {

	// Static fields.

	public static final int FRAME_WIDTH = 900;
	public static final int FRAME_HEIGHT = 650;
	public static LAF[] LAFS;

	// Fields.

	/**
	 * All the dockables of the application.
	 */
	private Dockable[] dockables;
	/**
	 * All the dockables for buttons of the application.
	 */
	private Dockable[] buttonDockables;

	// Constructors.

	public MainExample(JFrame frame) {

		super(new BorderLayout());

		// Create the dock model for the docks, minimizer and maximizer.
		FloatDockModel dockModel = new FloatDockModel("workspace.dck");
		String frameId = "frame";
		dockModel.addOwner(frameId, frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

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
		Table table = new Table(Table.TABLE);
		ContactTree contactTree = new ContactTree();
		Find find = new Find();
		Picture sales = new Picture(new ImageIcon(getClass().getResource("/com/javadocking/resources/images/salesSm.gif")));
		Chart chart = new Chart();
		WordList wordList = new WordList();

		// The arrays for the dockables and button dockables.
		dockables = new Dockable[14];
		buttonDockables = new Dockable[42];

		// Create the dockables around the content components.
		dockables[0] = createDockable("Book1", book1, "Book 1", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 1<br><i>Gaius Julius Caesar</i><html>");
		dockables[1] = createDockable("Book2", book2, "Book 2", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 2<br><i>Gaius Julius Caesar</i><html>");
		dockables[2] = createDockable("Book3", book3, "Book 3", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 3<br><i>Gaius Julius Caesar</i><html>");
		dockables[3] = createDockable("Book4", book4, "Book 4", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 4<br><i>Gaius Julius Caesar</i><html>");
		dockables[4] = createDockable("Book5", book5, "Book 5", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 5<br><i>Gaius Julius Caesar</i><html>");
		dockables[5] = createDockable("Book6", book6, "Book 6", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 6<br><i>Gaius Julius Caesar</i><html>");
		dockables[6] = createDockable("Book7", book7, "Book 7", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 7<br><i>Gaius Julius Caesar</i><html>");
		dockables[7] = createDockable("Book8", book8, "Book 8", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/text12.gif")), "<html>De Bello Gallico: Liber 8<br><i>Gaius Julius Caesar</i><html>");
		dockables[8] = createDockable("Table", table, "Table", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/table12.gif")), "Table with hello");
		dockables[9] = createDockable("Contacts", contactTree, "Contacts", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/person12.gif")), "Sales Contacts");
		dockables[10] = createDockable("Find", find, "Find", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/binocular12.gif")), "Find a text");
		dockables[11] = createDockable("Sales", sales, "Sales", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/graph12.gif")), "Sales of Books and CDs");
		dockables[12] = createDockable("Chart", chart, "Chart", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/graph12.gif")), "Chart");
		dockables[13] = createDockable("Words", wordList, "Words", new ImageIcon(getClass().getResource("/com/javadocking/resources/images/table12.gif")), "Roman Gods");

		// The dockable with the find panel may not be maximized.
		((DefaultDockable) dockables[10]).setPossibleStates(DockableState.CLOSED | DockableState.NORMAL | DockableState.MINIMIZED | DockableState.EXTERNALIZED);

		// Add actions to the dockables.
		for (int index = 0; index < dockables.length; index++) {
			if (index == 10) {
				// All actions, except the maximize.
				dockables[index] = addLimitActions(dockables[index]);
			} else {
				// All actions.
				dockables[index] = addAllActions(dockables[index]);
			}
		}

		// Create the buttons with a dockable around.
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

		// Give the float dock a different child dock factory.
		// We don't want the floating docks to be splittable.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new LeafDockFactory(false));

		// Create the tab docks.
		TabDock centerTabbedDock = new TabDock();
		TabDock bottomTabbedDock = new TabDock();
		TabDock leftTabbedDock = new TabDock();
		TabDock rightTabbedDock = new TabDock();

		// Add the dockables to these tab docks.
		centerTabbedDock.addDockable(dockables[0], new Position(0));
		centerTabbedDock.addDockable(dockables[1], new Position(1));
		centerTabbedDock.addDockable(dockables[2], new Position(2));
		centerTabbedDock.setSelectedDockable(dockables[0]);
		bottomTabbedDock.addDockable(dockables[11], new Position(0));
		bottomTabbedDock.addDockable(dockables[12], new Position(1));
		bottomTabbedDock.setSelectedDockable(dockables[12]);
		leftTabbedDock.addDockable(dockables[9], new Position(0));
		rightTabbedDock.addDockable(dockables[13], new Position(0));

		// The 4 windows have to be splittable.
		SplitDock centerSplitDock = new SplitDock();
		centerSplitDock.addChildDock(centerTabbedDock, new Position(Position.CENTER));
		centerSplitDock.addChildDock(rightTabbedDock, new Position(Position.RIGHT));
		centerSplitDock.setDividerLocation(530);
		SplitDock bottomSplitDock = new SplitDock();
		bottomSplitDock.addChildDock(bottomTabbedDock, new Position(Position.CENTER));
		SplitDock rightSplitDock = new SplitDock();
		rightSplitDock.addChildDock(centerSplitDock, new Position(Position.CENTER));
		rightSplitDock.addChildDock(bottomSplitDock, new Position(Position.BOTTOM));
		rightSplitDock.setDividerLocation(400);
		SplitDock leftSplitDock = new SplitDock();
		leftSplitDock.addChildDock(leftTabbedDock, new Position(Position.CENTER));
		SplitDock totalSplitDock = new SplitDock();
		totalSplitDock.addChildDock(leftSplitDock, new Position(Position.LEFT));
		totalSplitDock.addChildDock(rightSplitDock, new Position(Position.RIGHT));
		totalSplitDock.setDividerLocation(180);

		// Add the root dock to the dock model.
		dockModel.addRootDock("totalDock", totalSplitDock, frame);

		// Dockable 10 should float. Add dockable 10 to the float dock of the dock model (this is a default root dock).
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		floatDock.addDockable(dockables[10], new Point(screenSize.width / 2 + 100, screenSize.height / 2 + 60), new Point());

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

		// Create the tool bar border dock for the buttons.
		BorderDock toolBarBorderDock = new BorderDock(new CompositeToolBarDockFactory(), minimizerBorderDock);
		toolBarBorderDock.setMode(BorderDock.MODE_TOOL_BAR);
		CompositeLineDock compositeToolBarDock1 = new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
				new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		CompositeLineDock compositeToolBarDock2 = new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
				new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		toolBarBorderDock.setDock(compositeToolBarDock1, Position.TOP);
		toolBarBorderDock.setDock(compositeToolBarDock2, Position.LEFT);

		// Add this dock also as root dock to the dock model.
		dockModel.addRootDock("toolBarBorderDock", toolBarBorderDock, frame);

		// Add the tool bar border dock to this panel.
		this.add(toolBarBorderDock, BorderLayout.CENTER);

		// The line docks and one grid dock for the buttons.
		LineDock toolBarDock1 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock2 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock3 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock4 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock5 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock6 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		GridDock toolGridDock = new GridDock(DockingMode.TOOL_GRID);

		// Add the button dockables to the line docks.
		toolBarDock1.addDockable(buttonDockables[0], new Position(0));
		toolBarDock1.addDockable(buttonDockables[1], new Position(1));
		toolBarDock1.addDockable(buttonDockables[2], new Position(2));
		toolBarDock1.addDockable(buttonDockables[3], new Position(3));
		toolBarDock1.addDockable(buttonDockables[4], new Position(4));
		toolBarDock1.addDockable(buttonDockables[5], new Position(5));
		toolBarDock1.addDockable(buttonDockables[6], new Position(6));
		toolBarDock2.addDockable(buttonDockables[7], new Position(0));
		toolBarDock2.addDockable(buttonDockables[8], new Position(1));
		toolBarDock2.addDockable(buttonDockables[9], new Position(2));
		toolBarDock3.addDockable(buttonDockables[10], new Position(0));
		toolBarDock3.addDockable(buttonDockables[11], new Position(1));
		toolBarDock3.addDockable(buttonDockables[12], new Position(2));
		toolBarDock4.addDockable(buttonDockables[13], new Position(0));
		toolBarDock4.addDockable(buttonDockables[14], new Position(1));
		toolBarDock4.addDockable(buttonDockables[15], new Position(2));
		toolBarDock4.addDockable(buttonDockables[16], new Position(3));
		toolBarDock4.addDockable(buttonDockables[17], new Position(4));
		toolBarDock5.addDockable(buttonDockables[18], new Position(0));
		toolBarDock5.addDockable(buttonDockables[19], new Position(1));
		toolBarDock6.addDockable(buttonDockables[37], new Position(0));
		toolBarDock6.addDockable(buttonDockables[38], new Position(1));
		toolBarDock6.addDockable(buttonDockables[39], new Position(2));
		toolBarDock6.addDockable(buttonDockables[40], new Position(3));
		toolBarDock6.addDockable(buttonDockables[41], new Position(4));

		// Add the button dockables to the grid dock.
		toolGridDock.addDockable(buttonDockables[20], new Position(0));
		toolGridDock.addDockable(buttonDockables[21], new Position(1));
		toolGridDock.addDockable(buttonDockables[22], new Position(2));
		toolGridDock.addDockable(buttonDockables[23], new Position(3));
		toolGridDock.addDockable(buttonDockables[24], new Position(4));
		toolGridDock.addDockable(buttonDockables[25], new Position(5));
		toolGridDock.addDockable(buttonDockables[26], new Position(6));
		toolGridDock.addDockable(buttonDockables[27], new Position(7));
		toolGridDock.addDockable(buttonDockables[28], new Position(8));
		toolGridDock.addDockable(buttonDockables[29], new Position(9));
		toolGridDock.addDockable(buttonDockables[30], new Position(10));
		toolGridDock.addDockable(buttonDockables[31], new Position(11));
		toolGridDock.addDockable(buttonDockables[32], new Position(12));
		toolGridDock.addDockable(buttonDockables[33], new Position(13));
		toolGridDock.addDockable(buttonDockables[34], new Position(14));
		toolGridDock.addDockable(buttonDockables[35], new Position(15));
		toolGridDock.addDockable(buttonDockables[36], new Position(16));

		// Add the line docks to their composite parents.
		compositeToolBarDock1.addChildDock(toolBarDock1, new Position(0));
		compositeToolBarDock1.addChildDock(toolBarDock2, new Position(1));
		compositeToolBarDock1.addChildDock(toolBarDock3, new Position(2));
		compositeToolBarDock1.addChildDock(toolBarDock4, new Position(3));
		compositeToolBarDock2.addChildDock(toolBarDock5, new Position(0));
		compositeToolBarDock2.addChildDock(toolBarDock6, new Position(1));

		// Add the grid dock to the float dock.
		floatDock.addChildDock(toolGridDock, new Position(screenSize.width / 2 + 100, screenSize.height / 2 - 170));

		// Minimize dockable 3, 4, 5, 6, 7.
		minimizer.visualizeDockable(dockables[3]);
		minimizer.visualizeDockable(dockables[4]);
		minimizer.visualizeDockable(dockables[5]);
		minimizer.visualizeDockable(dockables[6]);
		minimizer.visualizeDockable(dockables[7]);

		// Add the paths of the docked dockables to the model with the docking paths.
		addDockingPath(dockables[0]);
		addDockingPath(dockables[1]);
		addDockingPath(dockables[2]);
		addDockingPath(dockables[9]);
		addDockingPath(dockables[10]);
		addDockingPath(dockables[11]);
		addDockingPath(dockables[12]);
		addDockingPath(dockables[1]);

		// Add the path of the dockables that are not docked already.
		// We want dockable 8 to be docked, when it is made visible, where dockable 11 is docked.
		DockingPath dockingPathToCopy11 = DockingManager.getDockingPathModel().getDockingPath(dockables[11].getID());
		DockingPath dockingPath8 = DefaultDockingPath.copyDockingPath(dockables[8], dockingPathToCopy11);
		DockingManager.getDockingPathModel().add(dockingPath8);
		// We want dockable 3, 4, 5, 6, 7 to be docked, when they are made visible, where dockable 0 is docked.
		DockingPath dockingPathToCopy1 = DockingManager.getDockingPathModel().getDockingPath(dockables[0].getID());
		DockingPath dockingPath3 = DefaultDockingPath.copyDockingPath(dockables[3], dockingPathToCopy1);
		DockingPath dockingPath4 = DefaultDockingPath.copyDockingPath(dockables[4], dockingPathToCopy1);
		DockingPath dockingPath5 = DefaultDockingPath.copyDockingPath(dockables[5], dockingPathToCopy1);
		DockingPath dockingPath6 = DefaultDockingPath.copyDockingPath(dockables[6], dockingPathToCopy1);
		DockingPath dockingPath7 = DefaultDockingPath.copyDockingPath(dockables[7], dockingPathToCopy1);
		DockingManager.getDockingPathModel().add(dockingPath3);
		DockingManager.getDockingPathModel().add(dockingPath4);
		DockingManager.getDockingPathModel().add(dockingPath5);
		DockingManager.getDockingPathModel().add(dockingPath6);
		DockingManager.getDockingPathModel().add(dockingPath7);

		// Create the menubar.
		JMenuBar menuBar = createMenu(dockables);
		frame.setJMenuBar(menuBar);

	}

	public static void createAndShowGUI() {

		// Create the look and feels.
		LAFS = new LAF[9];
		LAFS[0] = new LAF("Substance", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel", LAF.THEME_DEAULT);
		LAFS[1] = new LAF("Mac", "javax.swing.plaf.mac.MacLookAndFeel", LAF.THEME_DEAULT);
		LAFS[2] = new LAF("Metal", "javax.swing.plaf.metal.MetalLookAndFeel", LAF.THEME_DEAULT);
		LAFS[3] = new LAF("Liquid", "com.birosoft.liquid.LiquidLookAndFeel", LAF.THEME_DEAULT);
		LAFS[4] = new LAF("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", LAF.THEME_DEAULT);
		LAFS[5] = new LAF("Nimrod Ocean", "com.nilo.plaf.nimrod.NimRODLookAndFeel", LAF.THEME_OCEAN);
		LAFS[6] = new LAF("Nimrod Gold", "com.nilo.plaf.nimrod.NimRODLookAndFeel", LAF.THEME_GOLD);
		LAFS[7] = new LAF("Nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", LAF.THEME_DEAULT);
		LAFS[8] = new LAF("TinyLaF", "de.muntjak.tinylookandfeel.TinyLookAndFeel", LAF.THEME_DEAULT);

		// Set the first enabled look and feel.

		try {
			if (LAFS[4].isSupported()) {
				LAFS[4].setSelected(true);
				UIManager.setLookAndFeel(LAFS[4].getClassName());

			}
		} catch (Exception e) {
		}


		// Remove the borders from the split panes and the split pane dividers.
		LookAndFeelUtil.removeAllSplitPaneBorders();

		// Create the frame.
		JFrame frame = new JFrame("Main Example");

		// Set the default location and size.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((screenSize.width - FRAME_WIDTH) / 2, (screenSize.height - FRAME_HEIGHT) / 2);
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);

		// Create the panel and add it to the frame.
		MainExample panel = new MainExample(frame);
		frame.getContentPane().add(panel);

		// Set the frame properties and show it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	 * Decorates the given dockable with some state actions (not maximized).
	 *
	 * @param dockable The dockable to decorate.
	 * @return The wrapper around the given dockable, with actions.
	 */
	private Dockable addLimitActions(Dockable dockable) {

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), DockableState.statesClosed());
		int[] limitStates = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.EXTERNALIZED};
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), limitStates);
		return wrapper;

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

		return dockable;

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
		JMenu windowMenu = new JMenu("Window");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.getAccessibleContext().setAccessibleDescription("The Window Menu");
		menuBar.add(windowMenu);

		// Build the Look and Feel menu.
		JMenu lookAndFeelMenu = new JMenu("Look and Feel");
		lookAndFeelMenu.setMnemonic(KeyEvent.VK_L);
		lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("The Lool and Feel Menu");
		menuBar.add(lookAndFeelMenu);

		// Build the Dragging menu.
		JMenu draggingMenu = new JMenu("Drag Painting");
		draggingMenu.setMnemonic(KeyEvent.VK_D);
		draggingMenu.getAccessibleContext().setAccessibleDescription("The Dragging Menu");
		menuBar.add(draggingMenu);

		// The JMenuItem for File
		JMenuItem menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Exit te application");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(menuItem);

		// The JMenuItems for the dockables.
		for (int index = 0; index < dockables.length; index++) {
			// Create the check box menu for the dockable.
			JCheckBoxMenuItem cbMenuItem = new DockableMenuItem(dockables[index]);
			windowMenu.add(cbMenuItem);
		}

		// The JMenuItems for the look and feels.
		ButtonGroup group = new ButtonGroup();
		for (int index = 0; index < LAFS.length; index++) {
			LafMenuItem lafMenuItem = new LafMenuItem(LAFS[index]);
			lookAndFeelMenu.add(lafMenuItem);
			group.add(lafMenuItem);
		}

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
		group = new ButtonGroup();
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

	// Private classes.

	/**
	 * Sets the look and feel on the application.
	 *
	 * @param lafClassName The class name of the new look and feel.
	 */
	private void setLookAndFeel(LAF laf) {

		try {
			UIManager.setLookAndFeel(laf.getClassName());
			LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
			LAF.setTheme(lookAndFeel, laf.getTheme());
			UIManager.setLookAndFeel(lookAndFeel);

			// Iterate over the owner windows of the dock model.
			DockModel dockModel = DockingManager.getDockModel();
			for (int index = 0; index < dockModel.getOwnerCount(); index++) {

				// Set the LaF on the owner.
				Window owner = dockModel.getOwner(index);
				SwingUtilities.updateComponentTreeUI(owner);

				// Set the LaF on the floating windows.
				Set floatDocks = DockModelUtil.getVisibleFloatDocks(dockModel, owner);
				Iterator iterator = floatDocks.iterator();
				while (iterator.hasNext()) {
					FloatDock floatDock = (FloatDock) iterator.next();
					for (int childIndex = 0; childIndex < floatDock.getChildDockCount(); childIndex++) {
						Component floatingComponent = (Component) floatDock.getChildDock(childIndex);
						SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(floatingComponent));
					}
				}

				// Set the LaF on all the dockable components.
				for (int dockableIndex = 0; dockableIndex < dockables.length; dockableIndex++) {
					SwingUtilities.updateComponentTreeUI(dockables[dockableIndex].getContent());
				}
				for (int dockableIndex = 0; dockableIndex < buttonDockables.length; dockableIndex++) {
					SwingUtilities.updateComponentTreeUI(buttonDockables[dockableIndex].getContent());
				}

			}
		} catch (Exception e) {
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
	 * A check box menu item to enable a look and feel.
	 */
	private class LafMenuItem extends JRadioButtonMenuItem {

		public LafMenuItem(LAF laf) {
			super(laf.getTitle());

			// Is this look and feel supported?
			if (laf.isSupported()) {
				LafListener lafItemListener = new LafListener(laf);
				addActionListener(lafItemListener);
			} else {
				setEnabled(false);
			}

			if (laf.isSelected()) {
				setSelected(true);
			}
		}
	}

	/**
	 * A listener that installs its look and feel.
	 */
	private class LafListener implements ActionListener {

		// Fields.

		private LAF laf;

		// Constructors.

		public LafListener(LAF laf) {
			this.laf = laf;
		}

		// Implementations of ItemListener.

		public void actionPerformed(ActionEvent arg0) {
			for (int index = 0; index < LAFS.length; index++) {
				LAFS[index].setSelected(false);
			}
			setLookAndFeel(laf);
			laf.setSelected(true);
		}

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

	// Main method.

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

}

