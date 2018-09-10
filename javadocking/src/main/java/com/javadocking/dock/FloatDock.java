package com.javadocking.dock;

import com.javadocking.DockingManager;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dock.factory.SingleDockFactory;
import com.javadocking.dock.factory.SplitDockFactory;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.DockingMode;
import com.javadocking.event.ChildDockEvent;
import com.javadocking.event.DockingEventSupport;
import com.javadocking.event.DockingListener;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;
import com.javadocking.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * <p>
 * This special dock contains all the floating dockables. It is a composite dock. The child docks are put in a
 * dialog (javaw.swing.JDialog) and are floating on the screen.
 * </p>
 * <p>
 * Information on using float docks is in
 * <a href="http://www.javadocking.com/developerguide/compositedock.html#FloatDock" target="_blank">How to Use Composite Docks</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * A dockable can be added to this dock if
 * <ul>
 * <li>the dockable has {@link DockingMode#FLOAT} as one of its possible docking modes.</li>
 * <li>the child dock factory can create a child dock for the dockable.</li>
 * </ul>
 * When a dockable is added, a child dock is created with the 'childDockFactory'. The dockable is added to
 * the child dock. The child dock is put in a floating window.
 *
 * <p>
 * There is an order for the floating child docks. Children with a lower index are on top of children with a higher
 * index.
 * </p>
 * <p>
 * The parent dock of the float dock is always null.
 * The float dock has a owner window. This is the window that that will be the owner of the dialogs that contain
 * the child docks.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class FloatDock implements CompositeDock {

	// Static fields.

	/**
	 * The name used for the <code>windowRectangle</code> property that defines the rectangle of the floating dialog.
	 */
	private static final String PROPERTY_WINDOW_DIMENSION = "windowRectangle";
	/**
	 * The location of the window, when the position of the window could not be loaded from the properties,
	 * or if the requested position is outside the screen.
	 */
	private static final Point DEFAULT_WINDOW_LOCATION = new Point(30, 30);

	// Fields.

	/**
	 * This factory creates the leaf child docks.
	 */
	private DockFactory childDockFactory;
	/**
	 * The list with the child docks of this dock.
	 */
	private List childDocks = new LinkedList();
	/**
	 * The mapping between the dockables and there floating windows.
	 */
	private Map childDockWindows = new HashMap();
	/**
	 * This is the priority for docking dockables in this dock.
	 */
	private int dockPriority = Priority.CAN_DOCK;
	/**
	 * The window that owns the floating windows created by this dock.
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
	 * The support for handling the docking events.
	 */
	private DockingEventSupport dockingEventSupport = new DockingEventSupport();


	// Ghost.
	/**
	 * This is a dock that has to be removed later. It is already made invisible.
	 * It cannot be removed now because there are still listeners for dragging that are busy.
	 * We only want to lose the listeners when dragging is finished. This is only used with dynamic dragging.
	 */
	private Dock ghostChildDock;

	// Constructors.

	/**
	 * Constructs a float dock with no owner and a {@link SplitDockFactory}
	 * as factory for the child docks.
	 */
	public FloatDock() {
		this(null, new SplitDockFactory());
	}

	/**
	 * Constructs a float dock with the given window as owner for the child dock windows
	 * and a {@link SplitDockFactory} as factory for creating child docks.
	 *
	 * @param    owner                The window that owns the floating windows created by this dock.
	 */
	public FloatDock(Window owner) {
		this(owner, new SplitDockFactory());
	}

	/**
	 * Constructs a float dock with the given window as owner for the child dock windows
	 * and the given factory for the child docks.
	 *
	 * @param    owner                The window that owns the floating windows created by this dock.
	 * @param    childDockFactory    The factory for creating child docks.
	 */
	public FloatDock(Window owner, DockFactory childDockFactory) {
		this.childDockFactory = childDockFactory;
		setOwner(owner);
	}

	// Implementations of Dock.

	/**
	 * <p>
	 * Determines if the dockable can be added.
	 * </p>
	 * <p>
	 * It can be added if
	 * <ul>
	 * <li>the dockable has {@link DockingMode#FLOAT} as one of its possible docking modes.</li>
	 * <li>the child dock factory can create a child dock for the dockable.</li>
	 * </ul>
	 *
	 * <p>
	 * When the dockable can be added the value of the property
	 * <code>dockPriority</code> is returned.
	 * </p>
	 *
	 * @return When the dockable can be added the value of the property
	 * <code>dockPriority</code> is returned, otherwise {@link Priority#CANNOT_DOCK}.
	 */
	public int getDockPriority(Dockable dockableToAdd, Point relativeLocation) {

		// If the dockable is floatable, it can be docked in the float dock.
		if ((dockableToAdd.getDockingModes() & DockingMode.FLOAT) != 0) {
			// Can the child dock factory create a child dock?
			if (childDockFactory.createDock(dockableToAdd, DockingMode.FLOAT) != null) {
				return dockPriority;
			}
		}

		return Priority.CANNOT_DOCK;

	}

	public int retrieveDockingRectangle(Dockable dockable, Point relativeLocation, Point dockableOffset, Rectangle rectangle) {

		// Can we dock in this dock?
		int priority = getDockPriority(dockable, relativeLocation);
		if (priority != Priority.CANNOT_DOCK) {
			// Get the prefered size of the dockable.
			Dimension preferredSize = childDockFactory.getDockPreferredSize(dockable, DockingMode.FLOAT);

			// Adapt the offset when it is bigger than the preferred size.
			int offsetX = dockableOffset.x;
			int offsetY = dockableOffset.y;
			if (offsetX > preferredSize.width) {
				offsetX = preferredSize.width;
			}
			if (offsetY > preferredSize.height) {
				offsetY = preferredSize.height;
			}

			// Create a rectangle with this size and put it at the location.
			rectangle.setBounds(relativeLocation.x - offsetX,
					relativeLocation.y - offsetY,
					preferredSize.width,
					preferredSize.height);
		}

		return priority;

	}

	public boolean addDockable(Dockable dockableToAdd, Point relativeLocation, Point dockableOffset) {

		// Verify the conditions for adding the dockable.
		if (getDockPriority(dockableToAdd, relativeLocation) == Priority.CANNOT_DOCK) {
			// We are not allowed to dock the dockable in this dock.
			return false;
		}

		// Create the child dock and add the dockable.
		Dock childDock = childDockFactory.createDock(dockableToAdd, DockingMode.FLOAT);
		if (childDock == null) {
			return false;
		}
		childDock.setParentDock(this);
		childDock.addDockable(dockableToAdd, new Point(), dockableOffset);

		// Adapt the offset when it is bigger than the preferred size of the dockable.
		Dimension preferredSize = childDockFactory.getDockPreferredSize(dockableToAdd, DockingMode.FLOAT);
		int offsetX = dockableOffset.x;
		int offsetY = dockableOffset.y;
		if (offsetX > preferredSize.width) {
			offsetX = preferredSize.width;
		}
		if (offsetY > preferredSize.height) {
			offsetY = preferredSize.height;
		}

		// Add the child dock.
		addChildDock(childDock, new Point(relativeLocation.x - offsetX, relativeLocation.y - offsetY), null);

		return true;
	}

	public boolean isEmpty() {
		return childDocks.size() == 0;
	}

	/**
	 * Always returns false, because it is never full.
	 */
	public boolean isFull() {
		return false;
	}

	/**
	 * Always returns null, because this dock can't have a parent dock.
	 */
	public CompositeDock getParentDock() {
		return null;
	}

	/**
	 * Does nothing, because this dock can't have a parent dock.
	 */
	public void setParentDock(CompositeDock parentDock) {
	}

	public void saveProperties(String prefix, Properties properties, Map childDockIds) {

		// Save the dock priority
		PropertiesUtil.setInteger(properties, prefix + "dockPriority", getDockPriority());

		// Save the class of the child dock factory and its properties.
		String className = childDockFactory.getClass().getName();
		PropertiesUtil.setString(properties, prefix + "childDockFactory", className);
		childDockFactory.saveProperties(prefix + "childDockFactory.", properties);

		// Iterate over the child docks.
		int[] dim = new int[2];
		for (Object childDock : childDocks) {
			// Get the child dock.
			Dock dock = (Dock) childDock;

			// Get the ID of the childDock.
			String childDockId = (String) childDockIds.get(dock);

			// Get the parent dialog.
			Window window = (Window) childDockWindows.get(dock);

			// Save the rectangle.
			dim[0] = window.getSize().width;
			dim[1] = window.getSize().height;
			PropertiesUtil.setIntegerArray(properties, prefix + CHILD_DOCK_PREFIX + "." + childDockId + "." + PROPERTY_WINDOW_DIMENSION, dim);

			// Save the position. This is the (x,y) position and the index of the child dock in the list of child docks.
			Position.setPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + "." + childDockId + "." + Position.PROPERTY_POSITION, getChildDockPosition(dock));
		}

	}

	public void loadProperties(String prefix, Properties properties, Map newChildDocks, Map dockablesMap, Window owner) throws IOException {

		// Load the class and properties of the child dock factory.
		try {
			String className = SingleDockFactory.class.getName();
			className = PropertiesUtil.getString(properties, prefix + "childDockFactory", className);
			Class clazz = Class.forName(className);
			childDockFactory = (DockFactory) clazz.newInstance();
			childDockFactory.loadProperties(prefix + "childDockFactory.", properties);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException exception) {
			System.out.println("Could not create the child dock factory.");
			exception.printStackTrace();
			childDockFactory = new SingleDockFactory();
		}

		// Set the owner of the float dock.
		setOwner(owner);

		// Set the dock priority property.
		int dockPriority = Priority.CAN_DOCK;
		dockPriority = PropertiesUtil.getInteger(properties, prefix + "dockPriority", dockPriority);
		setDockPriority(dockPriority);

		// Create an array with the child dock IDs in the right order.
		String[] childDockIdsArray = new String[newChildDocks.keySet().size()];
		for (Object o : newChildDocks.keySet()) {
			// Get the ID of the child dock.
			String childDockId = (String) o;

			// Get the position of this child in the list of child docks.
			Position position = null;
			position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + "." + childDockId + "." + Position.PROPERTY_POSITION, position);
			childDockIdsArray[position.getPosition(2)] = childDockId;
		}

		// Create the dialogs for the children.
		for (int index = childDockIdsArray.length - 1; index >= 0; index--) {
			// Get the child dock.
			Dock dock = (Dock) newChildDocks.get(childDockIdsArray[index]);

			// Remove the empty children of this dock.
			if (dock instanceof CompositeDock) {
				DockingUtil.removeEmptyChildren((CompositeDock) dock);
			}

			// Add only the dock if it is not empty.
			if (!dock.isEmpty()) {
				// Get the rectangle.
				int[] dim = null;
				dim = PropertiesUtil.getIntegerArray(properties, prefix + CHILD_DOCK_PREFIX + "." + childDockIdsArray[index] + "." + PROPERTY_WINDOW_DIMENSION, dim);
				Position position = null;
				position = Position.getPositionProperty(properties, prefix + CHILD_DOCK_PREFIX + "." + childDockIdsArray[index] + "." + Position.PROPERTY_POSITION, position);
				if ((dim != null) && (dim.length == 2)) {
					addChildDock(dock, new Point(position.getPosition(0), position.getPosition(1)), new Dimension(dim[0], dim[1]));
				}
			}
		}

	}

	public void addDockingListener(DockingListener listener) {
		dockingEventSupport.addDockingListener(listener);
	}

	public void removeDockingListener(DockingListener listener) {
		dockingEventSupport.removeDockingListener(listener);
	}

	// Implementations of CompositeDock.


	public void emptyChild(Dock childDock) {

		// Get the child dock.
		if (childDocks.contains(childDock)) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

			// Remove the child.
			childDocks.remove(childDock);
			childDockWindows.remove(childDock);

			// Get the window of the child and dispose it.
			Window window = (Window) SwingUtilities.windowForComponent((Component) childDock);
			window.setVisible(false);
			window.dispose();

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, childDock));
		}

	}

	public void ghostChild(Dock childDock) {

		// Get the child dock.
		if (childDocks.contains(childDock)) {
			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, null, childDock));

			// Remove the child.
			childDocks.remove(childDock);
			childDockWindows.remove(childDock);
			ghostChildDock = childDock;

			// Get the window of the child and make it invisible.
			Window window = (Window) SwingUtilities.windowForComponent((Component) childDock);
			window.setVisible(false);
		}

	}

	public void clearGhosts() {

		// Do we have a ghost?
		if (ghostChildDock != null) {
			// Get the window of the child and dispose it.
			Window window = (Window) SwingUtilities.windowForComponent((Component) ghostChildDock);
			window.setVisible(false);
			window.dispose();
			Dock oldGhostChildDock = ghostChildDock;
			ghostChildDock = null;

			// Inform the listeners about the removal.
			dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, null, oldGhostChildDock));

		}

	}

	public int getChildDockCount() {
		return childDocks.size();
	}

	public Dock getChildDock(int index) throws IndexOutOfBoundsException {
		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getChildDockCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return (Dock) childDocks.get(index);
	}

	public Position getChildDockPosition(Dock childDock) throws IllegalArgumentException {

		// Get the (x,y) position.
		Window window = (Window) childDockWindows.get(childDock);
		int index = childDocks.indexOf(childDock);
		if ((index >= 0) && (window != null)) {
			int[] positions = new int[3];
			positions[0] = window.getLocation().x;
			positions[1] = window.getLocation().y;
			positions[2] = index;
			return new Position(positions);
		}

		throw new IllegalArgumentException("The dock is not docked in this composite dock.");

	}

	/**
	 * Adds the child dock to the given position. The position has normally 3 dimensions.
	 *
	 * @param dock     The child dock that is added to this float dock in a dialog.
	 * @param position The position for the child dock with 3 dimensions:
	 *                 <ul>
	 *                 <li>The first dimension value will be the x-position of the dialog</li>
	 *                 <li>The second dimension value will be the y-position of the dialog</li>
	 *                 <li>The third dimension value will be the z-order of the dialog</li>
	 *                 </ul>
	 */
	public void addChildDock(Dock dock, Position position) throws IllegalStateException {

		// Get the (x, y)location.
		Point point = null;
		if (position.getDimensions() >= 2) {
			point = new Point(position.getPosition(0), position.getPosition(1));
		} else {
			point = new Point(0, 0);
		}
		//TODO z-order.
		addChildDock(dock, point, null);
	}

	public DockFactory getChildDockFactory() {
		return childDockFactory;
	}

	public void setChildDockFactory(DockFactory childDockFactory) {

		if (childDockFactory == null) {
			throw new IllegalArgumentException("The child dock factory cannot be null.");
		}

		this.childDockFactory = childDockFactory;

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
	 * @param owner The window that owns the floating windows created by this dock.
	 */
	public void setOwner(Window owner) {
		this.owner = owner;
		if (this.owner != null) {
			owner.addWindowListener(ownerWindowListener);
		}
		// TODO remove the listeners added to the owner, when we are destroyed.

	}


	protected Map getChildDockWindows() {
		return childDockWindows;
	}

	/**
	 * Gets the dock priority for a floatable dockable that will be added to this float dock.
	 *
	 * @return The dock priority for a floatable dockable that will be added to the float dock.
	 */
	public int getDockPriority() {
		return dockPriority;
	}

	/**
	 * Sets the dock priority for a floatable dockable that will be added to the float dock.
	 *
	 * @param dockPriority The dock priority for a floatable dockable that will be added to the float dock.
	 */
	public void setDockPriority(int dockPriority) {
		this.dockPriority = dockPriority;
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
		for (Object o : childDockWindows.values()) {
			Window window = (Window) o;
			window.removeWindowFocusListener(windowFocusListener);
		}

	}

	// Public methods.

	/**
	 * Moves the given child dock to the new location.
	 *
	 * @param childDock        The child dock that has to be moved to the new location.
	 * @param relativeLocation The new location for the child dock.
	 * @param dockableOffset   The mouse location where the dragging started, relatively to the child dock.
	 */
	public void moveDock(Dock childDock, Point relativeLocation, Point dockableOffset) {

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, this, this, childDock));

		// Calculate the location for the floating window.
		Point point = new Point(relativeLocation.x - dockableOffset.x, relativeLocation.y - dockableOffset.y);

		// Get the floating window and change the location.
		Window window = SwingUtilities.getWindowAncestor((Component) childDock);
		window.setLocation(new Point(point.x, point.y));

		// Inform the listeners about the move.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, this, this, childDock));

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
	public void addChildDock(Dock dock, Point location, Dimension size) {

		// Inform the listeners.
		dockingEventSupport.fireDockingWillChange(new ChildDockEvent(this, null, this, dock));

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
		childDockWindows.put(dock, dialog);

		// Add the child dock.
		dock.setParentDock(this);
		childDocks.add(0, dock);

		// Inform the listeners.
		dockingEventSupport.fireDockingChanged(new ChildDockEvent(this, null, this, dock));

		dialog.setVisible(true);

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
		location.move(DEFAULT_WINDOW_LOCATION.x, DEFAULT_WINDOW_LOCATION.y);

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
			childDocks.remove(childDock);
			childDockWindows.remove(childDock);

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
			if (childDocks.contains(childDock)) {
				// Remove it and add it to the front.
				childDocks.remove(childDock);
				childDocks.add(0, childDock);
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

			for (int index = 0; index < childDocks.size(); index++) {
				// Get the child dock.
				Dock dock = (Dock) childDocks.get(index);

				// Get the parent window.
				Window window = (Window) childDockWindows.get(dock);
				window.setVisible(false);
				window.dispose();

				// Remove it from the list with child docks.
				childDocks.remove(dock);
				childDockWindows.remove(dock);

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
