<?php
require_once ("utils/RandomStringGenerator.php");
$oneDrivePath = getOneDrivePath();

function println ($string_message) {
    echo "$string_message" . "<br>" . PHP_EOL;
}

function printh ($string_message) {
    echo "$string_message" . PHP_EOL;
}

function println_old ($string_message) {
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
   $field = htmlspecialchars($_POST[$value]);
}

function assignCheckbox(&$field, $value){
   $field = isset($_POST[$value]);
}

function write ($file, $text){
	file_put_contents($file, $text . PHP_EOL, FILE_APPEND | LOCK_EX);
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

function execInBackground($cmd) {
    if (substr(php_uname(), 0, 7) == "Windows"){
        pclose(popen('start /B "" "'. $cmd . '"', "r")); 
    }
    else {
        exec($cmd . " > /dev/null &");  
    }
}

function execInBackground2($cmd) {
    if (substr(php_uname(), 0, 7) == "Windows"){
		$handle = popen('start /B "" "'. $cmd . '"', "r");
		//echo "'$handle'; " . gettype($handle) . "\n";
		$read = fread($handle, 20960);
		println(nl2br($read));
		pclose($handle);
    }
    else {
        exec($cmd . " > /dev/null &");  
    }
}

function goMenu(){
	$menuObj = readJSON(getOneDrivePath() . '/Config/Java/Menu2.json');
	println('<a href="' . $menuObj->root->href . '">' . $menuObj->root->description . '</a>');
}

function getUrl(){
	$base_url = ( isset($_SERVER['HTTPS']) && $_SERVER['HTTPS']=='on' ? 'https' : 'http' ) . '://' .  $_SERVER['HTTP_HOST'];
	$url = $base_url . $_SERVER["REQUEST_URI"];
	return $url;
}

class Month {
//extends SplEnum {
    const __default = self::January;
    
    const January = 1;
    const February = 2;
    const March = 3;
    const April = 4;
    const May = 5;
    const June = 6;
    const July = 7;
    const August = 8;
    const September = 9;
    const October = 10;
    const November = 11;
    const December = 12;
}

function objectExist ($array, $field, $value, $caseSensitive = true, $idField = null, $idValue = null){

	foreach($array as $key => $obj) {

		if (!empty($idField)){
			if (strcmp($obj->{$idField}, $idValue) == 0){
				continue;
			}
		}
		if ($caseSensitive){
			if (strcmp($obj->{$field}, $value) == 0){
				return true;
			}
		}
		else {
			if (strcasecmp($obj->{$field}, $value) == 0){
				return true;
			}
		}
	}
	return false;
}
use Utils\RandomStringGenerator;

function getUniqueId(){

// Create new instance of generator class.
$generator = new RandomStringGenerator;

// Set token length.
$tokenLength = 32;

// Call method to generate random string.
$token = $generator->generate($tokenLength);
    return $token;
}


?>

