<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
?>
<?php
session_start(); 
if(isset($_POST['htmlSettings'])){
	$button = $_POST['htmlSettings'];
	if ($button == "addColor"){
		$file = $oneDrivePath . '/Config/Java/HTML.json';
		//saveMP3Preprocessor($file, $mp3PreprocessorObj);
		addColor($file);
	}
}

function addColor($file){

	println("<h1>HTML Color</h1>");
	$htmlObj = initSave($file);
	$color = new Color();

	assignField($color->description, "colorDescription");
	assignField($color->code, "colorCode");
	$save = true;
	//$color = new Color($description, $code);
	$errors = array();
	if (empty($color->description)) {
		addError('htmlSettings', 'description', "Color Description can't be empty");
		$save = false;
	}
	if (empty($color->code)) {
		addError('htmlSettings', 'code', "Color code can't be empty");
		$save = false;
	}
	if (objectExist($htmlObj->colors, "code", $color->code, false)){
		addError('htmlSettings','code', "Color Code already exist: " . $color->code);
		$save = false;
	}
	if ($save) {
		array_push ($htmlObj->colors, $color);
		println ('Contents saved to ' . $file);
		//writeJSON($htmlObj, $file);
	}
	else{
		$_SESSION["color"] = $color;
		header("Location: " . $_SESSION["previous_location"]);
		exit();
	}
}

?>
