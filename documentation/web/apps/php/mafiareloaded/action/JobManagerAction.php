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

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "list":
            getListScheduledJobs();
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