<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
$oneDrive = getOneDrivePath();
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
if (isset($_SESSION["mp3Settings"])) {
    $mp3SettingsObj = $_SESSION["mp3Settings"];
}
if (isset($_SESSION["mp3Preprocessor"])) {
    $mp3SettingsObj = $_SESSION["mp3Preprocessor"];
}
if (isset($_SESSION["mp3PrettifierGlobal"])) {
    $globalWordObj = $_SESSION["mp3PrettifierGlobal"];
} else {
    $globalWordObj = new Word();
}
if (isset($_SESSION["mp3PrettifierArtistWord"])) {
    $artistWordObj = $_SESSION["mp3PrettifierArtistWord"];
} else {
    $artistWordObj = new Word();
}
if (isset($_SESSION["mp3PrettifierArtistName"])) {
    $artistNameObj = $_SESSION["mp3PrettifierArtistName"];
} else {
    $artistNameObj = new Word();
}
if (isset($_SESSION["mp3PrettifierSongTitle"])) {
    $songTitleObj = $_SESSION["mp3PrettifierSongTitle"];
} else {
    $songTitleObj = new Word();
}

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

<form action="albumSave.php" method="post">
    <?php
    goMenu();
    ?>
    <h1>Album Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox3(new Input(array('name' => "album",
        'size' => 100,
        'label' => 'Album',
        'value' => $mp3SettingsObj->album)));
    $layout->button2(new Input(array('name' => "mp3Settings",
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
    $layout->inputBox3(new Input(array('name' => "mezzmoBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mezzmo->base)));
    $layout->inputBox3(new Input(array('name' => "importBase",
        'size' => 100,
        'label' => 'Import Base',
        'value' => $mp3SettingsObj->mezzmo->importF->base)));
    $layout->inputBox3(new Input(array('name' => "filename",
        'size' => 50,
        'label' => 'Import File',
        'value' => $mp3SettingsObj->mezzmo->importF->filename)));
    $layout->button2(new Input(array('name' => "mp3Settings",
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
    $layout->checkBox3(new Input(array('name' => "updateRating",
        'label' => 'Update Rating',
        'value' => $mp3SettingsObj->synchronizer->updateRating)));
    $layout->button2(new Input(array('name' => "mp3Settings",
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
    $layout->inputBox3(new Input(array('name' => "mediaMonkeyBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mediaMonkey->base)));
    $layout->inputBox3(new Input(array('name' => "mediaMonkeyPlaylistPath",
        'size' => 100,
        'label' => 'Playlist Path',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->path)));
    $layout->inputBox3(new Input(array('name' => "mediaMonkeyTop20",
        'size' => 100,
        'label' => 'Top 20 Name',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->top20)));
    $layout->button2(new Input(array('name' => "mp3Settings",
        'value' => 'saveMM',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>MP3Preprocessor Configuration</h1>
    <div class="horizontalLine">.</div>
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox3(new Input(array('name' => "albumTag",
        'size' => 50,
        'label' => 'AlbumTag',
        'value' => $mp3PreprocessorObj->albumTag)));
    $layout->inputBox3(new Input(array('name' => "cdTag",
        'size' => 50,
        'label' => 'CdTag',
        'value' => $mp3PreprocessorObj->cdTag)));
    $layout->inputBox3(new Input(array('name' => "prefix",
        'size' => 50,
        'label' => 'Prefix',
        'value' => $mp3PreprocessorObj->prefix)));
    $layout->inputBox3(new Input(array('name' => "suffix",
        'size' => 50,
        'label' => 'Suffix',
        'value' => $mp3PreprocessorObj->suffix)));
    $layout->comboBox3($mp3PreprocessorObj->configurations, "id", "id",
        new Input(array('name' => "activeConfiguration",
            'label' => 'Active Configuration',
            'method' => 'getConfigurationText',
            'methodArg' => 'config',
            'default' => $mp3PreprocessorObj->activeConfiguration)));
    $layout->button2(new Input(array('name' => "mp3Preprocessor",
        'value' => 'save',
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
    $layout->inputBox3(new Input(array('name' => "number",
        'size' => 5,
        'label' => 'Number',
        'value' => $mp3SettingsObj->lastPlayedSong->number)));
    $layout->comboBox3($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollColor",
            'label' => 'Scroll Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollColor)));
    $layout->comboBox3($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollBackgroundColor",
            'label' => 'Scroll Background Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollBackgroundColor)));
    $layout->button2(new Input(array('name' => "mp3Settings",
        'value' => 'saveLastPlayed',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form action="albumSave.php" method="post">

    <?php
    $layout = new Layout(array('numCols' => 2));
    $layout->label(new Input(array('label' => '<h3>General</h3>',
        'col' => 1,
        'colspan' => 2)));
    $layout->inputBox3(new Input(array('name' => "oldWord",
        'size' => 50,
        'label' => 'Old Word',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $globalWordObj->oldWord)));
    $layout->inputBox3(new Input(array('name' => "newWord",
        'size' => 50,
        'label' => 'New Word',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $globalWordObj->newWord)));
    $layout->button2(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveGlobalWord',
        'colspan' => 2,
        'col' => 1)));

    $layout->label(new Input(array('label' => '<h3>Artist Word</h3>',
        'col' => 2,
        'colspan' => 2)));
    $layout->inputBox3(new Input(array('name' => "artistOldWord",
        'size' => 50,
        'label' => 'Old Word',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistWordObj->oldWord)));
    $layout->inputBox3(new Input(array('name' => "artistNewWord",
        'size' => 50,
        'label' => 'New Word',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistWordObj->newWord)));
    $layout->button2(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveArtistWord',
        'colspan' => 2,
        'col' => 2)));
    $layout->close();
    ?>
    <?php
    $layout = new Layout(array('numCols' => 2));

    $layout->label(new Input(array('label' => '<h3>Song Title</h3>',
        'col' => 1,
        'colspan' => 2)));
    $layout->inputBox3(new Input(array('name' => "songTitleOldWord",
        'size' => 50,
        'label' => 'Old Song Title',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $songTitleObj->oldWord)));
    $layout->inputBox3(new Input(array('name' => "songTitleNewWord",
        'size' => 50,
        'label' => 'New Song Title',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $songTitleObj->newWord)));
    $layout->button2(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveSongTitle',
        'colspan' => 2,
        'col' => 1)));

    $layout->label(new Input(array('label' => '<h3>Artist Name</h3>',
        'col' => 2,
        'colspan' => 2)));
    $layout->inputBox3(new Input(array('name' => "artistNameOldWord",
        'size' => 50,
        'label' => 'Old Artist Name',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistNameObj->oldWord)));
    $layout->inputBox3(new Input(array('name' => "artistNameNewWord",
        'size' => 50,
        'label' => 'New Artist Name',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistNameObj->newWord)));
    $layout->button2(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveArtistName',
        'colspan' => 2,
        'col' => 2)));
    $layout->close();
    ?>

</form>

<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["mp3Settings"]);
unset($_SESSION["mp3Preprocessor"]);
unset($_SESSION["mp3Prettifier"]);
?>

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

?>
