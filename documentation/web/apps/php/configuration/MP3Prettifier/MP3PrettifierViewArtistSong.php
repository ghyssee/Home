<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','ArtistSong Exceptions');
$smarty->assign('item','ArtistSong Exception');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
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
        array("field" => "oldArtist", "label"=>"Old Artist", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "newArtist", "label"=>"New Artist", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "oldSong", "label"=>"Old Song", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "newSong", "label"=>"New Song", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>
