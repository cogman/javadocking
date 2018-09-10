package com.javadocking.dockgallery;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dock.factory.CompositeToolBarDockFactory;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.dock.factory.ToolBarDockFactory;
import com.javadocking.dockable.ButtonDockable;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DragListener;
import com.javadocking.model.FloatDockModel;
import com.javadocking.util.SmallPanel;
import com.javadocking.util.ToolBarButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ButtonSample extends JPanel {

	public ButtonSample(@NotNull JFrame frame) {
		super(new BorderLayout());

		// Create the dock model for the docks.
		FloatDockModel dockModel = new FloatDockModel();
		dockModel.addOwner("frame0", frame);

		// Give the dock model to the docking manager.
		DockingManager.setDockModel(dockModel);

		// Give the float dock a different child dock factory.
		// We don't want the floating docks to be splittable.
		// And we want the floating tool bars to be grids.
		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new LeafDockFactory(false));

		// Create the content for the dockable.
		SmallPanel smallText1 = new SmallPanel();
		SmallPanel smallText2 = new SmallPanel();

		// Create the dockable with the content.
		DefaultDockable dockable1 = new DefaultDockable("Window1", smallText1, "Window 1", null, DockingMode.ALL);
		DefaultDockable dockable2 = new DefaultDockable("Window2", smallText2, "Window 2", null, DockingMode.ALL);

		// Create the child tab dock.
		TabDock tabDock1 = new TabDock();
		TabDock tabDock2 = new TabDock();

		// Add the dockables to the tab dock.
		tabDock1.addDockable(dockable1, new Position(0));
		tabDock2.addDockable(dockable2, new Position(1));

		// Create the split dock.
		SplitDock splitDock = new SplitDock();

		// Add the child docks to the split dock.
		splitDock.addChildDock(tabDock1, new Position(Position.LEFT));
		splitDock.addChildDock(tabDock2, new Position(Position.RIGHT));
		splitDock.setDividerLocation(295);

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

		// Create the border dock for the buttons.
		BorderDock toolBarBorderDock = new BorderDock(new CompositeToolBarDockFactory(), splitDock);
		toolBarBorderDock.setMode(BorderDock.MODE_TOOL_BAR);
		CompositeLineDock compositeToolBarDock1 = new CompositeLineDock(CompositeLineDock.ORIENTATION_HORIZONTAL, false,
				new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		CompositeLineDock compositeToolBarDock2 = new CompositeLineDock(CompositeLineDock.ORIENTATION_VERTICAL, false,
				new ToolBarDockFactory(), DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		toolBarBorderDock.setDock(compositeToolBarDock1, Position.TOP);
		toolBarBorderDock.setDock(compositeToolBarDock2, Position.LEFT);

		// The line docks for the buttons.
		LineDock toolBarDock1 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock2 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock3 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock4 = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock5 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock6 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		LineDock toolBarDock7 = new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
		GridDock toolGridDock = new GridDock(DockingMode.TOOL_GRID);

		// Add the dockables to the line docks and the grid dock.
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

		toolGridDock.addDockable(buttonDockables[20], new Position(0));
		toolGridDock.addDockable(buttonDockables[21], new Position(1));
		toolGridDock.addDockable(buttonDockables[22], new Position(2));
		toolGridDock.addDockable(buttonDockables[23], new Position(3));
		toolGridDock.addDockable(buttonDockables[24], new Position(4));
		toolGridDock.addDockable(buttonDockables[25], new Position(5));
		toolGridDock.addDockable(buttonDockables[26], new Position(6));
		toolGridDock.addDockable(buttonDockables[27], new Position(7));
		toolGridDock.addDockable(buttonDockables[28], new Position(8));

		toolBarDock6.addDockable(buttonDockables[29], new Position(0));
		toolBarDock6.addDockable(buttonDockables[30], new Position(1));
		toolBarDock6.addDockable(buttonDockables[31], new Position(2));
		toolBarDock6.addDockable(buttonDockables[32], new Position(3));
		toolBarDock6.addDockable(buttonDockables[33], new Position(4));
		toolBarDock6.addDockable(buttonDockables[34], new Position(5));
		toolBarDock6.addDockable(buttonDockables[35], new Position(6));
		toolBarDock6.addDockable(buttonDockables[36], new Position(6));

		toolBarDock7.addDockable(buttonDockables[37], new Position(0));
		toolBarDock7.addDockable(buttonDockables[38], new Position(1));
		toolBarDock7.addDockable(buttonDockables[39], new Position(2));
		toolBarDock7.addDockable(buttonDockables[40], new Position(3));
		toolBarDock7.addDockable(buttonDockables[41], new Position(4));

		// Add the line docks and grid to their composite parents.
		compositeToolBarDock1.addChildDock(toolBarDock1, new Position(0));
		compositeToolBarDock1.addChildDock(toolBarDock2, new Position(1));
		compositeToolBarDock1.addChildDock(toolBarDock3, new Position(2));
		compositeToolBarDock1.addChildDock(toolBarDock4, new Position(3));
		compositeToolBarDock2.addChildDock(toolBarDock5, new Position(0));
		compositeToolBarDock2.addChildDock(toolBarDock6, new Position(1));
		compositeToolBarDock2.addChildDock(toolBarDock7, new Position(2));
		floatDock.addChildDock(toolGridDock, new Position(400, 300));

		// Add the root dock.
		add(toolBarBorderDock, BorderLayout.CENTER);
		dockModel.addRootDock("borderDock", toolBarBorderDock, frame);

		// Set the frame properties.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 600;
		int height = 400;
		frame.setLocation((screenSize.width - width) / 2, (screenSize.height - height) / 2);
		frame.setSize(width, height);

		frame.getContentPane().add(this);

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
	@NotNull
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
	private void createDockableDragger(@NotNull Dockable dockable) {

		// Create the dragger for the dockable.
		DragListener dragListener = DockingManager.getDockableDragListenerFactory().createDragListener(dockable);
		dockable.getContent().addMouseListener(dragListener);
		dockable.getContent().addMouseMotionListener(dragListener);
	}

	// Private classes.

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

}
