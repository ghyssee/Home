<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "CacheBO.php");
session_start();
CacheBO::clearCache(CacheBO::MULTIARTIST2);
//purgeSongCorrections();

function convert()
{
    //$mp3PrettifierObj = readJSONWithCode(JSON_MP3PRETTIFIER);
    //$mp3PrettifierObj->words = convertWords($mp3PrettifierObj->words);
    //$mp3PrettifierObj->artist->words = convertWords($mp3PrettifierObj->artist->words);
    //$mp3PrettifierObj->artist->words = convertWords($mp3PrettifierObj->artist->words);
    //$mp3PrettifierObj->artist->names = convertWords($mp3PrettifierObj->artist->names);
    //$mp3PrettifierObj->song->replacements = convertWords($mp3PrettifierObj->song->replacements);
    //$mp3PrettifierObj->artist->names = convertExactMath($mp3PrettifierObj->artist->names);
    //$mp3PrettifierObj->song->replacements = convertExactMath($mp3PrettifierObj->song->replacements);
    //$mp3PrettifierObj->Global->words = convertExactMath($mp3PrettifierObj->Global->words, true);
    //$mp3PrettifierObj->artist->words = convertExactMath($mp3PrettifierObj->artist->words, true);
    //$multiArtistObj = readJSONWithCode(JSON_MULTIARTIST);

    //$file = getFullPath(JSON_MULTIARTIST) . ".NEW";
    //convertArtistSequence($multiArtistObj);
    //writeJSON($multiArtistObj, $file);
}

function purgeSongCorrections(){
    $obj = readJSONWithCode(JSON_SONGCORRECTIONS);
    foreach ($obj->items as $key => $song){
        if (isset($song->done) && $song->done){
            echo "remove " . $song->fileId . "<br>";
            unset( $obj->items[$key]);
        }
    }
    $obj->items = array_values($obj->items);
    writeJSON($obj, getFullPath(JSON_SONGCORRECTIONS) . ".NEW");
}

function getCurrentDate(){
    $today = date("d/m/Y H:i:s");
    echo $today .'<br>';
    $today = date("Ymd");
    echo $today .'<br>';
}

function convertArtistSequence($multiArtistObj){
    foreach($multiArtistObj->list as $record){
        echo "Record Id: " . $record->id . "<br>";
        $record->master = "artistSequence";
    }
}

function convertExactMath($objects, $exactMatch = false){
    $array = [];
    foreach($objects as $key => $word) {
        $wordObj = new ExtWord();
        $wordObj->id = $word->id;
        $wordObj->oldWord = $word->oldWord;
        $wordObj->newWord = $word->newWord;
        if (isset($word->parenthesis)) {
            $wordObj->parenthesis = $word->parenthesis;
        }
        else {
            $wordObj->parenthesis = false;
        }
        $wordObj->exactMatch = $exactMatch;
        array_push($array, $wordObj);
    }
    return $array;

}

function convertWords($objects){
    $array = [];
    foreach($objects as $key => $word) {
        if (isset($word->parenthesis)) {
            $wordObj = new ExtWord($word->oldWord, $word->newWord, $word->parenthesis);
        }
        else {
            $wordObj = new Word($word->oldWord, $word->newWord);
        }
        $wordObj->id = getUniqueId();
        array_push($array, $wordObj);
    }
    return $array;

}
?>
