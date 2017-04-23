<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../bo/ColorBO.php");
//include_once("../html/config.php");

$file = getFullPath(JSON_ALBUMERRORS);

$method = htmlspecialchars($_REQUEST['method']);

try {
	switch ($method) {
		case "updateSettings":
			updateSettings();
		case "list":
			getList();
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
		case "select":
			$sel = json_decode($_POST['selectedIds']);
			selectRows($sel);
			break;
	}
}
catch(Error $e) {
	echo $e->getMessage();
}
function updateSettings(){
    session_start();
    $mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
	assignField($mp3SettingsObj->mezzmo->mp3Checker->status, "albumErrorsStatus");
	assignNumber($mp3SettingsObj->mezzmo->mp3Checker->maxNumberOfErrors, "maxNumberOfErrors");
	writeJSONWithCode($mp3SettingsObj, JSON_MP3SETTINGS);
	//addInfo("mp3SettingsObj", "Contents saved to '" . getFullPath(JSON_MP3SETTINGS));
	header("Location: " . $_SESSION["previous_location"]);
}

function selectRows($selObj){
    $albumErrors = readJSONWithCode(JSON_ALBUMERRORS);
    foreach ($albumErrors->items as $key => $record) {
        if (!$record->done) {
            $record->process = false;
            foreach ($selObj->ids as $id) {
                if ($id == $record->uniqueId){
                    $record->process = true;
                }
            }
        }
    }
    writeJSONWithCode($albumErrors, JSON_ALBUMERRORS);
	echo json_encode(array('success'=>true));

}

function selectRows2($selArray){
	$albumErrors = readJSONWithCode(JSON_ALBUMERRORS);
	foreach ($albumErrors->items as $key => $record) {
		if (!$record->done) {
			$record->process = false;
			foreach ($selArray as $id) {
				if ($id == $record->uniqueId){
					$record->process = true;
				}
			}
		}
	}
	writeJSONWithCode($albumErrors, JSON_ALBUMERRORS);
	echo json_encode(array('success'=>true));

}


function getList(){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $albumErrors = readJSONWithCode(JSON_ALBUMERRORS);
    $filteredArray = [];
	foreach ($albumErrors->items as $key => $value) {
		if (!$value->done) { //} && !$value->process) {
			array_push($filteredArray, $value);
		}
	}

	if (isset($_POST['sort'])){
		$field = isset($_POST['sort']) ? strval($_POST['sort']) : '';
		$order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
		$sort = new CustomSort();
		$array = $sort->sortObjectArrayByField($filteredArray, $field, $order);
		$filteredArray = $array;
	}
	$array2 = array_slice($filteredArray, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($filteredArray);
    $result["rows"] = $array2;

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