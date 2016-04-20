package be.home.picmgt.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import jonelo.jacksum.algorithm.Cksum;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import be.home.common.utils.FileUtils;
import be.home.picmgt.model.to.TarFileTO;


public class CheckSum {

	final static String INSTALL_FILE = "F035.V0006.00.00#1153.00_PMTV60_02_SCRIPTS";
	final static String INSTALL_FILE2 = "F035.V0006.00.00#1153.00_PMTV60_01_DML";
	final static String OUTPUT_PATH = "C:/Projects/pmt/DEV_V0006.00.00/db/V0006.00.00/";
	final static String INPUT_FILE2 = "changesRunOnce.txt";
	final static String INPUT_FILE = "changes.txt";
	final static String SCRIPTS_PATH = "C:\\Projects\\pmt\\DEV_V0006.00.00\\db";
	final static String SCRIPTS_PATH2 = "C:\\Projects\\pmt\\DEV_V0006.00.00\\db\\V0006.00.00\\TmpScripts";
	final static String INSTALL_EXTENSION = ".install";
	
	public static void main(String args[]) {
		createTAR("C:/Projects/DEV/PicMgt/data/tar.json");
		createTAR("C:/Projects/DEV/PicMgt/data/tar2.json");
	}
	
	private class TARConfig {
		String installFile;
		String scriptsPath;
		String outputPath;
		String inputFile;
		String installExtension;
		String tempPath;
		
		public String toString(){
			StringBuilder b = new StringBuilder();
			b.append("installFile : " + installFile + "\n");
			b.append("scriptsPath : " + scriptsPath + "\n");
			b.append("outputPath : " + outputPath + "\n");
			b.append("tempPath : " + tempPath + "\n");
			b.append("inputFile : " + inputFile + "\n");
			b.append("installExtension : " + installExtension);
			return b.toString();
		}
	}
	
	private static void createTAR(String filename){
		System.out.println("Making TAR file with Initailization file : " + filename);
		try {
			TARConfig config = init(filename);
			System.out.println(config.toString());
			copyModifiedScriptsToChangeFolder(config.outputPath + config.inputFile, config.scriptsPath,
					new File(config.tempPath));
			File installFile = new File(config.outputPath + config.installFile + config.installExtension);
			List<TarFileTO> files;
			files = listFiles(config.tempPath);
			for (TarFileTO file : files) {
				String line = StringUtils.rightPad(file.getFile().getName(),
						45, " ");
				line += StringUtils
						.rightPad("$XXPOST_TOP/install/sql", 25, " ");
				line += StringUtils.rightPad(file.getCrc() + " "
						+ file.getLength(), 20, " ");
				line += StringUtils.rightPad(file.getVersion() == null ? "???"
						: file.getVersion(), 5, " ");
				line += StringUtils.rightPad("Y", 4, " ");
				line += StringUtils.rightPad("SQL", 9, " ");
				line += "xxpmt_pwd";
				System.out.println(line);
			}
			makeInstallFile(files, config);
			TarFileTO tarFileTO = new TarFileTO(installFile.getName(),
					installFile, 0L, 0L);
			tarFileTO.setType("bin");
			files.add(tarFileTO);
			tar2(files, config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("FINISHED Creating TAR ");
		System.out.println(StringUtils.repeat("=", 100));
		
	}
	
	
	private static TARConfig init(String filename) throws FileNotFoundException, UnsupportedEncodingException{
		InputStream i = new FileInputStream(filename);
		Reader reader = new InputStreamReader(i,"UTF-8");
		JsonReader r = new JsonReader(reader);
		Gson gson = new Gson();
		TARConfig config = gson.fromJson(r, TARConfig.class);
		return config;
    }
	
	private static void copyModifiedScriptsToChangeFolder(String file, String scriptsPath, File destDir) throws Exception{
		File changeFile = new File(file);
		if (!changeFile.exists()){
			throw new Exception("File " + file + " Not Found!");
		}
		if (!destDir.exists()){
			destDir.mkdirs();
		}
		else {
			System.out.println("INFO: Deleting all files from the destination Directory: " + destDir.getAbsolutePath());
			File[] files = destDir.listFiles();
			for (int i=0; i < files.length; i++){
				System.out.println("INFO: Deleting File: " + files[i].getName());
				// some files can not be deleted directly, you'll have to do a garbage collection before delete
				System.gc();
				if (files[i].delete()){
					System.out.println("INFO: File: " + files[i].getName() + " successfully deleted");
				}
				else {
					System.err.println("INFO: File: " + files[i].getName() + " NOT deleted");
				}
			}
		}
		System.out.println("INFO: Input File is: " + changeFile.getAbsolutePath());
		System.out.println("INFO: Destination directory is: " + destDir.getAbsolutePath());
		BufferedReader in = new BufferedReader(new FileReader(changeFile));
		String line = null;
		int sequence = 1;
		do {
			line = in.readLine();
			if (line != null) {
				System.out.println(line);
				File tmpFile = new File(scriptsPath + "\\" + line);
				File destFile = new File(destDir.getAbsolutePath() + File.separator + String.format("%02d", sequence) + "_" + tmpFile.getName());
				if (tmpFile.exists()){
					FileUtils.copy(tmpFile, destFile, false);
				}
				else {
					System.err.println("Warning: File " + tmpFile.getAbsolutePath() + " does not exist!!!");
				}
			}
			sequence++;
		}
		while (line != null);
		in.close();
	}
	
	private static void makeInstallFile(List <TarFileTO> files, TARConfig config) throws Exception{
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy"); //09-FEB-15
		String formattedDate = formatter.format(new Date()).toUpperCase();
		
		
		System.setProperty("line.separator", "\n");
		Properties props = System.getProperties();
	    props.setProperty("line.separator", "\n");
		
		VelocityEngine ve = new VelocityEngine();
        ve.init("C:/Projects/DEV/PicMgt/templates/velocity.properties");
        /*  next, get the Template  */
        Template t = ve.getTemplate( "install.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("StringUtils", new StringUtils());
        context.put("installName", INSTALL_FILE + INSTALL_EXTENSION);
        context.put("currentDate", formattedDate);
        context.put("installVersion", "1.0");
        context.put("fileList", files);
        /* now render the template into a StringWriter */
        File installFile = new File(config.outputPath + config.installFile + config.installExtension + ".INIT");
        
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(installFile, false)));

        t.merge( context, out );
        //out.print
        out.flush();
        out.close();
        ConvertDosToUnixFile(installFile, new File(config.outputPath + config.installFile + config.installExtension));
        installFile.delete();
        
        
        /* show the World */
        //System.out.println( writer.toString() );     		
	}
	
	private static void ConvertDosToUnixFile(File oldFile, File newFile)  throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(oldFile));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
		String line = null;
		do {
			line = in.readLine();
			if (line != null) {
				out.write(line + "\n");
			}
		}
		while (line != null);
		in.close();
		out.flush();
		out.close();
	}
	
	
	private static List <TarFileTO> listFiles(String sourceDir) throws Exception{
		
		System.out.println("INFO: Get the list of updated script files from directory " + sourceDir);
		File folder = new File(sourceDir);
		if (!folder.exists()){
			throw new Exception("Directory where scripts should be in, not found: " + sourceDir);
		}
		File[] listOfFiles = folder.listFiles();
		List <TarFileTO> files = new ArrayList<TarFileTO>();
        Long crc;

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	String file = listOfFiles[i].getAbsolutePath();
	        Cksum ck = new Cksum();
	        ck.readFile(file);
	        crc = ck.getValue();
	        TarFileTO tarFileTO = new TarFileTO(listOfFiles[i].getName(), listOfFiles[i], crc, listOfFiles[i].getAbsoluteFile().length());
	        tarFileTO.setVersion(getVersion(listOfFiles[i].getAbsoluteFile()));
	        tarFileTO.setType("sql");
	        files.add(tarFileTO);
	        getVersion(listOfFiles[i].getAbsoluteFile());
	      } else if (listOfFiles[i].isDirectory()) {
	      }
	    }	
	    return files;
	}
	
	private static String getVersion(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = null;
		String version = null;
		do {
			line = in.readLine();
			if (line != null){
				if (StringUtils.contains(line, "$XXPOSTHeader")){
					//System.out.println(line);
					String[] fields = StringUtils.split(line);
					if (fields.length > 5){
						version = fields[3];
						break;
					}
				}
			}
		}
		while (line != null);
		in.close();
		return version;
	}
	
	private static void tar2(List <TarFileTO> files, TARConfig config) throws IOException{
		   TarArchiveOutputStream out = null;
		   try {
		        out = new TarArchiveOutputStream(
		             new GZIPOutputStream(
		                  new BufferedOutputStream(new FileOutputStream(config.outputPath + config.installFile + ".tar.gz"))));

				   for(TarFileTO tarFileTO : files){
					      TarArchiveEntry entry=new TarArchiveEntry(tarFileTO.getFile(), "install/" + tarFileTO.getType() + "/" + tarFileTO.getFileName());
					      out.putArchiveEntry(entry);
					      IOUtils.copy(new FileInputStream(tarFileTO.getFile()),out);
					      out.closeArchiveEntry();
				   }
		        
		        
		   } finally {
		        out.close();
		   }
	}

}
