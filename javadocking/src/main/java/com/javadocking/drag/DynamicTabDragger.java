package com.javadocking.drag;

import com.javadocking.DockingManager;
import com.javadocking.dock.*;
import com.javadocking.dockable.*;
import com.javadocking.drag.dockretriever.DockRetriever;
import com.javadocking.drag.dockretriever.DynamicDockRetriever;
import com.javadocking.util.CollectionUtil;
import com.javadocking.util.DockingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is a class for dragging one {@link com.javadocking.dockable.Dockable} or all the dockables in
 * a {@link com.javadocking.dock.TabDock} dynamically.  The dockables will be removed from the old dock and
 * placed in a new dock while the user is dragging.
 * </p>
 * <p>
 * One dockable can be dragged by dragging the tab. All the dockables
 * can be dragged by dragging another part of the tabbed pane. A {@link com.javadocking.dockable.CompositeDockable}
 * is created with the dockables of the javax.swing.JTabbedPane.
 * </p>
 * <p>
 * The {@link com.javadocking.dock.Dock}s that are used in the application should inherit
 * from the java.awt.Component class.
 * </p>
 * <p>
 * With this class a tab of the javax.swing.JTabbedPane can also be dragged. This happens, when one tab is dragged,
 * and the mouse is over the header of the JTabbedPane.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DynamicTabDragger implements Dragger {

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

	/**
	 * The dockRetriever.
	 */
	@NotNull
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
	@Nullable
	private Timer timer;
	/**
	 * The origin dock of the dragged dockable.
	 */
	@Nullable
	private TabDock originDock;
	/**
	 * The dock where the dragged dockable was before.
	 */
	@Nullable
	private LeafDock previousDock;
	/**
	 * True when the mouse has been outside the precious dock.
	 */
	private boolean mouseExitedPreviousDock;
	/**
	 * The rectangle of the previous dock in screen coordinates.
	 */
	@NotNull
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
	@Nullable
	private Dock currentChildOfRootDock;
	/**
	 * The current location of the mouse in screen coordinates.
	 */
	@NotNull
	private Point screenLocation = new Point();
	/**
	 * This is the location of the mouse in the dock where the dockable will be docked for the current mouse location.
	 * We keep it as field because we don't want to create every time a new point.
	 */
	@NotNull
	private Point locationInDestinationDock = new Point();
	/**
	 * This is the current location of the mouse in the tabbed pane.
	 * We keep it as field because we don't want to create every time a new point.
	 */
	@NotNull
	private Point currentOffsetInTabbedPane = new Point();
	/**
	 * The rectangle where the dockable will be docked for the current mouse location.
	 */
	@NotNull
	private Rectangle dockableDragRectangle = new Rectangle();
	/**
	 * The offset of the clicked point.
	 */
	@NotNull
	private Point dockableOffset = new Point();
	/**
	 * A point that is used in calculations.
	 */
	@NotNull
	private Point helpPoint = new Point();
	/**
	 * The dockable that is dragged.
	 */
	@Nullable
	private Dockable draggedDockable;
	/**
	 * True when the dockable is removed already at least one time from a dock during dragging.
	 */
	private boolean firstRemoved;
	/**
	 * This field contains the dock that contains ghosts while dragging is performed.
	 * When dragging is finished, these ghosts should be removed.
	 */
	@Nullable
	private CompositeDock dockWithGhost;
	/**
	 * These are the single docks that canhave ghosts.
	 */
	@NotNull
	private Set singleDocksWithGhosts = new HashSet();

	// Tabs.
	/**
	 * The tabbed pane that contained the dragged dockable.
	 */
	private JTabbedPane sourceTabbedPane;

	// Cursors.
	/**
	 * Manages the cursors used for dragging dockables.
	 */
	@NotNull
	private DragCursorManager cursorManager = new DragCursorManager();


	// Implementations of Dragger.

	public boolean startDragging(@NotNull MouseEvent mouseEvent) {

		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component) mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Reset the fields.
		reset();

		// Is the mouse component a JTabbedPane?
		if (mouseComponent instanceof JTabbedPane) {
			// Is the ancestor component a TabbedDock?
			Component ancestorComponent = SwingUtilities.getAncestorOfClass(Component.class, mouseComponent);
			if (ancestorComponent instanceof TabDock) {
				// Does the dock has dockables docked in it?
				TabDock tabDock = (TabDock) ancestorComponent;
				if (tabDock.getDockableCount() > 0) {
					// We can start dragging.
					originDock = (TabDock) ancestorComponent;
					sourceTabbedPane = (JTabbedPane) mouseComponent;
					LeafDock originDock = (LeafDock) SwingUtilities.getAncestorOfClass(LeafDock.class, sourceTabbedPane);

					// Calculate the dockable offset.
					dockableOffset.setLocation(x, y);
					dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, sourceTabbedPane);

					// Get the selected tab and its dockable.
					/**
					 * The index of the tab that contains the dragged dockable, when the dragged dockable is not
					 * a composite dockable.
					 */
					final int oldTabIndex = sourceTabbedPane.indexAtLocation(dockableOffset.x, dockableOffset.y);
					if (oldTabIndex >= 0) {
						// One tab is selected. The dockable that is docked in the tab will be dragged.
						Component tabComponent = sourceTabbedPane.getComponentAt(oldTabIndex);
						draggedDockable = tabDock.retrieveDockableOfComponent(tabComponent);
						if (draggedDockable != null) {
							if (TEST) System.out.println("one dockable in tab");

							// Make sure the offset is not larger than the dockable size.
							Dimension size = draggedDockable.getContent().getPreferredSize();
							if (dockableOffset.x > size.getWidth()) {
								dockableOffset.x = (int) (Math.round(size.getWidth()));
							}
							if (dockableOffset.y > size.getHeight()) {
								dockableOffset.y = (int) (Math.round(size.getHeight()));
							}

							// Set the 'can dock' cursor.
							cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

							// We have a dockable to drag.
							return true;
						}
					} else {
						// No tab is selected.

						// Are there any tabs?
						if (sourceTabbedPane.getTabCount() <= 0) {
							return false;
						}

						// Check if the component of the selected dockable is not clicked.
						Component selectedComponent = sourceTabbedPane.getSelectedComponent();
						Point helpPoint = new Point(mouseEvent.getX(), mouseEvent.getY());
						helpPoint = SwingUtilities.convertPoint(mouseComponent, helpPoint, selectedComponent);
						if (selectedComponent.contains(helpPoint)) {
							return false;
						}

						// A composite dockable is created with the dockables that are docked in the different tab.
						// This composite dockable is dragged.
						// Create the composite dockable.
						Dockable[] dockables = new Dockable[sourceTabbedPane.getTabCount()];
						for (int index = 0; index < sourceTabbedPane.getTabCount(); index++) {
							dockables[index] = tabDock.retrieveDockableOfComponent(sourceTabbedPane.getComponentAt(index));
							if (dockables[index] == null) {
								return false;
							}
						}
						draggedDockable = new DefaultCompositeDockable(dockables, sourceTabbedPane.getSelectedIndex());
						draggedDockable.setState(DockableState.NORMAL, originDock);
						if (TEST) System.out.println("multiple dockables of tabs");

						// Make sure the offset is not larger than the dockable size.
						Dimension size = DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) draggedDockable, DockingMode.TAB);
						if (dockableOffset.x > size.getWidth()) {
							dockableOffset.x = (int) (Math.round(size.getWidth()));
						}
						if (dockableOffset.y > size.getHeight()) {
							dockableOffset.y = (int) (Math.round(size.getHeight()));
						}


						// Set the 'can dock' cursor.
						cursorManager.setCursor(mouseComponent, retrieveCanDockCursor());

						// We have a dockable to drag.
						return true;
					}
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
	public void drag(@NotNull MouseEvent mouseEvent) {

		if (TEST) System.out.println("drag");

		// Do we still have a dockable to drag.
		// It can be null, when we forced the dragging to stop.
		if (draggedDockable == null) {
			return;
		}

		// Did the mouse went outside the previous dock?
		if (!mouseExitedPreviousDock) {
			if (!previousDockRectangle.contains(screenLocation)) {
				if (TEST) System.out.println("   mouse exited previous dock");
				mouseExitedPreviousDock = true;
			}
		}

		// Are we dragging a tab?
		int tabIndex = tabDragged(mouseEvent);
		if (tabIndex >= 0) {

			// Move the tabs.
			Point locationInOriginDock = new Point(mouseEvent.getPoint().x, mouseEvent.getPoint().y);
			locationInOriginDock = SwingUtilities.convertPoint((Component) mouseEvent.getSource(), locationInOriginDock, originDock);
			if (!originDock.equals(draggedDockable.getDock())) {
				throw new IllegalStateException("The origin dock is not the parent of the dockable.");
			}
			DockingManager.getDockingExecutor().changeDocking(draggedDockable, originDock, locationInOriginDock, new Point(0, 0));

			// Set the right titles in the tablabels. The JTabbedPane is not refreshing the titles by itself.
			resetTabTitles(sourceTabbedPane);

			if (TEST) System.out.println("Tab dragged");
			return;
		}

		// Get the mouse location in screen coordinates.
		computeScreenLocation(mouseEvent);

		// Get the destination dock.
		Dock[] destinationDocks = dockRetriever.retrieveHighestPriorityDock(screenLocation, draggedDockable);
		if (destinationDocks == null) {
			return;
		}
		Dock destinationDock = destinationDocks[0];

		// Get the dock where the dockable is docked now.
		currentDock = draggedDockable.getDock();

		// Test that we have a current dock that is not null.
		// It can become null, when the different dockables of a composite dockable were docked in different docks.
		if (currentDock == null) {
			stopDragging(mouseEvent);
			return;
		}

		// Is the destination dock not null?
		if (destinationDock != null) {

			// Is the destination dock different from the origin?
			if (!destinationDock.equals(currentDock)) {

				if (TEST) System.out.println("   different destination");

				// Is the dockable floating?
				floating = isFloating();
				if (TEST) System.out.println("   floating " + floating);

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
					if (TEST) System.out.println("change dock");

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
							if (TEST) System.out.println("move dock");
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
						previousDockRectangle.setLocation(previousDockComponent
								.getLocationOnScreen());
						if (TEST) {
							System.out.println("previous rectangle:  "
									+ previousDockRectangle.getLocation().x + "   "
									+ previousDockRectangle.getLocation().y + "   "
									+ previousDockRectangle.width + "   "
									+ previousDockRectangle.height);
						}
						mouseExitedPreviousDock = false;
					} else {
						mouseExitedPreviousDock = true;
					}

					// Remove the dockable from the old dock, add to the new
					// dock.
					// Use the docking manager for the addition and removal, because the listenenrs have to informed.
					if (!currentDock.equals(draggedDockable.getDock())) {
						throw new IllegalStateException("Here it is");
					}
					if (TEST) System.out.println("remove add dock");
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
				// Can we move the dockable in the dock?
				if (!(draggedDockable instanceof CompositeDockable)) {
					// Get the mouse location for the new dock.
					locationInDestinationDock.setLocation(screenLocation.x, screenLocation.y);
					if (destinationDock instanceof Component) {
						SwingUtilities.convertPointFromScreen(locationInDestinationDock, (Component) destinationDock);
					}

					// Use the docking manager for the move, because the listeners have to be informed.				
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

		// Clear the ghost docks.
		if (dockWithGhost != null) {
			dockWithGhost.clearGhosts();
		}
		for (Object singleDocksWithGhost : singleDocksWithGhosts) {
			((SingleDock) singleDocksWithGhost).clearGhosts();
		}

		// Reset dragging fields.
		reset();

	}

	public void showPopupMenu(@NotNull MouseEvent mouseEvent) {

		// Get the mouse position and the component of the mouse event. 
		Component mouseComponent = (Component) mouseEvent.getSource();
		int x = mouseEvent.getX();
		int y = mouseEvent.getY();

		// Is the deepest component a JTabbedPane?
		Component pressedComponent = SwingUtilities.getDeepestComponentAt(mouseComponent, x, y);
		JTabbedPane pressedTabbedPane;
		if (pressedComponent instanceof JTabbedPane) {
			pressedTabbedPane = (JTabbedPane) pressedComponent;
		} else {
			pressedTabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, pressedComponent);
		}
		if (pressedTabbedPane != null) {
			// Is the ancestor component a TabbedDock?
			Component ancestorComponent = SwingUtilities.getAncestorOfClass(Component.class, pressedTabbedPane);
			if (ancestorComponent instanceof TabDock) {
				TabDock clickedDock = (TabDock) ancestorComponent;

				// Calculate the dockable offset.
				dockableOffset.setLocation(x, y);
				dockableOffset = SwingUtilities.convertPoint(mouseComponent, dockableOffset, pressedTabbedPane);

				// Get the selected tab and its dockable.
				int oldTabIndex = pressedTabbedPane.indexAtLocation(dockableOffset.x, dockableOffset.y);
				// Create the popup menu.
				JPopupMenu popupMenu;
				Dockable clickedDockable = null;
				if (oldTabIndex >= 0) {
					Component tabComponent = pressedTabbedPane.getComponentAt(oldTabIndex);
					clickedDockable = clickedDock.retrieveDockableOfComponent(tabComponent);
				}
				Dockable[] dockableArray = new Dockable[clickedDock.getDockableCount()];
				int selectedIndex = -1;
				for (int index = 0; index < clickedDock.getDockableCount(); index++) {
					dockableArray[index] = clickedDock.getDockable(index);
					if (dockableArray[index].equals(clickedDockable)) {
						selectedIndex = index;
					}
				}
				CompositeDockable compositeDockable = new DefaultCompositeDockable(dockableArray, selectedIndex);
				popupMenu = DockingManager.getComponentFactory().createPopupMenu(clickedDockable, compositeDockable);

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
		firstRemoved = false;
		dockWithGhost = null;

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
	 * Determines if a tab is being dragged.
	 *
	 * @param mouseEvent The mouse event that contains information about the current location of the mouse.
	 * @return The index of the tab label that is under the mouse. When no tab is under the mouse
	 * -1 is returned.
	 */
	private int tabDragged(@NotNull MouseEvent mouseEvent) {

		// When we are already undocked, tab dragging is not possible anymore.
		if (undocked) {
			return -1;
		}

		// we can only drag a tab if one dockable is dragged.
		if (!(draggedDockable instanceof CompositeDockable) && (sourceTabbedPane != null)) {
			// Compute the location of the mouse in the tabbed pane.
			currentOffsetInTabbedPane.setLocation(mouseEvent.getX(), mouseEvent.getY());
			currentOffsetInTabbedPane = SwingUtilities.convertPoint((Component) mouseEvent.getSource(), currentOffsetInTabbedPane, sourceTabbedPane);

			// Return the index of the tabthat is under the mouse.
			return sourceTabbedPane.indexAtLocation(currentOffsetInTabbedPane.x, currentOffsetInTabbedPane.y);
		}

		// A composite dockable is dragged. No tab can be dragged.
		return -1;
	}

	/**
	 * Sets the titles of the tabs of the given tabbed pane. The components in the tabs are dockables.
	 *
	 * @param tabbedPane The tabbed pane that contains the components of dockables.
	 *                   The titles of this tabbed pane should be set.
	 */
	private void resetTabTitles(@NotNull JTabbedPane tabbedPane) {

		// Get the dock.
		TabDock originDock = (TabDock) SwingUtilities.getAncestorOfClass(TabDock.class, tabbedPane);

		// Iterate over the tabs.
		for (int index = 0; index < tabbedPane.getTabCount(); index++) {
			// Set the title of the tab.
			Component tabComponent = tabbedPane.getComponentAt(index);
			Dockable dockable = originDock.retrieveDockableOfComponent(tabComponent);
			tabbedPane.setTitleAt(index, dockable.getTitle());
			tabbedPane.setIconAt(index, dockable.getIcon());
		}

	}

	// Private methods.

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
			return CollectionUtil.sameElements(childrenOfDockable, childrenOfDock);
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
