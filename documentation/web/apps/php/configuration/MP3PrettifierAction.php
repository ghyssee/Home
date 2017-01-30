<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../bo/WordBO.php");

$file = getFullPath(JSON_MP3PRETTIFIER);

$method = htmlspecialchars($_REQUEST['method']);
$cat1 = htmlspecialchars($_REQUEST['cat1']);
$cat2 = htmlspecialchars($_REQUEST['cat2']);

switch ($method) {
    case "list":
        getList($cat1, $cat2);
        break;
    case "update":
        update($file);
        break;
    case "add":
        add($file);
        break;
    case "delete":
        delete($file);
        break;
}

function getList($cat1, $cat2){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3Prettifier = readJSONWithCode(JSON_MP3PRETTIFIER);
    if (isset($_POST['sort'])){
        $field = strval($_POST['sort']);
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($mp3Prettifier->{$cat1}->{$cat2}, $field, $order);
        $mp3Prettifier->global->words = $array;
    }
    $array = array_slice($mp3Prettifier->{$cat1}->{$cat2}, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($mp3Prettifier->{$cat1}->{$cat2});
    $result["rows"] = $array;
    echo json_encode($result);
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function update($file){
    $id = $_REQUEST['id'];
    $obj = readJSON($file);
    $word = new Word();
    assignField($word->oldWord, "oldWord", !HTML_SPECIAL_CHAR);
    assignField($word->newWord, "newWord", !HTML_SPECIAL_CHAR);
    $word->id = $id;
    $save = true;
    $test = $obj->{"global"}->{"words"};
    if (objectExist($obj->global->words, "oldWord", $word->oldWord, true, "id", $word->id)) {
        //addError('colorCode', "Color Code already exist: " . $color->code);
        $errors = addErrorMsg('Global Word already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->saveGlobalWord($word);
        $items = array();
        array_push($items, $word);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function add($file)
{

    $obj = readJSON($file);
    $word = new Word();

    assignField($word->oldWord, "oldWord", !ESCAPE_HTML);
    assignField($word->newWord, "newWord", !ESCAPE_HTML);
    $save = true;
    If (objectExist($obj->{'global'}->{'words'}, "oldWord", $word->oldWord, false)) {
        $errors = addErrorMsg('Global Word already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->addGlobalWord($word);
        $items = array();
        array_push($items, $word);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function delete($file)
{
    $id = $_REQUEST['id'];
    $wordBO = new WordBO();
    $success = $wordBO->deleteGlobalWord($id);
    echo json_encode(array('success'=>$success));
}


?>