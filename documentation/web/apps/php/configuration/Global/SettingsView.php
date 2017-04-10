<?php
include_once("../../setup.php");
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
</head>
<body>

<?php
sessionStart();
$settingsSave = "SettingsAction.php";

$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
/*
if (isset($_SESSION["mp3Preprocessor"])) {
    $mp3PreprocessorObj = $_SESSION["mp3Preprocessor"];
}*/
?>

<?php
goMenu();
?>
<h3 class="centered">Settings</h3>
<div id="tt" class="easyui-tabs centered" data-options="selected:0" style="width:900px;height:500px;margin:0 auto;">
    <div title="Settings" style="padding:20px;display:none;">
        <?php include "SettingsViewTab1.php"; ?>
    </div>
</div>
<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>

<?php
goMenu();
unset($_SESSION["errors"]);
?>

</body>
</html>
