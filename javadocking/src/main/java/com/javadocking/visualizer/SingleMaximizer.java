package com.javadocking.visualizer;

import com.javadocking.DockingManager;
import com.javadocking.dock.DockableHider;
import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.util.DockingUtil;
import com.javadocking.util.PropertiesUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * A visualizer that can show one maximized dockable.
 * </p>
 * <p>
 * Information on using single maximizers is in
 * <a href="http://www.javadocking.com/developerguide/visualizer.html" target="_blank">
 * How to Use Visualizers (Minimizers and Maximizers)</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * This is a panel that can have 2 types of content:
 * <ul>
 * <li>its normal content</li>
 * <li>a maximized dockable</li>
 * </ul>
 * When there is a maximized dockable, then this dockable is visible.
 * The normal content is hidden behind the maximized dockable.
 * When there is no maximized dockable, then the normal content is visible.
 *
 * <p>
 * When no dockable is maximized, the normal content is show in this panel.
 * When a dockable is maximized, the content of the dockable is shown,
 * together with a header created with the method
 * {@link com.javadocking.component.SwComponentFactory#createMaximizeHeader(Dockable, int)}.
 * </p>
 * <p>
 * Only one dockable can be maximized in the maximizer.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class SingleMaximizer extends JPanel implements Visualizer {

	// Fields.

	/**
	 * The content from this panel.
	 */
	private Component content;
	/**
	 * The maximized dockable of this panel.
	 */
	private Dockable maximizedDockable;
	/**
	 * The position where the header is placed.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}.
	 */
	private int headerPosition = Position.TOP;


	// Constructors.

	/**
	 * Constructs a visualizer that can show one maximized dockable.
	 */
	public SingleMaximizer() {
		super(new BorderLayout());
	}

	/**
	 * Constructs a visualizer that can show one maximized dockable.
	 *
	 * @param    content        The content of the maximizer, when no dockable is maximized.
	 */
	public SingleMaximizer(Component content) {

		super(new BorderLayout());
		setContent(content);

	}

	// Implementations of Visualizer.

	public boolean canVisualizeDockable(Dockable dockableToVisualize) {

		// Check the dockable is not null.
		if (dockableToVisualize == null) {
			throw new NullPointerException("Dockable to maximize null.");
		}

		return true;

	}

	public void visualizeDockable(Dockable dockableToVisualize) {

		// Check if the dockable is not null.
		if (dockableToVisualize == null) {
			throw new NullPointerException("Null visualized dockable.");
		}

		// Check if there is already a maximized dockable.
		if (this.maximizedDockable != null) {
			// Remove the maximized dockable.
			Dockable dockableToRemove = DockingUtil.retrieveDockableOfDockModel(this.maximizedDockable.getID());
			if (dockableToRemove == null) {
				dockableToRemove = this.maximizedDockable;
			}
			removeVisualizedDockable(dockableToRemove);
			LeafDock dock = dockableToRemove.getDock();
			if (dock != null) {
				// Restore the maximized dockable.
				((DockableHider) dock).restoreDockable(dockableToRemove);
				dockableToRemove.setState(DockableState.NORMAL, dock);
			} else {
				dockableToRemove.setState(DockableState.CLOSED, null);
			}
		}

		// Set the dockable maximized.
		dockableToVisualize.setState(DockableState.MAXIMIZED, this);

		// Remove the normal content.
		if (content != null) {
			remove(content);
		}

		// Make the content of the visualized dockable visible.
		this.maximizedDockable = dockableToVisualize;
		add(dockableToVisualize.getContent(), BorderLayout.CENTER);
		Component header = (Component) DockingManager.getComponentFactory().createMaximizeHeader(dockableToVisualize, headerPosition);
		add(header, BorderLayout.NORTH);

		// Add a header.


		// Repaint.
		revalidate();
		repaint();

	}

	public int getState() {
		return DockableState.MAXIMIZED;
	}

	public Dockable getVisualizedDockable(int index) {

		// Check if the index is in the bounds.
		if ((index < 0) || (index >= getVisualizedDockableCount())) {
			throw new IndexOutOfBoundsException("Index " + index);
		}

		return maximizedDockable;
	}

	public int getVisualizedDockableCount() {

		if (maximizedDockable != null) {
			return 1;
		}

		return 0;
	}

	public void removeVisualizedDockable(Dockable dockable) {

		// Check if there is a content.
		if (maximizedDockable == null) {
			throw new IllegalStateException("There is no visualized dockable.");
		}

		// Check if the dockable is visualized in this minimizer.
		if (!maximizedDockable.equals(dockable)) {
			throw new IllegalArgumentException("The dockable is not minimized in this minimizer.");
		}

		// Remove the visualized component.
		removeAll();
		//TODO TEST
		if ((maximizedDockable.getDock() instanceof DockableHider)) {
			((DockableHider) maximizedDockable.getDock()).restoreDockable(maximizedDockable);
			maximizedDockable.setState(DockableState.NORMAL, maximizedDockable.getDock());
		}
		maximizedDockable = null;


		// Make the normal content visible again.
		if (content != null) {
			add(content, BorderLayout.CENTER);
		}

		// Repaint.
		revalidate();
		repaint();


	}

	/**
	 * Loads the properties of this maximizer. The dockables that were maximized,
	 * when the model was saved, are not maximized again.
	 */
	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException {

		// Get the position of the header.
		int headerPosition = Position.TOP;
		headerPosition = PropertiesUtil.getInteger(properties, prefix + "headerPosition", headerPosition);
		setHeaderPosition(headerPosition);

	}

	/**
	 * Saves the properties of this maximizer. The dockables that are maximized,
	 * are not saved.
	 */
	public void saveProperties(String prefix, Properties properties) {

		// Save the position of the header.
		PropertiesUtil.setInteger(properties, prefix + "headerPosition", headerPosition);

	}

	// Getters / Setters.

	/**
	 * Gets the content of this panel.
	 *
	 * @return The content from this panel, if there is one; otherwise null.
	 */
	public Component getContent() {
		return content;
	}

	/**
	 * Sets a component as content of this panel.
	 *
	 * @param component The new content component.
	 * @throws IllegalStateException    If there is already a content.
	 * @throws NullPointerException    If the component is null.
	 */
	public void setContent(Component component) {

		// Check if the content is not null.
		if (component == null) {
			throw new NullPointerException("Null content.");
		}

		// Check if there is already a content.
		if (content != null) {
			throw new IllegalStateException("There is already a content.");
		}

		// Add.
		this.content = component;
		add(content, BorderLayout.CENTER);

	}

	/**
	 * <p>
	 * Gets the position where the header is placed.
	 * </p>
	 * <p>
	 * The default value is {@link com.javadocking.dock.Position#TOP}.
	 * </p>
	 *
	 * @return The position where the header is placed.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public int getHeaderPosition() {
		return headerPosition;
	}

	/**
	 * Sets the position where the header is placed.
	 *
	 * @param headerPosition The position where the header is placed.
	 *                       Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 *                       <ul>
	 *                       <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 *                       <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 *                       <li>{@link com.javadocking.dock.Position#TOP},</li>
	 *                       <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 *                       </ul>
	 */
	public void setHeaderPosition(int headerPosition) {
		this.headerPosition = headerPosition;
	}

}
