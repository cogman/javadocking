package com.javadocking.dockable.action;

import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.DefaultCompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.util.DockingUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the default popup menu factory.
 * </p>
 * <p>
 * If a selected dockable is given, a menu item is created for every action of the matrix that is retrieved by
 * {@link Dockable#getActions()}. The actions of a row are separated from other rows.
 * </p>
 * <p>
 * If also a composite dockable is given, an action to close and to minimize all the dockables is added.
 * </p>
 * <p>
 * If the composite dockable has  selected dockable, an action to close and to minimize all the other dockables is added.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultPopupMenuFactory implements PopupMenuFactory {

	// Static fields.

	/**
	 * The constant that defines that the actions of the dockable have to be added to the popup menu.
	 * The actions of the dockable are retrieved by {@link Dockable#getActions()}.
	 */
	public static final int DOCKABLE_ACTIONS = 1;
	/**
	 * The constant that defines that the close all action has to be added to the popup menu.
	 */
	public static final int CLOSE_ALL_ACTION = 1 << 1;
	/**
	 * The constant that defines that the close others action has to be added to the popup menu.
	 */
	public static final int CLOSE_OTHERS_ACTION = 1 << 2;
	/**
	 * The constant that defines that the minimize all action has to be added to the popup menu.
	 */
	public static final int MINIMIZE_ALL_ACTION = 1 << 3;
	/**
	 * The constant that defines that the minimize others action has to be added to the popup menu.
	 */
	public static final int MINIMIZE_OTHERS_ACTION = 1 << 4;
	/**
	 * The constant that defines that the restore all action has to be added to the popup menu.
	 */
	public static final int RESTORE_ALL_ACTION = 1 << 5;

	// Fields.

	/**
	 * This integer defines which actions will be added to te popup menu.
	 * The  constants DOCKABLE_ACTIONS, CLOSE_ALL_ACTION, CLOSE_OTHERS_ACTION,
	 * MINIMIZE_ALL_ACTION, and MINIMIZE_OTHERS_ACTION can be combined with the bitwise or-operation.
	 */
	private int popupActions = Integer.MAX_VALUE;

	// Implementations of PopupMenuFactory.

	@Nullable
	public JPopupMenu createPopupMenu(@Nullable Dockable selectedDockable, @Nullable CompositeDockable compositeDockable) {

		JPopupMenu popupMenu = new JPopupMenu();
		int count = 0;
		int currentState = -1;

		// Create the actions for the selected dockable.
		if ((selectedDockable != null) && ((DOCKABLE_ACTIONS & popupActions) != 0)) {

			currentState = selectedDockable.getState();

			// We have to get the wrapper around the dockable with all the actions.
			Dockable dockable = DockingUtil.retrieveDockableOfDockModel(selectedDockable.getID());
			if (dockable == null) {
				throw new IllegalStateException("The dragged dockable should be docked in the dock model.");
			}

			Action[][] actionMatrix = dockable.getActions();

			// Do we have actions?
			if (actionMatrix == null) {
				return null;
			}

			// Iterate over the rows.
			boolean firstGroup = true;
			for (Action[] actionGroup : actionMatrix) {

				if ((actionGroup != null) && (actionGroup.length > 0)) {
					// Are we in the first group?
					if (firstGroup) {
						firstGroup = false;
					} else {
						popupMenu.addSeparator();
					}

					// Add all the actions of the group.
					for (final Action anActionGroup : actionGroup) {
						popupMenu.add(new JMenuItem(anActionGroup));
						count++;
					}
				}
			}

		}

		// Create the actions for the composite dockable.
		if ((compositeDockable != null) && (compositeDockable.getDockableCount() > 0)) {
			if ((compositeDockable.getSelectedDockable() == null) || (compositeDockable.getDockableCount() > 1)) {
				DockableStateAction closeAllAction = null;
				DockableStateAction minimizeAllAction = null;
				DockableStateAction restoreAllAction = null;
				DockableStateAction closeOthersAction = null;
				DockableStateAction minimizeOthersAction = null;

				// We create the 2 actions: close all and minimize all.
				if ((currentState != DockableState.CLOSED) && (CLOSE_ALL_ACTION & popupActions) != 0) {
					if (createAction(DockableState.CLOSED, compositeDockable)) {
						closeAllAction = new DefaultDockableStateAction(compositeDockable, DockableState.CLOSED, "Close All", null);
					}
				}
				if ((currentState != DockableState.MINIMIZED) && (MINIMIZE_ALL_ACTION & popupActions) != 0) {
					if (createAction(DockableState.MINIMIZED, compositeDockable)) {
						minimizeAllAction = new DefaultDockableStateAction(compositeDockable, DockableState.MINIMIZED, "Minimize All", null);
					}
				}
				if ((currentState != DockableState.NORMAL) && (RESTORE_ALL_ACTION & popupActions) != 0) {
					if (createAction(DockableState.NORMAL, compositeDockable)) {
						restoreAllAction = new DefaultDockableStateAction(compositeDockable, DockableState.NORMAL, "Restore All", null);
					}
				}

				// Is there a selected dockable?
				if ((compositeDockable.getSelectedDockable() != null) && (compositeDockable.getDockableCount() > 1)) {
					List otherDockablesList = new ArrayList();
					Dockable selectedDockableOfComposite = compositeDockable.getSelectedDockable();
					for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
						Dockable dockableToAdd = compositeDockable.getDockable(index);
						if (!dockableToAdd.equals(selectedDockableOfComposite)) {
							otherDockablesList.add(dockableToAdd);
						}
					}
					Dockable[] otherDockables = new Dockable[compositeDockable.getDockableCount() - 1];
					otherDockables = (Dockable[]) otherDockablesList.toArray(otherDockables);
					CompositeDockable compositeDockableExceptSelected = new DefaultCompositeDockable(otherDockables);

					// We create a menu with 2 actions. Close all and minimize all.
					if ((currentState != DockableState.CLOSED) && (CLOSE_OTHERS_ACTION & popupActions) != 0) {
						if (createAction(DockableState.CLOSED, compositeDockableExceptSelected)) {
							closeOthersAction = new DefaultDockableStateAction(compositeDockableExceptSelected, DockableState.CLOSED, "Close Others", null);
						}
					}
					if ((currentState != DockableState.MINIMIZED) && (MINIMIZE_OTHERS_ACTION & popupActions) != 0) {
						if (createAction(DockableState.MINIMIZED, compositeDockableExceptSelected)) {
							minimizeOthersAction = new DefaultDockableStateAction(compositeDockableExceptSelected, DockableState.MINIMIZED, "Minimize Others", null);
						}
					}

				}

				// Add the action.
				if ((count > 0) &&
						((closeAllAction != null) ||
								(restoreAllAction != null) ||
								(closeOthersAction != null) ||
								(minimizeAllAction != null) ||
								(minimizeOthersAction != null))) {
					popupMenu.addSeparator();
				}
				if (closeAllAction != null) {
					count++;
					popupMenu.add(new JMenuItem(closeAllAction));
				}
				if (closeOthersAction != null) {
					count++;
					popupMenu.add(new JMenuItem(closeOthersAction));
				}
				if (minimizeAllAction != null) {
					count++;
					popupMenu.add(new JMenuItem(minimizeAllAction));
				}
				if (minimizeOthersAction != null) {
					count++;
					popupMenu.add(new JMenuItem(minimizeOthersAction));
				}
				if (restoreAllAction != null) {
					count++;
					popupMenu.add(new JMenuItem(restoreAllAction));
				}

			}
		}

		// Return the popup menu, if it has any items.
		if (count > 0) {
			return popupMenu;
		}
		return null;

	}

	// Getters / Setters.

	/**
	 * Gets the integer that defines which actions will be added to te popup menu.
	 * This is a bitwise or of the constants DOCKABLE_ACTIONS, CLOSE_ALL_ACTION, CLOSE_OTHERS_ACTION,
	 * MINIMIZE_ALL_ACTION, and MINIMIZE_OTHERS_ACTION.
	 *
	 * @return The integer that defines which actions will be added to te popup menu.
	 */
	public int getPopupActions() {
		return popupActions;
	}

	/**
	 * Sets the integer that defines which actions will be added to te popup menu.
	 *
	 * @param popupActions The integer that defines which actions will be added to te popup menu.
	 *                     The  constants DOCKABLE_ACTIONS, CLOSE_ALL_ACTION, CLOSE_OTHERS_ACTION,
	 *                     MINIMIZE_ALL_ACTION, and MINIMIZE_OTHERS_ACTION can be combined with the bitwise or-operation.
	 */
	public void setPopupActions(int popupActions) {
		this.popupActions = popupActions;
	}

	// Private metods.

	/**
	 * True is returned when all the following conditions are true for all the child dockables:
	 * <ul>
	 * <li>The new state is a valid state for every dockable.</li>
	 * <li>Every dockable of the composite has an action to convert to the new state.</li>
	 * <li>Every dockable is in a different state as the new state.</li>
	 * </ul>
	 */
	private boolean createAction(int newState, CompositeDockable compositeDockable) {

		for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
			Dockable childDockable = compositeDockable.getDockable(index);
			// Is the new state a valid state for the dockable?
			if ((childDockable.getPossibleStates() & newState) == 0) {
				return false;
			}
			// The new state is different from the current state for this child dockable.
			if (childDockable.getState() == newState) {
				return false;
			}
			// Has the dockable an action to convert to the new state.
			if (!hasDockableStateAction(childDockable, newState)) {
				return false;
			}
		}

		// The new state is not valid for any child.
		return true;

	}

	/**
	 * Determines if the dockable has a an action to change the dockable to the new state ({@link DockableStateAction}).
	 *
	 * @param newState The state for which the dockable can have an action.
	 * @return True when the dockable has an action to go to the given state.
	 */
	private boolean hasDockableStateAction(Dockable dockable, int newState) {

		Action[][] actionMatrix = dockable.getActions();
		if (actionMatrix == null) {
			return false;
		}
		for (Action[] actionRow : actionMatrix) {
			if (actionRow != null) {
				for (Action action : actionRow) {
					if (action instanceof DockableStateAction) {
						DockableStateAction dockableStateAction = (DockableStateAction) action;
						if (dockableStateAction.getNewDockableState() == newState) {
							return true;
						}
					}
				}
			}
		}
		return false;

	}
}
