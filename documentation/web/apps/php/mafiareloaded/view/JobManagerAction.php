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
    $activeJobBO = new ActiveJobBO();
    $list = $activeJobBO->getDistrictsForComboBox();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "description");
    echo json_encode($list);

}

function getJobs(){
    $activeJobBO = new ActiveJobBO();
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
    $activeJobBO = new ActiveJobBO();
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
    $activeJobBO = new ActiveJobBO();
    $list = $activeJobBO->getJobTypes();
    echo json_encode($list);

}

function getListScheduledJobs()
{
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $activeJobBO = new ActiveJobBO();
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