package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dockable.*;
import com.javadocking.drag.dockretriever.DockRetriever;
import com.javadocking.drag.dockretriever.DynamicDockRetriever;
import com.javadocking.util.CollectionUtil;
import com.javadocking.util.DockingUtil;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a class for dragging all the dockables in a {@link com.javadocking.dock.LeafDock} dynamically.
 * The dockables will be removed from the old dock and
 * placed in a new dock while the user is dragging.
 * </p>
 * <p>
 * If there is only one {@link Dockable} in the leaf dock, then the dockable is dragged,
 * else a {@link com.javadocking.dockable.CompositeDockable} is created with all the dockables of the leaf dock.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Dock}s that are used in the application should inherit
 * from the java.awt.Component class.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DynamicDragger implements Dragger {

	// Static fields.

	private static final boolean TEST = false;
	/**
	 * Floating starts only this delay after the dockable wants to float.
	 */
	private static final int FLOAT_DELAY = 150;
	/**
	 * The value of floatingDelay, when the dockable does not want to float.
	 */
	private static final int NO_FLOATING = 0;
	/**
	 * The value of floatingDelay, when the dockable wants to float, but we are still in a wait mode.
	 */
	private static final int START_FLOATING = 1;
	/**
	 * The value of floatingDelay when the dockable can really start floating.
	 */
	private static final int FLOATING = 2;


	// Fields.

	// For docking.

	/**
	 * The dockRetriever.
	 */
	private DockRetriever dockRetriever = new DynamicDockRetriever();
	/**
	 * When dragging starts this is false. Once the dragged dockable is undocked and docked in another
	 * dock, or moved in its dock, undocked is set to true.
	 */
	private boolean undocked;
	/**
	 * When true the dragged dockable is currently floating alone in a child dock of the float dock.
	 */
	private boolean floating;
	/**
	 * When true the dragged dockable can start floating.
	 */
	private int floatingDelay = NO_FLOATING;
	/**
	 * The timer to delay the floaing.
	 */
	private Timer timer;
	/**
	 * The dock of the dockable before dragging.
	 */
	private LeafDock originDock;
	/**
	 * The dock where the dragged dockable was before.
	 */
	private LeafDock previousDock;
	/**
	 * True when the mouse has been outside the precious dock.
	 */
	private boolean mouseExitedPreviousDock;
	/**
	 * The rectangle of the previous dock in screen coordinates.
	 */
	private Rectangle previousDockRectangle = new Rectangle();
	/**
	 * The dock, where the dragged dockable is currently docked.
	 */
	private LeafDock currentDock;
	/**
	 * The root dock of the dock, where the dragged dockable is currently docked.
	 */
	private Dock currentRootDock;
	/**
	 * The child of the root dock of the dock, where the dragged dockable is currently docked.
	 */
	private Dock currentChildOfRootDock;
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
	 * The rectangle where the dockable will be docked for the current mouse location.
	 */
	private Rectangle dockableDragRectangle = new Rectangle();
	/**
	 * The offset of the clicked point.
	 */
	private Point dockableOffset = new Point();
	/**
	 * A point that is used in calculations.
	 */
	private Point helpPoint = new Point();
	/**
	 * The dockable that is dragged. It can be a composite.
	 */
	private Dockable draggedDockable;
	/**
	 * The only dockable that is dragged by this dragger.
	 */
	private Dockable fixedDockable;
	/**
	 * True when the dockable is removed already at least one time from a dock during dragging.
	 */
	private boolean firstRemoved;
	/**
	 * This field contains the dock that contains ghosts while dragging is performed.
	 * When dragging is finished, these ghosts should be removed.
	 */
	private CompositeDock dockWithGhost;
	/**
	 * These are the single docks that canhave ghosts.
	 */
	private Set singleDocksWithGhosts = new HashSet();

	// Cursors.
	/**
	 * Manages the cursors used for dragging dockables.
	 */
	private DragCursorManager cursorManager = new DragCursorManager();

	// Constructors.

	/**
	 * Constructs a dynamic dragger for the dockables of a dock.
	 */
	public DynamicDragger() {
	}

	/**
	 * Constructs a dynamic dragger for the given dockable.
	 *
	 * @param    fixedDockable        The only dockable that is dragged by this dragger.
	 */
	public DynamicDragger(Dockable fixedDockable) {
		if (fixedDockable == null) {
			throw new IllegalArgumentException("Dockable null");
		}
		this.fixedDockable = fixedDockable;
	}

	// Implementations of Dragger.

	public boolean startDragging(MouseEvent mouseEvent) {

		// Get the mouse position and the component. 
		Component mouseComponent = (Component) mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Reset the fields.
		reset();

		// Is there a deepest leaf dock?
		Component pressedComponent = SwingUtilities.getDeepestComponentAt(mouseComponent, x, y);
		LeafDock ancestorDock = (LeafDock) SwingUtilities.getAncestorOfClass(LeafDock.class, pressedComponent);

		// Does the dock has dockables docked in it?
		if (ancestorDock.getDockableCount() > 0) {
			if (fixedDockable == null) {
				// We can start dragging.
				originDock = (LeafDock) ancestorDock;
				int count = originDock.getDockableCount();
				Dimension dockableSize = null;
				if (count > 0) {
					// Calculate the dockable offset.
					dockableOffset.setLocation(x, y);
					dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, (Component) originDock);

					if (count == 1) {
						if (TEST) System.out.println("one dockable");
						draggedDockable = originDock.getDockable(0);
						dockableSize = draggedDockable.getContent().getPreferredSize();
					} else {
						if (TEST) System.out.println("multiple dockables");
						// Create the composite dockable.
						Dockable[] dockables = new Dockable[originDock.getDockableCount()];
						for (int index = 0; index < originDock.getDockableCount(); index++) {
							dockables[index] = originDock.getDockable(index);
						}
						draggedDockable = new DefaultCompositeDockable(dockables, -1);
						dockableSize = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) draggedDockable, DockingMode.TAB);
						draggedDockable.setDock(originDock);
						draggedDockable.setState(DockableState.NORMAL, originDock);
					}

					// Make sure the offset is not larger than the dockable size.
					if (dockableOffset.x > dockableSize.getWidth()) {
						dockableOffset.x = (int) (Math.round(dockableSize.getWidth()));
					}
					if (dockableOffset.y > dockableSize.getHeight()) {
						dockableOffset.y = (int) (Math.round(dockableSize.getHeight()));
					}


					// Set the 'can dock' cursor.
					cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

					// We can drag.
					return true;
				}
			} else {
				// Control that the dragged dockable belongs to this dock.
				originDock = (LeafDock) ancestorDock;
				if (originDock.containsDockable(fixedDockable)) {
					draggedDockable = fixedDockable;

					// Calculate the dockable offset.
					dockableOffset.setLocation(x, y);
					dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, draggedDockable.getContent());

					// Make sure the offset is not larger than the dockable size.
					Dimension dockableSize = draggedDockable.getContent().getPreferredSize();
					if (dockableOffset.x > dockableSize.getWidth()) {
						dockableOffset.x = (int) (Math.round(dockableSize.getWidth()));
					}
					if (dockableOffset.y > dockableSize.getHeight()) {
						dockableOffset.y = (int) (Math.round(dockableSize.getHeight()));
					}
					if (TEST) System.out.println("offset " + dockableOffset.x + "    " + dockableOffset.y);


					// Set the 'can dock' cursor.
					cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

					// We can drag.
					return true;
				}
			}
		}


		// We can not drag.
		return false;

	}

	/**
	 * Searches the dock, where the dockable can be docked for the current mouse location.
	 * The dockable is docked immediately in this location.
	 * If we cannot dock for the current location, the 'cannot dock' cursor is shown.
	 */
	public void drag(MouseEvent mouseEvent) {

		if (TEST) System.out.println("drag");

		// Do we still have a dockable to drag.
		// It can be null, when we forced the dragging to stop.
		if (draggedDockable == null) {
			return;
		}

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Do we have to move an externalized dockable?
		if (draggedDockable.getState() == DockableState.EXTERNALIZED) {
			// Move the dockable.
			ExternalizedDraggerSupport.moveExternalizedDockable(draggedDockable, screenLocation, dockableOffset);
			return;
		}

		// Did the mouse went outside the previous dock?
		if (!mouseExitedPreviousDock) {
			if (!previousDockRectangle.contains(screenLocation)) {
				if (TEST) System.out.println("mouse exited previous dock");
				mouseExitedPreviousDock = true;
			}
		}

		// Get the dock where the dockable is docked now.
		currentDock = draggedDockable.getDock();

		// Test that we have a current dock that is not null.
		// It can become null, when the different dockables of a composite dockable were docked in different docks.
		if (currentDock == null) {
			stopDragging(mouseEvent);
			return;
		}

		// Get the destination dock for this position.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null) {
			return;
		}
		Dock destinationDock = destinationDocks[0];
		if (TEST) System.out.println("drag destination " + destinationDock);
		// Is the destination dock not null?
		if (destinationDock != null) {
			// Is the destination dock different from the origin?
			if (!destinationDock.equals(currentDock)) {
				if (TEST) System.out.println("   different destination");

				// Is the dockable floating?
				floating = isFloating();

				// Determine if we really want to change the dock of the dockable.
				// We don't want the dockable flipping between docks.
				boolean changeDock = changeDock(mouseEvent, destinationDock);
				boolean moveInFloat = false;
				if (!changeDock) {
					if (destinationDocks.length > 1) {

						destinationDock = destinationDocks[1];
						if (!destinationDock.equals(currentDock)) {
							changeDock = changeDock(mouseEvent, destinationDock);
						} else {
							if (floating) {
								changeDock = true;
								moveInFloat = true;
							}
						}
					} else {
						if (floating) {
							changeDock = true;
							moveInFloat = true;
						}
					}
				}

				if (changeDock) {
					// Change the destination dock if we have to move in the float dock.
					if (moveInFloat) {
						destinationDock = currentRootDock;
					}

					// Get the mouse location for the new dock.
					locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
					if (destinationDock instanceof Component) {
						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
					}

					// Check if we can move the dock of the dockable in the float dock.
					if (destinationDock instanceof FloatDock) {
						if (floating) {
							if (TEST)
								System.out.println("floating screenLocation " + screenLocation.x + "   " + screenLocation.y);
							((FloatDock) currentRootDock).moveDock(currentChildOfRootDock, locationInDestinationDock, dockableOffset);
							undocked = true;
							floatingDelay = NO_FLOATING;
							return;
						}
						if (floatingDelay == NO_FLOATING) {
							// We could start dragging.
							floatingDelay = START_FLOATING;
							startFloatDelay();
							return;
						} else if (floatingDelay == START_FLOATING) {
							// We are waiting to really float.
							return;
						}
					} else {
						floatingDelay = NO_FLOATING;
					}

					// Set the previous dock.
					previousDock = currentDock;
					if (!floating) {
						Component previousDockComponent = (Component) previousDock;
						previousDockRectangle.setSize(previousDockComponent.getSize());
						previousDockRectangle.setLocation(previousDockComponent.getLocationOnScreen());
						if (TEST) {
							System.out.println("previous rectangle:  " +
									previousDockRectangle.getLocation().x + "   " +
									previousDockRectangle.getLocation().y + "   " +
									previousDockRectangle.width + "   " +
									previousDockRectangle.height);
						}
						mouseExitedPreviousDock = false;
					} else {
						mouseExitedPreviousDock = true;
					}

					// Remove the dockable from the old dock, add to the new dock.
					// Use the docking manager for the addition and removal, because the listenenrs have to informed.
					if (!currentDock.equals(draggedDockable.getDock())) {
						throw new IllegalStateException("The origin dock is not the parent of the dockable.");
					}
					DockingManager.getDockingExecutor().changeDocking(draggedDockable, destinationDock, locationInDestinationDock, dockableOffset);
					undocked = true;

					// The current dock can become a singl dock. In that case it will contain ghosts.
					if (currentDock instanceof SingleDock) {
						singleDocksWithGhosts.add(currentDock);
					}

					// Clean the dock from which the dockable is removed.
					if (TEST) System.out.println("current dock empty");
					if (firstRemoved) {
						// The dockable was already removed from a dock. We don't need to keep ghosts.
						DockingManager.getDockingExecutor().cleanDock(currentDock, false);
					} else {
						// The origin dock may not be removed. There can still be listeners on this component.
						// There can be ghosts on the dock.
						dockWithGhost = DockingManager.getDockingExecutor().cleanDock(currentDock, true);
					}
					firstRemoved = true;

				}
			} else {
				if (TEST) System.out.println("   destination is current dock");
				// Can we move the dockable in the dock?
				if (!(draggedDockable instanceof CompositeDockable)) {
					if (TEST) System.out.println("   try to move dockable");
					// Get the mouse location for the new dock.
					locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
					if (destinationDock instanceof Component) {
						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
					}

					// Use the docking manager for the move, because the listeners have to informed.				
					DockingManager.getDockingExecutor().changeDocking(draggedDockable, destinationDock, locationInDestinationDock, new Point(0, 0));
				}
			}
		}

	}

	/**
	 * It is not possible to cancel previous changes.
	 * The dockable remains, where it is.
	 * The dragging process is only stopped.
	 */
	public void cancelDragging(MouseEvent mouseEvent) {
		stopDragging(mouseEvent);
	}

	public void stopDragging(MouseEvent mouseEvent) {

		// Reset the old cursor.
		cursorManager.resetCursor();

		// Clear the ghost docks.
		if (dockWithGhost != null) {
			dockWithGhost.clearGhosts();
		}
		Iterator iterator = singleDocksWithGhosts.iterator();
		while (iterator.hasNext()) {
			((SingleDock) iterator.next()).clearGhosts();
		}
//		if (originDock instanceof SingleDock)
//		{
//			((SingleDock)originDock).clearGhosts();
//		}

		// Reset dragging fields.
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

	// Private metods.

	/**
	 * Resets to the state when there is no dragging. All the fields are set to null.
	 */
	private void reset() {
		undocked = false;
		originDock = null;
		previousDock = null;
		draggedDockable = null;
	}

	/**
	 * Computes the location in screen coordinates of the current mouse position.
	 *
	 * @param mouseEvent The mouse event that contains information about the current location of the mouse.
	 */
	private void computeScreenLocation(MouseEvent mouseEvent) {
		screenLocation.setLocation(mouseEvent.getX(), mouseEvent.getY());
		SwingUtilities.convertPointToScreen(screenLocation, (Component) mouseEvent.getSource());

//		Point locinscr = ((Component)mouseEvent.getSource()).getLocationOnScreen();
//		System.out.println("      mouse ev loc " + mouseEvent.getX() + "   " +  mouseEvent.getY());
//		System.out.println("      source   loc " + locinscr.x + "   " +  locinscr.y);
//		System.out.println("      screen   loc " + screenLocation.x + "   " +  screenLocation.y);

	}

	/**
	 * Determines if the dragged dockable is currently floating.
	 * It is floating, when its root dock is a {@link FloatDock} and
	 * if the dragged dockable is the only dockable in the child docks of the float dock.
	 *
	 * @return True if the dragged dockable is currently floating, false otherwise.
	 */
	private boolean isFloating() {

		// Get the root dock and the dock under the root.
		currentRootDock = draggedDockable.getDock();
		currentChildOfRootDock = null;
		while (currentRootDock.getParentDock() != null) {
			currentChildOfRootDock = currentRootDock;
			currentRootDock = currentRootDock.getParentDock();
		}

		// Is the root dock the float dock?
		if (currentRootDock instanceof FloatDock) {
			// Is the dockable already in this dock and are there no others?
			List childrenOfDockable = new ArrayList();
			List childrenOfDock = new ArrayList();
			DockingUtil.retrieveDockables(draggedDockable, childrenOfDockable);
			DockingUtil.retrieveDockables(currentChildOfRootDock, childrenOfDock);
			if (CollectionUtil.sameElements(childrenOfDockable, childrenOfDock)) {
				return true;
			}
		}

		return false;

	}

	private boolean changeDock(MouseEvent mouseEvent, Dock destinationDock) {
		if (TEST) System.out.println("   different destination");

		// Determine if we really want to change the dock of the dockable.
		// We don't want the dockable flipping between docks.
		boolean changeDock = false;

		// If the dockable has not already changed dock, then it's OK to change.
		if (!undocked) {
			if (TEST) System.out.println("   first undock");
			changeDock = true;
		} else {
			if (floating) {
				if (TEST) System.out.println("   floating");
				// The dockable is allowed to move in the float dock.
				if (destinationDock instanceof FloatDock) {
					if (TEST) System.out.println("      move in float dock");
					changeDock = true;
				} else {
					// The dockable is allowed to dock in a dock that was not the previous one.
					if (!destinationDock.equals(previousDock)) {
						if (TEST) System.out.println("      dock not in previous");
						// But only if the mouse is in the docking rectangle.
						// Get the docking rectangle for the destination dock.
						locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
						destinationDock.retrieveDockingRectangle(draggedDockable, locationInDestinationDock, dockableOffset, dockableDragRectangle);
						if (dockableDragRectangle.contains(locationInDestinationDock)) {
							if (TEST) System.out.println("      dock because mouse is inside docking rectangle");
							changeDock = true;
						} else {
							if (TEST) System.out.println("      not dock because mouse is outside docking rectangle");
						}
					} else {
						if (TEST) {
							if (mouseExitedPreviousDock) {
								System.out.println("      dock because was outside previous dock");
							} else {
								System.out.println("      no dock because was not outside previous dock");
							}
						}
						changeDock = mouseExitedPreviousDock;
					}
				}
			} else {
				if (TEST) System.out.println("   docked normal (not floating)");
				if (destinationDock instanceof FloatDock) {
					// Is the mouse outside the current dock?
					Component currentDockComponent = (Component) currentDock;
					helpPoint.setLocation(screenLocation.x, screenLocation.y);
					SwingUtilities.convertPointFromScreen(helpPoint, currentDockComponent);
					changeDock = !currentDockComponent.contains(helpPoint);
					if (TEST) {
						if (changeDock) {
							System.out.println("      go float, because mouse outside current dock");
						} else {
							System.out.println("      no float, because mouse inside current dock");
						}
					}
				} else {
					if (TEST) System.out.println("      dock in another dock");
					changeDock = true;
				}
			}
		}

		return changeDock;

	}

	/**
	 * The delay for floating is passed. If we still want to float,
	 * then the floatingDelay is set to FLOATING.
	 */
	private void floatDelayFinished() {
		timer = null;
		if (floatingDelay == START_FLOATING) {
			floatingDelay = FLOATING;
		}
	}

	/**
	 * Starts the delay for dragging.
	 */
	private void startFloatDelay() {

		ActionListener taskPerformer = evt -> floatDelayFinished();
		timer = new Timer(FLOAT_DELAY, taskPerformer);
		timer.setRepeats(false);
		timer.start();

	}

}
