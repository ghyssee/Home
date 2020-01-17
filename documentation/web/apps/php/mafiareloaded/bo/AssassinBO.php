<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 6/03/2018
 * Time: 12:07
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "MafiaReloadedBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "FightTO.php");

const ASSASSIN_DEFAULT_PROFILE = "malin";

const ASSASSIN_PROFILE_STATUS_UNKNOWN = "UNKNOWN";

class AssassinProfileTO {
    public $id;
    public $name;
    public $status;
    function __construct($id, $name) {
        $this->id = $id;
        $this->name = $name;
        $this->status = ASSASSIN_PROFILE_STATUS_UNKNOWN;
    }
}

class AssassinSettingsTO {
    public $activeProfile;
    public $redIce;

    function getBase(){
        return null;
    }
}

class AssassinBO
{
    public $assassinObj;
    public $file;

    function __construct() {
        $fileCode = JSON_MR_ASSASSIN;
        $this->file = getMRFile($fileCode);
        $this->assassinObj = readJSON($this->file);
    }

    function getRequestProfile(){
        $profile = null;
        if (isset($_REQUEST["assassinProfile"])){
            $profile = $_REQUEST["assassinProfile"];
        }
        return $profile;
    }

    function getAssassinProfile($id){
        foreach($this->assassinObj->profiles as $key => $item){
            if ($item->id == $id){
                return $item;
            }
        }
        return null;
    }


    function getAssassinProfiles(){
        $list = Array();
        foreach($this->assassinObj->profiles as $key => $item){
            $assassinTO = new AssassinProfileTO($item->id, $item->name);
            if ($item->id == $this->assassinObj->activeProfile){
                $assassinTO->selected = true;
            }
            $list[] = $assassinTO;
        }
        return $list;
    }

    function saveAssassinActiveProfile(AssassinSettingsTO $assassinSettingsTO){
        $feedBack = new FeedBackTO();
        $tmpVar = get_object_vars($assassinSettingsTO);
        //    $props = getProperties($fightSettingsTO);
        foreach ($tmpVar as $key => $value) {
            if ($assassinSettingsTO->getBase() == null) {
                $this->assassinObj->{$key} = $value;
            } else {
                $this->assassinObj->{$assassinSettingsTO->getBase()}->{$key} = $value;
            }

        }
        $this->save();
        $feedBack->success = true;
        return $feedBack;
    }

    function getListAssassin($id){
        $assassinProfile = $this->getAssassinProfile($id);
        if ($assassinProfile != null){
            return $assassinProfile->players;
        }
        return Array();
    }

    function addAssassin(AssassinTO $assassinTO){
        // bug: should be added to >assassinObj->profiles[0..n]->players
        // first look up the exact profile, than add player to that profile
        $assassinTO->id = getUniqueId();
        $feedbackTO = new FeedBackTO();
        $this->assassinObj->players[] = $assassinTO;
        $feedbackTO->success = true;
        $this->save();
        return $feedbackTO;
    }

    function getAssassin($id){
        $assassin = null;
        foreach($this->assassinObj->players as $key => $item){
            if ($item->id == $id){
                $assassin = new AssassinTO($item);
                break;
            }
        }
        return $assassin;
    }

    function updateAssassin(AssassinTO $assassinTO){
        $counter = 0;
        $feedBackTO = new FeedBackTO();
        $feedBackTO->success = false;
        foreach ($this->assassinObj->players as $key => $value) {
            if (strcmp($value->id, $assassinTO->id) == 0) {
                $this->assassinObj->players[$counter] = $assassinTO;
                $this->save();
                $feedBackTO->success = true;
                break;
            }
            $counter++;
        }
        if (!$feedBackTO->success){
            $feedBackTO->errorMsg = "Problem updating the assassin with Id " . $assassinTO->id;
        }
        return $feedBackTO;
    }

    function deleteAssassin($id){
        $feedbackTO = new FeedBackTO();
        $key = array_search($id, array_column($this->assassinObj->players, "id"));
        if ($key === false) {
            $feedbackTO->success = false;
            $feedbackTO->errorMsg = 'There was a problem finding the assassin with ID ' . $id;
        } else {
            unset($this->assassinObj->players[$key]);
            $array = array_values($this->assassinObj->players);
            $this->assassinObj->players = $array;
            $this->save();
            $feedbackTO->success = true;
        }
        return $feedbackTO;
    }

    function save(){
        writeJSON($this->assassinObj, $this->file);
    }

    function getAssassinSettings(){
        $assassinSettingsTO = new AssassinSettingsTO();
        $tmpVar = get_object_vars($assassinSettingsTO);
        //$tmpVar = getProperties($dailyLinkTO);
        foreach($tmpVar as $key => $value) {
            $assassinSettingsTO->{$key} = $this->getSetting($assassinSettingsTO, $key);
        }
        return $assassinSettingsTO;

    }

    function getSetting($to, $key){
        $value = null;
        if ($to->getBase() == null){
            $value = $this->assassinObj->{$key};
        }
        else {
            $value = $this->assassinObj->{$to->getBase()}->{$key};
        }
        return $value;
    }
}