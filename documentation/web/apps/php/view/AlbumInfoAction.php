<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
session_start();
if (isset($_POST['albumInfo'])) {
    $button = $_POST['albumInfo'];
    if ($button == "save") {
        updateAlbumInfo();
    }
}

function updateAlbumInfo(){
    $mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
    $mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
    assignField($mp3PreprocessorObj->album, "album", !HTML_SPECIAL_CHAR);
    assignField($mp3SettingsObj->album, "albumLocation", !HTML_SPECIAL_CHAR);
    assignField($mp3SettingsObj->albumArtist, "albumArtist");
    assignField($mp3SettingsObj->albumYear, "albumYear");
    assignCheckbox($mp3SettingsObj->filename->renameEnabled, "filenameRenameEnabled");
    assignField($albumInfo, "albumContent", !HTML_SPECIAL_CHAR);
    assignField($mp3PreprocessorObj->cdTag, "cdTag");
    assignField($mp3PreprocessorObj->prefix, "prefix");
    assignField($mp3PreprocessorObj->suffix, "suffix");
    assignField($mp3PreprocessorObj->activeConfiguration, "activeConfiguration");
    assignCheckbox($mp3PreprocessorObj->renum, "renum");
    $save = true;
    if (empty($mp3PreprocessorObj->cdTag)) {
        addError ('cdTag', 'cdTag is either empty, or not set at all');
        $save = false;
    }
    if (empty($mp3PreprocessorObj->album)) {
        addError('album', "Album is either empty, or not set at all");
        $save = false;
    }
    if (empty($mp3SettingsObj->album)) {
        addError('albumLocation', "Album Location is either empty, or not set at all");
        $save = false;
    }
    if ($save){
        $file = getFullPath(FILE_ALBUM);
        write ($file, $albumInfo, !APPEND);
        writeJSONWithCode($mp3PreprocessorObj, JSON_MP3PREPROCESSOR);
        writeJSONWithCode($mp3SettingsObj, JSON_MP3SETTINGS);
        addInfo("album", "Contents saved to '" . $file);
        addInfo("mp3Preprocessor", "Contents saved to '" . getFullPath(JSON_MP3PREPROCESSOR));
        addInfo("mp3Settings", "Contents saved to '" . getFullPath(JSON_MP3SETTINGS));
    }
    checkForErrors($save, "mp3Preprocessor", $mp3PreprocessorObj);
    checkForErrors($save, "mp3Settings", $mp3SettingsObj);
    header("Location: " . $_SESSION["previous_location"]);
    exit();

}

?>