package be.home.picmgt.common;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * Print values as a comma separated list that can be read by the Excel spreadsheet.
 */
public class CSVPrinterImpl implements CSVPrinter {
	protected char delimiterChar = ';';
	protected char quoteChar = '"';
	protected PrintWriter out;
	protected int nrOfLines = 0;
	protected boolean newLine = true;

	/**
	 * Create a printer that will print values to the given
	 * stream.	 Character to byte conversion is done using
	 * the default character encoding.	Comments will be
	 * written using the default comment character '#'.
	 *
	 * @param out stream to which to print.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public CSVPrinterImpl (OutputStream os){
		this.out = new PrintWriter(os);
	}

	/**
	 * Create a printer that will print values to the given
	 * stream.	Comments will be
	 * written using the default comment character '#'.
	 *
	 * @param out stream to which to print.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public CSVPrinterImpl (Writer ow){
		if (ow instanceof PrintWriter){
			this.out = (PrintWriter)ow;
		} else {
			this.out = new PrintWriter(ow);
		}
	}

	/**
	 * Print the string as the last value on the line.	The value
	 * will be quoted if needed. If value is null, an empty value is printed.
	 *
	 * @param value value to be outputted.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public void println(String value){
		print(value);
		out.println();
		out.flush();
		newLine = true;
		nrOfLines++;
	}

	/**
	 * Start a new line.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public void println(){
		out.println();
		out.flush();
		newLine = true;
		nrOfLines++;
	}

	/**
	 * Print a single line of comma separated values.
	 * The values will be quoted if needed.  Quotes and
	 * newLine characters will be escaped.
	 *
	 * @param values values to be outputted.
	 * @throws NullPointerException if values is null.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public void println(String[] values){
		for (int i=0; i<values.length; i++){
			print(values[i]);
		}
		out.println();
		out.flush();
		newLine = true;
		nrOfLines++;
	}

	/**
	 * Print the string as the next value on the line.	The value
	 * will be quoted if needed. If value is null, an empty value is printed.
	 *
	 * @param value value to be outputted.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public void print(String value){
		if (value == null) {value = "";}
		boolean quote = false;
		if (value.length() > 0){
			for (int i=0; i<value.length(); i++){
				char c = value.charAt(i);
				if (c==quoteChar || c==delimiterChar || c=='\n' || c=='\r'){
					quote = true;
				}
			}
		} else if (newLine) {
			// always quote an empty token that is the first
			// on the line, as it may be the only thing on the
			// line.  If it were not quoted in that case,
			// an empty line has no tokens.
//			quote = true;
		}
		if (newLine){
			newLine = false;
		} else {
			out.print(delimiterChar);
		}
		if (quote){
			out.print(escapeAndQuote(value));
		} else {
			out.print(value);
		}
		out.flush();
	}

	/**
	 * Enclose the value in quotes and escape the quote
	 * and comma characters that are inside.
	 *
	 * @param value needs to be escaped and quoted.
	 *
	 * @return the value, escaped and quoted.
	 * @since ostermillerutils 1.00.00
	 */
	private String escapeAndQuote(String value){
    String s = StringUtils.replace(value, Character.toString(quoteChar), Character.toString(quoteChar) + Character.toString(quoteChar));
		return (new StringBuffer(2 + s.length())).append(quoteChar).append(s).append(quoteChar).toString();
	}

	/**
	 * Write some test data to the given file.
	 *
	 * @param args First argument is the file name.  System.out used if no filename given.
	 *
	 * @since ostermillerutils 1.00.00
	 */
	public static void main(String[] args) {
		OutputStream out;
		try {
			if (args.length > 0){
				File f = new File(args[0]);
				if (!f.exists()){
					f.createNewFile();
					if (f.canWrite()){
						out = new FileOutputStream(f);
					} else {
						throw new IOException("Could not open " + args[0]);
					}
				} else {
					throw new IOException("File already exists: " + args[0]);
				}
			} else {
				out = System.out;
			}
			CSVPrinterImpl p  = new CSVPrinterImpl(out);
			p.print("unquoted");
			p.print("escaped\"quote");
			p.println("comma,comma");
			p.print("!quoted");
			p.print("!unquoted");
			p.print(" quoted");
			p.println("quoted ");
			p.print("one");
			p.print("12");
			p.print("23");
			p.print("34");
			p.println("");
			p.println("two");
			p.print("\nthree\nline\n");
			p.println("\ttab");
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

  /* Getters & Setters */
  public void setdelimiterChar(char c) {this.delimiterChar = c;}
  public char getdelimiterChar() {return this.delimiterChar;}
  
  public void close(){
	if (this.out != null){
		this.out.close();
	}
  }
  
  public int getNrOfLines(){
	  return this.nrOfLines;
  }

}
