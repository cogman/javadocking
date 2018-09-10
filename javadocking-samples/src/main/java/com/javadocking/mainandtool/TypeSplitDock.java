package com.javadocking.mainandtool;

import com.javadocking.dock.Priority;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.factory.CompositeDockFactory;
import com.javadocking.dock.factory.DockFactory;
import com.javadocking.dockable.Dockable;
import com.javadocking.util.PropertiesUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class TypeSplitDock extends SplitDock {

	private boolean main;


	public TypeSplitDock(boolean main) {
		this.main = main;
		setChildDockFactory(new TypeLeafDockFactory(main));
		setCompositeChildDockFactory(new TypeSplitDockFactory(main));
	}

	public TypeSplitDock(DockFactory childDockFactory, CompositeDockFactory splitChildDockFactory, boolean main) {
		super(childDockFactory, splitChildDockFactory);
		this.main = main;
	}

	public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public int getDockPriority(Dockable dockable, Point relativeLocation) {

		if (!(dockable instanceof TypeDockable)) {
			return Priority.CANNOT_DOCK;
		}
		TypeDockable typeDockable = (TypeDockable) dockable;
		if (typeDockable.getMain() != main) {
			return Priority.CANNOT_DOCK;
		}
		return super.getDockPriority(dockable, relativeLocation);
	}

	public void loadProperties(String prefix, Properties properties, Map childDocks, Map dockablesMap, Window owner) throws IOException {
		main = PropertiesUtil.getBoolean(properties, prefix + "main", main);
		super.loadProperties(prefix, properties, childDocks, dockablesMap, owner);
	}

	public void saveProperties(String prefix, Properties properties, Map childDockIds) {
		super.saveProperties(prefix, properties, childDockIds);
		PropertiesUtil.setBoolean(properties, prefix + "main", main);
	}


}
