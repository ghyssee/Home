package be.home.common.utils;

import be.home.common.constants.Constants;
import be.home.common.exceptions.ApplicationException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ghyssee on 28/06/2016.
 */
public class JSONUtils {

    public static Object openJSONOld(String filename, Class className){
        InputStream i = null;
        Object object = null;
        try {
            i = new FileInputStream(filename);
            Reader reader = new InputStreamReader(i, "UTF-8");
            JsonReader r = new JsonReader(reader);
            Gson gson = new Gson();
            object = gson.fromJson(r, className);
            r.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to find file " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem processing file " +filename);
        }
        return object;

    }

    public static Object openJSON(String filename, Class className)  {
        return openJSON(filename, className, "UTF-8");

    }

    public static Object openJSON(String filename, Class className, String charSet)  {
        Path file = Paths.get(filename);
        BufferedReader reader = null;
        Object o = null;
        try {
            reader = Files.newBufferedReader(file, Charset.forName(charSet));
            JsonReader r = new JsonReader(reader);
            Gson gson =  new GsonBuilder().disableHtmlEscaping().create();
            o = gson.fromJson(r, className);
        } catch (IOException e) {
            throw new ApplicationException("Problem processing file " + filename);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new ApplicationException("Problem processing file " + filename);
                }
            }
        }
        return o;

    }

    public static void writeJsonFile(Object o, String filename) throws IOException {
        Path file = Paths.get(filename);
        BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));
        //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file), Charset.forName("ISO-8859-1")));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(o, writer);
        writer.flush();
        writer.close();
    }



}
