<?php
$str = file_get_contents('/My Programs/OneDrive/Config/Java/MP3Settings.json');
$json = json_decode($str);

function println ($string_message) {
    $_SERVER['SERVER_PROTOCOL'] ? print "$string_message<br />" : print "$string_message\n";
}

function getOneDrivePath() {
	$Wshshell= new COM('WScript.Shell');
	try {
		$oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\OneDrive\\UserFolder');
	} catch (Exception $e) {
		$oneDrive= $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\SkyDrive\\UserFolder');
	}
	return $oneDrive;
}
?>

