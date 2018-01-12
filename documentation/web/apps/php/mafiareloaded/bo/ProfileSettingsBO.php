<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 13/12/2017
 * Time: 13:45
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");
include_once documentPath (ROOT_PHP_MR_BO, "MafiaReloadedBO.php");

class JobSettingsTO  {
    public $money = false;
    public $levelUpExp = 0;
    public $levelUpMinEnergy = 0;

    public function getBase(){
        return "jobs";
    }

    public function __construct()
    {
    }
}

class HomefeedSettingsTO  {
    public $processLines = false;
    public $attack = false;
    public $attackSize = 0;
    public $checkMini = false;

    public function getBase(){
        return "homefeed";
    }

    public function __construct()
    {
    }
}

class BossSettingsTO  {
    public $active = false;
    public $stopWhenHealthBelow = 0;
    public $name = "";

    public function getBase(){
        return "boss";
    }

    public function __construct()
    {
    }
}

class FightSettingsTO  {
    public $autoHeal = false;
    public $heal = 0;
    public $numberOfHealsLimit = 0;
    public $minLengthOfFightList = 0;

    public function getBase(){
        return "fight";
    }

    public function __construct()
    {
    }
}

class ProfileSettingsBO{
    public $mrObj;
    public $file;

    function __construct() {
        $fileCode = JSON_MR_MAFIARELOADED;
        $this->file = getMRFile($fileCode);
        $this->mrObj = readJSON($this->file);
    }

    function getSettings1($settingsTO){
        $tmpVar = get_object_vars($settingsTO);
        foreach($tmpVar as $key => $value) {
            $settingsTO->{$key} = $this->getSetting($settingsTO, $key);
        }
        return $settingsTO;
    }

    function getSetting($fightSettingsTO, $key){
        $value = null;
        if ($fightSettingsTO->getBase() == null){
            $value = $this->mrObj->{$key};
        }
        else {
            $value = $this->mrObj->{$fightSettingsTO->getBase()}->{$key};
        }
        return $value;
    }

    function saveSettings($settingsTO){
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($settingsTO);
    //    $props = getProperties($fightSettingsTO);
        foreach($tmpVar as $key => $value) {
            if ($settingsTO->getBase() == null){
                $this->mrObj->{$key} = $value;
            }
            else {
                $this->mrObj->{$settingsTO->getBase()}->{$key} = $value;
            }

        }
        $this->save();
        $feedBack->success = true;
        $feedBack->message = 'Configuration saved successfully';
        return $feedBack;
    }

    function save(){
        writeJSON($this->mrObj, $this->file);
    }

    function getProperties($obj)
    {
        $reflect = new ReflectionClass($obj);
        $props = $reflect->getProperties(ReflectionProperty::IS_PUBLIC | ReflectionProperty::IS_PROTECTED);
        return $props;
    }
}
