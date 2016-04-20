package be.home.main;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class HelloWorld {
    public static void main(String args[]) throws IOException, NoSuchFieldException, IllegalAccessException {

        System.setProperty("file.encoding","UTF-8");
        Field charset = Charset.class.getDeclaredField("defaultCharset");
        charset.setAccessible(true);
        charset.set(null,null);

        System.out.println("Hello World");
        String currentDir = System.getProperty("user.dir");
        File file = new File("g:/My Music/iPod/Ultratop 50 20140104 04 Januari 2014/23 Anna Kendrick - Cups (Pitch Perfect's \"When I'm Gone\").mp3");
        System.out.println(file.getName());
        File folder = new File("g:/My Music/iPod/Ultratop 50 20140104 04 Januari 2014");

        System.out.println(System.getProperty("file.encoding"));

        File[] listOfFiles = folder.listFiles();
        for (int i=0; i < listOfFiles.length; i++){
            if (listOfFiles[i].getName().startsWith("23")){
                System.out.println(listOfFiles[i].getName());
                System.out.println(URLEncoder.encode(listOfFiles[i].getName(), "UTF-8"));
                System.out.println(URLEncoder.encode( Normalizer.normalize(listOfFiles[i].getName(), Normalizer.Form.NFC), "UTF-8"));
                //System.out.println(URLDecoder.decode(listOfFiles[i].getName()));
                //System.out.println(normalizeUnicode(listOfFiles[i].getName()));
                if (listOfFiles[i].exists()){
                    System.out.println("EXIST");
                }
                else {
                    System.out.println("NOT EXIST");
                }
            }
        }


    }

    // Normalize to "Normalization Form Canonical Decomposition" (NFD)
    protected static String normalizeUnicode(String str) {
        Normalizer.Form form = Normalizer.Form.NFC;
        if (!Normalizer.isNormalized(str, form)) {
            return Normalizer.normalize(str, form);
        }
        return str;
    }

    private static Config init(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStream i = new FileInputStream(filename);
        Reader reader = new InputStreamReader(i, "UTF-8");
        JsonReader r = new JsonReader(reader);
        Gson gson = new Gson();
        Config config = gson.fromJson(r, Config.class);
        r.close();
        return config;
    }

    private class Log4J{
        String config;
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Log4J config : " + config + "\n");
            return b.toString();
        }
    }

    private class Wiki {
        private String inputDir;
        private String outputDir;
        private String inputFile;
        private String outputFile;
        private String maxAppsFile;

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Wiki inputDir : " + inputDir + "\n");
            b.append("Wiki outputDir : " + outputDir + "\n");
            b.append("Wiki inputFile : " + inputFile + "\n");
            b.append("Wiki outputFile : " + outputFile + "\n");
            b.append("Wiki maxAppsFile : " + maxAppsFile + "\n");
            return b.toString();
        }
    }

    private class Config {
        private String logFile;
        private Log4J log4J;
        private Wiki wiki;


        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("logFile : " + logFile + "\n");
            if (log4J != null){
                b.append(log4J.toString());
            }
            else {
                b.append("Parameter log4J does not exist in config file" + "\n");
            }
            if (wiki != null){
                b.append(wiki.toString());
            }
            else {
                b.append("Parameter wiki does not exist in config file" + "\n");
            }
            return b.toString();
        }
    }

}
