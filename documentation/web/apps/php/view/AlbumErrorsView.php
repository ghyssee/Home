<?php
include_once("../setup.php");
?>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body style="background">

<style>
	.datagrid-cell{
		font-size:12px;
	}
	.datagrid-header .datagrid-cell span {
		font-size: 12px;
	}
	.datagrid-row{
		height: 18px;
	}
</style>

<?php
session_start();
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$htmlObj = readJSONWithCode(JSON_ALBUMERRORS);
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);
?>
<?php
goMenu();
?>
<h1>Mezzmo / MP3 Check</h1>
<div class="horizontalLine">.</div>
<br>

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
<form action="AlbumErrorsAction.php?method=updateSettings" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->checkBox(new Input(array('name' => "check",
        'label' => 'Check for modifications',
        'value' => $mp3SettingsObj->mezzmo->mp3Checker->check)));
    $layout->inputBox(new Input(array('name' => "maxNumberOfErrors",
        'size' => 5,
        'label' => 'Max Number Of Errors',
        'type' => 'number',
        'min' => '1',
        'max' => '10000',
        'value' => $mp3SettingsObj->mezzmo->mp3Checker->maxNumberOfErrors)));
    $layout->button(new Input(array('name' => "mp3Check",
        'col' => 1,
        'value' => 'save',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form onsubmit="return myFunction()">
    <input type="submit" value="Process Selected Rows">
</form>

<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('title','Album Errors');
$smarty->assign('item','Album Errors');
$smarty->assign('tableWidth','1500px');
$smarty->assign('tableHeight','500px');
$smarty->assign('id',"uniqueId");
$smarty->assign('tablegrid','');
$url = "AlbumErrorsAction.php";
$smarty->assign('singleSelect','false');
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['uniqueId']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['uniqueId']}");

$smarty->assign("contacts", array(array("field" => "process", "selectRow" => true),
                                  array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
								  array("field" => "done", "label"=>"Done", "size" => 1),
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
<br>
<?php
goMenu();
?>
<br/>
    <div style="margin:10px 0;">
        <span>Selection Mode: </span>
        <select onchange="$('#dg').datagrid({singleSelect:(this.value==0)})">
            <option value="0">Single Row</option>
            <option value="1" selected>Multiple Rows</option>
        </select>
		<br/>
        SelectOnCheck: <input type="checkbox" checked onchange="$('#dg').datagrid({selectOnCheck:$(this).is(':checked')})">
		<br/>
        CheckOnSelect: <input type="checkbox" checked onchange="$('#dg').datagrid({checkOnSelect:$(this).is(':checked')})">
    </div>
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

</body>
</html>

