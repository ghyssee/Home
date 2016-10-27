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
        public static final String BASEDIR = "mp3Processor";
        public static final String ALBUM = "album";
        public static final String TMP = "tmp";
        public static final String TMP_JAVA = "tmpjava";
        public static final String PREPROCESS = "preprocess";
        public static final String PROCESS = "process";
        public static final String NEW = "new";
        public static final String ONEDRIVE = "oneDrive";
        public static final String CONFIG = "config";
        public static final String DATA = "data";
        public static final String PLAYLIST = "playlist";
        public static final String LOCAL_CONFIG = "localConfig";
        public static final String VELOCITY = "velocity";
        public static final String RESOURCES = "resource";
        public static final String MAIN_LOG = "mainLog";
        public static final String LOG = "log";
        public static final String WEB = "web";
        public static final String WEB_MUSIC = "webMusic";
        public static final String WEB_MUSIC_ALBUMS = "webMusicAlbums";
        public static final String WEB_MUSIC_SONGS = "webMusicSongs";
    }
}
