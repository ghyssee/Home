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
include_once documentPath (ROOT_PHP_MR_BO, "AssassinBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "FriendBO.php");
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
        case "getGlobalSettings":
            getGlobalSettings();
            break;
        case "saveGlobalSettings":
            saveGlobalSettings();
            break;
        case "getGlobalSettingsBoss":
            getGlobalSettingsBoss();
            break;
        case "saveGlobalSettingsBoss":
            saveGlobalSettingsBoss();
            break;
        case "getListAssassin":
            getListAssassin();
            break;
        case "addAssassin":
            addAssassin();
            break;
        case "updateAssassin":
            updateAssassin();
            break;
        case "deleteAssassin":
            deleteAssassin();
            break;
        case "getListAllies":
            getListAllies();
            break;
        case "addAlly":
            addAlly();
            break;
        case "updateAlly":
            updateAlly();
            break;
        case "deleteAlly":
            deleteAlly();
            break;
        case "getAssassinProfiles":
            getAssassinProfiles();
            break;
        case "getSettingsAssassin":
            getSetttingsAssassin();
            break;
        case "saveAssassinActiveProfile":
            saveAssassinActiveProfile();
            break;
        case "getListBullies":
            getListBullies();
            break;
        case "addBully":
            addBully();
            break;
        case "updateBully":
            updateBully();
            break;
        case "deleteBully":
            deleteBully();
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

function fillFormV2($to, $form){
    $tmpVar = get_object_vars($to);
    foreach($tmpVar as $key => $value) {
        $typeof = gettype($value);
        $composedKey = $to->getBase() . "_" . $key;
        if ($typeof == 'boolean') {
            $to->{$key} = isset($form[$composedKey]);
        } elseif ($typeof == 'integer') {
            if (isset($form[$composedKey])) {
                $to->{$key} = intval($form[$composedKey]);
            }
        } else {
            $to->{$key} = $form[$composedKey];
        }
    }
}

function getSettingsFighting(){
    $settingsBO = new ProfileSettingsBO();
    $fightSettingsTO = new FightSettingsTO();
    $settingsTO = $settingsBO->getSettings($fightSettingsTO);

    $warSettingsTO = $settingsBO->getSubSettings(new WarSettingsTO(), $fightSettingsTO->getBase());

    $obj_merged = (object) array_merge(
        (array) $settingsTO, (array) $warSettingsTO);

    echo json_encode($obj_merged);
}

function saveSettingsFighting(){
    $fightSettingsTO = new FightSettingsTO();
    fillForm($fightSettingsTO, $_POST);
    $fightSettingsTO->war = new WarSettingsTO();
    fillFormV2($fightSettingsTO->war, $_POST);
    //$fightSettingsTO->war->enabled = false;


    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($fightSettingsTO);
    fillSaveMessage($feedBack, "Profile Specific Fight Settings");
    echo json_encode($feedBack);
}

function getSettingsBoss(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings(new BossSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsBoss(){
    $bossSettingsTO = new BossSettingsTO();
    fillForm($bossSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($bossSettingsTO);
    fillSaveMessage($feedBack, "Profile Specific Boss Settings");
    echo json_encode($feedBack);
}

function getSettingsJob(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = new stdClass();
    $settingsBO->fillSettings($settingsTO, new JobSettingsTO());
    $settingsBO->fillSettings($settingsTO, new RobbingSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsJob(){
    $settingsArray = [];
    $jobSettingsTO = new JobSettingsTO();
    $settingsArray[] = $jobSettingsTO;
    $robbingSettingsTO = new RobbingSettingsTO();
    $settingsArray[] = $robbingSettingsTO;
    fillFormV2($jobSettingsTO, $_POST);
    fillFormV2($robbingSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();

    $feedBack = $settingsBO->saveMultiSettings($settingsArray);
    fillSaveMessage($feedBack, "Profile Specific Job Settings");

    echo json_encode($feedBack);
}

function getSettingsHomefeed(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings(new HomefeedSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsHomefeed(){
    $homefeedSettingsTO = new HomefeedSettingsTO();
    fillForm($homefeedSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($homefeedSettingsTO);
    fillSaveMessage($feedBack, "Profile Specific Homefeed Settings");
    echo json_encode($feedBack);
}

function getSettingsGlobal(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings(new globalProfileSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsGlobal(){
    $globalSettingsTO = new globalProfileSettingsTO();
    fillForm($globalSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($globalSettingsTO);
    fillSaveMessage($feedBack, "Profile Specific Global Settings");
    echo json_encode($feedBack);
}

function getSettingsCrime(){
    $settingsBO = new ProfileSettingsBO();
    $settingsTO = $settingsBO->getSettings(new CrimeSettingsTO());
    echo json_encode($settingsTO);
}

function saveSettingsCrime(){
    $crimeSettingsTO = new CrimeSettingsTO();
    fillForm($crimeSettingsTO, $_POST);

    $settingsBO = new ProfileSettingsBO();
    $feedBack = $settingsBO->saveSettings($crimeSettingsTO);
    fillSaveMessage($feedBack, "Profile Specific Crime Settings");
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
    fillSaveMessage($feedBack, "Daily Link Settings");
    echo json_encode($feedBack);
}


function getGlobalSettings()
{
    $settingsBO = new SettingsBO();
    $settingsTO = new stdClass();
    $settingsBO->fillSettings($settingsTO, new DailyLinkTO());
    $settingsBO->fillSettings($settingsTO, new GlobalSettingsTO());
    $settingsBO->fillSettings($settingsTO, new GlobalRobbingSettingsTO());
    $settingsBO->fillSettings($settingsTO, new GlobalSettingsBossTO());
    $settingsBO->fillSettings($settingsTO, new GlobalFightingSettingsTO());
    echo json_encode($settingsTO);
}

function saveGlobalSettings(){
    $settingsArray = [];
    $globalSettingsTO = new GlobalSettingsTO();
    fillFormV2($globalSettingsTO, $_POST);
    $settingsArray[] = $globalSettingsTO;

    $globalRobbingSettingsTO = new GlobalRobbingSettingsTO();
    fillFormV2($globalRobbingSettingsTO, $_POST);
    $settingsArray[] = $globalRobbingSettingsTO;

    $globalSettingsBossTO = new GlobalSettingsBossTO();
    fillFormV2($globalSettingsBossTO, $_POST);
    $settingsArray[] = $globalSettingsBossTO;

    $dailyLinkTO = new DailyLinkTO();
    fillFormV2($dailyLinkTO, $_POST);
    $settingsArray[] = $dailyLinkTO;

    $globalFightingSettingsTO = new GlobalFightingSettingsTO();
    fillFormV2($globalFightingSettingsTO, $_POST);
    $settingsArray[] = $globalFightingSettingsTO;

    $settingsBO = new SettingsBO();

    $feedBack = $settingsBO->saveMultiSettings($settingsArray);
    fillSaveMessage($feedBack, "Profile Specific Job Settings");

    echo json_encode($feedBack);
}


function getGlobalSettingsBoss()
{
    $SettingsBO = new SettingsBO();
    $to = $SettingsBO->getGlobalSettings(new GlobalSettingsBossTO());
    echo json_encode($to);
}

function saveGlobalSettingsBoss(){
    $to = new GlobalSettingsBossTO();
    fillForm($to, $_POST);
    $settingsBO = new SettingsBO();
    $feedBack = $settingsBO->saveGlobalSettings($to);
    fillSaveMessage($feedBack, "Global Settings Boss");
    echo json_encode($feedBack);
}

function fillSaveMessage(FeedBackTO $feedBackTO, $message){
    if ($feedBackTO->success){
        $feedBackTO->message = $message . " saved successfully";
    }
    else {
        $feedBackTO->errorMsg = $message . "There was a problem saving " . $message;
    }

}

function getListAssassin(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $assassinBO = new AssassinBO();
    $list = $assassinBO->getListAssassin($assassinBO->getRequestProfile());
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "name", "asc");
    $array = array_slice($list, ($page-1)*$rows, $rows);
    $result["total"] = count($list);
    $result["rows"] = $array;
    echo json_encode($result);
}


function fillInassassin($assassinTO){
    if (!isset($assassinTO)) {
        $assassinTO = new AssassinTO();
    }
    assignCheckbox($assassinTO->active, "active", !HTML_SPECIAL_CHAR);
    assignField($assassinTO->fighterId, "fighterId", !HTML_SPECIAL_CHAR);
    assignField($assassinTO->name, "name", !HTML_SPECIAL_CHAR);
    return $assassinTO;

}

function addAssassin(){
    $assassinTO = fillInassassin( new AssassinTO() );
    $assassinBO = new AssassinBO(getProfile());
    $feedBackTO = $assassinBO->addAssassin($assassinTO);
    if ($feedBackTO->success){
        echo json_encode($assassinBO->getListAssassin(getProfile()));
    }
    else {
        echo json_encode($feedBackTO);
    }
    exit();
}

function updateAssassin(){
    $feedBackTO = new FeedBackTO();
    if (!isset($_GET['id'])){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = 'Assassin Id Not Found!';
        echo json_encode($feedBackTO);
    }
    else {
        $id = $_GET['id'];
        $assassinBO = new AssassinBO(getProfile());
        $assassinTO = $assassinBO->getAssassin($id);
        fillInassassin($assassinTO);
        $feedBackTO = $assassinBO->updateAssassin($assassinTO);
        if ($feedBackTO->success) {
            echo json_encode($assassinBO->getListAssassin());
        } else {
            echo json_encode($feedBackTO);
        }
    }
    exit();
}

function deleteAssassin()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $assassinBO = new AssassinBO(getProfile());
        $feedBackTO = $assassinBO->deleteAssassin($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of assassin not filled in";
    }
    echo json_encode($feedBackTO);
}

function getListAllies(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $friendBO = new FriendBO();
    $list = $friendBO->getListAllies();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "name", "asc");
    $array = array_slice($list, ($page-1)*$rows, $rows);
    $result["total"] = count($list);
    $result["rows"] = $array;
    echo json_encode($result);
}

function fillInAlly($allyTO){
    if (!isset($allyTO)) {
        $allyTO = new AllyTO();
    }
    assignCheckbox($allyTO->active, "active", !HTML_SPECIAL_CHAR);
    assignField($allyTO->id, "id", !HTML_SPECIAL_CHAR);
    assignField($allyTO->name, "name", !HTML_SPECIAL_CHAR);
    return $allyTO;

}

function addAlly(){
    $allyTO = fillInAlly( new AllyTO() );
    $friendBO = new FriendBO(getProfile());
    $feedBackTO = $friendBO->addAlly($allyTO);
    if ($feedBackTO->success){
        echo json_encode($friendBO->getListAllies());
    }
    else {
        echo json_encode($feedBackTO);
    }
    exit();
}

function updateAlly(){
    $feedBackTO = new FeedBackTO();
    if (!isset($_GET['id'])){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = 'Ally Id Not Found!';
        echo json_encode($feedBackTO);
    }
    else {
        $id = $_GET['id'];
        $friendBO = new FriendBO(getProfile());
        $allyTO = $friendBO->getAlly($id);
        fillInAlly($allyTO);
        $feedBackTO = $friendBO->updateAlly($allyTO);
        if ($feedBackTO->success) {
            echo json_encode($friendBO->getListAllies());
        } else {
            echo json_encode($feedBackTO);
        }
    }
    exit();
}

function deleteAlly()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $friendBO = new FriendBO(getProfile());
        $feedBackTO = $friendBO->deleteAlly($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of ally not filled in";
    }
    echo json_encode($feedBackTO);
}

function getAssassinProfiles(){
    $assassinBO = new AssassinBO(getProfile());
    $list = $assassinBO->getAssassinProfiles();
    echo json_encode($list);
}

function saveAssassinActiveProfile(){
    $assassinSettingsTO = new AssassinSettingsTO();
    fillForm($assassinSettingsTO, $_POST);
    $assassinBO = new AssassinBO(getProfile());
    $feedBackTO = $assassinBO->saveAssassinActiveProfile($assassinSettingsTO);
    fillSaveMessage($feedBackTO, "Assassin Active Profile");
    echo json_encode($feedBackTO);
}

function getSetttingsAssassin(){
    $assassinBO = new AssassinBO(getProfile());
    $assassinTO = $assassinBO->getAssassinSettings();
    echo json_encode($assassinTO);
}

function getListBullies(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $bo = new ProfileSettingsBO(getProfile());
    $list = $bo->getListBullies();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "name", "asc");
    $array = array_slice($list, ($page-1)*$rows, $rows);
    $result["total"] = count($list);
    $result["rows"] = $array;
    echo json_encode($result);
}

function fillInBully(BullyTO $bullyTO){
    if (!isset($bullyTO)) {
        $bullyTO = new $bullyTO();
    }
    assignCheckbox($bullyTO->active, "active", !HTML_SPECIAL_CHAR);
    assignField($bullyTO->id, "id", !HTML_SPECIAL_CHAR);
    assignField($bullyTO->name, "name", !HTML_SPECIAL_CHAR);
    return $bullyTO;

}

function addBully(){
    $bullyTO = fillInBully( new BullyTO() );
    $bo = new ProfileSettingsBO(getProfile());
    $feedBackTO = $bo->addBully($bullyTO);
    if ($feedBackTO->success){
        echo json_encode($bo->getListBullies());
    }
    else {
        echo json_encode($feedBackTO);
    }
    exit();
}

function updateBully(){
    $feedBackTO = new FeedBackTO();
    if (!isset($_GET['id'])){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = 'Bully Id Not Found!';
        echo json_encode($feedBackTO);
    }
    else {
        $id = $_GET['id'];
        $bo = new ProfileSettingsBO(getProfile());
        $bullyTO = $bo->getBully($id);
        fillInBully($bullyTO);
        $feedBackTO = $bo->updateBully($bullyTO);
        if ($feedBackTO->success) {
            echo json_encode($bo->getListBullies());
        } else {
            echo json_encode($feedBackTO);
        }
    }
    exit();
}

function deleteBully()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $bo = new ProfileSettingsBO(getProfile());
        $feedBackTO = $bo->deleteBully($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of bully not filled in";
    }
    echo json_encode($feedBackTO);
}
