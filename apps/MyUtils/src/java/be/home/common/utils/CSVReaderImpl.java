package be.home.common.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a line of a CSV-file and returns an array of fields
 */

public class CSVReaderImpl implements CSVReader {

	protected char delimiterChar = ';';

	protected char quoteChar = '"';

	protected BufferedReader in;

	private boolean eof = false;

	private int lineNumber = 0;

	private boolean firstCharIgnore = false;

	/**
	 * Reads a string from a CSV-file. will be quoted if needed.
	 * 
	 * @returns String[] an array of strings. Each field is an element of the
	 *          array
	 */

	public CSVReaderImpl(File filename) throws FileNotFoundException,
			IOException {
		this.in = new BufferedReader(new FileReader(filename));

	}

	public List <String> readln() throws IOException {
		String line = null;
		List <String> fields = null;
		line = in.readLine();
		if (line == null) {
			fields = null;
			eof = true;

		} else {
			lineNumber++;
			fields = parseToken(line);
		}
		return fields;

	}

	public boolean isEof() {
		return eof;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void close() throws IOException {
		in.close();
	}

	/**
	 * ignore the first character of a line param arg if true, ignore the first
	 * character of the string
	 * 
	 */

	public void ignoreFirstChar(boolean arg) {
		firstCharIgnore = arg;
	}

	/* Getters & Setters */
	public void setdelimiterChar(char c) {
		this.delimiterChar = c;
	}

	public char getdelimiterChar() {
		return this.delimiterChar;
	}

	/* private methods */

	private List <String> parseToken(String line) {

		if (line == null || "".equals(line)) {
			return null;
		}
		if (firstCharIgnore) {
			line = line.substring(1);
		}
		List<String> coll = new ArrayList<String>();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == delimiterChar) {
				coll.add(stringBuffer.toString());
				stringBuffer = new StringBuffer();
			} else {
				stringBuffer.append(line.charAt(i));
			}
		}
		if (stringBuffer.length() > 0) {
			coll.add(stringBuffer.toString());
		}
		// if last char was the delimiter add an empty field
		else if (line.endsWith(String.valueOf(getdelimiterChar()))) {
			coll.add("");
		}

		for (String fields : coll) {
			if (fields != null && fields.startsWith("\"")) {
				switch (fields.length()) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					if (fields.endsWith("\"")) {
						fields = "";
					}
					break;
				default:
					int length = fields.length() - 1
							- ((fields.endsWith("\"")) ? 1 : 0);
					fields = fields.substring(1, 1 + length);
					break;
				}

			}
		}
		return coll;
	}

}
