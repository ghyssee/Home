<?php
include_once("../setup.php");

include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "WordBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");

$file = getFullPath(JSON_MP3PRETTIFIER);

$method = htmlspecialchars($_REQUEST['method']);
if (isset($_REQUEST['type'])){
    $type = htmlspecialchars($_REQUEST['type']);
}
if (isset($_REQUEST['category'])) {
    $category = htmlspecialchars($_REQUEST['category']);
}
try {
    switch ($method) {
        case "getListArtists":
            getListArtists();
            break;
        case "addArtist":
            addArtist();
            break;
        case "updateArtist":
            updateArtist();
            break;
        case "deleteArtist":
            deleteArtist();
            break;
        case "list":
            getList($type, $category);
            break;
        case "update":
            update($file, $type, $category);
            break;
        case "add":
            add($file, $type, $category);
            break;
        case "delete":
            delete($type, $category);
            break;
        case "listMultiArtists":
            listMultiArtists();
            break;
        case "listArtists":
            listArtists();
            break;
        case "listSplitters":
            listSplitters();
            break;
        case "deleteMultiArtist":
            deleteMultiArtist();
        case "saveMulti":
            saveMulti();
            break;
        case "updateMultiArtist":
            updateMultiArtist();
            break;
    }
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}

function getListArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $artists = readJSONWithCode(JSON_ARTISTS);
    //if (isset($_POST['sort'])){
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'name';
    $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($artists->list, "name", $order);
    $artists->list = $array;
    //}
    $array = array_slice($artists->list, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($artists->list);
    $result["rows"] = $array;
    echo json_encode($result);
}


function getList($type, $category){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3Prettifier = readJSONWithCode(JSON_MP3PRETTIFIER);
    //if (isset($_POST['sort'])){
        $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'oldWord';
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($mp3Prettifier->{$type}->{$category}, $field, $order);
        $mp3Prettifier->{$type}->{$category} = $array;
    //}
    $array = array_slice($mp3Prettifier->{$type}->{$category}, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($mp3Prettifier->{$type}->{$category});
    $result["rows"] = $array;
    echo json_encode($result);
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function isExtWord($type, $category){
    /*
    if ($type == "song" && $category == "replacements"){
        return true;
    }
    else if ($type == "artist" && $category == "names"){
        return true;
    }*/
    return true;

    }

function update($file, $type, $category){
    $id = $_REQUEST['id'];
    $obj = readJSON($file);
    if (isExtWord($type, $category)){
        $word = new ExtWord();
        assignCheckbox($word->parenthesis, "parenthesis", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->exactMatch, "exactMatch", !HTML_SPECIAL_CHAR);
        if (isset($_POST["priority"])){
            $word->priority = (int) $_POST["priority"];
        }
    }
    else {
        $word = new Word();
    }
    assignField($word->oldWord, "oldWord", !HTML_SPECIAL_CHAR);
    assignField($word->newWord, "newWord", !HTML_SPECIAL_CHAR);
    $word->id = $id;
    $save = true;
    if (objectExist($obj->{$type}->{$category}, "oldWord", $word->oldWord, true, "id", $word->id)) {
        //addError('colorCode', "Color Code already exist: " . $color->code);
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->saveGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function add($file, $type, $category)
{

    $obj = readJSON($file);
    if (isExtWord($type, $category)){
        $word = new ExtWord();
        assignCheckbox($word->parenthesis, "parenthesis", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->exactMatch, "exactMatch", !HTML_SPECIAL_CHAR);
    }
    else {
        $word = new Word();
    }
    if (isset($_POST["priority"])){
        $word->priority = (int) $_POST["priority"];
    }

    assignField($word->oldWord, "oldWord", !ESCAPE_HTML);
    assignField($word->newWord, "newWord", !ESCAPE_HTML);
    $save = true;
    If (objectExist($obj->{$type}->{$category}, "oldWord", $word->oldWord, true)) {
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->addGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function delete($type, $category)
{
    $id = $_REQUEST['id'];
    $wordBO = new WordBO();
    $success = $wordBO->deleteGlobalWord($id, 'id', $type, $category);
    echo json_encode(array('success'=>$success));
}

function updateArtist(){
    $id = $_REQUEST['id'];
    $obj = readJSONWithCode(JSON_ARTISTS);
    $artist = new Artist();
    assignField($artist->name, "name", !HTML_SPECIAL_CHAR);
    $artist->id = $id;
    $save = true;
    if (objectExist($obj->list, "name", $artist->name, true, "id", $artist->id)) {
        //addError('colorCode', "Color Code already exist: " . $color->code);
        $errors = addErrorMsg('Artist already exist: ' . $artist->name);
        $save = false;
    }
    if ($save) {
        $artistBO = new ArtistBO();
        $artistBO->saveArtist($artist);
        $items = array();
        array_push($items, $artist);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function addArtist()
{

    $obj = readJSONWithCode(JSON_ARTISTS);
    $artist = new Artist();
    assignField($artist->name, "name", !ESCAPE_HTML);
    $save = true;
    If (objectExist($obj->list, "name", $artist->name, true)) {
        $errors = addErrorMsg('Artist already exist: ' . $artist->name);
        $save = false;
    }
    if ($save) {
        $artistBO = new ArtistBO();
        $artistBO->addArtist($artist);
        $items = array();
        array_push($items, $artist);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function deleteArtist()
{
    $id = $_REQUEST['id'];
    $artistBO = new ArtistBO();
    $success = $artistBO->deleteArtist($id);
    echo json_encode(array('success'=>$success));
}

function listMultiArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $multi = readJSONWithCode(JSON_MULTIARTIST);
    $artistBO = new ArtistBO();
    $artists = readJSONWithCode(JSON_ARTISTS);

    foreach ($multi->list as $key => $value) {
        $artistList = "";
        foreach($value->artists as $artistItem) {
            $artistObj = $artistBO->lookupArtist($artists->list, $artistItem->id);
            $artistList .= $artistObj->name . "|";
        }
        $value->description = $artistList;
        $artistNewSeq = "";
        foreach($value->artistSequence as $artistItem) {
            $artistObj = $artistBO->lookupArtist($artists->list, $artistItem->artistId);
            $artistNewSeq .= $artistObj->name;
            $splitter = lookupSplitter($multi->splitters, $artistItem->splitterId);
            $artistNewSeq .= $splitter->id == $multi->splitterEndId ? "" : $splitter->value2;
        }
        $value->description2 = $artistNewSeq;
    }

    //if (isset($_POST['sort'])){
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'description';
    if ($field != 'description') {
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($multi->list, $field, $order);
    }
    else {
        $array = $multi->list;
    }
    //}
    $array = array_slice($array, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($multi->list);
    $result["rows"] = $array;
    echo json_encode($result);
}

function lookupSplitter ($splitters, $id){
    foreach ($splitters as $key => $value) {
        if ($value->id == $id){
            return $value;
        }
    }
    return null;

}

function listArtists(){
    $artists = readJSONWithCode(JSON_ARTISTS);
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($artists->list, "name");
    echo json_encode($array);
}

function listSplitters(){
    $multiArtist = readJSONWithCode(JSON_MULTIARTIST);
    echo json_encode($multiArtist->splitters);

}

function saveMulti(){
    $multiArtist = readJSONWithCode(JSON_MULTIARTIST);
    $success = false;
    $msg = '';
    if (isset($_POST['config'])){
        $config = json_decode($_POST['config']);
        $saveConfig = new MultiArtist();
        $saveConfig->id = getUniqueId();
        $multiArtistLine = new MultiArtist();
        $multiArtistLine->id = getUniqueId();
        $multiArtistLine->exactPosition = $config->exactPosition;
        $multiArtistLine->master = $config->master;
        foreach ($config->artists as $value){
            $multiArtistLine->artists[] = new ArtistItem($value->id);
        }
        foreach ($config->artistSequence as $value){
            $multiArtistLine->artistSequence[] = new ArtistSequence($value->artistId, $value->splitterId);
        }
        $existAlready = checkMultiArtistConfigExist($multiArtist, $multiArtistLine);
        if ($existAlready){
            $success = false;
            $msg = 'Multi Artist Config exist already';
        }
        else {
            $multiArtist->list[] = $multiArtistLine;
            writeJSONWithCode($multiArtist, JSON_MULTIARTIST);
            $success = true;
        }
    }
    else {
        $success = false;
    }
    echo json_encode(array('success'=>$success,'message'=>$msg));

}

function checkMultiArtistConfigExist($multiArtist, $multiArtistLine){
    $nrOfItems = count($multiArtistLine->artists);
    foreach($multiArtist->list as $multiArtistItem){
        if (count($multiArtistItem->artists) == $nrOfItems){
            $diff = array_udiff($multiArtistItem->artists, $multiArtistLine->artists,
                function ($obj_a, $obj_b) {
                    return strcmp($obj_a->id, $obj_b->id) ;
                }
            );
            if (count($diff) == 0){
                return true;
            }
        }
    }
    return false;
}

function deleteMultiArtist(){
    $id = $_REQUEST['id'];
    $artistBO = new ArtistBO();
    $success = $artistBO->deleteMultiAristConfig($id);
    echo json_encode(array('success'=>$success));
}

function updateMultiArtist(){
    $multiArtist = new MultiArtist();
    $multiArtist->id = $_REQUEST['id'];
    assignCheckbox($multiArtist->exactPosition, "exactPosition", !HTML_SPECIAL_CHAR);
    $artistBO = new ArtistBO();
    $success = $artistBO->saveMultiAristConfig($multiArtist);
    if ($success) {
        echo json_encode(array('success' => true));
    }
    else {
        echo json_encode(addErrorMsg("There was a problem updating the Multi Artist with ID " . $multiArtist->id));
    }
}

?>

