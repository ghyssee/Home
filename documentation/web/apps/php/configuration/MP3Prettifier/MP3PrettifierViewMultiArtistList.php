<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Mulit Artist List');
$smarty->assign('item','Artist');
$smarty->assign('tableWidth','100%');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$url2 = "MultiArtistView.php";
$smarty->assign('tablegrid',"MultiArtistList");
$smarty->assign('id',$fieldId);
$smarty->assign('fitColumns',"true");
$smarty->assign('newUrl',$url2);
$smarty->assign('customAdd',"addMultiArtist()");
$smarty->assign('customEdit',"editMultiArtist()");
$smarty->assign('viewUrl',$url . "?method=listMultiArtists");
$smarty->assign('updateUrl', "'" . $url . "?method=updateMultiArtist&id='+row['" . $fieldId . "']");
$smarty->assign('deleteUrl', $url . "?method=deleteMultiArtist");
//$smarty->assign('deleteUrl', $url . "?method=delete");


$smarty->assign("contacts", array(
        array("field" => "id", "label"=>"Id", "size" => 40, "editable" => false),
        array("field" => "description", "label"=>"Artists", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "description2", "label"=>"ArtistSequence", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "master", "label"=>"Master", "size" => 20, "required" => true, "sortable" => true),
        array("field" => "exactPosition", "label"=>"Exact", "size" => 15, "formatter" => "checkboxFormatter",
            "align" => "center", "checkbox" => true)
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>
<div><button onclick="refreshMultiArtist()">Refresh</button></div>

<script>
    function refreshMultiArtist(){
        $('#dgMultiArtistList').datagrid('reload');
    }
    function addMultiArtist(){
        var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'MultiArtistView.php') ?>";
        var win = window.open(url, '_blank');
        win.focus();
    }
    function editMultiArtist(){
        var selectedRow = $('#dgMultiArtistList').edatagrid("getSelected");
        var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'MultiArtistView.php') ?>" + '?id='+selectedRow.id;
        var win = window.open(url, '_blank');
        win.focus();
    }
</script>
<script type="text/javascript">
    $(function(){
        var dg = $('#dgMultiArtistList').datagrid();
        dg.datagrid('enableFilter');
    });
</script>
