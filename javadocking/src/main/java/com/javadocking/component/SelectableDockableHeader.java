package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.action.ShowActionMode;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * <p>
 * A header for a dockable that can be selected.
 *
 * <p>
 * It contains:
 * <ul>
 * <li>a label with the title of the dockable.</li>
 * <li>the icon of the dockable, if there is one.</li>
 * <li>the buttons with the first array of actions of the dockable, if there are actions.</li>
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class SelectableDockableHeader extends JPanel implements SelectableHeader {

	// Static fields.

	/**
	 * The height of the header.
	 */
	private static final int HEADER_HEIGHT = 16;
	/**
	 * The maximum width of the header.
	 */
	private static final int MAXIMUM_WIDTH = Integer.MAX_VALUE;
	/**
	 * The width of the space between the header components.
	 */
	private static final int DIVIDER_WIDTH = 6;


	private static final String DOCKABLE_ICON_PROPERTY = "icon";
	private static final String DOCKABLE_TITLE_PROPERTY = "title";
	private static final String DOCKABLE_DESCRIPTION_PROPERTY = "description";

	// Fields.

	/**
	 * The dockable for which this component is the header.
	 */
	private Dockable dockable;
	/**
	 * True if the header is selected, false otherwise.
	 */
	private boolean selected;
	/**
	 * The label for the title of the dockable.
	 */
	private JLabel titleLabel;
	/**
	 * The label for the icon of the dockable.
	 */
	private JLabel iconLabel;
	/**
	 * The panel for the actions of the dockable, when the header is not selected.
	 */
	private JPanel actionPanel;
	/**
	 * The panel for the actions of the dockable, when the header is selected.
	 */
	private JPanel selectedActionPanel;
	/**
	 * The preferred size of this header.
	 */
	private Dimension preferredSize;
	/**
	 * The orientation of this handle.
	 */
	private int position = Position.TOP;
	/**
	 * Defines which actions are shown in the header, when the header is not selected.
	 */
	private ShowActionMode showActionMode;
	/**
	 * Defines which actions are shown in the header, when the header is selected.
	 */
	private ShowActionMode selectedShowActionMode;
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	/**
	 * The listener for changes of the dockable.
	 */
	private PropertyChangeListener dockableChangeListener;

	// TODO vertical orientation of the header.

	// Constructors.

	/**
	 * <p>
	 * Constructs a small header for a dockable that can be selected.
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
	 */
	public SelectableDockableHeader(Dockable dockable, int position) {
		this(dockable, position, ShowActionMode.FIRST_ROW_ACTIONS, ShowActionMode.ALL_ACTIONS);
	}

	/**
	 * <p>
	 * Constructs a header for a dockable that can be selected.
	 * </p>
	 * <p>
	 * The title of the dockable is set in the header. The icon is displayed.
	 * </p>
	 * <p>
	 * If there are actions, they are also displayed. The buttons for the actions are created
	 * with the method {@link com.javadocking.component.SwComponentFactory#createIconButton(Action)}.
	 * The component factory that is used is the component factory of the {@link DockingManager}.
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
	public SelectableDockableHeader(Dockable dockable, int position, ShowActionMode showActionMode, ShowActionMode selectedShowActionMode) {

		setOpaque(false);

		this.dockable = dockable;
		this.showActionMode = showActionMode;
		this.selectedShowActionMode = selectedShowActionMode;

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// Create the icon label.
		Icon icon = dockable.getIcon();
		if (icon != null) {
			iconLabel = DockingManager.getComponentFactory().createJLabel();
			iconLabel.setIcon(dockable.getIcon());
			add(iconLabel);
		}
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());

		// Create the center with the label.
		titleLabel = DockingManager.getComponentFactory().createJLabel();
		titleLabel.setText(dockable.getTitle());
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		add(titleLabel);

		// Create the action panels.
		actionPanel = createActionPanel(showActionMode);
		selectedActionPanel = createActionPanel(selectedShowActionMode);

		// Add the actions of the dockable to the actions panel.
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());
		addActions();

		// Set the sizes.
		setSizes();

		// Listen to changes of the dockable.
		dockableChangeListener = new DockableChangeListener();
		dockable.addPropertyChangeListener(dockableChangeListener);

	}

	// Implementations of SelectableHeader.

	public void dispose() {
		dockable.removePropertyChangeListener(dockableChangeListener);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {

		boolean oldValue = this.selected;
		if (this.selected != selected) {
			this.selected = selected;
			addActions();
		}

		revalidate();
		repaint();

		propertyChangeSupport.firePropertyChange("selected", oldValue, selected);

	}

	// Overwritten methods.

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyChangeSupport != null) {
			propertyChangeSupport.addPropertyChangeListener(listener);
		}
		super.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyChangeSupport != null) {
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		super.removePropertyChangeListener(listener);
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// Getters / Setters.

	/**
	 * Gets which actions are shown in the header, when the header is selected.
	 *
	 * @return Which actions are shown in the header, when the header is selected.
	 */
	public ShowActionMode getSelectedShowActionMode() {
		return selectedShowActionMode;
	}

	/**
	 * Sets which actions are shown in the header, when the header is selected.
	 *
	 * @param selectedShowActionMode Which actions are shown in the header, when the header is selected.
	 */
	public void setSelectedShowActionMode(ShowActionMode selectedShowActionMode) {
		this.selectedShowActionMode = selectedShowActionMode;
	}

	/**
	 * Gets which actions are shown in the header, when the header is not selected.
	 *
	 * @return Which actions are shown in the header, when the header is not selected.
	 */
	public ShowActionMode getShowActionMode() {
		return showActionMode;
	}

	/**
	 * Sets which actions are shown in the header, when the header is not selected.
	 *
	 * @param showActionMode Which actions are shown in the header, when the header is not selected.
	 */
	public void setShowActionMode(ShowActionMode showActionMode) {
		this.showActionMode = showActionMode;
	}

	/**
	 * Gets the panel with the actions.
	 *
	 * @return The panel with the actions. Can be null.
	 */
	protected JPanel getActionPanel() {
		return actionPanel;
	}

	/**
	 * Gets the panel with the actions when the header is selected.
	 *
	 * @return The panel with the actions when the header is selected. Can be null.
	 */
	public JPanel getSelectedActionPanel() {
		return selectedActionPanel;
	}

	/**
	 * Gets the label with the icon.
	 *
	 * @return The label with the icon. Can be null.
	 */
	protected JLabel getIconLabel() {
		return iconLabel;
	}

	/**
	 * Gets the label with the title.
	 *
	 * @return The label with the title. Can be null.
	 */
	protected JLabel getTitleLabel() {
		return titleLabel;
	}

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

	// Private metods.

	/**
	 * Removes the current actions from the header and adds the actions again.
	 */
	private void addActions() {

		// Remove the current actions.
		if (actionPanel != null) {
			remove(actionPanel);
		}
		if (selectedActionPanel != null) {
			remove(selectedActionPanel);
		}

		// Which actions have to be shown?
		if ((selected) && (selectedActionPanel != null)) {
			add(selectedActionPanel);
		} else if ((!selected) && (actionPanel != null)) {
			add(actionPanel);
		}

		// Set the sizes.
		setSizes();

	}

	/**
	 * Creates a panel with the actions for the dockable for the given action model.
	 *
	 * @param    actionMode                The mode which actions are shown.
	 */
	private JPanel createActionPanel(ShowActionMode actionMode) {

		// The panel with the actions.
		JPanel actionPanel = null;

		// Add the actions .
		Action[][] actionMatrix = dockable.getActions();
		if ((actionMatrix != null) && (actionMatrix.length > 0)) {
			if (actionMode == ShowActionMode.ALL_ACTIONS) {
				// Create the action panel.
				actionPanel = new JPanel();
				actionPanel.setOpaque(false);
				actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
				for (int group = actionMatrix.length - 1; group >= 0; group--) {
					Action[] actionGroup = actionMatrix[group];
					if (actionGroup != null) {
						for (int index = 0; index < actionGroup.length; index++) {
							actionPanel.add(DockingManager.getComponentFactory().createIconButton(actionGroup[index]));
						}
					}
				}

			} else if (actionMode == ShowActionMode.FIRST_ROW_ACTIONS) {
				Action[] actionGroup = actionMatrix[0];
				if ((actionGroup != null) && (actionGroup.length > 0)) {
					// Create the action panel.
					actionPanel = new JPanel();
					actionPanel.setOpaque(false);
					actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
					for (int index = 0; index < actionGroup.length; index++) {
						actionPanel.add(DockingManager.getComponentFactory().createIconButton(actionGroup[index]));
					}
				}
			}
		}

		return actionPanel;
	}

	/**
	 * Sets the minimum, maximum and preferred size of this component.
	 */
	private void setSizes() {

		int preferredWidth = calculatePreferredWidth();
		int maximumWidth = getHeaderMaximumWidth();
		this.setMaximumSize(new Dimension(maximumWidth, getHeaderHeight()));
		this.setMinimumSize(new Dimension(preferredWidth, getHeaderHeight()));
		preferredSize = new Dimension(preferredWidth, getHeaderHeight());

	}


	/**
	 * Calculates the preferred width of the components in the header.
	 *
	 * @return The maximum width of the components in the header.
	 */
	protected int calculatePreferredWidth() {

		int icon = 0;
		if (iconLabel != null) {
			icon = iconLabel.getPreferredSize().width;
		}
		int action = 0;
		if (actionPanel != null) {
			for (int index = 0; index < actionPanel.getComponentCount(); index++) {
				action += actionPanel.getComponent(index).getPreferredSize().width;
			}
		}
		int selectedAction = 0;
		if ((selected) && (selectedActionPanel != null)) {
			for (int index = 0; index < selectedActionPanel.getComponentCount(); index++) {
				selectedAction += selectedActionPanel.getComponent(index).getPreferredSize().width;
			}
		}

		if (action > selectedAction) {
			selectedAction = action;
		}

		return DIVIDER_WIDTH * 2 + icon + titleLabel.getPreferredSize().width + selectedAction;

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
			setSizes();
			revalidate();
			repaint();

		}

	}

}
