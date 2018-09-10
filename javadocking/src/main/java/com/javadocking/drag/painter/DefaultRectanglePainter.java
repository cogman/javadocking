package com.javadocking.drag.painter;

import java.awt.*;

/**
 * <p>
 * This rectangle painter paints the border of a rectangle and it can fill the rectangle.
 * </p>
 * <p>
 * By setting the line width the rectangle is painted multiple times.
 * </p>
 * <p>
 * It is possible to give a stroke and a color to the painter.
 * </p>
 * <p>
 * It is possible to set the arc width and arc height to paint rounded rectangles.
 * </p>
 *
 * @author Heidi Rakels.
 */
public class DefaultRectanglePainter implements RectanglePainter {

	// Static fields.

	private static final int DEFAULT_BORDER_COUNT = 1;
	private static final int DEFAULT_BORDER_SHIFT = 1;
	private static final int DEFAULT_ARC_WIDTH = 10;
	private static final int DEFAULT_ARC_HEIGHT = 10;
	private static final Color DEFAULT_FILL_COLOR = new Color(180, 180, 255, 100);
	private static final Color DEFAULT_BORDER_COLOR = Color.blue;

	// Fields.

	/**
	 * The number of times the rectangle is painted.
	 */
	private int borderCount = DEFAULT_BORDER_COUNT;
	/**
	 * The number of pixels the border is shifted, each time it is painted.
	 */
	private int borderShift = DEFAULT_BORDER_SHIFT;
	/**
	 * The color that is used for painting the border.
	 */
	private Color borderColor = DEFAULT_BORDER_COLOR;
	/**
	 * The stroke that is used for painting the border. If it is null, a normal line is painted.
	 */
	private Stroke stroke = null;
	/**
	 * The width of the angle arc.
	 */
	private int arcWidth = DEFAULT_ARC_WIDTH;
	/**
	 * The height of the angle arc.
	 */
	private int arcHeight = DEFAULT_ARC_HEIGHT;
	/**
	 * The fill color for the rectangle.
	 */
	private Color fillColor = DEFAULT_FILL_COLOR;
	/**
	 * The label that will be painted in the middle of the rectangle.
	 */
	private String label;
	/**
	 * The label back ground color.
	 */
	private Color labelBackground = Color.white;
	/**
	 * The label fore ground color.
	 */
	private Color labelForeground = Color.black;
	/**
	 * The label border.
	 */
	private Color labelBorderColor = Color.blue;


	// Implementations of RectanglePainter.

	public void paintRectangle(Graphics graphics, int x, int y, int width, int height) {
		// Set border color.
		Color oldColor = graphics.getColor();
		graphics.setColor(borderColor);

		// Set border stroke;
		Stroke oldStroke = ((Graphics2D) graphics).getStroke();
		if (stroke != null) {
			((Graphics2D) graphics).setStroke(stroke);
		}

		// Draw the rectangles. The number of rectangles equals the line width.
		for (int index = 0; index < borderCount; index++) {
			int totalShift = index * borderShift;
			if ((arcWidth == 0) && (arcHeight == 0)) {
				graphics.drawRect(totalShift, totalShift, width - 1 - totalShift * 2, height - 1 - totalShift * 2);
			} else {
				graphics.drawRoundRect(totalShift, totalShift, width - 1 - totalShift * 2, height - 1 - totalShift * 2, arcWidth, arcHeight);
			}
		}

		// Set fill color.
		if (fillColor != null) {
			graphics.setColor(fillColor);
		}

		if (fillColor != null) {
			int totalShift = borderShift * borderCount;
			if ((arcWidth == 0) && (arcHeight == 0)) {
				graphics.fillRect(x + totalShift, x + totalShift, width - 1 - totalShift * 2, height - 1 - totalShift * 2);
			} else {
				graphics.fillRoundRect(x + totalShift, y + totalShift, width - 1 - totalShift * 2, height - 1 - totalShift * 2, arcWidth, arcHeight);
			}
		}

		// Draw the label.
		if (label != null) {
			FontMetrics fm = graphics.getFontMetrics();
			int textWidth = fm.stringWidth(label);
			int stringAscent = fm.getAscent();
			int textHeight = stringAscent - fm.getLeading();
			int labelWidth = textWidth + 10;
			int labelHeight = textHeight + 10;

			graphics.setColor(labelBackground);
			graphics.fillRect(x + width / 2 - labelWidth / 2, y + height / 2 - labelHeight / 2, labelWidth, labelHeight);
			graphics.setColor(labelBorderColor);
			graphics.drawRect(x + width / 2 - labelWidth / 2, y + height / 2 - labelHeight / 2, labelWidth, labelHeight);
			graphics.setColor(labelForeground);
			graphics.drawString(label, x + width / 2 - textWidth / 2, y + height / 2 + textHeight / 2);
		}

		// Reset.
		((Graphics2D) graphics).setStroke(oldStroke);
		graphics.setColor(oldColor);

	}

	// Getters / Setters.

	/**
	 * <p>
	 * Gets the number of times the border is painted.
	 * </p>
	 * <p>
	 * The default is 4.
	 * </p>
	 *
	 * @return The number of times the border is painted.
	 */
	public int getBorderCount() {
		return borderCount;
	}

	/**
	 * Sets the number of times the border is painted.
	 *
	 * @param borderCount The number of times the border is painted.
	 */
	public void setBorderCount(int borderCount) {
		this.borderCount = borderCount;
	}

	/**
	 * <p>
	 * Gets the number of pixels the border is shifted, each time it is painted.
	 * </p>
	 * <p>
	 * The default is 1.
	 * </p>
	 *
	 * @return The number of pixels the border is shifted, each time it is painted.
	 */
	public int getBorderShift() {
		return borderShift;
	}

	/**
	 * Sets the number of pixels the border is shifted, each time it is painted.
	 *
	 * @param borderShift The number of pixels the border is shifted, each time it is painted.
	 */
	public void setBorderShift(int borderShift) {
		this.borderShift = borderShift;
	}

	/**
	 * <p>
	 * Gets the color that is used for painting the border.
	 * </p>
	 * <p>
	 * The default is (10, 80, 255).
	 * </p>
	 *
	 * @return The color that is used for painting the border.
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the color that is used for painting the border.
	 *
	 * @param color The color that is used for painting the border.
	 */
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	/**
	 * Gets the stroke that is used for painting the border. If it is null, a normal line is painted.
	 *
	 * @return The stroke that is used for painting the border. If it is null, a normal line is painted.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Sets the stroke that is used for painting the border. If it is null, a normal line is painted.
	 *
	 * @param stroke The stroke that is used for painting the border. If it is null, a normal line is painted.
	 */
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	/**
	 * Gets the height of the angle arc. The default value is 0.
	 *
	 * @return The height of the angle arc.
	 */
	public int getArcHeight() {
		return arcHeight;
	}

	/**
	 * Sets the height of the angle arc.
	 *
	 * @param arcHeight The height of the angle arc.
	 */
	public void setArcHeight(int arcHeight) {
		this.arcHeight = arcHeight;
	}

	/**
	 * Gets the width of the angle arc. The default value is 0.
	 *
	 * @return The width of the angle arc.
	 */
	public int getArcWidth() {
		return arcWidth;
	}

	/**
	 * Sets the width of the angle arc.
	 *
	 * @param arcWidth The width of the angle arc.
	 */
	public void setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
	}

	/**
	 * Gets the fill color for the rectangle. The default is null.
	 *
	 * @return The fill color for the rectangle.
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Sets the fill color for the rectangle.
	 *
	 * @param fillColor The fill color for the rectangle.
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Gets the label that will be painted in the middle of the rectangle.
	 *
	 * @return The label that will be painted in the middle of the rectangle.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label that will be painted in the middle of the rectangle.
	 *
	 * @param label The label that will be painted in the middle of the rectangle.
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the label back ground color.
	 *
	 * @return The label back ground color.
	 */
	public Color getLabelBackground() {
		return labelBackground;
	}

	/**
	 * Gets the label back ground color.
	 *
	 * @param labelBackground The label back ground color.
	 */
	public void setLabelBackground(Color labelBackground) {
		this.labelBackground = labelBackground;
	}

	/**
	 * Gets the label border.
	 *
	 * @return The label border.
	 */
	public Color getLabelBorderColor() {
		return labelBorderColor;
	}

	/**
	 * Sets the label border.
	 *
	 * @param labelBorderColor The label border.
	 */
	public void setLabelBorderColor(Color labelBorderColor) {
		this.labelBorderColor = labelBorderColor;
	}

	/**
	 * Gets the label fore ground color.
	 *
	 * @return The label fore ground color.
	 */
	public Color getLabelForeground() {
		return labelForeground;
	}

	/**
	 * Sets the label fore ground color.
	 *
	 * @param labelForeground The label fore ground color.
	 */
	public void setLabelForeground(Color labelForeground) {
		this.labelForeground = labelForeground;
	}


}
