package be.home.mezzmo.domain.util;

import be.home.model.MP3Settings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ghyssee on 12/07/2016.
 */
public class Utils {


    public String relativizeFile(String filename, MP3Settings.Mezzmo mezzmoConfig){
        // get relative path
        Path pathAbsolute = Paths.get(filename);
        Path pathBase = Paths.get(mezzmoConfig.base + File.separator + mezzmoConfig.playlist.path);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
    }
}
