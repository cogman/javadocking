package com.javadocking.util;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * A pane with a tree with the contact persons in the different countries.
 *
 * @author Heidi Rakels.
 */
public class ContactTree extends JPanel {

	/**
	 * Constructs a tree with the contact persons in the different countries.
	 */
	public ContactTree() {

		// Create the nodes of the countries and their contact person nodes.

		DefaultMutableTreeNode belgiumNode = new DefaultMutableTreeNode("Belgium");
		belgiumNode.add(new DefaultMutableTreeNode("Lies Holsbeek"));
		belgiumNode.add(new DefaultMutableTreeNode("Koen Van Lierde"));
		belgiumNode.add(new DefaultMutableTreeNode("Jan De Coninck"));

		DefaultMutableTreeNode netherlandsNode = new DefaultMutableTreeNode("Netherlands");
		netherlandsNode.add(new DefaultMutableTreeNode("Kees van Leeuwen"));
		netherlandsNode.add(new DefaultMutableTreeNode("Tineke Poetsma"));

		DefaultMutableTreeNode japanNode = new DefaultMutableTreeNode("Japan");
		japanNode.add(new DefaultMutableTreeNode("Ai Fujimoto"));
		japanNode.add(new DefaultMutableTreeNode("Hiro Kashiwasaki"));
		japanNode.add(new DefaultMutableTreeNode("Ichiro Yamashita"));

		DefaultMutableTreeNode germanyNode = new DefaultMutableTreeNode("Germany");
		germanyNode.add(new DefaultMutableTreeNode("Siegfried Schwartz"));
		germanyNode.add(new DefaultMutableTreeNode("Heinz Eisenhauer"));

		DefaultMutableTreeNode franceNode = new DefaultMutableTreeNode("France");
		franceNode.add(new DefaultMutableTreeNode("Marie D'Aubigne"));
		franceNode.add(new DefaultMutableTreeNode("Alex Deville"));

		DefaultMutableTreeNode britainNode = new DefaultMutableTreeNode("Great Britain");
		britainNode.add(new DefaultMutableTreeNode("Jane McHard"));
		britainNode.add(new DefaultMutableTreeNode("Dave Reynolds"));

		DefaultMutableTreeNode unitedStatesNode = new DefaultMutableTreeNode("United States");
		unitedStatesNode.add(new DefaultMutableTreeNode("John Smith"));
		unitedStatesNode.add(new DefaultMutableTreeNode("Kate Richard"));
		unitedStatesNode.add(new DefaultMutableTreeNode("Mary Jackson"));

		DefaultMutableTreeNode italyNode = new DefaultMutableTreeNode("Italy");
		italyNode.add(new DefaultMutableTreeNode("Marco Spinielli"));
		italyNode.add(new DefaultMutableTreeNode("Enrico Di Caprio"));
		italyNode.add(new DefaultMutableTreeNode("Dario Felicetti"));

		// Create the root and add the country nodes.
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Contacts");
		rootNode.add(belgiumNode);
		rootNode.add(britainNode);
		rootNode.add(franceNode);
		rootNode.add(germanyNode);
		rootNode.add(italyNode);
		rootNode.add(japanNode);
		rootNode.add(netherlandsNode);
		rootNode.add(unitedStatesNode);

		// Create the tree model.
		TreeModel treeModel = new DefaultTreeModel(rootNode);

		// Create the JTree from the tree model.
		JTree tree = new JTree(treeModel);

		// Expand the tree.
		for (int row = 0; row < tree.getRowCount(); row++) {
			tree.expandRow(row);
		}

		// Add the tree in a scroll pane.
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(tree), BorderLayout.CENTER);

	}

}
