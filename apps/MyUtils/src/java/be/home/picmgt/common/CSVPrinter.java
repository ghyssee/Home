package be.home.picmgt.common;

/**
 * This interface is designed to be set of general methods that all CSV printers should implement.
 * CSV Printers write files in comma separated value format.
 */

  /**
   * Print values as a comma separated list.
   */
  public interface CSVPrinter {

	/**
	 * Print the string as the last value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void println(String value);

	/**
	 * Output a blank line.
	 */
	public void println();

	/**
	 * Print a single line of comma separated values.
	 * The values will be quoted if needed.  Quotes and
	 * and other characters that need it will be escaped.
	 *
	 * @param values values to be outputted.
	 */
	public void println(String[] values);

	/**
	 * Print the string as the next value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void print(String value);
	
	/**
	 * Close the CSVPrinter
	 *
	 */
	public void close();
	
	public int getNrOfLines();
	
	public void setdelimiterChar(char c);
}
