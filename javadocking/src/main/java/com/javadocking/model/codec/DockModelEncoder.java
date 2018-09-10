package com.javadocking.model.codec;

import com.javadocking.model.DockModel;
import com.javadocking.model.DockingPathModel;

import java.io.IOException;

/**
 * <p>
 * This is an interface for a class that encodes a {@link com.javadocking.model.DockModel} to a destination.
 * It can also encode a {@link DockingPathModel}.
 * </p>
 * <p>
 * Information on using dock model encoders is in
 * <a href="http://www.javadocking.com/developerguide/codec.html" target="_blank">How to Use Dock Model Encoders and Decoders</a> in
 * <i>The Sanaware Developer Guide</i>.
 * </p>
 *
 * @author Heidi Rakels.
 */
public interface DockModelEncoder {

	// Interface methods.

	/**
	 * Checks whether this encoder can export the given dock model to a new specified location.
	 *
	 * @param dockModel       The dock model that has to be exported.
	 * @param destinationName The name of the location where the dock model should be saved.
	 * @return True if this encoder can save the given dock model at the specified location,
	 * false otherwise.
	 */
	boolean canExport(DockModel dockModel, String destinationName);

	/**
	 * Checks whether this encoder can save the given dock model in the location, where it originally came from.
	 *
	 * @param dockModel The dock model that has to be saved.
	 * @return True if this encoder can save the given dock model in the location, where it originally
	 * came from, false otherwise.
	 */
	boolean canSave(DockModel dockModel);

	/**
	 * Exports the dock model to the specified location.
	 *
	 * @param dockModel       The dock model that has to be exported.
	 * @param destinationName The name of the location, where the dock model should be saved.
	 * @throws IllegalArgumentException If the dock model can not be exported to the new location by this encoder.
	 * @throws IOException              If an error occurs while exporting the data.
	 */
	void export(DockModel dockModel, String destinationName) throws IOException, IllegalArgumentException;

	/**
	 * Saves the dock model in the location, where it originally came from.
	 *
	 * @param dockModel The dock model model that has to be saved.
	 * @throws IllegalArgumentException If the dock model can not be saved by this encoder.
	 * @throws IOException              If an error occurs while saving the data.
	 */
	void save(DockModel dockModel) throws IOException, IllegalArgumentException;

}
