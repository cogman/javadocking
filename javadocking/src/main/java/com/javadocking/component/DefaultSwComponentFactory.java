package com.javadocking.component;

import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.SingleDock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.action.DefaultPopupMenuFactory;
import com.javadocking.dockable.action.PopupMenuFactory;
import com.javadocking.visualizer.ExternalizeDock;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


/**
 * <p>
 * This Swing component factory provides default implementations for the creation of the components that
 * will be used by the docking library.
 * </p>
 * <p>
 * It uses the normal constructors of the Swing components (javax.swing.JSplitPane, javax.swing.JTabbedPane,
 * javax.swing.JDialog, javax.swing.JWindow and javax.swing.JLabel).
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultSwComponentFactory implements SwComponentFactory {

	/**
	 * The factory that creates a popup menu for one dockable or a group of dockables.
	 */
	private PopupMenuFactory PopupMenuFactory = new DefaultPopupMenuFactory();

	// Implementations of SwComponentFactory.

	public JSplitPane createJSplitPane() {

		return new JSplitPane();

	}

	public JTabbedPane createJTabbedPane() {
		return new JTabbedPane();
	}

	/**
	 * Creates an undecorated non-modal dialog that is resizable.
	 */
	public JDialog createJDialog(Window owner) {

		// Create the dialog.
		JDialog dialog;
		if (owner instanceof JDialog) {
			dialog = new JDialog((JDialog) owner, ((JDialog) owner).getTitle());
		} else if (owner instanceof JFrame) {
			dialog = new JDialog((JFrame) owner, ((JFrame) owner).getTitle());
		} else {
			dialog = new JDialog((JFrame) null, "");
		}

		// We don't want decorations.
		dialog.setUndecorated(true);
		WindowResizer windowResizer = new WindowResizer(dialog);
		dialog.addMouseListener(windowResizer);
		dialog.addMouseMotionListener(windowResizer);

		return dialog;

	}

	public Window createWindow(Window owner) {
		return createJDialog(owner);
	}

	/**
	 * Creates a raised border.
	 */
	public Border createFloatingBorder() {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.lightGray),
				BorderFactory.createRaisedBevelBorder());
	}

	public JLabel createJLabel() {
		return new JLabel();
	}

	/**
	 * Creates a {@link IconButton}.
	 */
	public Component createIconButton(Action action) {
		return new IconButton(action);
	}

	/**
	 * Creates a {@link SelectableDockableHeader}.
	 */
	public SelectableHeader createTabDockHeader(Dockable dockable, int position) {

		SelectableDockableHeader tabDockHeader = new SelectableDockableHeader(dockable, position);
		tabDockHeader.setPosition(position);
		return tabDockHeader;

	}

	/**
	 * Creates a {@link SelectableDockableHeader}.
	 */
	public SelectableHeader createCompositeTabDockHeader(Dock childDock, int position) {

		SelectableDockHeader tabDockHeader = new SelectableDockHeader(childDock, position);
		tabDockHeader.setPosition(position);
		return tabDockHeader;

	}

	/**
	 * Creates a {@link SingleDockHeader}.
	 */
	public DockHeader createSingleDockHeader(LeafDock dock, int position) {
		return new SingleDockHeader(dock, position);
	}

	/**
	 * Creates a {@link DefaultDockHeader}.
	 */
	public DockHeader createDockHeader(LeafDock dock, int orientation) {
		return new DefaultDockHeader(dock, orientation);
	}

	/**
	 * Creates a {@link MaximizeHeader}.
	 */
	public Header createMaximizeHeader(Dockable dockable, int position) {
		return new MaximizeHeader(dockable, position);
	}

	/**
	 * Creates a {@link MinimzeHeader}.
	 */
	public SelectableHeader createMinimizeHeader(Dockable dockable, int position) {
		return new MinimzeHeader(dockable, position);
	}

	/**
	 * Creates a {@link SingleDock}.
	 */
	public ExternalizeDock createExternalizer() {
		return new SingleDock();
	}

	/**
	 * Creates a popup menu with {@link PopupMenuFactory} of this class.
	 */
	public JPopupMenu createPopupMenu(Dockable selectedDockable, CompositeDockable compositeDockable) {
		return PopupMenuFactory.createPopupMenu(selectedDockable, compositeDockable);
	}

	// Getters / Setters.

	/**
	 * Gets the factory that creates a popup menu for one dockable or a group of dockables.
	 *
	 * @return The factory that creates a popup menu for one dockable or a group of dockables.
	 */
	public PopupMenuFactory getPopupMenuFactory() {
		return PopupMenuFactory;
	}

	/**
	 * Sets the factory that creates a popup menu for one dockable or a group of dockables.
	 *
	 * @param popupMenuFactory The factory that creates a popup menu for one dockable or a group of dockables.
	 */
	public void setPopupMenuFactory(PopupMenuFactory popupMenuFactory) {
		PopupMenuFactory = popupMenuFactory;
	}


}
