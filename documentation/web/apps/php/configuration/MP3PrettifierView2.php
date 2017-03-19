<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
$multiArtist = readJSONWithCode(JSON_MULTIARTIST);
?>
<html xmlns:OnChange="http://www.w3.org/1999/xhtml">
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
        </div>
    </div>
</div>
<script>
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

        var object = {artists:rowsArtists, artistSequence:rowsSeq};
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

</body>
</html>

