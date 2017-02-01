<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Artist Words');
$smarty->assign('item','Artist Word');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$type = "artist";
$category = "words";
$smarty->assign('tablegrid',"ArtistWord");
$smarty->assign('id',$fieldId);
$smarty->assign('viewUrl',constructUrl($url, "list", $type, $category));
$smarty->assign('updateUrl',"'" . constructUrl($url, "update&id='+row['" . $fieldId . "']+'", $type, $category) . "'");
//$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['" . $fieldId . "']");
$smarty->assign('newUrl',"'" . constructUrl($url, "add", $type, $category) . "'");
//$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', constructUrl($url, "delete", $type, $category));
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "oldWord", "label"=>"Old Word", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "newWord", "label"=>"New Word", "size" => 50,"sortable" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');
?>
