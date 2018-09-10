package com.javadocking.visualizer;

import com.javadocking.dockable.Dockable;

import java.awt.*;

public interface Externalizer extends Visualizer {

	public void moveExternalizedDockable(Dockable dockable, Point position, Point dockableOffset);

}
