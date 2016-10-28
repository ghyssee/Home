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
<h1>Save Settings</h1>

<?php
if(isset($_POST['htmlSettings'])){
	$button = $_POST['htmlSettings'];
	if ($button == "addColor"){
		$file = $oneDrivePath . '/Config/Java/HTML.json';
		//saveMP3Preprocessor($file, $mp3PreprocessorObj);
		addColor($file);
	}
}

function addColor($file){

	println("<h1>HTML Color</h1>");
	$htmlObj = initSave($file);

	assignField($description, "colorDescription");
	assignField($code, "colorCode");
	$save = true;
	if (empty($description)) {
		printErrorMessage ("Color Description can't be empty", "errorMessage");
		$save = false;
	}
	if (objectExist($htmlObj->colors, "code", $code, false)){
		printErrorMessage ("Color Code already exist: " . $code, "errorMessage");
		$save = false;
	}
	if ($save) {
		//$wordObj = getWordClass($oldWord, $newWord);
		//array_push ($mp3PrettifierObj->words, $wordObj);
		//println ('Contents saved to ' . $file);
		//writeJSON($mp3PrettifierObj, $file);
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


function objectExist ($array, $field, $value, $caseSensitive = true){

	foreach($array as $key => $obj) {
		if ($caseSensitive){
			return strcmp($obj->{$field}, $value) == 0;
		}
		else {
			return strcasecmp($obj->{$field}, $value) == 0;
		}
		//if ($obj->{$field} == $value){
			//return true;
		//}
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