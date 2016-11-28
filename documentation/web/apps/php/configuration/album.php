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
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
$htmlObj = readJSONWithCode(JSON_HTML);
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
if (isset($_SESSION["mp3Settings"])) {
    $mp3SettingsObj = $_SESSION["mp3Settings"];
}
/*
if (isset($_SESSION["mp3Preprocessor"])) {
    $mp3PreprocessorObj = $_SESSION["mp3Preprocessor"];
}*/
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

<form action="albumSave.php" method="post">
    <h1>Album Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "album",
        'size' => 100,
        'label' => 'Album',
        'value' => $mp3SettingsObj->album)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveAlbum',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>

</form>

<form action="albumSave.php" method="post">
    <h1>Mezzmo Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "mezzmoBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mezzmo->base)));
    $layout->inputBox(new Input(array('name' => "importBase",
        'size' => 100,
        'label' => 'Import Base',
        'value' => $mp3SettingsObj->mezzmo->importF->base)));
    $layout->inputBox(new Input(array('name' => "filename",
        'size' => 50,
        'label' => 'Import File',
        'value' => $mp3SettingsObj->mezzmo->importF->filename)));
    $layout->inputBox(new Input(array('name' => "exportBase",
        'size' => 100,
        'label' => 'Export Path',
        'value' => $mp3SettingsObj->mezzmo->export->base)));
    $layout->inputBox(new Input(array('name' => "exportiPod",
        'size' => 100,
        'label' => 'Export iPod Path',
        'value' => $mp3SettingsObj->mezzmo->export->iPod)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveMezzmo',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form action="albumSave.php" method="post">
    <h1>iPod Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->checkBox(new Input(array('name' => "updateRating",
        'label' => 'Update Rating',
        'value' => $mp3SettingsObj->synchronizer->updateRating)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveiPod',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form action="albumSave.php" method="post">
    <h1>MediaMonkey Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mediaMonkey->base)));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyPlaylistPath",
        'size' => 100,
        'label' => 'Playlist Path',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->path)));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyTop20",
        'size' => 100,
        'label' => 'Top 20 Name',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->top20)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveMM',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form action="albumSave.php" method="post">
    <h1>LastPlayed Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "number",
        'size' => 5,
        'label' => 'Number',
        'type' => 'number',
        'min' => '1',
        'max' => '100',
        'value' => $mp3SettingsObj->lastPlayedSong->number)));
    $layout->comboBox($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollColor",
            'label' => 'Scroll Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollColor)));
    $layout->comboBox($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollBackgroundColor",
            'label' => 'Scroll Background Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollBackgroundColor)));
    $layout->checkBox(new Input(array('name' => "scrollShowAlbum",
        'label' => 'Show Album',
        'value' => $mp3SettingsObj->lastPlayedSong->scrollShowAlbum)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveLastPlayed',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["mp3Settings"]);
/*
unset($_SESSION["mp3Preprocessor"]);
*/
?>

</body>
</html>
