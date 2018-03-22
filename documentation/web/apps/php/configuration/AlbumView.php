<?php
include_once("../setup.php");
?>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body>

<?php
session_start();
?>
<?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>
<?php
$albumSave = "AlbumAction.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

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
<h3 class="centered">Settings</h3>
<div id="tt" class="easyui-tabs centered" data-options="selected:0" style="width:900px;height:500px;margin:0 auto;">
    <div title="Album" style="padding:20px;display:none;">
        <?php include "AlbumViewAlbum.php"; ?>
    </div>
    <div title="Mezzmo" style="padding:20px;display:none;">
        <?php include "AlbumViewMezzmo.php"; ?>
    </div>
    <div title="Synchronizer" style="padding:20px;display:none;">
        <?php include "AlbumViewSynchronizer.php"; ?>
    </div>
    <div title="MediaMonkey" style="padding:20px;display:none;">
        <?php include "AlbumViewMediaMonkey.php"; ?>
    </div>
    <div title="Last Played" style="padding:20px;display:none;">
        <?php include "AlbumViewLastPlayed.php"; ?>
    </div>
</div>
<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>

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
