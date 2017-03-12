package be.home.common.constants;

import be.home.common.utils.NetUtils;
import be.home.common.utils.WinUtils;

import java.io.File;
import java.nio.file.FileSystem;

/**
 * Created by ghyssee on 28/09/2015.
 */
public interface Constants {

    interface Movies {

        enum Import {
            ID(0), TITLE(1), ALTERNATE_TITLE(2), YEAR(3), GENRE(4), STOCKPLACE(5), IMDB_ID(6);
            private int value;

            Import(int value) {
                this.value = value;
            }

            public int getValue() {
                return this.value;
            }
        }

        enum Status {
            NFO, SOURCE, DESTINATION, ERROR_NFO, DIFFERENT_TITLE, EXIST_NFO;
        }
    }

    interface AlbumArtist {
        String name = "Various Artists";
    }

    interface Path {
        String CONTEXTROOT = "contextRoot";
        String BASEDIR = "mp3Processor";
        String ALBUM = "album";
        String TMP = "tmp";
        String TMP_JAVA = "tmpjava";
        String PREPROCESS = "preprocess";
        String PROCESS = "process";
        String NEW = "new";
        String ONEDRIVE = "oneDrive";
        String MAIN_CONFIG = "mainConfig";
        String CONFIG = "config";
        String DATA = "data";
        String PLAYLIST = "playlist";
        String LOCAL_CONFIG = "localConfig";
        String VELOCITY = "velocity";
        String RESOURCES = "resource";
        String MAIN_LOG = "mainLog";
        String LOG = "log";
        String WEB = "web";
        String WEB_MUSIC = "webMusic";
        String WEB_MUSIC_ALBUMS = "webMusicAlbums";
        String WEB_MUSIC_SONGS = "webMusicSongs";
        String ULTRATOP = "ultratopBase";
        String IPOD = "iPod";
        String MUSIC_LISTS = "musicList";
    }

    interface JSON {
        String HTML = "htmlConfig";
        String MP3PREPROCESSOR = "mp3PreprocessorConfig";
        String MP3SETTINGS = "mp3SettingsConfig";
        String MP3PRETTIFIER = "mp3PrettifierConfig";
        String PLAYLIST = "playlistConfig";
        String PLAYLISTSETUP = "playlistSetup";
        String SONGCORRECTIONS = "songCorrections";
        String ALBUMERRORS = "albumErrors";
        String ULTRATOP = "ultratop";
        String BACKUP = "backup";
        String ARTISTS = "artists";
        String MULTIARTISTCONFiG = "multiArtistConfig";
    }
    interface FILE {
        String ALBUM = "albumInfo";
        String ALBUM_LOG = "albumLog";
        String ALBUMS_TO_CHECK = "albumsToCheck";
        String ALBUMS_TO_EXCLUDE = "albumsToExclude";
        String ALBUMS_WITHOUT_ERRORS = "albumsWithoutErrors";
        String MUSIC_INDEX = "musicIndex";

    }

}
