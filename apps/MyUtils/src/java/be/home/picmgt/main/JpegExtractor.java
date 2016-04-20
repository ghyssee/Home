package be.home.picmgt.main;

/**
*
 * jpegextractor
 *
 * A command line program to extract embedded JPEG streams from
 * files or standard input.
 *
 * Written by Marco Schmidt <marcoschmidt@users.sourceforge.net>
 * Version 1.0
 * Homepage http://www.geocities.com/marcoschmidt.geo/jpeg-extractor.html
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * A command line program to extract embedded JPEG streams from arbitrary files.
 *
 * Homepage of the tool is <a href="http://www.geocities.com/marcoschmidt.geo/jpeg-extractor.html">http://www.geocities.com/marcoschmidt.geo/jpeg-extractor.html</a>.
 * @author Marco Schmidt
 * @version 1.0
 */
public class JpegExtractor
{
	/**
	 * A class similar to java.util.Iterator (which is only available in Java 1.2
 	 * and higher, so it is rewritten here).
 	 * Returns the elements of a Vector one after another.
 	 */
	class Iterator
	{
		private Vector v;
		private int currentIndex;

		public Iterator(Vector aVector)
		{
			v = aVector;
			currentIndex = 0;
		}

		public boolean hasNext()
		{
			return v != null && currentIndex < v.size();
		}

		public Object next()
		{
			if (hasNext())
			{
				return v.elementAt(currentIndex++);
			}
			else
			{
				return null;
			}
		}
	}


	private int initialIndex;
	private Vector inputFileNames;
	private int numDigits;
	private File outputDirectory;
	private String outputPrefix;
	private String outputSuffix;
	private boolean overwrite;
	private long totalBytes;
	private long totalInputFiles;
	private long totalOutputFiles;
	private boolean quiet;

	public JpegExtractor(String[] args)
	{
		init(args);
	}

	/**
	 * Copies everything up to and including the byte sequence <code>ff d9</code> from
	 * input to output.
	 * It is assumed that three bytes (<code>ff d8 ff</code> have already been read from
	 * input.
	 * The output stream is closed unless an exception is thrown.
	 * @param in input stream from which is read
	 * @param out output stream to which is written
	 * @throws IOException if either reading or writing fails
	 */
	private void copyJpeg(InputStream in, OutputStream out) throws
		IOException
	{
		boolean notFinished = true;
		long copiedBytes = 3;
		do
		{
			int v1 = in.read();
			if (v1 == 0xff) // marker
			{
				int v2 = in.read();
				if (v2 < 0) // end of file
				{
					out.write(0xff);
					copiedBytes++;
					notFinished = false;
				}
				else
				if (v2 == 0xd9) // embedded JPEG stream ended
				{
					// copy the end of stream marker
					out.write(0xff);
					out.write(0xd9);
					copiedBytes += 2;
					notFinished = false;
				}
				else
				{
					// copy the two bytes, just a marker of the embedded JPEG
					out.write(0xff);
					out.write(v2);
					copiedBytes += 2;
				}
			}
			else
			if (v1 == -1) // unexpected end of input file
			{
				notFinished = false;
			}
			else // just copy that value
			{
				out.write(v1);
				copiedBytes++;
			}
		}
		while (notFinished);
		totalBytes += copiedBytes;
		if (!quiet)
		{
			System.out.println(" (" + copiedBytes + " bytes)");
		}
		totalOutputFiles++;
		// close output stream
		try
		{
			out.close();
		}
		catch (IOException ioe)
		{
			// ignore error when closing output stream
		}
	}

	private OutputStream createNextOutputStream() throws
		IOException
	{
		OutputStream result = null;
		do
		{
			String number = Integer.toString(initialIndex++);
			if (numDigits > 0)
			{
				while (number.length() < numDigits)
				{
					number = "0" + number;
				}
			}
			String name = outputPrefix + number + outputSuffix;
			File outputFile = new File(outputDirectory, name);
			if (overwrite || !outputFile.exists())
			{
				result = new BufferedOutputStream(new FileOutputStream(outputFile));
				if (!quiet)
				{
					System.out.print(" =>" + name);
				}
			}
		}
		while (result == null);
		return result;
	}

	private void printUsageAndTerminate()
	{
		System.out.println("Usage: java jpegextractor <OPTIONS> [FILEs]");
		System.out.println("Extract embedded JPEG streams from arbitrary files or standard input.");
		System.out.println("");
		System.out.println("Options:");
		System.out.println("\t-H, --help                 Print this help screen and terminate.");
		System.out.println("\t-d, --digits NUM           Pad numbers in output files to NUM digits.");
		System.out.println("\t-D, --outputdirectory DIR  Write to directory DIR (default: \".\").");
		System.out.println("\t-p, --prefix P             Use P as output prefix (default: \"output\").");
		System.out.println("\t-s, --suffix S             Use S as output suffix (default: \".jpg\").");
		System.out.println("\t-n, --initialnumber NUM    Use NUM as initial output number (default: 0).");
		System.out.println("\t-o, --overwrite            Overwrite existing output files.");
		System.out.println("\t-q, --quiet                Nothing is written to standard output.");
		System.out.println();
		System.out.println("Copyright (C) 2002 Marco Schmidt <marcoschmidt@users.sourceforge.net>");
		System.out.println("Homepage http://www.geocities.com/marcoschmidt.geo/jpeg-extractor.html");
		System.out.println();
		System.out.println("This program is distributed under the GNU Lesser General Public");
		System.out.println("License 2.1. See http://www.gnu.org/copyleft/lesser.html for more.");
		System.exit(0);
	}

	private void process(InputStream in) throws
		IOException
	{
		int v1;
		do
		{
			v1 = in.read();
			if (v1 == 0xff)
			{
				int v2 = in.read();
				if (v2 == 0xd8)
				{
					int v3 = in.read();
					if (v3 == 0xff)
					{
						OutputStream out = createNextOutputStream();
						out.write(v1);
						out.write(v2);
						out.write(v3);
						copyJpeg(in, out);
					}
				}
			}
		}
		while (v1 != -1);
	}

	private void process(String fileName) throws
		IOException
	{
		BufferedInputStream in = null;
		try
		{
			in = new BufferedInputStream(new FileInputStream(fileName));
			if (!quiet)
			{
				System.out.println(fileName);
			}
		}
		catch (IOException ioe)
		{
			System.err.println("Error while opening \"" + fileName + "\": " + ioe.toString());
			return;
		}
		process(in);
	}

	/**
	 * Put program parameters from argument String array into Vector
	 * and call {@link #init(Vector)}.
	 * @param args String objects to be put into a Vector
	 */
	@SuppressWarnings("unchecked")
	private void init(String[] args)
	{
		Vector v = new Vector();
		if (args != null && args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				v.addElement(args[i]);
			}
		}
		init(v);
	}

	@SuppressWarnings("unchecked")
	private void init(Vector args)
	{
		setDefaultArguments();
		Iterator iter = new Iterator(args);
		while (iter.hasNext())
		{
			String s = (String)iter.next();
			if ("-o".equals(s) || "--overwrite".equals(s))
			{
				overwrite = true;
			}
			else
			if ("-q".equals(s) || "--quiet".equals(s))
			{
				quiet = true;
			}
			else
			if ("-H".equals(s) || "--help".equals(s))
			{
				printUsageAndTerminate();
			}
			else
			if ("-D".equals(s) || "--outputdirectory".equals(s))
			{
				if (!iter.hasNext())
				{
					System.err.println("Output directory switch needs directory name as argument.");
					System.exit(1);
				}
				String name = (String)iter.next();
				outputDirectory = new File(name);
				if (!outputDirectory.isDirectory())
				{
					System.err.println("Error: \"" + s + "\" does not seem to be a directory.");
				}
			}
			else
			if ("-d".equals(s) || "--digits".equals(s))
			{
				if (!iter.hasNext())
				{
					System.err.println("Digits switch needs number > 0 as argument.");
					System.exit(1);
				}
				String num = (String)iter.next();
				boolean error;
				try
				{
					numDigits = Integer.parseInt(num);
					error = (numDigits < 1);
					
				}
				catch (NumberFormatException nfe)
				{
					error = true;
				}
				if (error)
				{
					System.err.println(num + " is not an integer number larger than 0.");
					System.exit(1);
				}
			}
			else
			if ("-n".equals(s) || "--initialnumber".equals(s))
			{
				if (!iter.hasNext())
				{
					System.err.println("Initial number switch needs number >= 0 as argument.");
					System.exit(1);
				}
				String num = (String)iter.next();
				boolean error;
				try
				{
					initialIndex = Integer.parseInt(num);
					error = (initialIndex < 1);
				}
				catch (NumberFormatException nfe)
				{
					error = true;
				}
				if (error)
				{
					System.err.println(num + " is not an integer number larger than or equal to 0.");
					System.exit(1);
				}
			}
			else
			if ("-p".equals(s) || "--prefix".equals(s))
			{
				if (!iter.hasNext())
				{
					System.err.println("Prefix switch needs argument.");
					System.exit(1);
				}
				outputPrefix = (String)iter.next();
			}
			else
			if ("-s".equals(s) || "--suffix".equals(s))
			{
				if (!iter.hasNext())
				{
					System.err.println("Suffix switch needs argument.");
					System.exit(1);
				}
				outputSuffix = (String)iter.next();
			}
			else
			{
				inputFileNames.addElement(s);
			}
		}
	}

	public static void main(String[] args)
	{
		JpegExtractor ext = new JpegExtractor(args);
		ext.run();
	}

	public void run()
	{
		Iterator iter = new Iterator(inputFileNames);
		if (iter.hasNext())
		{
			do
			{
				String fileName = (String)iter.next();
				try
				{
					totalInputFiles++;
					process(fileName);
				}
				catch (IOException ioe)
				{
					System.err.println("Error while processing file \"" + fileName + "\": " + ioe.toString());
				}
			}
			while (iter.hasNext());
		}
		else
		{
			try
			{
				totalInputFiles++;
				process(new BufferedInputStream(System.in));
			}
			catch (IOException ioe)
			{
				System.err.println("Error while reading from standard input: " + ioe.toString());
			}
		}
		if (!quiet)
		{
			System.out.println("Extracted " + totalOutputFiles + " JPEG file(s) with " + totalBytes + " bytes from " + totalInputFiles + " input file(s).");
		}
	}

	private void setDefaultArguments()
	{
		initialIndex = 0;
		inputFileNames = new Vector();
		numDigits = -1;
		outputDirectory = new File(System.getProperty("user.dir"));
		outputPrefix = "output";
		outputSuffix = ".jpg";
		overwrite = false;
		quiet = false;
		totalBytes = 0;
		totalOutputFiles = 0;
		totalInputFiles = 0;
	}
}
