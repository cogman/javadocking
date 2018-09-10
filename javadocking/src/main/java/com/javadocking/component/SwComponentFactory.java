package com.javadocking.component;

import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.visualizer.ExternalizeDock;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * <p>
 * This factory creates the graphical components that are used by the docking library to build the docking UI.
 * With this factory the components used by the library can be customized.
 * </p>
 * <p>
 * Information on using the component factory is in
 * <a href="http://www.javadocking.com/developerguide/componentfactory.html" target="_blank">How to Use the Component Factory</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * The implementation of this interface should be given to the docking manager with
 * {@link com.javadocking.DockingManager#setComponentFactory(SwComponentFactory)}.
 * Otherwise the default implementation will be used.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface SwComponentFactory {

	// Interface methods.

	/**
	 * Creates a tabbed pane.
	 *
	 * @return The created tabbed pane.
	 */
	JTabbedPane createJTabbedPane();

	/**
	 * Creates a split pane.
	 *
	 * @return The created split pane.
	 */
	JSplitPane createJSplitPane();

	/**
	 * Creates a non-modal dialog.
	 *
	 * @param owner The owner of the dialog.
	 * @return The created non-modal dialog.
	 */
	JDialog createJDialog(Window owner);

	/**
	 * Creates a window.
	 *
	 * @param owner The owner of the window.
	 * @return The created window.
	 */
	Window createWindow(Window owner);

	/**
	 * Creates a border for a floating window.
	 *
	 * @return The created border.
	 */
	Border createFloatingBorder();

	/**
	 * Creates a label.
	 *
	 * @return The created label.
	 */
	JLabel createJLabel();

	/**
	 * Creates a small icon component to perform the given action.
	 *
	 * @param action The action that will be performed when the button is clicked.
	 * @return The created small icon component to perform the given action.
	 */
	Component createIconButton(Action action);

	/**
	 * Creates the header for the given dockable that will be displayed in a tab of a dock.
	 *
	 * @param    dockable        The dockable that will be represented in the tab.
	 * @param    position        Possible values are constants defined by the class Position:
	 * Position.LEFT, Position.RIGHT, Position.TOP or Position.BOTTOM.
	 * @return A header for the dockable in the tab.
	 */
	SelectableHeader createTabDockHeader(Dockable dockable, int position);

	/**
	 * Creates the header for the given dock that will be displayed in a tab of a dock.
	 *
	 * @param    childDock        The child dock that will be represented in the tab.
	 * @param    position        Possible values are constants defined by the class Position:
	 * Position.LEFT, Position.RIGHT, Position.TOP or Position.BOTTOM.
	 * @return A header for the dockable in the tab.
	 */
	SelectableHeader createCompositeTabDockHeader(Dock childDock, int position);

	/**
	 * Creates the header for the specified dock with one dockable.
	 *
	 * @param    dock            The dock of the header.
	 * @param    position        Possible values are constants defined by the class Position:
	 * Position.LEFT, Position.RIGHT, Position.TOP or Position.BOTTOM.
	 * @return A header for a dock with one dockable.
	 */
	DockHeader createSingleDockHeader(LeafDock dock, int position);

	/**
	 * <p>
	 * Creates the header for the specified dock with multiple dockables.
	 * </p>
	 *
	 * @param    dock            The dock of the header.
	 * @param    position        The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 * @return The header for a dock with multiple dockables.
	 */
	DockHeader createDockHeader(LeafDock dock, int position);

	/**
	 * Creates the header for the specified dockable in a maximize panel.
	 *
	 * @param    dockable        The dockable of the header.
	 * @param    position        The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 * @return A header for a dockable.
	 */
	Header createMaximizeHeader(Dockable dockable, int position);

	/**
	 * Creates the header for the specified dockable in a minimize panel.
	 *
	 * @param    dockable        The dockable of the header.
	 * @param    position        The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 * @return A header for a dockable.
	 */
	SelectableHeader createMinimizeHeader(Dockable dockable, int position);

	/**
	 * Creates a {@link LeafDock} in which a dockable can be externalized.
	 *
	 * @return A dock in which a dockable can be externalized.
	 */
	ExternalizeDock createExternalizer();


	/**
	 * Creates a popup menu for the selected dockable and the other dockables in the dock of the selected dockable.
	 * If the selected dockable is null,
	 * a popup menu for the composite dockable is created.
	 *
	 * @param selectedDockable  The selected dockable.
	 * @param compositeDockable The dockables in the selected dock.
	 * @return A popup menu for the selected dockable and the other dockables in the dock.
	 */
	JPopupMenu createPopupMenu(Dockable selectedDockable, CompositeDockable compositeDockable);

}
