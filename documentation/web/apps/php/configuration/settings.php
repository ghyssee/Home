<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body style="background">

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .descriptionColumn {
        width: 20%;
    }
</style>

<?php
goMenu();
if (isset($_SESSION["color"])) {
    $color = $_SESSION["color"];
} else {
    $color = new Color();
}
?>
<h1>Settings</h1>
<div class="horizontalLine">.</div>
<form action="settingsSave.php" method="post">
    <?php
        //<?php errorCheck("description");
        $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "colorDescription",
        'size' => 50,
        'label' => 'Description',
        'value' => $color->description)));
        //<?php errorCheck("code");
    $layout->inputBox(new Input(array('name' => "colorCode",
    'size' => 50,
    'label' => 'Color Code',
    'value' => $color->code)));
    $layout->button(new Input(array('name' => "htmlSettings",
        'value' => 'addColor',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    usort($htmlObj->colors, function ($a, $b){
        return strcmp($a->description, $b->description);
    });
    echo build_table($htmlObj->colors);

    ?>
</form>

<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["color"]);
?>
</body>
</html>

<?php
function build_table($array){
    // start table
    $class = "displayTable";
    $html = addClassToElement("table", array($class, 'displayTable-zebra', 'displayTable-horizontal', 'centerTable'));
    // header row
    $html .= '<caption class="displayTable">Colors</caption>';
    $html .= "<thead>";
    $html .= '<tr>';
    $html .= '<th>' . '&nbsp;</th>';
    $html .= '<th>' . '&nbsp;</th>';
    //$html .= addClassToElement("th", $class) . '&nbsp;</th>';
    foreach($array[0] as $key=>$value){
        $html .= '<th>' . $key . '</th>';
    }
    $html .= '</tr>';
    $html .= "</thead>";

    // data rows
    $html .= "<tbody>";
    foreach( $array as $key=>$value){
            //addClassToElement("tbody", "tabuler_data");
            //"<tbody>";
        $html .=  '<tr>';//addClassToElement("tr", $class);
        $html .= '<td>' . '&nbsp;<a href="/catalog/apps/php/configuration/colorEdit.php?id=' . $value->id .'"><img src="/catalog/apps/images/edit.gif" border="0" alt="Wijzigen"></a></td>';
        $html .= '<td>' . '&nbsp;<a href="/catalog/apps/php/configuration/colorDelete.php?id=' . $value->id .'"><img src="/catalog/apps/images/delete.gif" border="0" alt="Verwijderen"></a></td>';
        foreach($value as $key2=>$value2){
            $html .= '<td>' . $value2 . '</td>';
        }
        $html .= '</tr>';
    }
    $html .= "</tbody>";

    // finish table and return it

    $html .= '</table>';
    return $html;
}