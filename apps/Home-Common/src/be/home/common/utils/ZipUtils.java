package be.home.common.utils;

import net.lingala.zip4j.core.ZipFile;
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

    String zipFile;

    public ZipUtils(String zipfile) {
        this.zipFile = zipFile;
    }

    public void zip(String nameOfZipFile, String folderOrFile, String zipPath) {
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
        long milliseconds = time.getTime();

        // Initiate ZipFile object with the path/name of the zip file.
        Path path = Paths.get(folderOrFile);
        if (Files.exists(path)) {
            try {
                ZipFile zipFile = new ZipFile(nameOfZipFile);

                // Folder to add
                Path path2 = Paths.get(folderOrFile);
                String folderToAdd = path.toString();

                // Initiate Zip Parameters which define various properties such
                // as compression method, etc.
                ZipParameters parameters = new ZipParameters();

                // set compression method to store compression
                parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

                // Set the compression level
                parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
                parameters.setRootFolderInZip(zipPath);
                // Add folder to the zip file
                zipFile.addFile(new File(path2.toString()), parameters);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void zipFile() {
        try {
            Calendar calendar = Calendar.getInstance();
            Date time = calendar.getTime();
            long milliseconds = time.getTime();

            // Initiate ZipFile object with the path/name of the zip file.
            Path path = Paths.get("c:/My Backups/tmpmezmmo");
            ZipFile zipFile = new ZipFile(path + "_" + milliseconds + ".zip");

            // Folder to add
            Path path2 = Paths.get("c:/My Data/mgofilealbum.csv");
            String folderToAdd = path.toString();

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc.
            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
            parameters.setRootFolderInZip("/mezzmo");
            // Add folder to the zip file
            zipFile.addFile(new File(path2.toString()), parameters);
            parameters.setRootFolderInZip("/test");
            zipFile.addFile(new File("c:/My Data/fields.csv"), parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
