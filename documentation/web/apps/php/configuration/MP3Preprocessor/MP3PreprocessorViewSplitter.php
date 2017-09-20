<?php
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Delimiter();
}
?>

<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('tablegrid',"Splitters");
$smarty->assign('title','Splitter');
$smarty->assign('item','Splitter Item');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = getAction();
$smarty->assign('id', 'id');
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['id']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', $url . "?method=delete");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "description", "label"=>"Description", "size" => 100, "sortable" => true),
        array("field" => "pattern", "label"=>"Pattern", "size" => 20)
    )
);
$smarty->display('TableGridV3.tpl');
?>

