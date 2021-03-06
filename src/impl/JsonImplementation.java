package impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import api.Json;

/**
 * This is an implementation of the JSON interface.
 *
 * @see api.Json
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 *
 */
public class JsonImplementation implements Json {

	////////////////////////////////////////////////////////////////////////////////
	// Instance fields

	/**
	 * Internal value of this JSON value. It may be a HashMap if it's a JSON object,
	 * an ArrayList if it's a JSON array, a String if it's a JSON string, a
	 * BigInteger or a BigDecimal if it's a JSON number, a Boolean if it's a JSON
	 * boolean or null if it's a JSON null.
	 */
	private final Object value;

	////////////////////////////////////////////////////////////////////////////////
	// Instance initializers

	/**
	 * Constructs a JsonImplementation with a given value.
	 */
	protected JsonImplementation(final Object value) {
		this.value = value;
	}

	////////////////////////////////////////////////////////////////////////////////
	// Class methods

	/**
	 * Creates a JSON structure from the given String.
	 *
	 * @param string
	 *                   A string.
	 * @return A JSON structure parsed from the given String.
	 * @throws IllegalArgumentException
	 *                                      If the given String can't be parsed.
	 */
	public static Json parse(final String string) {
		try {
			final JsonReaderImplementation jsonReader = new JsonReaderImplementation(string);
			return jsonReader.read();
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Obtain this JSON value in its String form without any formatting.
	 */
	@Override
	public String toString() {
		return JsonWriterImplementation.toString(this);
	}

	@Override
	public JsonType getType() {
		// Check for null first to avoid null pointer exceptions
		if (this.value == null) {
			return JsonType.NULL;
		}
		switch (this.value.getClass().getName()) {
		case "java.util.HashMap":
			return JsonType.OBJECT;
		case "java.util.ArrayList":
			return JsonType.ARRAY;
		case "java.lang.String":
			return JsonType.STRING;
		case "java.lang.Byte":
		case "java.lang.Short":
		case "java.lang.Integer":
		case "java.lang.Long":
		case "java.lang.Float":
		case "java.lang.Double":
		case "java.math.BigInteger":
		case "java.math.BigDecimal":
			return JsonType.NUMBER;
		case "java.lang.Boolean":
			return JsonType.BOOLEAN;
		default:
			throw new ClassCastException();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T as(Class<T> resultClass) {
		if (resultClass.isArray()) {
			final Collection<Json> values = this.values();
			final Class<?> componentType = resultClass.getComponentType();
			final int size = values.size();
			final Object[] array = new Object[size];
			final T newArray = (T) Array.newInstance(componentType, size);
			int index = 0;
			for (final Json value : values) {
				array[index] = value.as(componentType);
				++index;
			}
			System.arraycopy(array, 0, newArray, 0, size);
			return resultClass.cast(newArray);
		}
		// Check for null first to avoid null pointer exceptions
		if (this.value == null) {
			return null;
		}
		switch (resultClass.getName()) {
		case "boolean":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Boolean.class;
		case "java.lang.Boolean":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Boolean.parseBoolean((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).doubleValue() == 0.0d);
			case "java.lang.Boolean":
				return resultClass.cast((Boolean) this.value);
			default:
				return resultClass.cast(this.value);
			}
		case "byte":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Byte.class;
		case "java.lang.Byte":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Byte.parseByte((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).byteValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Byte.valueOf((byte) 1) : Byte.valueOf((byte) 0));
			default:
				return resultClass.cast(this.value);
			}
		case "short":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Short.class;
		case "java.lang.Short":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Short.parseShort((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).shortValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Short.valueOf((short) 1) : Short.valueOf((short) 0));
			default:
				return resultClass.cast(this.value);
			}
		case "int":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Integer.class;
		case "java.lang.Integer":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Integer.parseInt((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).intValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Integer.valueOf(1) : Integer.valueOf(0));
			default:
				return resultClass.cast(this.value);
			}
		case "long":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Long.class;
		case "java.lang.Long":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Long.parseLong((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).longValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Long.valueOf(1L) : Long.valueOf(0L));
			default:
				return resultClass.cast(this.value);
			}
		case "float":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Float.class;
		case "java.lang.Float":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Float.parseFloat((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).floatValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Float.valueOf(1.0f) : Float.valueOf(0.0f));
			default:
				return resultClass.cast(this.value);
			}
		case "double":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Double.class;
		case "java.lang.Double":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(Double.parseDouble((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).doubleValue());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Double.valueOf(1.0d) : Double.valueOf(0.0d));
			default:
				return resultClass.cast(this.value);
			}
		case "java.util.concurrent.atomic.AtomicInteger":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(new AtomicInteger(Integer.parseInt((String) this.value)));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(new AtomicInteger(((Number) this.value).intValue()));
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? new AtomicInteger(1) : new AtomicInteger(0));
			default:
				return resultClass.cast(this.value);
			}
		case "java.util.concurrent.atomic.AtomicLong":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(new AtomicLong(Long.parseLong((String) this.value)));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(new AtomicLong(((Number) this.value).longValue()));
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? new AtomicLong(1L) : new AtomicLong(0L));
			default:
				return resultClass.cast(this.value);
			}
		case "java.math.BigInteger":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(new BigInteger((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
				return resultClass.cast(BigInteger.valueOf((((Number) this.value).longValue())));
			case "java.lang.Float":
			case "java.lang.Double":
				return resultClass.cast(BigDecimal.valueOf((((Number) this.value).doubleValue())).toBigInteger());
			case "java.math.BigInteger":
				return resultClass.cast((BigInteger) this.value);
			case "java.math.BigDecimal":
				return resultClass.cast(((BigDecimal) this.value).toBigInteger());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? BigInteger.ONE : BigInteger.ZERO);
			default:
				return resultClass.cast(this.value);
			}
		case "java.math.BigDecimal":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(new BigDecimal((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
				return resultClass.cast(BigDecimal.valueOf((((Number) this.value).doubleValue())));
			case "java.math.BigInteger":
				return resultClass.cast(new BigDecimal((BigInteger) this.value));
			case "java.math.BigDecimal":
				return resultClass.cast((BigDecimal) this.value);
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? BigDecimal.ONE : BigDecimal.ZERO);
			default:
				return resultClass.cast(this.value);
			}
		case "java.lang.Number":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(new BigDecimal((String) this.value));
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast((Number) this.value);
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? BigDecimal.ONE : BigDecimal.ZERO);
			default:
				return resultClass.cast(this.value);
			}
		case "char":
			// Avoids errors when dealing with primitives
			resultClass = (Class<T>) Character.class;
		case "java.lang.Character":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast(((String) this.value).charAt(0));
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? Character.valueOf('t') : Character.valueOf('f'));
			default:
				return resultClass.cast(this.value);
			}
		case "java.lang.CharSequence":
			// If the class is CharSequence, use String
		case "java.lang.String":
			switch (this.value.getClass().getName()) {
			case "java.lang.String":
				return resultClass.cast((String) this.value);
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.math.BigInteger":
			case "java.math.BigDecimal":
				return resultClass.cast(((Number) this.value).toString());
			case "java.lang.Boolean":
				return resultClass.cast(((Boolean) this.value) ? "true" : "false");
			default:
				return resultClass.cast(this.value);
			}
		case "void":
			// If the class is void, return null
		case "java.lang.Void":
		case "null":
			// If the class is null, return null
			return null;
		default:
			return resultClass.cast(this.value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Json get(final Object... keys) {
		JsonImplementation current = this;
		for (int index = 0; index < keys.length; ++index) {
			final Object key = keys[index];
			if (current.value instanceof Map) {
				final Map<String, JsonImplementation> map = (Map<String, JsonImplementation>) current.value;
				current = map.get(key.toString());
			} else if (current.value instanceof List) {
				Integer valueIndex = null;
				if (key instanceof Number) {
					valueIndex = ((Number) key).intValue();
				} else if (key instanceof String) {
					valueIndex = Integer.parseInt((String) key);
				} else {
					throw new IllegalArgumentException("Key is not a Number or a String.");
				}
				final List<JsonImplementation> list = (List<JsonImplementation>) current.value;
				current = list.get(valueIndex);
			} else {
				throw new ClassCastException("JSON value is not an object or an array");
			}
		}
		return current;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> keys() {
		if (this.value instanceof Map) {
			final Map<String, Json> map = (Map<String, Json>) this.value;
			return map.keySet();
		} else if (this.value instanceof List) {
			final List<Json> list = (List<Json>) this.value;
			final Set<String> keySet = new HashSet<String>();
			for (int i = 0; i < list.size(); ++i) {
				keySet.add(Integer.toString(i));
			}
			return keySet;
		} else {
			throw new ClassCastException("JSON value is not an object or an array");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Json> values() {
		if (this.value instanceof Map) {
			final Map<String, Json> map = (Map<String, Json>) this.value;
			return map.values();
		} else if (this.value instanceof List) {
			final List<Json> list = (List<Json>) this.value;
			return list;
		} else {
			throw new ClassCastException("JSON value is not an object or an array");
		}
	}

}
