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
            var products = [
                {id:'FEAT',value2:' Feat '},
                {id:'AMP',value2:' & '},
                {id:'WITH',value2:' With '}
            ];
            $(function(){
                $('#artistDl').edatagrid({
                });
            });
        </script>
            <table id="artistDl" style="width:400px;height:200px"
            title="Editable DataGrid"
            singleSelect="true">
                <thead>
                <tr>
                <th field="id" width="100" hidden="true">ID</th>
            <th field="name" width="198">Artist</th>
            <th field="splitterId" width="100" align="right"
                    data-options="
                        formatter:function(value,row){
                            return getSplitter(value);
                        }"
                editor="{type:'combobox',
                         options:{valueField:'id',
                                  textField:'value2',
                                  data:products,
                                  required:true
                                 }
                         }
                       ">Splitter
            </th>
                </tr>
                </thead>
                </table>

    </div>
</div>
<button onclick="insert()">Click me</button>
<button onclick="clear()">Clear</button>
<button onclick="save()">Save</button>
<script>
    $('#artistDl').edatagrid('enableDnd');
    function clear(){
        $('#artistDl').datagrid('loadData', {"total":0,"rows":[]});
        $('#artistDl').datagrid('enableDnd');
    }
    function insert(){
        var row = $('#dl').datagrid('getSelected');
        if (row){
            var rows = $('#artistDl').edatagrid('getRows');
            console.log(JSON.stringify(rows, null, 4));
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                var index = $('#artistDl').edatagrid('getRows').length;
                $('#artistDl').edatagrid('addRow',{
                    index: index,
                    row:{
                        id: row.id,
                        name: row.name,
                        splitterId: 'AMP'
                    }
                });

                $('#dl').datagrid('clearSelections');
                $('#artistDl').edatagrid('enableDnd');
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

