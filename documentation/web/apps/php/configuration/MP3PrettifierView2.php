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
        width: 15%;
    }
    #column2 {
        float: left ;
        width: 8% ;
    }
    #column3 {
        float: left ;
        width: 20% ;
    }    #innercolumn {
        padding-left: 5px ;
        padding-right: 5px ;
    }
</style>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/metro-blue/easyui.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/icon.css')?>">
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS, 'jquery-3.1.1.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.easyui.min.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.edatagrid.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'datagrid-dnd.js')?>"></script>

</head>

<body>

<div id="container">
    <div id="column1">
        <div id="innercolumn">
            <div id="dl" class="easyui-datalist" title="Remote Data" style="width:200px;height:400px"
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
    <div id="column2">
        <div id="innercolumn">
            <div><button onclick="insert()">Add</button></div>
            <div><button onclick="clear1()">Clear</button></div>
            <div><button onclick="validateAndSave()">Save</button></div>
        </div>
    </div>
    <div id="column3">
        <div id="innercolumn">
            <table id="dgArtist" class="easyui-datagrid" style="width:400px;height:100px"
                   data-options="fitColumns:true,singleSelect:true">
                <thead>
                <tr>
                    <th data-options="field:'id',hidden:true">Id</th>
                    <th data-options="field:'name',width:100">Artist</th>
                </tr>
                </thead>
            </table>

            <br>


            <script>
                var splitters = [
                    {id:'FEAT',value2:' Feat '},
                    {id:'AMP',value2:' & '},
                    {id:'WITH',value2:' With '}
                ];
                var DEFAULT_SPLITTER = splitters[0];
                $(function(){
                    $('#dgArtistSeq').edatagrid({
                    });
                });
            </script>
            <table  id="dgArtistSeq" style="width:400px;height:150px"
                    title="Editable DataGrid"
                    singleSelect="true"
            >
                <thead>
                <tr>
                <th field="id" width="100" hidden="true">ID</th>
                <th field="name" width="198">Artist</th>
                <th field="splitterId" width="100" align="right"
                    data-options="
                            formatter:function(value,row){
                                        return row.splitterName;
                                       }"
                    editor="{type:'combobox',
                             options:{valueField:'id',
                                    textField:'value2',
                                    data:splitters,
                                    required:true,
                                    onChange:function(newValue,oldValue){
                                                saveCombo();
                                            },
                                    onSelect:function(row) {
                                            }
                                    }
                            }
                       ">Splitter
                </th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
<script>
    $('#dgArtistSeq').datagrid('enableDnd');

    function saveCombo(){
        var selectedrow = $("#dgArtistSeq").edatagrid("getSelected");
        if (selectedrow != null){
            var rowIndex = $("#dgArtistSeq").edatagrid("getRowIndex", selectedrow);
            var ed = $("#dgArtistSeq").edatagrid('getEditor', {
                index: rowIndex,
                field: 'splitterId'
            });
            selectedrow.splitterId = $(ed.target).combobox('getValue');
            selectedrow.splitterName = $(ed.target).combobox('getText');
        }
    }

    function clear1(){
        $('#dgArtistSeq').edatagrid('loadData',[]);
    }


    function insert(){
        var row = $('#dl').datagrid('getSelected');
        if (row){
            var rows = $('#dgArtistSeq').edatagrid('getRows');
            console.log(JSON.stringify(rows, null, 4));
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                $('#dgArtist').datagrid('appendRow',{
                    name: row.name,
                    id: row.id
                });
                var index = $('#dgArtistSeq').edatagrid('getRows').length;
                $('#dgArtistSeq').edatagrid('addRow',{
                    index: index,
                    row:{
                        id: row.id,
                        name: row.name,
                        splitterId: DEFAULT_SPLITTER.id,
                        splitterName: DEFAULT_SPLITTER.value2
                    }
                });

                $('#dl').datagrid('clearSelections');
                $('#dgArtistSeq').datagrid('enableDnd');
                $('#dgArtistSeq').edatagrid('selectRow', index);
                var selectedRow = $("#dgArtistSeq").edatagrid("getSelected");
                saveCombo();
                //$('#dgArtistSeq').edatagrid('editRow', index);
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

    function save(rows){

        var object = {artists:rows, artistSequence:'blabla2'};
        var tmp = $.post('MP3PrettifierAction.php?method=saveMulti', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    $('#dgArtistSeq').datagrid('loadData', {"total":0,"rows":[]});
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

    function validateAndSave(){
        var rows = $('#dgArtistSeq').datagrid('getRows');
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

