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
include("../html/config.php");
include("../model/HTML.php");
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();
?>
<h1>MP3 Pretttifier</h1>
<div class="horizontalLine">.</div>
<br>

<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Global Words');
$smarty->assign('item','Global Word');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$cat1 = "global";
$cat2 = "words";
$smarty->assign('id',$fieldId);
$smarty->assign('viewUrl',constructUrl($url, "list", $cat1, $cat2));
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['" . $fieldId . "']");
$smarty->assign('newUrl',"'" . constructUrl($url, "add", $cat1, $cat2) . "'");
//$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "oldWord", "label"=>"Old Word", "size" => 50, "sortable" => true),
        array("field" => "newWord", "label"=>"New Word", "size" => 50,"sortable" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');

function constructUrl($url, $method, $cat1, $cat2){
    $newUrl = $url . "?method=" . $method . "&cat1=" . $cat1 . "&cat2=" . $cat2;
    return $newUrl;
}

?>

<br>
<?php
goMenu();
?>
<br>

</body>
</html>

