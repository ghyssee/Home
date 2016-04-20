package be.home.picmgt.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.devlib.schmidt.imageinfo.ImageInfo;

import be.home.common.constants.InitConstants;
import be.home.common.constants.StatusConstants;
import be.home.common.utils.DateUtils;
import be.home.picmgt.common.CSVPrinter;
import be.home.picmgt.common.CSVPrinterImpl;

public class ImageReader extends BasePicMgt {

	private static final Logger log = Logger.getLogger(BasePicMgt.class);

	private CSVPrinter csvPrinter;

	private CSVPrinter csvBadPrinter;

	private int nrOfFiles = 0;

	private int nrOfDirs = 0;

	public ImageReader() {
	}

	public void start() {

		final String batchJob = this.getClass().toString();
		log.info("Batchjob " + batchJob + " started on " + new Date());
		log4GE.start(batchJob);
		String sourceFolder = (String) map.fetch("sourceFolder");
		log4GE.info("Source Folder : " + sourceFolder);
		int status = StatusConstants.FAILED;
		try {
			status = readRootFoldersA(sourceFolder);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
		}
		if (status == StatusConstants.FAILED) {
			log4GE
					.error("Job not executed properly. See LOG-file for more info");
		}
		log4GE.info("Nr of files read: " + nrOfFiles);
		log4GE.info("Nr of directories read: " + nrOfDirs);
		log4GE.end();
		log.info("Batchjob " + batchJob + " ended on " + new Date());

	}

	public int readRootFoldersA(String source) throws IOException,
			FileNotFoundException {

		File sourceFile = new File(source);
		if (!sourceFile.exists()) {
			log4GE.error("Source folder not found");
			return StatusConstants.FAILED;
		}
		int maxNrOfLines = Integer.parseInt(map
				.fetch(InitConstants.IMAGEINFO_MAXNROFLINES));
		log4GE.info("maxNrOfLines: " + maxNrOfLines);
		csvPrinter = openCSVFile();
		csvBadPrinter = openCSVBadFile();
		readFileInfo(sourceFile, maxNrOfLines);
		csvPrinter.close();
		csvBadPrinter.close();
		// remark: file will not be closed if exception occurs

		return StatusConstants.OK;
	}

	public void readFileInfo(File directory, int maxNrOfLines)
			throws IOException {

		File[] inputFolders = directory.listFiles();
		for (int i = 0; i < inputFolders.length; i++) {
			File inputFile = inputFolders[i];
			if (inputFile != null) {
				if (inputFile.isDirectory()) {
					log4GE.info("Now reading folder "
							+ inputFile.getAbsolutePath());
					nrOfDirs++;
					readFileInfo(inputFile, maxNrOfLines);
				} else if (inputFile.isFile()) {
					// log.info("Now reading file " +
					// inputFile.getAbsolutePath());
					if (this.csvPrinter.getNrOfLines() > maxNrOfLines) {
						this.csvPrinter.close();
						this.csvPrinter = openCSVFile();
					}
					viewFileInfo(inputFile, this.csvPrinter);
					nrOfFiles++;

				}
			}
		}
	}

	/*
	 * This method is obsolete and may be deleted
	 */

	public int readRootFolders(String source) throws IOException,
			FileNotFoundException {

		File sourceFile = new File(source);
		if (!sourceFile.exists()) {
			log4GE.error("Source folder not found");
			return StatusConstants.FAILED;
		}
		int maxNrOfLines = Integer.parseInt(map
				.fetch(InitConstants.IMAGEINFO_MAXNROFLINES));
		File[] inputFolders = sourceFile.listFiles();
		CSVPrinter csvPrinter = openCSVFile();
		for (int i = 0; i < inputFolders.length; i++) {
			File inputFolder = inputFolders[i];
			if (inputFolder != null && inputFolder.isDirectory()) {

				log4GE.info("Now reading folder " + inputFolder.getName());
				// Get all files from this subfolder
				File[] inputFiles = inputFolder.listFiles();
				for (int j = 0; j < inputFiles.length; j++) {
					File inputFile = inputFiles[j];
					// log.info("File : " + inputFile.getName());
					if (csvPrinter.getNrOfLines() > maxNrOfLines) {
						csvPrinter.close();
						csvPrinter = openCSVFile();
					}
					viewFileInfo(inputFile, csvPrinter);
				}
			}
		}
		// remark: file will not be closed if exception occurs

		return StatusConstants.OK;
	}

	public File ConstructCSVFile() {

		DecimalFormat df = new DecimalFormat(map
				.fetch(InitConstants.IMAGEINFO_COUNTERPATTTERN));
		int startCounter = 0;
		boolean exists = true;
		File f;

		do {
			startCounter++;
			String counter = df.format(startCounter);
			String fileName = map.fetch(InitConstants.IMAGEINFO_CSVPATH)
					+ map.fetch(InitConstants.IMAGEINFO_CSVFILE) + counter
					+ ".CSV";
			f = new File(fileName);
			exists = f.exists();
		} while (exists);

		return f;
	}

	public CSVPrinter openCSVFile() throws IOException {
		File f = ConstructCSVFile();
		CSVPrinter csvPrinter = openGlobalCSVFile(f);
		writeHeader(csvPrinter);
		return csvPrinter;
	}

	public CSVPrinter openGlobalCSVFile(File f) throws IOException {
		OutputStream out;
		f.createNewFile();
		if (f.canWrite()) {
			out = new FileOutputStream(f);
		} else {
			throw new IOException("Could not open " + f.getAbsolutePath());
		}
		CSVPrinterImpl csvPrinter = new CSVPrinterImpl(out);
		return csvPrinter;
	}

	public CSVPrinter openCSVBadFile() throws IOException {
		String fileName = map.fetch(InitConstants.IMAGEINFO_CSVPATH)
				+ map.fetch(InitConstants.IMAGEINFO_CSVBADFILE) + "."
				+ DateUtils.formatDate(new Date(), "yyyy.MM.dd.HH.mm.ss")
				+ ".CSV";
		File f = new File(fileName);
		CSVPrinter csvPrinter = openGlobalCSVFile(f);
		csvPrinter.print("Path");
		csvPrinter.print("Filename");
		csvPrinter.print("Format");
		csvPrinter.println("mimeType");
		return csvPrinter;
	}

	public void writeHeader(CSVPrinter csvPrinter) {
		csvPrinter.print("Path");
		csvPrinter.print("Filename");
		csvPrinter.print("Format");
		csvPrinter.print("mimeType");
		csvPrinter.print("Width");
		csvPrinter.print("Height");
		csvPrinter.print("BitsPerPixel");
		csvPrinter.print("NumberOfImages");
		csvPrinter.println("Comment(s)");
	}

	public void viewFileInfo(File jpgFile, CSVPrinter csvPrinter)
			throws FileNotFoundException {
		ImageInfo ii = new ImageInfo();
		FileInputStream fis = null;
		fis = new FileInputStream(jpgFile);
		ii.setInput(fis); // in can be InputStream or RandomAccessFile
		ii.setDetermineImageNumber(true); // default is false
		ii.setCollectComments(true); // default is false
		if (!ii.check()) {
			System.err.println(jpgFile.getAbsolutePath()
					+ "is not a supported image file format.");
			csvBadPrinter.print(jpgFile.getParent());
			csvBadPrinter.println(jpgFile.getName());
		} else if (ii.getFormat() == ImageInfo.FORMAT_JPEG) {
			/*
			 * log.info("Format name : " + ii.getFormatName());
			 * log.info("MimeType : " + ii.getMimeType());
			 * log.info("Width/Height : " + ii.getWidth() + " x " +
			 * ii.getHeight() + " pixels"); log.info("BitsPerPixel : " +
			 * ii.getBitsPerPixel()); log.info("NumberOfImages: " +
			 * ii.getNumberOfImages()); log.info("Comment(s) : " +
			 * ii.getNumberOfComments());
			 */
			csvPrinter.print(jpgFile.getParent());
			csvPrinter.print(jpgFile.getName());
			csvPrinter.print(ii.getFormatName());
			csvPrinter.print(ii.getMimeType());
			csvPrinter.print(String.valueOf(ii.getWidth()));
			csvPrinter.print(String.valueOf(ii.getHeight()));
			csvPrinter.print(String.valueOf(ii.getBitsPerPixel()));
			csvPrinter.print(String.valueOf(ii.getNumberOfImages()));
			csvPrinter.println(String.valueOf(ii.getNumberOfComments()));
		} else {
			System.err.println(jpgFile.getAbsolutePath()
					+ " is not a supported image file format.");
			csvBadPrinter.print(jpgFile.getParent());
			csvBadPrinter.print(jpgFile.getName());
			csvBadPrinter.print(ii.getFormatName());
			csvBadPrinter.println(ii.getMimeType());
		}
	}
}
