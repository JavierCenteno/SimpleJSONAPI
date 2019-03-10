package impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

import api.Json;
import api.Json.JsonType;
import api.JsonWriter;

/**
 * A Writer to write JSON values to a Stream.
 * 
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 * 
 */
public class JsonWriterImplementation implements JsonWriter {

	////////////////////////////////////////////////////////////////////////////////
	// Instance fields

	/**
	 * The Writer this JsonReader is based on.
	 */
	private Writer writer;

	////////////////////////////////////////////////////////////////////////////////
	// Instance initializers

	/**
	 * Constructs a JsonWriter from the given Writer.
	 * 
	 * @param writer
	 *                   A Writer to write JSON data to.
	 */
	public JsonWriterImplementation(Writer writer) {
		this.writer = writer;
	}

	/**
	 * Constructs a JsonWriter from the given OutputStream by making a Writer from
	 * it using the given Charset.
	 * 
	 * @param outputStream
	 *                         An OutputStream to write JSON data to.
	 * @param charset
	 *                         A Charset to write the data to the OutputStream as.
	 */
	public JsonWriterImplementation(OutputStream outputStream, Charset charset) {
		this(new OutputStreamWriter(outputStream, charset));
	}

	/**
	 * Constructs a JsonWriter from the given OutputStream by making a Writer from
	 * it.
	 * 
	 * @param outputStream
	 *                         An OutputStream to write JSON data to.
	 */
	public JsonWriterImplementation(OutputStream outputStream) {
		this(new OutputStreamWriter(outputStream));
	}

	////////////////////////////////////////////////////////////////////////////////
	// Class methods

	/**
	 * Converts the given JSON value to String without formatting.
	 * 
	 * @param json A JSON value.
	 * @return The given JSON value in its String form.
	 */
	protected static String toString(Json json) {
		Writer writer = new StringWriter();
		JsonWriterImplementation jsonWriter = new JsonWriterImplementation(writer);
		try {
			jsonWriter.writeValue(json, 0, "", "", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	/**
	 * Generates a line of indentation using the given line break String,
	 * indentation String and indentation level.
	 * 
	 * @param indentationLevel
	 *                             An indendation level.
	 * @param lineBreak
	 *                             String that will be used as a line break.
	 * @param indentation
	 *                             String that will be used to indent.
	 * @return A line of indentation.
	 */
	private static String indentation(int indentationLevel, String lineBreak, String indendation) {
		String result = lineBreak;
		for (int index = 0; index < indentationLevel; ++index) {
			result += indendation;
		}
		return result;
	}

	/**
	 * Escapes the given String.
	 * 
	 * @param string
	 *                   The String to escape.
	 * @return The given String, escaped.
	 */
	private static String escape(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int index = 0; index < string.length(); ++index) {
			char character = string.charAt(index);
			switch (character) {
			case '\"':
				stringBuilder.append("\\\"");
			case '\\':
				stringBuilder.append("\\\\");
			case '/':
				stringBuilder.append("\\/");
			case '\b':
				stringBuilder.append("\\b");
			case '\f':
				stringBuilder.append("\\f");
			case '\n':
				stringBuilder.append("\\n");
			case '\r':
				stringBuilder.append("\\r");
			case '\t':
				stringBuilder.append("\\t");
			}
		}
		return stringBuilder.toString();
	}

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	@Override
	public void write(Json json, String lineBreak, String indentation, String padding) throws IOException {
		if (json.getType() != JsonType.OBJECT && json.getType() != JsonType.ARRAY) {
			throw new IllegalArgumentException("The JSON value provided is not a JSON structure");
		}
		writeValue(json, 0, lineBreak, indentation, padding);
	}

	/**
	 * Writes a JSON value to this Writer starting at a given indentation level.
	 * 
	 * @param json
	 *                             A JSON value to write to this Writer.
	 * @param indentationLevel
	 *                             An indentation level to start writing at.
	 * @param lineBreak
	 *                             String that will be used as a line break.
	 * @param indentation
	 *                             String that will be used to indent.
	 * @param padding
	 *                             String that will be used to pad.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	private void writeValue(Json json, int indentationLevel, String lineBreak, String indentation, String padding)
			throws IOException {
		switch (json.getType()) {
		case OBJECT:
			Set<String> keys = json.keys();
			this.writer.write("{");
			int keyCounter = 0;
			for (String key : keys) {
				this.writer.write(indentation(indentationLevel + 1, lineBreak, indentation));
				this.writer.write("\"");
				this.writer.write(escape(key));
				this.writer.write("\"");
				this.writer.write(":");
				this.writer.write(padding);
				Json value = json.get(key);
				this.writeValue(value, indentationLevel + 1, lineBreak, indentation, padding);
				if (++keyCounter < keys.size()) {
					this.writer.write(",");
				}
			}
			this.writer.write(indentation(indentationLevel, lineBreak, indentation));
			this.writer.write("}");
			break;
		case ARRAY:
			Collection<Json> values = json.values();
			this.writer.write("[");
			int valueCounter = 0;
			for (Json value : values) {
				this.writer.write(indentation(indentationLevel + 1, lineBreak, indentation));
				this.writeValue(value, indentationLevel + 1, lineBreak, indentation, padding);
				if (++valueCounter < values.size()) {
					this.writer.write(",");
				}
			}
			this.writer.write(indentation(indentationLevel, lineBreak, indentation));
			this.writer.write("]");
			break;
		case STRING:
			this.writer.write("\"");
			this.writer.write(escape(json.as(String.class)));
			this.writer.write("\"");
			break;
		case NUMBER:
			this.writer.write(json.as(Number.class).toString());
			break;
		case BOOLEAN:
			this.writer.write(json.as(boolean.class) ? "true" : "false");
			break;
		case NULL:
			this.writer.write("null");
			break;
		default:
			throw new ClassCastException();
		}
	}

}
