package com.javadocking.dockable;

import com.javadocking.dock.LeafDock;
import com.javadocking.event.DockableEvent;
import com.javadocking.event.DockingListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;


/**
 * <p>
 * A decorator for a delegate {@link com.javadocking.dockable.Dockable}.
 * This decorator adds actions to the delegate.
 * </p>
 * <p>
 * Information on using dockables with actions is in
 * <a href="http://www.javadocking.com/developerguide/dockable.html#ActionDockable" target="_blank">How to Use Dockables</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * When the method {@link #getActions()} is called, the actions of this decorator
 * are added at the end of the matrix of actions retrieved by executing this method
 * on the delegate.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class ActionDockable implements Dockable {

	// Fields.

	/**
	 * The delegate dockable.
	 */
	private Dockable delegate;
	/**
	 * The actions that will be added to the actions of the delegate dockable.
	 */
	private Action[][] actionsToAdd;

	// Constructors.

	/**
	 * Constructs a decorator for the given dockable. The action matrix for this decorator
	 * is a matrix with 0 rows.
	 *
	 * @param    delegate        The delegate dockable.
	 */
	public ActionDockable(Dockable delegate) {

		this(delegate, new Action[0][]);

	}

	/**
	 * Constructs a decorator for the given dockable.
	 *
	 * @param    delegate        The delegate dockable.
	 * @param    actionsToAdd    The actions that will be added to the matrix of actions of the delegate.
	 * @see    #getActions()
	 */
	public ActionDockable(Dockable delegate, Action[][] actionsToAdd) {

		this.delegate = delegate;
		this.actionsToAdd = actionsToAdd;

	}

	// Implementations of Dockable.

	/**
	 * <p>
	 * Creates a matrix of actions with the actions retrieved from the delegate
	 * and the actions of this decorator.
	 * </p>
	 * <p>
	 * The rows of actions of this decorator will be the rows at the end of the action matrix.
	 * </p>
	 * <p>
	 * If the delegate returns null, then the matrix of actions of this decorator is returned.
	 * </p>
	 *
	 * @return The matrix of actions created with the actions retrieved from the delegate
	 * and the actions of this decorator.
	 */
	public Action[][] getActions() {

		// Retrieve the actions of the delegate.
		Action[][] delegateActions = delegate.getActions();

		// Get the current actions to add.
		Action[][] currentActionsToAdd = getActionsToAdd();

		// Are the actions null.
		if (delegateActions == null) {
			return currentActionsToAdd;
		}

		// Create a new matrix with the rows of the action matrix of the delegate 
		// and the rows of the action matrix of this decorator.
		Action[][] combinedActions = new Action[delegateActions.length + currentActionsToAdd.length][];
		for (int index = 0; index < delegateActions.length; index++) {
			combinedActions[index] = delegateActions[index];
		}
		for (int index = 0; index < currentActionsToAdd.length; index++) {
			combinedActions[delegateActions.length + index] = currentActionsToAdd[index];
		}

		return combinedActions;

	}

	public Component getContent() {
		return delegate.getContent();
	}

	public LeafDock getDock() {
		return delegate.getDock();
	}

	public void setDock(LeafDock dock) {
		delegate.setDock(dock);
	}

	public int getDockingModes() {
		return delegate.getDockingModes();
	}

	public Icon getIcon() {
		return delegate.getIcon();
	}

	public String getID() {
		return delegate.getID();
	}

	public int getLastDockingMode() {
		return delegate.getLastDockingMode();
	}

	public void setLastDockingMode(int dockingMode) {
		delegate.setLastDockingMode(dockingMode);
	}

	public int getPossibleStates() {
		return delegate.getPossibleStates();
	}

	public int getState() {
		return delegate.getState();
	}

	public String getTitle() {
		return delegate.getTitle();
	}

	public String getDescription() {
		return delegate.getDescription();
	}

	public boolean isWithHeader() {
		return delegate.isWithHeader();
	}

	public Object getVisualizer() {
		return delegate.getVisualizer();
	}

	public void setState(int state, Object visualizer) {
		delegate.setState(state, visualizer);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		delegate.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		delegate.removePropertyChangeListener(listener);
	}

	public void addDockingListener(DockingListener listener) {
		delegate.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		delegate.removeDockingListener(listener);
	}

	public void fireDockingWillChange(DockableEvent dockableEvent) {
		delegate.fireDockingWillChange(dockableEvent);
	}

	public void fireDockingChanged(DockableEvent dockableEvent) {
		delegate.fireDockingChanged(dockableEvent);
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
		return delegate.toString();
	}

	// Getters / Setters.

	/**
	 * Gets the actions that will be added by this wrapper to the matrix of actions of the delegate.
	 *
	 * @return actions            The actions that will be added to the matrix of actions of the delegate.
	 * @see    #getActions()
	 */
	public Action[][] getActionsToAdd() {
		return actionsToAdd;
	}

	/**
	 * Sets the actions that will be added by this wrapper to the matrix of actions of the delegate.
	 *
	 * @param    actions            The actions that will be added to the matrix of actions of the delegate.
	 * @see    #getActions()
	 */
	public void setActionsToAdd(Action[][] actions) {
		this.actionsToAdd = actions;
	}

	/**
	 * Gets the wrapped dockable.
	 *
	 * @return The wrapped dockable.
	 */
	public Dockable getDelegate() {
		return delegate;
	}

}
