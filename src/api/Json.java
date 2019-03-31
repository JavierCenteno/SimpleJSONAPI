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
	/**
	 * Represents the object type of JSON value, represented by pairs consisting of
	 * a JSON string and a JSON value, separated by a colon, in curly brackets
	 * separated by commas.
	 */
	OBJECT,
	/**
	 * Represents the array type of JSON value, represented by other JSON values in
	 * square brackets separated by commas.
	 */
	ARRAY,
	/**
	 * Represents the string type of JSON value, represented by a string in
	 * quotations with any quotation marks or backslashes escaped with a backslash
	 * and any ASCII control characters represented as a backslash followed by an u
	 * and the Unicode code point in hexadecimal format.
	 */
	STRING,
	/**
	 * Represents the number type of JSON value. In their string form, numbers are
	 * represented in base 10, they can have sign, a decimal point and an exponent
	 * (noted by an e).
	 */
	NUMBER,
	/**
	 * Represents the boolean type of JSON value. Booleans can be either true,
	 * represented by the string "true", or false, represented by the string
	 * "false".
	 */
	BOOLEAN,
	/**
	 * Represents the null type of JSON value, represented by the string "null".
	 */
	NULL
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
	 * the keys in the JSON value in the order they appear.
	 *
	 * @param keys
	 *                 The array of keys used to access a JSON value.
	 * @throws IllegalArgumentException
	 *                                      If any of the keys doesn't exist.
	 * @return The JSON value found under the given array of keys.
	 */
	public Json get(Object... keys);

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
