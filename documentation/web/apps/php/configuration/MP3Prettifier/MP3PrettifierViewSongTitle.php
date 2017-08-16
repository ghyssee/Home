<?php
include_once('Smarty.class.php');

$fieldId = "id";
$smarty = initializeSmarty();
$smarty->assign('title','Song Titles');
$smarty->assign('item','Song Title');
$smarty->assign('tableWidth','100%');
$smarty->assign('tableHeight','400px');
$url = "MP3PrettifierAction.php";
$type = "song";
$category = "replacements";
$smarty->assign('tablegrid',"SongTitle");
$smarty->assign('id',$fieldId);
$smarty->assign('fitColumns',"true");
$smarty->assign('viewUrl',constructUrl($url, "list", $type, $category));
$smarty->assign('updateUrl',"'" . constructUrl($url, "update&id='+row['" . $fieldId . "']+'", $type, $category) . "'");
$smarty->assign('newUrl',"'" . constructUrl($url, "add", $type, $category) . "'");
$smarty->assign('deleteUrl', constructUrl($url, "delete", $type, $category));
$smarty->assign('onLoadSuccess', "");

$smarty->assign("contacts", array(
        array("field" => "oldWord", "label"=>"Old Word", "size" => 30, "required" => true, "sortable" => true),
        array("field" => "newWord", "label"=>"New Word", "size" => 30, "sortable" => true),
        array("field" => "parenthesis", "label"=>"Parenthesis", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "exactMatch", "label"=>"Exact Match", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "beginOfWord", "label"=>"Begin Of Word", "size" => 10, "formatter" => "checkboxFormatter", "align" => "center", "checkbox" => true),
        array("field" => "endOfWord", "label"=>"End Of Word", "size" => 10, "type" => "number", "sortable" => true),
        array("field" => "id", "label"=>"Id", "hidden" => "true")
    )
);
//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');
?>

<script type="text/javascript">
    $(function(){
        var dg = $('#dgSongTitle').datagrid();
        dg.datagrid('enableFilter',);
    });
</script>