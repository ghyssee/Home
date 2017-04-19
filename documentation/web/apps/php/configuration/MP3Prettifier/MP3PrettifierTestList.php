<?php
include_once('Smarty.class.php');

$fieldId = "fileId";
$smarty = initializeSmarty();
$smarty->assign('title','Test Items');
$smarty->assign('item','Artist Song');
$smarty->assign('tableWidth','100%');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierTestAction.php";
$smarty->assign('tablegrid',"ArtistSongTest");
$smarty->assign('id',$fieldId);
$smarty->assign('fitColumns',"true");
$smarty->assign('viewUrl',$url . "?method=list");

$smarty->assign("contacts", array(
        array("field" => "oldArtist", "label"=>"OldArtist", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "newArtist", "label"=>"newArtist", "size" => 40, "required" => true, "sortable" => true, "styler" => "styleArtist"),
        array("field" => "oldSong", "label"=>"OldSong", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "newSong", "label"=>"newSong", "size" => 40, "required" => true, "sortable" => true, "styler" => "styleSong"),
        array("field" => "fileId", "label"=>"FileId", "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>
<div><button onclick="refreshTst()">Refresh</button></div>

<script>
    function refreshTst(){
        $('#dgArtistSongTest').datagrid('reload');
    }
    function styleArtist(value, row, index){
        if (row.oldArtist != row.newArtist) {
            return 'color:red;';
        }
        return '';
    }
    function styleSong(value, row, index){
        if (row.oldSong != row.newSong) {
            return 'color:red;';
        }
        return '';
    }
</script>


