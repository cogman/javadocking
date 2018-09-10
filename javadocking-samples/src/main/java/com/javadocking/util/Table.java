package com.javadocking.util;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

/**
 * @author Heidi Rakels.
 */
public class Table extends JPanel {
	// Static fields.

	public static final int LIST = 0;
	public static final int TABLE = 1;

	// Fields.

	private int tableSize = TABLE;

	// Constructors.

	public Table(int tableSize) {
		super(new BorderLayout());

		this.tableSize = tableSize;

		MyTableModel dataModel = new MyTableModel();
		JTable table = new JTable(dataModel);
		JScrollPane scrollpane = new JScrollPane(table);
		add(scrollpane, BorderLayout.CENTER);

	}

	// Getters / Setters.

	public int getTableSize() {
		return tableSize;
	}

	public void setTableSize(int size) {
		this.tableSize = size;
	}

	private class MyTableModel extends AbstractTableModel {
		public int getColumnCount() {
			if (tableSize == LIST) {
				return 1;
			}
			return 4;
		}

		public int getRowCount() {
			if (tableSize == LIST) {
				return 20;
			}
			return 4;
		}

		public Object getValueAt(int row, int col) {
			return "Hello";
		}
	}
}
