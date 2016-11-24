<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
</head>
<body>

<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .inlineTable {
        float: left;
    }

    th {
        text-align: left;
    }

</style>


<?php
goMenu();
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Splitter();
}
?>
<h1>MP3Preprocessor Settings</h1>
<h3>Splitters</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "splitterId",
        'size' => 30,
        'label' => 'Id',
        'value' => $splitter->id)));
    $layout->inputBox(new Input(array('name' => "pattern",
        'size' => 30,
        'label' => 'Pattern',
        'value' => $splitter->pattern)));
    $layout->button(new Input(array('name' => "mp3Preprocessor",
        'value' => 'addSplitter',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>
<h3>MP3Preprocessor Configuration</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <?php
        $layout = new Layout(array('numCols' => 1));
    $layout->comboBox($mp3PreprocessorObj->fields, "id", "description",
        new Input(array('name' => "scrollColor",
            'label' => 'Type',
            'default' => '')));
    $layout->inputBox(new Input(array('name' => "configSplitter",
        'size' => 30,
        'label' => 'Id',
        'value' => '')));
    $layout->checkBox(new Input(array('name' => "configDuration",
        'label' => 'Remove Duration At End',
        'value' => true)));
    $layout->button(new Input(array('name' => "mp3Preprocessor",
        'value' => 'addConfig',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html> 

