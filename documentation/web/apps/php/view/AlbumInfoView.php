<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
    <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
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

