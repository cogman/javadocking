package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.component.DockHeader;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.DockingMode;
import com.javadocking.drag.DragListener;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;
import com.javadocking.visualizer.ExternalizeDock;
import com.javadocking.visualizer.Externalizer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * This is a dock that contains zero or one dockables.
 * </p>
 * <p>
 * Information on using single docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html#SingleDock" target="_blank">How to Use Laef Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * It is a leaf dock. It cannot contain other docks.
 * </p>
 * <p>
 * When it contains one dockable, it is full. When it contains no dockable, it is empty.
 * </p>
 * <p>
 * A dockable can be docked in this dock if:
 * <ul>
 * <li>it has {@link DockingMode#SINGLE} as possible docking mode.</li>
 * <li>its content component is not null.</li>
 * <li>this dock is not already full.</li>
 * </ul>
 * A composite dockable can also be docked to this dock if:
 * <ul>
 * <li>it has exactly one child dockable.</li>
 * <li>its child dockable has {@link DockingMode#SINGLE} as possible docking mode.</li>
 * <li>its child dockable has a content component that is not null.</li>
 * <li>this dock is not already full.</li>
 * </ul>
 *
 * <p>
 * If the mouse is inside the priority rectangle, the dockable can be docked with priority (see {@link Priority#CAN_DOCK_WITH_PRIORITY}).
 * When the mouse is inside the panel of this dock, but outside the priority rectangle,
 * the dockable can be docked without priority (see {@link Priority#CAN_DOCK}).
 * The priority rectangle is a rectangle in the middle of the dock and retrieved with {@link #getPriorityRectangle(Rectangle)}.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class SingleDock extends JPanel implements LeafDock, DockableHider, ExternalizeDock {

	// Static fields.

	/**
	 * The only position for a dockable docked in his dock.
	 */
	public static final Position SINGLE_POSITION = new Position();

	/**
	 * The name of the <code>dockableIds</code> property.
	 */
	private static final String PROPERTY_DOCKABLE_IDS = "dockableIds";

	// Fields.

	/**
	 * The only dockable that is docked in this dock.
	 */
	private Dockable dockable;
	/**
	 * True if the dockable is externalized in this dock.
	 */
	private boolean externalized = false;
	/**
	 * The externalizer, when this dock contains an externalized dockable.
	 */
	private Externalizer externalizer;
	/**
	 * The parent dock of this dock.
	 */
	private CompositeDock parentDock;
	/**
	 * With this handle the dockable of this dock can be dragged.
	 */
	private DockHeader header;
	/**
	 * The position where the header will be placed.
	 * It should be java.awt.BorderLayout.NORTH, java.awt.BorderLayout.SOUTH,
	 * java.awt.BorderLayout.EAST or java.awt.BorderLayout.WEST.
	 * The default value is java.awt.BorderLayout.NORTH.
	 */
	private int headerPosition = Position.TOP;

	// For help.
	/**
	 * This is the rectangle in which a dockable can be docked with priority.
	 * it is computed with the values of priorityRectangleTopOffset, priorityRectangleLeftOffset
	 * priorityRectangleBottomOffset and priorityRectangleRightOffset. We keep it as field
	 * because we don't want to create every time a new rectangle.
	 */
	private Rectangle priorityRectangle = new Rectangle();

	// For event handling.
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();

	// For ghosts.
	/**
	 * The header that can have a listener that is still working.
	 */
	private DockHeader ghostHeader;

	// For hiding.
	private boolean hidden;

	// Constructors.

	/**
	 * Constructs a single dock.
	 */
	public SingleDock() {
		// Set the layout.
		super(new BorderLayout());

	}

	// Implementations of Dock.

	/**
	 * Determines if the dockable can be added. It can be added if:
	 * <ul>
	 * <li>it has {@link DockingMode#SINGLE} as possible docking mode.</li>
	 * <li>its content component is not null.</li>
	 * <li>this dock is not already full.</li>
	 * </ul>
	 * A composite dockable can also be docked to this dock if:
	 * <ul>
	 * <li>it has exactly one child dockable.</li>
	 * <li>its child dockable has {@link DockingMode#SINGLE} as possible docking mode.</li>
	 * <li>its child dockable has a content component that is not null.</li>
	 * <li>this dock is not already full.</li>
	 * </ul>
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		// Check if the dockable may be docked in a single dock.
		if ((dockable.getDockingModes() & DockingMode.SINGLE) == 0) {
			return Priority.CANNOT_DOCK;
		}

		// Retrieve the only dockable in the tree of dockables.
		Dockable singleDockable = getDeepestSingleChild(dockable);
		if (singleDockable == null) {
			// No we can't add the dockable.
			return Priority.CANNOT_DOCK;
		}

		// The dockable can be docked if its component is not null and if the dock is not full.
		if ((singleDockable.getContent() != null) && (!isFull())) {
			// Can we dock with priority?
			getPriorityRectangle(priorityRectangle);
			if (priorityRectangle.contains(relativeLocation)) {
				return Priority.CAN_DOCK_WITH_PRIORITY;
			}

			// We can dock, but not with priority.
			return Priority.CAN_DOCK;
		}

		// No we can't add the dockable.
		return Priority.CANNOT_DOCK;
	}

	/**
	 * Sets the rectangle on the whole dock, if the dockable can be docked.
	 */
	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation, Point dockableOffset, Rectangle rectangle) {
		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {
			// The docking rectangle is the rectangle defined by this dock panel.
			rectangle.setBounds(0, 0, getSize().width, getSize().height);
		}

		return priority;
	}

	public boolean addDockable(Dockable dockableToAdd, Point relativeLocation, Point dockableOffset) {

		// Verify the conditions for adding the dockable.
		if (getDockPriority(dockableToAdd, relativeLocation) == Priority.CANNOT_DOCK) {
			// We are not allowed to dock the dockable in this dock.
			return false;
		}

		// Retrieve the only dockable in the tree of dockables.
		Dockable singleDockable = getDeepestSingleChild(dockableToAdd);
		if (singleDockable != null) {
			// Add the dockable.
			addDockable(singleDockable, SINGLE_POSITION);
			dockableToAdd.setState(DockableState.NORMAL, this);

			// The add was successful.
			return true;

		}

		return false;

	}

	public boolean canRemoveDockable(Dockable dockableToRemove) {
		return dockableToRemove.equals(dockable);
	}

	public boolean removeDockable(Dockable dockableToRemove) {

		// Verify the conditions for removing the dockable.
		if (!canRemoveDockable(dockableToRemove)) {
			return false;
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, this, null, dockableToRemove));

		// Remove the dockable.
		if (hidden) {
			hidden = false;
		} else {
			remove(dockable.getContent());
		}
		dockable.setState(DockableState.CLOSED, null);
		dockable = null;

		// Remove the header.
		if (header != null) {
			if (ghostHeader == null) {
				ghostHeader = header;
				((Component) header).setVisible(false);
			} else {
				remove((Component) header);
			}
			header = null;
		}

		// Inform the listeners about the removal.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, this, null, dockableToRemove));

		// Repaint.
		SwingUtil.repaintParent(this);

		return true;

	}

	public boolean isEmpty() {
		return dockable == null;
	}

	public boolean isFull() {
		return dockable != null;
	}

	public CompositeDock getParentDock() {
		return parentDock;
	}

	public void setParentDock(CompositeDock parentDock) {
		this.parentDock = parentDock;
	}

	public void saveProperties(String prefix, Properties properties, Map childDocks) {
		if (dockable != null) {
			// Save the ID of the dockable.
			PropertiesUtil.setString(properties, prefix + PROPERTY_DOCKABLE_IDS, dockable.getID());
			PropertiesUtil.setInteger(properties, prefix + "headerPosition", headerPosition);
		}
		PropertiesUtil.setBoolean(properties, prefix + "externalized", externalized);
	}

	public void loadProperties(String prefix, Properties properties, Map childDocks, Map dockablesMap, Window owner) throws IOException {

		// Get the externalization.
		boolean externalized = false;
		externalized = PropertiesUtil.getBoolean(properties, prefix + "externalized", externalized);

		// Get the ID of the dockable.
		String dockableId = null;
		dockableId = PropertiesUtil.getString(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableId);
		if (dockableId != null) {
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(dockableId);
			if (dockableObject != null) {
				if (dockableObject instanceof Dockable) {
					Dockable dockable = (Dockable) dockableObject;

					if (externalized) {
						externalizeDockable(dockable);
					} else {
						// Try to add the dockable.
						addDockable(dockable, SINGLE_POSITION);
					}
				} else {
					throw new IOException("The values in the dockables mapping should be of type com.javadocking.Dockable.");
				}
			}

		}

		// Get the position of the header.
		int headerPosition = Position.TOP;
		headerPosition = PropertiesUtil.getInteger(properties, prefix + "headerPosition", headerPosition);
		setHeaderPosition(headerPosition);

	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	// Implementations of LeafDock.

	public Dockable getDockable(int index) throws IndexOutOfBoundsException {
		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return dockable;
	}

	public int getDockableCount() {
		if (dockable != null) {
			return 1;
		} else {
			return 0;
		}
	}

	public boolean containsDockable(Dockable otherDockable) {
		return otherDockable.equals(dockable);
	}

	public boolean moveDockable(Dockable dockableToMove, Point relativeLocation) {

		// Check if the dockable is docked in this dock.
		if (!dockableToMove.equals(dockable)) {
			throw new IllegalArgumentException("The dockable should be docked in this dock.");
		}

		// Do nothing, there is only one position in this dock.
		// We didn't change the position.
		return false;

	}

	public Position getDockablePosition(Dockable childDockable) throws IllegalArgumentException {

		if (childDockable.equals(dockable)) {
			return SINGLE_POSITION;
		}

		throw new IllegalArgumentException("The dockable is not docked in this dock.");

	}

	public void addDockable(Dockable dockableToAdd, Position position) {

		// TODO composite dockable with one component?
		// Are we empty?
		if (!isEmpty()) {
			throw new IllegalStateException("The dock is full.");
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, null, this, dockableToAdd));

		// Add the new dockable in the center.
		dockable = dockableToAdd;
		add(dockableToAdd.getContent(), BorderLayout.CENTER);
		dockable.setState(DockableState.NORMAL, this);
		dockable.setLastDockingMode(DockingMode.SINGLE);
		hidden = false;

		// Make the header visible if necessary.
		if (dockable.isWithHeader()) {
			// Create the dragger for the dockable of this dock.
			DragListener dragListener = DockingManager.getDockDragListenerFactory().createDragListener(this);

			// Create the header and set the dragger on the handle.
			header = DockingManager.getComponentFactory().createSingleDockHeader(this, Position.TOP);
			header.setDragListener(dragListener);
			add(((Component) header), getBorderLayoutString(headerPosition));
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockableToAdd));

		// Repaint.
		SwingUtil.repaintParent(this);

	}

	// Implementations of DockableHider.

	public void hideDockable(Dockable dockableToHide) throws IllegalArgumentException {

		// Check if the dockable is docked in this dock.
		if (!dockableToHide.equals(dockable)) {
			throw new IllegalArgumentException("The dockable is not docked in this dock.");
		}

		// Check if the dockable is not already hidden.
		if (hidden) {
			throw new IllegalStateException("The dockable is already hidden.");
		}

		// Hide the dockable.
		hidden = true;
		remove(dockable.getContent());

		// Repaint.
		revalidate();
		repaint();

	}

	public int getHiddenDockableCount() {

		if (hidden) {
			return 1;
		}

		return 0;
	}

	public Dockable getHiddenDockable(int index) {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getHiddenDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		if (hidden) {
			return dockable;
		} else {
			return null;
		}

	}

	public void restoreDockable(Dockable dockableToRestore) {

		// Check if the dockable is docked in this dock.
		if (!dockableToRestore.equals(dockable)) {
			throw new IllegalArgumentException("The dockable is not in this dock.");
		}

		// Check if the dockable is hidden.
		if (!hidden) {
			throw new IllegalStateException("No dockable is hidden.");
		}

		// Restore the hidden dockable.
		add(dockable.getContent(), BorderLayout.CENTER);
		hidden = false;

	}

	// Implementations of Externalizer.

	public void externalizeDockable(Dockable dockableToExternalize) {

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new DockableEvent(this, null, this, dockableToExternalize));

		// Add the new dockable in the center.
		dockable = dockableToExternalize;
		add(dockableToExternalize.getContent(), BorderLayout.CENTER);
		dockable.setDock(this);

		externalized = true;

		// Needed during load otherwise the header has the wrong actions
		if (dockable.getState() != DockableState.EXTERNALIZED) {
			dockable.setState(DockableState.EXTERNALIZED, getExternalizer());
		}

		// Make the header visible if necessary.
		if (dockable.isWithHeader()) {
			// Create the dragger for the dockable of this dock.
			DragListener dragListener = DockingManager.getDockDragListenerFactory().createDragListener(this);

			// Create the header and set the dragger on the handle.
			header = DockingManager.getComponentFactory().createSingleDockHeader(this, Position.TOP);
			header.setDragListener(dragListener);
			add(((Component) header), getBorderLayoutString(headerPosition));
		}

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new DockableEvent(this, null, this, dockableToExternalize));

		// Repaint.
		SwingUtil.repaintParent(this);


	}

	public Externalizer getExternalizer() {

		if (isExternalized()) {
			return externalizer;
		}
		return null;
	}

	public void setExternalizer(Externalizer externalizer) {
		this.externalizer = externalizer;
	}

	public boolean isExternalized() {
		return externalized;
	}

	// Public methods.

	/**
	 * Clears the ghost child docks from this dock. The ghost child docks are removed.
	 */
	public void clearGhosts() {

		if (ghostHeader != null) {
			ghostHeader = null;
		}

	}


	// Protected methods.

	/**
	 * <p>
	 * Computes the relative rectangle in this dock in which docking has priority.
	 * This rectangle is in the middle of the dock.
	 * </p>
	 *
	 * @param    rectangle The rectangle that will get the position and size of the priority rectangle for this dock.
	 */
	protected void getPriorityRectangle(Rectangle rectangle) {
		Dimension size = getSize();
		/**
		 * The relative right offset of the priority rectangle.
		 */
		final double priorityRectangleRelativeRightOffset = 2.0 / 8.0; /**
		 * The relative bottom offset of the priority rectangle.
		 */final double priorityRectangleRelativeBottomOffset = 2.0 / 8.0; /**
		 * The relative left offset of the priority rectangle.
		 */final double priorityRectangleRelativeLeftOffset = 2.0 / 8.0; /**
		 * The relative top offset of the priority rectangle.
		 */final double priorityRectangleRelativeTopOffset = 2.0 / 8.0;
		rectangle.setBounds((int) (size.width * priorityRectangleRelativeLeftOffset),
				(int) (size.height * priorityRectangleRelativeTopOffset),
				(int) (size.width * (1 - priorityRectangleRelativeLeftOffset - priorityRectangleRelativeRightOffset)),
				(int) (size.height * (1 - priorityRectangleRelativeTopOffset - priorityRectangleRelativeBottomOffset)));
	}

	// Getters / setters.

	/**
	 * <p>
	 * Gets the position where the header of the dock is placed.
	 * </p>
	 * <p>
	 * It should be one of the following values:
	 * <ul>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * </ul>
	 *
	 * <p>
	 * The default value is {@link Position#TOP}.
	 * </p>
	 *
	 * @return The position where the header is placed.
	 */
	public int getHeaderPosition() {
		return headerPosition;
	}

	/**
	 * <p>
	 * Sets the position where the header is placed.
	 * </p>
	 * <p>
	 * It should be one of the following values:
	 * <ul>
	 * <li>{@link Position#TOP}</li>
	 * <li>{@link Position#BOTTOM}</li>
	 * <li>{@link Position#LEFT}</li>
	 * <li>{@link Position#RIGHT}</li>
	 * </ul>
	 *
	 * @param headerPosition The position where the header is placed.
	 * @throws IllegalArgumentException If the given postion is not Position.TOP, Position.BOTTOM, Position.LEFT or Position.RIGHT.
	 */
	public void setHeaderPosition(int headerPosition) {

		this.headerPosition = headerPosition;

		if (header != null) {
			remove((Component) header);
			String borderLayoutString = getBorderLayoutString(headerPosition);
			add(((Component) header), borderLayoutString);
		}

	}

	// Private methods.

	/**
	 * <p>
	 * Searches the only dockable in a tree defined by a {@link CompositeDockable}.
	 * </p>
	 * <p>
	 * If there are multiple dockables in the tree, null is returned.
	 * If the given dockable is not a composite, the dockable itself is returned.
	 * </p>
	 *
	 * @return The only dockable in a tree of dockables, if it exists. Otherwise null.
	 */
	private Dockable getDeepestSingleChild(Dockable dockable) {

		if (dockable instanceof CompositeDockable) {
			CompositeDockable compositeDockable = (CompositeDockable) dockable;
			if (compositeDockable.getDockableCount() == 1) {
				return getDeepestSingleChild(compositeDockable.getDockable(0));
			} else {
				return null;
			}
		}

		return dockable;

	}

	/**
	 * Gets the correspondent java.awt.BorderLayout string for the given position.
	 *
	 * @param position May be Position.TOP, Position.BOTTOM, Position.RIGHT, Position.LEFT.
	 * @return The correspondent java.awt.BorderLayout string
	 * for the given position.
	 */
	private String getBorderLayoutString(int position) {

		switch (headerPosition) {
			case Position.TOP:
				return BorderLayout.NORTH;
			case Position.BOTTOM:
				return BorderLayout.SOUTH;
			case Position.RIGHT:
				return BorderLayout.EAST;
			case Position.LEFT:
				return BorderLayout.WEST;
			default:
				throw new IllegalArgumentException("Illegal header position.");
		}

	}
}
