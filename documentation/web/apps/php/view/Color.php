<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
include("../bo/ColorBO.php");
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
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
$formMode = getMode();
if ($formMode == 'U') {
    $id = htmlspecialchars($_GET["id"]);
    $colorBO = new ColorBO();
    if (isset($_SESSION["color"])) {
        $color = $_SESSION["color"];
        unset($_SESSION["color"]);
    } else {
        $color = $colorBO->lookupColor($id);
        if (empty ($color)) {
            exit('Id Not Found: ' . $id);
        }
    }
    $title = 'Edit Color';
}
elseif ($formMode == 'I') {
    if (isset($_SESSION["color"])) {
        $color = $_SESSION["color"];
        unset($_SESSION["color"]);
    } else {
        $color = new Color();
    }
    $title = 'Add Color';
}
elseif ($formMode == 'D') {
    $id = htmlspecialchars($_GET["id"]);
    echo 'deleting ' . $id;
    exit();
}
?>
<h1><?php echo $title ?></h1>
<div class="horizontalLine">.</div>
<form action="ColorSave.php" method="post">

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
    $layout->close();
    $layout = new Toolbar(array('numCols' => 2));
    if ($formMode == 'I'){
        $layout->button(new Input(array('name' => "htmlSettings",
            'value' => 'addColor',
            'text' => 'Add')));
    }
    else {
        echo '<input type="hidden" name="id" value="' . $color->id . '"">' . PHP_EOL;
        $layout->button(new Input(array('name' => "htmlSettings",
            'value' => 'saveColor',
            'text' => 'Save')));
    }
    $layout->button(new Input(array('name' => "htmlSettings",
        'value' => 'back',
        'text' => 'Back')));
    $layout->resetButton();
    $layout->close();
    ?>
</form>

<?php
goMenu();
?>
</body>
</html>

<?php
    function getMode(){
        if (isset($_GET['mode'])){
            $formMode = htmlspecialchars($_GET['mode']);
            return $formMode;
        }
        else {
           exit('Form Mode Not Set!!!');
        }
        
    }
    
    ?>