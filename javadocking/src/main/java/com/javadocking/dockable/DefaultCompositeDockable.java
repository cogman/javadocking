package com.javadocking.dockable;

import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * <p>
 * The default composite dockable implementation that keeps its dockables in an array.
 * </p>
 * <p>
 * Its content component, title, icon and actions are null.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultCompositeDockable implements CompositeDockable {

	// Static fields.

	/**
	 * This string separates the IDs of the child dockables in the ID of the composite dockable.
	 */
	private static final String ID_SEPARATOR = " ";

	// Fields.

	/**
	 * The array with the child dockables of this composite dockable object.
	 */
	private Dockable[] dockablesArray;
	/**
	 * The index of the dockable that is selected. If no dockable is selected, it is -1.
	 */
	private int selectedIndex;
	/**
	 * The mode how the dockable is docked in its current dock or how it was docked the last time it was
	 * in a dock.
	 */
	private int lastDockingMode = DockingMode.UNKNOWN;
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
	 * The leaf dock in which the dockable is docked.
	 */
	private LeafDock dock;
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	/**
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();


	// Constructors. 

	/**
	 * <p>
	 * Constructs a composite dockable with the given array of dockables.
	 * </p>
	 * <p>
	 * No dockable will be selected.
	 * </p>
	 *
	 * @param dockablesArray The array of dockables for the composite dockable.
	 */
	public DefaultCompositeDockable(Dockable[] dockablesArray) {
		this(dockablesArray, -1);
	}

	/**
	 * <p>
	 * Constructs a composite dockable with the given array of dockables.
	 * </p>
	 * <p>
	 * The dockable with the given index will be selected.
	 * </p>
	 *
	 * @param dockablesArray The array of dockables for the composite dockable.
	 * @param selectedIndex  The index of the selected dockable in the given array.
	 */
	public DefaultCompositeDockable(Dockable[] dockablesArray, int selectedIndex) {

		this.dockablesArray = dockablesArray;
		this.selectedIndex = selectedIndex;

		// Calculate the docking modes. It's a bitwise and of the docking modes of every chid dockable.
		if (dockablesArray.length > 0) {
			lastDockingMode = dockablesArray[0].getLastDockingMode();
		}

	}

	// Implementations of CompositeDockable.

	public Dockable getDockable(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return dockablesArray[index];

	}

	public int getDockableCount() {
		return dockablesArray.length;
	}

	public Dockable getSelectedDockable() {

		// Do we have a valid selected index?
		if ((selectedIndex < 0) || (selectedIndex >= getDockableCount())) {
			return null;
		}

		// Return the selected dockable of the list.
		return dockablesArray[selectedIndex];

	}

	// Implementations of Dockable.

	/**
	 * <p>
	 * Generates an ID with the IDs of the child dockables (separated by a space).
	 * </p>
	 */
	public String getID() {

		// Create the ID. It's the concatenation of the IDs of the children.
		StringBuilder compositeId = new StringBuilder();
		for (int index = 0; index < getDockableCount(); index++) {
			if (index != 0) {
				compositeId.append(ID_SEPARATOR);
			}
			String childId = getDockable(index).getID();
			if (compositeId != null) {
				compositeId.append(childId);
			}
		}
		return compositeId.toString();

	}

	/**
	 * Always returns null. This dockable does not have a content component. It contains multiple dockables. Each dockable has
	 * it's own content.
	 *
	 * @return Always null.
	 */
	public Component getContent() {
		return null;
	}

	public LeafDock getDock() {
		return dock;
	}

	public void setDock(LeafDock dock) {

		Dock oldValue = this.dock;
		this.dock = dock;
		propertyChangeSupport.firePropertyChange("dock", oldValue, dock);

	}

	/**
	 * Always returns null.
	 *
	 * @return Always null.
	 */
	public String getTitle() {
		return null;
	}

	/**
	 * Always returns null.
	 *
	 * @return Always null.
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Always returns null.
	 *
	 * @return Always null.
	 */
	public Icon getIcon() {
		return null;
	}

	/**
	 * <p>
	 * Calculates the docking modes with the values of the child dockables by using a bitwise and-operation.
	 * </p>
	 * <p>
	 * When there are no child dockables, DockingMode.ALL is returned.
	 * </p>
	 */
	public int getDockingModes() {

		// Calculate the docking modes. It's a bitwise and of the docking modes of every chid dockable.
		if (dockablesArray.length > 0) {
			int combinedDockingModes = dockablesArray[0].getDockingModes();
			for (final Dockable aDockablesArray : dockablesArray) {
				combinedDockingModes = combinedDockingModes & aDockablesArray.getDockingModes();
			}
			return combinedDockingModes;
		}

		return DockingMode.ALL;

	}

	/**
	 * <p>
	 * The default value is taken from the first child dockable.
	 * Typically, the last docking modes of all the child dockables are the same.
	 * </p>
	 */
	public int getLastDockingMode() {
		return lastDockingMode;
	}

	public void setLastDockingMode(int lastDockingMode) {

		int oldValue = this.lastDockingMode;
		this.lastDockingMode = lastDockingMode;
		propertyChangeSupport.firePropertyChange("lastDockingMode", oldValue, lastDockingMode);

	}

	/**
	 * Always returns true.
	 *
	 * @return Always true.
	 */
	public boolean isWithHeader() {
		return true;
	}

	/**
	 * Always returns null.
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

		// Fire the event for the child dockables.
		for (int index = 0; index < getDockableCount(); index++) {
			getDockable(index).fireDockingWillChange(dockableEvent);
		}

	}

	public void fireDockingChanged(DockableEvent dockableEvent) {
		dockingEventSupport.fireDockingChanged(dockableEvent);

		// Fire the event for the child dockables.
		for (int index = 0; index < getDockableCount(); index++) {
			getDockable(index).fireDockingChanged(dockableEvent);
		}


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
				if (visualizer != null) {
					throw new IllegalArgumentException("The visualizer should be a com.javadocking.dock.LeafDock when the dockable is in state DockableState.NORMAL, not [" + visualizer.getClass() + "].");
				}
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
	 * The default states are DockableState.CLOSED | DockableState.NORMAL | DockableState.MAXIMIZED | DockableState.MINIMIZED.
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

	// Getters / Setters.

	/**
	 * Gets the index of the selected dockable.
	 *
	 * @return The index of the selected dockable.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Sets the index of the selected dockable.
	 *
	 * @param selectedIndex The index of the selected dockable.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getDockableCount()).
	 */
	public void setSelectedIndex(int selectedIndex) throws IndexOutOfBoundsException {
		// Check if the index is in the bounds.
		if ((selectedIndex < 0) || (selectedIndex >= getDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + selectedIndex);
		}

		this.selectedIndex = selectedIndex;
	}

	// Overwritten methods.

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

	public int hashCode() {
		return getID().hashCode();
	}

	public String toString() {
		return "dockable [" + getID() + "]";
	}

}
