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

class AssassinBO
{
    public $assassinObj;
    public $file;

    function __construct() {
        $fileCode = JSON_MR_ASSASSIN;
        $this->file = getMRFile($fileCode);
        $this->assassinObj = readJSON($this->file);
    }

    function getListAssassin(){
        return $this->assassinObj->players;
    }

    function addAssassin(AssassinTO $assassinTO){
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
}