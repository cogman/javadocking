package com.javadocking.util;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * A panel with the title and the text of a book.
 *
 * @author Heidi Rakels.
 */
public class Book extends JPanel {

	// Static fields.

	private static final String NEWLINE = System.getProperty("line.separator");

	// Constructors.

	/**
	 * Constructs a book.
	 *
	 * @param title   The title of the book.
	 * @param text    The text of the book.
	 * @param picture A picture of the book.
	 */
	public Book(String title, String text, Icon picture) {

		super(new BorderLayout());
		setBackground(Color.white);

		JTextPane textPane = createTextPane(title, text, picture);
		JScrollPane paneScrollPane = new JScrollPane(textPane);
		paneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		paneScrollPane.setPreferredSize(new Dimension(500, 350));
		paneScrollPane.setMinimumSize(new Dimension(10, 10));
		textPane.setCaretPosition(0);
		this.add(paneScrollPane, BorderLayout.CENTER);

	}

	/**
	 * Creates the text pane for the book.
	 *
	 * @param title   The title of the book.
	 * @param text    The text of the book.
	 * @param picture A picture of the book.
	 * @return The text pane for the book.
	 */
	private JTextPane createTextPane(String title, String text, Icon picture) {

		String[] initString = {title + NEWLINE + NEWLINE, " ", NEWLINE + NEWLINE + text};
		String[] initStyles = {"large", "icon", "regular"};

		JTextPane textPane = new JTextPane();
		StyledDocument styledDocument = textPane.getStyledDocument();
		addStylesToDocument(styledDocument, picture);

		try {
			for (int i = 0; i < initString.length; i++) {
				styledDocument.insertString(styledDocument.getLength(), initString[i],
						styledDocument.getStyle(initStyles[i]));
			}
		} catch (BadLocationException ble) {
			System.err.println("Couldn't insert initial text into text pane.");
		}

		return textPane;
	}

	/**
	 * Adds the styles to a styled document.
	 *
	 * @param doc     The styled document.
	 * @param picture A picture used in the document.
	 */
	protected void addStylesToDocument(StyledDocument doc, Icon picture) {

		Style def = StyleContext.getDefaultStyleContext().
				getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style style = doc.addStyle("italic", regular);
		StyleConstants.setItalic(style, true);

		style = doc.addStyle("bold", regular);
		StyleConstants.setBold(style, true);

		style = doc.addStyle("small", regular);
		StyleConstants.setFontSize(style, 10);

		style = doc.addStyle("large", regular);
		StyleConstants.setFontSize(style, 16);
		StyleConstants.setBold(style, true);

		style = doc.addStyle("icon", regular);
		StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);

		if (picture != null) {
			StyleConstants.setIcon(style, picture);
		}
	}

}
