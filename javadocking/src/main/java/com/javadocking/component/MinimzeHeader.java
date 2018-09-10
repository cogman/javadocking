package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.dockable.action.ShowActionMode;
import com.javadocking.drag.DragListener;
import com.javadocking.util.DockingUtil;
import com.javadocking.visualizer.Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <p>
 * A header for a minimized dockable.
 * </p>
 * <p>
 * This header should be put in a dockable.The dockable can be dragged by dragging the header.
 * The header can be dragged, if it is put in a dockable.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class MinimzeHeader extends SelectableDockableHeader implements DraggableContent {

	/**
	 * The height of the header.
	 */
	private static final int HEADER_HEIGHT = 20;
	/**
	 * The width of the space between the header components.
	 */
	private static final int DIVIDER_WIDTH = 6;
	/**
	 * The maximum width of the header.
	 */
	private static final int MAXIMUM_WIDTH = 140;

	/**
	 * The border for the header, when it is selected.
	 */
	private Border selectedBorder = BorderFactory.createLineBorder(Color.gray);
	/**
	 * The border for the header, when it not selected.
	 */
	//private Border			deselectedBorder		= BorderFactory.createRaisedBevelBorder();
	private Border deselectedBorder = BorderFactory.createBevelBorder(1, Color.gray, Color.white);
	/**
	 * The dockable which is represented by this minimize header.
	 */
	private Dockable dockable;


	/**
	 * <p>
	 * Constructs a header for a dockable that is minimized.
	 * </p>
	 * <p>
	 * The title of the dockable is set in the header. The icon is displayed.
	 * </p>
	 * <p>
	 * If there are actions, they are also displayed. The buttons for the actions are created
	 * with the method {@link com.javadocking.component.SwComponentFactory#createIconButton(Action)}.
	 * The component factory that is used is the component factory of the {@link DockingManager}.
	 * </p>
	 * <p>
	 * No actions are shown when the header is not selected.
	 * All the dockable actions are shown when the header is selected.
	 * </p>
	 *
	 * @param    dockable        The dockable of the header.
	 * @param    position        The position of the header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public MinimzeHeader(Dockable dockable, int position) {
		this(dockable, position, ShowActionMode.NO_ACTIONS, ShowActionMode.ALL_ACTIONS);
	}

	/**
	 * <p>
	 * Constructs a header for a dockable that is docked in a tabbed pane.
	 * This header has to be put in the tab.
	 * </p>
	 * <p>
	 * The title of the dockable is set in the header. The icon is displayed.
	 * </p>
	 * <p>
	 * If there are actions, they are also displayed. The buttons for the actions are created
	 * with the method {@link com.javadocking.component.SwComponentFactory#createIconButton(Action)}.
	 * The component factory that is used is the component factory of the {@link DockingManager}.
	 * </p>
	 * <p>
	 * When the dockable is not selected, only the first row of actions of the dockable are shown.
	 * When the dockable is selected, all dockable actions are shown.
	 * </p>
	 *
	 * @param    dockable        The dockable of the header.
	 * @param    position        The position of the header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 * @param    showActionMode    Defines which actions are shown in the header, when the header is not selected.
	 * @param    selectedShowActionMode    Defines which actions are shown in the header, when the header is selected.
	 */
	public MinimzeHeader(Dockable dockable, int position, ShowActionMode showActionMode, ShowActionMode selectedShowActionMode) {

		super(dockable, position, showActionMode, selectedShowActionMode);

		this.dockable = dockable;

		this.addMouseListener(new SelectionListener());

		setBorder(deselectedBorder);
		addToolTip(dockable);

//		setSizes();

	}

	// Implementations of DraggableContent.

	public void addDragListener(DragListener dragListener) {

		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);
		JLabel titleLabel = getTitleLabel();
		if (titleLabel != null) {
			titleLabel.addMouseListener(dragListener);
			titleLabel.addMouseMotionListener(dragListener);
		}
		JLabel iconLabel = getIconLabel();
		if (iconLabel != null) {
			iconLabel.addMouseListener(dragListener);
			iconLabel.addMouseMotionListener(dragListener);
		}

	}

	// Getters / Setters.

	/**
	 * Gets the border, that is used when the haeder is not selected.
	 *
	 * @return The border, that is used when the haeder is not selected.
	 */
	public Border getDeselectedBorder() {
		return deselectedBorder;
	}

	/**
	 * Sets the border, that is used when the haeder is not selected.
	 *
	 * @param    border            The border, that is used when the haeder is not selected.
	 */
	public void setDeselectedBorder(Border border) {
		this.deselectedBorder = border;
	}

	/**
	 * Gets the border, when the haeder is selected.
	 *
	 * @return The border, when the haeder is selected.
	 */
	public Border getSelectedBorder() {
		return selectedBorder;
	}

	/**
	 * Sets the border, when the haeder is selected.
	 *
	 * @param    selectedBorder    The border, when the haeder is selected.
	 */
	public void setSelectedBorder(Border selectedBorder) {
		this.selectedBorder = selectedBorder;
	}

	// Overwritten methods.

	public void setSelected(boolean selected) {
		boolean oldSelected = super.isSelected();

		super.setSelected(selected);

		if (oldSelected != selected) {
			if (selected) {
				setBorder(selectedBorder);
			} else {
				setBorder(deselectedBorder);
			}
		}
	}

	public synchronized void addMouseListener(MouseListener mouseListener) {

		if (getTitleLabel() != null) {
			getTitleLabel().addMouseListener(mouseListener);
		}
		if (getIconLabel() != null) {
			getIconLabel().addMouseListener(mouseListener);
		}
		super.addMouseListener(mouseListener);

	}

	public Dimension getPreferredSize() {
		Dimension preferredSize = super.getPreferredSize();
		Dimension maximumSize = getMaximumSize();
		if (preferredSize.width > maximumSize.width) {
			return new Dimension(maximumSize.width, preferredSize.height);
		}
		return preferredSize;
	}

	// Protected metods.

	/**
	 * Get the height of the header.
	 *
	 * @return The height of the header.
	 */
	protected int getHeaderHeight() {
		return HEADER_HEIGHT;
	}

	/**
	 * Get the maximum width of the header.
	 *
	 * @return The maximum width of the header.
	 */
	protected int getHeaderMaximumWidth() {
		return MAXIMUM_WIDTH;
	}

	protected int calculatePreferredWidth() {

		int icon = 0;
		if (getIconLabel() != null) {
			icon = getIconLabel().getPreferredSize().width;
		}
		int action = 0;
		if (getActionPanel() != null) {
			for (int index = 0; index < getActionPanel().getComponentCount(); index++) {
				action += getActionPanel().getComponent(index).getPreferredSize().width;
			}
		}
		int selectedAction = 0;
		if (getSelectedActionPanel() != null) {
			for (int index = 0; index < getSelectedActionPanel().getComponentCount(); index++) {
				selectedAction += getSelectedActionPanel().getComponent(index).getPreferredSize().width;
			}
		}

		if (action > selectedAction) {
			selectedAction = action;
		}

		return DIVIDER_WIDTH * 2 + icon + getTitleLabel().getPreferredSize().width + selectedAction;

	}

	// Private metods.

	private void addToolTip(Dockable dockable) {
		// Set a tooltip on the components.
		String description = dockable.getDescription();
		if (description != null) {
			this.setToolTipText(description);
			if (getTitleLabel() != null) {
				getTitleLabel().setToolTipText(description);
			}
			if (getIconLabel() != null) {
				getIconLabel().setToolTipText(description);
			}
		}

	}

	// Private classes.

	/**
	 * Listens to the mouse events on this header.
	 */
	private class SelectionListener implements MouseListener {

		/**
		 * Don't react on the mouseClicked event when this is false.
		 */
		private boolean react = true;

		// Implementations of MouseListener.

		public void mouseReleased(MouseEvent mouseEvent) {
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {
				react = false;

				// Get the other dockables that are minimized in this minimizer.
				CompositeDockable compositeDockable = null;
				Object object = dockable.getVisualizer();
				if (object instanceof Visualizer) {
					int selectedIndex = -1;
					Visualizer visualizer = (Visualizer) object;
					Dockable[] dockables = new Dockable[visualizer.getVisualizedDockableCount()];
					for (int index = 0; index < visualizer.getVisualizedDockableCount(); index++) {
						Dockable dockableOfComposite = visualizer.getVisualizedDockable(index);
						dockables[index] = dockableOfComposite;
						if (dockable.equals(dockableOfComposite)) {
							selectedIndex = index;
						}
					}
					compositeDockable = new DefaultCompositeDockable(dockables, selectedIndex);
				}

				// Get the popup and show it.
				JPopupMenu popupMenu = DockingManager.getComponentFactory().createPopupMenu(dockable, compositeDockable);
				if (popupMenu != null) {
					popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			} else {
				react = true;
			}
		}

		public void mouseClicked(MouseEvent mouseEvent) {

			if (react) {
				if (mouseEvent.getClickCount() == 2) {
					// Create an action to restore the dockable. Perform it.
					Action restoreAction = new DefaultDockableStateAction(DockingUtil.retrieveDockableOfDockModel(dockable.getID()), DockableState.NORMAL);
					restoreAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Restore"));
				} else {
					setSelected(!isSelected());
				}
			}

		}

		public void mouseEntered(MouseEvent mouseEvent) {
		}

		public void mouseExited(MouseEvent mouseEvent) {
		}

		public void mousePressed(MouseEvent mouseEvent) {
		}

	}

}
