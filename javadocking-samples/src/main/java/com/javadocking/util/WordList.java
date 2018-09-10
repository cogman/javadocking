package com.javadocking.util;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * A list with the names of Roman gods.
 *
 * @author Heidi Rakels.
 */
public class WordList extends JPanel {
	// Static fields.

	private static final String[] WORDS = {
			"Fortuna",
			"Jupiter",
			"Juno",
			"Pluto",
			"Neptunus",
			"Vesta",
			"Ceres",
			"Apollo",
			"Diana",
			"Venus",
			"Vulcanus",
			"Mercurius",
			"Mars",
			"Bacchus",
			"Minerva",
			"Cupido",
			"Yuventus",
			"Persipina",
			"Saturnus",
			"Lucifer",
	};

	// Constructors.

	public WordList() {
		super(new BorderLayout());

		MyTableModel dataModel = new MyTableModel();
		JTable table = new JTable(dataModel);
		JScrollPane scrollpane = new JScrollPane(table);
		add(scrollpane, BorderLayout.CENTER);
		table.setPreferredSize(new Dimension(180, 320));
		this.setPreferredSize(new Dimension(180, 340));
		this.setMaximumSize(new Dimension(180, 340));
	}

	private class MyTableModel extends AbstractTableModel {

		public String getColumnName(int col) {
			return "Names";
		}

		public int getColumnCount() {
			return 1;
		}

		public int getRowCount() {
			return WORDS.length;
		}

		public Object getValueAt(int row, int col) {
			return WORDS[row];
		}
	}
}
