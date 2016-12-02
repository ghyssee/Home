<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
</head>
<body style="background">

<style>
	.datagrid-cell{
		font-size:20px;
	}
	.datagrid-header .datagrid-cell span {
		font-size: 20px;
	}
	.datagrid-row{
		height: 30px;
	}
</style>

<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../html/config.php");
$htmlObj = readJSONWithCode(JSON_HTML);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);
?>
<?php
goMenu();
?>
<h1>Colors</h1>
<div class="horizontalLine">.</div>
<br>

<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");

require_once(SMARTY_DIR . 'Smarty.class.php');

$smarty = new Smarty();

$smarty->setTemplateDir('/smarty/templates/');
$smarty->setCompileDir('/smarty/templates_c/');
$smarty->setConfigDir('/smarty/configs/');
$smarty->setCacheDir('/smarty/cache/');

$smarty->assign('title','Colors123');

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGrid.tpl');
?>

<br>
<?php
goMenu();
?>
<br>

</body>
</html>

