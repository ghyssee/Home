<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "CacheBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
session_start();

header( 'Content-type: text/html; charset=utf-8' );
exportArtistGroup();
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
    //exportArtistGroup();

}

function exportArtistGroup(){
    printAndFlush("Exporting to file ");
    $file = getFullPath(PATH_CONFIG) . "/MultiArtistList.csv";
    printAndFlush("Exporting to file " . $file);
    $multiArtistBO = new MultiArtistBO();
    $multiArtistObj = $multiArtistBO->loadData();
    //$array = Array();
    $array = array();
    $counter = 0;
    foreach($multiArtistObj->list as $multiArtistItem){
        $counter++;
        if (($counter % 100) == 0){
            printAndFlush($counter . " record(s) exported");
        }
        $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($multiArtistItem);
        $array[] = $multiArtistTO;
    }
    printAndFlush("Sorting List");
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($array, "description2", "asc");

    $fp = fopen($file, 'w');
    foreach($array as $item){
        $list = array ($item->description2, $item->id);
        fputcsv($fp, $list, ';');
    }
    fclose($fp);
    printAndFlush("Exported to " .$file);
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
    writeJSONWithCode($obj, JSON_SONGCORRECTIONS);
}

function getCurrentDate(){
    $today = date("d/m/Y H:i:s");
    echo $today .'<br>';
    $today = date("Ymd");
    echo $today .'<br>';
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

?>
