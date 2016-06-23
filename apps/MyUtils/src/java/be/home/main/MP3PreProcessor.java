package be.home.main;

import be.home.common.archiving.Archiver;
import be.home.common.archiving.ZipArchiver;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.DateUtils;
import be.home.domain.model.MP3Helper;
import be.home.model.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MP3PreProcessor extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ZipFiles.class);
    private static ParamTO PARAMS [] = {new ParamTO("-source", new String[]{"This is the source directory to start the backup", "of files and folders"},
            ParamTO.REQUIRED),
            new ParamTO("-zipFile", new String[]{"This is the name of the zipfile"},
                    ParamTO.REQUIRED)
    };


    public static void main(String args[]) {

        String currentDir = System.getProperty("user.dir");
        log.info("Current Working dir: " + currentDir);


        MP3PreProcessor instance = new MP3PreProcessor();
        instance.printHeader("ZipFiles " + VERSION, "=");
        try {
            instance.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start() throws IOException {

        /*
        File albumInfo = new File("c:/My Programs/iMacros/output/album.txt");
        InputStream i = null;
        Reader reader;
        i = new FileInputStream(albumInfo.getAbsolutePath());
        reader = new InputStreamReader(i, "UTF-8");
        JsonReader r = new JsonReader(reader);
        Gson gson = new Gson();
        AlbumInfo.Config album = gson.fromJson(r, AlbumInfo.Config.class);
        */

        Path file = Paths.get("c:/reports/AlbumInfo.txt");
        BufferedReader reader2 = Files.newBufferedReader(file, Charset.defaultCharset());
        StringBuilder content = new StringBuilder();
        String line = null;
        while ((line = reader2.readLine()) != null) {
            processLine(line);
        }


    }

    private void processLine(String line){
        String tmp = line.trim();
        System.out.println(tmp);
        String fileName = "";
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(tmp);
        if (matcher.matches()) {
            System.out.println("matched");
            System.out.println(matcher.group(0));
        }
    }

}
