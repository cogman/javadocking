package com.javadocking.drag.painter;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CompositeDockableDragPainter implements DockableDragPainter {

	@NotNull
	private List painters = new ArrayList();

	public void addPainter(DockableDragPainter dockableDragPainter) {
		painters.add(dockableDragPainter);
	}

	public void removePainter(DockableDragPainter dockableDragPainter) {
		painters.remove(dockableDragPainter);
	}

	public void clear() {

		for (Object painter : painters) {
			DockableDragPainter dockableDragPainter = (DockableDragPainter) painter;
			dockableDragPainter.clear();
		}

	}

	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		for (Object painter : painters) {
			DockableDragPainter dockableDragPainter = (DockableDragPainter) painter;
			dockableDragPainter.paintDockableDrag(dockable, dock, rectangle, locationInDestinationDock);
		}
	}


}
