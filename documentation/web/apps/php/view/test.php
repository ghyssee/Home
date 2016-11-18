<?php
include("../config.php");
include("../model/HTML.php");
include("../bo/ColorBO.php");

$method = htmlspecialchars($_REQUEST['method']);
$file = $GLOBALS['oneDrivePath'] . '/Config/Java/test.txt';
switch ($method) {
	case "list":
		getList();
		break;
	case "update":
		update($file);
		break;
	case "cake":
		break;
}

function getList(){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $htmlObj = readJSON($GLOBALS['oneDrivePath'] . '/Config/Java/HTML.json');
    $array = array_slice($htmlObj->colors, ($page-1)*$rows, $rows);
    
    $result = array();
    $result["total"] = 14;
    $result["rows"] = $array;

	echo json_encode($result);

}

function update($file){
	$id = $_REQUEST['id'];
	$htmlObj = readJSON($GLOBALS['oneDrivePath'] . '/Config/Java/HTML.json');
	$color = new Color();
	assignField($color->description, "description");
	assignField($color->code, "code");
	$color->id = $id;
	$save = true;
	if (objectExist($htmlObj->colors, "code", $color->code, false, "id", $color->id)) {
		addError('colorCode', "Color Code already exist: " . $color->code);
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
		write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
		echo json_encode(array('errorMsg'=>'Some errors occured.'));
	}
}

?>