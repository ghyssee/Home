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
</style>
<script>
    var splitters =
        [{"id":"FEAT","value2":" Feat "},{"id":"AMP","value2":" & ",{"id":"KOMMA","value2":", "}];
</script>
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
                <th data-options="field:'splitter',
                                  width:98,
                                  editor:{
                                    type:'combobox',
                                    options:{
                                        valueField:'id',
                                        textField:'value2',
                                        data:splitters,
                                        required:false
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
<script>
    function clear(){
        $('#artistDl').datagrid('loadData', {"total":0,"rows":[]});
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
                var index = $('#artistDl').datagrid('getRows').length  + 1;
                $('#artistDl').datagrid('appendRow',{
                    id: row.id,
                    name: row.name,
                    splitter: "FEAT"
                });
                $('#dl').datagrid('clearSelections');
                $('#artistDl').datagrid('enableDnd');
                $('#artistDl').datagrid('selectRow', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
    }
</script>