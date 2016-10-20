<?php
$oneDrivePath = getOneDrivePath();

function println ($string_message) {
    $_SERVER['SERVER_PROTOCOL'] ? print "$string_message<br />" : print "$string_message\n";
}

function printErrorMessage ($string_message, $class) {
    $msg = '<span class="' . $class . '">' . $string_message . "</span>";
	println($msg);
}

function readJSON($file){
	$str = file_get_contents($file);
	return json_decode($str);
}

function writeJSON($json, $file){
	file_put_contents($file, json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES));
}

function assignField(&$field, $value){
   $field = $_POST[$value];
}

function assignCheckbox(&$field, $value){
   $field = isset($_POST[$value]);
}

function initSave($file){
	$obj = readJSON($file);
	print "<pre>";
	var_dump($_POST); // write your code here as you would 
	print "</pre>";
	return $obj;
}

function setCheckBox($value){
	if ($value) {
		echo " checked";
	}
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

function execInBackground($cmd) {
    if (substr(php_uname(), 0, 7) == "Windows"){
        pclose(popen('start /B "" "'. $cmd . '"', "r")); 
    }
    else {
        exec($cmd . " > /dev/null &");  
    }
}

?>

