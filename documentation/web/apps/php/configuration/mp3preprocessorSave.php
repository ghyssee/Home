<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
session_start();
if (isset($_POST['mp3Preprocessor'])) {
    $button = $_POST['mp3Preprocessor'];
    $file = $oneDrivePath . '/Config/Java/MP3Preprocessor.json';
    if ($button == "addSplitter") {
        addSplitter($file);
    }
    if ($button == "addConfig") {
        addConfig($file);
    }
}

function addSplitter($file)
{

    $mp3PreprocesorObj = initSave($file);
    $splitter = new Splitter();

    assignField($splitter->id, "splitterId");
    assignField($splitter->pattern, "pattern");
    $save = true;
    if (empty($splitter->id)) {
        addError('id', "Splitter Id can't be empty");
        $save = false;
    }
    if (empty($splitter->pattern)) {
        addError('pattern', "Pattern can't be empty");
        $save = false;
    } elseif (objectExist($mp3PreprocesorObj->splitters, "pattern", $splitter->pattern, false)) {
        addError('pattern', "Splitter already exist: " . $splitter->pattern);
        $save = false;
    }
    if ($save) {
        println("<h1>MP3Preprocessor Splitter</h1>");
        array_push($mp3PreprocesorObj->splitters, $splitter->pattern);
        println('Contents saved to ' . $file);
        //writeJSON($mp3PreprocesorObj, $file);
    } else {
        $_SESSION["splitter"] = $splitter;
        header("Location: " . $_SESSION["previous_location"]);
        exit();
    }
}

?>