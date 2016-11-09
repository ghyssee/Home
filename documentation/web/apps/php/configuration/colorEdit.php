<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    th {
        text-align: left;
    }
    .descriptionColumn {
        width: 20%;
    }
</style>

<?php
goMenu();
$id = htmlspecialchars($_GET["id"]);
if (isset($_SESSION["color"])) {
    $color = $_SESSION["color"];
} else {
    $color = new Color();
    $found = false;
    foreach ($htmlObj-> colors as $key => $value){
        if (strcmp($value->code, $id) == 0){
            $color = $value;
            $found = true;
            break;
        }
    }
    if (!$found){
        exit('Code Not Found: ' . $id);
    }
}
?>
<h1>Edit Color</h1>
<div class="horizontalLine">.</div>
<form action="colorSave.php" method="post">
    <?php
        $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "colorDescription",
        'size' => 50,
        'label' => 'Description',
        'value' => $color->description)));
    $layout->inputBox(new Input(array('name' => "colorCode",
    'size' => 50,
    'label' => 'Color Code',
    'value' => $id)));
    $layout->button(new Input(array('name' => "htmlSettings",
        'value' => 'edit',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["color"]);
?>
</body>
</html>
