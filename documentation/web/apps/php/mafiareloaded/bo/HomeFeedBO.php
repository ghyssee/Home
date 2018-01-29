<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 11/12/2017
 * Time: 12:59
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");

class HomeFeedTO extends Castable {
    public $timeMsg;
    public $feedMsg;
    public $fighterId;
    public $name;
    public $gangId;
    public $gangName;
    public $timeStamp;
    public $currentTime;
    public $processed;
}

class Castable
{
    public function __construct($object = null)
    {
        $this->cast($object);
    }

    public function cast($object)
    {
        if (is_array($object) || is_object($object)) {
            foreach ($object as $key => $value) {
                if (property_exists($this, $key)) {
                    $this->$key = $value;
                }
                $var = "blbb";
            }
        }
    }
}

class HomeFeedBO{
    public $homeFeedObj;
    public $file;
    public $profile;

    function __construct($profile) {
        $fileCode = JSON_MR_HOMEFEED;
        $this->file = getProfileFile($fileCode, $profile);
        $this->homeFeedObj = readJSON($this->file);
        $this->profile = $profile;
    }

    function getHomeFeed(){
        $homeFeedLines = Array();
        foreach($this->homeFeedObj->kills as $key => $item){
            $homeFeedTO = new HomeFeedTO($item);
            $homeFeedLines[] = $homeFeedTO;
        }
        return $homeFeedLines;
    }

    function cleanupHomefeed() {
        $hisFile = getProfileFile(JSON_MR_HOMEFEED_HISTORY, $this->profile);
        //$homeFeedHistoryObj = readJSON($hisFile);
        foreach($this->homeFeedObj->kills as $key => $item){
            $date = DateUtils::convertStringToDate($item->timeStamp);
            //$homeFeedHistoryObj->kills[] = $item;
            println($date->format('Y-m-d H:i:s'));
        }
    }

}

function getProfileFile($filecode, $profile){
    $file = getFullPath($filecode);
    if (!isset($profile) || $profile == ''){
        $profile = '';
    }
    else {
        $profile .= "\\";
    }
    $file = str_replace("%PROFILE%", $profile, $file);
    return $file;
}