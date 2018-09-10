package com.javadocking.dock;


import com.javadocking.dock.factory.DockFactory;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * This is a {@link com.javadocking.dock.Dock} that can contain other child docks.
 * </p>
 * <p>
 * Information on using composite docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * Child docks can be added. They can not be removed explicitly. When a child dock is empty, the method
 * {@link #emptyChild(Dock)} of the composite parent dock has to be called. The parent dock can then remove the child.
 * </p>
 * <p>
 * Dockables can also be added with the method {@link Dock#addDockable(com.javadocking.dockable.Dockable, java.awt.Point, java.awt.Point)}.
 * When this dockable is added, a child dock is created with the child dock factory, retrieved by
 * {@link #getChildDockFactory()}. The dockable is added to this child dock, and the child dock is added to this composite dock.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface CompositeDock extends Dock {

	// Fields for the properties of a dock.

	/**
	 * The name childDock that will be used to create property names for specifying properties of child docks.
	 */
	String CHILD_DOCK_PREFIX = "childDock";

	// Interface methods.

	/**
	 * <p>
	 * Adds the given dock as child dock at the given position.
	 * </p>
	 * <p>
	 * If there is already a dock at the given position, the child is added at the first free position.
	 * If the given position is illegal, then the dock is added at the first free position.
	 * </p>
	 *
	 * @param dock     The new child dock for this dock.
	 * @param position The position for the child dock.
	 * @throws IllegalStateException If the dock is full.
	 */
	void addChildDock(Dock dock, Position position) throws IllegalStateException;

	/**
	 * Is called when the specified child dock is empty. Normally this child dock will be removed.
	 *
	 * @param childDock The child dock that is empty.
	 */
	void emptyChild(Dock childDock);

	/**
	 * Makes the given empty child dock invisible. It may not be removed, because there are still listeners
	 * attached to the child dock, that are currently dragging the dockable. The dock is becoming a ghost. It is still
	 * there, but it is invisible.
	 *
	 * @param childDock The child dock that is empty, but not may be removed.
	 */
	void ghostChild(Dock childDock);

	/**
	 * Clears the ghost child docks from this dock. The ghost child docks are removed.
	 */
	void clearGhosts();

	/**
	 * Gets the number of child docks of this dock.
	 *
	 * @return The number of child docks of this dock.
	 */
	int getChildDockCount();

	/**
	 * Gets the child dock with the specified index.
	 *
	 * @param index The index of the child dock.
	 * @return The child dock with the specified index.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getChildDockCount()).
	 */
	@Nullable
	Dock getChildDock(int index) throws IndexOutOfBoundsException;

	/**
	 * Gets the position, where the child dock is docked in this dock.
	 *
	 * @param childDock The dockable that is docked in this dock.
	 * @return The position where the child dock is docked in this dock.
	 * Not null.
	 * @throws IllegalArgumentException    If the given dock is not docked in this dock.
	 */
	Position getChildDockPosition(Dock childDock) throws IllegalArgumentException;

	/**
	 * Gets the factory that creates the child docks for this composite dock.
	 *
	 * @return The factory that creates the child docks for this composite dock.
	 */
	@Nullable
	DockFactory getChildDockFactory();

	/**
	 * Sets the factory that creates the child docks for this composite dock.
	 *
	 * @param dockFactory The factory that creates the child docks for this composite dock.
	 * @throws IllegalArgumentException When the child dock factory is null.
	 */
	void setChildDockFactory(DockFactory dockFactory);

}
