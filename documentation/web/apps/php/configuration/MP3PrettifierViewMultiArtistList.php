<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Mulit Artist List');
$smarty->assign('item','Artist');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$smarty->assign('tablegrid',"MultiArtistList");
$smarty->assign('id',$fieldId);
$smarty->assign('viewUrl',$url . "?method=listMultiArtists");
$smarty->assign('deleteUrl', $url . "?method=deleteMultiArtistLine");
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "description", "label"=>"Description", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');
?>
