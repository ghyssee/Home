<?php

include_once('Smarty.class.php');

const PATH_MAIN_CONFIG = "mainConfig";
const PATH_CONFIG = "config";
const PATH_CONFIG_BACKUP = "configBackup";
const PATH_LOCAL_CONFIG = "localConfig";
const PATH_CONFIG_MP3PRETTIFIER = "mp3PrettifierConfigPath";
const JSON_LOCAL_DATABASE = "localDatabase";
const JSON_DATABASE = "database";
const JSON_MP3SETTINGS = "mp3SettingsConfig";
const JSON_MP3PREPROCESSOR = "mp3PreprocessorConfig";
const JSON_MP3PRETTIFIER = "mp3PrettifierConfig";
const JSON_ARTISTS = "artists";
const JSON_MULTIARTIST = "multiArtistConfig";
const JSON_ARTISTSONGRELATIONSHIP = "artistSongRelationship";
const JSON_HTML = "htmlConfig";
const JSON_JOBS = "jobs";
const JSON_MENU = "menu";
const JSON_ALBUMERRORS = "albumErrors";
const JSON_SONGCORRECTIONS = "songCorrections";
const JSON_ARTISTSONGTEST = "artistSongTest";
const JSON_MR_JOBS = "mafiaReloadedJobs";
const JSON_MR_PROFILE = "mafiaReloadedProfile";
const JSON_MR_HOMEFEED = "mafiaReloadedHomeFeed";
const FILE_ERROR_LOG = "errorLog";
const FILE_INFO_LOG = "infoLog";
const FILE_ALBUM = "albumInfo";
const FILE_ALBUMCHECK = "albumsToCheck";
const FILE_ALBUMEXCLUDE = "albumsToExclude";
const FILE_ALBUMWITHOUTERRORS = "albumsWithoutErrors";
const APPEND = true;
const ESCAPE_HTML = true;
const JSON_ASSOCIATIVE = true;
define ('DOCUMENT_ROOT', $_SERVER["CONTEXT_DOCUMENT_ROOT"]);
define ('WEB_ROOT', "/catalog");
define ('ROOT_APPS', DOCUMENT_ROOT . "/apps");
define ('ROOT_PHP', ROOT_APPS . "/php");
define ('ROOT_PHP_BO', ROOT_PHP . "/bo");
define ('ROOT_PHP_DATABASE', ROOT_PHP . "/database");
define ('ROOT_PHP_CONFIGURATION', ROOT_PHP . "/configuration");
define ('ROOT_PHP_CONFIGURATION_MP3PRETTIFIER', ROOT_PHP_CONFIGURATION . "/MP3Prettifier");
define ('ROOT_PHP_HTML', ROOT_PHP . "/html");
define ('ROOT_PHP_JOBS', ROOT_PHP . "/jobs");
define ('ROOT_PHP_MODEL', ROOT_PHP . "/model");
define ('ROOT_PHP_TEMPLATES', ROOT_PHP . "/templates");
define ('ROOT_APPS_MUSIC', ROOT_APPS . "/music");
define ('ROOT_APPS_MUSIC_SONGS', ROOT_APPS_MUSIC . "/songs");
define ('ROOT_PHP_UTILS', ROOT_PHP . "/utils");
define ('ROOT_PHP_VIEW', ROOT_PHP . "/view");
define ('ROOT_PHP_MR', ROOT_PHP . "/mafiareloaded");
define ('ROOT_PHP_MR_BO', ROOT_PHP_MR . "/bo");
define ('ROOT_CSS', ROOT_APPS . "/css");
define ('ROOT_JS', ROOT_APPS . "/js");
define ('ROOT_JS_EASYUI', ROOT_JS . "/easyui");
define ('ROOT_THEMES', ROOT_APPS . "/themes");
define ('ROOT_PHP_TEST', ROOT_PHP . "/test");

const MULTIARTIST_RADIO_ARTISTS = "artists";
const MULTIARTIST_RADIO_ARTISTSEQUENCE = "artistSequence";

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

function documentRoot($string){
    return DOCUMENT_ROOT . "/" . $string;
}

function documentPath($path, $string){
    return $path . "/" . $string;
}

function webPath($path, $string){
    $path = str_replace(DOCUMENT_ROOT, WEB_ROOT, $path);
    return $path . "/" . $string;
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
            throw new ApplicationException("Path With Id Not Found: " . $id);
        }
        $first = false;
    } while ($parent);
    return replaceSystemVariables($path);

}

function getHostNameCache(){
    static $host;
    if (!isset($host)){
        $host = gethostname();
    }
    return $host;
}

function replaceSystemVariables($string){
    $string = str_replace("%ONEDRIVE%", getOneDrivePath(), $string );
    $string = str_replace("%HOST%",getHostNameCache(), $string );
    return $string;
}

function getOneDrivePath() {
    if (isset($GLOBALS["ONEDRIVE"])){
        return $GLOBALS["ONEDRIVE"];
    }
    else {
        $Wshshell = new COM('WScript.Shell');
        try {
            $oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\OneDrive\\CustomUserFolder');
        } catch (Exception $e) {
            try {
                $oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\OneDrive\\UserFolder');
            } catch (Exception $e) {
                $oneDrive = $Wshshell->regRead('HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\SkyDrive\\UserFolder');
            }
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
        throw new ApplicationException("File Not Found:" . $file);
    }
}


function readJSONWithCode($code, $associative = false){
    $file = getFullPath($code);
    logInfo("read " . $file);
    return readJSON($file, $associative);
}


function writeJSONWithCode($json, $code){
    $file = getFullPath($code);
    writeJSON($json, $file);
}

function writeJSON($json, $file){
    if (isset($json)) {
        $obj = json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES);
        $str = mb_convert_encoding($obj, "UTF-8", "auto");
        file_put_contents($file, $str);
    }
}

function write ($file, $text, $append = APPEND){
    file_put_contents($file, $text . PHP_EOL, ($append ? FILE_APPEND : 0) | LOCK_EX);
}

function logError ($class, $line, $msg){
    $file = getFullPath(FILE_ERROR_LOG);
    $msg = "File: " . $class . PHP_EOL . "Line: " . $line . PHP_EOL . "Message: " . $msg;
    file_put_contents($file, $msg . PHP_EOL, FILE_APPEND | LOCK_EX);
}

function logInfo ($msg){
    $file = addTimeStampToFile(getFullPath(FILE_INFO_LOG));
    $msg = getCurrentTime() . ' ' . $msg;
    file_put_contents($file, $msg . PHP_EOL, FILE_APPEND | LOCK_EX);
}

const INFO = 0;
const WARN = 1;
const ERROR = 2;
class LogTest {
    public static $file = "C:\\My Programs\\iMacros\logs\\test.txt";

    public static function log($infoType, $category, $msg){
        $time = getCurrentTime();
        $infoMsg = "";
        switch  ($infoType){
            case INFO:
                $infoMsg = "INFO";
                break;
            case ERROR:
                $infoMsg = "ERROR";
                break;
            case WARN:
                $infoMsg = "WARN";
                break;

        }
        file_put_contents(logTest::$file, $time . ' ' . $infoMsg . ': TITLE: ' . $category . ' - ' .
            $msg . PHP_EOL, FILE_APPEND | LOCK_EX);
    }
}

function getCurrentTime(){
    return date("d/m/Y H:i:s");
}

function addTimeStampToFile($file){
    $path_parts = pathinfo($file);
    $file = $path_parts['dirname'] . '/' . $path_parts['filename'] . getYYYYMMDD() . '.' . $path_parts['extension'];
    return $file;
}

function getTimeStamp(){
    return date("YmdHis");
}

function getYYYYMMDD(){
    return date("Ymd");
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

interface IException
{
    /* Protected methods inherited from Exception class */
    public function getMessage();                 // Exception message
    public function getCode();                    // User-defined Exception code
    public function getFile();                    // Source filename
    public function getLine();                    // Source line
    public function getTrace();                   // An array of the backtrace()
    public function getTraceAsString();           // Formated string of trace

    /* Overrideable methods inherited from Exception class */
    public function __toString();                 // formated string for display
    public function __construct($message = null, $code = 0);
}

abstract class CustomException extends Exception implements IException
{
    protected $message = 'Unknown exception';     // Exception message
    private   $string;                            // Unknown
    protected $code    = 0;                       // User-defined exception code
    protected $file;                              // Source filename of exception
    protected $line;                              // Source line of exception
    private   $trace;                             // Unknown

    public function __construct($message = null, $code = 0)
    {
        if (!$message) {
            throw new $this('Unknown '. get_class($this));
        }
        parent::__construct($message, $code);
    }

    public function __toString()
    {
        return get_class($this) . " '{$this->message}' in {$this->file}({$this->line})\n"
        . "{$this->getTraceAsString()}";
    }
}

class ApplicationException extends CustomException {
}



?>