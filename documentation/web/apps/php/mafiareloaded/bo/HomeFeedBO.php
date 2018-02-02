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
require_once documentPath (ROOT_PHP_COMMON, "DateUtils.php");

class HomeFeedTO extends Castable {
    public $id;
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

    function cleanupHomefeed($nrOfDays) {
        //$nrOfDays =
        $hisFile = getProfileFile(JSON_MR_HOMEFEED_HISTORY, $this->profile);
        $hisObj = readJSON($hisFile);
        //$homeFeedHistoryObj = readJSON($hisFile);
        $cnt = 0;
        $minDate = new DateTime();
        $minDate->sub(new DateInterval("P" . $nrOfDays . "D"));
        $save = false;
        $newListKills = Array();
        foreach($this->homeFeedObj->kills as $key => $item){
            $date = DateUtils::convertStringToDate($item->timeStamp);
            if ($date < $minDate){
                $cnt++;
                $hisObj->kills[] = $item;
                $save = true;
            }
            else {
                $newListKills[] = $item;
            }
        }
        $this->homeFeedObj->kills = $newListKills;
        $newListLines = Array();
        foreach($this->homeFeedObj->lines as $key => $item){
            $date = DateUtils::convertStringToDate($item->timeStamp);
            if ($date < $minDate){
                $cnt++;
                $hisObj->lines[] = $item;
                $save = true;
            }
            else {
                $newListLines[] = $item;
            }
        }
        $this->homeFeedObj->lines = $newListLines;
        if ($save){
            writeJSON($hisObj, $hisFile);
            $this->save();
        }
        $feedbackTO = new FeedBackTO();
        $feedbackTO->success = true;
        $feedbackTO->message = "Cleanup executed. " . $cnt . " lines were removed";
        return $feedbackTO;
    }

    function deleteHomefeedLine($id){
        $feedbackTO = new FeedBackTO();
        $key = array_search($id, array_column($this->homeFeedObj->kills, "id"));
        if ($key === false) {
            $feedbackTO->success = false;
            $feedbackTO->errorMsg = 'There was a problem finding the line with ID ' . $id;
            return $feedbackTO;

        } else {
            unset($this->homeFeedObj->kills[$key]);
            $array = array_values($this->homeFeedObj->kills);
            $this->homeFeedObj->kills = $array;
            $this->save();
            $feedbackTO->success = true;
        }
        return $feedbackTO;

    }

    function save(){
        writeJSON($this->homeFeedObj, $this->file);
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
