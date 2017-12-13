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
include_once documentPath (ROOT_PHP_MR_BO, "SettingsBO.php");
sessionStart();
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
//set_error_handler("customError",E_ALL);

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "set1":
            getProfiles();
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

function getProfiles(){
    foreach($_POST as $key => $value){
        $tmp = $key;
        $tmp2 = $value;
        $tmp3 = '';
    }
    $tmp = new FightSettingsTO();
    $tmpVar = get_object_vars($tmp);
    foreach($tmpVar as $key => $value){
        $bla = '';
        $bla2 = '';
        $typeof = gettype($value);
        if ($typeof == 'boolean'){
            $tmp->{$key} = isset($_POST[$key]);
        }
        elseif ($typeof == 'integer') {
            if (isset($_POST[$key])) {
                $tmp->{$key} = intval($_POST[$key]);
            }
        }
        else {

        }
    }

    $profileBO = new ProfileBO();
    $list = $profileBO->getProfiles();
    $sort = new CustomSort();
    $list = $sort->sortObjectArrayByField($list, "id");

    array_unshift($list, $profileBO->getEmptyProfile());
    echo json_encode($list);
}
