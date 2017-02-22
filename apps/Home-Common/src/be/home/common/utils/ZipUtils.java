package be.home.common.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gebruiker on 25/12/2016.
 */
public class ZipUtils {

    private enum ZipType {
        FILE, FOLDER;
    }

    public ZipUtils() {
    }

    public void zipFile(String nameOfZipFile, String zipFolder, Path file) throws ZipException {

        zip(nameOfZipFile, zipFolder, file.toString(), ZipType.FILE);
    }

    public void zipFolder(String nameOfZipFile, String zipFolder, String folder) throws ZipException {

        zip(nameOfZipFile, zipFolder, folder, ZipType.FOLDER);
    }

    private void zip(String nameOfZipFile, String zipFolder, String folderOrFile, ZipType zipType) throws ZipException {
        ZipFile zipFile = new ZipFile(nameOfZipFile);

        // Initiate Zip Parameters which define various properties such
        // as compression method, etc.
        ZipParameters parameters = new ZipParameters();

        // set compression method to store compression
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

        // Set the compression level
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
        zipFolder = zipFolder.startsWith("/") ? zipFolder : "/" + zipFolder;
        parameters.setRootFolderInZip(zipFolder);

        switch (zipType){
            case FILE :
                zipFile.addFile(new File(folderOrFile), parameters);
                break;
            case FOLDER :
                parameters.setIncludeRootFolder(false);
                zipFile.addFolder(new File(folderOrFile), parameters);
                break;
        }
    }

}
