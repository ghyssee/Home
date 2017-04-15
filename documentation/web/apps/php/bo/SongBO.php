<?php
require_once("../setup.php");
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");
require_once "MyClasses.php";

$htmlFile = getFullPath(JSON_SONGCORRECTIONS);

class SongBO
{
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

    function checkArtistSongTestExist(AristSongTestTO $artistSongTest, array $artistSongArray) : bool {
        foreach($artistSongArray as $item){
            if ($artistSongTest->fileId == $item->fileId){
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
        $db = openDatabase(MEZZMO);
        $result = $db->getMezzmoSong($id);
        $songObj = $db->convertToSongUpdateObj($result);
        $songObj->source = "DB";
        $db = NULL;

        return $songObj;
    }

    function searchSong($songTO)
    {
        $db = openDatabase(MEZZMO);
        $result = $db->searchSong($songTO->artistId);
        $nr = count($result);
        $songs = array();
        if ($nr > 0) {
            foreach ($result as $songRec) {
                $songs[] = $db->convertToSongObj($songRec);
            }
        }
        $db = NULL;

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

}


?>
