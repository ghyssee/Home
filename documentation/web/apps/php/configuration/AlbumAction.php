<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
session_start();
?>
<html>
<body>
<style>
.emptySpace {
	height:100px
}
.errorMessage {
   color: red;
   font-weight: bolder;
   font-size: 20px;
   
}
</style>

<?php
/*
if(isset($_POST['mp3Preprocessor'])){
	$button = $_POST['mp3Preprocessor'];
	if ($button == "save"){
		$mp3PreprocessorObj = initSave(JSON_MP3PREPROCESSOR);
		saveMP3Preprocessor(getFullPath(JSON_MP3PREPROCESSOR), $mp3PreprocessorObj);
	}
}*/
const ALBUM_VIEW = "AlbumView.php";

if(isset($_POST['mp3Settings'])){
	$file = getFullPath(JSON_MP3SETTINGS);
	$mp3SettingsObj = initSave(JSON_MP3SETTINGS);
	$button = $_POST['mp3Settings'];
	if ($button == "saveAlbum"){
		saveAlbum($file, $mp3SettingsObj);
	}
	else if ($button == "saveMezzmo"){
		saveMezzmo($file, $mp3SettingsObj);
	}
	else if ($button == "saveiPod"){
		saveiPod($file, $mp3SettingsObj);
	}
	else if ($button == "saveMM"){
		saveMediaMonkey($file, $mp3SettingsObj);
	}
	else if ($button == "saveLastPlayed"){
		saveLastPlayed($file, $mp3SettingsObj);
	}
}

/*
function saveMP3Preprocessor($file, $mp3PreprocessorObj){
	assignField($mp3PreprocessorObj->albumTag, "albumTag");
	assignField($mp3PreprocessorObj->cdTag, "cdTag");
	assignField($mp3PreprocessorObj->prefix, "prefix");
	assignField($mp3PreprocessorObj->suffix, "suffix");
	assignField($mp3PreprocessorObj->activeConfiguration, "activeConfiguration");
	$save = true;
	if (empty($mp3PreprocessorObj->albumTag)) {
		addError ('albumTag', 'AlbumTag is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3PreprocessorObj->cdTag)) {
		addError ('cdTag', 'cdTag is either empty, or not set at all');
		$save = false;
	}
	checkSave2($save, 'mp3Preprocessor', $mp3PreprocessorObj, $file, 'AlbumInformation.php');
}*/

function saveAlbum($file, $mp3Settings){
	assignField($mp3Settings->album, "album");
	$save = true;
	if (empty($mp3Settings->album)) {
		addError('album', "Album is either empty, or not set at all");
		//printErrorMessage ('Album is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	checkSave2($save, 'mp3Settings', $mp3Settings, $file, ALBUM_VIEW);
}

function saveMezzmo($file, $mp3Settings){
	assignField($mp3Settings->mezzmo->base, "mezzmoBase");
	assignCheckbox($mp3Settings->mezzmo->synchronizePlaycount, "synchronizePlaycount");
	assignField($mp3Settings->mezzmo->importF->base, "importBase");
	assignField($mp3Settings->mezzmo->importF->filename, "filename");
	assignField($mp3Settings->mezzmo->export->base, "exportBase");
	assignField($mp3Settings->mezzmo->export->iPod, "exportiPod");
	assignField($mp3Settings->mezzmo->mp3Checker->currentRelativePath, "currentRelativePath");
    assignField($mp3Settings->mezzmo->version, "version");
	$save = true;
	if (empty($mp3Settings->mezzmo->base)) {
		addError ('mezzmoBase', 'Mezzmo Base Directory is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mezzmo->importF->base)) {
		addError ('importBase', 'Mezzmo Import Base Directory is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mezzmo->importF->filename)) {
		addError ('filename', 'Mezzmo Import Filename is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mezzmo->export->base)) {
		addError ('exportBase', 'Mezzmo Export Base Directory is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mezzmo->export->iPod)) {
		addError ('exportiPod', 'Mezzmo Export iPod Directory is either empty, or not set at all');
		$save = false;
	}
    if (empty($mp3Settings->mezzmo->version)) {
        addError ('version', 'Mezzmo Version is either empty, or not set at all');
        $save = false;
    }
	checkSave2($save, 'mp3Settings', $mp3Settings, $file, ALBUM_VIEW);
}

function saveiPod($file, $mp3Settings){
	assignField($mp3Settings->synchronizer->startDirectory, "synchronizerStartDirectory");
	assignCheckbox($mp3Settings->synchronizer->updateRating, "updateRating");
    checkSave2(true, 'mp3Settings', $mp3Settings, $file, 'album.php');
}

function saveMediaMonkey($file, $mp3Settings){
	assignField($mp3Settings->mediaMonkey->base, "mediaMonkeyBase");
	assignField($mp3Settings->mediaMonkey->playlist->path, "mediaMonkeyPlaylistPath");
	assignField($mp3Settings->mediaMonkey->playlist->top20, "mediaMonkeyTop20");
	
	$save = true;
	if (empty($mp3Settings->mediaMonkey->base)) {
		addError ('mediaMonkeyBase', 'MediaMonkey Base Directory is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mediaMonkey->playlist->path)) {
		addError ('mediaMonkeyPlaylistPath', 'MediaMonkey Playlist Directory is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3Settings->mediaMonkey->playlist->top20)) {
		addError ('mediaMonkeyTop20', 'MediaMonkey Playlist Top 20 Name is either empty, or not set at all');
		$save = false;
	}
    checkSave2($save, 'mp3Settings', $mp3Settings, $file, ALBUM_VIEW);
}

function saveLastPlayed($file, $mp3Settings){
	assignField($value, "number");
	assignField($mp3Settings->lastPlayedSong->scrollColor, "scrollColor");
	assignField($mp3Settings->lastPlayedSong->scrollBackgroundColor, "scrollBackgroundColor");
	assignCheckbox($mp3Settings->lastPlayedSong->scrollShowAlbum, "scrollShowAlbum");
	$intNumber = intval($value);
	$save = true;
	if ($intNumber <= 0) {
		addError ('number', 'Number must be an integer and greater than 0');
		$save = false;
	}
	else {
		$mp3Settings->lastPlayedSong->number = $intNumber;
	}
    checkSave2($save, 'mp3Settings', $mp3Settings, $file, ALBUM_VIEW);
}

?>
<div class="emptySpace"></div>

<script>
    document.write('<a href="' + document.referrer + '">Go Back</a>');
</script>
</body>
</html> 