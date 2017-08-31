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
    $artistSongRelationShipTO = new ArtistSongRelationshipTO();
    if (empty($config->oldArtistType)){
        $errorObj->success = false;
        $errorObj->errorMsg = "Old Artist Type Not Selected";
        return $errorObj;
    }
    switch ($config->oldArtistType){
        case "01":
            if (empty($config->oldArtists)){
                $errorObj->success = false;
                $errorObj->errorMsg = "No Old Artists Added";
                return $errorObj;
            }
            $list = [];
            foreach ($config->oldArtists as $item){
                $list[] =  new ArtistItemTO($item->id);
            }
            $artistSongRelationShipTO->oldArtistList = $list;
            break;
        case "02":
            if (empty($config->oldMultiArtist)){
                $errorObj->success = false;
                $errorObj->errorMsg = "Old Multi Artist is empty";
                return $errorObj;
            }
            $artistSongRelationShipTO->oldMultiArtistId = $config->oldMultiArtist;
            break;
        case "03":
            if (empty($config->oldFreeArtist)){
                $errorObj->success = false;
                $errorObj->errorMsg = "Old Free Artist is empty";
                return $errorObj;
            }
            break;
        default:
            $errorObj->success = false;
            $errorObj->errorMsg = "Undefined Old Artist Type: " & $config->oldArtistType;
            return $errorObj;
    }
    if (empty($config->newArtistType)){
        $errorObj->success = false;
        $errorObj->errorMsg = "New Artist Type Not Selected";
        return $errorObj;
    }
    switch ($config->newArtistType) {
        case "01":
            if (empty($config->newArtist)){
                $errorObj->success = false;
                $errorObj->errorMsg = "No New Artist Selected";
                return $errorObj;
            }
            $artistSongRelationShipTO->newArtistId = $config->newArtist->id;
            break;
        case "02":
            if (empty($config->newMultiArtist)){
                $errorObj->success = false;
                $errorObj->errorMsg = "No New Multi Artist Selected";
                $artistSongRelationShipTO->newMultiArtistId = $config->newMultiArtist;
                return $errorObj;
            }
            $artistSongRelationShipTO->newMultiArtistId = $config->newMultiArtist;
            break;
        default:
            $errorObj->success = false;
            $errorObj->errorMsg = "Undefined New Artist Type: " & $config->newArtistType;
            return $errorObj;
    }
    if (empty($config->oldSong)){
        $errorObj->success = false;
        $errorObj->errorMsg = "Old Song is empty";
        return $errorObj;
    }
    else {
        $artistSongRelationShipTO->oldSong = $config->oldSong;
    }
    if (empty($config->newSong)){
        $errorObj->success = false;
        $errorObj->errorMsg = "New Song is empty";
        return $errorObj;
    }
    else {
        $artistSongRelationShipTO->newSong = $config->newSong;
    }
    $errorObj->success = true;
    $artistSongRelationShipBO = new ArtistSongRelationShipBO();
    //$artistSongRelationShipBO->addArtistSong($artistSongRelationShipTO);
    return $errorObj;
}

function addArtistSong(){
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $errorObj = validateArtistSong($config);
        echo json_encode($errorObj);
    }
    else {
        echo json_encode(array('success' => false, 'message' => 'Config Object Not Found'));
    }
}

?>
