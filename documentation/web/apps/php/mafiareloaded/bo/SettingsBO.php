<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 13/12/2017
 * Time: 13:45
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");
include_once documentPath (ROOT_PHP_MR_BO, "MafiaReloadedBO.php");


class DailyLinkTO  {
    public $date;
    public $link;

    function getBase(){
        return "dailyLink";
}

    public function __construct()
    {
    }
}

class GlobalSettingsTO  {
    public $eventEnabled = false;

    function getBase(){
        return "global";
    }

    public function __construct()
    {
    }
}

class GlobalSettingsBossTO  {
    public $bossName;

    function getBase(){
        return "boss";
    }

    public function __construct()
    {
    }
}

class SettingsBO{
    public $settingsObj;
    public $fileCode = JSON_MR_SETTINGS;

    function __construct() {
        $this->settingsObj = readJSONWithCode($this->fileCode);
    }

    function getDailyLink(){
        $dailyLinkTO = new DailyLinkTO();
        $tmpVar = get_object_vars($dailyLinkTO);
        //$tmpVar = getProperties($dailyLinkTO);
        foreach($tmpVar as $key => $value) {
            $dailyLinkTO->{$key} = $this->getSetting($dailyLinkTO, $key);
        }
        return $dailyLinkTO;
    }

    function getGlobalSettings($to){
        $tmpVar = get_object_vars($to);
        //$tmpVar = getProperties($dailyLinkTO);
        foreach($tmpVar as $key => $value) {
            $to->{$key} = $this->getSetting($to, $key);
        }
        return $to;
    }

    function saveGlobalSettings($to){
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($to);
        foreach($tmpVar as $key => $value) {
            if ($to->getBase() == null){
                $this->settingsObj->{$key} = $value;
            }
            else {
                $this->settingsObj->{$to->getBase()}->{$key} = $value;
            }
        }
        $feedBack->success = true;
        $this->save();
        return $feedBack;
    }

    function saveDailyLink(DailyLinkTO $dailyLinkTO){
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($dailyLinkTO);
        //$tmpVar = getProperties($dailyLinkTO);
        foreach($tmpVar as $key => $value) {
            if ($dailyLinkTO->getBase() == null){
                $this->settingsObj->{$key} = $value;
            }
            else {
                $this->settingsObj->{$dailyLinkTO->getBase()}->{$key} = $value;
            }
        }
        $feedBack->success = true;
        $feedBack->message = 'Daily Link saved successfully';
        $this->save();
        return $feedBack;
    }

    function save(){
        writeJSONWithCode($this->settingsObj, $this->fileCode);
    }

    function getSetting($to, $key){
        $value = null;
        if ($to->getBase() == null){
            $value = $this->settingsObj->{$key};
        }
        else {
            $value = $this->settingsObj->{$to->getBase()}->{$key};
        }
        return $value;
    }
}


function getProperties($obj)
{
    $reflect = new ReflectionClass($obj);
    $props = $reflect->getProperties(ReflectionProperty::IS_PUBLIC | ReflectionProperty::IS_PROTECTED);
    return $props;
}
