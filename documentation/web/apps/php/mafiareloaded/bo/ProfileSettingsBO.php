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

class ConfigTO {
    private $base = "fight";
    public function getBase(){
        return $this->base;
    }
}

class FightSettingsTO extends ConfigTO {
    public $autoHeal = false;
    public $heal = 0;
    public $numberOfHealsLimit = 0;
    public $minLengthOfFightList = 0;

    public function __construct()
    {
    }
}

class ProfileSettingsBO{
    public $mrObj;
    public $fileCode = JSON_MR_MAFIARELOADED;

    function __construct() {
        $this->mrObj = readJSON(getMRFile($this->fileCode));
    }

    function getSettings1(){
        $fightSettingsTO = new FightSettingsTO();
        $tmpVar = get_object_vars($fightSettingsTO);
        foreach($tmpVar as $key => $value) {
           $fightSettingsTO->{$key} = $this->getSetting($fightSettingsTO, $key);
        }
        return $fightSettingsTO;
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

    function saveSettings(FightSettingsTO $fightSettingsTO){
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($fightSettingsTO);
    //    $props = getProperties($fightSettingsTO);
        foreach($tmpVar as $key => $value) {
            if ($fightSettingsTO->getBase() == null){
                $this->mrObj->{$key} = $value;
            }
            else {
                $this->mrObj->{$fightSettingsTO->getBase()}->{$key} = $value;
            }

        }
        $feedBack->success = true;
        $feedBack->message = 'Configuration saved successfully';
        return $feedBack;
    }

    function save(){
        writeJSONWithCode($this->mrObj, $this->fileCode);
    }

    function getProperties($obj)
    {
        $reflect = new ReflectionClass($obj);
        $props = $reflect->getProperties(ReflectionProperty::IS_PUBLIC | ReflectionProperty::IS_PROTECTED);
        return $props;
    }
}
