<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
?>
<html xmlns:OnChange="http://www.w3.org/1999/xhtml">
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
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/metro-blue/easyui.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/icon.css')?>">
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS, 'jquery-3.1.1.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.easyui.min.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'datagrid-dnd.js')?>"></script>

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
        <script>
            var splitters = [
                {id:'FEAT',value2:' Feat '},
                {id:'AMP',value2:' & '},
                {id:'WITH',value2:' With '}
            ];
        </script>

        <table id="artistDl" class="easyui-datagrid" title="Basic DataGrid" style="width:200px;height:250px"
               data-options="singleSelect:true,
                            onLoadSuccess:function(){
                                $(this).datagrid('enableDnd');
	                         }
               "
        >
            <thead>
            <tr>
                <th data-options="field:'id',hidden:true">ID</th>
                <th data-options="field:'name',width:100">Name</th>
                <th data-options="field:'splitterId',
                                  width:98,
                                  editor:{
                                    type:'combobox',
                                    options:{
                                        valueField:'id',
                                        textField:'value2',
                                        data:splitters,
                                        required:false,
                                        onSelect: function (row){ test(row) }
                                    }
                                  }
                                  "
                >Splitter</th>
            </tr>
            </thead>
        </table>

    </div>
</div>
<button onclick="insert()">Click me</button>
<button onclick="clear()">Clear</button>
<button onclick="save()">Save</button>
<script>
    //$('#artistDl').datagrid('enableDnd');
    $('#artistDl').datagrid({
        onClickRow: function(index,row){
            onClickRow(index,row);
        },
        onEndEdit: function(row) {
            alert("onendedit");
        },
        onStopDrag: function(row) {
             var rowIndex = $("#artistDl").datagrid("getRowIndex", row);
             $('#artistDl').datagrid('beginEdit', rowIndex);
        },
        onBeforeSave: function(index){
            alert("onbeforesave");
            var ed = $(this).datagrid('getEditor', {
                index: index,
                field: 'id'
            });
            var row = $(this).datagrid('getRows')[index];
            row.splitterName = $(ed.target).combobox('getText');
        }
    });
    function test(row){
        onEndEdit3(row);
    }

    function onClickRow(index, row){
        alert("onclickrow");
        $('#dg').datagrid('selectRow', index);
    }

    function onEndEdit2(row){
        var selectedrow = $("#artistDl").datagrid("getSelected");
        if (selectedrow != null) {
            var rowIndex = $("#artistDl").datagrid("getRowIndex", selectedrow);
            alert(selectedrow.id);
            alert("rowindex = " + rowIndex);
            alert("row = " + row);
            $('#artistDl').datagrid('beginEdit', rowIndex);
            var ed = $("#artistDl").datagrid('getEditor', {
                index: rowIndex,
                field: 'splitterId'
            });
            alert("ed = " + ed);
            if (ed != null) {
                selectedrow.splitterName = $(ed.target).combobox('getText');
            }
            console.log(JSON.stringify(selectedrow, null, 4));
        }
    }

    function onEndEdit3(row){
        alert("Save");
        var selectedrow = $("#artistDl").datagrid("getSelected");
        alert(selectedrow);
        if (selectedrow != null) {
            selectedrow.splitterId = row.id;
            selectedrow.splitterName = row.value2;
            console.log(JSON.stringify(row, null, 4));
            console.log(JSON.stringify(selectedrow, null, 4));
        }
    }

    function clear(){
        $('#artistDl').datagrid('loadData', {"total":0,"rows":[]});
        $('#artistDl').datagrid('enableDnd');
    }


    function insert(){
        var row = $('#dl').datagrid('getSelected');
        if (row){
            var rows = $('#artistDl').datagrid('getRows');
            //console.log(JSON.stringify(rows, null, 4));
            var alreadyAdded = false;
            if (rows == null) return;
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
                    splitterId: "FEAT"
                });
                $('#dl').datagrid('clearSelections');
                $('#artistDl').datagrid('enableDnd');
                $('#artistDl').datagrid('beginEdit', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
    }

    function getSplitter(id){
        for (var i=0; i < products.length; i++){
            if (products[i].id == id){
                return products[i].value2;
            }
        }
        return "";
    }

    function save2(rows){

        var object = {artists:rows, artistSequence:'blabla2'};
        var tmp = $.post('MP3PrettifierAction.php?method=saveMulti', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    $('#artistDl').datagrid('loadData', {"total":0,"rows":[]});
                }
                else {
                    alert("Data Not Saved!");
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

    function save(){
        var rows = $('#artistDl').datagrid('getRows');
        if (rows == null || rows.length <= 1){
            alert("At least two rows must be added!");
        }
        else {
            console.log(JSON.stringify(rows, null, 4));
        }
    }
</script>


</body>
</html>

