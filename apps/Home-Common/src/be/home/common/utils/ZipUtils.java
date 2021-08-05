package be.home.common.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.model.enums.AesKeyStrength;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by Gebruiker on 25/12/2016.
 */
public class ZipUtils {

    public ZipUtils() {
    }

    public static void zip(String nameOfZipFile, String zipFolder, String folderOrFile, String password) {
        try {
            zipFolder = zipFolder.startsWith("/") ? zipFolder : "/" + zipFolder;
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(CompressionMethod.DEFLATE);
            parameters.setCompressionLevel(CompressionLevel.MAXIMUM);
            parameters.setRootFolderNameInZip(zipFolder);
            //parameters.setDefaultFolderPath();
            //parameters.setDefaultFolderPath(zipFolder);
            ZipFile zipFile = new ZipFile(nameOfZipFile);

            if (password != null && password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.AES);
                parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
                zipFile.setPassword(password.toCharArray());
            }


            File targetFile = new File(folderOrFile);
            if (targetFile.isFile()) {
                zipFile.addFile(targetFile, parameters);
            } else if (targetFile.isDirectory()) {
                zipFile.addFolder(targetFile, parameters);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

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
    */

}
