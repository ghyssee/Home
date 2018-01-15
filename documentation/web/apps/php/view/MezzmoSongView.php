<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");
session_start();
?>


<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
</head>
<body style="background">




<?php
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
if (isset($_REQUEST["forward"])) {
    $forward = webPath (ROOT_APPS_MUSIC_SONGS, htmlspecialchars($_REQUEST['forward']));
    showUrl($forward, "Back");
}
else {
    $forward = $_SESSION['previous_location'];
}

$_SESSION['save_location'] = $forward;
if (isset($_REQUEST["id"])) {
    $id = htmlspecialchars($_REQUEST['id']);
}
else {
    echo "Id Not Found";
    exit(0);
}
if (isset($_SESSION["SONG"])) {
    $songObj = $_SESSION["SONG"];
    unset($_SESSION["SONG"]);
}
else {
    $songBO = new SongBO();
    $songBO->setDB(MEZZMO);
    try {
        $songObj = $songBO->lookupSong($id);
    }
    catch (Exception $e){
        echo $e->getMessage();
        exit(0);
    }
}
$mezzmoSongSave = "MezzmoSongAction.php";
?>
<h1>Update Song</h1>
<div class="horizontalLine">.</div>

<form action="<?php echo $mezzmoSongSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->hiddenField(new Input(array('name' => "fileId",
        'value' => $songObj->fileId)));
    $layout->inputBox(new Input(array('name' => "fileId",
        'size' => 10,
        'label' => 'FileId',
        'disabled' => true,
        'value' => $songObj->fileId)));
    $layout->inputBox(new Input(array('name' => "source",
        'size' => 10,
        'label' => 'Source',
        'disabled' => true,
        'value' => $songObj->source)));
    $layout->inputBox(new Input(array('name' => "track",
        'size' => 5,
        'type' => 'number',
        'min' => 1,
        'label' => 'Track',
        'value' => $songObj->track)));
    $layout->inputBox(new Input(array('name' => "title",
        'size' => 100,
        'label' => 'Title',
        'value' => $songObj->title)));
    $layout->inputBox(new Input(array('name' => "artist",
        'size' => 100,
        'label' => 'Artist',
        'value' => $songObj->artist)));
    $layout->button(new Input(array('name' => "saveButton",
        'value' => 'save',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>

</form>

<?php
goMenu();
?>
<br/>

</body>
</html>

