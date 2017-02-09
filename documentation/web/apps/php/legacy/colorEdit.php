<?php
include("../setup.php");
?>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "ColorBO.php");

$htmlObj = readJSONWithCode(JSON_HTML);
session_start();
$_SESSION['previous_location'] = getUrl();
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
$colorBO = new ColorBO();
if (isset($_SESSION["color"])) {
    $color = $_SESSION["color"];
    unset($_SESSION["color"]);
}
else {
    $color = $colorBO->lookupColor($id);
    if (empty ($color)){
        exit('Id Not Found: ' . $id);
    }
}
?>
<h1>Edit Color</h1>
<div class="horizontalLine">.</div>
<form action="settingsSave.php" method="post">
    <input type="hidden" name="id" value="<?php echo $color->id ?>">
    <?php
        $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "colorDescription",
        'size' => 50,
        'label' => 'Description',
        'value' => $color->description)));
    $layout->inputBox(new Input(array('name' => "colorCode",
    'size' => 50,
    'label' => 'Color Code',
    'value' => $color->code)));
    $layout->button(new Input(array('name' => "htmlSettings",
        'value' => 'saveColor',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<?php
goMenu();
?>
</body>
</html>
