package be.home.picmgt.model.wiki;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import be.home.common.constants.InitConstants;
import be.home.common.logging.Log4GE;
import be.home.picmgt.common.CSVPrinter;
import be.home.picmgt.common.CSVPrinterImpl;
import be.home.picmgt.model.BasePicMgt;
import be.home.picmgt.model.to.WikiAppsTO;
import be.home.common.utils.FileUtils;

public class Wiki extends BasePicMgt {

	private static final Logger log = Logger.getLogger(Wiki.class);

	private static final int CATEGORY = 0;
	private static final int TITLE = 1;
	private static final int URL = 2;
	public static Log4GE log4GE;

	@Override
	public void start() {
		
		final String batchJob = "Wiki";
		log4GE = new Log4GE(map.fetch(InitConstants.WIKI_RESULTLOG));
		log4GE.clear();
		log4GE.start("Wiki Make CSV file");
		log.info("Batchjob " + batchJob + " started on " + new Date());
		String csvFile = map.fetch(InitConstants.WIKI_INPUT);
		validateParams();
		if (StringUtils.isEmpty(map.fetch(InitConstants.WIKI_INPUT))) {
			// no CSV file specified, try to find series in INI file

		} else {
			try {
				List <WikiAppsTO> apps = this.readInputFile(map.fetch(InitConstants.WIKI_INPUT));
				apps = validateWikiApps(apps);
				makeWikiCSV(apps, map.fetch(InitConstants.WIKI_OUTPUT));
				
//			} catch (FileNotFoundException e) {
				// csv file not found
//				log.error("CSV File not found " + csvFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		log.info("Batchjob " + batchJob + " ended on " + new Date());
		log4GE.end();
		System.out.println("Results are witten to the logFile " + map.fetch(InitConstants.WIKI_RESULTLOG));
		
	}
	
	private List <WikiAppsTO> readInputFile(String inputFile) throws IOException {
		List <String> lines = FileUtils.getContents(new File(inputFile));
		List <WikiAppsTO> apps = new ArrayList<WikiAppsTO>();
		WikiAppsTO wikiApps = new WikiAppsTO();
		String title = "";
		String description = "";
		String page = "";
		boolean startTagTitle = false;
		boolean startTagDescription = true;
		boolean startTagPage = false;
		for(String line: lines){
			//System.out.println(line);
			if (line.equals("<Page>")){
				wikiApps = new WikiAppsTO();
				System.out.println("start Page");
				title = "";
				description = "";
				startTagTitle = false;
				startTagDescription = false;
				startTagPage = true;
				page = "";
			}
			else if (line.equals("</Page>")){
				System.out.println("end Page");
				startTagPage = false;
			}
			else if (line.equals("<Title>")){
				wikiApps = new WikiAppsTO();
				wikiApps.setPage(page);
				System.out.println("start Title");
				title = "";
				startTagTitle = true;
				startTagDescription = false;
				description = "";
			}
			else if (line.equals("</Title>")){
				System.out.println("end Title");
				//wikiApps.setTitle(replaceSpecialChars(title).replaceAll("|", "-"));
				wikiApps.setTitle(replaceSpecialCharsTitle(title));
				title = "";
				startTagTitle = false;
			}
			else if (line.equals("<Description>")){
				System.out.println("start Title");
				title = "";
				startTagTitle = false;
				startTagDescription = true;
				description = "";
			}
			else if (line.equals("</Description>")){
				System.out.println("end description");
				wikiApps.setDescription(replaceSpecialChars(description));
				startTagDescription = false;
				description = "";
				apps.add(wikiApps);
			}
			else if (startTagPage){
				page = page + line; 
			}
			else if (startTagTitle){
				title = title + line; 
			}
			else if (startTagDescription){
				description = description + line + WikiHelper.LINE_BREAK; 
			}
			else {
				System.err.println("Invalid Line : " + line);
				log4GE.error("Invalid Line : " + line);
			}
		}
		return apps;
	}
	
	private List <WikiAppsTO> validateWikiApps(List <WikiAppsTO> apps){
		
		List <WikiAppsTO> newApps = new ArrayList<WikiAppsTO>();
		log4GE.startTable();
		log4GE.addColumn("ErrorMessage", 40);
		log4GE.addColumn("Wiki Input Information", 100);
		log4GE.printHeaders();
		int errors = 0;
		for (WikiAppsTO wikiApp: apps){
			if (StringUtils.isEmpty(wikiApp.getPage())){
				log4GE.printRow(new String[] {"App without pagename specified", wikiApp.getTitle()});
				errors++;
			}
			else if (StringUtils.isEmpty(wikiApp.getTitle())){
				log4GE.printRow(new String[] {"App without title specified", wikiApp.getDescription()});
				errors++;
			}
			else if (StringUtils.isEmpty(wikiApp.getDescription())){
				log4GE.printRow(new String[] {"App without description specified", wikiApp.getTitle()});
				errors++;
			}
			else {
				newApps.add(wikiApp);
			}
		}
		log4GE.endTable();
		if (errors == 0){
			log4GE.info("No Errors Found");
		}
		else {
			log4GE.info(Integer.toString(errors) + " Error(s) found");
		}
		log4GE.emptyLine();
		return newApps;
	}

	private void makeWikiCSV(List <WikiAppsTO> apps, String outputFile) throws IOException {
	      int counter = 1;
	      CSVPrinter csvPrinter = getOutputFile(outputFile, counter);
	      //String PAGENAME = "Android/Apps";
	      String line = "";
	      int maxAppsFile = Integer.valueOf(map.fetch(InitConstants.WIKI_MAXAPPSFILE));
	      if (maxAppsFile <= 0) {
	    	  maxAppsFile = 30;
	      }
	      Collections.sort(apps);
	      
	      // write header
	      header(csvPrinter);
	      String page = "";
	      boolean firstPage = true;
		
			for (WikiAppsTO wikiApp: apps){
				if (!page.equals(wikiApp.getPage())){
					if (!firstPage){
						csvPrinter.print(page);
						csvPrinter.println(line);
						line = "";
					}
					else {
						firstPage = false;
					}
					page = wikiApp.getPage();
				}
				line+= "[[" + wikiApp.getPage() + "/" + wikiApp.getTitle() + "|" + wikiApp.getTitle() + "]]" + WikiHelper.LINE_BREAK;
			}
			csvPrinter.print(page);
			csvPrinter.println(line);

			log4GE.info("List of processed Apps");
			log4GE.startTable();
			log4GE.addColumn("Page", 40);
			log4GE.addColumn("Title", 100);
			log4GE.printHeaders();
	        int nr = 0;
			for (WikiAppsTO wikiApp: apps){
				if (nr > maxAppsFile){
					csvPrinter.close();
					counter++;
					csvPrinter = getOutputFile(outputFile, counter);
					header(csvPrinter);
					nr = 0;
				}
				log4GE.printRow ( new String[] {wikiApp.getPage(), wikiApp.getTitle()});
				System.out.println("Page = " + wikiApp.getPage());
				System.out.println("Title = " + wikiApp.getTitle());
				System.out.println("Description = " + wikiApp.getDescription());
				csvPrinter.print(wikiApp.getPage() + "/" + wikiApp.getTitle());
				csvPrinter.println(WikiHelper.getWikiPageTitle(wikiApp.getTitle()) + wikiApp.getDescription());
				nr++;
			}
			log4GE.endTable();
			log4GE.info(Integer.toString(apps.size()) + " Apps(s) found");
			csvPrinter.close();
	}
	
	private void header(CSVPrinter csvPrinter){
	      // write header
	      csvPrinter.print("Titel");
	      csvPrinter.println("Vrije tekst");
	}
	
	private CSVPrinter getOutputFile(String outputFile, int counter) throws FileNotFoundException{
	      Charset charset = Charset.forName("UTF-16");
	      String csvFile = outputFile.replaceFirst("%counter%", String.format("%02d", counter));
	      BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), charset));
	      CSVPrinter csvPrinter = new CSVPrinterImpl(output);
	      csvPrinter.setdelimiterChar(',');
	      return csvPrinter;
		
	}
	
	private String replaceSpecialChars(String a){
		String b = a;
		if (a != null){
	    	Character y = Character.valueOf('\u221a');
			b = a.replace('\u221a', '\u2713');
	    	b = b.replace('\uff1a', ':');
	    	//b = b.replace("[", "<nowiki>[</nowiki>");
	    	b = b.replace("[", "(");
			//b = a.replaceAll("\"", "\"\"");
		}
		return b;
	}

	private String replaceSpecialCharsTitle(String a){
		// impossible to use a [ in the title of a MediaWiki Page, so replace it
		String b = a;
		if (a != null){
	    	Character y = Character.valueOf('\u221a');
	    	b = replaceSpecialChars(a);
	    	b = b.replace("[", "(");
	    	b = b.replace("]", ")");
		}
		return b;
	}

	private void validateParams() {

		//this.validateParam(InitConstants.WEBSHOTS_PREFIX);
		//this.validateParam(InitConstants.WEBSHOTS_SERIES);
	}
}
