package com.javadocking.drag;

import java.awt.event.MouseEvent;

/**
 * This is an interface for a class that drags {@link com.javadocking.dockable.Dockable}s
 * from a source {@link com.javadocking.dock.Dock} to a destination dock.
 * It can also move a dockable inside a dock.
 *
 * @author Heidi Rakels.
 */
public interface Dragger {
	/**
	 * <p>
	 * Tries to start the dragging of a {@link com.javadocking.dockable.Dockable}
	 * from a source {@link com.javadocking.dock.Dock} to a destination dock.
	 * </p>
	 * <p>
	 * The dockable that should be dragged is searched for the given mouse position.
	 * </p>
	 *
	 * @param mouseEvent The mouse event that was triggered.
	 * @return True if the dragging of a dockable could be started, false otherwise.
	 */
	public boolean startDragging(MouseEvent mouseEvent);

	/**
	 * <p>
	 * Continues the dragging of a {@link com.javadocking.dockable.Dockable} object
	 * from a source {@link com.javadocking.dock.Dock} to a destination dock.
	 * </p>
	 * <p>
	 * Static draggers typically paint a rectangle on the place where the dockable would be docked,
	 * if the mouse was released at this position.
	 * </p>
	 * <p>
	 * Dynamic draggers typically do the docking immediately: the destination dock is searched
	 * for the given mouse location. They try to remove the dockable from its current dock and
	 * add it to its destination dock. If the destination dock is the same dock as the origin,
	 * they can move the dockable to a new position.
	 * </p>
	 *
	 * @param mouseEvent The new mouse event that was triggered.
	 */
	public void drag(MouseEvent mouseEvent);

	/**
	 * <p>
	 * Finishes the dragging of a {@link com.javadocking.dockable.Dockable} object
	 * from a source {@link com.javadocking.dock.Dock} to a destination dock.
	 * </p>
	 * <p>
	 * Static draggers typically do the docking now: the destination dock is searched
	 * for the given mouse location. They try to remove the dockable from its current dock and
	 * add it to its destination dock. If the destination dock is the same dock as the origin,
	 * they can move the dockable to a new position.
	 * </p>
	 * <p>
	 * Dynamic draggers typically only clean up now.
	 * </p>
	 *
	 * @param mouseEvent The last mouse event that was triggered.
	 */
	public void stopDragging(MouseEvent mouseEvent);

	/**
	 * <p>
	 * Cancels the dragging of a {@link com.javadocking.dockable.Dockable} object
	 * from a source {@link com.javadocking.dock.Dock} to a destination dock.
	 * The dragged dockable remains in its current dock.
	 * </p>
	 * <p>
	 * For dynamic draggers it is possible that the dock of the dockable has changed already.
	 * </p>
	 *
	 * @param mouseEvent The last mouse event that was triggered.
	 */
	public void cancelDragging(MouseEvent mouseEvent);

	/**
	 * Shows the popup menu for the selected dockable or the selected composite dockable.
	 *
	 * @param mouseEvent The last mouse event that was triggered.
	 */
	public void showPopupMenu(MouseEvent mouseEvent);
}
