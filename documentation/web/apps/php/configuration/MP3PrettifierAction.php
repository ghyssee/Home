<?php
include_once("../setup.php");

include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
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
        case "listArtists":
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
    }
}
catch(Error $e) {
    echo $e->getMessage();
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
    //if (isset($_POST['sort'])){
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'description';
    $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
    $sort = new CustomSort();
//    $array = $sort->sortObjectArrayByField($multi->list, "description", $order);
//    $multi->list = $array;
    $array = $multi->list;
    //}
    $array = array_slice($multi->list, ($page-1)*$rows, $rows);
    $artistBO = new ArtistBO();
    $artistList = "";
    $artists = readJSONWithCode(JSON_ARTISTS);

    foreach ($array as $key => $value) {
        $artistList = "";
        foreach($value->artists as $artistItem) {
            $artistObj = $artistBO->lookupArtist($artists->list, $artistItem->id);
            $artistList .= $artistObj->name . "|";
        }
        $value->description = $artistList;
    }

    $result = array();
    $result["total"] = count($multi->list);
    $result["rows"] = $array;
    echo json_encode($result);
}



?>