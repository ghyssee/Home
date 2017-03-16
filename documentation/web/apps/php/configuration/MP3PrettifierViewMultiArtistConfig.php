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
        [{"id":"FEAT","value2":" Feat "},{"id":"AMP","value2":" & "},{"id":"KOMMA","value2":", "}];
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
        <script>
            var splitters = [
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
<button onclick="insert()">Click me</button>
<button onclick="clear()">Clear</button>
<button onclick="validateAndSave()">Save</button>
<script>
    $('#artistDl').datagrid('enableDnd');

    function saveCombo(){
        var selectedrow = $("#artistDl").edatagrid("getSelected");
        if (selectedrow != null){
            var rowIndex = $("#artistDl").edatagrid("getRowIndex", selectedrow);
            var ed = $("#artistDl").edatagrid('getEditor', {
                index: rowIndex,
                field: 'splitterId'
            });
            selectedrow.splitterId = $(ed.target).combobox('getValue');
            selectedrow.splitterName = $(ed.target).combobox('getText');
        }
    }

    function clear(){
        $('#artistDl').edatagrid('loadData', {"total":0,"rows":[]});
        $('#artistDl').edatagrid('enableDnd');
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
                        splitterId: 'AMP',
                        splitterName: ''
                    }
                });

                $('#dl').datagrid('clearSelections');
                $('#artistDl').edatagrid('enableDnd');
                $('#artistDl').edatagrid('selectRow', index);
                var selectedRow = $("#artistDl").edatagrid("getSelected");
                saveCombo();
                //$('#artistDl').edatagrid('editRow', index);
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

    function validateAndSave(){
        var rows = $('#artistDl').datagrid('getRows');
        if (rows == null || rows.length <= 1){
            alert("At least two rows must be added!");
        }
        else {
            console.log(JSON.stringify(rows, null, 4));
        }
    }
</script>
