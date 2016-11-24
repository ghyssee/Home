<?php

const PATH_CONFIG = "Config";
const JSON_MP3SETTINGS = "mp3SettingsConfig";
const JSON_MP3PREPROCESSOR = "mp3PreprocessorConfig";
const JSON_MP3PRETTIFIER = "mp3PrettifierConfig";
const JSON_HTML = "htmlConfig";

function initSetup(){
    if (isset($GLOBALS["SETUP"])){
        return $GLOBALS["SETUP"];
    }
    else {
        $oneDrive = getOneDrivePath();
        // main JSON File => can't use getFullPath yet
        $setupObj = readJSON($oneDrive . '/Config/Setup.json');
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

function readJSON($file){
    if (file_exists ($file)){
        $str = file_get_contents($file);
        return json_decode($str);
    }
    else {
        throw new Exception("File Not Found:" . $file);
    }
}


function readJSONWithCode($code){
    $file = getFullPath($code);
    return readJSON($file);
}



function writeJSONWithCode($json, $code){
    $file = getFullPath($code);
    writeJSON($json, $file);
}

function writeJSON($json, $file){
    file_put_contents($file, json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES));
}


function write ($file, $text){
    file_put_contents($file, $text . PHP_EOL, FILE_APPEND | LOCK_EX);
}

?>