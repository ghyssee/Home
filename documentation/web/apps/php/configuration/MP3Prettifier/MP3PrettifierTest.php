<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
require_once "../../setup.php";
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_HTML, "config.php");
require_once documentPath (ROOT_PHP_BO, "SessionBO.php");
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
goMenu();
?>
<h1>MP3 Pretttifier Test</h1>
<div class="horizontalLine">.</div>
<br>

<div id="tt" class="easyui-tabs" data-options="selected:0" style="width:1100px;height:500px;">
    <div title="Test" style="padding:20px;display:none;">
        <?php include "MP3PrettifierTestList.php"; ?>
    </div>
    <div title="Settings" style="padding:20px;display:none;">
        <?php include "MP3PrettifierTestSettings.php"; ?>
    </div>
</div>

<?php
/*<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>*/
goMenu();

function constructUrl($url, $method, $type, $category){
    $newUrl = $url . "?method=" . $method . "&type=" . $type . "&category=" . $category;
    return $newUrl;
}

?>
<br>

</body>
</html>

