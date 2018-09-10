package com.javadocking.dockable.action;

import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;

import javax.swing.*;

/**
 * <p>
 * This factory creates the default actions for changing the state of a dockable.
 * </p>
 * <p>
 * The new state of a dockable is defined by a constant of the class {@link com.javadocking.dockable.DockableState}.
 * </p>
 * <p>
 * This class defines also constants for the possible actions that can be added to the popup, e.g. closed all, close others,
 * minimize all, minimize others, and dockable actions.
 * These constants are integers that can be combined with the bitwise or-operation.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultDockableStateActionFactory implements DockableStateActionFactory {


	// Implementations of WindowStateActionFactory.

	/**
	 * Creates a default window state action for the states: DockableState.CLOSED, DockableState.NORMAL, DockableState.MAXIMIZED, DockableState.MINIMIZED, and DockableState.EXTERNALIZED.
	 *
	 * @param    dockable            The dockable whose state will be changed by the created action.
	 * @param    newDockableState    The action puts the dockable in this state. Should be DockableState.NORMAL, DockableState.MAXIMIZED, DockableState.MINIMIZED, DockableState.CLOSED, or DockableState.EXTERNALIZED.
	 * @return The action to change the state of the window in which the dockable is docked.
	 * @throws IllegalArgumentException    If the given window state is not DockableState.NORMAL, DockableState.MAXIMIZED, DockableState.MINIMIZED, DockableState.CLOSED, or DockableState.EXTERNALIZED.
	 */
	public DockableStateAction createDockableStateAction(Dockable dockable, int newDockableState) {

		boolean enabled = (dockable.getPossibleStates() & newDockableState) != 0;
		Icon icon;
		DockableStateAction action;
		switch (newDockableState) {
			case DockableState.CLOSED:
				if (enabled) {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/close12.gif"));
				} else {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/closeDisabled12.gif"));
				}
				action = new DefaultDockableStateAction(dockable, DockableState.CLOSED, "Close", icon);
				break;
			case DockableState.NORMAL:
				if (enabled) {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/normal12.gif"));
				} else {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/normalDisabled12.gif"));
				}
				action = new DefaultDockableStateAction(dockable, DockableState.NORMAL, "Restore", icon);
				break;
			case DockableState.MAXIMIZED:
				if (enabled) {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/maximize12.gif"));
				} else {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/maximizeDisabled12.gif"));
				}
				action = new DefaultDockableStateAction(dockable, DockableState.MAXIMIZED, "Maximize", icon);
				break;
			case DockableState.MINIMIZED:
				if (enabled) {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/minimize12.gif"));
				} else {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/minimizeDisabled12.gif"));
				}
				action = new DefaultDockableStateAction(dockable, DockableState.MINIMIZED, "Minimize", icon);
				break;
			case DockableState.EXTERNALIZED:
				if (enabled) {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/externalize12.gif"));
				} else {
					icon = new ImageIcon(getClass().getResource("/com/javadocking/resources/images/externalizeDisabled12.gif"));
				}
				action = new DefaultDockableStateAction(dockable, DockableState.EXTERNALIZED, "Externalize", icon);
				break;
			default:
				throw new IllegalArgumentException("Cannot create a DockableStateAction for the state [" + newDockableState + "]");
		}

		action.setEnabled(enabled);
		return action;

	}

}
