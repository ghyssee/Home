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

class FriendBO
{
    public $friendObj;
    public $file;

    function __construct() {
        $fileCode = JSON_MR_FRIEND;
        $this->file = getMRFile($fileCode);
        $this->friendObj = readJSON($this->file);
    }

    function getListAllies(){
        return $this->friendObj->gangs;
    }

    function addAlly(AllyTO $allyTO){
        $feedbackTO = new FeedBackTO();
        $this->friendObj->gangs[] = $allyTO;
        $feedbackTO->success = true;
        $this->save();
        return $feedbackTO;
    }

    function getAlly($id){
        $ally = null;
        foreach($this->friendObj->gangs as $key => $item){
            if ($item->id == $id){
                $ally = new AllyTO($item);
                break;
            }
        }
        return $ally;
    }

    function updateAlly(AllyTO $allyTO){
        $counter = 0;
        $feedBackTO = new FeedBackTO();
        $feedBackTO->success = false;
        foreach ($this->friendObj->gangs as $key => $value) {
            if (strcmp($value->id, $allyTO->id) == 0) {
                $this->friendObj->gangs[$counter] = $allyTO;
                $this->save();
                $feedBackTO->success = true;
                break;
            }
            $counter++;
        }
        if (!$feedBackTO->success){
            $feedBackTO->errorMsg = "Problem updating Ally with Id " . $allyTO->id;
        }
        return $feedBackTO;
    }

    function deleteAlly($id){
        $feedbackTO = new FeedBackTO();
        $key = array_search($id, array_column($this->friendObj->gangs, "id"));
        if ($key === false) {
            $feedbackTO->success = false;
            $feedbackTO->errorMsg = 'There was a problem finding the ally with ID ' . $id;
        } else {
            unset($this->friendObj->gangs[$key]);
            $array = array_values($this->friendObj->gangs);
            $this->friendObj->gangs = $array;
            $this->save();
            $feedbackTO->success = true;
        }
        return $feedbackTO;
    }


    function save(){
        writeJSON($this->friendObj, $this->file);
    }
}