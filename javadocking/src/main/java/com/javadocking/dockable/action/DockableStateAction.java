package com.javadocking.dockable.action;

import com.javadocking.dockable.Dockable;

import javax.swing.*;

/**
 * <p>
 * This is an action to change the state of the dockable.
 * </p>
 * <p>
 * The new state for the dockable is defined by an integer.
 * This is an integer constant defined by {@link com.javadocking.dockable.DockableState}.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockableStateAction extends Action {

	/**
	 * Gets the dockable whose state is changed by this action.
	 *
	 * @return The dockable whose state is changed by this action.
	 */
	public Dockable getDockable();

	/**
	 * Gets the state in which the dockable should be after performing the action.
	 * This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 *
	 * @return The state in which the dockable should be after performing the action.
	 */
	public int getNewDockableState();

}
