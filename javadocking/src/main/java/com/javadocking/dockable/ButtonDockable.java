package com.javadocking.dockable;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.drag.DragListener;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * <p>
 * This is an implementation for a dockable that serves as wrapper around components like buttons.
 * This gives these components the ability to be moved to different tool bars.
 * </p>
 * <p>
 * By default it can be docked only in tool bars and tool grids.
 * </p>
 * <p>
 * The content button may not be null.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class ButtonDockable implements Dockable {

	// Fields.

	/**
	 * The ID of the dockable.
	 */
	private String id;
	/**
	 * The content of the dockable.
	 */
	private Component content;
	/**
	 * The possible modes how the dockable can be docked.
	 */
	private int dockingModes;
	/**
	 * The mode how the dockable is docked in its current dock or how it was docked the last time it was
	 * in a dock.
	 */
	private int lastDockingMode = DockingMode.UNKNOWN;
	/**
	 * The leaf dock in which the dockable is docked.
	 */
	private LeafDock dock;
	/**
	 * The possible states of the dockable.
	 */
	private int possibleStates = DockableState.CLOSED | DockableState.NORMAL | DockableState.MAXIMIZED | DockableState.MINIMIZED;
	/**
	 * The current state of the dockable.
	 */
	private int state = DockableState.CLOSED;
	/**
	 * The object that currently shows the content of the dockable.
	 * Can be null, i.e. when the state of the dockable is {@link DockableState#CLOSED}.
	 */
	private Object visualizer;
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	/**
	 * The support for handling the dockable events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();


	// Constructors.

	/**
	 * Constructs a new dockable with the specified ID and content.
	 * The possible docking modes are {@link DockingMode#TOOL_BAR} and {@link DockingMode#FLOAT}.
	 *
	 * @param id      The ID of the dockable. The IDs of all dockables should be different.
	 * @param content The content of the dockable.
	 * @throws IllegalArgumentException If the given ID is null.
	 */
	public ButtonDockable(String id, Component content) {
		this(id, content, DockingMode.TOOL_BAR | DockingMode.FLOAT);
	}

	/**
	 * Constructs a new dockable with the specified id, content and docking modes.
	 *
	 * @param id           The ID of the dockable. The IDs of all dockables should be different.
	 * @param content      The content of the dockable.
	 * @param dockingModes The possible docking modes of the dockable. This integer should be combination of constants
	 *                     defined by {@link DockingMode}.
	 * @throws IllegalArgumentException If the given ID is null.
	 */
	public ButtonDockable(String id, Component content, int dockingModes) {

		// Set the ID.
		if (id == null) {
			throw new IllegalArgumentException("Dockable with ID null.");
		}
		this.id = id;

		// Set the content.
		if (content == null) {
			throw new IllegalArgumentException("The content of a dockable may not be null.");
		}
		this.content = content;

		// Is it possible to drag the dockable by dragging the content?
		if (content instanceof DraggableContent) {
			addDraggerInDockable((DraggableContent) content);
		}

		this.dockingModes = dockingModes;

	}

	// Implementations of Dockable.

	public String getID() {
		return id;
	}

	public LeafDock getDock() {
		return dock;
	}

	public void setDock(LeafDock dock) {

		Dock oldValue = this.dock;
		this.dock = dock;
		propertyChangeSupport.firePropertyChange("dock", oldValue, dock);

	}

	public Component getContent() {
		return content;
	}

	/**
	 * Always returns null
	 *
	 * @return Always null.
	 */
	public String getTitle() {
		return null;
	}

	/**
	 * Always returns null
	 *
	 * @return Always null.
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Always returns null
	 *
	 * @return Always null.
	 */
	public Icon getIcon() {
		return null;
	}

	public int getDockingModes() {
		return dockingModes;
	}

	public int getLastDockingMode() {
		return lastDockingMode;
	}

	public void setLastDockingMode(int dockPosition) {
		this.lastDockingMode = dockPosition;
	}

	/**
	 * Always returns false.
	 *
	 * @return Always false.
	 */
	public boolean isWithHeader() {
		return false;
	}

	/**
	 * <p>
	 * Tries to set the new state of the dockable.
	 * </p>
	 * <p>
	 * When the given state is {@link DockableState#CLOSED}, the <code>dock</code> of this dockable will be set to null.
	 * </p>
	 * <p>
	 * When the given state is {@link DockableState#NORMAL}, the visualizer should be the {@link LeafDock} where the
	 * dockable is docked in. The <code>dock</code> of this dockable will be set.
	 * </p>
	 * <p>
	 * For other states the visualizer can have another type, but these types are not obligatory. The visualizer can even be null.
	 * </p>
	 */
	public void setState(int state, Object visualizer) {

		if ((state & getPossibleStates()) == 0) {
			throw new IllegalArgumentException("Illegal state for dockable [" + state + "].");
		}
		this.state = state;

		if (state == DockableState.CLOSED) {
			setDock(null);
		} else if (state == DockableState.NORMAL) {
			if (!(visualizer instanceof LeafDock)) {
				if (visualizer == null) {
					throw new NullPointerException("Visualizer is null.");
				}
				throw new IllegalArgumentException("The visualizer should be a com.javadocking.dock.LeafDock when the dockable is in state DockableState.NORMAL, not [" + visualizer.getClass() + "].");
			}
			setDock((LeafDock) visualizer);
		} else {
			this.visualizer = visualizer;
		}

	}

	public Object getVisualizer() {

		if (state == DockableState.NORMAL) {
			return dock;
		}
		if (state == DockableState.CLOSED) {
			return null;
		}
		return visualizer;

	}

	public int getState() {

		return state;

	}

	/**
	 * <p>
	 * Gets the possible states of the dockable. This can be a combination of constants defined by {@link DockableState}.
	 * A combination is made by the bitwise or-operation on the integer constants.
	 *
	 * <p>
	 * The default states are DockableState.CLOSED | DockableState.NORMAL.
	 * <p>
	 *
	 * @return The possible states of the dockable.
	 */
	public int getPossibleStates() {
		return possibleStates;
	}

	/**
	 * Sets the possible states of the dockable.
	 *
	 * @param    possibleStates                    The possible states of the dockable.
	 * @see #getPossibleStates()
	 */
	public void setPossibleStates(int possibleStates) {
		this.possibleStates = possibleStates;
	}

	/**
	 * Returns null.
	 *
	 * @return Always null.
	 */
	public Action[][] getActions() {
		return null;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	public void fireDockingWillChange(DockableEvent dockableEvent) {
		dockingEventSupport.fireDockingWillChange(dockableEvent);
	}

	// Overwritten methods.

	public void fireDockingChanged(DockableEvent dockableEvent) {
		dockingEventSupport.fireDockingChanged(dockableEvent);
	}

	/**
	 * Returns true if the given object is a {@link Dockable} with the same ID
	 * as this dockable.
	 *
	 * @param    object
	 * @return True if the given object is a {@link Dockable} with the same ID
	 * as this dockable, false otherwise.
	 */
	public boolean equals(Object object) {

		if (!(object instanceof Dockable)) {
			return false;
		}

		Dockable other = (Dockable) object;
		return this.getID().equals(other.getID());

	}

	// Private metods.

	public int hashCode() {
		return getID().hashCode();
	}

	// Getters / Setters.

	/**
	 * Adds a drag listener on the content component of the dockable.
	 * With this listener the dockable can also be dragged by dragging on the content.
	 *
	 * @param draggableComponent The drag listener is set on this panel as mouse listener and mouse motion listener.
	 */
	private void addDraggerInDockable(DraggableContent draggableComponent) {

		// Create a drag listener.
		DragListener dragListener = DockingManager.getDockableDragListenerFactory().createDragListener(this);

		// Add the listener to the content.
		draggableComponent.addDragListener(dragListener);

	}

}
