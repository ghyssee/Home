<?php
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
sessionStart();

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "addMulti":
            addMultiArtist();
            break;
        case "saveMulti":
            saveMulti();
            break;
        case "deleteMultiArtist":
            deleteMultiArtist();
            break;
        case "getMultiArtistList":
            getMultiArtistList();
            break;
        case "listMultiArtists":
            listMultiArtists();
            break;
        case "batch":
            batchProcess();
        default :
            //addMultiArtist2();
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

function addMultiArtist(){
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $multiArtistBO = new MultiArtistBO();
        $result = $multiArtistBO->addMultiArtistConfig($config->multiArtistConfig);
        echo json_encode($result);
    }
    else {
        echo json_encode(array('success' => false, 'message' => 'Config Object Not Found'));
    }
}

function saveMulti(){
    $msg = '';
    if (isset($_POST['config'])){
        $config = json_decode($_POST['config']);
        $multiArtistLine = new MultiArtist();
        $multiArtistLine->exactPosition = $config->exactPosition;
        $multiArtistLine->master = $config->master;
        foreach ($config->artists as $value){
            $multiArtistLine->artists[] = new ArtistItem($value->id);
        }
        foreach ($config->artistSequence as $value){
            $multiArtistLine->artistSequence[] = new ArtistSequence($value->artistId, $value->splitterId);
        }
        $multiArtistBO = new MultiArtistBO();
        if (isset($config->id) && $config->id != ''){
            $multiArtistLine->id = $config->id;
            $multiArtistBO->updateMultiArtist($multiArtistLine);
            $success = true;
            $msg = "Multi Artist Configuration Saved!";
        }
        else {
            $multiArtistLine->id = getUniqueId();
            $existAlready = $multiArtistBO->checkMultiArtistConfigExist($multiArtistLine);
            if ($existAlready){
                $success = false;
                $msg = 'Multi Artist Config exist already';
            }
            else {
                $multiArtistBO->addMultiArtist($multiArtistLine);
                $success = true;
                $msg = "Multi Artist Configuration Saved!";
            }
        }
    }
    else {
        $success = false;
    }
    echo json_encode(array('success'=>$success,'message'=>$msg));

}

function deleteMultiArtist(){
    $id = $_REQUEST['id'];
    $multiArtistBO = new MultiArtistBO();
    $success = $multiArtistBO->deleteMultiAristConfig($id);
    $returnObj = array('success' => $success);
    if (!$success) {
        $returnObj['errorMessage'] = "There was a problem trying to delete the MultiArtist!";
    }
    echo json_encode($returnObj);

}

function getMultiArtistList(){
    $multiArtistBO = new MultiArtistBO();
    $newArray = $multiArtistBO->loadFullData();
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($newArray, "description2", "asc");
    echo json_encode($array);
}

function listMultiArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $multiArtistBO = new MultiArtistBO();
    $newArray = $multiArtistBO->loadFullData();

    if (isSetFilterRule()){
        $filterRules = json_decode($_POST["filterRules"]);
        $newArray = $multiArtistBO->filterData($filterRules, $newArray);
    }


    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'description';
    if ($field != 'description') {
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
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

/*
function addMultiArtist2(){
    $line = "Zucchero & Ilse DeLange";
    $multiArtistBO = new MultiArtistBO();
    $result = $multiArtistBO->addMultiArtistConfig($line);
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
*/
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
