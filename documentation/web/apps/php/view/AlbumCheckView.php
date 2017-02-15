<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
</head>
<body>

<?php
session_start();
include_once("../setup.php");
include_once("../config.php");
include_once("../html/config.php");
include_once("../model/HTML.php");
//$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
/*
if (isset($_SESSION["mp3Settings"])) {
    $mp3SettingsObj = $_SESSION["mp3Settings"];
}
*/
?>

<style>
    .inlineTable {
        float: left;
    }

    th {
        text-align: left;
    }

    .descriptionColumn {
        width: 20%;
    }

</style>
<?php
goMenu();
?>

<form action="AlbumCheckAction.php" method="post">
    <h1>Album Check Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php

    $file = getFullPath(FILE_ALBUMCHECK);
    $textValue = '';
    if (file_exists($file)){
        $textValue = read($file);
    }
    
    $layout = new Layout(array('numCols' => 2));

    $layout->textArea(new Input(array('name' => "albumList",
        'col' => 1,
        'cols' => 80,
        'rows' => 15,
        'label' => 'Album(s) To Check',
        'value' => $textValue)));

    $file = getFullPath(FILE_ALBUMWITHOUTERRORS);
    $textValue = '';
    if (file_exists($file)){
        $textValue = read($file);
    }
    $layout->textArea(new Input(array('name' => "albmuWithoutErrors",
        'col' => 2,
        'cols' => 60,
        'rows' => 10,
        'label' => 'Album(s) Without Errors',
        'value' => $textValue)));


    $file = getFullPath(FILE_ALBUMEXCLUDE);
    $textValue = '';
    if (file_exists($file)){
        $textValue = read($file);
    }
    $layout->textArea(new Input(array('name' => "albumExclude",
        'col' => 1,
        'cols' => 80,
        'rows' => 10,
        'label' => 'Album(s) To Exclude',
        'value' => $textValue)));


    $layout->button(new Input(array('name' => "albumCheck",
        'col' => 1,
        'value' => 'save',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>

</form>


<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["mp3Settings"]);
?>

</body>
</html>
