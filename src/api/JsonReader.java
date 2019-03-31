package api;

import java.io.IOException;

/**
 * Reads a JSON structure from an input.
 *
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 *
 */
public interface JsonReader {

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Reads the next JSON structure.
	 *
	 * @return The next JSON structure.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	public Json read() throws IOException;

}
