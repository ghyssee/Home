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
$smarty->assign('updateUrl', "'" . $url . "?method=updateMultiArtist&id='+row['" . $fieldId . "']");
$smarty->assign('deleteUrl', $url . "?method=deleteMultiArtist");
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "description", "label"=>"Artists", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "description2", "label"=>"ArtistSequence", "size" => 50, "required" => true, "sortable" => true),
        array("field" => "master", "label"=>"Master", "size" => 30, "required" => true, "sortable" => true),
        array("field" => "exactPosition", "label"=>"Exact", "size" => 15, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "id", "label"=>"Id", "size" => 50, "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');
?>
<div><button onclick="refreshMultiArtist()">Refresh</button></div>

<script>
    function refreshMultiArtist(){
        $('#dgMultiArtistList').datagrid('reload');
    }
    $(".datagrid-header-rownumber").click(function(){
        alert("clicked");
    });
    var div = document.getElementsByClassName('datagrid-header-rownumber')[0];

    div.addEventListener('click', function (event) {
        alert('Hi!');
    });

</script>
