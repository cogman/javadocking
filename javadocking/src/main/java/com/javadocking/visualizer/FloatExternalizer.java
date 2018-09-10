package com.javadocking.visualizer;

import com.javadocking.DockingManager;
import com.javadocking.dock.Dock;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This is the default externalizer. It shows the externalized dockable in a floating
 * window that contains an {@link ExternalizeDock}.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class FloatExternalizer implements Externalizer {

	// Static fields.

	/**
	 * The name externalizeDock that will be used to create property names for specifying properties of externalize docks.
	 */
	private static final String EXTERNALIZE_DOCK_PREFIX = "externalizeDock";
	/**
	 * The name of the <code>hidden</code> property.
	 */
	private static final String PROPERTY_HIDDEN = "hidden";

	// Fields.

	/**
	 * True when the float dock is hidden, false otherwise.
	 */
	private boolean hidden = false;
	/**
	 * The list with the child docks of this dock.
	 */
	private List externalizeDocks = new LinkedList();
	/**
	 * The mapping between the docks and there floating windows.
	 */
	private Map externalizeDockWindows = new HashMap();
	/**
	 * The window that owns the floating windows created by this externalizer.
	 */
	private Window owner;
	/**
	 * This is the listener that will listen to the window closing events of all the floating windows
	 * created by this class. When the window is closed, the child dock is removed from this dock.
	 */
	private WindowClosingListener windowClosingListener = new WindowClosingListener();
	/**
	 * This is the listener that will listen to the window focus events of all the floating windows
	 * created by this class.
	 */
	private List windowFocusListeners = new ArrayList();
	/**
	 * Listens to the window events on the owner window of the float dock. Does the appropriate
	 * actions with the child windows.
	 */
	private OwnerWindowListener ownerWindowListener = new OwnerWindowListener();
	/**
	 * The support for handling the property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	// Constructors.

	/**
	 * Constructs an externalizer with the given window as owner for the windows.
	 *
	 * @param    owner                The window that owns the floating windows created by this dock.
	 */
	public FloatExternalizer(Window owner) {
		setOwner(owner);
	}

	// Implementations of Externalizer.

	/**
	 * Moves the externalized dockable in this float dock.
	 * The dockable should be externalized in a child dock of this float dock.
	 * The dockable state should be {@link DockableState#EXTERNALIZED}.
	 *
	 * @param dockable       The dockable to move.
	 * @param position       The location, where the dockable will be placed.
	 * @param dockableOffset The offset of the mouse position in the externalizer.
	 */
	public void moveExternalizedDockable(Dockable dockable, Point position, Point dockableOffset) {

		if (dockable.getState() != DockableState.EXTERNALIZED) {
			throw new IllegalStateException("The dockable shoud be in state [com.javadocking.dockable.DockableState.EXTERNALIZED].");
		}

		// The dockable is already externalized.
		LeafDock leafDock = dockable.getDock();
		if (!(leafDock instanceof ExternalizeDock)) {
			throw new IllegalStateException("The dock of the externalized dockable is a ["
					+ leafDock.getClass()
					+ "]. It should be a [com.javadocking.visualizer.Externalizer].");
		}
		ExternalizeDock childDock = (ExternalizeDock) leafDock;

		if (!this.equals(childDock.getExternalizer())) {
			throw new IllegalStateException("The dockable is externalized in another [com.javadocking.dock.FloatDock].");
		}
		moveDock(childDock, position, dockableOffset);

	}

	public void visualizeDockable(Dockable dockable) {

		// Check if we can externalize the dockable.
//		if (!canVisualizeDockable(dockable))
//		{
//			return;
//		}

		// Get the location for the dockable.
		Point ownerLocation = getOwner().getLocation();
		Dimension ownersize = getOwner().getSize();
		Dimension preferredSize = dockable.getContent().getPreferredSize();
		Point location = new Point(ownerLocation.x + ownersize.width / 2 - preferredSize.width / 2,
				ownerLocation.y + ownersize.height / 2 - preferredSize.height / 2);
		checkFloatingWindowLocation(location);

		// Create the externalize dock and add the dockable.
		dockable.setState(DockableState.EXTERNALIZED, this);
		ExternalizeDock externalizeDock = DockingManager.getComponentFactory().createExternalizer();
		externalizeDock.setExternalizer(this);
		externalizeDock.externalizeDockable(dockable);

		// Add the dock.
		addExternalizeDock(externalizeDock, location, null);

	}

	/**
	 * Moves the given child dock to the new location.
	 *
	 * @param childDock        The child dock that has to be moved to the new location.
	 * @param relativeLocation The new location for the child dock.
	 * @param dockableOffset   The mouse location where the dragging started, relatively to the child dock.
	 */
	private void moveDock(Dock childDock, Point relativeLocation, Point dockableOffset) {

//		// Inform the listeners about the move.
//		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, this, childDock));

		// Calculate the location for the floating window.
		Point point = new Point(relativeLocation.x - dockableOffset.x, relativeLocation.y - dockableOffset.y);

		// Get the floating window and change the location.
		Window window = SwingUtilities.getWindowAncestor((Component) childDock);
		window.setLocation(new Point(point.x, point.y));

//		// Inform the listeners about the move.
//		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, this, childDock));

	}

	public boolean canVisualizeDockable(Dockable dockableToVisualize) {

		if (hidden) {
			return false;
		}
		if ((dockableToVisualize.getPossibleStates() & DockableState.EXTERNALIZED) == 0) {
			return false;
		}

		return true;

	}

	public int getState() {
		return DockableState.EXTERNALIZED;
	}

	public Dockable getVisualizedDockable(int index) throws IndexOutOfBoundsException {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getVisualizedDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		ExternalizeDock externalizeDock = (ExternalizeDock) externalizeDocks.get(index);
		if (externalizeDock.getDockableCount() != 1) {
			throw new IllegalStateException("The externalize dock contains [" + externalizeDock.getDockableCount() + "] dockables.");
		}

		return externalizeDock.getDockable(0);

	}

	public int getVisualizedDockableCount() {
		return externalizeDocks.size();
	}

	public void removeVisualizedDockable(Dockable dockableToRemove) {

		// Search the externalize dock of the dockable.
		for (int index = 0; index < externalizeDocks.size(); index++) {
			ExternalizeDock externalizeDock = (ExternalizeDock) externalizeDocks.get(index);
			if (externalizeDock.getDockableCount() != 1) {
				throw new IllegalStateException("The externalize dock contains [" + externalizeDock.getDockableCount() + "] dockables.");
			}

			Dockable dockable = externalizeDock.getDockable(0);
			if (dockable.equals(dockableToRemove)) {
				// Get the parent window.
				Window window = (Window) externalizeDockWindows.get(externalizeDock);
				window.setVisible(false);
				window.dispose();

				// Remove it from the list with externalize docks.
				externalizeDocks.remove(externalizeDock);
				externalizeDockWindows.remove(externalizeDock);

				// Remove the dockable from its extyernalize dock.
				externalizeDock.removeDockable(dockableToRemove);

				return;
			}

		}
	}

	public void saveProperties(String prefix, Properties properties) {

		// Save if the externalizer is hidden.
		PropertiesUtil.setBoolean(properties, prefix + PROPERTY_HIDDEN, hidden);

		// Save the number of externalize docks.
		PropertiesUtil.setInteger(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + "externalizeDockCount" + ".", externalizeDocks.size());

		// Iterate over the child docks.
		int[] dim = new int[2];
		for (int index = 0; index < externalizeDocks.size(); index++) {
			// Get the externalize dock.
			ExternalizeDock dock = (ExternalizeDock) externalizeDocks.get(index);

			// Save the class of the externalize dock.
			PropertiesUtil.setString(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "class", dock.getClass().getName());

			// Save the properties of this externalize dock.
			dock.saveProperties(prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "dock", properties, null);

			// Get the parent window.
			Window window = (Window) externalizeDockWindows.get(dock);

			// Save the rectangle.
			dim[0] = window.getSize().width;
			dim[1] = window.getSize().height;
			PropertiesUtil.setIntegerArray(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "windowRectangle", dim);

			// Save the position. This is the (x,y) position and the index of the externalize dock in the list of externalize docks.
			Position.setPositionProperty(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "position", getExternalizeDockPosition(dock));
		}

	}

	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException {

		//TODO What if the dockable was not found.

		// Set the owner of the externalizer.
		setOwner(owner);

		// Set if the externalizer is hidden.
		hidden = PropertiesUtil.getBoolean(properties, prefix + PROPERTY_HIDDEN, hidden);

		// Get the number of externalize docks.
		int count = 0;
		count = PropertiesUtil.getInteger(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + "externalizeDockCount" + ".", count);

		// Create the dialogs for the children.
		for (int index = count - 1; index >= 0; index--) {
			// Get the class of the externalize dock.
			// Create the externalize dock object with the class name property.
			String className = null;
			className = PropertiesUtil.getString(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "class", className);
			Class clazz = null;
			ExternalizeDock dock = null;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException classNotFoundException) {
				throw new IOException("Could not find class [" + className + "] (ClassNotFoundException).");
			}
			try {
				dock = (ExternalizeDock) clazz.newInstance();
			} catch (IllegalAccessException illegalAccessException) {
				throw new IOException("Illegal acces to class [" + className + "] (IllegalAccessException).");
			} catch (InstantiationException instantiationException) {
				throw new IOException("Could not instantiate class [" + className + "] (InstantiationException).");
			} catch (ClassCastException classCastException) {
				throw new IOException("Class [" + className + "] is not a ExternalizeDock. (ClassCastException).");
			}
			dock.setExternalizer(this);
			dock.loadProperties(prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "dock", properties, null, dockablesMap, owner);

			// Add only the dock if it is not empty.
			if (!dock.isEmpty()) {
				// Set the state of the dockable.
				dock.getDockable(0).setState(DockableState.EXTERNALIZED, this);

				// Get the rectangle.
				int[] dim = null;
				dim = PropertiesUtil.getIntegerArray(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "windowRectangle", dim);
				Position position = null;
				position = Position.getPositionProperty(properties, prefix + EXTERNALIZE_DOCK_PREFIX + "." + index + "." + "position", position);
				if ((dim != null) && (dim.length == 2)) {
					addExternalizeDock(dock, new Point(position.getPosition(0), position.getPosition(1)), new Dimension(dim[0], dim[1]));
				}
			}
		}

	}


	// Getters / Setters.

	/**
	 * Gets the window that owns the floating windows created by this dock.
	 *
	 * @return The window that owns the floating windows created by this dock.
	 */
	public Window getOwner() {
		return owner;
	}

	/**
	 * Sets the window that owns the floating windows created by this dock.
	 *
	 * @param newOwner The window that owns the floating windows created by this dock.
	 */
	public void setOwner(Window newOwner) {

		if (owner != null) {
			owner.removeWindowListener(ownerWindowListener);
		}
		if (newOwner != null) {
			newOwner.addWindowListener(ownerWindowListener);
		}
		this.owner = newOwner;

	}

	/**
	 * Determines if the float dock is hidden.
	 *
	 * @return True, when the float dock is hidden, false otherwise.
	 */
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		boolean oldValue = this.hidden;
		this.hidden = hidden;
		hide(hidden);
		propertyChangeSupport.firePropertyChange("hidden", oldValue, hidden);

	}

	// Public methods.

	/**
	 * Externalizes the dockable in this float externalizer and puts the dockable in the given position.
	 *
	 * @param dockable The dockable externalize.
	 * @param position The location, where the dockable will be placed.
	 */
	public void externalizeDockable(Dockable dockable, Point position) {

		// Check if we can externalize the dockable.
//		if (!canVisualizeDockable(dockable))
//		{
//			return;
//		}

		// Create the externalize dock and add the dockable.
		dockable.setState(DockableState.EXTERNALIZED, this);
		ExternalizeDock externalizeDock = DockingManager.getComponentFactory().createExternalizer();
		externalizeDock.setExternalizer(this);
		externalizeDock.externalizeDockable(dockable);

		// Add the dock.
		addExternalizeDock(externalizeDock, position, null);

	}

	/**
	 * Adds the listener that will listen to the window focus events of all the floating windows
	 * created by this class.
	 *
	 * @param    windowFocusListener            The listener that will listen to the window focus events of all the floating windows
	 * created by this class.
	 */
	public void addWindowFocusListener(WindowFocusListener windowFocusListener) {
		windowFocusListeners.add(windowFocusListener);
	}

	/**
	 * Removes the given listener.
	 *
	 * @param windowFocusListener The listener that listens to the window focus events of all the floating windows
	 *                            created by this class.
	 */
	public void removeWindowFocusListener(WindowFocusListener windowFocusListener) {
		windowFocusListeners.remove(windowFocusListener);
		for (Object o : externalizeDockWindows.values()) {
			Window window = (Window) o;
			window.removeWindowFocusListener(windowFocusListener);
		}

	}

	// Protected methods.

	/**
	 * Moves the location inside the screen where the floating window has to be placed.
	 *
	 * @param location The location where the floating window will be put,
	 *                 if the requested location is outside the screen.
	 */
	protected void getDefaultFloatingWindowLocation(Point location) {

		// Return the position of the defaultWindowLocation.
		location.move(getOwner().getLocation().x, getOwner().getLocation().y);

	}

	// Private metods.

	/**
	 * Checks if the location is visible on the screen. If true, nothing is done.
	 * If false, the location is moved to the location set by the method
	 * {@link #getDefaultFloatingWindowLocation(Point)}.
	 *
	 * @param location The location that contains the normal location for the floating window.
	 *                 If this location is outside the screen, it is moved to the default floating window location.
	 */
	private void checkFloatingWindowLocation(Point location) {
		if (!SwingUtil.isLocationInScreenBounds(location)) {
			getDefaultFloatingWindowLocation(location);
		}
	}

	/**
	 * <p>
	 * Adds the given dock as child dock to this dock.
	 * </p>
	 * <p>
	 * The dock is put in a dialog. This dialog is created with the method
	 * {@link com.javadocking.component.SwComponentFactory#createJDialog(Window)} of
	 * the component factory of the {@link com.javadocking.DockingManager}.
	 * </p>
	 * <p>
	 * There is a border set around the dock. This border is created with the method
	 * {@link com.javadocking.component.SwComponentFactory#createFloatingBorder()} of
	 * the component factory of the docking manager.
	 * </p>
	 * <p>
	 * The floating window is put at the given location. The window will have the given size.
	 * If this size is null, then the preferred size is taken.
	 * </p>
	 *
	 * @param dock     The child dock that is added to this float dock in a floating dialog.
	 * @param location The location for the dialog.
	 * @param size     The size for the dialog. This may be null. In that case the preferred
	 *                 size is taken.
	 */
	private void addExternalizeDock(ExternalizeDock dock, Point location, Dimension size) {

//		// Inform the listeners.
//		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dock));

		// Calculate the location for the floating window.
		Point point = new Point(location.x, location.y);
		checkFloatingWindowLocation(point);

		// Create a panel for the dock.
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(DockingManager.getComponentFactory().createFloatingBorder());
		panel.add((Component) dock, BorderLayout.CENTER);

		// Create the floating window.
		Window dialog = DockingManager.getComponentFactory().createWindow(this.owner);
		if (dialog instanceof JDialog)
			((JDialog) dialog).setContentPane(panel);
		else
			((JFrame) dialog).setContentPane(panel);

		// Add the listeners.
		dialog.addWindowFocusListener(new MoveToFrontListener(dock));
		for (Object windowFocusListener : windowFocusListeners) {
			dialog.addWindowFocusListener((WindowFocusListener) windowFocusListener);
		}
		dialog.addWindowListener(windowClosingListener);

		// The size and location.
		if (size != null) {
			dialog.setSize(size.width, size.height);
		} else {
			dialog.pack();
		}
		dialog.setLocation(point.x - dialog.getInsets().left, point.y - dialog.getInsets().top);
		externalizeDockWindows.put(dock, dialog);

		// Add the child dock.
		dock.setExternalizer(this);
		externalizeDocks.add(0, dock);

//		// Inform the listeners.
//		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, dock));

		dialog.setVisible(!hidden);

	}

	/**
	 * Gets the position of the externalize dock. It is the x- and y-coordinate of the window location,
	 * and the index of the externalize dock.
	 *
	 * @param externalizeDock The externalize dock.
	 * @throws IllegalArgumentException If this externalizer does not contain the externalize dock.
	 * @return The position of the externalize dock.
	 */
	private Position getExternalizeDockPosition(ExternalizeDock externalizeDock) throws IllegalArgumentException {

		// Get the (x,y) position.
		Window window = (Window) externalizeDockWindows.get(externalizeDock);
		int index = externalizeDocks.indexOf(externalizeDock);
		if ((index >= 0) && (window != null)) {
			int[] positions = new int[3];
			positions[0] = window.getLocation().x;
			positions[1] = window.getLocation().y;
			positions[2] = index;
			return new Position(positions);
		}

		throw new IllegalArgumentException("Externalizer does not contain the externalize dock.");

	}

	private void hide(boolean hidden) {

		for (Object o : externalizeDockWindows.values()) {
			Window window = (Window) o;
			window.setVisible(!hidden);
			//window.repaint();
		}

	}


	// Private classes.

	/**
	 * This listener listens when a dialog that contains a child dock closes.
	 * It removes then the child from the float dock.
	 *
	 * @author Heidi Rakels.
	 */
	private class WindowClosingListener implements WindowListener {
		// Implementations of WindowListener.

		public void windowClosing(WindowEvent windowEvent) {

			// Get the window.
			Container contentPane = SwingUtil.getContentPane(windowEvent.getWindow());

			// Get the dock in the window.
			Dock childDock = (Dock) contentPane.getComponent(0);

			// Remove it from the list with child docks.
			externalizeDocks.remove(childDock);
			externalizeDockWindows.remove(childDock);

			// Get all the dockables in the dock tree.
			List childDockables = new ArrayList();
			DockingUtil.retrieveDockables(childDock, childDockables);

			// The dockables have no dock anymore.
			for (Object childDockable : childDockables) {
				Dockable dockable = (Dockable) childDockable;
				dockable.setState(DockableState.CLOSED, null);
			}

		}

		public void windowDeactivated(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowDeiconified(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowIconified(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowOpened(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowActivated(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowClosed(WindowEvent windowEvent) {
			// Do nothing.
		}

	}

	/**
	 * Moves the child dock to the front of the list with child docks, when its window has the focus.
	 *
	 * @author Heidi Rakels.
	 */
	private class MoveToFrontListener implements WindowFocusListener {

		// Fields.

		private Dock childDock;

		// Constructors.

		public MoveToFrontListener(Dock childDock) {
			this.childDock = childDock;
		}

		// Implementations of WindowFocusListener.

		public void windowGainedFocus(WindowEvent windowEvent) {

			// Check if the dock is part of this screendock. It is possible that it was just removed.
			if (externalizeDocks.contains(childDock)) {
				// Remove it and add it to the front.
				externalizeDocks.remove(childDock);
				externalizeDocks.add(0, childDock);
			}

		}

		public void windowLostFocus(WindowEvent windowEvent) {
			// Do nothing.
		}

	}

	/**
	 * This class listens to window events on the owner window
	 * and executes the appropriate actions on the child windows.
	 *
	 * @author Heidi Rakels.
	 */
	private class OwnerWindowListener implements WindowListener {

		public void windowActivated(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowClosed(WindowEvent windowEvent) {

			for (int index = 0; index < externalizeDocks.size(); index++) {
				// Get the child dock.
				Dock dock = (Dock) externalizeDocks.get(index);

				// Get the parent window.
				Window window = (Window) externalizeDockWindows.get(dock);
				window.setVisible(false);
				window.dispose();

				// Remove it from the list with child docks.
				externalizeDocks.remove(dock);
				externalizeDockWindows.remove(dock);
			}

		}

		public void windowClosing(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowDeactivated(WindowEvent windowEvent) {
			// Do nothing.
		}

		public void windowDeiconified(WindowEvent windowEvent) {
			// Do nothing.		
		}

		public void windowIconified(WindowEvent windowEvent) {
			// Do nothing.		
		}

		public void windowOpened(WindowEvent windowEvent) {
			// Do nothing.
		}

	}

}
