package com.javadocking.drag.painter;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * This dockable drag painter shows a window with the title of the dockable
 * at the current mouse position, when the dockable is dragged.
 * This window is also visible outside the owner window.
 * The dock cursor or cannotdock cursor is shown on the window.
 * <p>
 * TODO dispose the window
 *
 * @author Heidi Rakels.
 */
public class LabelDockableDragPainter implements DockableDragPainter {

	/**
	 * The window that shows the title of the dockable.
	 */
	@Nullable
	private JWindow window;
	/**
	 * The content of the window.
	 */
	private JPanel contentPanel;
	/**
	 * The dockable that is currently shown in the window. Can be null, when no dockable is shown.
	 */
	@Nullable
	private Dockable dockable;
	/**
	 * The position where the window will be placed.
	 */
	@NotNull
	private Point windowLocation = new Point();
	/**
	 * The label is shifted this distance from the mouse.
	 */
	private int verticalShift = 5;

	// Constructors.


	/**
	 * Constructs a dockable drag painter that shows a window with the title of the dockable
	 * at the current mouse position, when the dockable is dragged.
	 */
	public LabelDockableDragPainter() {


	}

	// Implementations of DockableDragPainter.

	public void clear() {

		dockable = null;
		if (window != null) {
			window.dispose();
			window = null;
		}

	}

	public void paintDockableDrag(@NotNull Dockable newDockable, @Nullable Dock dock, Rectangle rectangle, @NotNull Point locationInDestinationDock) {

		// Create the window.
		if (window == null) {
			window = new JWindow();
			contentPanel = new JPanel(new BorderLayout());
			contentPanel.setBackground(Color.white);
			contentPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.black),
					BorderFactory.createEmptyBorder(0, 5, 0, 5)));
			window.getContentPane().add(contentPanel);
		}

		// Can we dock the dockable?
		if (dock == null) {
			window.setCursor(DockingManager.getCanNotDockCursor());
		} else {
			window.setCursor(DockingManager.getCanDockCursor());
		}

		// Does the dockable have a title?
		if (newDockable.getTitle() == null) {
			window.setVisible(false);
			return;
		}

		// Did the dockable change?
		if (!newDockable.equals(dockable)) {
			dockable = newDockable;
			JLabel component = new JLabel(dockable.getTitle());
			component.setBackground(Color.white);
			contentPanel.removeAll();
			contentPanel.add(component);
			window.pack();
		}

		// Set the new location.
		windowLocation.move(locationInDestinationDock.x - window.getSize().width / 2,
				locationInDestinationDock.y - window.getSize().height / 2 - verticalShift);
		if (dock instanceof Component) {
			SwingUtilities.convertPointToScreen(windowLocation, (Component) dock);
		}
		window.setLocation(windowLocation);

		// Make the window visible.
		if (!window.isVisible()) {
			window.setVisible(true);
		}

	}

	// Getters / setters.

	/**
	 * Gets how much the label is shifted from the mouse in the vertical direction.
	 *
	 * @return How much the label is shifted from the mouse in the vertical direction.
	 */
	public int getVerticalShift() {
		return verticalShift;
	}

	/**
	 * Sets how much the label is shifted from the mouse in the vertical direction.
	 *
	 * @param verticalShift How much the label is shifted from the mouse in the vertical direction.
	 */
	public void setVerticalShift(int verticalShift) {
		this.verticalShift = verticalShift;
	}

}
