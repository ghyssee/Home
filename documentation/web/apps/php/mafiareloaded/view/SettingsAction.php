<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:52
 */

chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "ProfileBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "ProfileSettingsBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "SettingsBO.php");
sessionStart();
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
//set_error_handler("customError",E_ALL);

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "getSettings1":
            getSettings1();
            break;
        case "set1":
            saveSettings1();
            break;
    }
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}
catch(ApplicationException $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
    echo 'Caught exception: ',  $e->getMessage(), "\n";
}
exit(0);

function customError($errno, $errstr) {
    echo "<b>Error:</b> [$errno] $errstr<br>";
    echo "Ending Script";
    die();
}

function fillFightSettings(){

}

function fillForm($to, $form){
    $tmpVar = get_object_vars($to);
    foreach($tmpVar as $key => $value) {
        $typeof = gettype($value);
        if ($typeof == 'boolean') {
            $to->{$key} = isset($form[$key]);
        } elseif ($typeof == 'integer') {
            if (isset($form[$key])) {
                $to->{$key} = intval($form[$key]);
            }
        } else {
            $to->{$key} = $form[$key];
        }
    }
}

function saveSettings1(){
    $fightSettingsTO = new FightSettingsTO();
    fillForm($fightSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($fightSettingsTO);
    echo json_encode($feedBack);
}

function getSettings1(){
    $settingsBO = new ProfileSettingsBO();
    $fightSettingsTO = $settingsBO->getSettings1();
    $SettingsBO = new SettingsBO();
    $dailyLinkTO = $SettingsBO->getDailyLink();
    echo json_encode($fightSettingsTO);


}
