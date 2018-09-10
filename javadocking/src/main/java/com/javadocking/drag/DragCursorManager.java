package com.javadocking.drag;

import javax.swing.*;
import java.awt.*;

/**
 * This class helps other classes setting a drag cursor on the glass pane above the component.
 * The glass pane of the component will be made visible.
 *
 * @author Heidi Rakels.
 */
class DragCursorManager {

	// Fields.

	/**
	 * The old cursor of the glass pane.
	 */
	private Cursor oldCursor;
	/**
	 * The cursor that is currently on the glass pane of the component.
	 */
	private Cursor cursor;
	/**
	 * The component for which the cursor is set.
	 */
	private Component cursorComponent;
	/**
	 * The glass pane of the component.
	 */
	private Component glassPane;
	/**
	 * True if the glass pane was visible before the new cursor was put on the glass pane, false otherwise.
	 */
	private boolean glassPaneOldVisible;

	// Public methods.

	/**
	 * Sets the given cursor on the glass pane of the given component.
	 *
	 * @param component The component where the mouse currently is.
	 * @param newCursor The cursor for the component.
	 */
	public void setCursor(Component component, Cursor newCursor) {
		// Do nothing if the cursor is the same and the component is the same.
		if ((newCursor.equals(cursor) && component.equals(cursorComponent))) {
			return;
		}

		// Do nothing if there is no glass pane.
		Component newGlassPane = SwingUtilities.getRootPane(component).getGlassPane();
		if (newGlassPane == null) {
			return;
		}

		// Do nothing if the cursor is the same and the glass pane is the same.			
		if ((newCursor.equals(cursor) && newGlassPane.equals(glassPane))) {
			return;
		}

		// Reset the previous glasspane.
		if (glassPane != null) {
			glassPane.setVisible(glassPaneOldVisible);
			glassPane.setCursor(oldCursor);
		}

		// Set the cursor for the new glasspane.
		glassPane = newGlassPane;
		glassPaneOldVisible = glassPane.isVisible();
		if (oldCursor == null) {
			oldCursor = glassPane.getCursor();
		}
		glassPane.setCursor(newCursor);
		glassPane.setVisible(true);
		cursor = newCursor;
		cursorComponent = component;

	}

	/**
	 * Resets the glass pane in its previous state. Resets the fields of this object.
	 */
	public void resetCursor() {
		// Reset the glasspane.
		if (oldCursor != null) {
			glassPane.setCursor(oldCursor);
			glassPane.setVisible(glassPaneOldVisible);
		}

		// Reset cursor fields.
		glassPane = null;
		cursorComponent = null;
		oldCursor = null;
		cursor = null;
		glassPaneOldVisible = false;
	}

}
