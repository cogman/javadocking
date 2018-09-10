package com.javadocking.component;

/**
 * <p>
 * A header that can be selected.
 * </p>
 * <p>
 * Information on using dock headers is in
 * <a href="http://www.javadocking.com/developerguide/componentfactory.html#LeafDockHeaders" target="_blank">How to Use the Component Factory</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 * <p>
 * Implementations of this class should extend java.awt.Component.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface SelectableHeader extends Header {

	/**
	 * Determines if the header is selected.
	 *
	 * @return True if the header is elected, false otherwise.
	 */
	boolean isSelected();

	/**
	 * Selects or deselects the header.
	 *
	 * @param selected True if the header has to be selected, false otherwise.
	 */
	void setSelected(boolean selected);
}
