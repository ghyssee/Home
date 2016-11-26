<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
session_start();
if (isset($_POST['albumInfo'])) {
    $button = $_POST['albumInfo'];
    if ($button == "save") {
        $mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
        updateAlbumInfo($mp3PreprocessorObj);
    }
}

function updateAlbumInfo($mp3PreprocessorObj){
    assignField($mp3PreprocessorObj->album, "album");
    assignField($albumInfo, "albumContent", !HTML_SPECIAL_CHAR);
    assignField($mp3PreprocessorObj->cdTag, "cdTag");
    assignField($mp3PreprocessorObj->prefix, "prefix");
    assignField($mp3PreprocessorObj->suffix, "suffix");
    assignField($mp3PreprocessorObj->activeConfiguration, "activeConfiguration");
    $save = true;
    if (empty($mp3PreprocessorObj->cdTag)) {
        addError ('cdTag', 'cdTag is either empty, or not set at all');
        $save = false;
    }
    if (empty($mp3PreprocessorObj->album)) {
        addError('album', "Album is either empty, or not set at all");
        $save = false;
    }
    if ($save){
        $file = getFullPath(FILE_ALBUM);
        write ($file, $albumInfo, !APPEND);
        writeJSONWithCode($mp3PreprocessorObj, JSON_MP3PREPROCESSOR);
        addInfo("album", "Contents saved to '" . $file);
        addInfo("mp3Preprocessor", "Contents saved to '" . getFullPath(JSON_MP3PREPROCESSOR));
    }
    checkForErrors($save, "mp3Preprocessor", $mp3PreprocessorObj);
    header("Location: " . $_SESSION["previous_location"]);
    exit();

}

?>