package impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

import api.Json;

/**
 * A Writer to write JSON values to a Stream.
 * 
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 * 
 */
public class JsonWriter {

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
	public JsonWriter(Writer writer) {
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
	public JsonWriter(OutputStream outputStream, Charset charset) {
		this(new OutputStreamWriter(outputStream, charset));
	}

	/**
	 * Constructs a JsonWriter from the given OutputStream by making a Writer from
	 * it.
	 * 
	 * @param outputStream
	 *                         An OutputStream to write JSON data to.
	 */
	public JsonWriter(OutputStream outputStream) {
		this(new OutputStreamWriter(outputStream));
	}

	////////////////////////////////////////////////////////////////////////////////
	// Class methods

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
		string = string.replace("\"", "\\\"");
		string = string.replace("\\", "\\\\");
		string = string.replace("/", "\\/");
		string = string.replace("\b", "\\\b");
		string = string.replace("\f", "\\\f");
		string = string.replace("\n", "\\\n");
		string = string.replace("\r", "\\\r");
		string = string.replace("\t", "\\\t");
		return string;
	}

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Writes a JSON value to this Writer.
	 * 
	 * @param json
	 *                        A JSON value to write to this Writer.
	 * @param lineBreak
	 *                        String that will be used as a line break.
	 * @param indentation
	 *                        String that will be used to indent.
	 * @param padding
	 *                        String that will be used to pad.
	 * @throws IOException
	 *                         If the Writer fails.
	 */
	public void writeValue(JsonImplementation json, String lineBreak, String indentation, String padding)
			throws IOException {
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
	 *                         If the Writer fails.
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
