package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * <p>
 * This drag listener contains a {@link Dragger}. The real dragging functionality is done by this dragger.
 * This class only analyzes the mouse events and decides if dragging should be started, continued, stopped,
 * or canceled.
 * </p>
 * <p>
 * Dragging is started when a mouse button is pressed and a specific modifier (CTRL, ALT, SHIFT or META)
 * is down.
 * </p>
 * <p>
 * To have a specific mouse button for starting dragging, this button should be specified with
 * the method {@link #setStartButtonMask(int)}, e.g. if you want to use
 * the middle mouse button for dragging:
 * <code>setStartButtonMask(java.awt.event.InputEvent.BUTTON2_MASK)</code><br>
 * The default mouse button for starting dragging is the left mouse button.
 * </p>
 * <p>
 * To have a specific modifier for starting dragging, this modifier should be specified with
 * the method {@link #setStartModifierMask(int)}, e.g. if you want to use
 * the CTRL modifier for dragging:
 * <code>setStartModifierMask(java.awt.event.InputEvent.CTRL_DOWN_MASK)</code><br>
 * If no specific modifier should be down, give NO_MASK as parameter.
 * The default modifier for starting dragging is NO_MASK.
 * </p>
 * <p>
 * Dragging is canceled when a specific mouse button is pressed. To have a specific mouse button for canceling dragging,
 * this button should be specified with the method {@link #setCancelButtonMask(int)}, e.g. if you want to use
 * the right mouse button for canceling dragging:
 * <code>setCancelButtonMask(java.awt.event.InputEvent.BUTTON3_MASK)</code><br>
 * The default mouse button for canceling dragging is the right mouse button.
 * </p>
 * <p>
 * Dragging can also be canceled when releasing the mouse button while a modifier (CTRL, ALT, SHIFT or META) is pressed.
 * To have a specific modifier for canceling dragging,
 * this modifier should be specified with the method {@link #setCancelModifierMask(int)}, f.e. if you want to use
 * the ALT modifier for canceling dragging:
 * <code>setCancelModifierMask(java.awt.event.InputEvent.ALT_DOWN_MASK)</code><br>
 * There is no default modifier for canceling dragging.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultDragListener implements DragListener {

	// Static fields.

	public static final int NO_MASK = 0;

	/**
	 * The value of dragState when there is no dragging.
	 */
	private static final int NO_DRAGGING = 0;
	/**
	 * The value of dragState when dragging started, but we are still in a wait mode.
	 */
	private static final int START_DRAGGING = 1;
	/**
	 * The value of dragState when we are really dragging.
	 */
	private static final int DRAGGING = 2;
	/**
	 * Dragging starts only this delay after the mouse is pressed.
	 */
	private static final int DELAY = 150;


	// Fields.

	/**
	 * The drag listener listens to drag events for this dock. Is null when dockable is not null.
	 */
	private Dock dock;
	/**
	 * The drag listener listens to drag events for this dockable. Is null when dock is not null.
	 */
	private Dockable dockable;
	/**
	 * This dragger does the real dragging.
	 */
	private Dragger dragger;
	/**
	 * True when we are dragging, false otherwise.
	 */
	private int dragState = NO_DRAGGING;
	/**
	 * When one of these buttons are pressed when the mouse is pressed or released, dragging is canceled.
	 */
	private int cancelButtonMask = InputEvent.BUTTON3_MASK;
	/**
	 * When one of these buttons are pressed when the mouse is pressed or released, dragging is canceled.
	 */
	private int cancelModifierMask = NO_MASK;
	/**
	 * When these buttons are pressed when the mouse is pressed, dragging is started.
	 */
	private int startButtonMask = InputEvent.BUTTON1_MASK;
	/**
	 * When these buttons are pressed when the mouse is pressed, dragging is started.
	 */
	private int startModifierMask = NO_MASK;
	/**
	 * The timer to delay the dragging.
	 */
	private Timer timer;
	/**
	 * The ID of the last interesting mouse event.
	 */
	private int eventID = Integer.MIN_VALUE;
	/**
	 * The time of the last interesting mouse event.
	 */
	private long eventWhen = Long.MIN_VALUE;

	// Constructors.

	/**
	 * Constructs a drag mouse listener for the given dock.
	 *
	 * @throws IllegalArgumentException If the dock is null.
	 * @param    dock            The drag listener listens to drag events for this dock.
	 */
	public DefaultDragListener(Dock dock) {
		if (dock == null) {
			throw new IllegalArgumentException("Dock null.");
		}
		this.dock = dock;
	}

	/**
	 * Constructs a drag mouse listener for the given dockable.
	 *
	 * @throws IllegalArgumentException If the dockable is null.
	 * @param    dockable            The drag listener listens to drag events for this dockable.
	 */
	public DefaultDragListener(Dockable dockable) {
		if (dockable == null) {
			throw new IllegalArgumentException("Dockable null.");
		}
		this.dockable = dockable;
	}

	// Implementations of DragListener.

	public void mousePressed(MouseEvent mouseEvent) {

		// Did we handle this event already?
		if ((eventID == mouseEvent.getID()) && (eventWhen == mouseEvent.getWhen())) {
			return;
		} else {
			eventID = mouseEvent.getID();
			eventWhen = mouseEvent.getWhen();
		}

		// Stop the delay timer.
		if (timer != null) {
			timer.stop();
			timer = null;
		}

		if (dragState == NO_DRAGGING) {
			if (canStartDragging(mouseEvent)) {
				// Create a dragger.
				dragger = createDragger();

				// Try to start dragging.
				if (dragger.startDragging(mouseEvent)) {
					// We could start dragging.
					dragState = START_DRAGGING;
					startDragDelay();
				} else {
					dragger = null;
				}
			}

			if ((dragState == NO_DRAGGING) && (canShowPopup(mouseEvent))) {
				// Create a dragger.
				dragger = createDragger();

				// Show the popup.
				dragger.showPopupMenu(mouseEvent);

				dragger = null;
			}
		} else {
			// Do we have to cancel dragging?
			if (canCancelDragging(mouseEvent)) {
				dragger.cancelDragging(mouseEvent);
				dragState = NO_DRAGGING;
				dragger = null;
			}
		}

	}

	public void mouseDragged(MouseEvent mouseEvent) {

		// Did we handle this event already?
		if ((eventID == mouseEvent.getID()) && (eventWhen == mouseEvent.getWhen())) {
			return;
		} else {
			eventID = mouseEvent.getID();
			eventWhen = mouseEvent.getWhen();
		}

		// Verify if we are dragging?
		if (dragState == DRAGGING) {
			dragger.drag(mouseEvent);
		}

	}

	public void mouseReleased(MouseEvent mouseEvent) {

		// Did we handle this event already?
		if ((eventID == mouseEvent.getID()) && (eventWhen == mouseEvent.getWhen())) {
			return;
		} else {
			eventID = mouseEvent.getID();
			eventWhen = mouseEvent.getWhen();
		}

		// Verify if we are dragging?
		if (dragState == DRAGGING) {
			// Should the dragging be canceled?
			if (canCancelDragging(mouseEvent)) {
				dragger.cancelDragging(mouseEvent);
			} else {
				dragger.stopDragging(mouseEvent);
			}
		} else {
			if (canShowPopup(mouseEvent)) {
				// Create a dragger.
				dragger = createDragger();

				// Show the popup.
				dragger.showPopupMenu(mouseEvent);
			}
		}

		dragState = NO_DRAGGING;
		dragger = null;
		cancelDragDelay();

	}

	public void mouseMoved(MouseEvent mouseEvent) {
		// Do nothing.
	}

	public void mouseClicked(MouseEvent mouseEvent) {
		// Do nothing.
	}

	public void mouseEntered(MouseEvent mouseEvent) {
		// Do nothing.
	}

	public void mouseExited(MouseEvent mouseEvent) {
		// Do nothing.
	}

	// Getters / Setters.

	/**
	 * Gets the mouse button that should be pressed for canceling dragging.
	 *
	 * @return The mouse button that should be pressed for canceling dragging.
	 * When no mouse button has to be pressed, NO_MASK is returned.
	 * The default value is the right mouse button java.awt.event.InputEvent.BUTTON3_MASK.
	 */
	public int getCancelButtonMask() {
		return cancelButtonMask;
	}

	/**
	 * Sets the mouse button that should be pressed for canceling dragging.
	 *
	 * @param cancelButtonMask The mouse button that should be pressed for canceling dragging.
	 *                         When no mouse button has to be pressed, this should be NO_MASK.
	 */
	public void setCancelButtonMask(int cancelButtonMask) {
		this.cancelButtonMask = cancelButtonMask;
	}

	/**
	 * Gets the modifier that should be down for canceling dragging when the button is released.
	 *
	 * @return The modifier that should be down for canceling dragging when the button is released.
	 * When no modifier has to be down, NO_MASK is returned.
	 * The default value is NO_MASK.
	 */
	public int getCancelModifierMask() {
		return cancelModifierMask;
	}

	/**
	 * Sets the modifier that should be down for canceling dragging when the button is released.
	 *
	 * @param cancelModifierMask The modifier that should be down for canceling dragging when the button is released.
	 *                           When no modifier has to be down, this should be NO_MASK.
	 */
	public void setCancelModifierMask(int cancelModifierMask) {
		this.cancelModifierMask = cancelModifierMask;
	}

	/**
	 * Gets the mouse button that should be pressed for starting dragging.
	 *
	 * @return The mouse button that should be pressed for starting dragging.
	 * When no specific mouse button has to be pressed, NO_MASK is returned.
	 * The default value is the left mouse button java.awt.event.InputEvent.BUTTON1_MASK.
	 */
	public int getStartButtonMask() {
		return startButtonMask;
	}

	/**
	 * Sets the mouse button that should be pressed for starting dragging.
	 *
	 * @param startButtonMask The mouse button that should be pressed for starting dragging.
	 *                        When no specific mouse button has to be pressed, this should be NO_MASK.
	 */
	public void setStartButtonMask(int startButtonMask) {
		this.startButtonMask = startButtonMask;
	}

	/**
	 * Gets the modifier that should be pressed for starting dragging.
	 *
	 * @return The modifier that should be pressed for starting dragging.
	 * When no modifier has to be pressed, NO_MASK is returned.
	 * The default value is NO_MASK.
	 */
	public int getStartModifierMask() {
		return startModifierMask;
	}

	/**
	 * Sets the modifier that should be pressed for starting dragging.
	 *
	 * @param startModifierMask The modifier that should be pressed for starting dragging.
	 *                          When no modifier has to be pressed, this should be NO_MASK.
	 */
	public void setStartModifierMask(int startModifierMask) {
		this.startModifierMask = startModifierMask;
	}


	// Protected metods.

	/**
	 * Determines if dragging should be started for the given mouse event.
	 * Dragging can be started if the start button defined by {@link #getStartButtonMask()}
	 * is pressed and if one of the modifiers defined by {@link #getStartModifierMask()}
	 * is down.
	 *
	 * @return True if dragging should be started, false otherwise.
	 */
	protected boolean canStartDragging(MouseEvent mouseEvent) {
		boolean buttonOk = false;
		boolean modifierOk = false;

		// Is there a start button mask?
		if (startButtonMask == NO_MASK) {
			buttonOk = true;
		} else {
			// Is the start mouse button pressed?
			if ((mouseEvent.getModifiers() & startButtonMask) != 0) {
				buttonOk = true;
			}
		}

		// Is there a start modifier mask?
		if (startModifierMask == NO_MASK) {
			modifierOk = true;
		} else {
			// Is there a start modifier pressed?
			if ((startModifierMask & mouseEvent.getModifiersEx()) != 0) {
				modifierOk = true;
			}
		}

		return buttonOk && modifierOk;

	}

	/**
	 * Determines if dragging should be canceled for the given mouse event.
	 * Dragging should be canceled if the cancel button defined by {@link #getCancelButtonMask()}
	 * is pressed and if one of the modifiers defined by {@link #getCancelModifierMask()}
	 * is down.
	 *
	 * @return True if dragging should be started, false otherwise.
	 */
	protected boolean canCancelDragging(MouseEvent mouseEvent) {
		boolean buttonOk = false;
		boolean modifierOk = false;

		// Is there a cancel button mask?
		if (cancelButtonMask == NO_MASK) {
			buttonOk = false;
		} else {
			// Is the cancel mouse button pressed?
			if ((mouseEvent.getModifiers() & cancelButtonMask) != 0) {
				buttonOk = true;
			}
		}

		// Is there a cancel modifier mask?
		if (cancelModifierMask == NO_MASK) {
			modifierOk = true;
		} else {
			// Is there a cancel modifier pressed?
			if ((cancelModifierMask & mouseEvent.getModifiersEx()) != 0) {
				modifierOk = true;
			}
		}

		return buttonOk && modifierOk;

	}

	/**
	 * Determines if for this mouse event the popup of the dockable should be shown.
	 *
	 * @return True if dragging should be started, false otherwise.
	 */
	protected boolean canShowPopup(MouseEvent mouseEvent) {
		return SwingUtilities.isRightMouseButton(mouseEvent);
	}

	// Private metods.

	/**
	 * Creates a dragger for the dock or the dockable.
	 *
	 * @return The created dragger.
	 */
	private Dragger createDragger() {

		if (dock != null) {
			return DockingManager.getDraggerFactory().createDragger(dock);
		}
		if (dockable != null) {
			return DockingManager.getDraggerFactory().createDragger(dockable);
		}

		throw new IllegalStateException("The dock and dockable are null.");
	}

	/**
	 * Starts the delay for dragging.
	 */
	private void startDragDelay() {

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dragDelayFinished();
			}
		};
		timer = new Timer(DELAY, taskPerformer);
		timer.setRepeats(false);
		timer.start();

	}

	/**
	 * The delay for dragging is passed. If we still want to drag,
	 * then the dragState is set to DRAGGING.
	 */
	private void dragDelayFinished() {
		timer = null;
		if (dragState == START_DRAGGING) {
			dragState = DRAGGING;
		}
	}

	/**
	 * Stops the delay timer and sets it to null, if it is not already null.
	 */
	private void cancelDragDelay() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

}
