package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dockable.*;
import com.javadocking.drag.dockretriever.DockRetriever;
import com.javadocking.drag.dockretriever.StaticDockRetriever;
import com.javadocking.drag.painter.DefaultRectanglePainter;
import com.javadocking.drag.painter.DockableDragPainter;
import com.javadocking.drag.painter.SwDockableDragPainter;
import com.javadocking.util.DockingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * This is a class for dragging all the dockables in a {@link com.javadocking.dock.LeafDock}.
 * A {@link com.javadocking.dockable.CompositeDockable}
 * is created with the dockables of the leaf dock.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Dock}s that are used in the application should inherit
 * from the java.awt.Component class.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class StaticDragger implements Dragger {

	// Fields.

	// For docking.
	/**
	 * The dockRetriever.
	 */
	private DockRetriever dockRetriever = new StaticDockRetriever();
	/**
	 * The dock of the dockable before dragging.
	 */
	private LeafDock originDock;
	/**
	 * The current location of the mouse in screen coordinates.
	 */
	private Point screenLocation = new Point();
	/**
	 * This is the current location of the mouse in the dock where the dockable will be docked for the current mouse location.
	 * We keep it as field because we don't want to create every time a new point.
	 */
	private Point locationInDestinationDock = new Point();
	/**
	 * The offset of the clicked point.
	 */
	private Point dockableOffset = new Point();
	/**
	 * The composite dockable that is dragged.
	 */
	private CompositeDockable draggedDockable;

	// Cursors.
	/**
	 * Manages the cursors used for dragging dockables.
	 */
	private DragCursorManager cursorManager = new DragCursorManager();

	// For painting.
	/**
	 * Paints the rectangle where the dockable will be docked for the current mouse location.
	 */
	private DockableDragPainter dockableDragPainter;
	/**
	 * The rectangle where the dockable will be docked for the current mouse location.
	 */
	private Rectangle dockableDragRectangle = new Rectangle();

	// Constructors.

	/**
	 * Constructs a dragger with a default painter for painting the dragged dockables:
	 * a {@link SwDockableDragPainter} with a {@link DefaultRectanglePainter}.
	 */
	public StaticDragger() {
		this(new SwDockableDragPainter(new DefaultRectanglePainter()));
	}

	/**
	 * Constructs a dragger with the given painter for painting the dragged dockables.
	 *
	 * @param    dockableDragPainter The painter for painting the dragged dockables.
	 */
	public StaticDragger(DockableDragPainter dockableDragPainter) {
		this.dockableDragPainter = dockableDragPainter;
	}

	// Implementations of Dragger.

	public boolean startDragging(MouseEvent mouseEvent) {

		// Get the mouse position and the component. 
		Component mouseComponent = (Component) mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Reset the fields.
		reset();

		// Is the deepest dock a line dock?
		Component pressedComponent = SwingUtilities.getDeepestComponentAt(mouseComponent, x, y);
		LeafDock ancestorDock = (LeafDock) SwingUtilities.getAncestorOfClass(LeafDock.class, pressedComponent);

		// Does the dock has dockables docked in it?
		if (ancestorDock.getDockableCount() > 0) {
			// We can start dragging.
			originDock = (LeafDock) ancestorDock;

			// Calculate the dockable offset.
			dockableOffset.setLocation(x, y);
			dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, (Component) originDock);

			// Create the composite dockable.
			Dockable[] dockables = new Dockable[originDock.getDockableCount()];
			for (int index = 0; index < originDock.getDockableCount(); index++) {
				dockables[index] = originDock.getDockable(index);
			}
			draggedDockable = new DefaultCompositeDockable(dockables, -1);
			draggedDockable.setState(DockableState.NORMAL, originDock);

			// Make sure the offset is not larger than the dockable size.
			Dimension size = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) draggedDockable, DockingMode.TAB);
			if (dockableOffset.x > size.getWidth()) {
				dockableOffset.x = (int) (Math.round(size.getWidth()));
			}
			if (dockableOffset.y > size.getHeight()) {
				dockableOffset.y = (int) (Math.round(size.getHeight()));
			}

			// We can drag.
			return true;
		}

		// We can not drag.
		return false;

	}

	/**
	 * Searches the dock where the dockable will be docked for the current mouse location.
	 * A rectangle is painted that shows, where the dockable will be docked.
	 * The cursor shows, if we can dock, or if we cannot dock for the current location.
	 */
	public void drag(MouseEvent mouseEvent) {

		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component) mouseEvent.getSource();

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Get the destination dock for this position.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null) {
			// We have no destination dock any more. 
			dockableDragPainter.paintDockableDrag(draggedDockable, null, null, screenLocation);

			// Set the 'cannot dock' cursor.
			cursorManager.setCursor(mouseComponent, retrieveCanNotDockCursor());

			return;
		}
		Dock destinationDock = destinationDocks[0];

		// Do we have a destination dock?
		if (destinationDock != null) {

			// Does the destination dock inherit from java.awt.Component or is it the float dock?
			if (destinationDock instanceof Component) {
				// Get the docking rectangle from the destination dock.
				locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
				SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
				destinationDock.retrieveDockingRectangle(draggedDockable, locationInDestinationDock, dockableOffset, dockableDragRectangle);

				// Paint the new rectangle.
				dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, dockableDragRectangle, locationInDestinationDock);

				// Set the 'can dock' cursor.
				cursorManager.setCursor((Component) destinationDock, retrieveCanDockCursor());

			} else if (destinationDock instanceof FloatDock) {

				// Are we in the special situation that we will move a child dock of the float dock?
				boolean move = false;

				// Get the root dock and the dock under the root.
				Dock rootDock = originDock;
				Dock dockUnderRoot = null;
				while (rootDock.getParentDock() != null) {
					dockUnderRoot = rootDock;
					rootDock = rootDock.getParentDock();
				}

				// Is the root dock the float dock?
				if (rootDock instanceof FloatDock) {
					// Is the dockable already in this dock and are there no others?
					List childrenOfDockable = new ArrayList();
					List childrenOfDock = new ArrayList();
					DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
					DockingUtil.retrieveDockables(dockUnderRoot, childrenOfDock);
					if (sameElements(childrenOfDockable, childrenOfDock)) {
						move = true;
					}
				}

				// We cannot paint on the screen, but maybe we can paint in the pane of the origin dock.
				if (originDock instanceof Component) {
//					if (move)
//					{
//						locationInDestinationDock.setLocation(screenLocation.x - dockableOffset.x, screenLocation.y - dockableOffset.y);
//						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component)originDock);
//						dockableDragRectangle.setLocation(locationInDestinationDock);
//						Window window = SwingUtilities.getWindowAncestor((Component)dockUnderRoot);
//						dockableDragRectangle.setSize(window.getSize());
//						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
//					}
//					else
//					{
//						// Get the docking rectangle for the destination float dock.
//						destinationDock.retrieveDockingRectangle(draggedDockable, screenLocation, dockableOffset, dockableDragRectangle);
//	
//						// Convert this rectangle to the origindock.
//						locationInDestinationDock.setLocation(dockableDragRectangle.x, dockableDragRectangle.y);
//						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component)originDock);
//						dockableDragRectangle.setLocation(locationInDestinationDock);
//						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
//					}
//	
//					// Paint the new rectangle.
//					dockableDragPainter.paintDockableDrag(draggedDockable, originDock, dockableDragRectangle, locationInDestinationDock);

					// We cannot paint on the screen, but maybe we can paint in the owner of the screen dock.
					if (move) {
						locationInDestinationDock.setLocation(screenLocation.x - dockableOffset.x, screenLocation.y - dockableOffset.y);
						//SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
						dockableDragRectangle.setLocation(locationInDestinationDock);
						Window window = SwingUtilities.getWindowAncestor((Component) dockUnderRoot);
						dockableDragRectangle.setSize(window.getSize());
						//locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
					} else {
						// Get the docking rectangle for the destination float dock.
						destinationDock.retrieveDockingRectangle(draggedDockable, screenLocation, dockableOffset, dockableDragRectangle);

						// Convert this rectangle to the origindock.
						locationInDestinationDock.setLocation(dockableDragRectangle.x, dockableDragRectangle.y);
						//SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) originDock);
						dockableDragRectangle.setLocation(locationInDestinationDock);
						locationInDestinationDock.setLocation(locationInDestinationDock.x + dockableOffset.x, locationInDestinationDock.y + dockableOffset.y);
					}

					// Paint the new rectangle.
					dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, dockableDragRectangle, locationInDestinationDock);

				} else {
					// We have a dock where we cannot paint. 
					dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, null, screenLocation);
				}

				// Set the 'can dock' cursor.
				cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());
			} else {
				// Currently this should not happen. All docks, except the float dock inherit from java.awt.Component.
				// We have a dock where we cannot paint. 
				dockableDragPainter.paintDockableDrag(draggedDockable, destinationDock, null, screenLocation);

				// Set the 'can dock' cursor.
				cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());
			}

		} else {
			// We have no destination dock any more. 
			dockableDragPainter.paintDockableDrag(draggedDockable, null, null, screenLocation);

			// Set the 'cannot dock' cursor.
			cursorManager.setCursor(mouseComponent, retrieveCanNotDockCursor());
		}

	}

	/**
	 * Resets the cursor and other fields. Cleans up what was painted before.
	 * Leaves the dragged dockable in its origin dock.
	 */
	public void cancelDragging(MouseEvent mouseEvent) {

		// Reset the old cursor.
		cursorManager.resetCursor();

		// Clean up what was painted for dragging.
		clearPainting();

		// No dragging anymore.
		reset();

	}

	/**
	 * Resets the cursor. Cleans up what was painted before.
	 * Searches a destination dock for the last mouse location and tries to dock the dragged dockable in
	 * this dock.
	 */
	public void stopDragging(MouseEvent mouseEvent) {

		// Reset the old cursor.
		cursorManager.resetCursor();

		// Clear what we painted.
		clearPainting();

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Get the destination dock.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null) {
			return;
		}
		Dock destinationDock = destinationDocks[0];

		// Is the destination dock different from the origin?
		if ((destinationDock != null) && (!destinationDock.equals(originDock))) {
			// Get the mouse location for the new dock.
			locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
			if (destinationDock instanceof Component) {
				SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
			}

			// Check if we can move the dock of the dockable in the float dock.
			if (destinationDock instanceof FloatDock) {
				// Get the root dock and the dock under the root.
				Dock rootDock = originDock;
				Dock dockUnderRoot = null;
				while (rootDock.getParentDock() != null) {
					dockUnderRoot = rootDock;
					rootDock = rootDock.getParentDock();
				}

				// Is the root dock the float dock?
				if (rootDock instanceof FloatDock) {
					// Is the dockable already in this dock and are there no others?
					List childrenOfDockable = new ArrayList();
					List childrenOfDock = new ArrayList();
					DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
					DockingUtil.retrieveDockables(dockUnderRoot, childrenOfDock);
					if (sameElements(childrenOfDockable, childrenOfDock)) {
						((FloatDock) rootDock).moveDock(dockUnderRoot, locationInDestinationDock, dockableOffset);
						return;
					}
				}
			}

			// Remove the dockable from the old dock, add to the new dock.
			// Use the docking manager for the addition and removal, because the listenenrs have to informed.
			if (!originDock.equals(draggedDockable.getDock())) {
				throw new IllegalStateException("The origin dock is not the parent of the dockable.");
			}
			DockingManager.getDockingExecutor().changeDocking(draggedDockable, destinationDock, locationInDestinationDock, dockableOffset);

			// Clean the dock from which the dockable is removed.
			DockingManager.getDockingExecutor().cleanDock(originDock, false);
		}

		// No dragging anymore.
		reset();
	}

	public void showPopupMenu(MouseEvent mouseEvent) {

		// Get the component of the mouse event.  
		Component mouseComponent = (Component) mouseEvent.getSource();

		// Get the origin dock.
		originDock = (LeafDock) SwingUtilities.getAncestorOfClass(LeafDock.class, mouseComponent);
		if (originDock != null) {
			// Normally this dock should have only one dockable (f.e. SingleDock), but we don't control this.
			// We take the first dockable.
			if (originDock.getDockableCount() == 1) {
				// Get the dockable
				Dockable clickedDockable = originDock.getDockable(0);
				JPopupMenu popupMenu = DockingManager.getComponentFactory().createPopupMenu(clickedDockable, null);

				// Show the popup menu.
				if (popupMenu != null) {
					popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			} else {
				Dockable[] dockableArray = new Dockable[originDock.getDockableCount()];
				for (int index = 0; index < originDock.getDockableCount(); index++) {
					dockableArray[index] = originDock.getDockable(index);
				}
				CompositeDockable compositeDockable = new DefaultCompositeDockable(dockableArray);
				JPopupMenu popupMenu = DockingManager.getComponentFactory().createPopupMenu(null, compositeDockable);

				// Show the popup menu.
				if (popupMenu != null) {
					popupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
				}
			}
		}

	}

	// Getters / Setters.

	/**
	 * Gets the painter for painting the dragged dockables.
	 *
	 * @return dockableDragPainter The painter for painting the dragged dockables.
	 */
	public DockableDragPainter getDockableDragPainter() {
		return dockableDragPainter;
	}

	/**
	 * Sets the painter for painting the dragged dockables.
	 *
	 * @param dockableDragPainter The painter for painting the dragged dockables.
	 */
	public void setDockableDragPainter(DockableDragPainter dockableDragPainter) {
		this.dockableDragPainter = dockableDragPainter;
	}

	// Protected methods.

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 *
	 * @return The cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 */
	protected Cursor retrieveCanDockCursor() {
		return DockingManager.getCanDockCursor();
	}

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable cannot be docked in an underlying dock.
	 *
	 * @return The cursor that is used for dragging a dockable,
	 * when the dockable cannot be docked in an underlying dock.
	 */
	protected Cursor retrieveCanNotDockCursor() {
		return DockingManager.getCanNotDockCursor();
	}

	// Private metods.

	/**
	 * Resets to the state when there is no dragging. All the fields are set to null.
	 */
	private void reset() {
		originDock = null;
		draggedDockable = null;
		dockableOffset = new Point();
	}

	/**
	 * Clears up what was painted before.
	 */
	private void clearPainting() {
		dockableDragPainter.clear();
	}

	/**
	 * Computes the location in screen coordinates of the current mouse position.
	 *
	 * @param mouseEvent The mouse event that contains information about the current location of the mouse.
	 */
	private void computeScreenLocation(MouseEvent mouseEvent) {
		screenLocation.setLocation(mouseEvent.getX(), mouseEvent.getY());
		SwingUtilities.convertPointToScreen(screenLocation, (Component) mouseEvent.getSource());
	}

	/**
	 * Determines if the given lists contain the same elements. We suppose that all the elements of the given lists
	 * are different.
	 *
	 * @param firstList  The first list.
	 * @param secondList The second list.
	 * @return True if the given lists contain the same elements, false otherwise.
	 */
	private boolean sameElements(List firstList, List secondList) {

		// The size hould be the same, otherwise stop.
		if (firstList.size() != secondList.size()) {
			return false;
		}

		// Iterate over the elements of the first list.
		for (int index = 0; index < firstList.size(); index++) {
			// Check if the element is also in the second list.
			if (!secondList.contains(firstList.get(index))) {
				return false;
			}
		}

		// They heve the same elements.
		return true;
	}

}
