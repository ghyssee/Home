<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
</head>
<body>

<?php
session_start();
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");

$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
if (isset($_SESSION["mp3Preprocessor"])) {
    $mp3PreprocessorObj = $_SESSION["mp3Preprocessor"];
}
else {
    $mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
}
if (isset($_SESSION["mp3Settings"])) {
    $mp3SettingsObj = $_SESSION["mp3Settings"];
}
else {
    $mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
}
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
?>
<h1>Album Information</h1>
<div class="horizontalLine">.</div>
<form action="albumInfoSave.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "cdTag",
        'size' => 50,
        'col' => 1,
        'label' => 'CdTag',
        'value' => $mp3PreprocessorObj->cdTag)));
    $layout->inputBox(new Input(array('name' => "prefix",
        'size' => 50,
        'col' => 1,
        'label' => 'Prefix',
        'value' => $mp3PreprocessorObj->prefix)));
    $layout->inputBox(new Input(array('name' => "suffix",
        'size' => 50,
        'col' => 1,
        'label' => 'Suffix',
        'value' => $mp3PreprocessorObj->suffix)));
    $layout->comboBox($mp3PreprocessorObj->configurations, "id", "id",
        new Input(array('name' => "activeConfiguration",
            'label' => 'Active Configuration',
            'col' => 1,
            'method' => 'getConfigurationText',
            'methodArg' => 'config',
            'default' => $mp3PreprocessorObj->activeConfiguration)));
    $layout->inputBox(new Input(array('name' => "album",
        'size' => 80,
        'col' => 1,
        'label' => 'Album',
        'value' => $mp3PreprocessorObj->album)));
    $layout->inputBox(new Input(array('name' => "albumLocation",
        'size' => 80,
        'col' => 1,
        'label' => 'Album Location',
        'value' => $mp3SettingsObj->album)));
    $layout->inputBox(new Input(array('name' => "albumArtist",
        'size' => 50,
        'col' => 1,
        'label' => 'Album Artist',
        'value' => $mp3SettingsObj->albumArtist)));
    $layout->inputBox(new Input(array('name' => "albumYear",
        'size' => 4,
        'col' => 1,
        'label' => 'Album Year',
        'type' => 'number',
        'min' => '1900',
        'value' => $mp3SettingsObj->albumYear)));
    $layout->checkBox(new Input(array('name' => "renum",
        'label' => 'Renum Tracks',
        'value' => $mp3PreprocessorObj->renum)));


    $file = getFullPath(FILE_ALBUM);
    $textValue = '';
    if (file_exists($file)){
        $textValue = read($file);
    }

    $layout->textArea(new Input(array('name' => "albumContent",
        'col' => 1,
        'cols' => 80,
        'rows' => 40,
        'label' => 'Album Inforomation',
        'value' => $textValue)));

    $layout->button(new Input(array('name' => "albumInfo",
        'col' => 1,
        'value' => 'save',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["mp3Preprocessor"]);
?>
</form>
</body>
</html>
<?php

function getConfigurationText($array)
{
    $desc = "";
    foreach ($array as $key => $config) {
        if (isset($config->type)) {
            $desc = $desc . (empty($desc) ? '' : ' ') . $config->type;
        }
        if (isset($config->splitter)) {
            $desc = $desc . " " . $config->splitter;
        }
        if (!empty($config->duration)) {
            $desc = $desc . " (duration TRUE)";
        }
    }
    return $desc;
}

