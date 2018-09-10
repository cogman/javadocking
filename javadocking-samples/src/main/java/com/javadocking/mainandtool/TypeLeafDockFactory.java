package com.javadocking.mainandtool;

import com.javadocking.dock.*;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Properties;

/**
 * <p>
 * This dock factory checks the docking modes of the given dockable and creates a
 * dock in which the dockable is allowed to be docked.
 * </p>
 * <p>
 * When the property <code>useLastDockingMode</code> is true, the last docking mode
 * of the dockable is used to create an appropriate dock.
 * </p>
 * <p>
 * When the property <code>useLastDockingMode</code> is false, it tries to create a
 * dock of the given types in the given order:
 * <ul>
 * <li>{@link GridDock} for {@link DockingMode#TOOL_GRID}.</li>
 * <li>{@link LineDock} for {@link DockingMode#HORIZONTAL_TOOLBAR}.</li>
 * <li>{@link LineDock} for {@link DockingMode#VERTICAL_TOOLBAR}.</li>
 * <li>{@link GridDock} for {@link DockingMode#MINIMIZE_GRID}.</li>
 * <li>{@link LineDock} for {@link DockingMode#HORIZONTAL_MINIMIZE}.</li>
 * <li>{@link LineDock} for {@link DockingMode#VERTICAL_MINIMIZE}.</li>
 * <li>{@link TypeTabDock} or {@link DockingMode#TAB}.</li>
 * <li>{@link SingleDock} for {@link DockingMode#SINGLE}.</li>
 * <li>{@link LineDock} for {@link DockingMode#HORIZONTAL_LINE}.</li>
 * <li>{@link LineDock} for {@link DockingMode#VERTICAL_LINE}.</li>
 * <li>{@link GridDock} for {@link DockingMode#GRID}.</li>
 * </ul>
 *
 * @author Heidi Rakels
 */
public class TypeLeafDockFactory implements DockFactory {

	// Fields.

	/**
	 * When true, this factory tries to create a dock with the same docking mode
	 * as the last docking mode of the given dockable ({@link Dockable#getLastDockingMode()}).
	 */
	private boolean useLastDockingMode;

	private boolean main;

	// Constructors.

	/**
	 * Constructs a dock factory that creates {@link com.javadocking.dock.LeafDock}s.
	 */
	public TypeLeafDockFactory(boolean main) {
		this(main, true);

	}

	/**
	 * Constructs a dock factory that creates {@link com.javadocking.dock.LeafDock}s.
	 *
	 * @param useLastDockingMode When true, this factory tries to create a dock with the same docking mode
	 *                           as the last docking mode of the given dockable ({@link Dockable#getLastDockingMode()}).
	 */
	public TypeLeafDockFactory(boolean main, boolean useLastDockingMode) {
		this.useLastDockingMode = useLastDockingMode;
		this.main = main;
	}

	// Implementations of DockFactory.

	@Nullable
	public Dock createDock(@Nullable Dockable dockable, int dockingMode) {

		// Is the dockable null?
		if (dockable == null) {
			return new TabDock();
		}

		// Get the allowed docking modes of the dockable.
		int dockingModes = dockable.getDockingModes();

		// Do we have to use the last docking mode of the dockable?
		if (useLastDockingMode) {
			switch (dockable.getLastDockingMode()) {
				case DockingMode.TOOL_GRID:
					return new GridDock(DockingMode.TOOL_GRID);
				case DockingMode.HORIZONTAL_TOOLBAR:
					return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
				case DockingMode.VERTICAL_TOOLBAR:
					return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
				case DockingMode.MINIMIZE_GRID:
					return new GridDock(DockingMode.MINIMIZE_GRID);
				case DockingMode.HORIZONTAL_MINIMIZE:
					return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
				case DockingMode.VERTICAL_MINIMIZE:
					return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
				case DockingMode.TAB:
					return new TypeTabDock(main);
				case DockingMode.SINGLE:
					if ((dockingModes & DockingMode.SINGLE) != 0) {
						// Is there only one dockable?
						if (dockable instanceof CompositeDockable) {
							if (((CompositeDockable) dockable).getDockableCount() == 1) {
								return new SingleDock();
							}
						} else {
							return new SingleDock();
						}
					}
					break;
				case DockingMode.HORIZONTAL_LINE:
					LineDock lineDock = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false);
					lineDock.setRealSizeRectangle(false);
					return lineDock;
				case DockingMode.VERTICAL_LINE:
					lineDock = new LineDock(LineDock.ORIENTATION_VERTICAL, false);
					lineDock.setRealSizeRectangle(false);
					return lineDock;
				case DockingMode.GRID:
					return new GridDock();
			}
		}

		// Do we have a composite dockable.
		if (dockable instanceof CompositeDockable) {
			if ((dockingModes & DockingMode.TOOL_GRID) != 0) {
				return new GridDock(DockingMode.TOOL_GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
				return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			}
			if ((dockingModes & DockingMode.VERTICAL_TOOLBAR) != 0) {
				return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			}
			if ((dockingModes & DockingMode.MINIMIZE_GRID) != 0) {
				return new GridDock(DockingMode.MINIMIZE_GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_MINIMIZE) != 0) {
				return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
			}
			if ((dockingModes & DockingMode.VERTICAL_MINIMIZE) != 0) {
				return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
			}
			if ((dockingModes & DockingMode.TAB) != 0) {
				return new TabDock();
			}
			if ((dockingModes & DockingMode.SINGLE) != 0) {
				// Is there only one dockable?
				if (((CompositeDockable) dockable).getDockableCount() == 1) {
					return new SingleDock();
				}

			}
			if ((dockingModes & DockingMode.HORIZONTAL_LINE) != 0) {
				LineDock lineDock = new LineDock(LineDock.ORIENTATION_HORIZONTAL, false);
				lineDock.setRealSizeRectangle(false);
				return lineDock;
			}
			if ((dockingModes & DockingMode.VERTICAL_LINE) != 0) {
				LineDock lineDock = new LineDock(LineDock.ORIENTATION_VERTICAL, false);
				lineDock.setRealSizeRectangle(false);
				return lineDock;
			}
			if ((dockingModes & DockingMode.GRID) != 0) {
				return new GridDock();
			}
			return null;
		}

		// We have a single dockable.
		Object dockableObject = dockable.getContent();
		if ((dockableObject instanceof Component)) {
			if ((dockingModes & DockingMode.TOOL_GRID) != 0) {
				return new GridDock(DockingMode.TOOL_GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
				return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			}
			if ((dockingModes & DockingMode.VERTICAL_TOOLBAR) != 0) {
				return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_TOOLBAR, DockingMode.VERTICAL_TOOLBAR);
			}
			if ((dockingModes & DockingMode.MINIMIZE_GRID) != 0) {
				return new GridDock(DockingMode.MINIMIZE_GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_MINIMIZE) != 0) {
				return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
			}
			if ((dockingModes & DockingMode.VERTICAL_MINIMIZE) != 0) {
				return new LineDock(LineDock.ORIENTATION_VERTICAL, false, DockingMode.HORIZONTAL_MINIMIZE, DockingMode.VERTICAL_MINIMIZE);
			}
			if ((dockingModes & DockingMode.TAB) != 0) {
				return new TabDock();
			}
			if ((dockingModes & DockingMode.SINGLE) != 0) {
				return new SingleDock();
			}
			if ((dockingModes & DockingMode.HORIZONTAL_LINE) != 0) {
				return new LineDock(LineDock.ORIENTATION_HORIZONTAL, false);
			}
			if ((dockingModes & DockingMode.VERTICAL_LINE) != 0) {
				return new LineDock(LineDock.ORIENTATION_VERTICAL, false);
			}
			if ((dockingModes & DockingMode.GRID) != 0) {
				return new GridDock();
			}
		}

		return null;
	}

	@Nullable
	public Dimension getDockPreferredSize(Dockable dockable, int dockingMode) {

		// Do we have a composite dockable?
		if (dockable instanceof CompositeDockable) {
			// Get the allowed docking modes.
			int dockingModes = dockable.getDockingModes();

			// Do we have to use the last docking mode of the dockable?
			if (useLastDockingMode) {
				switch (dockable.getLastDockingMode()) {
					case DockingMode.TOOL_GRID:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
					case DockingMode.HORIZONTAL_TOOLBAR:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
					case DockingMode.VERTICAL_TOOLBAR:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
					case DockingMode.MINIMIZE_GRID:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
					case DockingMode.HORIZONTAL_MINIMIZE:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
					case DockingMode.VERTICAL_MINIMIZE:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
					case DockingMode.TAB:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.TAB);
					case DockingMode.SINGLE:
						if (((CompositeDockable) dockable).getDockableCount() == 1) {
							return ((CompositeDockable) dockable).getDockable(0).getContent().getPreferredSize();
						}
						break;
					case DockingMode.HORIZONTAL_LINE:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);

					case DockingMode.VERTICAL_LINE:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
					case DockingMode.GRID:
						return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
				}
			}

			// We could not use the last docking mode.

			if ((dockingModes & DockingMode.TOOL_GRID) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_TOOLBAR) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
			}
			if ((dockingModes & DockingMode.VERTICAL_TOOLBAR) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
			}
			if ((dockingModes & DockingMode.MINIMIZE_GRID) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
			}
			if ((dockingModes & DockingMode.HORIZONTAL_MINIMIZE) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
			}
			if ((dockingModes & DockingMode.VERTICAL_MINIMIZE) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
			}
			if ((dockingModes & DockingMode.TAB) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.TAB);
			}
			if ((dockingModes & DockingMode.SINGLE) != 0) {
				if (((CompositeDockable) dockable).getDockableCount() == 1) {
					return ((CompositeDockable) dockable).getDockable(0).getContent().getPreferredSize();
				}
			}
			if ((dockingModes & DockingMode.HORIZONTAL_LINE) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.HORIZONTAL_LINE);
			}
			if ((dockingModes & DockingMode.VERTICAL_LINE) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.VERTICAL_LINE);
			}
			if ((dockingModes & DockingMode.GRID) != 0) {
				return DockingUtil.getCompositeDockablePreferredSize((CompositeDockable) dockable, DockingMode.GRID);
			}

			return null;
		}

		// We have a simple dockable. Get the size of the component.
		if (dockable.getContent() != null) {
			return dockable.getContent().getPreferredSize();
		}

		return new Dimension(0, 0);

	}

	public void saveProperties(String prefix, @NotNull Properties properties) {

		PropertiesUtil.setBoolean(properties, prefix + "useLastDockingMode", useLastDockingMode);
		PropertiesUtil.setBoolean(properties, prefix + "main", main);

	}


	public void loadProperties(String prefix, @NotNull Properties properties) {

		useLastDockingMode = PropertiesUtil.getBoolean(properties, prefix + "useLastDockingMode", useLastDockingMode);
		main = PropertiesUtil.getBoolean(properties, prefix + "main", main);

	}

	// Getters / Setters.

	/**
	 * Returns whether the last docking mode of the dockable will be used to create a new dock.
	 * The default value is true.
	 *
	 * @return When true, this factory tries to create a dock with the same docking mode
	 * as the last docking mode of the given dockable ({@link Dockable#getLastDockingMode()}).
	 */
	public boolean getUseLastDockingMode() {
		return useLastDockingMode;
	}

	/**
	 * Sets whether the last docking mode of the dockable will be used to create a new dock.
	 *
	 * @param useLastDockingMode When true, this factory tries to create a dock with the same docking mode
	 *                           as the last docking mode of the given dockable ({@link Dockable#getLastDockingMode()}).
	 */
	public void setUseLastDockingMode(boolean useLastDockingMode) {
		this.useLastDockingMode = useLastDockingMode;
	}

	public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

}
