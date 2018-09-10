package com.javadocking.dockable.action;

/**
 * A mode that describes which actions of a dockable are shown in a header.
 *
 * @author Heidi Rakels.
 */
public class ShowActionMode {

	// Static fields.

	/**
	 * With this mode no actions of the docable are shown in the header.
	 */
	public static final ShowActionMode NO_ACTIONS = new ShowActionMode("no actions");
	/**
	 * With this mode all the actions of the docable are shown in the header.
	 */
	public static final ShowActionMode ALL_ACTIONS = new ShowActionMode("all actions");
	/**
	 * With this mode the first row of the matrix of actions of the docable are shown in the header.
	 */
	public static final ShowActionMode FIRST_ROW_ACTIONS = new ShowActionMode("first row actions");

	// Fields.

	private String name;

	// Overwritten methods.

	/**
	 * Constructs a show action mode.
	 *
	 * @param    name    The action name.
	 */
	protected ShowActionMode(String name) {
		this.name = name;
	}

	// Protected constructor.

	public String toString() {
		return name;
	}

	// Overwritten methods.

	// Prevent subclasses from overriding Object.equals.
	public boolean equals(Object object) {
		return super.equals(object);
	}

	// Prevent subclasses from overriding Object.hashCode.
	public int hashCode() {
		return super.hashCode();
	}

}
