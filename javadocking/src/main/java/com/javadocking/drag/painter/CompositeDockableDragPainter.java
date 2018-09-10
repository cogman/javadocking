package com.javadocking.drag.painter;

import com.javadocking.dock.Dock;
import com.javadocking.dockable.Dockable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompositeDockableDragPainter implements DockableDragPainter {

	private List painters = new ArrayList();

	public void addPainter(DockableDragPainter dockableDragPainter) {
		painters.add(dockableDragPainter);
	}

	public void removePainter(DockableDragPainter dockableDragPainter) {
		painters.remove(dockableDragPainter);
	}

	public void clear() {

		Iterator iterator = painters.iterator();
		while (iterator.hasNext()) {
			DockableDragPainter dockableDragPainter = (DockableDragPainter) iterator.next();
			dockableDragPainter.clear();
		}

	}

	public void paintDockableDrag(Dockable dockable, Dock dock, Rectangle rectangle, Point locationInDestinationDock) {

		Iterator iterator = painters.iterator();
		while (iterator.hasNext()) {
			DockableDragPainter dockableDragPainter = (DockableDragPainter) iterator.next();
			dockableDragPainter.paintDockableDrag(dockable, dock, rectangle, locationInDestinationDock);
		}
	}


}
