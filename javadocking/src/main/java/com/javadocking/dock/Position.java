package com.javadocking.dock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * <p>
 * This class describes the positions of dockables in {@link com.javadocking.dock.LeafDock}s
 * and the positions of child docks in {@link com.javadocking.dock.CompositeDock}s.
 * </p>
 * <p>
 * A position has a number of dimensions. For each dimension there is an integer that specifies the position in that
 * dimension.
 * </p>
 * <p>
 * In many cases the positions are one-dimensional, but some docks like {@link com.javadocking.dock.FloatDock}
 * have 3-dimensional positions for their child docks:
 * <ul>
 * <li> the x-position of the child.
 * <li> the y-position of the child.
 * <li> the z-order of the child.
 * </ul>
 *
 * <p>
 * This class describes also the integer constants for special positions of dockables or docks in other docks.
 * The possible positions are:
 * <ul>
 * <li> LEFT
 * <li> RIGHT
 * <li> TOP
 * <li> BOTTOM
 * <li> CENTER
 * </ul>
 *
 * @author Heidi Rakels.
 */
public class Position {

	// Static fields.

	/**
	 * A dock or dockable at the center of a dock.
	 */
	public static final int CENTER = 0;
	/**
	 * A dock or dockable at the left side of a dock.
	 */
	public static final int LEFT = 1;

	// Integers for special positions for dockables or docks in other docks.
	/**
	 * A dock or dockable at the right side of a dock.
	 */
	public static final int RIGHT = 2;
	/**
	 * A dock or dockable at the top of a dock.
	 */
	public static final int TOP = 3;
	/**
	 * A dock or dockable at the bottom  of a dock.
	 */
	public static final int BOTTOM = 4;
	/**
	 * The name of the <code>position</code> property.
	 */
	static final String PROPERTY_POSITION = "position";
	/**
	 * The char ','.
	 */
	private static final char COMMA = ',';

	// Fields.
	/**
	 * The array with the positions in the different dimensions.
	 */
	private int[] positions;

	// Constructors.

	/**
	 * Creates a position with 0 dimensions.
	 */
	public Position() {
		positions = new int[0];
	}

	/**
	 * Creates a position with 1 dimension.
	 *
	 * @param position The position in the only dimension.
	 */
	public Position(int position) {

		positions = new int[1];
		positions[0] = position;

	}

	/**
	 * Creates a position with 2 dimensions.
	 *
	 * @param positionX The position in the first dimension.
	 * @param positionY The position in the second dimension.
	 */
	public Position(int positionX, int positionY) {

		positions = new int[2];
		positions[0] = positionX;
		positions[1] = positionY;

	}

	/**
	 * Creates a position with 3 dimensions.
	 *
	 * @param positionX The position in the first dimension.
	 * @param positionY The position in the second dimension.
	 * @param positionZ The position in the third dimension.
	 */
	public Position(int positionX, int positionY, int positionZ) {

		positions = new int[3];
		positions[0] = positionX;
		positions[1] = positionY;
		positions[2] = positionZ;

	}

	/**
	 * Creates a position with the given integers.
	 *
	 * @param positions The positions in the different dimensions.
	 */
	public Position(int[] positions) {
		this.positions = positions;
	}

	// Public methods.

	/**
	 * Gets the position property with the given name. If the property is not found, the default value is returned.
	 *
	 * @param properties   The given properties where the property is searched.
	 * @param name         The name of the property.
	 * @param defaultValue The default value of the property.
	 * @return The position property with the given name.
	 * If the property is not found, the default value is returned.
	 */
	public static Position getPositionProperty(Properties properties, String name, Position defaultValue) {

		// Get the property from the given properties list.
		String stringValue = properties.getProperty(name);

		// Return the default value if the value is invalid.
		if ((stringValue == null) || (stringValue.length() == 0))
			return defaultValue;

		try {
			// Trim the string value.
			stringValue = stringValue.trim();

			// Get the position strings.
			StringTokenizer tokenizer = new StringTokenizer(stringValue, "[{(,/)}]");
			List<Integer> positionList = new ArrayList<>();
			while (tokenizer.hasMoreElements()) {
				String positionString = tokenizer.nextToken();
				int position = Integer.parseInt(positionString);
				positionList.add(position);
			}

			// Create the array with the positions.
			int[] positions = new int[positionList.size()];
			for (int index = 0; index < positionList.size(); index++) {
				positions[index] = positionList.get(index);
			}

			// Make the position.
			return new Position(positions);
		} catch (NumberFormatException numberFormatException) {
			// Exception while parsing the integers.
			return defaultValue;
		}

	}

	/**
	 * Adds the position property with the given name and value to the given properties.
	 *
	 * @param properties The given properties to which the property is added.
	 * @param name       The name of the property.
	 * @param value      The default value of the property.
	 */
	public static void setPositionProperty(@NotNull Properties properties, String name, @Nullable Position value) {

		// Check if the position is not null.
		if (value != null) {
			// Create the string for the position.
			StringBuilder positionString = new StringBuilder("(");
			for (int dimension = 0; dimension < value.getDimensions() - 1; dimension++) {
				positionString.append(value.getPosition(dimension));
				positionString.append(COMMA);
			}
			if (value.getDimensions() > 0) {
				positionString.append(value.getPosition(value.getDimensions() - 1));
			}
			positionString.append(")");


			// Add the property.
			properties.put(name, positionString.toString());
		}

	}

	// Overwritten methods.

	/**
	 * Gets the number of dimensions of this position.
	 *
	 * @return The number of dimensions of this position.
	 */
	public int getDimensions() {
		return positions.length;
	}

	/**
	 * Gets the position integer for the given dimension.
	 *
	 * @param dimension The dimension for which the position is retrieved.
	 * @throws IllegalArgumentException If the given dimension is &lt; 0 or &gt;= getDimensions().
	 * @return The position integer for the given dimension.
	 */
	public int getPosition(int dimension) {

		// Verify if we have a legal dimension.
		if ((dimension < 0) || (dimension >= positions.length)) {
			throw new IllegalArgumentException("Illegal dimension [" + dimension + "].");
		}

		// Return the position for the given dimension.
		return positions[dimension];

	}

	public boolean equals(Object object) {

		// Control the class.
		if (!(object instanceof Position)) {
			return false;
		}
		Position other = (Position) object;

		// Control the dimension.
		{
			if (other.getDimensions() != this.getDimensions()) {
				return false;
			}
		}

		// Control the position in every dimension.
		for (int dimension = 0; dimension < getDimensions(); dimension++) {
			if (this.getPosition(dimension) != other.getPosition(dimension)) {
				return false;
			}
		}

		return true;

	}

	// Public static methods.

	public int hashCode() {

		int result = 17;

		// Iterate over the dimensions.
		for (int dimension = 0; dimension < getDimensions(); dimension++) {
			result = 37 * result + getPosition(dimension);
		}

		return result;

	}

	@NotNull
	public String toString() {

		// Create the string for the position.
		StringBuilder positionString = new StringBuilder("(");
		for (int dimension = 0; dimension < getDimensions() - 1; dimension++) {
			positionString.append(getPosition(dimension));
			positionString.append(COMMA);
		}
		if (getDimensions() > 0) {
			positionString.append(getPosition(getDimensions() - 1));
		}
		positionString.append(")");

		return positionString.toString();

	}

}
