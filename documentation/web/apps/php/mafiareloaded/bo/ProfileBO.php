<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 24/11/2017
 * Time: 13:32
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");

const DEFAULT_PROFILE = "01";

class ProfileTO extends Castable2
{
    public $id;
    public $name;
}

class Castable2
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

class ProfileBO{
    public $profileObj;
    public $fileCode = JSON_MR_PROFILE;

    function __construct() {
        $this->profileObj = readJSONWithCode($this->fileCode);
    }

    function getEmptyProfile(){
        $profileTO = new ProfileTO();
        $profileTO->id = '';
        $profileTO->name = htmlspecialchars('|Empty|');
        return $profileTO;
    }

    function getProfiles(){
        return $this->profileObj->list;
    }

    function getProfilesAndSetDefault($id){
        foreach($this->profileObj->list as $key => $value){
            if ($value->id == $id){
                $value->selected = true;
            }
        }
        return $this->profileObj->list;
    }

    function save(){
        writeJSONWithCode($this->profileObj, $this->fileCode);
    }
}