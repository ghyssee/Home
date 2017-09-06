<?php
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "ArtistSongRelationshipBO.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
sessionStart();

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "listArtistSong":
            listArtistSong();
            break;
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

function listArtistSong(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
//    if (CacheBO::isInCache(CacheBO::MULTIARTIST2)){
    //       $list = CacheBO::getObject(CacheBO::MULTIARTIST2);
    //  }
    // else {
    $artistSongRelationshipBO = new ArtistSongRelationshipBO();
    //$artistSongRel = $artistSongRelationshipBO->getArtistSongRelationshipList();
    //$list = Array();
    $list = $artistSongRelationshipBO->loadFullData();

    $newArray = $list;

    //if (isset($_POST['sort'])){
    //$field = isset($_POST['sort']) ? strval($_POST['sort']) : 'oldSong';
    if (isset($_POST['sort'])) {
        $field = $_POST['sort'];
        $order = isset($_POST['order']) ? $_POST['order'] : 'asc';
        $sort = new CustomSort();
        //$array = $sort->sortObjectArrayByField($multi->list, $field, $order);
        $array = $sort->sortObjectArrayByField($newArray, $field, $order);
    }
    else {
        //$array = $multi->list;
        $array = $newArray;
    }
    //}
    $array = array_slice($array, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($newArray);
    $result["rows"] = $array;
    echo json_encode($result);
}

function validateArtistSong($config){
    $errorObj = new stdClass();
    $artistSongRelationShipTO = new ArtistSongRelationshipTO();
    $artistSongRelationShipTO->id = $config->id;
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
                $artistItemTO = new ArtistItemTO();
                if (isset($item->id)){
                    $artistItemTO->id = $item->id;
                }
                else {
                    $artistItemTO->text = $item->name;
                }
                $list[] =  $artistItemTO;
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
    if (isset($artistSongRelationShipTO->id) && !empty($artistSongRelationShipTO->id)){
        $artistSongRelationShipBO->saveArtistSong($artistSongRelationShipTO);
    }
    else {
        $artistSongRelationShipBO->addArtistSong($artistSongRelationShipTO);
    }
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
