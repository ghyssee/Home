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
<div id="container">
    <div id="column1">
        <div id="innercolumn1">
            <div id="innercolumn2">
                <div id="dl" class="easyui-datalist" title="Remote Data" style="width:400px;height:250px" data-options="
                            url: 'MP3PrettifierAction.php?method=listArtists',
                            method: 'get',
                            lines: 'true',
                            valueField: 'id',
                            singleSelect: false,
                            textField: 'name'

                            ">
                </div>
            </div>
        </div>
    </div>
    <div id="column2">
        <div id="artistDl" class="easyui-datalist" title="Remote Data" style="width:400px;height:250px" data-options="
                            method: 'get',
                            lines: 'true',
                            valueField: 'id',
                            textField: 'name'

                            ">
        </div>
    </div>
</div>
<button onclick="insert()">Click me</button>
<script>
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
                $('#artistDl').datagrid('insertRow', {
                    index: index,
                    row: {
                        id: row.id,
                        name: row.name
                    }
                });
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