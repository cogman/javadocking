package com.javadocking.util;

import javax.swing.*;
import java.awt.*;

/**
 * This is a panel with a text area.
 *
 * @author Heidi Rakels.
 */
public class TextEditor extends JPanel {

	// Fields.

	// Constructors.

	/**
	 * Constructs the panel with the text area.
	 */
	public TextEditor(String text) {
		super(new BorderLayout());

		/**
		 * The text area.
		 */
		final JTextArea textArea = new JTextArea(12, 35);
		textArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		textArea.setText(text);
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

}
