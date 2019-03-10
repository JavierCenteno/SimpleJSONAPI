package api;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a JSON value.
 * 
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 * 
 */
public interface Json {

	////////////////////////////////////////////////////////////////////////////////
	// Nested classes

	/**
	 * An enumeration of the different types of JSON data.
	 * 
	 * @author Javier Centeno Vega <jacenve@telefonica.net>
	 * @version 1.0
	 * @since 1.0
	 *
	 */
	public static enum JsonType {
	OBJECT, ARRAY, STRING, NUMBER, BOOLEAN, NULL
	}

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Get the type of this JSON value.
	 * 
	 * @return The type of this JSON value.
	 */
	public JsonType getType();

	/**
	 * Obtain this JSON value as the given class.
	 * 
	 * @param resultClass
	 *                        The class the result will be attempted to be converted
	 *                        into. Primitive classes are supported.
	 * @throws ClassCastException
	 *                                If the result can't be converted to the given
	 *                                class.
	 * @return This JSON value as the given class.
	 */
	public <T> T as(Class<T> resultClass);

	/**
	 * Obtain the JSON value under the given array of keys.
	 * 
	 * The value accessed by this method will be the one found by looking up each of
	 * the keys in the JSON value obtained from the previous key, or the value used
	 * by this JSON value for the first key.
	 * 
	 * @param keys
	 *                 The array of keys used to access a JSON value.
	 * @throws IllegalArgumentException
	 *                                      If any of the keys doesn't exist.
	 * @return The JSON value found under the given array of keys.
	 */
	public Json get(String... keys);

	/**
	 * Gets the set of keys of this JSON value.
	 * 
	 * @throws ClassCastException
	 *                                If this JSON value is not a JSON structure.
	 * @return The set of keys of this JSON value.
	 */
	public Set<String> keys();

	/**
	 * Gets the collection of values of this JSON value.
	 * 
	 * @throws ClassCastException
	 *                                If this JSON value is not a JSON structure.
	 * @return The collection of values of this JSON value.
	 */
	public Collection<Json> values();

}
