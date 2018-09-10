package com.javadocking.visualizer;

import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * A visualizer that shows minimized dockables in a line at a the bottom borer of a panel.
 * The minimized dockables cannot be dragged.
 * </p>
 * <p>
 * Information on using line minimizers is in
 * <a href="http://www.javadocking.com/developerguide/visualizer.html" target="_blank">
 * How to Use Visualizers (Minimizers and Maximizers)</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class LineMinimizer extends JPanel implements Visualizer {

	// Fields.

	/**
	 * The content from this panel.
	 */
	@Nullable
	private Component content;
	/**
	 * The panel with the minimizers.
	 */
	private MinimizerPanel minimizerPanel;
	/**
	 * The panel at the border with the minimizer panel.
	 */
	private JPanel borderPanel;

	// Constructors.

	/**
	 * Constructs a visualizer that shows minimized dockables in a line at the border.
	 */
	public LineMinimizer() {
		super(new BorderLayout());

		borderPanel = new JPanel(new BorderLayout());
		add(borderPanel, BorderLayout.SOUTH);
		minimizerPanel = new MinimizerPanel(MinimizerPanel.ORIENTATION_HORIZONTAL);
		borderPanel.setVisible(false);
		borderPanel.add(minimizerPanel, BorderLayout.WEST);
		borderPanel.add(new JPanel(), BorderLayout.CENTER);

	}

	/**
	 * Constructs a visualizer that shows minimized dockables in a line at the border.
	 *
	 * @param    content        The normal content of the panel.
	 */
	public LineMinimizer(Component content) {

		this();

		setContent(content);

	}

	// Implementations of Visualizer.

	public boolean canVisualizeDockable(Dockable dockableToVisualize) {
		return minimizerPanel.canVisualizeDockable(dockableToVisualize);
	}

	public void visualizeDockable(@NotNull Dockable dockableToVisualize) {

		minimizerPanel.visualizeDockable(dockableToVisualize);
		if (minimizerPanel.getVisualizedDockableCount() > 0) {
			borderPanel.setVisible(true);
		}

		// Don't forget this, otherwise the delegate will be registered as the visualizer.
		dockableToVisualize.setState(DockableState.MINIMIZED, this);

		revalidate();
		repaint();

	}

	public int getState() {
		return minimizerPanel.getState();
	}

	public int getVisualizedDockableCount() {
		return minimizerPanel.getVisualizedDockableCount();
	}

	public Dockable getVisualizedDockable(int index) throws IndexOutOfBoundsException {

		return minimizerPanel.getVisualizedDockable(index);

	}

	public void removeVisualizedDockable(Dockable dockableToRemove) {

		minimizerPanel.removeVisualizedDockable(dockableToRemove);
		if (minimizerPanel.getVisualizedDockableCount() == 0) {
			borderPanel.setVisible(false);
		}
		revalidate();
		repaint();

	}

	/**
	 * Loads the properties of this minimizer.
	 */
	public void loadProperties(String prefix, Properties properties, Map dockablesMap, Window owner) throws IOException {
		minimizerPanel.loadProperties(prefix, properties, dockablesMap, owner);
		if (minimizerPanel.getVisualizedDockableCount() == 0) {
			borderPanel.setVisible(false);
		} else {
			borderPanel.setVisible(true);
		}
	}

	/**
	 * Saves the properties of this minimizer.
	 */
	public void saveProperties(String prefix, Properties properties) {

		minimizerPanel.saveProperties(prefix, properties);

	}

	// Getters / Setters.

	/**
	 * Gets the content of this panel.
	 *
	 * @return The content from this panel, if there is one; otherwise null.
	 */
	@Nullable
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
	public void setContent(@Nullable Component component) {

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

		revalidate();
		repaint();
	}

	/**
	 * <p>
	 * Gets the position that has to be used for creating the minimize headers
	 * with the method {@link com.javadocking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * </p>
	 * <p>
	 * The default value is {@link com.javadocking.dock.Position#TOP}.
	 * </p>
	 *
	 * @return The position that has to be used for creating the minimize headers
	 * with the method {@link com.javadocking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public int getHeaderPosition() {
		return minimizerPanel.getHeaderPosition();
	}

	/**
	 * Sets the position that has to be used for creating the minimize headers
	 * with the method {@link com.javadocking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 *
	 * @param newHeaderPosition The position that has to be used for creating the minimize headers
	 *                          with the method {@link com.javadocking.component.SwComponentFactory#createMinimizeHeader(Dockable, int)}.
	 *                          Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 *                          <ul>
	 *                          <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 *                          <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 *                          <li>{@link com.javadocking.dock.Position#TOP},</li>
	 *                          <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 *                          </ul>
	 */
	public void setHeaderPosition(int newHeaderPosition) {
		minimizerPanel.setHeaderPosition(newHeaderPosition);
	}

}
