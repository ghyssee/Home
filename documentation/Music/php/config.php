<?php
$oneDrivePath = getOneDrivePath();

function println ($string_message) {
    $_SERVER['SERVER_PROTOCOL'] ? print "$string_message<br />" : print "$string_message\n";
}

function readJSON($file){
	$str = file_get_contents($file);
	return json_decode($str);
}

function writeJSON($json, $file){
	file_put_contents($file . ".NEW", json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES));
}

function assignField(&$field, $value){
   $field = $_POST[$value];
}

function initSave($file){
	$obj = readJSON($file);
	print "<pre>";
	var_dump($_POST); // write your code here as you would 
	print "</pre>";
	return $obj;
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

