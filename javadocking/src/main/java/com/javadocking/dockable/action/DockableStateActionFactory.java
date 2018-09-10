package com.javadocking.dockable.action;

import com.javadocking.dockable.Dockable;

/**
 * <p>
 * This is an interface for a factory that creates a {@link DockableStateAction}
 * that is used to change the state of a dockable.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockableStateActionFactory {

	// Interface methods.

	/**
	 * Creates the action to change the state of the dockable.
	 *
	 * @param    dockable            The dockable whose state will be changed by the created action.
	 * @param    newDockableState    The action puts the dockable in this state.
	 * This is an integer constant defined by {@link com.javadocking.dockable.DockableState}.
	 * @return The action to change the state of the dockable.
	 * @throws IllegalArgumentException    If the given dockable state is not valid.
	 */
	public DockableStateAction createDockableStateAction(Dockable dockable, int newDockableState);

}
