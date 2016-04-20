package be.home.common.utils;

import java.io.IOException;
import java.util.List;

/**
 * This interface is designed to be set of general methods that all CSV readers should implement.
 * CSV readers reads files in comma separated value format.
 * An example of how CSVReader might be used:
 * <pre>
 * CSVReader csvReader = new CSVReader(new File("c:/test.csv");
 * List fields = null;
 * while (!csvReader.isEof()){
 *      // some code to handle the array of fields
 *      fields = csvReader.readln();
 * }
 * </pre>
 */

  /**
  * Reads a line of a CSV-file and returns an array of fields
  */

  public interface CSVReader {

	/**
	 * Reads a string from a CSV-file.
	 * will be quoted if needed.
	 *
	 * @returns String[] an array of strings. Each field is an element of the array
	 */

	public List <String> readln() throws IOException;


	/**
	 * Checks if the EOF is reached.
	 *
	 * @returns boolean             true if EOF is reached, otherwise false
	 */

	public boolean isEof();


	/**
	 * Closes the CSV-file.
	 *
	 */

	public void close() throws IOException;

	/**
	 * ignore the first character of a line
   * param arg         if true, ignore the first character of the string
	 *
	 */

	public void ignoreFirstChar(boolean arg);


	/**
	 * Gets the current line number (0 means : no lines read yet)
	 *
	 */

	public int getLineNumber() throws IOException;


	/**
  * Getters and setters
	*/

  public void setdelimiterChar(char c);
  public char getdelimiterChar();
}
