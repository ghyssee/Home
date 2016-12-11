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
		font-size:12px;
	}
	.datagrid-header .datagrid-cell span {
		font-size: 12px;
	}
	.datagrid-row{
		height: 18px;
	}
</style>

<?php
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../html/config.php");
$htmlObj = readJSONWithCode(JSON_ALBUMERRORS);
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

<script type="text/javascript">
	function stripCell(val,row){
		return val.replace("H:\\Shared\\Mijn Muziek\\Eric\\iPod\\Ultratop 50 20160102 02 Januari 2016\\", "");
	}
</script>

<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('title','Album Errors');
$smarty->assign('item','Album Errors');
$url = "AlbumErrorsAction.php";
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['id']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['id']}");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
	                              array("field" => "file", "label"=>"File", "formatter" => "stripCell", "size" => 200),
	                              array("field" => "type", "label"=>"Type",  "size" => 50),
	                              array("field" => "oldValue", "label"=>"Old Value", "formatter" => "stripCell", "size" => 200),
	                              array("field" => "newValue", "label"=>"New Value", "formatter" => "stripCell", "size" => 200)
								  )
				);
/*
$smarty->assign("fields", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => true),
	                              array("field" => "file", "label"=>"File","size" => 200, "hidden" => false),
	                              array("field" => "type", "label"=>"Type", "size"=> 50, "hidden" => false),
	                              array("field" => "oldValue", "label"=>"Old Value", "size" => 200, "hidden" => false),
	                              array("field" => "newValue", "label"=>"New Value", "size" => 200, "hidden" => false)
								)
				);
*/

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGrid2.tpl');
?>

<br>
<?php
goMenu();
?>
<br>

</body>
</html>

