<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../bo/ColorBO.php");

$method = htmlspecialchars($_REQUEST['method']);
$file = getFullPath(JSON_ALBUMERRORS);
switch ($method) {
	case "list":
		getList($file);
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

function getList($file){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $htmlObj = readJSON($file);
    $array = array_slice($htmlObj->items, ($page-1)*$rows, $rows);
    
    $result = array();
    $result["total"] = count($htmlObj->items);
    $result["rows"] = $array;

	echo json_encode($result);

}

function addErrorMsg($msg){
	return array('errorMsg'=>$msg);
}

function update($file){
	$id = $_REQUEST['id'];
	$htmlObj = readJSON($file);
	$color = new Color();
	assignField($color->description, "description");
	assignField($color->code, "code");
	$color->id = $id;
	$save = true;
	if (objectExist($htmlObj->colors, "code", $color->code, false, "id", $color->id)) {
		//addError('colorCode', "Color Code already exist: " . $color->code);
		$errors = addErrorMsg('Color Code already exist: ' . $color->code);
		$save = false;
	}
	if ($save) {
		//addInfo("Color", "Modifications were updated for id " . $color->id);
		$colorBO = new ColorBO();
        $colorBO->saveColor($color);
        $items = array();
        array_push($items, $color);
        echo json_encode($items);
	}
	else {
		//write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
		echo json_encode($errors);
	}
}

function add($file)
{

	$htmlObj = readJSON($file);
	$color = new Color();

	assignField($color->description, "description");
	assignField($color->code, "code");
	$save = true;
	If (objectExist($htmlObj->colors, "code", $color->code, false)) {
		$errors = addErrorMsg('Color Code already exist: ' . $color->code);
		$save = false;
	}
	if ($save) {
		$colorBO = new ColorBO();
		$colorBO->addColor($color);
		$items = array();
		array_push($items, $color);
		echo json_encode($items);
	} else {
		echo json_encode($errors);
	}
	exit();
}

function delete($file)
{
	$id = $_REQUEST['id'];
	$colorBO = new ColorBO();
	$success = $colorBO->deleteColor($id);
	echo json_encode(array('success'=>$success));
}


?>