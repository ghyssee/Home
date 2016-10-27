<html>
<body>
<?php
include("../config.php");
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
<h1>Album Configuration Saved Status</h1>

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
	if ($button == "SaveGlobalWord"){
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
		printErrorMessage ('AlbumTag is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	if (empty($mp3PreprocessorObj->cdTag)) {
		printErrorMessage ('cdTag is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3PreprocessorObj, $file);
		println ('Contents saved to ' . $file);
	}
}

function saveAlbum($file, $mp3Settings){
	assignField($mp3Settings->album, "album");
	$save = true;
	if (empty($mp3Settings->album)) {
		printErrorMessage ('Album is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3Settings, $file);
		println ('Contents saved to ' . $file);
	}
}

function saveMezzmo($file, $mp3Settings){
	assignField($mp3Settings->mezzmo->base, "mezzmoBase");
	$save = true;
	if (empty($mp3Settings->mezzmo->base)) {
		printErrorMessage ('Mezzmo Base Directory is either empty, or not set at all',  'errorMessage');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3Settings, $file);
		println ('Contents saved to ' . $file);
	}
}

function saveiPod($file, $mp3Settings){
	assignCheckbox($mp3Settings->synchronizer->updateRating, "updateRating");
	writeJSON($mp3Settings, $file);
	println ('Contents saved to ' . $file);
}

function saveMediaMonkey($file, $mp3Settings){
	assignField($mp3Settings->mediaMonkey->base, "mediaMonkeyBase");
	assignField($mp3Settings->mediaMonkey->playlist->path, "mediaMonkeyPlaylistPath");
	assignField($mp3Settings->mediaMonkey->playlist->top20, "mediaMonkeyTop20");
	
	$save = true;
	if (empty($mp3Settings->mediaMonkey->base)) {
		printErrorMessage ('MediaMonkey Base Directory is either empty, or not set at all', 'errorMessage');
		$save = false;
	}
	if (empty($mp3Settings->mediaMonkey->playlist->path)) {
		printErrorMessage ('MediaMonkey Playlist Directory is either empty, or not set at all', 'errorMessage');
		$save = false;
	}
	if (empty($mp3Settings->mediaMonkey->playlist->top20)) {
		printErrorMessage ('MediaMonkey Playlist Top 20 Name is either empty, or not set at all', 'errorMessage');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3Settings, $file);
		println ('Contents saved to ' . $file);
	}
}

function saveLastPlayed($file, $mp3Settings){
	assignField($value, "number");
	assignField($mp3Settings->lastPlayedSong->scrollColor, "scrollColor");
	assignField($mp3Settings->lastPlayedSong->scrollBackgroundColor, "scrollBackgroundColor");
	$intNumber = intval($value);
	$save = true;
	if ($intNumber <= 0) {
		printErrorMessage ('Number must be an integer and greater than 0',  'errorMessage');
		$save = false;
	}
	else {
		$mp3Settings->lastPlayedSong->number = $intNumber;
	}
	if ($save) {
		writeJSON($mp3Settings, $file);
		println ('Contents saved to ' . $file);
	}
}



function saveGlobalWord($file, $mp3PrettifierObj){
	println("<h1>Global Word</h1>");

	assignField($oldWord, "oldWord");
	assignField($newWord, "newWord");
	$save = true;
	if (empty($oldWord)) {
		printErrorMessage ("Old Word can not be empty", "errorMessage");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->words, $oldWord)){
		printErrorMessage ("Old Word " . $oldWord . " already exist", "errorMessage");
		$save = false;
	}
	if ($save) {
		$wordObj = getWordClass($oldWord, $newWord);
		array_push ($mp3PrettifierObj->words, $wordObj);
		println ('Contents saved to ' . $file);
		writeJSON($mp3PrettifierObj, $file);
	}
}

function saveArtistWord($file, $mp3PrettifierObj){
	println("<h1>Artist Word</h1>");

	assignField($oldWord, "artistOldWord");
	assignField($newWord, "artistNewWord");
	$save = true;
	if (empty($oldWord)) {
		printErrorMessage ("Old Word can not be empty", "errorMessage");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->artist->words, $oldWord)){
		printErrorMessage ("Old Word " . $oldWord . " already exist", "errorMessage");
		$save = false;
	}
	if ($save) {
		$wordObj = getWordClass($oldWord, $newWord);
		array_push ($mp3PrettifierObj->artist->words, $wordObj);
		println ('Contents saved to ' . $file);
		writeJSON($mp3PrettifierObj, $file);
	}
}

function saveArtistName($file, $mp3PrettifierObj){
	println("<h1>Artist Name</h1>");

	assignField($oldWord, "artistNameOldWord");
	assignField($newWord, "artistNameNewWord");
	$save = true;
	if (empty($oldWord)) {
		printErrorMessage ("Old Name can not be empty", "errorMessage");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->artist->names, $oldWord)){
		printErrorMessage ("Old Name " . $oldWord . " already exist", "errorMessage");
		$save = false;
	}
	if ($save) {
		$wordObj = getWordClass($oldWord, $newWord);
		array_push ($mp3PrettifierObj->artist->names, $wordObj);
		println ('Contents saved to ' . $file);
		writeJSON($mp3PrettifierObj, $file);
	}
}

function saveSongTitle($file, $mp3PrettifierObj){
	println("<h1>Song Title</h1>");

	assignField($oldWord, "songTitleOldWord");
	assignField($newWord, "songTitleNewWord");
	$save = true;
	if (empty($oldWord)) {
		printErrorMessage ("Old Song Title can not be empty", "errorMessage");
		$save = false;
	}
	if (wordExist($mp3PrettifierObj->song->replacements, $oldWord)){
		printErrorMessage ("Old Song Title " . $oldWord . " already exist", "errorMessage");
		$save = false;
	}
	if ($save) {
		$wordObj = getWordClass($oldWord, $newWord);
		array_push ($mp3PrettifierObj->song->replacements, $wordObj);
		println ('Contents saved to ' . $file);
		writeJSON($mp3PrettifierObj, $file);
	}
}

function getWordClass($oldWord, $newWord){
	$wordObj = new StdClass();
	$wordObj->oldWord = $oldWord;
	$wordObj->newWord = $newWord;
	return $wordObj;
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