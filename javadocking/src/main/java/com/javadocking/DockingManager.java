package com.javadocking;

import com.javadocking.component.DefaultSwComponentFactory;
import com.javadocking.component.SwComponentFactory;
import com.javadocking.drag.DefaultDragListenerFactory;
import com.javadocking.drag.DragListenerFactory;
import com.javadocking.drag.DraggerFactory;
import com.javadocking.drag.StaticDraggerFactory;
import com.javadocking.drag.painter.*;
import com.javadocking.model.DefaultDockingPathModel;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPathModel;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.dnd.DragSource;

/**
 * <p>
 * The docking manager contains only static methods that provide general objects that can be used by multiple docking classes,
 * e.g. the cursors for dragging a dockable, the component factory,
 * dragger factories, etc.
 * </p>
 * <p>
 * It has a {@link DockModel} that contains all the docks of the application.
 * By default there is no dock model. The dock model should be set by {@link #setDockModel(DockModel)}.
 * </p>
 * <p>
 * This manager also provides the {@link DraggerFactory}. The default that will be used is
 * {@link StaticDraggerFactory}. If you want to use another factory, e.g.
 * {@link com.javadocking.drag.DynamicDraggerFactory}, you should set this factory
 * before creating any {@link com.javadocking.dock.Dock}s.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DockingManager {

	// Fields.

	/**
	 * The executor of docking actions, like adding, moving and removing dockables.
	 */
	private static DockingExecutor dockingExecutor = new DockingExecutor();
	/**
	 * The component factory that will be used in the docking library.
	 * The default is a {@link DefaultSwComponentFactory}.
	 */
	private static SwComponentFactory componentFactory = new DefaultSwComponentFactory();
	/**
	 * The dock model of the application.
	 */
	@Nullable
	private static DockModel dockModel;
	/**
	 * The docking path model of the application.
	 */
	@Nullable
	private static DockingPathModel dockingPathModel = new DefaultDockingPathModel();
	/**
	 * The factory that provides the {@link com.javadocking.drag.Dragger}s
	 * for the docks of the application.
	 * The default is a {@link StaticDraggerFactory}.
	 */
	private static DraggerFactory draggerFactory = new StaticDraggerFactory();
	/**
	 * The factory that provides the {@link com.javadocking.drag.DragListener}s
	 * for the dockables of docks of the application.
	 * The default is a {@link DefaultDragListenerFactory}.
	 */
	private static DragListenerFactory dockDragListenerFactory = new DefaultDragListenerFactory();
	/**
	 * The factory that provides the {@link com.javadocking.drag.DragListener}s
	 * for the dockables of the application.
	 * The default is a {@link DefaultDragListenerFactory}.
	 */
	private static DragListenerFactory dockableDragListenerFactory = new DefaultDragListenerFactory();
	/**
	 * The cursor that will be used when dragging a dockable that can be docked at the current mouse position.
	 * The default is java.awt.dnd.DragSource.DefaultMoveDrop.
	 */
	private static Cursor canDockCursor = DragSource.DefaultMoveDrop;
	/**
	 * The cursor that will be used when dragging a dockable that cannot be docked at the current mouse position.
	 * The default is java.awt.dnd.DragSource.DefaultMoveNoDrop.
	 */
	private static Cursor canNotDockCursor = DragSource.DefaultMoveNoDrop;

	static {
		CompositeDockableDragPainter dockableDragPainter = new CompositeDockableDragPainter();
		dockableDragPainter.addPainter(new SwDockableDragPainter(new RectangleDragComponentFactory(new DefaultRectanglePainter(), true)));
		dockableDragPainter.addPainter(new WindowDockableDragPainter(new DefaultRectanglePainter(), true));
		draggerFactory = new StaticDraggerFactory(dockableDragPainter);
	}

	// Getters / Setters.

	/**
	 * Gets the Swing component factory. The default is a {@link DefaultSwComponentFactory}.
	 * This factory will be used to create the Swing components in the docking library.
	 *
	 * @return The Swing component factory.
	 */
	public static SwComponentFactory getComponentFactory() {
		return componentFactory;
	}

	/**
	 * Sets a new Swing component factory.
	 *
	 * @param newComponentFactory The Swing component factory.
	 */
	public static void setComponentFactory(SwComponentFactory newComponentFactory) {
		componentFactory = newComponentFactory;
	}

	/**
	 * Gets the executor of docking actions like:
	 * <ul>
	 * <li>adding dockables to docks.</li>
	 * <li>moving dockables in docks.</li>
	 * <li>removing dockables from docks.</li>
	 * </ul>
	 *
	 * @return The executor of docking actions, like adding, moving and removing dockables.
	 */
	public static DockingExecutor getDockingExecutor() {
		return dockingExecutor;
	}

	/**
	 * Sets the executor of docking actions like:
	 * <ul>
	 * <li>adding dockables to docks.</li>
	 * <li>moving dockables in docks.</li>
	 * <li>removing dockables from docks.</li>
	 * </ul>
	 *
	 * @param newDockingExecutor The executor of docking actions, like adding, moving and removing dockables.
	 */
	public static void setDockingExecutor(DockingExecutor newDockingExecutor) {
		dockingExecutor = newDockingExecutor;
	}

	/**
	 * Gets the dockable dragger factory. This factory will be used to create a {@link com.javadocking.drag.Dragger}
	 * for every dock in the application. The default is a {@link StaticDraggerFactory}.
	 *
	 * @return The dockable dragger factory.
	 */
	public static DraggerFactory getDraggerFactory() {
		return draggerFactory;
	}

	/**
	 * Sets the dockable dragger factory. This factory will be used to create a {@link com.javadocking.drag.Dragger}
	 * for every dock in the application.
	 *
	 * @param newDraggerFactory The dockable dragger factory.
	 */
	public static void setDraggerFactory(DraggerFactory newDraggerFactory) {
		draggerFactory = newDraggerFactory;
	}

	/**
	 * Gets the drag listener factory.
	 * This factory will be used to create a {@link com.javadocking.drag.DragListener}
	 * for every dock in the application.
	 * The default is a {@link DefaultDragListenerFactory}.
	 *
	 * @return The drag listener factory.
	 */
	public static DragListenerFactory getDockDragListenerFactory() {
		return dockDragListenerFactory;
	}

	/**
	 * Sets the drag listener factory.
	 * This factory will be used to create a {@link com.javadocking.drag.DragListener}
	 * for every dock in the application.
	 * <br>
	 * <b>WARNING:</b> This factory should be set before creating any {@link com.javadocking.dock.Dock} objects!
	 *
	 * @param newDragListenerFactory The drag listener factory.
	 */
	public static void setDockDragListenerFactory(DragListenerFactory newDragListenerFactory) {
		dockDragListenerFactory = newDragListenerFactory;
	}

	/**
	 * Gets the drag listener factory that creates the drag listeners for individual dockables.
	 * This factory will be used to create a {@link com.javadocking.drag.DragListener}
	 * for every dock in the application.
	 * The default is a {@link DefaultDragListenerFactory}.
	 *
	 * @return The dockable dragger factory that creates the drag listeners individual dockables.
	 */
	public static DragListenerFactory getDockableDragListenerFactory() {
		return dockableDragListenerFactory;
	}

	/**
	 * Sets the drag listener factory that creates the drag listeners for individual dockables.
	 * This factory will be used to create a {@link com.javadocking.drag.DragListener}
	 * for every dock in the application.
	 * <br>
	 * <b>WARNING:</b>This factory should be set before creating any {@link com.javadocking.dock.Dock} objects!
	 *
	 * @param newDragListenerFactory The drag listener factory.
	 */
	public static void setDockableDragListenerFactory(DragListenerFactory newDragListenerFactory) {
		dockableDragListenerFactory = newDragListenerFactory;
	}

	/**
	 * Gets the dock model that contains all the docks of the application.
	 *
	 * @return The dock model that contains all the docks of the application.
	 */
	@Nullable
	public static DockModel getDockModel() {
		return dockModel;
	}

	/**
	 * Sets the dock model that contains all the docks of the application.
	 * <br>
	 * <b>WARNING:</b>The dock model has to be given to the docking manager as soon as possible.
	 * Many utility methods are using the dock model of the docking manager.
	 *
	 * @param newDockModel The dock model that contains all the docks of the application.
	 * @throws NullPointerException    If the dock model is null.
	 */
	public static void setDockModel(@Nullable DockModel newDockModel) {
		if (newDockModel == null) {
			throw new NullPointerException("Dock model null");
		}

		dockModel = newDockModel;
	}

	/**
	 * Gets the docking path model of the application.
	 * The default is a {@link DefaultDockingPathModel}.
	 *
	 * @return The docking path model of the application.
	 */
	@Nullable
	public static DockingPathModel getDockingPathModel() {
		return dockingPathModel;
	}

	/**
	 * Sets the docking path model of the application.
	 *
	 * @param newDockingPathModel The docking path model of the application.
	 * @throws NullPointerException    If the docking path model is null.
	 */
	public static void setDockingPathModel(@Nullable DockingPathModel newDockingPathModel) {
		if (newDockingPathModel == null) {
			throw new NullPointerException("Docking path model null");
		}
		dockingPathModel = newDockingPathModel;
	}

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 * The default is java.awt.dnd.DragSource.DefaultMoveDrop.
	 *
	 * @return The cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 */
	public static Cursor getCanDockCursor() {
		return canDockCursor;
	}

	/**
	 * Sets the cursor that is used for dragging a dockable,
	 * when the dockable can be docked in an underlying dock.
	 *
	 * @param newCanDockCursor The cursor that is used for dragging a dockable,
	 *                         when the dockable can be docked in an underlying dock.
	 */
	public static void setCanDockCursor(Cursor newCanDockCursor) {
		canDockCursor = newCanDockCursor;
	}

	/**
	 * Gets the cursor that is used for dragging a dockable,
	 * when the dockable cannot be docked in an underlying dock.
	 * The default is java.awt.dnd.DragSource.DefaultMoveNoDrop.
	 *
	 * @return The cursor that is used for dragging a dockable,
	 * when the dockable cannot be docked in an underlying dock.
	 */
	public static Cursor getCanNotDockCursor() {
		return canNotDockCursor;
	}

	/**
	 * Sets the cursor that is used for dragging a dockable,
	 * when the dockable cannnot be docked in an underlying dock.
	 *
	 * @param newCanNotDockCursor The cursor that is used for dragging a dockable,
	 *                            when the dockable cannot be docked in an underlying dock.
	 */
	public static void setCanNotDockCursor(Cursor newCanNotDockCursor) {
		canNotDockCursor = newCanNotDockCursor;
	}

}
