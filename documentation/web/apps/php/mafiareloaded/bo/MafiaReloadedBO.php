<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 6/12/2017
 * Time: 11:46
 */

function getProfile(){
    $profile = null;
    if (isset($_REQUEST["profile"])){
        $profile = $_REQUEST["profile"];
    }
    return $profile;
}


?>