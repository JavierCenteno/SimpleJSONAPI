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
	private Reader reader;
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
	public JsonReaderImplementation(Reader reader) throws IOException {
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
	public JsonReaderImplementation(InputStream inputStream, Charset charset) throws IOException {
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
	public JsonReaderImplementation(InputStream inputStream) throws IOException {
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
	public JsonReaderImplementation(String string) throws IOException {
		this(new ByteArrayInputStream(string.getBytes()));
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
	private IllegalArgumentException unexpectedCharacter(String expectedCharacters) {
		return new IllegalArgumentException("JSON parsing error near column:" + column + ", row:" + row + "; Expected "
				+ expectedCharacters + ", got \'" + (char) peek() + "\' instead");
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
	private void check(int character) throws IOException {
		if (peek() != character) {
			throw unexpectedCharacter("\'" + (char) character + "\'");
		}
		pop();
	}

	/**
	 * Get the current character from the Reader without advancing.
	 * 
	 * @return The current character from the Reader.
	 */
	private int peek() {
		return current;
	}

	/**
	 * Get the current character from the Reader and advance to the next one.
	 * 
	 * @return The current character from the Reader.
	 * @throws IOException
	 *                         If an I/O error occurs.
	 */
	private int pop() throws IOException {
		int previous = this.current;
		this.current = reader.read();
		if (current == '\n') {
			row = 1;
			++column;
		} else {
			++row;
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
		while (Character.isWhitespace(peek())) {
			pop();
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
		consumeWhitespace();
		check('{');
		Map<String, Json> map = new HashMap<>();
		consumeWhitespace();
		if (peek() == '}') {
			pop();
		} else {
			while (true) {
				String key = this.readString();
				consumeWhitespace();
				check(':');
				Json value = this.readValue();
				consumeWhitespace();
				map.put(key, value);
				if (peek() == ',') {
					pop();
				} else if (peek() == '}') {
					pop();
					break;
				} else {
					throw unexpectedCharacter("\',\' or \'}\'");
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
		check('[');
		List<Json> list = new ArrayList<>();
		consumeWhitespace();
		if (peek() == ']') {
			pop();
		} else {
			while (true) {
				Json value = this.readValue();
				consumeWhitespace();
				list.add(value);
				if (peek() == ',') {
					pop();
				} else if (peek() == ']') {
					pop();
					break;
				} else {
					throw unexpectedCharacter("\',\' or \']\'");
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
		consumeWhitespace();
		check('\"');
		StringBuilder stringBuilder = new StringBuilder();
		while (peek() != '\"') {
			if (peek() == '\\') {
				pop();
				switch (pop()) {
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
					String escapedUnicode = "";
					escapedUnicode += (char) pop();
					escapedUnicode += (char) pop();
					escapedUnicode += (char) pop();
					escapedUnicode += (char) pop();
					char unescapedUnicode = (char) Integer.parseInt(escapedUnicode, 16);
					stringBuilder.append(unescapedUnicode);
					break;
				}
			} else {
				stringBuilder.append((char) pop());
			}
		}
		pop();
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
		consumeWhitespace();
		StringBuilder stringBuilder = new StringBuilder();
		boolean isWhole = true;
		if (peek() == '-') {
			stringBuilder.append((char) pop());
		}
		while (Character.isDigit(peek())) {
			stringBuilder.append((char) pop());
		}
		if (peek() == '.') {
			stringBuilder.append((char) pop());
			isWhole = false;
			while (Character.isDigit(peek())) {
				stringBuilder.append((char) pop());
			}
		}
		if (peek() == 'e' || peek() == 'E') {
			stringBuilder.append((char) pop());
			isWhole = false;
			if (peek() == '-' || peek() == '+') {
				stringBuilder.append((char) pop());
			}
			while (Character.isDigit(peek())) {
				stringBuilder.append((char) pop());
			}
		}
		Number number;
		if (isWhole) {
			BigInteger result = new BigInteger(stringBuilder.toString());
			if (result.compareTo(MIN_BYTE_VALUE) >= 0 && 0 >= result.compareTo(MAX_BYTE_VALUE)) {
				number = new Byte(result.byteValue());
			} else if (result.compareTo(MIN_SHORT_VALUE) >= 0 && 0 >= result.compareTo(MAX_SHORT_VALUE)) {
				number = new Short(result.shortValue());
			} else if (result.compareTo(MIN_INTEGER_VALUE) >= 0 && 0 >= result.compareTo(MAX_INTEGER_VALUE)) {
				number = new Integer(result.intValue());
			} else if (result.compareTo(MIN_LONG_VALUE) >= 0 && 0 >= result.compareTo(MAX_LONG_VALUE)) {
				number = new Long(result.longValue());
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
		consumeWhitespace();
		check('t');
		check('r');
		check('u');
		check('e');
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
		consumeWhitespace();
		check('f');
		check('a');
		check('l');
		check('s');
		check('e');
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
		consumeWhitespace();
		check('n');
		check('u');
		check('l');
		check('l');
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
		consumeWhitespace();
		switch (peek()) {
		case '{':
			return readObject();
		case '[':
			return readArray();
		case '\"':
			return new JsonImplementation(readString());
		case 't':
			return readTrue();
		case 'f':
			return readFalse();
		case 'n':
			return readNull();
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
			return readNumber();
		default:
			throw unexpectedCharacter("\'{\', \'[\', \'t\', \'f\', \'n\', \'-\' or DIGIT");
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
		consumeWhitespace();
		switch (peek()) {
		case '{':
			return readObject();
		case '[':
			return readArray();
		default:
			throw unexpectedCharacter("not \'{\' or \'[\'");
		}
	}

	@Override
	public Json read() throws IOException {
		return readStructure();
	}

}
