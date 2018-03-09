<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
?>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.edatagrid.js')?>"></script>
</head>
<body>

<?php
sessionStart();
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
<form action="albumInfoAction.php" method="post">
    <div id="tt" class="easyui-tabs" data-options="selected:0" style="width:900px;height:720px;">
        <div title="PreProcessor" style="padding:20px;display:none;">
            <?php include "AlbumInfoViewPreProcessorConfiguration.php"; ?>
        </div>
        <div title="Processor" style="padding:20px;display:none;">
            <?php include "AlbumInfoViewProcessorConfiguration.php"; ?>
        </div>
        <div title="Tracklist" style="padding:20px;display:none;">
            <?php include "AlbumInfoViewTrackList.php"; ?>
        </div>
    </div>
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

