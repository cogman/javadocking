package com.javadocking.visualizer;

import com.javadocking.DockingManager;
import com.javadocking.component.SelectableHeader;
import com.javadocking.dock.Dock;
import com.javadocking.dock.Position;
import com.javadocking.dock.docker.Docker;
import com.javadocking.dockable.ButtonDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.DockingMode;
import com.javadocking.util.PropertiesUtil;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This visualizer shows minimized dockables in components that can be docked and moved around by themself.
 * </p>
 * <p>
 * Information on using docking minimizers is in
 * <a href="http://www.javadocking.com/developerguide/visualizer.html#DockingMinimizer" target="_blank">
 * How to Use Visualizers (Minimizers and Maximizers)</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * For the dockable that is minimized, a header is created with the method
 * {@link com.javadocking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}
 * of the component factory of the docking manager {@link com.javadocking.DockingManager#getComponentFactory()}.
 * </p>
 * <p>
 * A dockable is created around this header. This small dockable
 * is docked in the model by a {@link com.javadocking.dock.docker.Docker}.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DockingMinimizer implements Visualizer {

	/**
	 * The suffix that is used to create the ID of the dockable around the minimized header.
	 */
	private static final String MINIMIZED_DOCKABLE_ID_SUFFIX = "[MINIMIZED]";
	/**
	 * The name of the <code>dockableIds</code> property.
	 */
	private static final String PROPERTY_DOCKABLE_IDS = "dockableIds";
	/**
	 * The name of the <code>selectedDockableIds</code> property.
	 */
	private static final String PROPERTY_SELECTED_DOCKABLE_IDS = "selectedDockableIds";


	/**
	 * The minimized dockables of this panel.
	 */
	private List minimizedDockables = new ArrayList();
	/**
	 * Mapping between the minimized dockables and their minimized components.
	 */
	private Map minimizedHeaders = new HashMap();
	/**
	 * Mapping between the minimized dockables and the dockables around their minimized components.
	 */
	private Map minimizedHeaderDockables = new HashMap();
	/**
	 * The object that docks the dockables around the minimized headers in docks.
	 */
	private Docker docker;
	/**
	 * Listens to selection changes of the headers.
	 */
	private SelectionChangeListener selectionChangeListener = new SelectionChangeListener();

	// Constructors.

	/**
	 * Constructs a visualizer that shows minimized dockables in components
	 * that can be docked and moved around by themselves.
	 *
	 * @param    docker     The object that docks the dockables around the minimized headers.
	 */
	public DockingMinimizer(Docker docker) {

		// Check that the docker is not null.
		if (docker == null) {
			throw new NullPointerException("Docker null");
		}
		this.docker = docker;

	}

	// Implementations of Visualizer.

	public int getState() {
		return DockableState.MINIMIZED;
	}

	public boolean canVisualizeDockable(Dockable dockableToVisualize) {

		// Check the dockable is not null.
		if (dockableToVisualize == null) {
			throw new NullPointerException("Dockable to minimize null.");
		}

		// Is the dockable already minimized in this panel?
		if (minimizedDockables.contains(dockableToVisualize)) {
			return false;
		}

		return true;

	}

	public void visualizeDockable(Dockable dockableToVisualize) {

		// Check the dockable is not null.
		if (dockableToVisualize == null) {
			throw new NullPointerException("Dockable to minimize null.");
		}

		// Is the dockable already minimized in this panel?
		if (minimizedDockables.contains(dockableToVisualize)) {
			return;
		}

		// Set the dockable minimized.
		dockableToVisualize.setState(DockableState.MINIMIZED, this);

		// Create the minimized header.
		SelectableHeader dockableHeader = DockingManager.getComponentFactory().createMinimizeHeader(dockableToVisualize, Position.TOP);
		dockableHeader.addPropertyChangeListener(selectionChangeListener);

		// Create a dockable around this header.
		ButtonDockable buttonDockable = new ButtonDockable(createMinimizedHeaderDockableID(dockableToVisualize),
				(Component) dockableHeader, DockingMode.MINIMIZE_BAR | DockingMode.FLOAT);

		// Dock the dockable.
		boolean result = docker.dock(buttonDockable);
		if (result) {
			minimizedDockables.add(dockableToVisualize);
			minimizedHeaderDockables.put(dockableToVisualize, buttonDockable);
			minimizedHeaders.put(dockableToVisualize, dockableHeader);
		} else {
			// Reset the state.
			dockableToVisualize.setState(DockableState.CLOSED, null);
		}
	}

	public Dockable getVisualizedDockable(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getVisualizedDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dockable) minimizedDockables.get(index);

	}

	public int getVisualizedDockableCount() {
		return minimizedDockables.size();
	}

	public void removeVisualizedDockable(Dockable dockableToRemove) {

		// Check if the dockable is minimized in this minimizer.
		if (!minimizedDockables.contains(dockableToRemove)) {
			throw new IllegalArgumentException("The dockable is not minimized in this minimizer.");
		}

		SelectableHeader minimizedComponent = (SelectableHeader) minimizedHeaders.get(dockableToRemove);
		minimizedComponent.removePropertyChangeListener(selectionChangeListener);
		Dockable buttonDockable = (Dockable) minimizedHeaderDockables.get(dockableToRemove);
		Dock dockToClean = buttonDockable.getDock();
		DockingManager.getDockingExecutor().changeDocking(buttonDockable, (Dock) null);
		if (dockToClean != null) {
			DockingManager.getDockingExecutor().cleanDock(dockToClean, false);
		}

		minimizedHeaders.remove(dockableToRemove);
		minimizedDockables.remove(dockableToRemove);
		minimizedHeaderDockables.remove(dockableToRemove);

	}

	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException {

		// Load the IDs of the dockables.
		String[] dockableIdArray = new String[0];
		dockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);

		// Iterate over the IDs of the dockables.
		for (int index = 0; index < dockableIdArray.length; index++) {
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(dockableIdArray[index]);
			if (dockableObject != null) {
				if (dockableObject instanceof Dockable) {
					Dockable dockable = (Dockable) dockableObject;

					// Create the minimized dockable.
					dockable.setState(DockableState.MINIMIZED, this);
					minimizedDockables.add(dockable);
//					SelectableDockableHeader dockableHeader = (SelectableDockableHeader)DockingManager.getComponentFactory().createMinimizeHeader(dockable, Position.TOP);
					SelectableHeader dockableHeader = (SelectableHeader) DockingManager.getComponentFactory().createMinimizeHeader(dockable, Position.TOP);
					dockableHeader.addPropertyChangeListener(selectionChangeListener);
					minimizedHeaders.put(dockable, dockableHeader);
					ButtonDockable buttonDockable = new ButtonDockable(createMinimizedHeaderDockableID(dockable),
							(Component) dockableHeader, DockingMode.MINIMIZE_BAR | DockingMode.FLOAT);
					dockablesMap.put(buttonDockable.getID(), buttonDockable);
					minimizedHeaderDockables.put(dockable, buttonDockable);
					dockable.setState(DockableState.MINIMIZED, this);
				} else {
					throw new IOException("The values in the dockables mapping should be of type com.javadocking.Dockable.");
				}
			}
		}

		// Load the IDs of the selected dockables.
		String[] selectedDockableIdArray = new String[0];
		selectedDockableIdArray = PropertiesUtil.getStringArray(properties, prefix + PROPERTY_SELECTED_DOCKABLE_IDS, selectedDockableIdArray);

		// Deselect all the dockables.
		deselectAllMinimizedHeaders(null);

		// Iterate over the IDs of the selected dockables.
		for (int index = 0; index < selectedDockableIdArray.length; index++) {
			// Try to get the dockable.
			Object dockableObject = dockablesMap.get(selectedDockableIdArray[index]);
			if (dockableObject != null) {
				if (dockableObject instanceof Dockable) {
					// Select the dockable.
					Dockable dockable = (Dockable) dockableObject;
					//SelectableDockableHeader dockableHeader = (SelectableDockableHeader)minimizedHeaders.get(dockable);
					SelectableHeader dockableHeader = (SelectableHeader) minimizedHeaders.get(dockable);
					selectMinimizedHeader(dockableHeader, true);
				}
			}
		}

		// Load the properties of the border docker.
		docker.loadProperties(prefix + "docker.", properties, dockablesMap);

	}


	public void saveProperties(String prefix, Properties properties) {

		// Save the properties of the border docker.
		docker.saveProperties(prefix + "docker.", properties);

		// Save the IDs of the dockables and the IDs of the selected dockables.
		String[] dockableIdArray = new String[getVisualizedDockableCount()];
		List selectedDockableIDs = new ArrayList(1);
		for (int index = 0; index < dockableIdArray.length; index++) {
			// Get the ID of the dockable.
			Dockable minimizedDockable = getVisualizedDockable(index);
			dockableIdArray[index] = minimizedDockable.getID();
			//SelectableDockableHeader dockableHeader = (SelectableDockableHeader)minimizedHeaders.get(minimizedDockable);
			SelectableHeader dockableHeader = (SelectableHeader) minimizedHeaders.get(minimizedDockable);
			if (dockableHeader.isSelected()) {
				selectedDockableIDs.add(minimizedDockable.getID());
			}
		}
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_DOCKABLE_IDS, dockableIdArray);
		String[] selectedDockableIdArray = new String[selectedDockableIDs.size()];
		selectedDockableIdArray = (String[]) selectedDockableIDs.toArray(selectedDockableIdArray);
		PropertiesUtil.setStringArray(properties, prefix + PROPERTY_SELECTED_DOCKABLE_IDS, selectedDockableIdArray);

	}

	// Protected metods.

	/**
	 * Creates the ID for the dockable around the header component of a minimized dockable.
	 *
	 * @return The ID for the dockable around the header component of a minimized dockable.
	 */
	protected String createMinimizedHeaderDockableID(Dockable dockable) {
		return dockable.getID() + MINIMIZED_DOCKABLE_ID_SUFFIX;
	}

	// Private metods.


	/**
	 * Deselects all the headers, except the given object.
	 *
	 * @param    notTodeselectObject        This object should not be deselected. Can be null.
	 */
	private void deselectAllMinimizedHeaders(Object notTodeselectObject) {

		// Iterate over all the headers.
		Iterator headerIterator = minimizedHeaders.values().iterator();
		while (headerIterator.hasNext()) {
			SelectableHeader selectableDockableHeader = (SelectableHeader) headerIterator.next();
			if (!selectableDockableHeader.equals(notTodeselectObject)) {
				selectableDockableHeader.removePropertyChangeListener(selectionChangeListener);
				selectableDockableHeader.setSelected(false);
				selectableDockableHeader.addPropertyChangeListener(selectionChangeListener);
			}
		}
	}

	/**
	 * Selects the header.
	 *
	 * @param    objectToSelect            This object should be selected. Not null.
	 * @param    selected                True when the header has to be selectd, false otherwise.
	 */
	private void selectMinimizedHeader(Object objectToSelect, boolean selected) {

		// Iterate over all the headers.
		Iterator headerIterator = minimizedHeaders.values().iterator();
		while (headerIterator.hasNext()) {
			//SelectableDockableHeader selectableDockableHeader = (SelectableDockableHeader)headerIterator.next();
			SelectableHeader selectableDockableHeader = (SelectableHeader) headerIterator.next();
			if (selectableDockableHeader.equals(objectToSelect)) {
				selectableDockableHeader.removePropertyChangeListener(selectionChangeListener);
				selectableDockableHeader.setSelected(selected);
				selectableDockableHeader.addPropertyChangeListener(selectionChangeListener);
			}
		}

	}

	// Private classes.

	/**
	 * Gets the object that docks the dockables around the minimized headers in docks.
	 *
	 * @return The object that docks the dockables around the minimized headers in docks.
	 */
	public Docker getDocker() {
		return docker;
	}

	// Getters / Setters.

	/**
	 * Sets the object that docks the dockables around the minimized headers in docks.
	 *
	 * @param docker The object that docks the dockables around the minimized headers in docks.
	 */
	public void setDocker(Docker docker) {
		this.docker = docker;
	}

	private class SelectionChangeListener implements PropertyChangeListener {

		// Implementations of PropertyChangeListener.

		public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

			if (propertyChangeEvent.getPropertyName().equals("selected")) {
				Object newValue = propertyChangeEvent.getNewValue();
				if (newValue instanceof Boolean) {
					boolean newSelected = ((Boolean) newValue).booleanValue();
					Object source = propertyChangeEvent.getSource();
					if (newSelected) {
						// Deselect all the headers a select the selected.
						deselectAllMinimizedHeaders(source);
					} else {
						// Deselect the header.
						selectMinimizedHeader(source, false);
					}
				}
			}

		}

	}

}
