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
include_once("../bo/AlbumErrorsBO.php");

$albumErrorsBO = new AlbumErrorsBO();
$smarty = initializeSmarty();
$title = 'Album Errors';
$smarty->assign('title', $title);
$smarty->assign('item','Album Errors');
$smarty->assign('tableWidth','100%');
$smarty->assign('tableHeight','700px');
$smarty->assign('id',"uniqueId");
$smarty->assign('tablegrid','');
$url = "AlbumErrorsAction.php";
$smarty->assign('fitColumns',"true");
$smarty->assign('singleSelect','false');
$smarty->assign('pagePosition','both'); // 'top', 'both'
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['uniqueId']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['uniqueId']}");
$smarty->assign("contacts", array(array("field" => "process", "selectRow" => true),
        array("field" => "id", "label"=>"Id", "hidden" => "true"),
        array("field" => "file", "label"=>"File", "size" => 200, "sortable" => true),
        array("field" => "type", "label"=>"Type",  "size" => 50, "sortable" => true),
        array("field" => "oldValue", "label"=>"Old Value", "formatter" => "stripCell", "size" => 200),
        array("field" => "newValue", "label"=>"New Value", "formatter" => "stripCell", "size" => 200)
    )
);

//** un-comment the following line to show the debug console
//$smarty->debugging = true;

$smarty->display('TableGridV4.tpl');

?>
<script type="text/javascript">
    $('#dg').datagrid({
        onLoadSuccess:function(data){
            var rows = $(this).datagrid('getRows');
            var tmpSelectedRows = $('#dg').datagrid('getSelections');
            var count = 0;
            if (tmpSelectedRows == 0) {
                $(this).data('datagrid').checkedRows = data.selectedRows;
                $(this).data('datagrid').selectedRows = data.selectedRows;
                count = data.selectedRows.length;
            }
            else {
                for (i = 0; i < rows.length; ++i) {
                    if (rows[i]['process'] == 1) $(this).datagrid('checkRow', i);
                }
                count = tmpSelectedRows.length;
            }
            var dgPanel = $(this).datagrid('getPanel');
            var title = '<?php echo $title; ?>';
            dgPanel.panel('setTitle', title + '(' + count + ' rows selected)');
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
