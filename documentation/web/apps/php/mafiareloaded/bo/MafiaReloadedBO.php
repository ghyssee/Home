<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 6/12/2017
 * Time: 11:46
 */
require_once documentPath (ROOT_PHP, "setup.php");

function getProfile(){
    $profile = null;
    if (isset($_REQUEST["profile"])){
        $profile = $_REQUEST["profile"];
    }
    return $profile;
}

function getMRFile($fileCode)
{
    $profile = getProfile();
    $file = getFullPath($fileCode);
    if (!isset($profile) || $profile == '') {
        $profile = '';
    } else {
        $profile .= "\\";
    }
    $file = str_replace("%PROFILE%", $profile, $file);
    return $file;
}


?>