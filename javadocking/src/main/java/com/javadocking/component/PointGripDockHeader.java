package com.javadocking.component;

import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.drag.DragListener;

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 * A header for dragging all the dockables that are docked in a given dock.
 * The header is a grip with points.
 * </p>
 * <p>
 * The dockables can be dragged by dragging this component.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class PointGripDockHeader extends JPanel implements DockHeader {

	// Static fields.

	/**
	 * This is the minimum width when the orientation is vertical.
	 * It is the minimum height when the orientation is horizontal.
	 */
	private static final int HEADER_SIZE = 14;
	private static final Stroke STROKE;
	private static Color light = Color.white;
	private static Color shadow = Color.gray;


	static {
		float[] pattern = {1.0f, 3.0f};
		STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, pattern, 0.0f);

		// InternalFrame.borderColor
		//Button.toolBarBorderBackground
		//ToolBar.borderColor
		//InternalFrame.borderLight
		//InternalFrame.borderDarkShadow
		//InternalFrame.borderShadow
		//InternalFrame.borderHighlight
		//TabbedPane.borderHightlightColor
		//TitledBorder.titleColor

		//TabbedPane.focus

		Color lafLight = (Color) UIManager.getDefaults().get("InternalFrame.borderLight");
		Color lafShadow = (Color) UIManager.getDefaults().get("TabbedPane.borderHightlightColor");
		if (lafLight != null) {
			light = lafLight;
		}

		if (lafShadow != null) {
			shadow = lafShadow;
		}

	}

	// Fields.

	/**
	 * The dockables of this dock will be dragged when the handle is dragged.
	 */
	private LeafDock dock;
	/**
	 * The drag listener that drags the dockables of this dock.
	 */
	private DragListener dragListener;
	/**
	 * The position of this handle.
	 */
	private int position;

	// Constructors.

	/**
	 * Constructs a header for the given dock.
	 *
	 * @param    dock            The dock of the header.
	 * @param    position        The position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 */
	public PointGripDockHeader(LeafDock dock, int position) {
		super(new BorderLayout());
		this.setPosition(position);
		this.dock = dock;
	}

	// Implementations of DockHeader.

	public void dispose() {
	}

	public DragListener getDragListener() {
		return dragListener;
	}

	public void setDragListener(DragListener dragListener) {
		this.dragListener = dragListener;
		this.addMouseMotionListener(dragListener);
		this.addMouseListener(dragListener);
	}

	public LeafDock getDock() {
		return dock;
	}

	public int getPosition() {

		return position;
	}

	public void setPosition(int position) {

		this.position = position;
		if ((position == Position.TOP) || (position == Position.BOTTOM)) {
			this.setMinimumSize(new Dimension(HEADER_SIZE, HEADER_SIZE));
			this.setMaximumSize(new Dimension(Short.MAX_VALUE, HEADER_SIZE));
			this.setPreferredSize(new Dimension(HEADER_SIZE, HEADER_SIZE));
		} else {
			this.setMinimumSize(new Dimension(HEADER_SIZE, HEADER_SIZE));
			this.setMaximumSize(new Dimension(HEADER_SIZE, Short.MAX_VALUE));
			this.setPreferredSize(new Dimension(HEADER_SIZE, HEADER_SIZE));
		}

	}

	// Overwritten methods.

	public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.setColor(Color.darkGray);


		if ((position == Position.TOP) || (position == Position.BOTTOM)) {

			// Draw a horizontal handle.
			int width = getSize().width;
			graphics.setColor(light);
			graphics.drawLine(4, 3, width - 6, 3);
			graphics.drawArc(2, 3, 6, 6, -90, -180);
			graphics.setColor(shadow);
			graphics.drawLine(4, HEADER_SIZE - 5, width - 6, HEADER_SIZE - 5);
			graphics.drawArc(width - 8, 3, 6, 6, -90, 180);

			// Draw 2 lines with points.
			((Graphics2D) graphics).setStroke(STROKE);
			graphics.drawLine(6, 5, width - 6, 5);
			graphics.drawLine(8, 7, width - 6, 7);

		} else {

			// Draw a vertical handle.
			int height = getSize().height;
			graphics.setColor(light);
			graphics.drawLine(3, 4, 3, height - 6);
			graphics.drawArc(3, 2, 6, 6, -180, -180);
			graphics.setColor(shadow);
			graphics.drawLine(HEADER_SIZE - 5, 4, HEADER_SIZE - 5, height - 6);
			graphics.drawArc(3, height - 9, 6, 6, 180, 180);

			// Draw 2 lines with points.
			((Graphics2D) graphics).setStroke(STROKE);
			graphics.drawLine(5, 6, 5, height - 6);
			graphics.drawLine(7, 8, 7, height - 6);

		}
	}

}
