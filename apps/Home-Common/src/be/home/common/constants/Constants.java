package be.home.common.constants;

import be.home.common.utils.NetUtils;
import be.home.common.utils.WinUtils;

import java.io.File;
import java.nio.file.FileSystem;

/**
 * Created by ghyssee on 28/09/2015.
 */
public interface Constants {

    public interface Movies {

        public enum Import {
            ID(0), TITLE(1), ALTERNATE_TITLE(2), YEAR(3), GENRE(4), STOCKPLACE(5), IMDB_ID(6);
            private int value;

            private Import(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }
        }

        public enum Status {
            NFO, SOURCE, DESTINATION, ERROR_NFO, DIFFERENT_TITLE, EXIST_NFO;
        }
    }

    public interface Path {
        public static final String MP3_BASEDIR = "C:/My Data/tmp/Java/MP3Processor/";
        public static final String MP3_ALBUM = MP3_BASEDIR + "Album";
        public static final String MP3_PREPROCESSOR = MP3_BASEDIR + "Preprocess";
        public static final String MP3_PROCESSOR = MP3_BASEDIR + "Process";
        public static final String MP3_NEW = MP3_BASEDIR + "New";
        public static final String BASE_DIR = WinUtils.getOneDrivePath() + File.separator;
        public static final String BASE_CONFIG_DIR = BASE_DIR + "Config/Java";
        public static final String BASE_DATA_DIR = BASE_DIR + "Data/Java";
        public static final String BASE_LOCAL_CONFIG_DIR = BASE_CONFIG_DIR + File.separator + NetUtils.getHostName();
        public static final String VELOCITY_DIR = BASE_CONFIG_DIR + File.separator + "Velocity";
    }
}
