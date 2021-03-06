<?php
include("../setup.php");
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
session_start();

if(isset($_POST['mp3Prettifier'])){
	$mp3PrettifierObj = initSave(JSON_MP3PRETTIFIER);
	$button = $_POST['mp3Prettifier'];
	$file = getFullPath(JSON_MP3PRETTIFIER);
	if ($button == "saveGlobalWord"){
		saveGlobalWord($file, $mp3PrettifierObj);
	}
	else if ($button == "saveArtistWord"){
		saveArtistWord($file, $mp3PrettifierObj);
	}
	else if ($button == "saveArtistName"){
		saveArtistName($file, $mp3PrettifierObj);
	}
	else if ($button == "saveSongTitle"){
		saveSongTitle($file, $mp3PrettifierObj);
	}
}

function saveGlobalWord($file, $mp3PrettifierObj){
	assignField($oldWord, "oldWord", !ESCAPE_HTML);
	assignField($newWord, "newWord", !ESCAPE_HTML);
	$save = true;
	if (empty($oldWord)) {
		addError ("oldWord", "Old Word can't be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->global->words, $oldWord)){
        addError ("oldWord", "Old Word " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new Word($oldWord, $newWord);
	$wordObj->id = getUniqueId();
	if ($save) {
        array_push ($mp3PrettifierObj->global->words, $wordObj);
	}
    checkSave2($save, 'mp3PrettifierGlobal', $mp3PrettifierObj, $file, $_SESSION['previous_location'], $wordObj);
}

function saveArtistWord($file, $mp3PrettifierObj){
	assignField($oldWord, "artistOldWord", !ESCAPE_HTML);
	assignField($newWord, "artistNewWord", !ESCAPE_HTML);
	$save = true;
	if (empty($oldWord)) {
        addError ("artistOldWord", "Old Word can not be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->artist->words, $oldWord)){
        addError ("artistOldWord", "Old Word " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new Word($oldWord, $newWord);
	$wordObj->id = getUniqueId();
	if ($save) {
		array_push ($mp3PrettifierObj->artist->words, $wordObj);
	}
    checkSave2($save, 'mp3PrettifierArtistWord', $mp3PrettifierObj, $file, $_SESSION['previous_location'], $wordObj);
}

function saveArtistName($file, $mp3PrettifierObj){
	assignField($oldWord, "artistNameOldWord", !ESCAPE_HTML);
	assignField($newWord, "artistNameNewWord", !ESCAPE_HTML);
	$save = true;
	if (empty($oldWord)) {
        addError ("artistNameOldWord", "Old Name can not be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->artist->names, $oldWord)){
        addError ("artistNameOldWord", "Old Name " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new Word($oldWord, $newWord);
	$wordObj->id = getUniqueId();
	if ($save) {
		array_push ($mp3PrettifierObj->artist->names, $wordObj);
	}
    checkSave2($save, 'mp3PrettifierArtistName', $mp3PrettifierObj, $file, $_SESSION['previous_location'], $wordObj);
}

function saveSongTitle($file, $mp3PrettifierObj){
	assignField($oldWord, "songTitleOldWord", !ESCAPE_HTML);
	assignField($newWord, "songTitleNewWord", !ESCAPE_HTML);
	assignCheckbox($parenthesis, "songTitleParenthesis");
	$save = true;
	if (empty($oldWord)) {
		addError ("songTitleOldWord", "Old Song Title can not be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->song->replacements, $oldWord)){
		addError ("songTitleOldWord", "Old Song Title " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new ExtWord($oldWord, $newWord, $parenthesis, false);
	$wordObj->id = getUniqueId();
	if ($save) {
		array_push ($mp3PrettifierObj->song->replacements, $wordObj);
	}
    checkSave2($save, 'mp3PrettifierSongTitle', $mp3PrettifierObj, $file, $_SESSION['previous_location'], $wordObj);
}

function wordExist($wordObj, $oldWord){

	foreach($wordObj as $key => $word) {
		if ($word->oldWord == $oldWord){
			return true;
		}
	}
	return false;
}

?>
