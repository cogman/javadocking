package com.javadocking.model.codec;

import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPathModel;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * This is an interface for a class that creates a {@link com.javadocking.model.DockModel} from a source.
 * It can also decode a {@link DockingPathModel}.
 * </p>
 * <p>
 * Information on using dock model decoders is in
 * <a href="http://www.javadocking.com/developerguide/codec.html" target="_blank">How to Use Dock Model Encoders and Decoders</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockModelDecoder {
	// Interface methods.

	/**
	 * Checks whether this decoder can decode the given data into a dock model.
	 * Often this will only be a simple test, e.g. checking the file extension of a file.
	 *
	 * @param sourceName The name of a data source; typically a file name or a URL.
	 */
	boolean canDecodeSource(String sourceName);

	/**
	 * Creates a new dock model from the data found in the given data source,
	 * and adds the given dockables to the model. The decoded dock model is given to the docking manager
	 * ({@link com.javadocking.DockingManager#setDockModel(DockModel)}).
	 *
	 * @param sourceName   The name of a data source; typically a file name or a URL.
	 * @param dockablesMap A map with the dockables for the model.
	 *                     <ul>
	 *                     <li>map key: the ID of the dockable (java.lang.String).</li>
	 *                     <li>map value: the dockable ({@link com.javadocking.dockable.Dockable}).</li>
	 *                     </ul>
	 * @param ownersMap    A map with the owner windows.
	 *                     <ul>
	 *                     <li>map key: the ID of the owner window (java.lang.String).</li>
	 *                     <li>map value: the owner window (java.awt.window).</li>
	 *                     </ul>
	 * @return The dock model with the decoded docks and the given dockables.
	 * @throws IOException If an error occurs while retrieving the source, reading the source or decoding the data.
	 * @param    visualizersMap        A map with the visualizers.
	 * <ul>
	 * <li>map key: the key of the visualizer (java.lang.String).</li>
	 * <li>map value: the visualizer (java.awt.window).</li>
	 * </ul>
	 */
	DockModel decode(String sourceName, Map dockablesMap, Map ownersMap, Map visualizersMap) throws IOException;

}
