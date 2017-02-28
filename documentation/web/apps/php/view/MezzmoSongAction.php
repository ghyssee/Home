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
    $_SESSION["SONG"] = $songObj;
    addInfo("SongInfo", "Added Song");
    header("Location: " . "MezzmoSongView.php");
}
else {
    $_SESSION["SONG"] = $songObj;
    header("Location: " . $_SESSION["previous_location"]);
}
exit();

?>