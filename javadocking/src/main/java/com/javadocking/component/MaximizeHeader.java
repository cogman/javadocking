package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.util.DockingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <p>
 * The default header for a maximized dockable. It displays information about the dockable.
 * </p>
 * <p>
 * It contains:
 * <ul>
 * <li>a label with the title of the dockable that is docked in the dock.</li>
 * <li>the icon of the dockable that is docked in the dock.</li>
 * <li>the buttons with the actions of the dockable, if there are actions.</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class MaximizeHeader extends JPanel implements Header {
//	TODO orientation
	// Static fields.

	protected static final int HEADER_HEIGHT = 30;
	protected static final int DIVIDER_WIDTH = 6;

	private static final String DOCKABLE_ICON_PROPERTY = "icon";
	private static final String DOCKABLE_TITLE_PROPERTY = "title";
	private static final String DOCKABLE_DESCRIPTION_PROPERTY = "description";

	// Fields.

	/**
	 * The dockable for which this component is the header.
	 */
	private Dockable dockable;
	/**
	 * The label that has the title of the dockable as text.
	 */
	private JLabel titleLabel;
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
	 * Constructs a header for a maximized dockable.
	 *
	 * @throws IllegalArgumentException If the dock does not contain exactly one dockable.
	 * @param    dockable    The dock. The dock should contain only one dockable.
	 * @param    position    The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public MaximizeHeader(Dockable dockable, int position) {

		this.dockable = dockable;

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

		// We want all the actions.
		Dockable dockableFromModel = DockingUtil.retrieveDockableOfDockModel(dockable.getID());
		if (dockableFromModel == null) {
			dockableFromModel = dockable;
		}

		// Add the actions.
		Action[][] actionMatrix = dockableFromModel.getActions();
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

	public void dispose() {
		dockable.removePropertyChangeListener(dockableChangeListener);
	}

	public int getPosition() {
		return Position.TOP;
	}

	public void setPosition(int orientation) {
	}

	private void addTooltip() {
		// Set a tooltip on the components.
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
