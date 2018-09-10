package com.javadocking.model;

import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;

import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * Implementations of this interface keep the information about one path in a
 * tree structure of {@link com.javadocking.dock.Dock}s and {@link com.javadocking.dockable.Dockable}s.
 * </p>
 * <p>
 * Information on using docking paths is in
 * <a href="http://www.javadocking.com/developerguide/dockingpath.html" target="_blank">How to Use Docking Paths</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * This path can be an existing path in a {@link com.javadocking.model.DockModel}. It can be also a path that describes how a dockable that is
 * removed from the model was docked before. With this information the structure can be rebuild as
 * good as possible, when the dockable will be added again.
 * </p>
 * <p>
 * A dock model can contain several docking trees. This docking path
 * contains also information to identify a root dock of such a tree.
 * </p>
 * <p>
 * A dockable that is docked in a {@link com.javadocking.dock.LeafDock} has a specific position
 * in that dock. A dock that is docked in another
 * {@link com.javadocking.dock.CompositeDock} has also a position.
 * This docking path contains also this information.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockingPath {

	// Interface methods.

	/**
	 * Gets the ID of this docking path. A docking path usually describes the position of a {@link com.javadocking.dockable.Dockable}
	 * in a {@link DockModel}. The ID of this docking path can be the ID of this dockable, but this is not obligatory.
	 * <br>
	 * <b>WARNING:</b> All the docking paths used in an application should have a different ID.
	 *
	 * @return The ID of the docking path. Not null.
	 */
	String getID();

	/**
	 * Gets the key of the root dock of this path in the {@link DockModel}.
	 * The path belongs to a dock tree. This key defines the root of this tree in the dock model.
	 *
	 * @return The key of the root dock of this path. Not null.
	 */
	String getRootDockKey();

	/**
	 * Gets the number of docks in the path.
	 * Can be 0.
	 *
	 * @return The number of docks in the path.
	 */
	int getDockCount();

	/**
	 * <p>
	 * Gets the dock with the given index in the path.
	 * </p>
	 * <p>
	 * The root dock has index 0. The dock with index <code>getDockCount() - 1</code> can be a
	 * {@link com.javadocking.dock.LeafDock}, but this is not obligatory. The other docks are
	 * {@link com.javadocking.dock.CompositeDock}s.
	 * </p>
	 *
	 * @param index The index of the dock in the path.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getDockCount()).
	 * @return The dock with the given index in the path. Not null.
	 */
	Dock getDock(int index);

	/**
	 * Gets the position of the dockable or child dock in the dock with the given index in the path.
	 *
	 * @param index The index of the dock in the path.
	 * @throws IndexOutOfBoundsException If the index is out of range (index &lt; 0 || index &gt;= getDockCount()).
	 * @return The position of the dock with index <code>index + 1</code> in the dock with index
	 * <code>index</code> if the given index is smaller than <code>getDockCount() - 1</code>.
	 * The last position is usually the position of the dockable in the deepest dock of the path, if this
	 * deepest dock is a leaf dock. Not null.
	 */
	Position getPositionInDock(int index);

	/**
	 * <p>
	 * Saves the properties of this docking path in the given properties object.
	 * </p>
	 * <p>
	 * The property names for this docking path should start with the given prefix.
	 * </p>
	 *
	 * @param prefix     The prefix for the property names.
	 * @param properties The properties object to which the properties should be added.
	 * @param dockKeys   A mapping between the docks that are already saved and the keys that are used for the save.
	 *                   <ul>
	 *                   <li>map key: a dock that is already saved ({@link Dock}).</li>
	 *                   <li>map value: the key that is used for saving the dock (java.lang.String).</li>
	 *                   </ul>
	 */
	void saveProperties(String prefix, Properties properties, Map dockKeys);

	/**
	 * <p>
	 * Loads the properties for this docking path. The properties can be found in the given properties object
	 * and the property names start with the given prefix.
	 * </p>
	 * <p>
	 * The docks that should be used inside this docking path can be found in the given docks mapping.
	 * </p>
	 * <p>
	 * This method should be called after the empty constructor to create the content of the docking path.
	 * Don't call this method for a docking path that already has a content.
	 * </p>
	 *
	 * @param prefix     The prefix of the names of the properties that have been intended for this docking path.
	 * @param properties The properties object that contains the properties for this docking path. It can contain also
	 *                   properties for other objects, but they will have another prefix.
	 * @param docks      A mapping between the dock keys of the docks that are already loaded and the docks.
	 *                   <ul>
	 *                   <li>map key: the key that is used for saving the dock (java.lang.String).</li>
	 *                   <li>map value: a dock that is already loaded ({@link Dock}).</li>
	 *                   </ul>
	 */
	void loadProperties(String prefix, Properties properties, Map docks);

}
