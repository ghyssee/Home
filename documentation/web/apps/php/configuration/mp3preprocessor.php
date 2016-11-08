<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
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
    $layout->inputBox3(new Input(array('name' => "splitterId",
        'size' => 30,
        'label' => 'Id',
        'value' => $splitter->id)));
    $layout->inputBox3(new Input(array('name' => "pattern",
        'size' => 30,
        'label' => 'Pattern',
        'value' => $splitter->pattern)));
    $layout->button2(new Input(array('name' => "mp3Preprocessor",
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
    $layout->comboBox3($mp3PreprocessorObj->fields, "id", "description",
        new Input(array('name' => "scrollColor",
            'label' => 'Type',
            'default' => '')));
    $layout->inputBox3(new Input(array('name' => "configSplitter",
        'size' => 30,
        'label' => 'Id',
        'value' => '')));
    $layout->checkBox3(new Input(array('name' => "configDuration",
        'label' => 'Remove Duration At End',
        'value' => true)));
    $layout->button2(new Input(array('name' => "mp3Preprocessor",
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

