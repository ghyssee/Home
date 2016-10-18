<html>
<body>
<?php
include("config.php");
?>
<style>
.emptySpace {
	height:100px
}
</style>
<h1>Album Configuration Saved Status</h1>

<?php
if(isset($_POST['mp3Preprocessor'])){
		
	$file = $oneDrivePath . '/Config/Java/MP3Preprocessor.json';
	$mp3PreprocessorObj = initSave($file);

	assignField($mp3PreprocessorObj->albumTag, "albumTag");
	assignField($mp3PreprocessorObj->cdTag, "cdTag");
	assignField($mp3PreprocessorObj->prefix, "prefix");
	assignField($mp3PreprocessorObj->suffix, "suffix");
	assignField($mp3PreprocessorObj->activeConfiguration, "activeConfiguration");
	$save = true;
	if (empty($mp3PreprocessorObj->albumTag)) {
		println ('AlbumTag is either empty, or not set at all');
		$save = false;
	}
	if (empty($mp3PreprocessorObj->cdTag)) {
		println ('cdTag is either empty, or not set at all');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3PreprocessorObj, $file);
		println ('Contents saved to ' . $file);
	}
}

if(isset($_POST['mp3Settings'])){
	$file = $oneDrivePath . '/Config/Java/MP3Settings.json';
	$mp3SettingsObj = initSave($file);

	assignField($mp3SettingsObj->album, "album");
	$save = true;
	if (empty($mp3SettingsObj->album)) {
		println ('Album is either empty, or not set at all');
		$save = false;
	}
	if ($save) {
		writeJSON($mp3SettingsObj, $file);
		println ('Contents saved to ' . $file);
	}
}
?>
<div class="emptySpace"></div>

<a href="album.php">Config Album</a>
</body>
</html> 