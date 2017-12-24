<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 24/11/2017
 * Time: 15:30
 */
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "JobBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "MafiaReloadedBO.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");
sessionStart();
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
//set_error_handler("customError",E_ALL);

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "list":
            getListScheduledJobs();
            break;
        case "getDistricts":
            getDistricts();
            break;
        case "getJobs":
            getJobs();
            break;
        case "getChapters":
            getChapters();
            break;
        case "getJobTypes":
            getJobTypes();
            break;
        case "saveJobList":
            saveJobList();
            break;
        case "addActiveJob":
            addActiveJob();
            break;
        case "updateActiveJob":
            updateActiveJob();
            break;
        case "deleteScheduledJob":
            deleteScheduledJob();
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

function getDistricts(){
    $activeJobBO = new ActiveJobBO(getProfile());
    $list = $activeJobBO->getDistrictsForComboBox();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "description");
    echo json_encode($list);

}

function getJobs(){
    $activeJobBO = new ActiveJobBO(getProfile());
    $district = "1";
    if (isset($_REQUEST["district"])){
        $district = $_REQUEST["district"];
    }
    $chapter = null;
    if (isset($_REQUEST["chapter"])){
        $chapter = $_REQUEST["chapter"];
    }
    $list = $activeJobBO->getJobs($district, $chapter);
    $sort = new CustomSort();
    // TODO: Sort On multiple fields
    $list = $sort->sortObjectArrayByField($list, "id");
    echo json_encode($list);

}

function getChapters(){
    $activeJobBO = new ActiveJobBO(getProfile());
    $district = "1";
    if (isset($_REQUEST["district"])){
        $district = $_REQUEST["district"];
    }
    $list = $activeJobBO->getChapters($district);
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "id");
    echo json_encode($list);

}

function getJobTypes(){
    $activeJobBO = new ActiveJobBO(getProfile());
    $list = $activeJobBO->getJobTypes();
    echo json_encode($list);

}

function getListScheduledJobs()
{
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $activeJobBO = new ActiveJobBO(getProfile());
    $list = $activeJobBO->getScheduledJobs();

    if (isSetFilterRule()){
        $filterRules = json_decode($_POST["filterRules"]);
        //$artists = $artistBO->getArtists($filterRules);
    }
    else {
        //$artists = $artistBO->getArtists();
    }
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'name';
    $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
    /*
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($artists, "name", $order);
    $artists = $array;
    //}
    $array = array_slice($artists, ($page-1)*$rows, $rows);
*/
    $result = array();
    $result["total"] = count($list);
    $result["rows"] = $list;
    echo json_encode($result);
}

function saveJobList(){
    $feedBackTO = new FeedBackTO();
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $jobList = Array();
        //$multiArtistBO = new MultiArtistBO();
        //$result = $multiArtistBO->addMultiArtistConfig($config->multiArtistConfig);
        foreach ($config->jobs as $key => $item){
            $activeJobTO = new ActiveJobTO($item);
            $jobList[] = $activeJobTO;
        }
        $activeJobBO = new ActiveJobBO($config->profile);
        $activeJobBO->saveJobList($jobList);
        $feedBackTO->success = true;
        $feedBackTO->message = "Joblist Saved" . PHP_EOL;
        echo json_encode($feedBackTO);
    }
    else {
        $feedBackTO->success = false;
        $feedBackTO->message = "Config Object Not Found" . PHP_EOL;
        echo json_encode($feedBackTO);
    }
}

function addMsg($msg, $msgToAdd){
    if ($msg == null){
        $msg = $msgToAdd;
    }
    else {
        $msg = $msg . "<br>" . $msgToAdd;
    }
    return $msg;
}

function validateScheduledJob(ActiveJobTO $activeJobTO ){
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = true;
    if ($activeJobTO->type == null){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = addMsg ($feedBackTO->errorMsg,"Type must be filled in");
    }
    if ($activeJobTO->districtId == null){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = addMsg ($feedBackTO->errorMsg,"District Id must be filled in");
    }
    /*
    if ($activeJobTO->chapter == null){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = addMsg ($feedBackTO->errorMsg,"Chapter must be filled in");
    }*/
    if ($activeJobTO->jobId == null){
        if ($activeJobTO->type != "CHAPTER") {
            $feedBackTO->success = false;
            $feedBackTO->errorMsg = addMsg($feedBackTO->errorMsg, "Job must be filled in");
        }
        else {
            $activeJobTO->jobId = null;
        }
    }
    return $feedBackTO;
}

function fillInScheduledJob(){
    $activeJobTO = new ActiveJobTO();
    assignField($activeJobTO->id, "id", !HTML_SPECIAL_CHAR);
    assignCheckbox($activeJobTO->enabled, "enabled", !HTML_SPECIAL_CHAR);
    assignField($activeJobTO->type, "type", !HTML_SPECIAL_CHAR);
    assignField($activeJobTO->districtId, "districtId", !HTML_SPECIAL_CHAR);
    assignField($activeJobTO->chapter, "chapter", !HTML_SPECIAL_CHAR);
    assignField($activeJobTO->description, "description", !HTML_SPECIAL_CHAR);
    assignField($activeJobTO->jobId, "jobId", !HTML_SPECIAL_CHAR);
    assignNumber($activeJobTO->total, "total", !HTML_SPECIAL_CHAR);
    return $activeJobTO;

}

function addActiveJob(){

    $beforeRowId = null;
    if (isset($_REQUEST['insertBefore'])){
        $beforeRowId = $_REQUEST['insertBefore'];
    }
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    $scheduledJob = fillInScheduledJob();
    $feedBackTO = validateScheduledJob($scheduledJob);
    if ($feedBackTO->success) {
        $activeJobBO = new ActiveJobBO(getProfile());
        $activeJobBO->addScheduledJob($scheduledJob, $beforeRowId);
        $feedBackTO->success = true;
        echo json_encode($activeJobBO->getScheduledJobs());
    } else {
        $errors = addErrorMsg("Error adding a scheduled job");
        echo json_encode($feedBackTO);
    }
    exit();
}

function updateActiveJob(){
    $scheduledJob = fillInScheduledJob();
    if (isset($_REQUEST['id'])){
        $scheduledJob->id = $_REQUEST['id'];
    }
    $feedBackTO = new FeedBackTO();
    if (!isset($scheduledJob->id)){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = 'Scheduled Job Id Not Found!';
        echo json_encode($feedBackTO);
    }
    else {
        $feedBackTO->success = false;
        $feedBackTO = validateScheduledJob($scheduledJob);
        if ($feedBackTO->success) {
            $activeJobBO = new ActiveJobBO(getProfile());
            $feedBackTO = $activeJobBO->updateScheduledJob($scheduledJob);
            if ($feedBackTO->success) {
                echo json_encode($activeJobBO->getScheduledJobs());
            } else {
                echo json_encode($feedBackTO);
            }
        } else {
            echo json_encode($feedBackTO);
        }
    }
    exit();
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function deleteScheduledJob()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $activeJobBO = new ActiveJobBO(getProfile());
        $feedBackTO = $activeJobBO->deleteScheduledJob($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of scheduled job not filled in";
    }
    echo json_encode($feedBackTO);
}
