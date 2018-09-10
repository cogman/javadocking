package com.javadocking.mainandtool;

import com.javadocking.dockable.DefaultDockable;

import javax.swing.*;
import java.awt.*;

public class TypeDockable extends DefaultDockable {

	private boolean main;

	public TypeDockable(String id, Component content, String title, Icon icon, boolean main) {
		super(id, content, title, icon);
		this.main = main;
	}

	public TypeDockable(String id, Component content, String title, boolean main) {
		super(id, content, title);
		this.main = main;
	}

	public boolean getMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

}
