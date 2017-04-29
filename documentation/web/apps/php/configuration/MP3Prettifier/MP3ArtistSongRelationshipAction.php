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
        case "listMultiArtists":
            listMultiArtists();
            break;
        case "addMulti":
            addMultiArtist();
            break;
        case "batch":
            batchProcess();
        default :
            addMultiArtist2();
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

function listMultiArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 40;
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

function addMultiArtist(){
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $multiArtistBO = new MultiArtistBO();
        $result = $multiArtistBO->addMultiAristConfig($config->multiArtistConfig);
        echo json_encode($result);
    }
    else {
        echo json_encode(array('success' => false, 'message' => 'Config Object Not Found'));
    }
}

function addMultiArtist2(){
    $line = "Zucchero & Ilse DeLange";
    $multiArtistBO = new MultiArtistBO();
    $result = $multiArtistBO->addMultiAristConfig($line);
    if ($result->errorFound){
        println($result->errorMsg);
    }
    else {
        foreach ($result->artistsAdded as $item) {
            println($item);
        }
        println("ID: " . $result->multiArtist->id);
    }
}

function batchProcess(){
    $text = trim($_POST['multiArtistList']);
    $values = preg_split('/[\n\r]+/', $text);

    foreach ($values as $line) {
        $tmp = trim($line);
        $tmp2 = $line;
    }
    echo json_encode(array('success' => true));
}

?>
