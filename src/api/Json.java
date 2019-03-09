/*
 * This is an API for JSON data handling in Java.
 * Copyright (C) 2019 Javier Centeno Vega
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package api;

import java.util.Collection;
import java.util.Set;

/**
 * This interface offers methods to interact with JSON data. It represents a
 * JSON value.
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
