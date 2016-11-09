<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body>

<?php
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
$oneDrive = getOneDrivePath();
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
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
    .descriptionColumn {
        width: 20%;
    }
</style>

    <?php
    goMenu();
    ?>
    <h1>MP3Prettifier Configuration</h1>
    <div class="horizontalLine">.</div>

	<form action="MP3PrettifierSave.php" method="post">

    <?php
    $layout = new Layout(array('numCols' => 2));
    $layout->label(new Input(array('label' => '<h3>General</h3>',
        'col' => 1,
        'colspan' => 2)));
    $layout->inputBox(new Input(array('name' => "oldWord",
        'size' => 50,
        'label' => 'Old Word',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $globalWordObj->oldWord)));
    $layout->inputBox(new Input(array('name' => "newWord",
        'size' => 50,
        'label' => 'New Word',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $globalWordObj->newWord)));
    $layout->button(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveGlobalWord',
        'colspan' => 2,
        'col' => 1)));

    $layout->label(new Input(array('label' => '<h3>Artist Word</h3>',
        'col' => 2,
        'colspan' => 2)));
    $layout->inputBox(new Input(array('name' => "artistOldWord",
        'size' => 50,
        'label' => 'Old Word',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistWordObj->oldWord)));
    $layout->inputBox(new Input(array('name' => "artistNewWord",
        'size' => 50,
        'label' => 'New Word',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistWordObj->newWord)));
    $layout->button(new Input(array('name' => "mp3Prettifier",
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
    $layout->inputBox(new Input(array('name' => "songTitleOldWord",
        'size' => 50,
        'label' => 'Old Song Title',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $songTitleObj->oldWord)));
    $layout->inputBox(new Input(array('name' => "songTitleNewWord",
        'size' => 50,
        'label' => 'New Song Title',
        'labelClass' => 'descriptionColumn',
        'col' => 1,
        'value' => $songTitleObj->newWord)));
    $layout->button(new Input(array('name' => "mp3Prettifier",
        'text' => 'Save',
        'value' => 'saveSongTitle',
        'colspan' => 2,
        'col' => 1)));

    $layout->label(new Input(array('label' => '<h3>Artist Name</h3>',
        'col' => 2,
        'colspan' => 2)));
    $layout->inputBox(new Input(array('name' => "artistNameOldWord",
        'size' => 50,
        'label' => 'Old Artist Name',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistNameObj->oldWord)));
    $layout->inputBox(new Input(array('name' => "artistNameNewWord",
        'size' => 50,
        'label' => 'New Artist Name',
        'labelClass' => 'descriptionColumn',
        'col' => 2,
        'value' => $artistNameObj->newWord)));
    $layout->button(new Input(array('name' => "mp3Prettifier",
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
unset($_SESSION["mp3Prettifier"]);
?>

</body>
</html>
