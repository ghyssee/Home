<?php
include("../config.php");
include("../model/HTML.php");
include("../bo/ColorBO.php");
include("../html/config.php");
session_start();
if (isset($_POST['htmlSettings'])) {
    $button = $_POST['htmlSettings'];
    if ($button == "addColor") {
        $file = $oneDrivePath . '/Config/Java/HTML.json';
        addColor($file);
    }
    if ($button == "saveColor") {
        $file = $oneDrivePath . '/Config/Java/HTML.json';
        saveColor($file);
    }
}

function addColor($file)
{

    $htmlObj = initSave($file);
    $color = new Color();

    assignField($color->description, "colorDescription");
    assignField($color->code, "colorCode");
    $save = true;
    if (empty($color->description)) {
        addError('colorDescription', "Color Description can't be empty");
        $save = false;
    }
    if (empty($color->code)) {
        addError('colorCode', "Color code can't be empty");
        $save = false;
    } elseif (objectExist($htmlObj->colors, "code", $color->code, false)) {
        addError('colorCode', "Color Code already exist: " . $color->code);
        $save = false;
    }
    if ($save) {
        println("<h1>HTML Color</h1>");
        array_push($htmlObj->colors, $color);
        println('Contents saved to ' . $file);
        writeJSON($htmlObj, $file);
    } else {
        $_SESSION["color"] = $color;
        header("Location: " . $_SESSION["previous_location"]);
        exit();
    }
}

function saveColor($file)
{
    $color = new Color();
    assignField($color->description, "colorDescription");
    assignField($color->code, "colorCode");
    assignField($color->id, "id");
    $save = true;
    if (empty($color->description)) {
        addError('colorDescription', "Color Description can't be empty");
        $save = false;
    }
    if (empty($color->code)) {
        addError('colorCode', "Color code can't be empty");
        $save = false;
    }
    if ($save) {
        addInfo("Color", "Modifications were updated for id " . $color->id);
        $colorBO = new ColorBO();
        $colorBO->saveColor($color);
        header("Location: " . "settings.php");
    }
    else {
        $_SESSION["color"] = $color;
        header("Location: " . $_SESSION["previous_location"]);
    }
     exit();
}

?>
