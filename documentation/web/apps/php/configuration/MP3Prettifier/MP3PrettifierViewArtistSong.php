<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','ArtistSong Exceptions');
$smarty->assign('item','ArtistSong Exception');
$smarty->assign('tableWidth','100%');
$smarty->assign('tableHeight','100%');
$url = "MP3PrettifierAction.php";
$type = "artistSongExceptions";
$category = "items";
$smarty->assign('tablegrid',"ArtistSongExceptions");
$smarty->assign('id',$fieldId);
$smarty->assign('fitColumns',"true");
$smarty->assign('viewUrl',constructUrl($url, "list", $type, $category));
$smarty->assign('updateUrl',"'" . constructUrl($url, "updateArtistSong&id='+row['" . $fieldId . "']+'", $type, $category) . "'");
//$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['" . $fieldId . "']");
$smarty->assign('newUrl',"'" . constructUrl($url, "addArtistSong", $type, $category) . "'");
//$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', constructUrl($url, "deleteArtistSong", $type, $category));
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "oldArtist", "label"=>"Old Artist", "size" => 25, "required" => true, "sortable" => true),
        array("field" => "oldFreeArtist", "label"=>"Free", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "newArtist", "label"=>"New Artist", "size" => 35, "required" => true, "sortable" => true),
        array("field" => "oldSong", "label"=>"Old Song", "size" => 35, "required" => true, "sortable" => true),
        array("field" => "newSong", "label"=>"New Song", "size" => 35, "required" => true, "sortable" => true),
        array("field" => "exactMatchTitle", "label"=>"Exact Title", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "exactMatchArtist", "label"=>"Exact Artist", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "priority", "label"=>"Priority", "size" => 5, "type" => "number", "sortable" => true),
        array("field" => "indexTitle", "label"=>"IndexTitle", "size" => 5, "type" => "number", "sortable" => true),
        array("field" => "id", "label"=>"Id", "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>
