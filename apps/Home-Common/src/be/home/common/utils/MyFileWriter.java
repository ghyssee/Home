package be.home.common.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Gebruiker on 18/12/2016.
 */
public class MyFileWriter {

    private PrintWriter fw;
    public static final boolean APPEND = true;
    public static final boolean NO_APPEND = false;

    private MyFileWriter(){

    }

    public MyFileWriter(String filename, boolean append) throws IOException {
            fw = new PrintWriter( new FileWriter(filename, append));
    }

    public void append(String line) throws IOException {
        fw.println(line);
        fw.flush();
    }

    public void close() throws IOException {
        fw.close();
    }
}
