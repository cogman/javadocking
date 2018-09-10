package com.javadocking.mainandtool;

import com.javadocking.dock.Priority;
import com.javadocking.dock.TabDock;
import com.javadocking.dockable.Dockable;
import com.javadocking.util.PropertiesUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class TypeTabDock extends TabDock {

	private boolean main;


	public TypeTabDock(boolean main) {
		this.main = main;
	}

	public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public int getDockPriority(Dockable dockable, @NotNull Point relativeLocation) {
		if (!(dockable instanceof TypeDockable)) {
			return Priority.CANNOT_DOCK;
		}
		TypeDockable typeDockable = (TypeDockable) dockable;
		if (typeDockable.getMain() != main) {
			return Priority.CANNOT_DOCK;
		}
		return super.getDockPriority(dockable, relativeLocation);
	}

	public void loadProperties(String prefix, @NotNull Properties properties, Map childDockIds, @NotNull Map dockablesMap, Window owner) throws IOException {
		main = PropertiesUtil.getBoolean(properties, prefix + "main", main);
		super.loadProperties(prefix, properties, childDockIds, dockablesMap, owner);

	}

	public void saveProperties(String prefix, @NotNull Properties properties, Map childDocks) {
		super.saveProperties(prefix, properties, childDocks);
		PropertiesUtil.setBoolean(properties, prefix + "main", main);
	}


}
