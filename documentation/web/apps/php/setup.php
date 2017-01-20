<?php

include_once('Smarty.class.php');

const PATH_CONFIG = "config";
const JSON_MP3SETTINGS = "mp3SettingsConfig";
const JSON_MP3PREPROCESSOR = "mp3PreprocessorConfig";
const JSON_MP3PRETTIFIER = "mp3PrettifierConfig";
const JSON_HTML = "htmlConfig";
const JSON_JOBS = "jobs";
const JSON_MENU = "menu";
const JSON_ALBUMERRORS = "albumErrors";
const FILE_ALBUM = "albumInfo";
const FILE_ALBUMCHECK = "albumsToCheck";
const FILE_ALBUMEXCLUDE = "albumsToExclude";
const APPEND = true;
const ESCAPE_HTML = true;
const JSON_ASSOCIATIVE = true;

function initSetup(){
    if (isset($GLOBALS["SETUP"])){
        return $GLOBALS["SETUP"];
    }
    else {
        $oneDrive = getOneDrivePath();
        // main JSON File => can't use getFullPath yet
        $setupObj = readJSON($oneDrive . '/Config/Setup.json');
        //$mp3Settings = readJSONWithCode(JSON_MP3SETTINGS);
        //$setupObj->globalSettings->numberOfSongs = $mp3Settings->
        $GLOBALS["SETUP"] = $setupObj;
        return $setupObj;
    }
}

function getFullPath($pathId)
{
    $setupObj = initSetup();
    $parent = true;
    $path = "";
    $id = $pathId;
    $first = true;
    do {
        if (isset($setupObj->{$id}->path)) {
            $path = $setupObj->{$id}->path . ($first ? '' : '/') . $path;
            if (empty($setupObj->{$id}->parent)) {
                //echo "parent is empty" . "<br>";
                $parent = false;
                break;
            } else {
                $id = $setupObj->{$id}->parent;
                //echo "parent found: " . $id . "<br>";
            }
        } else {
            throw new Exception("Path With Id Not Found: " . $id);
        }
        $first = false;
    } while ($parent);
    return replaceSystemVariables($path);

}


function replaceSystemVariables($string){
    $string = str_replace("%ONEDRIVE%", getOneDrivePath(), $string );
    return $string;
}


function getOneDrivePath() {
    if (isset($GLOBALS["ONEDRIVE"])){
        return $GLOBALS["ONEDRIVE"];
    }
    else {
        $Wshshell = new COM('WScript.Shell');
        try {
            $oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\OneDrive\\UserFolder');
        } catch (Exception $e) {
            $oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\SkyDrive\\UserFolder');
        }
        $GLOBALS["ONEDRIVE"] = $oneDrive;
        return $oneDrive;
    }
}

function readJSON($file, $associative = false){
    if (file_exists ($file)){
        $str = file_get_contents($file);
        return json_decode($str, $associative);
    }
    else {
        throw new Exception("File Not Found:" . $file);
    }
}


function readJSONWithCode($code, $associative = false){
    $file = getFullPath($code);
    return readJSON($file, $associative);
}



function writeJSONWithCode($json, $code){
    $file = getFullPath($code);
    writeJSON($json, $file);
}

function writeJSON($json, $file){
    file_put_contents($file, json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES));
}


function write ($file, $text, $append = APPEND){
    file_put_contents($file, $text . PHP_EOL, ($append ? FILE_APPEND : 0) | LOCK_EX);
}


function read ($file){
    return file_get_contents($file);
}

function initializeSmarty(){
    $smarty = new Smarty();

    $smarty->setTemplateDir('/reports/smarty/templates/');
    $smarty->setCompileDir('/reports/smarty/templates_c/');
    $smarty->setConfigDir('/reports/smarty/configs/');
    $smarty->setCacheDir('/reports/smarty/cache/');
    $smarty->left_delimiter = '{{';
    $smarty->right_delimiter = '}}';
    return $smarty;

}

?>