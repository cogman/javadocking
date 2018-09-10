package com.javadocking.drag;


import com.javadocking.dock.*;
import com.javadocking.dockable.Dockable;
import com.javadocking.drag.painter.DefaultRectanglePainter;
import com.javadocking.drag.painter.DockableDragPainter;
import com.javadocking.drag.painter.SwDockableDragPainter;

/**
 * <p>
 * This dragger factory creates draggers that show with rectangles during dragging, where the
 * {@link com.javadocking.dockable.Dockable}s will be docked, if the mouse button is
 * released in that position.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class StaticDraggerFactory implements DraggerFactory {

	// Fields.

	/**
	 * Paints the rectangle where the dockable will be docked for the current mouse location.
	 */
	private DockableDragPainter dockableDragPainter = new SwDockableDragPainter(new DefaultRectanglePainter());

	// Constructors.

	/**
	 * Constructs a dragger factory with a default painter for painting the dragged dockables.
	 */
	public StaticDraggerFactory() {
	}

	/**
	 * Constructs a dragger factory with the given painter for painting the dragged dockables.
	 *
	 * @param    dockableDragPainter The painter for painting the dragged dockables.
	 */
	public StaticDraggerFactory(DockableDragPainter dockableDragPainter) {
		this.dockableDragPainter = dockableDragPainter;
	}

	// Implementations of DraggerFactory.

	/**
	 * It creates the following draggers with the <code>dockableDragPainter</code> of this class:
	 * <ul>
	 * <li>For a {@link com.javadocking.dock.SingleDock}: a {@link StaticSingleDragger}.</li>
	 * <li>For a {@link com.javadocking.dock.TabDock}: a {@link StaticTabDragger}.</li>
	 * </ul>
	 * For other types of docks a {@link StaticDragger} is returned.
	 */
	public Dragger createDragger(Dock dock) {
		if (dock instanceof SingleDock) {
			return new StaticSingleDragger(dockableDragPainter);
		} else if (dock instanceof TabDock) {
			return new StaticTabDragger(dockableDragPainter);
		} else if (dock instanceof LeafDock) {
			return new StaticDragger(dockableDragPainter);
		} else if (dock instanceof CompositeTabDock) {
			return new StaticCompositeTabDragger(dockableDragPainter);
		}
		return null;
	}

	/**
	 * It creates a {@link StaticDockableDragger} with the <code>dockableDragPainter</code> of this class.
	 */
	public Dragger createDragger(Dockable dockable) {
		return new StaticDockableDragger(dockable, dockableDragPainter);
	}

	// Getters / Setters.

	/**
	 * <p>
	 * Gets the painter for painting the dragged dockables.
	 * </p>
	 * <p>
	 * The default is a {@link SwDockableDragPainter} with a {@link DefaultRectanglePainter}.
	 * </p>
	 *
	 * @return dockableDragPainter The painter for painting the dragged dockables.
	 */
	public DockableDragPainter getDockableDragPainter() {
		return dockableDragPainter;
	}

	/**
	 * Sets the painter for painting the dragged dockables.
	 *
	 * @param dockableDragPainter The painter for painting the dragged dockables.
	 */
	public void setDockableDragPainter(DockableDragPainter dockableDragPainter) {
		this.dockableDragPainter = dockableDragPainter;
	}

}
