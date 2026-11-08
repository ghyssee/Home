package be.home.common.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

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
            if (StringUtils.isNotBlank(zipFolder)){
                parameters.setIncludeRootFolder(false);
                parameters.setRootFolderNameInZip(zipFolder);
            }
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
}
