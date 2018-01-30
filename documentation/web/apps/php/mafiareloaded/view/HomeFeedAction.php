<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 11/12/2017
 * Time: 13:00
 */
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "HomeFeedBO.php");
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
            getListHomeFeed();
            break;
        case "delete":
            deleteHomeFeedLine();
            break;
        case "cleanup":
            cleanupHomefeed();
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

function getListHomeFeed(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $homeFeedBO = new HomeFeedBO(getProfile());
    $list = $homeFeedBO->getHomeFeed();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "timeStamp", "desc");
    $array = array_slice($list, ($page-1)*$rows, $rows);
    $result["total"] = count($list);
    $result["rows"] = $array;
    echo json_encode($result);
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

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function cleanupHomefeed(){
    $date = DateTime::createFromFormat('YmdHis', '20171122201621');
    $date = $date->sub(new DateInterval("P31D"));
    $homeFeedBO = new HomeFeedBO(getProfile());
    $feedbackTO = $homeFeedBO->cleanupHomefeed();
    echo json_encode($feedbackTO);
}

function deleteHomeFeedLine()
{
    $feedBackTO = new FeedBackTO();
    $feedBackTO->success = false;
    if (isset($_REQUEST['id'])){
        $id = $_REQUEST['id'];
        $homefeedBO = new HomeFeedBO(getProfile());
        $feedBackTO = $homefeedBO->deleteHomefeedLine($id);
    }
    else {
        $feedBackTO->errorMsg = "Id of Homefeed Line not filled in";
    }
    echo json_encode($feedBackTO);
}
