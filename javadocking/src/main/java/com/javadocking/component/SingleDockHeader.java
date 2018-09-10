package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.drag.DragListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <p>
 * The default header for a dock that contains one dockable. It displays information about the dockable.
 * </p>
 * <p>
 * This handle can also be used to drag the dockable.
 * The dockable can be dragged by dragging the label, icon or this whole component.
 * </p>
 * <p>
 * It contains:
 * <ul>
 * <li>a label with the title of the dockable that is docked in the dock. </li>
 * <li>the icon of the dockable that is docked in the dock.</li>
 * <li>the buttons with the actions of the dockable, if there are actions.</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class SingleDockHeader extends JPanel implements DockHeader {
//	TODO orientation
	// Static fields.

	protected static final int HEADER_HEIGHT = 30;
	protected static final int DIVIDER_WIDTH = 6;

	private static final String DOCKABLE_ICON_PROPERTY = "icon";
	private static final String DOCKABLE_TITLE_PROPERTY = "title";
	private static final String DOCKABLE_DESCRIPTION_PROPERTY = "description";


	// Fields.

	/**
	 * The only dockable of the dock.
	 */
	private Dockable dockable;
	/**
	 * The dockable of this dock will be dragged when the header is dragged.
	 */
	private LeafDock dock;
	/**
	 * The label that has the title of the dockable as text.
	 */
	private JLabel titleLabel;
	/**
	 * The drag listener that drags the dockable.
	 */
	private DragListener dragListener;
	/**
	 * The panel with the actions of the dockable.
	 */
	private JPanel actionPanel;
	/**
	 * The panel with the icon of the dockable.
	 */
	private JLabel iconLabel;
	/**
	 * The listener for changes of the dockable.
	 */
	private PropertyChangeListener dockableChangeListener;

	// Constructors.

	/**
	 * Constructs a single dock header.
	 *
	 * @throws IllegalArgumentException If the dock does not contain exactly one dockable.
	 * @param    dock        The dock. The dock should contain only one dockable.
	 * @param    position    The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public SingleDockHeader(LeafDock dock, int position) {

		// Check that this dock contains only one dockable. 
		if (dock.getDockableCount() != 1) {
			throw new IllegalArgumentException("The dock should contain one and nly one dockable.");
		}
		dockable = dock.getDockable(0);

		// Set the layout and sizes.
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, HEADER_HEIGHT));
		this.setMinimumSize(new Dimension(HEADER_HEIGHT, HEADER_HEIGHT));

		// Create the icon label.
		iconLabel = DockingManager.getComponentFactory().createJLabel();
		iconLabel.setIcon(dockable.getIcon());
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(iconLabel, BorderLayout.WEST);
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());

		// Create the title label.
		titleLabel = DockingManager.getComponentFactory().createJLabel();
		titleLabel.setText(dockable.getTitle());
		titleLabel.setHorizontalAlignment(JLabel.CENTER);

		// Create the center panel with the label.
		add(titleLabel);

		// Create the action panel.
		actionPanel = new JPanel();
		actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());
		add(actionPanel);
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));

		// Add the actions.
		Action[][] actionMatrix = dockable.getActions();
		if (actionMatrix != null) {
			for (int group = actionMatrix.length - 1; group >= 0; group--) {
				Action[] actionGroup = actionMatrix[group];
				if (actionGroup != null) {
					for (final Action anActionGroup : actionGroup) {
						actionPanel.add(DockingManager.getComponentFactory().createIconButton(anActionGroup));
					}
					if (group != 0) {
						actionPanel.add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH / 2, 0)));
					}
				}
			}
		}

		// Set a tooltip on the components.
		addTooltip();

		// Listen to changes of the dockable.
		dockableChangeListener = new DockableChangeListener();
		dockable.addPropertyChangeListener(dockableChangeListener);

	}

	// Implementations of DockHeader.

	public void dispose() {
		dockable.removePropertyChangeListener(dockableChangeListener);
	}

	public DragListener getDragListener() {
		return dragListener;
	}

	public void setDragListener(DragListener dragListener) {

		this.dragListener = dragListener;

		// Add the drag mouse listener as mouse listener and mouse motion listener to the panels.
		titleLabel.addMouseMotionListener(dragListener);
		titleLabel.addMouseListener(dragListener);
		iconLabel.addMouseMotionListener(dragListener);
		iconLabel.addMouseListener(dragListener);
		this.addMouseMotionListener(dragListener);
		this.addMouseListener(dragListener);

	}

	public LeafDock getDock() {
		return dock;
	}

	public int getPosition() {
		return Position.TOP;
	}

	public void setPosition(int orientation) {
	}

	private void addTooltip() {
		String description = dockable.getDescription();
		if (description != null) {
			this.setToolTipText(description);
			titleLabel.setToolTipText(description);
			iconLabel.setToolTipText(description);
			actionPanel.setToolTipText(description);
		}

	}

	private class DockableChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
			if (propertyChangeEvent.getPropertyName().equals(DOCKABLE_DESCRIPTION_PROPERTY) ||
					propertyChangeEvent.getPropertyName().equals(DOCKABLE_TITLE_PROPERTY) ||
					propertyChangeEvent.getPropertyName().equals(DOCKABLE_ICON_PROPERTY))
				if (iconLabel != null) {
					iconLabel.setIcon(dockable.getIcon());
				}
			if (titleLabel != null) {
				titleLabel.setText(dockable.getTitle());
			}
			addTooltip();
			revalidate();
			repaint();

		}

	}

}
