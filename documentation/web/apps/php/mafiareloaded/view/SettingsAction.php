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
        case "getDailyLink":
            getDailyLink();
            break;
        case "saveDailyLink":
            saveDailyLink();
            break;
        case "getSettingsFighting":
            getSettingsFighting();
            break;
        case "saveSettingsFighting":
            saveSettingsFighting();
            break;
        case "saveSettingsBoss":
            saveSettingsBoss();
            break;
        case "getSettingsBoss":
            getSettingsBoss();
            break;
        case "getSettingsJob":
            getSettingsJob();
            break;
        case "saveSettingsJob":
            saveSettingsJob();
            break;
        case "getSettingsHomefeed":
            getSettingsHomefeed();
            break;
        case "saveSettingsHomefeed":
            saveSettingsHomefeed();
            break;
        case "getSettingsGlobal":
            getSettingsGlobal();
            break;
        case "saveSettingsGlobal":
            saveSettingsGlobal();
            break;
        case "saveSettingsCrime":
            saveSettingsCrime();
            break;
        case "getSettingsCrime":
            getSettingsCrime();
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

function getSettingsFighting(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new FightSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsFighting(){
    $fightSettingsTO = new FightSettingsTO();
    fillForm($fightSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($fightSettingsTO);
    echo json_encode($feedBack);
}

function getSettingsBoss(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new BossSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsBoss(){
    $bossSettingsTO = new BossSettingsTO();
    fillForm($bossSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($bossSettingsTO);
    echo json_encode($feedBack);
}

function getSettingsJob(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new JobSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsJob(){
    $jobSettingsTO = new JobSettingsTO();
    fillForm($jobSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($jobSettingsTO);
    echo json_encode($feedBack);
}

function getSettingsHomefeed(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new HomefeedSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsHomefeed(){
    $homefeedSettingsTO = new HomefeedSettingsTO();
    fillForm($homefeedSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($homefeedSettingsTO);
    echo json_encode($feedBack);
}

function getSettingsGlobal(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new GlobalSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsGlobal(){
    $globalSettingsTO = new GlobalSettingsTO();
    fillForm($globalSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($globalSettingsTO);
    echo json_encode($feedBack);
}

function getSettingsCrime(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings1(new CrimeSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsCrime(){
    $crimeSettingsTO = new CrimeSettingsTO();
    fillForm($crimeSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($crimeSettingsTO);
    echo json_encode($feedBack);
}

function getDailyLink()
{
    $SettingsBO = new SettingsBO();
    $dailyLinkTO = $SettingsBO->getDailyLink();
    echo json_encode($dailyLinkTO);
}

function saveDailyLink(){
    $dailyLinkTO = new DailyLinkTO();
    fillForm($dailyLinkTO, $_POST);
    $settingsBO = new SettingsBO();
    $feedBack = $settingsBO->saveDailyLink($dailyLinkTO);
    echo json_encode($feedBack);
}

