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
            getListDistricts();
            break;
        case "addDistrict":
            addDistrict();
            break;
        case "updateDistrict":
            updateDistrict();
            break;
        case "deleteDistrict":
            deleteDistrict();
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

function getListDistricts(){
    $activeJobBO = new ActiveJobBO(getProfile());
    $list = $activeJobBO->getDistricts();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "id");
    echo json_encode($list);

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

function validateDistrict(DistrictTO $districtTO ){
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = true;
    if ($districtTO->id == null){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = addMsg ($feedBackTO->errorMsg,"District Id must be filled in");
    }
    return $feedBackTO;
}

function fillInDistrict(){
    $districtTO = new DistrictTO();
    assignField($districtTO->id, "id", !HTML_SPECIAL_CHAR);
    assignField($districtTO->description, "description", !HTML_SPECIAL_CHAR);
    assignCheckbox($districtTO->scan, "scan", !HTML_SPECIAL_CHAR);
    assignCheckbox($districtTO->event, "event", !HTML_SPECIAL_CHAR);
    assignNumber($districtTO->scanChapterStart, "scanChapterStart", !HTML_SPECIAL_CHAR);
    assignNumber($districtTO->scanChapterEnd, "scanChapterEnd", !HTML_SPECIAL_CHAR);
    return $districtTO;

}

function addDistrict()
{

    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    $district = fillInDistrict();
    $feedBackTO = validateDistrict($district);
    if ($feedBackTO->success) {
        $activeJobBO = new ActiveJobBO(getProfile());
        $feedBackTO = $activeJobBO->addDistrict($district);
    }
    if (!$feedBackTO->success) {
        echo json_encode($feedBackTO);
    }
    else {
        echo json_encode($activeJobBO->getDistricts());
    }
    exit();
}

function updateDistrict(){
    $district = fillInDistrict();
    if (isset($_GET['id'])){
        $district->uniqueId = $_GET['id'];
    }
    $feedBackTO = new FeedBackTO();
    if (!isset($district->uniqueId)){
        $feedBackTO->success = false;
        $feedBackTO->errorMsg = 'District Id Not Found!';
        echo json_encode($feedBackTO);
    }
    else {
        $feedBackTO->success = false;
        $feedBackTO = validateDistrict($district);
        if ($feedBackTO->success) {
            $activeJobBO = new ActiveJobBO(getProfile());
            $feedBackTO = $activeJobBO->updateDistrict($district);
            if ($feedBackTO->success) {
                echo json_encode($activeJobBO->getDistricts());
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

function deleteDistrict()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $activeJobBO = new ActiveJobBO(getProfile());
        $feedBackTO = $activeJobBO->deleteDistrict($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of district not filled in";
    }
    echo json_encode($feedBackTO);
}
