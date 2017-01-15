<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
session_start();
if (isset($_POST['albumCheck'])) {
	$button = $_POST['albumCheck'];
	if ($button == "save") {
		updateAlbumInfo();
	}
}

function updateAlbumInfo($mp3PreprocessorObj){
//	$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
	assignField($albumList, "albumList", !HTML_SPECIAL_CHAR);
	assignField($albumExclude, "albumExclude", !HTML_SPECIAL_CHAR);
	$save = true;
	if ($save){
		$file = getFullPath(FILE_ALBUMCHECK);
		write ($file, $albumList, !APPEND);
		//writeJSONWithCode($mp3SettingsObj, JSON_MP3SETTINGS);
		addInfo("albumInclude", "Contents saved to '" . $file);
		$file = getFullPath(FILE_ALBUMEXCLUDE);
		write ($file, $albumExclude, !APPEND);
		//writeJSONWithCode($mp3SettingsObj, JSON_MP3SETTINGS);
		addInfo("albumExclude", "Contents saved to '" . $file);
	}
	header("Location: " . $_SESSION["previous_location"]);
	exit();
}

?>