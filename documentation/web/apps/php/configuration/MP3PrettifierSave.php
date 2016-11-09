<html>
<body>
<?php
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
session_start();

if(isset($_POST['mp3Prettifier'])){
	$file = $oneDrivePath . '/Config/Java/MP3Prettifier.json';
	$mp3PrettifierObj = initSave($file);
	$button = $_POST['mp3Prettifier'];
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
	println("<h1>Global Word</h1>");

	assignField($oldWord, "oldWord");
	assignField($newWord, "newWord");
	$save = true;
	if (empty($oldWord)) {
		addError ("oldWord", "Old Word can't be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->words, $oldWord)){
        addError ("oldWord", "Old Word " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new Word($oldWord, $newWord);
	if ($save) {
        array_push ($mp3PrettifierObj->words, $wordObj);
	}
    checkSave($save, 'mp3PrettifierGlobal', $mp3PrettifierObj, $file, $wordObj);
}

function saveArtistWord($file, $mp3PrettifierObj){
	println("<h1>Artist Word</h1>");

	assignField($oldWord, "artistOldWord");
	assignField($newWord, "artistNewWord");
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
	if ($save) {
		array_push ($mp3PrettifierObj->artist->words, $wordObj);
	}
    checkSave($save, 'mp3PrettifierArtistWord', $mp3PrettifierObj, $file, $wordObj);
}

function saveArtistName($file, $mp3PrettifierObj){
	println("<h1>Artist Name</h1>");

	assignField($oldWord, "artistNameOldWord");
	assignField($newWord, "artistNameNewWord");
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
	if ($save) {
		array_push ($mp3PrettifierObj->artist->names, $wordObj);
	}
    checkSave($save, 'mp3PrettifierArtistName', $mp3PrettifierObj, $file, $wordObj);
}

function saveSongTitle($file, $mp3PrettifierObj){
	println("<h1>Song Title</h1>");

	assignField($oldWord, "songTitleOldWord");
	assignField($newWord, "songTitleNewWord");
	$save = true;
	if (empty($oldWord)) {
		addError ("songTitleOldWord", "Old Song Title can not be empty");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->song->replacements, $oldWord)){
		addError ("songTitleOldWord", "Old Song Title " . $oldWord . " already exist");
		$save = false;
	}
    $wordObj = new Word($oldWord, $newWord);
	if ($save) {
		array_push ($mp3PrettifierObj->song->replacements, $wordObj);
	}
    checkSave($save, 'mp3PrettifierSongTitle', $mp3PrettifierObj, $file, $wordObj);
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
<div class="emptySpace"></div>

<script>
    document.write('<a href="' + document.referrer + '">Go Back</a>');
</script>
</body>
</html> 