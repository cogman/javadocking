package com.javadocking.component;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dockable.*;
import com.javadocking.dockable.action.DefaultDockableStateAction;
import com.javadocking.drag.DragListener;
import com.javadocking.util.DockingUtil;
import com.javadocking.visualizer.Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * <p>
 * A header with an image for a minimized dockable.
 * </p>
 * <p>
 * This header should be put in a dockable.The dockable can be dragged by dragging the header.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class ImageMinimzeHeader extends JComponent implements DraggableContent, SelectableHeader {

	private static final String DOCKABLE_TITLE_PROPERTY = "title";
	private static final String DOCKABLE_DESCRIPTION_PROPERTY = "description";

	// Fields.

	/**
	 * The dockable which is represented by this minimize header.
	 */
	private Dockable dockable;
	/**
	 * The border for the header, when it is selected.
	 */
	private Border selectedBorder = BorderFactory.createLineBorder(Color.gray);
	/**
	 * The border for the header, when it not selected.
	 */
	private Border deselectedBorder = BorderFactory.createLineBorder(Color.gray);
	/**
	 * True if the header is selected, false otherwise.
	 */
	private boolean selected;
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	/**
	 * The minimum size of the header.
	 */
	private Dimension headerSize = new Dimension(80, 80);
	/**
	 * The width of the border.
	 */
	private int borderWidth = 1;
	/**
	 * The small image of the dockable component.
	 */
	private Image smallImage;
	/**
	 * The size of the small image of the dockable component.
	 */
	private Dimension smallImageSize;
	/**
	 * The position of this header.
	 */
	private int position = Position.TOP;
	/**
	 * The listener for changes of the dockable.
	 */
	private PropertyChangeListener dockableChangeListener;


	// Constructors.


	/**
	 * <p>
	 * Constructs a header for a dockable that is minimized.
	 * </p>
	 * <p>
	 * An image of the dockable component is shown.
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
	public ImageMinimzeHeader(Dockable dockable, int position) {

		// Set header size, test OK with border
		this.dockable = dockable;
		this.position = position;

		setOpaque(false);
		setBorder(deselectedBorder);
		addToolTip(dockable);
		addMouseListener(new SelectionListener());

		this.setMinimumSize(headerSize);
		this.setMaximumSize(headerSize);
		this.setPreferredSize(headerSize);

		// Listen to changes of the dockable.
		dockableChangeListener = new DockableChangeListener();
		dockable.addPropertyChangeListener(dockableChangeListener);

		try {
			// Create the image.
			Component component = dockable.getContent();
			if ((component.getSize().width <= 0) || (component.getSize().height <= 0)) {
				JWindow frame = new JWindow();
				frame.setLocation(-5000000, -5000000);
				component.setSize(new Dimension(component.getPreferredSize()));
				frame.setSize(component.getPreferredSize());
				frame.getContentPane().add(component);
				frame.setVisible(true);

				Dimension usableHeaderSize = new Dimension(headerSize.width - 2 * borderWidth, headerSize.height - 2 * borderWidth);
				float stretchHeight = component.getPreferredSize().height / (float) usableHeaderSize.height;
				float stretchWidth = component.getPreferredSize().width / (float) usableHeaderSize.width;
				float stretch = Math.min(stretchHeight, stretchWidth);
				smallImageSize = new Dimension((int) (component.getPreferredSize().width / stretch),
						(int) (component.getPreferredSize().height / stretch));
				BufferedImage dockableImage = new BufferedImage(component.getPreferredSize().width, component.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = dockableImage.getGraphics();
				component.paint(graphics);
				graphics.dispose();
				smallImage = dockableImage.getScaledInstance(smallImageSize.width, smallImageSize.height, BufferedImage.SCALE_SMOOTH);
				dockableImage.flush();
				frame.dispose();
			} else {
				Dimension usableHeaderSize = new Dimension(headerSize.width - 2 * borderWidth, headerSize.height - 2 * borderWidth);
				float stretchHeight = component.getSize().height / (float) usableHeaderSize.height;
				float stretchWidth = component.getSize().width / (float) usableHeaderSize.width;
				float stretch = Math.min(stretchHeight, stretchWidth);
				smallImageSize = new Dimension((int) (component.getWidth() / stretch), (int) (component.getHeight() / stretch));

				BufferedImage dockableImage = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = dockableImage.getGraphics();
				component.paint(graphics);
				graphics.dispose();
				smallImage = dockableImage.getScaledInstance(smallImageSize.width, smallImageSize.height, BufferedImage.SCALE_SMOOTH);
				dockableImage.flush();
			}
		} catch (Exception exception) {
			smallImage = null;
			System.out.println("Exception occured while creating the image minimize header.");
			exception.printStackTrace();
		}

	}


	// Implementations of DraggableContent.

	public void dispose() {
		dockable.removePropertyChangeListener(dockableChangeListener);
	}

	public void addDragListener(DragListener dragListener) {

		addMouseListener(dragListener);
		addMouseMotionListener(dragListener);

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		boolean oldValue = this.selected;

		if (oldValue != selected) {
			if (selected) {
				setBorder(selectedBorder);
			} else {
				setBorder(deselectedBorder);
			}
		}

		revalidate();
		repaint();

		propertyChangeSupport.firePropertyChange("selected", oldValue, selected);
	}

	/**
	 * gets the minimum size of the header.
	 *
	 * @return The minimum size of the header.
	 */
	public Dimension getHeaderSize() {
		return headerSize;
	}

	/**
	 * Sets the minimum size of the header.
	 *
	 * @param headerSize The minimum size of the header.
	 */
	public void setHeaderSize(Dimension headerSize) {
		this.headerSize = headerSize;
	}

	/**
	 * Gets the width of the border.
	 *
	 * @return The width of the border.
	 */
	public int getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Sets the width of the border.
	 *
	 * @param borderWidth The width of the border.
	 */
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	/**
	 * Gets the position of this header.
	 *
	 * @return The position of this header.
	 */
	public int getPosition() {
		return position;
	}


	// Overwritten methods.

	/**
	 * Setsthe position of this header.
	 *
	 * @param    position        The position of this header.
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	public Dimension getPreferredSize() {
		return headerSize;
	}

	public Dimension getMaximumSize() {
		return headerSize;
	}


	public Dimension getMinimumSize() {
		return headerSize;
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

	protected void paintChildren(Graphics graphics) {

		if (smallImage != null) {
			((Graphics2D) graphics).drawImage(smallImage, borderWidth, borderWidth, getSize().width - borderWidth, getSize().height - borderWidth,
					0, 0, getSize().width - borderWidth * 2, getSize().height - borderWidth * 2, null);
		}
		super.paintChildren(graphics);
	}

	// Private metods.

	private void addToolTip(Dockable dockable) {
		// Set a tooltip on the components.
		String description = dockable.getDescription();
		if (description != null) {
			this.setToolTipText(description);
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

	private class DockableChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
			if (propertyChangeEvent.getPropertyName().equals(DOCKABLE_DESCRIPTION_PROPERTY) ||
					propertyChangeEvent.getPropertyName().equals(DOCKABLE_TITLE_PROPERTY)) {
				addToolTip(dockable);
				revalidate();
				repaint();
			}

		}

	}
}
