package be.home.common.mp3;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Created by ghyssee on 12/07/2016.
 */
public class PlayList {

    private static final Logger log = Logger.getLogger(PlayList.class);

    public static final String EXTENSION = ".m3u";

    public void make(List<MGOFileAlbumCompositeTO> list, String destinationFolder, String outputFile) throws IOException {
        Properties p = new Properties();
        p.setProperty("file.resource.loader.path", Setup.getInstance().getFullPath(Constants.Path.VELOCITY));

        Path outputFolder = Paths.get(destinationFolder);
        if (Files.notExists(outputFolder)){
            outputFolder = Paths.get(Setup.getInstance().getFullPath(Constants.Path.PLAYLIST));
        }
        log.info("PlayList folder: " + outputFolder.toString());

        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        /*  next, get the Template  */
        Template t = ve.getTemplate( "Top20.vm" );
        /*  create a context and add data */
        VelocityContext context = new VelocityContext();
        context.put("list", list);
        Path file = Paths.get( outputFolder + File.separator + outputFile);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));
            t.merge(context, writer);
        }
        finally {
            if (writer != null){
                writer.flush();
                writer.close();
                log.info("PlayList created: " + file.toString());
            }
        }
    }
}
