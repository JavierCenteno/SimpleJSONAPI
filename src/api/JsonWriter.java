package api;

import java.io.IOException;

/**
 * Writes a JSON structure to an output.
 * 
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 * 
 */
public interface JsonWriter {

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Writes a JSON structure without any formatting.
	 * 
	 * @param json
	 *                        A JSON structure to write.
	 * @throws IllegalArgumentException
	 *                                      If the JSON value provided has errors or
	 *                                      is not a JSON structure.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	public default void write(Json json) throws IOException {
		write(json, "", "", "");
	}

	/**
	 * Writes a JSON structure.
	 * 
	 * @param json
	 *                        A JSON structure to write.
	 * @param lineBreak
	 *                        String that will be used as a line break.
	 * @param indentation
	 *                        String that will be used to indent.
	 * @param padding
	 *                        String that will be used to pad.
	 * @throws IllegalArgumentException
	 *                                      If the JSON value provided has errors or
	 *                                      is not a JSON structure.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	public void write(Json json, String lineBreak, String indentation, String padding) throws IOException;

}
