<?php
$multiArtist = readJSONWithCode(JSON_MULTIARTIST);
?>
<style>
    #column1 {
        float: left;
        width: 30%;
    }
    #column2 {
        float: left ;
        width: 20% ;
    }
    #column3 {
        float: left ;
        width: 50% ;
    }    #innercolumn {
             padding-left: 5px ;
             padding-right: 5px ;
         }
</style>

<div id="container">
    <div id="column1">
        <div id="innercolumn">
            <div id="dlArtistList" class="easyui-datalist" title="Remote Data" style="width:200px;height:400px"
                 data-options="
                            url: 'MP3PrettifierAction.php?method=listArtists',
                            method: 'get',
                            lines: 'true',
                            valueField: 'id',
                            singleSelect: true,
                            textField: 'name'
                         "
            >
            </div>
        </div>
    </div>
    <div id="column2">
        <div id="innercolumn">
            <div><button onclick="insert()">Add</button></div>
            <div><button onclick="clearArtists()">Clear</button></div>
            <div><button onclick="validateAndSave()">Save</button></div>
            <div><button onclick="removeArtist('dgArtist')">Remove A1</button></div>
            <div><button onclick="removeArtist('dgArtistSeq')">Remove A2</button></div>
            <div><button onclick="refreshList('dlArtistList')">Refresh Art.</button></div>
            <div><button onclick="testChk()">Test Check</button></div>
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
                var splitters = <?php echo getSplitters(); ?>;
                var SPLITTER_EOL = <?php echo getEOL(); ?>;
                var DEFAULT_SPLITTER = splitters[0];
                $(function(){
                    $('#dgArtistSeq').edatagrid({
                    });
                });
            </script>
            <table  id="dgArtistSeq" style="width:400px;height:150px"
                    title="Artist Sequence"
                    singleSelect="true"
            >
                <thead>
                <tr>
                    <th field="artistId" width="100" hidden="true">ID</th>
                    <th field="artistName" width="198">Artist</th>
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
            <table>
                <tr><td colspan="2">Master</td></tr>
                <tr><td>
                        <input type="radio" name="master"
                               value="<?php echo MULTIARTIST_RADIO_ARTISTS ?>">Artist
                    </td>
                <td>
                    <input type="radio" name="master" checked
                               value="<?php echo MULTIARTIST_RADIO_ARTISTSEQUENCE ?>">Artist Sequence
                </td>
                </tr>
                <tr><td colspan = 2><input type="checkbox" id="exactPosition" name="exactPosition" value="0">Exact Position</td></tr>
                <tr><td>Config</td><td><input id="multiArtistConfig" name="multiArtistConfig"><button onclick="addConfig()">Add</button></td></tr>

            </table>
        </div>
    </div>
</div>
<script>
    function testChk(){
        alert($('input[name=master]:checked').val());
    }

    function refreshList(datagridName){
        datagridName = "#" + datagridName;
        $(datagridName).datagrid('reload');
    }
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
        console.log(JSON.stringify(selectedrow, null, 4));
    }

    function clearArtists(){
        $('#dgArtistSeq').edatagrid('loadData',[]);
        $('#dgArtist').datagrid('loadData',[]);
        $( "#exactPosition").prop('checked', false);
    }

    function removeArtist(datagridName){
        datagridName = "#" + datagridName;
        var selectedRow = $(datagridName).edatagrid("getSelected");
        if (selectedRow == null){
            alert("Please select an artist to remove!");
        }
        else {
            var rowIndex = $(datagridName).edatagrid("getRowIndex", selectedRow);
            $(datagridName).edatagrid("deleteRow", rowIndex);
        }
    }

    function removeArtistSequence(){
        var selectedRow = $("#dgArtistSeq").edatagrid("getSelected");
        if (selectedRow == null){
            alert("Please select an artist to remove!");
        }
        else {
            var rowIndex = $("#dgArtistSeq").edatagrid("getRowIndex", selectedRow);
            $("#dgArtistSeq").edatagrid("deleteRow", rowIndex);
        }
    }


    function insert(){
        var row = $('#dlArtistList').datagrid('getSelected');
        if (row){
            var rows = $('#dgArtistSeq').edatagrid('getRows');
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
                        artistId: row.id,
                        artistName: row.name,
                        splitterId: DEFAULT_SPLITTER.id,
                        splitterName: DEFAULT_SPLITTER.value2
                    }
                });

                $('#dlArtistList').datagrid('clearSelections');
                $('#dgArtistSeq').datagrid('enableDnd');
                $('#dgArtistSeq').edatagrid('selectRow', index);
                saveCombo();
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

    function save(rowsArtists, rowsSeq){

        var object = {artists:rowsArtists,
                      artistSequence:rowsSeq,
                      exactPosition: $('#exactPosition').is(":checked"),
                      master: $('input[name=master]:checked').val()

        };
        var tmp = $.post('MP3PrettifierAction.php?method=saveMulti', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    clearArtists();
                    alert("Multi Artist Config Item successfully saved");
                    $('#dgMultiArtistList').datagrid('reload');
                }
                else {
                    if (data2.message != null && data2.message != '') {
                        alert(data2.message);
                    }
                    else {
                        alert("Data Not Saved!");
                    }
                }
            },'json')
            .done(function() {
                //alert( "second success" );
            })
            .fail(function(response) {
                alert( "error: "  + response.responseText);
            })
            .always(function() {
                //alert( "finished" );
            });
        tmp.always(function() {
            //alert( "second finished" );
        });
        return false;
    }

    function validateAndSave(){
        var rowsSeq = $('#dgArtistSeq').datagrid('getRows');
        var rowsArtists = $('#dgArtist').datagrid('getRows');
        if (rowsSeq == null || rowsSeq.length <= 1){
            alert("At least two rows must be added!");
        }
        else {
            // set last Splitter to EOL
            lastRow = rowsSeq[rowsSeq.length-1];
            lastRow.splitterId = SPLITTER_EOL;
            //console.log(JSON.stringify(rowsArtists, rowsSeq, null, 4));
            save(rowsArtists, rowsSeq);
        }
    }

    function addConfig(){

        alert("addconfig");
        var object = {multiArtistConfig:($("#multiArtistConfig").val())
                     };
        var tmp = $.post('MP3PrettifierMultiArtistAction.php?method=addMulti', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    clearArtists();
                    alert("Multi Artist Config Item successfully saved");
                    $('#dgMultiArtistList').datagrid('reload');
                }
                else {
                    if (data2.message != null && data2.message != '') {
                        alert(data2.message);
                    }
                    else {
                        alert("Data Not Saved!");
                    }
                }
            },'json')
            .done(function() {
                //alert( "second success" );
            })
            .fail(function(response) {
                alert( "error: "  + response.responseText);
            })
            .always(function() {
                //alert( "finished" );
            });
        tmp.always(function() {
            //alert( "second finished" );
        });
        return false;
    }

</script>

<?php
function getSplitters(){
    global $multiArtist;
    return json_encode($multiArtist->splitters);
}
function getEOL(){
    global $multiArtist;
    return json_encode($multiArtist->splitterEndId);

}
?>
