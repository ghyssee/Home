package be.home.common.utils;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by ghyssee on 27/10/2016.
 */
public class VelocityUtils {

    public void makeFile(String template, String outputFile, VelocityContext context) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( template );
        /*  create a context and add data */
        Path file = Paths.get(outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.defaultCharset());
            t.merge(context, writer);
        } finally {
            if (writer != null){
                writer.flush();
                writer.close();
            }
        }

    }
}
