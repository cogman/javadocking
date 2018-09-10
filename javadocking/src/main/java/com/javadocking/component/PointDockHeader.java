package com.javadocking.component;

import com.javadocking.dock.LeafDock;
import com.javadocking.dock.Position;
import com.javadocking.drag.DragListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * <p>
 * A header for dragging all the dockables that are docked in a given dock.
 * The header is represented by a line with points.
 * </p>
 * <p>
 * The dockables can be dragged by dragging this component.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class PointDockHeader extends JPanel implements DockHeader {

	// Static fields.

	/**
	 * This is the minimum width when the orientation is vertical.
	 * It is the minimum height when the orientation is horizontal.
	 */
	private static final int HEADER_SIZE = 9;

	@Nullable
	private static final Image POINT;
	private static final int POINT_DISTANCE = 4;

	static {

		ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);
		SampleModel sampleModel = colorModel.createCompatibleSampleModel(3, 3);
		int[] pixels = new int[]
				{
						0xffd6cfc6,
						0xffb3b0ab,
						0xffefebe7,
						0xffb3b0a3,
						0xff8d887a,
						0xffffffff,
						0xffe7e7e7,
						0xffffffff,
						0xfffbffff,
				};

		DataBufferInt dataBuffer = new DataBufferInt(pixels, 9);
		WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point());
		POINT = new BufferedImage(colorModel, writableRaster, false, null);

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
	public PointDockHeader(LeafDock dock, int position) {
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

	public void paint(@NotNull Graphics graphics) {
		super.paint(graphics);

		if ((position == Position.TOP) || (position == Position.BOTTOM)) {

			// Draw a horizontal handle.
			int x = 4;
			int y = 3;
			while (x < getWidth() - POINT_DISTANCE) {
				graphics.drawImage(POINT, x, y, this);
				x += POINT_DISTANCE;
			}
		} else {

			// Draw a vertical handle.
			int x = 3;
			int y = 4;
			while (y < getHeight() - POINT_DISTANCE) {
				graphics.drawImage(POINT, x, y, this);
				y += POINT_DISTANCE;
			}

		}
	}

}
