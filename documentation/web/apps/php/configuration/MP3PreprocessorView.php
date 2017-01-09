<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
        <link rel="stylesheet" type="text/css" href="../../css/form.css">
        <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
        <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
    </head>
</head>
<body>

<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .inlineTable {
        float: left;
    }

    th {
        text-align: left;
    }

</style>


<?php
goMenu();
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Splitter();
}
?>
<h1>MP3Preprocessor Settings</h1>
<h3>Splitters</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "splitterId",
        'size' => 30,
        'label' => 'Id',
        'value' => $splitter->id)));
    $layout->inputBox(new Input(array('name' => "pattern",
        'size' => 30,
        'label' => 'Pattern',
        'value' => $splitter->pattern)));
    $layout->button(new Input(array('name' => "mp3Preprocessor",
        'value' => 'addSplitter',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>
<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('title','Splitter');
$smarty->assign('item','Splitter Item');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PreprocessorAction.php";
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['id']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['id']}");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "description", "label"=>"Description", "size" => 100, "sortable" => true),
        array("field" => "pattern", "label"=>"Pattern", "size" => 20)
    )
);
$smarty->display('TableGridV3.tpl');
?>

<table id="tt"></table>


<script>
    var products = [
        {productid:'FI-SW-01',name:'Koi'},
        {productid:'K9-DL-01',name:'Dalmation'},
        {productid:'RP-SN-01',name:'Rattlesnake'},
        {productid:'RP-LI-02',name:'Iguana'},
        {productid:'FL-DSH-01',name:'Manx'},
        {productid:'FL-DLH-02',name:'Persian'},
        {productid:'AV-CB-01',name:'Amazon Parrot'}
    ];
    $(function(){
        $('#tt').datagrid({
            title:'Editable DataGrid',
            iconCls:'icon-edit',
            width:660,
            height:250,
            singleSelect:true,
            idField:'itemid',
            url:'data/datagrid_data.json',
            columns:[[
                {field:'itemid',title:'Item ID',width:60},
                {field:'productid',title:'Product',width:100,
                    formatter:function(value,row){
                        return row.productname || value;
                    },
                    editor:{
                        type:'combobox',
                        options:{
                            valueField:'productid',
                            textField:'name',
                            data:products,
                            required:true
                        }
                    }
                },
                {field:'listprice',title:'List Price',width:80,align:'right',editor:{type:'numberbox',options:{precision:1}}},
                {field:'unitcost',title:'Unit Cost',width:80,align:'right',editor:'numberbox'},
                {field:'attr1',title:'Attribute',width:180,editor:'text'},
                {field:'status',title:'Status',width:50,align:'center',
                    editor:{
                        type:'checkbox',
                        options:{
                            on: 'P',
                            off: ''
                        }
                    }
                },
                {field:'action',title:'Action',width:80,align:'center',
                    formatter:function(value,row,index){
                        if (row.editing){
                            var s = '<a href="javascript:void(0)" onclick="saverow(this)">Save</a> ';
                            var c = '<a href="javascript:void(0)" onclick="cancelrow(this)">Cancel</a>';
                            return s+c;
                        } else {
                            var e = '<a href="javascript:void(0)" onclick="editrow(this)">Edit</a> ';
                            var d = '<a href="javascript:void(0)" onclick="deleterow(this)">Delete</a>';
                            return e+d;
                        }
                    }
                }
            ]],
            onEndEdit:function(index,row){
                var ed = $(this).datagrid('getEditor', {
                    index: index,
                    field: 'productid'
                });
                row.productname = $(ed.target).combobox('getText');
            },
            onBeforeEdit:function(index,row){
                row.editing = true;
                $(this).datagrid('refreshRow', index);
            },
            onAfterEdit:function(index,row){
                row.editing = false;
                $(this).datagrid('refreshRow', index);
            },
            onCancelEdit:function(index,row){
                row.editing = false;
                $(this).datagrid('refreshRow', index);
            }
        });
    });
    function getRowIndex(target){
        var tr = $(target).closest('tr.datagrid-row');
        return parseInt(tr.attr('datagrid-row-index'));
    }
    function editrow(target){
        $('#tt').datagrid('beginEdit', getRowIndex(target));
    }
    function deleterow(target){
        $.messager.confirm('Confirm','Are you sure?',function(r){
            if (r){
                $('#tt').datagrid('deleteRow', getRowIndex(target));
            }
        });
    }
    function saverow(target){
        $('#tt').datagrid('endEdit', getRowIndex(target));
    }
    function cancelrow(target){
        $('#tt').datagrid('cancelEdit', getRowIndex(target));
    }
    function insert(){
        var row = $('#tt').datagrid('getSelected');
        if (row){
            var index = $('#tt').datagrid('getRowIndex', row);
        } else {
            index = 0;
        }
        $('#tt').datagrid('insertRow', {
            index: index,
            row:{
                status:'P'
            }
        });
        $('#tt').datagrid('selectRow',index);
        $('#tt').datagrid('beginEdit',index);
    }
</script>

<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html> 

