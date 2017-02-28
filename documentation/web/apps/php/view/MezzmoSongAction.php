<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../html/config.php");
session_start();

$songObj = new SongCorrection();

assignField($songObj->fileId, "fileId");
assignField($songObj->track, "track");
assignField($songObj->title, "title");
assignField($songObj->artist, "artist");
$save = true;

if (empty($songObj->fileId)) {
    addError ('fileId', 'Mezzmo FileId is empty');
    $save = false;
}
if (empty($songObj->track)) {
    addError ('track', 'Mezzmo Track is empty');
    $save = false;
}
if (empty($songObj->title)) {
    addError ('title', 'Mezzmo Title is empty');
    $save = false;
}
if (empty($songObj->artist)) {
    addError ('artist', 'Mezzmo Artist is empty');
    $save = false;
}

if ($save) {
    //writeJSON($obj, $file);
    $songsObj = readJSONWithCode(JSON_SONGCORRECTIONS);
    $counter = 0;
    $updated = false;
    foreach ($songsObj->items as $key => $value) {
        if ($value->fileId == $songObj->fileId) {
            addInfo("SongInfo", "Updated Song");
            $songsObj->items[$counter] = $songObj;
            $updated = true;
            break;
        }
    }
    if (!$updated) {
        addInfo("SongInfo", "Added Song");
        array_push($songsObj->items, $songObj);
    }
    writeJSONWithCode($songsObj, JSON_SONGCORRECTIONS);
    header("Location: " . "MezzmoSongView.php");
}
else {
    $_SESSION["SONG"] = $songObj;
    header("Location: " . $_SESSION["previous_location"]);
}
exit();

?>