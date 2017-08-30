<?php
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "ArtistSongRelationshipBO.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
sessionStart();

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "add":
            addArtistSong();
            break;
        case "addMulti":
            break;
        default :
            break;
    }
}
catch(Exception $e){
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function validateArtistSong($config){
    $errorObj = new stdClass();
    if (empty($config->oldArtistType)){
        $errorObj->success = false;
        $errorObj->errorMessage = "Old Artist Type Not Selected";
        return $errorObj;
    }
    switch ($config->oldArtistType){
        case "01":
            break;
        case "02":
            break;
        case "03":
            break;
        default:
            $errorObj->success = false;
            $errorObj->errorMessage = "Undefined Old Artist Type: " & $config->oldArtistType;
            return $errorObj;
    }
    $errorObj->success = true;
}

function addArtistSong(){
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $errorObj = new stdClass();
        $errorObj->success = true;
        echo json_encode($errorObj);
    }
    else {
        echo json_encode(array('success' => false, 'message' => 'Config Object Not Found'));
    }
}

?>
