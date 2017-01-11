<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include_once("../bo/MP3PreprocessorBO.php");
session_start();
if (isset($_POST['mp3Preprocessor'])) {
    $button = $_POST['mp3Preprocessor'];
    if ($button == "addSplitter") {
        addSplitter($file);
    }
    if ($button == "addConfig") {
        addConfig($file);
    }
}
if (isset($_REQUEST['method'])){
    $method = htmlspecialchars($_REQUEST['method']);
    switch ($method) {
        case "listConfigurations":
            $id = "1";
            if (isset($_REQUEST["id"])) {
                $id = $_REQUEST["id"];
            }
            getListConfigurations($id);
            break;
        case "list":
            getList();
            break;
        case "update":
            update();
            break;
        case "add":
            add();
            break;
        case "delete":
            delete();
            break;
        case "updateConfig":
            $sel = json_decode($_POST['selectedRow']);
            //updateConfig($sel);
            break;
        case "init":
            $id = $_REQUEST["id"];
            init($id);
            break;
    }
}

function init($id){
    $_SESSION["CONFIG_ID"] = $id;
    header("Location: " . $_SESSION["previous_location"]);
}

function updateConfig($sel){
    /* contains 3 elements
    index : the index of the selected row
    row : the row elements
    configId : the id of the configuration that is selected
    */
}

function getList(){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
    if (isset($_POST['sort'])){
        $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'description';
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($mp3PreprocessorObj->splitters, $field, $order);
        $mp3PreprocessorObj->splitters = $array;
    }
    $array = array_slice($mp3PreprocessorObj->splitters, ($page-1)*$rows, $rows);
    $result = array();
    $result["total"] = count($mp3PreprocessorObj->splitters);
    $result["rows"] = $array;

    echo json_encode($result);

}

function findConfigurationById($configurations, $id){
    foreach ($configurations as $key => $value) {
        if (strcmp($value->id, $id) == 0) {
            return $value->config;
        }
    }
    return null;


}

function getListConfigurations($id){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
    $config = findConfigurationById($mp3PreprocessorObj->configurations, $id);
    $array = array_slice($config, ($page-1)*$rows, $rows);
    $result = array();
    $result["total"] = count($config);
    $result["rows"] = $array;
    echo json_encode($result);

}

function add()
{

    $splitter = new Splitter();
    assignField($splitter->description, "description");
    assignField($splitter->pattern, "pattern");
    $save = true;
    if ($save) {
        $mp3PreprocessorBO = new MP3PreprocessorBO();
        $mp3PreprocessorBO->addSplitter($splitter);
        $items = array();
        array_push($items, $splitter);
        echo json_encode($splitter);
    } else {
        echo json_encode(addErrorMsg("Error Occured"));
    }
    exit();
}

function update(){
    $id = $_REQUEST['id'];
    $splitter = new Splitter();
    assignField($splitter->description, "description");
    assignField($splitter->pattern, "pattern");
    $splitter->id = $id;
    $save = true;
    if ($save) {
        $mp3PreprocessorBO = new MP3PreprocessorBO();
        $mp3PreprocessorBO->saveSplitter($splitter);
        $items = array();
        array_push($items, $splitter);
        echo json_encode($items);
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode(addErrorMsg("Error Occured"));
    }
}

function delete()
{
    $id = $_REQUEST['id'];
    $mp3PreprocessorBO = new MP3PreprocessorBO();
    $success = $mp3PreprocessorBO->deleteSplitter($id);
    echo json_encode(array('success'=>$success));

}


function addSplitter($file)
{

    $mp3PreprocesorObj = initSave(JSON_MP3PREPROCESSOR);
    $splitter = new Splitter();

    assignField($splitter->id, "splitterId");
    assignField($splitter->pattern, "pattern");
    $save = true;
    if (empty($splitter->id)) {
        addError('splitterId', "Splitter Id can't be empty");
        $save = false;
    }
    if (empty($splitter->pattern)) {
        addError('pattern', "Pattern can't be empty");
        $save = false;
    } elseif (objectExist($mp3PreprocesorObj->splitters, "pattern", $splitter->pattern, false)) {
        addError('pattern', "Splitter already exist: " . $splitter->pattern);
        $save = false;
    }
    if ($save) {
        println("<h1>MP3Preprocessor Splitter</h1>");
        array_push($mp3PreprocesorObj->splitters, $splitter->pattern);
        println('Contents saved to ' . $file);
        //writeJSON($mp3PreprocesorObj, $file);
    } else {
        $_SESSION["splitter"] = $splitter;
        header("Location: " . $_SESSION["previous_location"]);
        exit();
    }
}

?>