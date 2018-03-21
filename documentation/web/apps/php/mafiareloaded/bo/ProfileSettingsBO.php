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
include_once documentPath (ROOT_PHP_MR_BO, "FightTO.php");

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

class CrimeSettingsTO  {
    public $enabled = false;
    public $startNewCrime = false;
    public $help = false;
    public $position = 0;

    public function getBase(){
        return "crimeEvent";
    }

    public function __construct()
    {
    }
}

class globalProfileSettingsTO {
    public $stopWhenExpBelow = 0;
    public $stopWhenStaminaBelow = 0;

    public function getBase(){
        return "global";
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
    public $pummel = false;

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
    public $stopWhenStaminaBelow = 0;
    public $profileAttackSize = 0;
    public $maxNumberOfAttacks = 0;
    public $maxKillsDay = 0;
    public $minStaminaToFight = 0;

    public function getBase(){
        return "fight";
    }

    public function __construct()
    {
    }
}

class BullyTO extends Castable {
    public $id;
    public $name;
    public $active;
}

class ProfileSettingsBO
{
    public $mrObj;
    public $file;

    function __construct()
    {
        $fileCode = JSON_MR_MAFIARELOADED;
        $this->file = getMRFile($fileCode);
        $this->mrObj = readJSON($this->file);
    }

    function getSettings($settingsTO)
    {
        $tmpVar = get_object_vars($settingsTO);
        foreach ($tmpVar as $key => $value) {
            $settingsTO->{$key} = $this->getSetting($settingsTO, $key);
        }
        return $settingsTO;
    }

    private function getSetting($settingsTO, $key)
    {
        $value = null;
        if ($settingsTO->getBase() == null) {
            $value = $this->mrObj->{$key};
        } else {
            $value = $this->mrObj->{$settingsTO->getBase()}->{$key};
        }
        return $value;
    }

    function saveSettings($settingsTO)
    {
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($settingsTO);
        //    $props = getProperties($fightSettingsTO);
        foreach ($tmpVar as $key => $value) {
            if ($settingsTO->getBase() == null) {
                $this->mrObj->{$key} = $value;
            } else {
                $this->mrObj->{$settingsTO->getBase()}->{$key} = $value;
            }

        }
        $this->save();
        $feedBack->success = true;
        return $feedBack;
    }

    function getListBullies(){
        return $this->mrObj->fight->underAttackList;
    }

    function addBully(BullyTO $bullyTO){
        $feedbackTO = new FeedBackTO();
        $this->mrObj->fight->underAttackList[] = $bullyTO;
        $feedbackTO->success = true;
        $this->save();
        return $feedbackTO;
    }

    function getBully($id){
        $bully = null;
        foreach($this->mrObj->fight->underAttackList as $key => $item){
            if ($item->id == $id){
                $bully = new BullyTO($item);
                break;
            }
        }
        return $bully;
    }

    function updateBully(BullyTO $bullyTO){
        $counter = 0;
        $feedBackTO = new FeedBackTO();
        $feedBackTO->success = false;
        foreach ($this->mrObj->fight->underAttackList as $key => $value) {
            if (strcmp($value->id, $bullyTO->id) == 0) {
                $this->mrObj->fight->underAttackList[$counter] = $bullyTO;
                $this->save();
                $feedBackTO->success = true;
                break;
            }
            $counter++;
        }
        if (!$feedBackTO->success){
            $feedBackTO->errorMsg = "Problem updating Bully with Id " . $bullyTO->id;
        }
        return $feedBackTO;
    }

    function deleteBully($id){
        $feedbackTO = new FeedBackTO();
        $key = array_search($id, array_column($this->mrObj->fight->underAttackList, "id"));
        if ($key === false) {
            $feedbackTO->success = false;
            $feedbackTO->errorMsg = 'There was a problem finding the bully with ID ' . $id;
        } else {
            unset($this->mrObj->fight->underAttackList[$key]);
            $array = array_values($this->mrObj->fight->underAttackList);
            $this->mrObj->fight->underAttackList = $array;
            $this->save();
            $feedbackTO->success = true;
        }
        return $feedbackTO;
    }

    function save()
    {
        writeJSON($this->mrObj, $this->file);
    }
}

function getProperties($obj)
{
    $reflect = new ReflectionClass($obj);
    $props = $reflect->getProperties(ReflectionProperty::IS_PUBLIC | ReflectionProperty::IS_PROTECTED);
    return $props;
}
