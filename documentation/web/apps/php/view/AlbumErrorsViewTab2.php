<script type="text/javascript">
    function stripCell(val,row){
        if (row.type == "FILE") {
            return val.replace(row.basePath, "");
        }
        else {
            return val;
        }
    }
</script>


<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('title','Album Errors');
$smarty->assign('item','Album Errors');
$smarty->assign('tableWidth','1200px');
$smarty->assign('tableHeight','700px');
$smarty->assign('id',"uniqueId");
$smarty->assign('tablegrid','');
$url = "AlbumErrorsAction.php";
$smarty->assign('singleSelect','false');
$smarty->assign('pagePosition','both'); // 'top', 'both'
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['uniqueId']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['uniqueId']}");

$smarty->assign("contacts", array(array("field" => "process", "selectRow" => true),
        array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "file", "label"=>"File", "size" => 200),
        array("field" => "type", "label"=>"Type",  "size" => 50),
        array("field" => "oldValue", "label"=>"Old Value", "formatter" => "stripCell", "size" => 200),
        array("field" => "newValue", "label"=>"New Value", "formatter" => "stripCell", "size" => 200)
    )
);

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV3.tpl');

/*
$smarty = initializeSmarty();
$smarty->assign('title','Album Errors');
$smarty->assign('item','Album Errors');
$smarty->assign('tableWidth','1500px');
$smarty->assign('tableHeight','500px');
$url = "AlbumErrorsAction.php";
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['uniqueId']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['uniqueId']}");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "done", "label"=>"Done", "size" => 1),
        array("field" => "file", "label"=>"File", "size" => 200),
        array("field" => "type", "label"=>"Type",  "size" => 50),
        array("field" => "oldValue", "label"=>"Old Value", "formatter" => "stripCell", "size" => 200),
        array("field" => "newValue", "label"=>"New Value", "formatter" => "stripCell", "size" => 200)
    )
);

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGrid2.tpl');
*/

?>
<script type="text/javascript">
    $('#dg').datagrid({
        onLoadSuccess:function(data){
            var rows = $(this).datagrid('getRows');
            for(i=0;i<rows.length;++i){
                if(rows[i]['process']==1) $(this).datagrid('checkRow',i);
            }
        }
    });
</script>
<script type="text/javascript">
    function myFunction(){
        var selectObj = { ids: []}
        var ids = [];
        var rows = $('#dg').datagrid('getSelections');
        for(var i=0; i<rows.length; i++){
            ids.push(rows[i].uniqueId);
        }
        selectObj.ids = ids;
        var tmp = $.post('AlbumErrorsAction.php?method=select', { selectedIds : JSON.stringify(selectObj)}, function(data2){
                if (data2.success){
                    //$('#dg').datagrid('gotoPage', data2.pageNumber);
                    $('#dg').datagrid('reload');
                }
            },'json')
            .done(function() {
                //alert( "second success" );
            })
            .fail(function() {
                //alert( "error" );
            })
            .always(function() {
                //alert( "finished" );
            });
        tmp.always(function() {
            //alert( "second finished" );
        });
        //$('#dg').datagrid('gotoPage', 2);
        return false;
    }
</script>
