<?php
require_once("../setup.php");
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");
require_once "MyClasses.php";

$htmlFile = getFullPath(JSON_SONGCORRECTIONS);

class SongBO
{
    public $db;
    function __construct() {
        //$this->db = new MezzmoSQLiteDatabase(MEZZMOV2);
    }

    public static function defaultDB( ) {
        $instance = new self();
        $instance->setDB( MEZZMOV2 );
        return $instance;
    }

    public static function db( $id ) {
        $instance = new self();
        $instance->setDB( $id );
        return $instance;
    }

    function setDB($id){
        $this->db = new MezzmoSQLiteDatabase($id);
    }


    function loadArtistIdsFile() : array {
        $file = getFullPath(PATH_CONFIG) . '/ArtistIds.txt';
        $lines = file($file, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
        $artistArray = array();
        foreach($lines as $line) {
            $songTO = new SongTO();
            $items = explode("\t", $line);
            if (count($items) > 1) {
                $songTO->artistId = $items[0];
                $songTO->artist = $items[1];
                $artistArray[] = $songTO;
            }
        }
        return $artistArray;
    }

    function saveArtistSongTest($songCorrections) {
        $file = readJSONWithCode(JSON_ARTISTSONGTEST);
        $save = false;
        foreach($songCorrections as $songCorrection){
            $artistSongTest = new AristSongTestTO();
            $artistSongTest->fileId = $songCorrection->fileId;
            $artistSongTest->oldArtist = $songCorrection->artist;
            $artistSongTest->oldSong = $songCorrection->title;
            if (!$this->checkArtistSongTestExist($artistSongTest, $file->items)){
                $file->items[] = $artistSongTest;
                $save = true;
            }
        }
        if ($save){
            writeJSONWithCode($file, JSON_ARTISTSONGTEST);
        }

    }

    function saveArtistSongTestItem(AristSongTestTO $artistSongTestTO) {
        $file = readJSONWithCode(JSON_ARTISTSONGTEST);
        $file->items[] = $artistSongTestTO;
        writeJSONWithCode($file, JSON_ARTISTSONGTEST);
    }

    function checkArtistSongTestExist(AristSongTestTO $artistSongTest, array $artistSongArray) : bool {
        foreach($artistSongArray as $item){
            if (isset($artistSongTest->fileId) && isset($item->fileId) && $artistSongTest->fileId == $item->fileId){
                return true;
            }
        }
        return false;
    }

    function lookupSong($id)
    {
        $file = $GLOBALS['htmlFile'];
        $obj = readJSON($file);
        foreach ($obj->items as $key => $value) {
            if (strcmp($value->fileId, $id) == 0) {
                $song = $value;
                $song->source = "JSON";
                return $song;
            }
        }
        // song not found in JSON file, look up in DB
        //$db = openDatabase(MEZZMOV2);
        $result = $this->db->getMezzmoSong($id);
        $songObj = $this->db->convertToSongUpdateObj($result);
        $songObj->source = "DB";
        //$db = NULL;

        return $songObj;
    }

    function searchSong($songTO)
    {
        //$db = openDatabase(MEZZMOV2);
        $result = $this->db->searchSong($songTO);
        $nr = count($result);
        $songs = array();
        $ericBO = new EricBO();
        if ($nr > 0) {
            foreach ($result as $songRec) {
                $song = $this->db->convertToSongObj($songRec);
                $songs[] = $song;
                $mezzmoFileTO = $ericBO->findMezzmoFileById($song->fileId);
                if ($mezzmoFileTO != null){
                    $song->status = $mezzmoFileTO->status;
                    $song->isNew = $mezzmoFileTO->isNew;
                    $song->ruleId = $mezzmoFileTO->ruleId;
                    $song->newArtist = $mezzmoFileTO->newArtist;
                    $song->newTitle = $mezzmoFileTO->newTitle;
                }
            }
        }
        //$db = NULL;

        return $songs;
    }

    function saveSong($song)
    {
        $counter = 0;
        $updated = false;
        $file = $GLOBALS['htmlFile'];
        $songsObj = readJSON($file);
        foreach ($songsObj->items as $key => $value) {
            if ($value->fileId == $song->fileId) {
                $songsObj->items[$counter] = $song;
                $updated = true;
                break;
            }
        }
        if (!$updated) {
            array_push($songsObj->items, $song);
        }
        writeJSONWithCode($songsObj, JSON_SONGCORRECTIONS);
    }

    function testSong($id)
    {
        $result = $this->db->testSong($id);
        $songObj = $this->db->convertToMGOFileObj($result);
        $songObj->source = "DB";
        return $songObj;
    }

    function findLatestVersion()
    {
        $result = $this->db->findLatestVersion();
        $version = $this->db->convertToVersionTO($result);
        $version->dbName = $this->db->dbName;

        return $version;
    }



}



?>
