package com.javadocking.component;

import com.javadocking.dock.LeafDock;
import com.javadocking.drag.DragHandle;

/**
 * <p>
 * A header for a dock.
 * </p>
 * <p>
 * It is used as a handle for dragging all the dockables that are docked in the dock.
 * The dockables can be dragged by dragging the component that implements this interface.
 * </p>
 * <p>
 * Information on using dock headers is in
 * <a href="http://www.javadocking.com/developerguide/componentfactory.html#LeafDockHeaders" target="_blank">How to Use the Component Factory</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * Implementations of this class should inherit from the class javax.swing.JComponent.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockHeader extends DragHandle, Header {

	// Interface methods.

	/**
	 * Gets the dock of this header.
	 *
	 * @return The dock of this header.
	 */
	LeafDock getDock();

}
