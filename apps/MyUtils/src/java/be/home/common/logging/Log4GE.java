package be.home.common.logging;

import java.io.*;
import java.util.Date;

import be.home.common.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import be.home.common.exceptions.ApplicationException;

import java.util.ArrayList;
import java.util.List;

public class Log4GE {

	private File logFile = null;

	private static final String PREFIX = "* ";

	private static final int OUTLINE = 15;

	private static final int LENGTH = 200;

	private List<String> tableHeaders = null;

	private List<Integer> tableLengths = null;

	private static PrintWriter log2 = null;

	private Log4GE() {
	}

	public Log4GE(String logFile) {
		this.logFile = new File(logFile);
		open();

	}

	public Log4GE(String logPath, String logFile) {
		this.logFile = new File(be.home.common.utils.FileUtils.joinPathAndFile(logPath, logFile));
        System.out.println("LOG File = " + this.logFile.getAbsolutePath());
		open();

	}

	private void open(){
		try {
			log2 = new PrintWriter(new BufferedWriter(new FileWriter(logFile,
					true)));
		} catch (IOException ex) {
			System.err.println("There was a problem opening file " + logFile);
		}
	}

	public static Log4GE getLogger(String logFile) {

		if (logFile == null){
			throw new ApplicationException ("LogFile is <null>");
		}
		return new Log4GE(logFile);

	}

	private void write(String line) {

		//PrintWriter log = null;
		//try {
			//log = new PrintWriter(new BufferedWriter(new FileWriter(logFile,
			//		true)));
			String tmpLine = StringUtils.stripEnd(line, " ");
			log2.println(tmpLine);
			log2.flush();
			System.out.println(tmpLine);
			//log.close();
		//} catch (IOException ex) {
		//}
	}
	
	public void clear(){
		boolean open = false;
		if (this.logFile.exists()){
			if (log2 != null){
				log2.close();
				open = true;
			}
			FileUtils.deleteFile(this.logFile);
			if (open){
				open();
			}
		}
	}
	
	

	public void info(String line) {
		write(PREFIX + line);
	}

	public void emptyLine() {
		write(PREFIX);
	}

	public void title(String title) {
		write(PREFIX + StringUtils.rightPad("Batchjob: ", OUTLINE, ' ') + title);
	}

	public void start(String title) {
		write(StringUtils.repeat("*", LENGTH));
		title(title);
		write(PREFIX);
		write(PREFIX + StringUtils.rightPad("Started on: ", OUTLINE, ' ')
				+ new Date());
	}

	public void end() {
		write(PREFIX + "Ended on: " + new Date());
		write(StringUtils.repeat("*", LENGTH));
		write("");
		log2.close();
        System.out.println(StringUtils.repeat('=', 100));
        System.out.println("Results written to file " + this.logFile);
        System.out.println(StringUtils.repeat('=', 100));
	}

	public void error(String errorMessage) {
		write(PREFIX + "ERROR : " + errorMessage);
	}

	public void startTable() {

	}

	public void addColumn(String column, int length) {
		if (tableHeaders == null) {
			tableHeaders = new ArrayList<String>();
			tableLengths = new ArrayList<Integer>();
		}
		tableHeaders.add(column);
		tableLengths.add(new Integer(length));
	}

	public void printHeaders() {
		String header = "";
		int totalLength = 0;
		if (tableHeaders != null && tableHeaders.size() > 0) {
			for (int i = 0; i < tableHeaders.size(); i++) {
				header += StringUtils.rightPad((String) tableHeaders.get(i),
						((Integer) tableLengths.get(i)).intValue(), ' ');
				totalLength += ((Integer) tableLengths.get(i)).intValue();
			}
			write(PREFIX + StringUtils.repeat("-", totalLength));
			write(PREFIX + header);
			write(PREFIX + StringUtils.repeat("-", totalLength));
		}
	}

	public void printRow(String[] cells) {

		String line = "";
		if (tableHeaders != null && tableHeaders.size() > 0) {
			for (int i = 0; i < tableHeaders.size(); i++) {
				line += StringUtils.rightPad(cells[i], ((Integer) tableLengths
						.get(i)).intValue(), ' ');
			}
		}
		write(PREFIX + line);

	}

	public void endTable() {
		if (tableHeaders != null && tableHeaders.size() > 0) {
			int totalLength = 0;
			for (int i = 0; i < tableHeaders.size(); i++) {
				totalLength += ((Integer) tableLengths.get(i)).intValue();
			}
			write(PREFIX + StringUtils.repeat("-", totalLength));
		}
		tableHeaders = null;
		tableLengths = null;

	}

}
