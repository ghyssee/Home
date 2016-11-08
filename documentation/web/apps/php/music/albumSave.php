<html>
<body>
<?php
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
session_start();
?>
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
if(isset($_POST['mp3Preprocessor'])){
	$button = $_POST['mp3Preprocessor'];
	if ($button == "save"){
		$file = $oneDrivePath . '/Config/Java/MP3Preprocessor.json';
		$mp3PreprocessorObj = initSave($file);
		saveMP3Preprocessor($file, $mp3PreprocessorObj);
	}
}

if(isset($_POST['mp3Settings'])){
	$file = $oneDrivePath . '/Config/Java/MP3Settings.json';
	$mp3SettingsObj = initSave($file);
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
	checkSave($save, 'mp3Preprocessor', $mp3PreprocessorObj, $file);
}

function saveAlbum($file, $mp3Settings){
	assignField($mp3Settings->album, "album");
	$save = true;
	if (empty($mp3Settings->album)) {
		addError('album', "Album is either empty, or not set at all");
		//printErrorMessage ('Album is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	checkSave($save, 'mp3Settings', $mp3Settings, $file);
}

function saveMezzmo($file, $mp3Settings){
	assignField($mp3Settings->mezzmo->base, "mezzmoBase");
	assignField($mp3Settings->mezzmo->importF->base, "importBase");
	assignField($mp3Settings->mezzmo->importF->filename, "filename");
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
	checkSave($save, 'mp3Settings', $mp3Settings, $file);
}

function saveiPod($file, $mp3Settings){
	assignCheckbox($mp3Settings->synchronizer->updateRating, "updateRating");
    checkSave(true, 'mp3Settings', $mp3Settings, $file);
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
    checkSave($save, 'mp3Settings', $mp3Settings, $file);
}

function saveLastPlayed($file, $mp3Settings){
	assignField($value, "number");
	assignField($mp3Settings->lastPlayedSong->scrollColor, "scrollColor");
	assignField($mp3Settings->lastPlayedSong->scrollBackgroundColor, "scrollBackgroundColor");
	$intNumber = intval($value);
	$save = true;
	if ($intNumber <= 0) {
		addError ('number', 'Number must be an integer and greater than 0');
		$save = false;
	}
	else {
		$mp3Settings->lastPlayedSong->number = $intNumber;
	}
    checkSave($save, 'mp3Settings', $mp3Settings, $file);
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

function checkSave($save, $key, $obj, $file, $returnObj)
{
	if ($save) {
		echo '<h1>Album Configuration Saved Status</h1>' . PHP_EOL;
		//writeJSON($obj, $file);
		println('Contents saved to ' . $file);
	} else {
		if (isset($returnObj)){
            $_SESSION[$key] = $returnObj;
        }
        else {
            $_SESSION[$key] = $obj;
        }
		header("Location: " . $_SESSION["previous_location"]);
		exit();
	}
}

?>
<div class="emptySpace"></div>

<script>
    document.write('<a href="' + document.referrer + '">Go Back</a>');
</script>
</body>
</html> 