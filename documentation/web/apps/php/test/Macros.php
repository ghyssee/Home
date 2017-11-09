<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 9/11/2017
 * Time: 11:39
 */
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

const CANCELED_BY_USER = -101;
const SUCCESS = 1;

Const MACRO_FOLDER = "C:\\My Programs\\iMacros\\macros";
Const FIGHT_FOLDER = "MR\\Fight";
Const COMMON_FOLDER = "MR\\Common";
Const JOB_FOLDER = "MR\\Jobs";

define ("MR_PATH", "C:\\My Programs\\OneDrive\\Config\\MR\\");
    //getFullPath(PATH_MAIN_CONFIG) . "\\MR");
Const MR_FIGHTERS_EXCLUDE_FILE = MR_PATH . "\\" . "FightersToExclude.json";
Const MR_FRIENDS_FILE = MR_PATH . "\\" . "Friends.json";
Const MR_FIGHTERS_FILE = MR_PATH . "\\" . "Fighters.json";
Const MR_CONFIG_FILE = MR_PATH . "\\" . "MafiaReloaded.json";
Const MACRO_INFO_LOGGING = true;

class UserCancelException extends Exception { }


class iMacros {
    static $iimHolder;
}

Class GlobalObjects
{
    public static $fightersToExclude;
    public static $friendObj;
    public static $fighterObj;
    public static $configMRObj;

    public static function init() {
        self::$fightersToExclude = readJSON(MR_FIGHTERS_EXCLUDE_FILE);
        self::$friendObj = readJSON(MR_FRIENDS_FILE);
        self::$fighterObj = readJSON(MR_FIGHTERS_FILE);
        self::$configMRObj = readJSON(MR_CONFIG_FILE);
    }
}

function playMacro($iimHolder, $folder, $macro, $logging = true){
    if (!empty($folder)){
        $folder .= "\\";
    }
    echo $folder . $macro;
    $retCode = $iimHolder->iimPlay($folder . $macro);
    if ($retCode == CANCELED_BY_USER){
        throw new UserCancelException();
    }
    printAndFlush( "Macro: " . $macro);
    printAndFlush( "Return code: " . $retCode);
    return $retCode;
}

function wait($iimHolder, $seconds){
    $iimHolder->iimSet("seconds", $seconds);
    $retcode = $iimHolder->iimPlay("Wait.iim");
    if ($retcode == CANCELED_BY_USER){
        throw new UserCancelException();
    }
    return $retcode;
}

function startsWith($msg, $prefix){
    return (strncmp($msg, $prefix, strlen($prefix)) === 0);
}

?>