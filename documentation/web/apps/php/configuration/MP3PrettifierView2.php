<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
?>
<html>
<style>
    #column1 {
        float: left;
        width: 50%;
    }
    #column2 {
        float: right ;
        width: 50% ;
    }
    #innercolumn1 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
    #innercolumn2 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
</style>
<script>
    var data = {"total":2,"rows":[
        {"id":"FI-SW-01","name":"BlaBlaKoi"},
        {"id":"K9-DL-01","name":"Dalmation"}
    ]};
</script>

<head>
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>

<body>

<div id="container">
    <div id="column1">
        <div id="innercolumn1">
            <div id="innercolumn2">
                <div id="dl" class="easyui-datalist" title="Remote Data" style="width:200px;height:250px"
                     data-options="
                                url: 'MP3PrettifierAction.php?method=listArtists',
                                method: 'get',
                                lines: 'true',
                                valueField: 'id',
                                singleSelect: false,
                                textField: 'name'
                             "
                >
                </div>
            </div>
        </div>
    </div>
    <div id="column2">
        <table id="artistDl"></table>
        <script>
            var products = [
                {id:'FEAT',value2:' Feat '},
                {id:'AMP',value2:' & '},
                {id:'WITH',value2:' With '}
            ];
            $(function(){
                $('#artistDl').datagrid({
                    title:'Editable DataGrid',
                    iconCls:'icon-edit',
                    width:400,
                    height:250,
                    singleSelect:true,
                    idField:'id',
                    onStopDrag: function(row){
                        var rowIndex = $('#artistDl').datagrid('getRowIndex', row);
                        var ed = $(artistDl).datagrid('getEditor', {
                            index: rowIndex+1,
                            field: 'splitterId'
                        });
                        alert(row);
                        row.splitterId = $(ed.target).combobox('getValue');
                        //console.log(JSON.stringify(ed, null, 4));
                        console.log(JSON.stringify(row, null, 4));
                        //console.log(JSON.stringify(ed, null, 4));
                        $('#artistDl').datagrid('endEdit', rowIndex);
                        $('#artistDl').datagrid('beginEdit', rowIndex);
                        alert(rowIndex);
                    },
                    columns:[[
                        {field:'id',title:'ID',hidden:true},
                        {field:'name', title:'Name', width:100},
                        {field:'splitterId',title:'Splitter',width:100,
                            editor:{
                                type:'combobox',
                                options:{
                                    valueField:'id',
                                    textField:'value2',
                                    data:products,
                                    required:true
                                }
                            }
                        }
                    ]],
                    onEndEdit:function(index,row){
                        alert("onendedit");
                        var ed = $(this).datagrid('getEditor', {
                            index: index,
                            field: 'type'
                        });
                        row.splitterId = $(ed.target).combobox('getText');
                    },
                    onBeforeEdit:function(index,row){
                        //alert("onbeforeedit");
                        row.editing = true;
                        $(this).datagrid('checkRow',index);
                        $(this).datagrid('refreshRow', index);
                    },
                    onAfterEdit:function(index,row){
                        alert("afteredit");
                        row.editing = false;
                        $(this).datagrid('refreshRow', index);
                    },
                });
            });
        </script>

    </div>
</div>
<button onclick="insert()">Click me</button>
<button onclick="clear()">Clear</button>
<button onclick="save()">Save</button>
<script>
    $('#artistDl').datagrid('enableDnd');
    function clear(){
        $('#artistDl').datagrid('loadData', {"total":0,"rows":[]});
        $('#artistDl').datagrid('enableDnd');
    }
    function insert(){
        var row = $('#dl').datagrid('getSelected');
        if (row){
            var rows = $('#artistDl').datagrid('getRows');
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                var index = $('#artistDl').datagrid('getRows').length;
                $('#artistDl').datagrid('appendRow',{
                    id: row.id,
                    name: row.name,
                    splitterId: 'FEAT'
                });
                $('#dl').datagrid('clearSelections');
                $('#artistDl').datagrid('enableDnd');
                $('#artistDl').datagrid('selectRow', index);
                $('#artistDl').datagrid('beginEdit', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
    }
    function saverow(target){
        var row = $('#tt').datagrid('getSelected');
        update(row);
        $('#tt').datagrid('endEdit', getRowIndex(target));
    }
    function save(){
        var rows = $('#artistDl').datagrid('getRows');
        if (rows == null || rows.length <= 1){
            alert("At least two rows must be added!");
        }
    }
</script>


</body>
</html>

