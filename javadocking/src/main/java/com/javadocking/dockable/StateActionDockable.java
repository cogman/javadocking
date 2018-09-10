package com.javadocking.dockable;

import com.javadocking.dockable.action.DockableStateAction;
import com.javadocking.dockable.action.DockableStateActionFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This action dockable is a decorator for a delegate dockable.
 * It adds one row of actions to the matrix of actions ({@link com.javadocking.dockable.Dockable#getActions()}) of the delegate.
 * The actions that are added, are created by the {@link DockableStateActionFactory}.
 * </p>
 * <p>
 * Information on using dockables with state actions is in
 * <a href="http://www.javadocking.com/developerguide/dockable.html#ActionDockable" target="_blank">How to Use Dockables</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class StateActionDockable extends ActionDockable {

	// Fields.

	private DockableStateActionFactory dockableStateActionFactory;
	private int[] newDockableStates;
	private Dockable delegate;

	/**
	 * <p>
	 * Constructs a decorator for the given delegate dockable.
	 * </p>
	 * <p>
	 * One row of actions is added to the matrix of actions ({@link com.javadocking.dockable.Dockable#getActions()}) of the delegate.
	 * </p>
	 * <p>
	 * For every state in the given array <code>newDockableStates</code> an action is created with the method {@link DockableStateActionFactory#createDockableStateAction(Dockable, int)}.
	 * </p>
	 *
	 * @param delegate                   The decorated dockable.
	 * @param dockableStateActionFactory The factory that creates the {@link DockableStateAction}s.
	 * @param newDockableStates          The dockable states for which an action has to be added.
	 */
	public StateActionDockable(Dockable delegate, DockableStateActionFactory dockableStateActionFactory, int[] newDockableStates) {

		super(delegate);

		if (dockableStateActionFactory == null) {
			throw new IllegalArgumentException("The DockableStateActionFactory is null.");
		}

		this.dockableStateActionFactory = dockableStateActionFactory;
		this.newDockableStates = newDockableStates;
		this.delegate = delegate;

		Action[] dockableStateActions = new DockableStateAction[newDockableStates.length];
		for (int stateIndex = 0; stateIndex < dockableStateActions.length; stateIndex++) {
			dockableStateActions[stateIndex] = dockableStateActionFactory.createDockableStateAction(
					delegate, newDockableStates[stateIndex]);
		}

		Action[][] actionsToAdd = new Action[1][];
		actionsToAdd[0] = dockableStateActions;

	}

	/**
	 * Sets the actions that will be added by this wrapper to the matrix of actions of the delegate.
	 *
	 * @return actions            The actions that will be added to the matrix of actions of the delegate.
	 * @see    #getActions()
	 */
	public Action[][] getActionsToAdd() {

		// Remove the action that corresponds with the current state of the dockable.
		int currentDockableState = this.getState();

		List dockableStateActions = new ArrayList();
		for (int dockableState : newDockableStates) {
			if (currentDockableState != dockableState) {
				Action dockableStateAction = dockableStateActionFactory.createDockableStateAction(
						delegate, dockableState);
				dockableStateActions.add(dockableStateAction);
			}
		}

		Action[] dockableStateActionsArray = new DockableStateAction[dockableStateActions.size()];
		dockableStateActionsArray = (Action[]) dockableStateActions.toArray(dockableStateActionsArray);
		Action[][] actionsToAdd = new Action[1][];
		actionsToAdd[0] = dockableStateActionsArray;

		return actionsToAdd;

	}
}
