package com.javadocking.dockable.action;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.DockableHider;
import com.javadocking.dock.LeafDock;
import com.javadocking.dockable.CompositeDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.model.DefaultDockingPath;
import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPath;
import com.javadocking.model.DockingPathModel;
import com.javadocking.util.DockingUtil;
import com.javadocking.visualizer.Visualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the default action to change the state of the dockable.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultDockableStateAction extends AbstractAction implements DockableStateAction {

	// Static fields.

	private static final boolean TEST = false;
	// Fields.

	/**
	 * The state in which the window of the dockable should be after performing the action.
	 * This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 */
	private int newDockableState;
	/**
	 * The dockable whose window state is changed by this action.
	 */
	private Dockable dockable;

	// Constructors.

	/**
	 * Constructs an action to put the dockable in the given state.
	 *
	 * @param    dockable            The state of this dockable will be changed by this action.
	 * @param    newDockableState    The new state for the dockable. This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 */
	public DefaultDockableStateAction(Dockable dockable, int newDockableState) {
		this.dockable = dockable;
		this.newDockableState = newDockableState;
	}

	/**
	 * Constructs an action to put the dockable in the given state.
	 *
	 * @param name The name and short description of the action.
	 * @param    dockable            The state of this dockable will be changed by this action.
	 * @param    newDockableState    The new state for the dockable. This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 */
	public DefaultDockableStateAction(Dockable dockable, int newDockableState, String name) {
		super(name);
		this.dockable = dockable;
		this.newDockableState = newDockableState;
	}

	/**
	 * @param name The name and short description of the action.
	 * @param icon The icon of the action.
	 * @param    dockable            The state of this dockable will be changed by this action.
	 * @param    newDockableState    The new state for the dockable. This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 */
	public DefaultDockableStateAction(Dockable dockable, int newDockableState, String name, Icon icon) {
		super(name, icon);
		putValue(SHORT_DESCRIPTION, name);
		this.dockable = dockable;
		this.newDockableState = newDockableState;
	}

	// Implementations of ActionListener.

	/**
	 * <p>
	 * Externalizes the dockable.
	 * </p>
	 * <p>
	 * If the dockable is in a dock, it is removed from the dock.
	 * If it is also maximized, it is removed from the maximizer.
	 * If it is minimized, it is removed from the minimizer
	 * Next it is added to its float dock.
	 * </p>
	 * <p>
	 * If the dockable is not in a dock, it is added to a float dock.
	 * </p>
	 */
	private static boolean externalize(Dockable dockableToChange) {
		if (TEST) System.out.println("externalize");

		// Use the wrapper round the dockable, we can find this in the model.
		Dockable dockableOfModel = DockingUtil.retrieveDockableOfDockModel(dockableToChange.getID());
		if (dockableOfModel == null) {
			dockableOfModel = dockableToChange;
		}

		// Search an externalizer.
		Visualizer externalizer = retrieveVisualizer(dockableOfModel, DockableState.EXTERNALIZED);
		if (externalizer == null) {
			// We could not externalize the dockable.
			return false;
		}

		// Remove the dockable from other visualizers.
		removeDockableFromVisualizer(dockableOfModel);

		// Is the dockable docked.
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {

			// Remove from dock.
			if (!removeDockableFromeDock(dockableOfModel)) {
				return false;
			}
		}

		// Externalize.
		dockableToChange.setState(DockableState.EXTERNALIZED, externalizer);
		externalizer.visualizeDockable(dockableOfModel);

		return true;

	}

	// Implementations of DockableStateAction.

	/**
	 * <p>
	 * Minimizes the dockable.
	 * </p>
	 * <p>
	 * If the dockable is in a dock, it is removed from the dock.
	 * if it is also maximized, it is removed from the maximize panel.
	 * Next it is added to minimize panel.
	 * </p>
	 * <p>
	 * If the dockable is not in a dock, it is added to minimize panel.
	 * </p>
	 */
	private static boolean minimize(Dockable dockableToChange) {
		if (TEST) System.out.println("minimize");

		// Use the wrapper round the dockable, we can find this in the model.
		Dockable dockableOfModel = DockingUtil.retrieveDockableOfDockModel(dockableToChange.getID());
		if (dockableOfModel == null) {
			dockableOfModel = dockableToChange;
		}

		// Search a minimize panel.
		Visualizer minimizePanel = retrieveVisualizer(dockableOfModel, DockableState.MINIMIZED);
		if (minimizePanel == null) {
			// We could not minimize the dockable.
			return false;
		}

		// Remove the dockable from other visualizers.
		removeDockableFromVisualizer(dockableOfModel);

		// Is the dockable docked.
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {
			if (dockableOfModel.getState() == DockableState.EXTERNALIZED) {
				removeDockableFromeExternalizer(dockableOfModel);
			} else {
				// Remove from dock.
				if (!removeDockableFromeDock(dockableOfModel)) {
					return false;
				}
			}
		}


		// Minimize.
		dockableOfModel.setState(DockableState.MINIMIZED, minimizePanel);
		minimizePanel.visualizeDockable(dockableOfModel);

		return true;

	}

	/**
	 * <p>
	 * Maximizes the dockable.
	 * </p>
	 * <p>
	 * First a maximize panel is searched for the dockable.
	 * When there is no maximize panel found, the dockable cannot be maximized.
	 * </p>
	 * <p>
	 * If the dockable is in a dock, it is hided in its dock.
	 * </p>
	 * <p>
	 * The dockable is maximized in the maximize panel.
	 * </p>
	 */
	private static boolean maximize(Dockable dockableToChange) {

		if (TEST) System.out.println("maximize");

		// Use the wrapper round the dockable, we can find this in the model.
		Dockable dockableOfModel = DockingUtil.retrieveDockableOfDockModel(dockableToChange.getID());
		if (dockableOfModel == null) {
			dockableOfModel = dockableToChange;
		}

		// Search a maximizer.
		Visualizer maximizer = retrieveVisualizer(dockableOfModel, DockableState.MAXIMIZED);
		if (maximizer == null) {
			// We could not maximize the dockable.
			return false;
		}

		// Remove the dockable from other visualizers.
		removeDockableFromVisualizer(dockableOfModel);

		// Close or restore all the maximized dockables.
		closeMaximizedDockables();

		// Is the dockable docked?
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {
			// Check if the maximizer is a component around the dockable.
			boolean maximizerContainsDockable = false;
			if ((maximizer instanceof Component) && (dock instanceof Component)) {
				if (SwingUtilities.isDescendingFrom((Component) dock, (Component) maximizer)) {
					maximizerContainsDockable = true;
				}
			}
			// Can the dockable be hidden in its current dock?
			if (maximizerContainsDockable && (dock instanceof DockableHider)) {
				// Hide dockable.
				((DockableHider) dock).hideDockable(dockableOfModel);
			} else {
				if (dockableOfModel.getState() == DockableState.EXTERNALIZED) {
					removeDockableFromeExternalizer(dockableOfModel);
				} else {
					// Remove the dockable from its current dock.
					if (!removeDockableFromeDock(dockableOfModel)) {
						return false;
					}
				}
			}
		}

		// Maximize.
		dockableOfModel.setState(DockableState.MAXIMIZED, maximizer);
		maximizer.visualizeDockable(dockableOfModel);

		return true;
	}

	// Getters / setters.

	/**
	 * <p>
	 * Restores the dockable.
	 * </p>
	 * <p>
	 * If the dockable is already in a dock, and if it is maximized,
	 * remove the dockable from the maximize panel. Next restore the dockable.
	 * </p>
	 * <p>
	 * If the dockable is not in a dock, but it is in a minimize panel, then
	 * remove it from the minimize panel and add the dockable using its docking path.
	 * </p>
	 * <p>
	 * If the dockable is not in a dock, and not in a minimize panel, then
	 * add the dockable using its docking path.
	 * </p>
	 */
	private static boolean restore(Dockable dockableToChange) {
		if (TEST) System.out.println("restore");

		// Use the wrapper round the dockable, we can find this in the model.
		Dockable dockableOfModel = DockingUtil.retrieveDockableOfDockModel(dockableToChange.getID());
		if (dockableOfModel == null) {
			dockableOfModel = dockableToChange;
		}

		// Get the current state of the dockable.
		int currentDockableState = dockableOfModel.getState();

		//Was the dockable maximized?
		if (currentDockableState == DockableState.MAXIMIZED) {
			removeDockableFromVisualizer(dockableOfModel);

			LeafDock dock = dockableOfModel.getDock();
			if (dock != null) {
				// Maybe the dockable was already restored.
				if (dockableIsHidden(dockableOfModel, (DockableHider) dock)) {
					((DockableHider) dock).restoreDockable(dockableOfModel);
					dockableOfModel.setState(DockableState.NORMAL, dock);
				}
				return true;
			}
		}

		// Remove the dockable from the visualizer.
		if ((currentDockableState == DockableState.MINIMIZED) ||
				(currentDockableState == DockableState.EXTERNALIZED)) {
			removeDockableFromVisualizer(dockableOfModel);
		}

		// Close or restore all the maximized dockables.
		closeMaximizedDockables();

		// Add the dockable using the docking path.
		DockingPathModel dockingPathModel = DockingManager.getDockingPathModel();
		DockingPath dockingPath = dockingPathModel.getDockingPath(dockableOfModel.getID());
		boolean succes = false;
		if (dockingPath != null) {
			succes = DockingManager.getDockingExecutor().changeDocking(dockableOfModel, dockingPath);
		}
		if (!succes) {
			// We could not add the dockable to the dock model where it was before.
			// Iterate over the root docks of the dock model.
			DockModel dockModel = DockingManager.getDockModel();
			if (dockModel == null) {
				throw new NullPointerException("Dock model of docking manager null.");
			}
			for (int index = 0; index < dockModel.getOwnerCount(); index++) {
				Iterator iterator = dockModel.getRootKeys(dockModel.getOwner(index));
				while ((iterator.hasNext()) && (!succes)) {
					String key = (String) iterator.next();
					Dock rootDock = dockModel.getRootDock(key);
					succes = DockingManager.getDockingExecutor().changeDocking(dockableOfModel, rootDock);
				}
				if (succes) {
					break;
				}
			}
		}

		return succes;

	}

	private static boolean dockableIsHidden(Dockable dockableOfModel, DockableHider dock) {
		for (int index = 0; index < dock.getHiddenDockableCount(); index++) {
			if (dockableOfModel.equals(dock.getHiddenDockable(index))) {
				return true;
			}
		}
		return false;
	}

	// Private metods.

	/**
	 * <p>
	 * Closes the dockable.
	 * </p>
	 * <p>
	 * If the dockable is in a dock, it is removed from the dock.
	 * </p>
	 * <p>
	 * If the dockable is in a maximize panel, it is removed from that panel.
	 * </p>
	 * <p>
	 * If the dockable is in a minimize panel, it is removed from that panel.
	 * </p>
	 */
	private static boolean close(Dockable dockableToChange) {

		if (TEST) System.out.println("close");

		// Use the wrapper round the dockable, we can find this in the model.
		Dockable dockableOfModel = DockingUtil.retrieveDockableOfDockModel(dockableToChange.getID());
		if (dockableOfModel == null) {
			dockableOfModel = dockableToChange;
		}

		boolean canClose = true;

		// Remove the dockable from visualizer.
		removeDockableFromVisualizer(dockableOfModel);

		// Is the dockable docked?
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {
			if (dockableOfModel.getState() == DockableState.EXTERNALIZED) {
				removeDockableFromeExternalizer(dockableOfModel);
			} else {
				// Remove the dockable from the dock.
				canClose = removeDockableFromeDock(dockableOfModel);
			}
		}

		if (canClose) {
			dockableOfModel.setState(DockableState.CLOSED, null);
		}

		return canClose;

	}

	// Private static metods.

	/**
	 * Removes the dockable from the visualizer, if the dockable is visualized.
	 *
	 * @return True if the dockable was visualized and removed from the visualizer.
	 * @throws IllegalArgumentException    If the dockable is visualized, but it could not be removed from the visualizer.
	 */
	private static boolean removeDockableFromVisualizer(Dockable dockableOfModel) {


		// Get the current state of the dockable.
		int currentDockableState = dockableOfModel.getState();
		if ((currentDockableState != DockableState.CLOSED) &&
				(currentDockableState != DockableState.NORMAL)) {
			// Get the visualizer of the dockable.
			Object visualizer = dockableOfModel.getVisualizer();
			if (visualizer instanceof Visualizer) {
				Visualizer stateVisualizer = (Visualizer) visualizer;
				if (stateVisualizer.getState() == currentDockableState) {

					// OK, we can remove the visualized the dockable.
					stateVisualizer.removeVisualizedDockable(dockableOfModel);
					return true;
				}
			}

			throw new IllegalStateException("The dockable is visualized, but it could not be removed from the visualizer");
		}

		return false;
	}

	/**
	 * Removes the dockable from its dock, if it is docked in a dock.
	 *
	 * @return True if the dockable is not docked in a dock anymore.
	 */
	private static boolean removeDockableFromeDock(Dockable dockableOfModel) {

		// Is the dockable docked.
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {
			// Save the docking path.
			addDockingPath(dockableOfModel);

			// Remove from dock.
			boolean succes = DockingManager.getDockingExecutor().changeDocking(dockableOfModel, (Dock) null);

			if (succes) {
				// Clean the dock from which the dockable is removed.
				DockingManager.getDockingExecutor().cleanDock(dock, false);
			}
			return succes;
		}

		return true;

	}

	/**
	 * Removes the dockable from its externalze dock, if it is externalized.
	 *
	 * @return True if the dockable is not docked in an externalize dock anymore.
	 */
	private static boolean removeDockableFromeExternalizer(Dockable dockableOfModel) {

		// Is the dockable docked.
		LeafDock dock = dockableOfModel.getDock();
		if (dock != null) {
			// Remove from dock.
			boolean succes = DockingManager.getDockingExecutor().changeDocking(dockableOfModel, (Dock) null);

			if (succes) {
				// Clean the dock from which the dockable is removed.
				DockingManager.getDockingExecutor().cleanDock(dock, false);
			}
			return succes;
		}

		return true;

	}

	/**
	 * Retrieves a visualizer for the dockable.
	 * If the dockable is in a dock, the visualizer is searched around the current dock.
	 * If the dockable is not in a dock. The docking path of the dock is used.
	 * The deepest dock of the docking path that still exists, is taken.
	 * A visualizer is searched around the dock.
	 *
	 * @return The visualizer. Null if no visualizer was found.
	 */
	private static Visualizer retrieveVisualizer(Dockable dockableOfModel, int dockableState) {

		// Is the dockable docked?
		LeafDock dock = dockableOfModel.getDock();
		if (dock == null) {

			// Can we find a visualizer in the dock model for the dockable.
			Visualizer[] visualizers = retrieveVisualizersOfDockModel(dockableOfModel, dockableState);
			for (int index = 0; index < visualizers.length; index++) {
				if (visualizers[index].canVisualizeDockable(dockableOfModel)) {
					return visualizers[index];
				}
			}

			// Can we find a general visualizer in the dock model.
			visualizers = retrieveVisualizersOfDockModel(dockableState);
			for (int index = 0; index < visualizers.length; index++) {
				if (visualizers[index].canVisualizeDockable(dockableOfModel)) {
					return visualizers[index];
				}
			}

		} else {

			// Can we find a visualizer around the content of the dockable?
			Component dockableContent = dockableOfModel.getContent();
			Visualizer visualizer = findVizualizerAroundComponent(dockableState, dockableContent);
			if (visualizer != null) {
				return visualizer;
			}

			// Can we find a visualizer in the dock model for the dockable.
			Visualizer[] visualizers = retrieveVisualizersOfDockModel(dockableOfModel, dockableState);
			for (int index = 0; index < visualizers.length; index++) {
				if (visualizers[index].canVisualizeDockable(dockableOfModel)) {
					return visualizers[index];
				}
			}

			// Can we find a general visualizer in the dock model.
			visualizers = retrieveVisualizersOfDockModel(dockableState);
			for (int index = 0; index < visualizers.length; index++) {
				if (visualizers[index].canVisualizeDockable(dockableOfModel)) {
					return visualizers[index];
				}
			}

		}

		return null;

	}

	private static Visualizer findVizualizerAroundComponent(int dockableState, Component component) {
		// Can we find a visualizer around the component?
		Visualizer visualizer = (Visualizer) SwingUtilities.getAncestorOfClass(Visualizer.class, component);
		while ((visualizer != null) && (visualizer.getState() != dockableState)) {
			visualizer = (Visualizer) SwingUtilities.getAncestorOfClass(Visualizer.class, (Component) visualizer);
		}
		if ((visualizer != null) && (visualizer.getState() == dockableState)) {
			return visualizer;
		}
		return null;

	}

	private static void addDockingPath(Dockable dockable) {

		if (dockable.getDock() != null) {
			// Create the docking path of the dockable.
			DockingPath dockingPath = DefaultDockingPath.createDockingPath(dockable);
			DockingManager.getDockingPathModel().add(dockingPath);
		}

	}

	/**
	 * Retrieves the possible visualizers for a dockable in a dock model.
	 * The visualizer has to belong to the same owner window as the dock where the dockable
	 * is currently docked or as the root dock of the docking path where the dockable was last docked before.
	 *
	 * @param dockable The dockable for which minimizers have to be retrieved.
	 * @param    dockableState    The visualizer has to be able to visualize that dockable in this state.
	 * @return An array with the possible visualizers. Cannot be null, but the length can be 0.
	 */
	private static Visualizer[] retrieveVisualizersOfDockModel(Dockable dockable, int dockableState) {

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// Get the root dock key for the dockable.
		String rootDockKey = null;

		// Is the dockable docked?
		Dock dock = dockable.getDock();
		if (dock != null) {
			Dock rootDock = DockingUtil.getRootDock(dock);
			rootDockKey = DockingUtil.getRootDockKey(rootDock);
		}
		if (rootDockKey == null || (dockable.getState() == DockableState.MAXIMIZED)) {
			DockingPathModel dockingPathModel = DockingManager.getDockingPathModel();
			if (dockingPathModel != null) {
				DockingPath dockingPath = dockingPathModel.getDockingPath(dockable.getID());
				if (dockingPath != null) {
					rootDockKey = dockingPath.getRootDockKey();

					// Try to find visualizers around parent docks in the docking path.
					if (dockingPath.getDockCount() > 0) {
						Dock rootDock = dockingPath.getDock(0);
						if (DockingUtil.containsRootDock(dockModel, rootDock)) {
							for (int index = dockingPath.getDockCount() - 1; index >= 0; index--) {
								Dock pathDock = dockingPath.getDock(index);
								if (DockingUtil.containsDock(rootDock, pathDock)) {
									// Find a visualizer around the dock.
									if (pathDock instanceof Component) {
										Visualizer visualizer = findVizualizerAroundComponent(dockableState, (Component) pathDock);
										if (visualizer != null) {
											Visualizer[] visualizers = new Visualizer[1];
											visualizers[0] = visualizer;
											return visualizers;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// The list with the visualizers.
		List visualizers = new ArrayList(1);

		// Iterate over the visualizers of the owner window.
		if (rootDockKey != null) {

			Window owner = DockingUtil.getWindowOwner(rootDockKey);
			if (owner != null) {
				Iterator visualizerKeysIterator = dockModel.getVisualizerKeys(owner);
				while (visualizerKeysIterator.hasNext()) {
					String key = (String) visualizerKeysIterator.next();
					Visualizer visualizer = dockModel.getVisualizer(key);
					if (visualizer.getState() == dockableState) {
						visualizers.add(visualizer);
					}
				}
			}
		}

		Visualizer[] visualizerArray = new Visualizer[visualizers.size()];
		visualizerArray = (Visualizer[]) visualizers.toArray(visualizerArray);

		return visualizerArray;

	}

	/**
	 * Retrieves the possible visualizers in a dock model for a given dockable state.
	 *
	 * @param    dockableState    The visualizer has to be able to visualize a dockable in this state.
	 * @return An array with the possible visualizers. Cannot be null, but the length can be 0.
	 */
	private static Visualizer[] retrieveVisualizersOfDockModel(int dockableState) {

		// Get the dock model.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}

		// The list with the visualizers.
		List visualizers = new ArrayList(1);

		// Iterate over the owner windows.
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);
			Iterator visualizerKeysIterator = dockModel.getVisualizerKeys(owner);
			while (visualizerKeysIterator.hasNext()) {
				String key = (String) visualizerKeysIterator.next();
				Visualizer visualizer = dockModel.getVisualizer(key);
				if (visualizer.getState() == dockableState) {
					visualizers.add(visualizer);
				}
			}
		}

		Visualizer[] visualizerArray = new Visualizer[visualizers.size()];
		visualizerArray = (Visualizer[]) visualizers.toArray(visualizerArray);

		return visualizerArray;

	}

	/**
	 * Closes or restores all the dockables that are maximized
	 * in a visualizer.
	 */
	private static void closeMaximizedDockables() {

		// Iterate over the visualizers of the owner.
		DockModel dockModel = DockingManager.getDockModel();
		if (dockModel == null) {
			throw new NullPointerException("Dock model of docking manager null.");
		}
		// Iterate over the owners.
		for (int index = 0; index < dockModel.getOwnerCount(); index++) {
			Window owner = dockModel.getOwner(index);

			// Iterate over the visualizers of the owner.
			Iterator visualizers = dockModel.getVisualizerKeys(owner);
			while (visualizers.hasNext()) {
				String visualizerKey = (String) visualizers.next();
				Visualizer visualizer = dockModel.getVisualizer(visualizerKey);

				// Do we have a maximizer?
				if (visualizer.getState() == DockableState.MAXIMIZED) {
					// Restore or close the dockables visualized by this maximizer.
					for (int dockableIndex = 0; dockableIndex < visualizer.getVisualizedDockableCount(); dockableIndex++) {
						Dockable dockableToRemove = visualizer.getVisualizedDockable(dockableIndex);
						visualizer.removeVisualizedDockable(dockableToRemove);
						LeafDock dock = dockableToRemove.getDock();
						if (dock != null) {
							// Restore the maximized dockable.
							((DockableHider) dock).restoreDockable(dockableToRemove);
							dockableToRemove.setState(DockableState.NORMAL, dock);
						} else {
							// Close the dockable.
							dockableToRemove.setState(DockableState.CLOSED, null);
						}
					}
				}
			}
		}
	}

	public void actionPerformed(ActionEvent actionEvent) {
		changeDockableState(dockable);
	}

	public Dockable getDockable() {
		return dockable;
	}

	/**
	 * Sets the dockable whose state is changed by this action.
	 *
	 * @param dockable The dockable whose state is changed by this action.
	 */
	public void setDockable(Dockable dockable) {
		this.dockable = dockable;
	}

	public int getNewDockableState() {
		return newDockableState;
	}

	/**
	 * Sets the state in which the dockable should be after performing the action.
	 * This is a constant defined by {@link com.javadocking.dockable.DockableState}.
	 *
	 * @param newWindowState The state in which the dockable should be after performing the action.
	 */
	public void setNewDockableState(int newWindowState) {
		this.newDockableState = newWindowState;
	}

	/**
	 * Changes the state of the dockable to the new state.
	 * First is checked if the new state is allowed for the dockable.
	 * If te dockable is not already in the new state, the state of the dockable is changed.
	 * <p>
	 * When the dockable is a composite, the action is performed for every chid dockable.
	 */
	private void changeDockableState(Dockable dockableToChange) {

		// Check if the dockable is a composite.
		if (dockableToChange instanceof CompositeDockable) {

			CompositeDockable compositeDockable = (CompositeDockable) dockableToChange;

			// Perform the action for every child dockable.
			for (int index = 0; index < compositeDockable.getDockableCount(); index++) {
				changeDockableState(compositeDockable.getDockable(index));
			}

		} else {

			// Check if the new state is allowed for this dockable.
			if ((dockableToChange.getPossibleStates() & newDockableState) != 0) {
				// Check if the new state is different from the current state.
				if (newDockableState != dockableToChange.getState()) {
					// Do we have a close action.
					if (newDockableState == DockableState.CLOSED) {
						close(dockableToChange);
					} else if (newDockableState == DockableState.NORMAL) {
						restore(dockableToChange);
					} else if (newDockableState == DockableState.MAXIMIZED) {
						maximize(dockableToChange);
					} else if (newDockableState == DockableState.MINIMIZED) {
						minimize(dockableToChange);
					} else if (newDockableState == DockableState.EXTERNALIZED) {
						externalize(dockableToChange);
					}
				} else {
					if (TEST) System.out.println("The dockable is already in this state [" + newDockableState + "].");
				}
			}
		}

	}

}
