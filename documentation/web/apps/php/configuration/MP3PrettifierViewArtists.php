<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Artists');
$smarty->assign('item','Artist');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$smarty->assign('tablegrid',"Artists");
$smarty->assign('id',$fieldId);
$smarty->assign('viewUrl',$url . "?method=listArtists");
$smarty->assign('updateUrl',"'" . $url . "?method=updateArtist&id='+row['" . $fieldId . "']");
//$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['" . $fieldId . "']");
$smarty->assign('newUrl',"'" . $url . "?method=addArtist'");
//$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', $url . "?method=deleteArtist");
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "name", "label"=>"Name", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');
?>
