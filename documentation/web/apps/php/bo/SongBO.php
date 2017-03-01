<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");

$htmlFile = getFullPath(JSON_SONGCORRECTIONS);

class SongBO
{
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
        $db = openDatabase();
        $result = $db->getMezzmoSong($id);
        $songObj = $db->convertToSongUpdateObj($result);
        $songObj->source = "DB";
        $db = NULL;

        return $songObj;
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
