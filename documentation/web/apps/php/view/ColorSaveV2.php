<?php
include("../config.php");
include("../model/HTML.php");
include("../bo/ColorBO.php");
include("../html/config.php");
include("../form/FormUtils.php");
session_start();
$file = $oneDrivePath . '/Config/Java/HTML.json';
if (isset($_POST['htmlSettings'])) {
    $button = $_POST['htmlSettings'];
    if ($button == "addColor") {
        addColor($file);
    }
    else if ($button == "saveColor") {
        saveColor($file);
    }
    else if ($button == "back") {
        header("Location: " . $_SESSION["form_location"]);
        exit();
    }
}
else {
    $formMode = getFormMode();
    if ($formMode == 'D') {
        $id = htmlspecialchars($_GET["id"]);
        if (empty($id)){
            exit("ERROR_0001: Color Id was not set!!!");
        }
        deleteColor($id);
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
        addInfo("Color", "Color Added: " . $color->description);
        $color->id = getUniqueId();
        array_push($htmlObj->colors, $color);
        writeJSON($htmlObj, $file);
        header("Location: " . $_SESSION["previous_location"]);
    } else {
        $_SESSION["color"] = $color;
        header("Location: " . $_SESSION["previous_location"]);
    }
    exit();
}

function saveColor($file)
{
    $htmlObj = initSave($file);
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
    } elseif (objectExist($htmlObj->colors, "code", $color->code, false, "id", $color->id)) {
        addError('colorCode', "Color Code already exist: " . $color->code);
        $save = false;
    }
    if ($save) {
        //addInfo("Color", "Modifications were updated for id " . $color->id);
        $colorBO = new ColorBO();
        $colorBO->saveColor($color);
        header("Location: " . $_SESSION["form_location"]);
    }
    else {
        $_SESSION["color"] = $color;
        header("Location: " . $_SESSION["previous_location"]);
    }
     exit();
}


function deleteColor($id)
{
    $colorBO = new ColorBO();
    if (!$colorBO->deleteColor($id)){
        exit("ERROR_0002: Problem Deleting Color With ID: " + $id);
    }
    header("Location: " . $_SESSION["previous_location"]);
    exit();
}
?>
