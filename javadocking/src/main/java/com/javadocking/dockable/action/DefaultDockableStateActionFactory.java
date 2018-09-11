package com.javadocking.dockable.action;

import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;

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
	public DockableStateAction createDockableStateAction(@NotNull Dockable dockable, int newDockableState) {

		boolean enabled = (dockable.getPossibleStates() & newDockableState) != 0;
		DockableStateAction action;
		final Icon icon = getIcon(newDockableState, enabled);
		switch (newDockableState) {
			case DockableState.CLOSED:
				action = new DefaultDockableStateAction(dockable, newDockableState, "Close", icon);
				break;
			case DockableState.NORMAL:
				action = new DefaultDockableStateAction(dockable, newDockableState, "Restore", icon);
				break;
			case DockableState.MAXIMIZED:
				action = new DefaultDockableStateAction(dockable, newDockableState, "Maximize", icon);
				break;
			case DockableState.MINIMIZED:
				action = new DefaultDockableStateAction(dockable, newDockableState, "Minimize", icon);
				break;
			case DockableState.EXTERNALIZED:
				action = new DefaultDockableStateAction(dockable, newDockableState, "Externalize", icon);
				break;
			default:
				throw new IllegalArgumentException("Cannot create a DockableStateAction for the state [" + newDockableState + "]");
		}

		action.setEnabled(enabled);
		return action;

	}

	static Icon getIcon(int dockableState, boolean enabled)
	{
		final ClassLoader classLoader = DefaultDockableStateActionFactory.class.getClassLoader();
		final URL resource;
		switch (dockableState) {
			case DockableState.CLOSED:
				if (enabled) {
					resource = classLoader.getResource("images/close12.gif");
				} else {
					resource = classLoader.getResource("images/closeDisabled12.gif");
				}
				break;
			case DockableState.NORMAL:
				if (enabled) {
					resource = classLoader.getResource("images/normal12.gif");
				} else {
					resource = classLoader.getResource("images/normalDisabled12.gif");
				}
				break;
			case DockableState.MAXIMIZED:
				if (enabled) {
					resource = classLoader.getResource("images/maximize12.gif");
				} else {
					resource = classLoader.getResource("images/maximizeDisabled12.gif");
				}
				break;
			case DockableState.MINIMIZED:
				if (enabled) {
					resource = classLoader.getResource("images/minimize12.gif");
				} else {
					resource = classLoader.getResource("images/minimizeDisabled12.gif");
				}
				break;
			case DockableState.EXTERNALIZED:
				if (enabled) {
					resource = classLoader.getResource("images/externalize12.gif");
				} else {
					resource = classLoader.getResource("images/externalizeDisabled12.gif");
				}
				break;
			default:
				throw new IllegalArgumentException("Cannot create a Icon for the state [" + dockableState + "] enabled [" + enabled + "]");
		}
		if (resource == null)
			throw new IllegalArgumentException("Unable to load icon resource for dockable [" + dockableState + "] enabled [" + enabled + "]");
		return new ImageIcon(resource);
	}
}
