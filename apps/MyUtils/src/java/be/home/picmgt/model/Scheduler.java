package be.home.picmgt.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import be.home.common.utils.CSVReader;
import be.home.common.utils.CSVReaderImpl;
import be.home.common.utils.FileComparator;
import java.util.Date;
import org.apache.log4j.Logger;
import be.home.common.constants.InitConstants;
import be.home.common.exceptions.ApplicationException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import be.home.common.utils.FileUtils;
import be.home.picmgt.model.to.ErrorFilesTO;

public class Scheduler extends BasePicMgt {

	private static final Logger log = Logger.getLogger(Scheduler.class);

	public Scheduler() {
	}

	public void start() {

		String batchJob = "Scheduler";
		log.info("Batchjob " + batchJob + " started on " + new Date());
		log4GE.start(batchJob);
		// String sourceFolder = (String) map.fetch("sourceFolder");
		// readRootFolders(sourceFolder);
		// removeEmptySubFolders(sourceFolder);
		checkSchedule();
		log.info("Batchjob " + batchJob + " ended on " + new Date());
		log4GE.end();
	}

	public void checkSchedule() {
		String configFile = (String) map.fetch("config");
		try {
			CSVReader scheduleTasks = new CSVReaderImpl(new File(configFile));
			do {
				List fields = scheduleTasks.readln();
				if (fields != null) {
					log.info(fields.get(0));
					log.info(fields.get(1));
					task((String) fields.get(0), (String) fields.get(1), scheduleTasks.getLineNumber());
					// Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c",
					// "\"C:/My Programs/iMacros/Datasources/bin/MW2h.cmd\""});
					// Runtime.getRuntime().exec("MW2h.cmd", null, new
					// File("C:/My Programs/iMacros/Datasources/bin"));
					// Runtime.getRuntime().exec("\"C:/My Programs/iMacros/Datasources/bin/MW2h.bat\"");
					// Runtime.getRuntime().exec(new
					// String[]{"\"C:/My Programs/iMacros/Datasources/bin/MW2h.cmd\""});

					// Process p =
					// Runtime.getRuntime().exec("cmd /c \"C:/My Programs/iMacros/Datasources/bin/MW2h.cmd\"");
					// try {
					// p.waitFor();
					// } catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// log.info(p.exitValue());
				}
			} while (!scheduleTasks.isEof());
		} catch (FileNotFoundException ex) {
			log.error("File " + configFile + " not found");
		} catch (IOException ex) {
			log.error("Problem reading file " + configFile);
		}

	}

	public void task(String cmdLine, String time, int lineNumber) {
		ProcessBuilder pb = new ProcessBuilder(
				"C:/My Programs/iMacros/Datasources/bin/MW2h.cmd", "", "");
		Map<String, String> env = pb.environment();
		env.clear();
		// env.put("YEAR", "2013");
		// env.put("MONTH", "05");
		// env.put("DATE", "21");
		// env.put("HOUR", "13");
		// env.put("MINUTES", "00");
		env.put("SUFFIX", "TT");
		// env.put("DATASOURCE_DIR", "c:/My Programs/iMacros/Datasources");
		pb.directory(new File("c:/My Programs/iMacros/Datasources/bin"));

		// File log = new File("log");
		// pb.redirectErrorStream(true);
		// pb.redirectOutput(Redirect.appendTo(log));
		try {
			Process p = pb.start();
			try {
				p.waitFor();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println("000 - " + line);
			}
			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// assert pb.redirectInput() == Redirect.PIPE;
		// assert pb.redirectOutput().file() == log;
		// assert p.getInputStream().read() == -1;
	}

	void task2(String cmdLine, String time) {
		try {
			Process p = Runtime.getRuntime().exec(
					"cmd /C \"C:/My Programs/iMacros/bin/do/MW2.cmd\"");
			BufferedReader in = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
