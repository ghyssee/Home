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
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);

function getCurrentPage(){
    return basename($_SERVER['PHP_SELF']);
}

function getAction(){
    return "MP3PreprocessorAction.php";
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

<div id="preprocessorTabs" class="easyui-tabs" data-options="selected:0" style="width:900px;height:500px;">
    <div title="Splitter" style="padding:20px;display:none;">
        <?php include "MP3PreprocessorViewSplitter.php"; ?>
    </div>
    <div title="Config" style="padding:20px;display:none;">
        <?php include "MP3PreprocessorViewConfiguration.php"; ?>
    </div>
</div>




<div class="emptySpace"></div>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html>

