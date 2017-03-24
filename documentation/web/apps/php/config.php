<?php
require_once ("utils/RandomStringGenerator.php");
require_once ("setup.php");
const HTML_SPECIAL_CHAR = true;

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

function getSessionField($field)
{
	$var = $_SESSION[$field];
	unset($_SESSION[$field]);
	return $var;
}

function assignField(&$field, $value, $isHtml = true)
{
	if ($isHtml) {
		$field = htmlspecialchars($_POST[$value]);
	} else {
		$field = $_POST[$value];
	}
}

function assignNumber(&$field, $value, $isHtml = true)
{
	assignField($field, $value, $isHtml);
    $field = intval($field);
}

function assignCheckbox(&$field, $value){
   $field = isset($_POST[$value]);
}

function initSave($file){
	$obj = readJSONWithCode($file);
	print "<pre>";
	var_dump($_POST); // write your code here as you would 
	print "</pre>";
	return $obj;
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
	$menuObj = readJSONWithCode(JSON_MENU);
	printh('<div class="centered">');
	printh('<a href="' . $menuObj->root->href . '">' . $menuObj->root->description . '</a>');
	printh('</div>');
}

function showUrl($url, $message){
	printh('<div class="centered">');
	printh('<a href="' . $url . '">' . $message . '</a>');
	printh('</div>');
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

class CustomSort{

	public $field = '';

	public function ascCmp($a, $b)
	{
		if (is_numeric($a->{$this->field})){
			return $a->{$this->field} - $b->{$this->field};
		}
		else {
			return strcmp($a->{$this->field}, $b->{$this->field});
		}
	}

    public function descCmp($a, $b)
    {
		if (is_numeric($a->{$this->field})){
			return $b->{$this->field} - $a->{$this->field};
		}
        else {
			return strcmp($b->{$this->field}, $a->{$this->field});
		}
    }

    public function sortObjectArrayByField($array, $field, $order = 'asc')
	{
		$this->field = $field;
        if ($order == 'desc') {
            usort($array, array("CustomSort", "descCmp"));
        }
        else {
            usort($array, array("CustomSort", "ascCmp"));
        }
		return $array;
	}
}

function nullIfEmpty($field){
	return (empty($field) ? null : $field);
}


?>

