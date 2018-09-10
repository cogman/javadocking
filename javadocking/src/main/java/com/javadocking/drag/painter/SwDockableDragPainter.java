package com.javadocking.drag.painter;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.FloatDock;
import com.javadocking.dockable.Dockable;
import com.javadocking.model.DockModel;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;

/**
 * <p>
 * This painter paints a {@link com.javadocking.dockable.Dockable} during dragging.
 * </p>
 * <p>
 * The dragged dockable is showed with a java.awt.Component. This component is created by the
 * {@link com.javadocking.drag.painter.DragComponentFactory}.
 * The default drag component factory is {@link com.javadocking.drag.painter.RectangleDragComponentFactory}.
 * If this default factory is used, a {@link com.javadocking.drag.painter.RectanglePainter}
 * that paints the dockable on the component, should be provided to the constructor of this class.
 * </p>
 * <p>
 * The component that represents the dockable, is added to the java.swing.JLayeredPane of the
 * {@link com.javadocking.dock.Dock}.
 * </p>
 * <p>
 * The given dock should be a java.awt.Component and the ancestor window should
 * be javax.swing.JFrame, javax.swing.JWindow, or javax.swing.JDialog. Otherwise nothing will be done by this painter.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class SwDockableDragPainter implements DockableDragPainter {

	// Static fields.

	/**
	 * The integer the represents layer 0 in the layered pane.
	 */
	private static final Integer LAYER_0 = 0;

	// Fields.

	/**
	 * On this component the dragged dockable is shown.
	 */
	private Component dragComponent;
	/**
	 * The factory that creates the drag component.
	 */
	private DragComponentFactory dragComponentFactory;
	/**
	 * The position in the layered pane where the component will be placed.
	 */
	private Point componentLocation = new Point();
	/**
	 * The layered pane of the dock, where the dockable will be docked.
	 */
	private JLayeredPane layeredPane;
	/**
	 * The root pane of the dock, where the dockable will be docked.
	 */
	private JRootPane rootPane;
	/**
	 * The rectangle where the dockable will be docked for the previous mouse location.
	 */
	private Rectangle previousDockableDragRectangle = new Rectangle();
	/**
	 * The dock on which the previous rectangle was painted.
	 */
	private Dock previousPaintingDock;
	/**
	 * When true, the dockable will only be painted when the dock is java.awt.Component.
	 */
	private boolean onlyComponentDocks = true;


	// Constructors.

	/**
	 * Constructs a Swing dockable drag painter that uses the given drag component factory.
	 *
	 * @param dragComponentFactory The factory that creates the component that represents the dragged dockable.
	 */
	public SwDockableDragPainter(DragComponentFactory dragComponentFactory) {
		this.dragComponentFactory = dragComponentFactory;
	}

	/**
	 * Constructs a Swing dockable drag painter that uses a {@link com.javadocking.drag.painter.RectangleDragComponentFactory}
	 * as drag component factory.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 */
	public SwDockableDragPainter(RectanglePainter rectanglePainter) {
		dragComponentFactory = new RectangleDragComponentFactory(rectanglePainter);
	}

	/**
	 * Constructs a Swing dockable drag painter that uses the given drag component factory.
	 *
	 * @param dragComponentFactory The factory that creates the component that represents the dragged dockable.
	 * @param    onlyComponentDocks        When true, the dockable will only be painted when the dock is java.awt.Component.
	 */
	public SwDockableDragPainter(DragComponentFactory dragComponentFactory, boolean onlyComponentDocks) {
		this.dragComponentFactory = dragComponentFactory;
		this.onlyComponentDocks = onlyComponentDocks;
	}

	/**
	 * Constructs a Swing dockable drag painter that uses a {@link com.javadocking.drag.painter.RectangleDragComponentFactory}
	 * as drag component factory.
	 *
	 * @param rectanglePainter The rectangle painter used by the default drag component factory
	 *                         to paint the dragged dockable on the component.
	 * @param    onlyComponentDocks        When true, the dockable will only be painted when the dock is java.awt.Component.
	 */
	public SwDockableDragPainter(RectanglePainter rectanglePainter, boolean onlyComponentDocks) {
		dragComponentFactory = new RectangleDragComponentFactory(rectanglePainter);
		this.onlyComponentDocks = onlyComponentDocks;
	}


	// Implementations of DockingPainter.

	/**
	 * @param    dock                    Should be a java.awt.Component and the ancestor window should
	 * be javax.swing.JFrame, javax.swing.JWindow, or javax.swing.JDialog.
	 * Otherwise nothing is done.
	 */
	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		// Check that the dock and rectangle are not null.
		if ((dock == null) && (rectangle == null)) {
			clear();
			return;
		}

		// Return, when the dock is not a component, and onlyComponentDocks is true.
		if ((onlyComponentDocks) && (!(dock instanceof Component))) {
			clear();
			return;
		}

		// Has the destination dock changed or has the rectangle changed?
		if ((!(dock.equals(previousPaintingDock))) ||
				(!(rectangle.equals(previousDockableDragRectangle)))) {

			// Something changed, so clean up and paint the new rectangle.
			previousPaintingDock = dock;
			previousDockableDragRectangle.setBounds(rectangle);

			// Clear what was painted before.
			clear();

			// Get the root pane of the dock.
			rootPane = retrieveRootPane(dock);
			if (rootPane != null) {
				// Get the layered pane.
				layeredPane = SwingUtil.getLayeredPane(rootPane);
				if (layeredPane != null) {
					// Get the corresponding rectangle in the root pane.
					componentLocation.move(rectangle.x, rectangle.y);
					if (dock instanceof Component) {
						SwingUtilities.convertPointToScreen(componentLocation, (Component) dock);
					}
					SwingUtilities.convertPointFromScreen(componentLocation, rootPane);


					// Create the drag rectangle.
					dragComponent = dragComponentFactory.createDragComponent(dockable, dock, rectangle);

					// Add to the layered pane.
					layeredPane.add(dragComponent, LAYER_0, 0);
					dragComponent.setLocation(componentLocation.x, componentLocation.y);
				}
			}
		}

	}

	public void clear() {

		if (layeredPane != null) {
			// Remove the drag component from the layered pane.
			layeredPane.remove(dragComponent);

			// Repaint.
			rootPane.repaint();

			// Reset the fields.
			layeredPane = null;
			dragComponent = null;
			rootPane = null;
		}

	}

	// Private metods.

	/**
	 * Retrieves the root pane for the given dock.
	 * If the dock is a component, it is the root pane of the component.
	 * If the dock is a float dock, it is the root pane of the owner window.
	 *
	 * @param    dock    The dock.
	 * @return The root pane for the dock.
	 */
	private JRootPane retrieveRootPane(Dock dock) {

		// Get the root pane of the dock.
		if (dock instanceof Component) {
			return SwingUtilities.getRootPane((Component) dock);
		} else if (dock instanceof FloatDock) {
			// Get the owner window of this dock.
			DockModel dockModel = DockingManager.getDockModel();
			for (int index = 0; index < dockModel.getOwnerCount(); index++) {
				Iterator iterator = dockModel.getRootKeys(dockModel.getOwner(index));
				while (iterator.hasNext()) {
					String rootDockKey = (String) iterator.next();
					Dock rootDock = dockModel.getRootDock(rootDockKey);
					if (rootDock.equals(dock)) {
						// This is the owner window.
						Window window = dockModel.getOwner(index);
						return SwingUtil.getRootPane(window);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets if the dockable will only be painted when the dock is java.awt.Component.
	 *
	 * @return When true, the dockable will only be painted when the dock is java.awt.Component.
	 */
	public boolean isOnlyComponentDocks() {
		return onlyComponentDocks;
	}

	/**
	 * Sets if the dockable will only be painted when the dock is java.awt.Component.
	 *
	 * @param onlyComponentDocks When true, the dockable will only be painted when the dock is java.awt.Component.
	 */
	public void setOnlyComponentDocks(boolean onlyComponentDocks) {
		this.onlyComponentDocks = onlyComponentDocks;
	}


}
