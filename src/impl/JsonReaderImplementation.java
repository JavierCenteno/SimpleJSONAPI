package impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.Json;
import api.JsonReader;

/**
 * A Reader to read JSON values from a Stream.
 *
 * @author Javier Centeno Vega <jacenve@telefonica.net>
 * @version 1.0
 * @since 1.0
 *
 */
public class JsonReaderImplementation implements JsonReader {

	////////////////////////////////////////////////////////////////////////////////
	// Class fields

	/**
	 * A BigInteger representing the minimum possible value that can be represented
	 * by a byte.
	 */
	private static final BigInteger MIN_BYTE_VALUE = BigInteger.valueOf(Byte.MIN_VALUE);
	/**
	 * A BigInteger representing the maximum possible value that can be represented
	 * by a byte.
	 */
	private static final BigInteger MAX_BYTE_VALUE = BigInteger.valueOf(Byte.MAX_VALUE);
	/**
	 * A BigInteger representing the minimum possible value that can be represented
	 * by a short.
	 */
	private static final BigInteger MIN_SHORT_VALUE = BigInteger.valueOf(Short.MIN_VALUE);
	/**
	 * A BigInteger representing the maximum possible value that can be represented
	 * by a short.
	 */
	private static final BigInteger MAX_SHORT_VALUE = BigInteger.valueOf(Short.MAX_VALUE);
	/**
	 * A BigInteger representing the minimum possible value that can be represented
	 * by an integer.
	 */
	private static final BigInteger MIN_INTEGER_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);
	/**
	 * A BigInteger representing the maximum possible value that can be represented
	 * by an integer.
	 */
	private static final BigInteger MAX_INTEGER_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
	/**
	 * A BigInteger representing the minimum possible value that can be represented
	 * by a long.
	 */
	private static final BigInteger MIN_LONG_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
	/**
	 * A BigInteger representing the maximum possible value that can be represented
	 * by a long.
	 */
	private static final BigInteger MAX_LONG_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

	////////////////////////////////////////////////////////////////////////////////
	// Instance fields

	/**
	 * The Reader this JsonReader is based on.
	 */
	private final Reader reader;
	/**
	 * A single character buffer for the current character.
	 */
	private int current;
	/**
	 * Current row of text.
	 */
	private int row;
	/**
	 * Current column of text.
	 */
	private int column;

	////////////////////////////////////////////////////////////////////////////////
	// Instance initializers

	/**
	 * Constructs a JsonReader from the given Reader.
	 *
	 * @param reader
	 *                   A Reader to read JSON data from.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	public JsonReaderImplementation(final Reader reader) throws IOException {
		this.reader = reader;
		this.current = reader.read();
		this.row = 1;
		this.column = 1;
	}

	/**
	 * Constructs a JsonReader from the given InputStream by making a Reader from it
	 * using the given Charset.
	 *
	 * @param inputStream
	 *                        An InputStream to read JSON data from.
	 * @param charset
	 *                        A Charset to interpret the data from the InputStream
	 *                        as.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	public JsonReaderImplementation(final InputStream inputStream, final Charset charset) throws IOException {
		this(new InputStreamReader(inputStream, charset));
	}

	/**
	 * Constructs a JsonReader from the given InputStream by making a Reader from
	 * it.
	 *
	 * @param inputStream
	 *                        An InputStream to read JSON data from.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	public JsonReaderImplementation(final InputStream inputStream) throws IOException {
		this(new InputStreamReader(inputStream));
	}

	/**
	 * Constructs a JsonReader from the given String by making a Reader from it.
	 *
	 * @param string
	 *                   A String to parse JSON data from.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	public JsonReaderImplementation(final String string) throws IOException {
		this(new ByteArrayInputStream(string.getBytes()));
	}

	////////////////////////////////////////////////////////////////////////////////
	// Class methods

	/**
	 * Turns a single hexadecimal character into an integer. Characters in the range
	 * '0' to '9' are turned into the integers 0 to 9 and characters in the range
	 * 'a' to 'f' or 'A' to 'F' are turned into the integers 10 to 15. Any other
	 * character causes an IllegalArgumentException.
	 *
	 * @param character
	 *                      An hexadecimal character which must be between '0' and
	 *                      '9', 'a' and 'f' or 'A' and 'F'.
	 * @return The character parsed as an hexadecimal character.
	 * @throws IllegalArgumentException
	 *                                      If the character is not a valid
	 *                                      hexadecimal character.
	 */
	private static int hexadecimalToInteger(final char character) {
		if (('0' <= character) && (character <= '9')) {
			return character - '0';
		} else if (('a' <= character) && (character <= 'f')) {
			return character - 87;// 87 == 'a' - 10
		} else if (('A' <= character) && (character <= 'F')) {
			return character - 55;// 55 == 'A' - 10
		}
		throw new IllegalArgumentException("Character \'" + character + "\' is not an hexadecimal character.");
	}

	////////////////////////////////////////////////////////////////////////////////
	// Instance methods

	/**
	 * Creates an IllegalArgumentException warning of an unexpected character with
	 * the given String in the message as the characters that would be expected.
	 *
	 * @param expectedCharacters
	 *                               A list of expected characters to be in the
	 *                               message.
	 * @return An IllegalArgumentException warning of an unexpected character.
	 */
	private IllegalArgumentException unexpectedCharacter(final String expectedCharacters) {
		return new IllegalArgumentException("JSON parsing error near column:" + this.column + ", row:" + this.row
				+ "; Expected " + expectedCharacters + ", got \'" + (char) this.peek() + "\' instead");
	}

	/**
	 * Checks if the current character matches the given character, then pops.
	 * Throws an IllegalArgumentException if they don't match.
	 *
	 * @param character
	 *                      A character to compare to the current character.
	 * @throws IllegalArgumentException
	 *                                      If the current character is not the
	 *                                      given character.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private void check(final int character) throws IOException {
		if (this.peek() != character) {
			throw this.unexpectedCharacter("\'" + (char) character + "\'");
		}
		this.pop();
	}

	/**
	 * Get the current character from the Reader without advancing.
	 *
	 * @return The current character from the Reader.
	 */
	private int peek() {
		return this.current;
	}

	/**
	 * Get the current character from the Reader and advance to the next one.
	 *
	 * @return The current character from the Reader.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	private int pop() throws IOException {
		final int previous = this.current;
		this.current = this.reader.read();
		if (this.current == '\n') {
			this.row = 1;
			++this.column;
		} else {
			++this.row;
		}
		return previous;
	}

	/**
	 * Pops for as long as there are whitespace characters.
	 *
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	private void consumeWhitespace() throws IOException {
		while (Character.isWhitespace(this.peek())) {
			this.pop();
		}
	}

	/**
	 * Reads the next JSON object.
	 *
	 * @return The next JSON object.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readObject() throws IOException {
		this.consumeWhitespace();
		this.check('{');
		final Map<String, Json> map = new HashMap<>();
		this.consumeWhitespace();
		if (this.peek() == '}') {
			this.pop();
		} else {
			while (true) {
				final String key = this.readString();
				this.consumeWhitespace();
				this.check(':');
				final Json value = this.readValue();
				this.consumeWhitespace();
				map.put(key, value);
				if (this.peek() == ',') {
					this.pop();
				} else if (this.peek() == '}') {
					this.pop();
					break;
				} else {
					throw this.unexpectedCharacter("\',\' or \'}\'");
				}
			}
		}
		return new JsonImplementation(map);
	}

	/**
	 * Reads the next JSON array.
	 *
	 * @return The next JSON array.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readArray() throws IOException {
		this.check('[');
		final List<Json> list = new ArrayList<>();
		this.consumeWhitespace();
		if (this.peek() == ']') {
			this.pop();
		} else {
			while (true) {
				final Json value = this.readValue();
				this.consumeWhitespace();
				list.add(value);
				if (this.peek() == ',') {
					this.pop();
				} else if (this.peek() == ']') {
					this.pop();
					break;
				} else {
					throw this.unexpectedCharacter("\',\' or \']\'");
				}
			}
		}
		return new JsonImplementation(list);
	}

	/**
	 * Reads the next JSON string.
	 *
	 * @return The next JSON string.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private String readString() throws IOException {
		this.consumeWhitespace();
		this.check('\"');
		final StringBuilder stringBuilder = new StringBuilder();
		while (this.peek() != '\"') {
			if (this.peek() == '\\') {
				this.pop();
				switch (this.pop()) {
				case '\"':
					stringBuilder.append('\"');
					break;
				case '\\':
					stringBuilder.append('\\');
					break;
				case '/':
					stringBuilder.append('/');
					break;
				case 'b':
					stringBuilder.append('\b');
					break;
				case 'f':
					stringBuilder.append('\f');
					break;
				case 'n':
					stringBuilder.append('\n');
					break;
				case 'r':
					stringBuilder.append('\r');
					break;
				case 't':
					stringBuilder.append('\t');
					break;
				case 'u':
					int unescaped = 0;
					unescaped |= JsonReaderImplementation.hexadecimalToInteger((char) this.pop()) << 12;
					unescaped |= JsonReaderImplementation.hexadecimalToInteger((char) this.pop()) << 8;
					unescaped |= JsonReaderImplementation.hexadecimalToInteger((char) this.pop()) << 4;
					unescaped |= JsonReaderImplementation.hexadecimalToInteger((char) this.pop());
					stringBuilder.append((char) unescaped);
					break;
				}
			} else {
				stringBuilder.append((char) this.pop());
			}
		}
		this.check('\"');
		return stringBuilder.toString();
	}

	/**
	 * Reads the next JSON number.
	 *
	 * @return The next JSON number.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readNumber() throws IOException {
		this.consumeWhitespace();
		final StringBuilder stringBuilder = new StringBuilder();
		boolean isWhole = true;
		if (this.peek() == '-') {
			stringBuilder.append((char) this.pop());
		}
		while (('0' <= this.peek()) && (this.peek() <= '9')) {
			stringBuilder.append((char) this.pop());
		}
		if (this.peek() == '.') {
			stringBuilder.append((char) this.pop());
			isWhole = false;
			while (('0' <= this.peek()) && (this.peek() <= '9')) {
				stringBuilder.append((char) this.pop());
			}
		}
		if ((this.peek() == 'e') || (this.peek() == 'E')) {
			stringBuilder.append((char) this.pop());
			isWhole = false;
			if ((this.peek() == '-') || (this.peek() == '+')) {
				stringBuilder.append((char) this.pop());
			}
			while (('0' <= this.peek()) && (this.peek() <= '9')) {
				stringBuilder.append((char) this.pop());
			}
		}
		Number number;
		if (isWhole) {
			final BigInteger result = new BigInteger(stringBuilder.toString());
			if ((result.compareTo(JsonReaderImplementation.MIN_BYTE_VALUE) >= 0)
					&& (0 >= result.compareTo(JsonReaderImplementation.MAX_BYTE_VALUE))) {
				number = Byte.valueOf(result.byteValue());
			} else if ((result.compareTo(JsonReaderImplementation.MIN_SHORT_VALUE) >= 0)
					&& (0 >= result.compareTo(JsonReaderImplementation.MAX_SHORT_VALUE))) {
				number = Short.valueOf(result.shortValue());
			} else if ((result.compareTo(JsonReaderImplementation.MIN_INTEGER_VALUE) >= 0)
					&& (0 >= result.compareTo(JsonReaderImplementation.MAX_INTEGER_VALUE))) {
				number = Integer.valueOf(result.intValue());
			} else if ((result.compareTo(JsonReaderImplementation.MIN_LONG_VALUE) >= 0)
					&& (0 >= result.compareTo(JsonReaderImplementation.MAX_LONG_VALUE))) {
				number = Long.valueOf(result.longValue());
			} else {
				number = result;
			}
		} else {
			number = new BigDecimal(stringBuilder.toString());
		}
		return new JsonImplementation(number);
	}

	/**
	 * Reads the next JSON boolean with true value.
	 *
	 * @return The next JSON boolean with true value.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readTrue() throws IOException {
		this.consumeWhitespace();
		this.check('t');
		this.check('r');
		this.check('u');
		this.check('e');
		return new JsonImplementation(Boolean.TRUE);
	}

	/**
	 * Reads the next JSON boolean with false value.
	 *
	 * @return The next JSON boolean with false value.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readFalse() throws IOException {
		this.consumeWhitespace();
		this.check('f');
		this.check('a');
		this.check('l');
		this.check('s');
		this.check('e');
		return new JsonImplementation(Boolean.FALSE);
	}

	/**
	 * Reads the next JSON null.
	 *
	 * @return The next JSON null.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readNull() throws IOException {
		this.consumeWhitespace();
		this.check('n');
		this.check('u');
		this.check('l');
		this.check('l');
		return new JsonImplementation(null);
	}

	/**
	 * Reads the next JSON value.
	 *
	 * @return The next JSON value.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readValue() throws IOException {
		this.consumeWhitespace();
		switch (this.peek()) {
		case '{':
			return this.readObject();
		case '[':
			return this.readArray();
		case '\"':
			return new JsonImplementation(this.readString());
		case 't':
			return this.readTrue();
		case 'f':
			return this.readFalse();
		case 'n':
			return this.readNull();
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '-':
			return this.readNumber();
		default:
			throw this.unexpectedCharacter("\'{\', \'[\', \'t\', \'f\', \'n\', \'-\' or DIGIT");
		}
	}

	/**
	 * Reads the next JSON structure.
	 *
	 * @return The next JSON structure.
	 * @throws IllegalArgumentException
	 *                                      If an unexpected character is found.
	 * @throws IOException
	 *                                      If an I/O error occurs.
	 */
	private Json readStructure() throws IOException {
		this.consumeWhitespace();
		switch (this.peek()) {
		case '{':
			return this.readObject();
		case '[':
			return this.readArray();
		default:
			throw this.unexpectedCharacter("not \'{\' or \'[\'");
		}
	}

	@Override
	public Json read() throws IOException {
		return this.readStructure();
	}

}
