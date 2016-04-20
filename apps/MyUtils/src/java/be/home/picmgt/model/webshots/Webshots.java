package be.home.picmgt.model.webshots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ini4j.Profile.Section;

import be.home.common.constants.InitConstants;
import be.home.picmgt.model.BasePicMgt;
import be.home.common.utils.CSVReader;
import be.home.common.utils.CSVReaderImpl;

public class Webshots extends BasePicMgt {

	private static final Logger log = Logger.getLogger(Webshots.class);

	private static final int CATEGORY = 0;
	private static final int TITLE = 1;
	private static final int URL = 2;


	@Override
	public void start() {
        final String batchJob = "Webshots";
		log.info("Batchjob " + batchJob + " started on " + new Date());
		String csvFile = map.fetch(InitConstants.WEBSHOTS_CSV);
		validateParams();
		if (StringUtils.isEmpty(map.fetch(InitConstants.WEBSHOTS_CSV))) {
			// no CSV file specified, try to find series in INI file
			makeSeries();
		} else {
			try {
				importCSV(csvFile);
			} catch (FileNotFoundException e) {
				// csv file not found
				log.error("CSV File not found " + csvFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e);
			}
		}
		log.info("Batchjob " + batchJob + " ended on " + new Date());
	}

	private void importCSV(String csvFile) throws IOException {
		CSVReader csvReader = new CSVReaderImpl(new File(csvFile));
		while (!csvReader.isEof()) {
			// some code to handle the array of fields
			List<String> fields = csvReader.readln();
			if (csvReader.isEof()){
				break;
			}
			// ignore first line, because it contains the headers
			System.out.println(fields);
			if (csvReader.getLineNumber() > 1) {
				if (fields.size() == 3) {
					// get the url info
					WebshotSerie wb = new WebshotSerie(fields.get(CATEGORY) + " - " + fields.get(TITLE), 
							                           fields.get(CATEGORY) + "." + fields.get(TITLE),
					                                   map.fetch(InitConstants.WEBSHOTS_EXPORTFILE),
					                                   map.fetch(InitConstants.WEBSHOTS_RENAMEFILE));
					getFiles(wb, fields.get(URL));
				} else {
					log.warn("Line " + csvReader.getLineNumber()
							+ " contains not enough fields");
				}
			}
		}

	}
	
	private void getFiles(WebshotSerie wb, String url){
		try {
			wb.begin();
			String url2 = null;
			for (int i = 0; i < 10; i++) {
				url2 = makeURL(url, i);
				try {
					wb.readUrl(url2);
				} catch (FileNotFoundException e) {
					// url not found, continue reading the next line
					log.warn("URL not found " + url2);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void makeSeries() {
		String series = map.fetch(InitConstants.WEBSHOTS_SERIES);
		StringTokenizer st = new StringTokenizer(series, ",");
		while (st.hasMoreTokens()) {
			String serie = st.nextToken();
			Section sec = configIni.get(serie);
			WebshotSerie wb = new WebshotSerie(sec, map
					.fetch(InitConstants.WEBSHOTS_EXPORTFILE), map
					.fetch(InitConstants.WEBSHOTS_RENAMEFILE));
			getFiles(wb, sec.fetch(InitConstants.WEBSHOTS_URLFILE));
		}
	}

	private String makeURL(String url, int index) {
		String newUrl = map.fetch(InitConstants.WEBSHOTS_PREFIX)
				+ url;
		if (index > 0) {
			newUrl += "_" + String.valueOf(index);
		}
		newUrl += map.fetch(InitConstants.WEBSHOTS_SUFFIX);
		log.info("Getting info from URL " + newUrl);
		return newUrl;

	}

	private void validateParams() {

		this.validateParam(InitConstants.WEBSHOTS_PREFIX);
		this.validateParam(InitConstants.WEBSHOTS_SERIES);
	}
}
