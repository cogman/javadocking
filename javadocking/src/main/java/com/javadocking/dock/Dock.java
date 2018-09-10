package com.javadocking.dock;

import com.javadocking.dockable.Dockable;
import com.javadocking.event.DockingListener;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;


/**
 * <p>
 * A dock is where {@link com.javadocking.dockable.Dockable}s can be docked.
 * How a dockable is docked in the dock, depends on the location where the dockable is added.
 * </p>
 * <p>
 * There are docks that have child docks ({@link com.javadocking.dock.CompositeDock}s)
 * and there are docks that only contain dockables ({@link LeafDock}s).
 * The UI of an application contains tree structures of docks. Every dock
 * has a parent composite dock. The parent composite dock of the root dock is null.
 * </p>
 * <p>
 * Information on using docks is in
 * <a href="http://www.javadocking.com/developerguide/leafdock.html" target="_blank">How to Use Laef Docks</a> and
 * <a href="http://www.javadocking.com/developerguide/compositedock.html" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * A dock can be empty. This means that it doesn't have any dockables docked in it, or it doesn't have any child docks if it is a composite dock.
 * If a dock becomes empty, it should inform the parent composite dock, that it is empty.
 * A parent composite dock can then decide to remove the empty child dock.
 * </p>
 * <p>
 * A dock can also be full. This means that no dockables can be added. If a composite dock is full,
 * no child docks can be added. This does not necessarily mean that all of its child docks are full!
 * </p>
 * <p>
 * When a dockable is dragged, depending on the location of the mouse, the dockable can be docked
 * in different ways in the dock. For example,
 * if the location is at the left side of a dock, the dockable can be docked left. if the location is in the center
 * of a dock, the dockable can be docked in the center. Every mouse location has a priority for docking
 * a dockable. The priority constants are defined in {@link Priority}.
 *
 * @author Heidi Rakels.
 */
public interface Dock {

	// Interface methods.

	/**
	 * <p>
	 * Determines if a dockable can be added to this dock at the specified location.
	 * </p>
	 * <p>
	 * The priority constants are defined in {@link Priority}.
	 * </p>
	 * <p>
	 * If the dockable cannot be added, it returns {@link Priority#CANNOT_DOCK}. If a dockable cannot be added to
	 * this dock, it is still possible that it can be added to a child dock of this dock, if it is a composite dock.
	 * </p>
	 * <p>
	 * The higher the integer that is returned, the more this dock wants this dockable to be docked here.
	 * Some predefined return values with ascending priority are:
	 * <ul>
	 * <li>{@link Priority#CAN_DOCK_AS_LAST}</li>
	 * <li>{@link Priority#CAN_DOCK}</li>
	 * <li>{@link Priority#CAN_DOCK_WITH_PRIORITY}</li>
	 * <li>{@link Priority#CAN_DOCK_WITH_HIGH_PRIORITY}</li>
	 * </ul>
	 *
	 * @param dockable         The dockable that will be added.
	 * @param relativeLocation The location where the dockable will be added.
	 * @return The integer that determines the priority for adding the dockable. If the dockable cannot
	 * be added, CANNOT_DOCK is returned. The higher the integer, the higher the priority for
	 * docking the dockable in this dock.
	 */
	public int getDockPriority(Dockable dockable, Point relativeLocation);

	/**
	 * <p>
	 * Sets the given rectangle to the position and size of the graphical
	 * content component of the dockable, when it will be docked in this dock.
	 * </p>
	 * <p>
	 * Determines if the dockable can be added to this dock. If the dockable cannot be added,
	 * it returns {@link Priority#CANNOT_DOCK} and the given rectangle is not changed.
	 * </p>
	 *
	 * @param dockable         The dockable that will be added.
	 * @param relativeLocation The location where the dockable will be added.
	 * @param dockableOffset   The mouse location where the dragging started, relatively to the previous dock of the dockable.
	 * @param rectangle        This rectangle will be set to the location and size of the graphical component of the dockable
	 *                         when it will be docked in this dock. The rectangle is relative to this dock.
	 * @return If the dockable cannot be added, {@link Priority#CANNOT_DOCK} is returned, otherwise a positive integer.
	 */
	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation, Point dockableOffset, Rectangle rectangle);

	/**
	 * <p>
	 * Adds a dockable to this dock.
	 * </p>
	 * <p>
	 * First it determines if the dockable can be added with {@link #getDockPriority(Dockable, Point)}.
	 * If this method returns CANNOT_DOCK, the dockable is not added.
	 * </p>
	 *
	 * @param dockable         The dockable that will be added.
	 * @param relativeLocation The location where the dockable will be added.
	 * @param dockableOffset   The mouse location where the dragging started, relatively to the previous dock of the dockable.
	 * @return True if the dockable was added to this dock, false otherwise.
	 */
	public boolean addDockable(Dockable dockable, Point relativeLocation, Point dockableOffset);

	/**
	 * Determines if this dock doesn't have any dockables docked in it, or doesn't have any child docks docked in it.
	 *
	 * @return True if this dock doesn't have any dockables docked in it, or doesn't have any child docks docked in it, false otherwise.
	 */
	public boolean isEmpty();

	/**
	 * Determines if any more dockables can be added to this dock.
	 *
	 * @return True if any more dockables can be added to this dock, false otherwise.
	 */
	public boolean isFull();

	/**
	 * Gets the parent dock of this dock.
	 *
	 * @return The parent dock of this dock.
	 */
	public CompositeDock getParentDock();

	/**
	 * Sets the specified dock as new parent dock of this dock.
	 *
	 * @param parentDock The parent dock of this dock.
	 */
	public void setParentDock(CompositeDock parentDock);

	/**
	 * <p>
	 * Saves the properties of this dock in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this dock should start with the given prefix.
	 * </p>
	 *
	 * @param prefix        The prefix for the property names.
	 * @param properties    The properties object to which the properties should be added.
	 * @param childDockKeys A mapping between the child docks that are already saved and the keys that are used for the save.
	 *                      <ul>
	 *                      <li>map key: a child dock that is already saved ({@link Dock}).</li>
	 *                      <li>map value: the key that is used for saving the dock (java.lang.String).</li>
	 *                      </ul>
	 */
	public void saveProperties(String prefix, Properties properties, Map childDockKeys);

	/**
	 * <p>
	 * Loads the properties for this dock. The properties can be found in the given properties object.
	 * The property names for this dock start with the given prefix.
	 * </p>
	 * <p>
	 * The dockables that should be docked in this dock are added to this dock.
	 * They can be found in the given dockables mapping.
	 * The docks that should be docked inside this dock are added to this dock.
	 * They can be found in the given childDocks mapping.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the dock.
	 * Don't call this method for a dock that already has a content.
	 * </p>
	 *
	 * @param prefix       The prefix of the names of the properties that have been intended for this dock.
	 * @param properties   The properties object that contains the properties for this dock. It can contain also
	 *                     properties for other objects, but they will have another prefix.
	 * @param childDocks   A mapping between the dock keys of the child docks that are already loaded and the docks.
	 *                     <ul>
	 *                     <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 *                     <li>map value: a child dock that is already loaded ({@link Dock}).</li>
	 *                     </ul>
	 * @param dockablesMap A mapping that contains the available dockables.
	 *                     <ul>
	 *                     <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                     <li>map value: the dockable ({@link Dockable}).</li>
	 *                     </ul>
	 * @param owner        The owner window of the dock in the dock model ({@link com.javadocking.model.DockModel}).
	 * @throws IOException    If an error occures while decoding the properties.
	 */
	public void loadProperties(String prefix, Properties properties, Map childDocks, Map dockablesMap, Window owner) throws IOException;

	/**
	 * Adds a listener for {@link com.javadocking.event.DockingEvent}s of this dock.
	 * The listener will be informed before and after
	 * adding, moving or removing dockables or child docks from this dock.
	 *
	 * @param listener A docking listener that will be notified when a dockable or child dock is added, moved, or removed.
	 */
	public void addDockingListener(DockingListener listener);

	/**
	 * Removes a listener for docking events of this dock.
	 *
	 * @param listener The docking listener to remove.
	 */
	public void removeDockingListener(DockingListener listener);

}
