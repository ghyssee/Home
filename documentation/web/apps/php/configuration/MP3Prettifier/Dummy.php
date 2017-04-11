<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Global Words');
$smarty->assign('item','Global Word');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$type = "global";
$category = "words";
$smarty->assign('tablegrid',"Dummy");
$smarty->assign('id',$fieldId);
$smarty->assign('fitColumns',"true");
$smarty->assign('viewUrl',constructUrl($url, "list", $type, $category));
//$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['" . $fieldId . "']");
//$smarty->assign('newUrl',"'" . $url . "?method=add'");
//$smarty->assign('deleteUrl', $url . "?method=delete");

$smarty->assign("contacts", array(
        array("field" => "oldWord", "label"=>"Old Word", "size" => 70, "required" => true, "sortable" => true),
        array("field" => "newWord", "label"=>"New Word", "size" => 70,"sortable" => true),
        array("field" => "exactMatch", "label"=>"Exact Match", "size" => 15, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>
