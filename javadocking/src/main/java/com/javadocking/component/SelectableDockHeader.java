package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.util.DockingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A header for a dock that can be selected.
 * </p>
 * <p>
 * It contains a label with the names of the dockables in the dock.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class SelectableDockHeader extends JPanel implements SelectableHeader {

	// Static fields.


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

	// Fields.

	/**
	 * True if the header is selected, false otherwise.
	 */
	private boolean selected;
	/**
	 * The label for the title of the dock.
	 */
	private JLabel titleLabel;
	/**
	 * The preferred size of this header.
	 */
	private Dimension preferredSize;
	/**
	 * The orientation of this handle.
	 */
	private int position = Position.TOP;
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


	// TODO vertical orientation of the header.

	// Constructors.

	/**
	 * <p>
	 * Constructs a small header for a dock that can be selected.
	 * </p>
	 * <p>
	 * The title of the dock is set in the header.
	 * </p>
	 *
	 * @param    dock            The dock of the header.
	 * @param    position        The position of the header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public SelectableDockHeader(Dock dock, int position) {

		setOpaque(false);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());

		// Get the dockables of the dock.
		List dockables = new ArrayList();
		DockingUtil.retrieveDockables(dock, dockables);
		StringBuilder title = new StringBuilder();
		if (dockables.size() > 0) {
			title = new StringBuilder(((Dockable) dockables.get(0)).getTitle());
		}
		for (int index = 1; index < dockables.size(); index++) {
			title.append(", ").append(((Dockable) dockables.get(index)).getTitle());
		}

		// Create the center with the label.
		titleLabel = DockingManager.getComponentFactory().createJLabel();
		titleLabel.setText(title.toString());
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		add(titleLabel);

		// Add the actions of the dockable to the actions panel.
		add(Box.createRigidArea(new Dimension(DIVIDER_WIDTH, 0)));
		add(Box.createHorizontalGlue());

		// Set the sizes.
		setSizes();

	}

	// Implementations of SelectableHeader.

	public void dispose() {
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

			// Set the sizes.
			setSizes();

		}

		revalidate();
		repaint();

		propertyChangeSupport.firePropertyChange("selected", oldValue, selected);

	}

	// Overwritten methods.

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		super.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
		super.removePropertyChangeListener(listener);
	}

	public Dimension getPreferredSize() {
		return preferredSize;
	}

	// Getters / Setters.

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

		return DIVIDER_WIDTH * 2 + titleLabel.getPreferredSize().width;

	}

}
