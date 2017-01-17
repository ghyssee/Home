<?php
include("../setup.php");
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
convert();

function convert()
{
    $mp3PrettifierObj = readJSONWithCode(JSON_MP3PRETTIFIER);
    $mp3PrettifierObj->words = convertWords($mp3PrettifierObj->words);
    $mp3PrettifierObj->artist->words = convertWords($mp3PrettifierObj->artist->words);
    $mp3PrettifierObj->artist->words = convertWords($mp3PrettifierObj->artist->words);
    $mp3PrettifierObj->artist->names = convertWords($mp3PrettifierObj->artist->names);
    $mp3PrettifierObj->song->replacements = convertWords($mp3PrettifierObj->song->replacements);

    $file = getFullPath(JSON_MP3PRETTIFIER) . ".NEW";
    writeJSON($mp3PrettifierObj, $file);
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

}?>
