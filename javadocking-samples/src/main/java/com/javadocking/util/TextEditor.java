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

	/**
	 * The text area.
	 */
	private JTextArea textArea;

	// Constructors.

	/**
	 * Constructs the panel with the text area.
	 */
	public TextEditor(String text) {
		super(new BorderLayout());

		textArea = new JTextArea(12, 35);
		textArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		textArea.setText(text);
		this.add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

}
