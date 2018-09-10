package com.javadocking.util;

import javax.swing.*;
import java.awt.*;

/**
 * The UI for a find dialog.
 *
 * @author Heidi Rakels.
 */
public class Find extends JPanel {

	/**
	 * Constructs the UI for a find dialog.
	 */
	public Find() {

		super(new FlowLayout());
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Create the label.
		JLabel label = new JLabel("Find:");
		add(label);

		// Create the combobox.
		String[] items = new String[2];
		items[0] = "Gallico";
		items[1] = "Belgae";
		JComboBox comboBox = new JComboBox(items);
		comboBox.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		add(comboBox);

		// Create the button.
		JButton button = new JButton("Find");
		add(button);

	}

}
