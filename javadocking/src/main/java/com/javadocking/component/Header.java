package com.javadocking.component;

import java.beans.PropertyChangeListener;

/**
 * An interface for a header of an object. The header has a position.
 *
 * @author Heidi Rakels.
 */
public interface Header {

	/**
	 * Gets the position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 *
	 * @return The position of this header.
	 */
	int getPosition();

	/**
	 * Gets the position of this header.
	 * Possible values are constants defined by the class {@link com.javadocking.dock.Position}, i.e.:
	 * <ul>
	 * <li>{@link com.javadocking.dock.Position#LEFT},</li>
	 * <li>{@link com.javadocking.dock.Position#RIGHT},</li>
	 * <li>{@link com.javadocking.dock.Position#TOP},</li>
	 * <li>{@link com.javadocking.dock.Position#BOTTOM}.</li>
	 * </ul>
	 *
	 * @param position The position of this header.
	 */
	void setPosition(int position);

	/**
	 * Adds a java.beans.PropertyChangeListener.
	 *
	 * @param listener The property change listener to be added.
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a java.beans.PropertyChangeListener. This removes a listener.
	 *
	 * @param listener The property change listener to be removed
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Disposes the header.
	 */
	void dispose();
}
