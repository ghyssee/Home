<?php
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
    chdir("..");
    include_once "../setup.php";
    include_once documentPath (ROOT_PHP, "config.php");
    include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
    include_once documentPath (ROOT_PHP_HTML, "config.php");
    include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
    include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
    $multiArtist = readJSONWithCode(JSON_MULTIARTIST);
?>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.edatagrid.js')?>"></script>
</head>

<body>

<?php
sessionStart();
goMenu();
if (isset($_REQUEST["id"])) {
    //$artistSongId = htmlspecialchars($_REQUEST['id']);
    //$artistSongRelationShipBO = new ArtistSongRelationShipBO();
    //$artistSongRelationshipTO = $artistSongRelationShipBO->findArtistSongRelationship($artistSongId);
    //$artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO($artistSongRelationshipTO);
}
else {
    //$artistSongId = null;
    //$artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO(null);
}
?>
<script>
    var oldData = [{"id":"1", "name":"test"}];
</script>
<h1>Multi Artist Configuration</h1>
<div class="horizontalLine">.</div>
<br>

<form id="multiArtistForm">

    <div id="cc" class="easyui-layout" style="width:1080px;height:480px;">
        <div id="leftPanel"
             data-options="region:'west',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title="Artist List"
        >
            <div style="padding-top:8px;padding-left:8px;padding-bottom:0px">
                List Artists
            </div>
            <div style="padding:8px">

                <input id="cbArtist" class="easyui-combobox" name="cbArtist"
                       data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'"
                >
            </div>
            <div style="padding:8px">
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
                    <tr><td>Config</td><td><input id="multiArtistConfig" name="multiArtistConfig"><button type="button" onclick="addConfig()">Add</button></td></tr>

                </table>
            </div>


        </div>
        <div id="actions"
             data-options="region:'center',collapsible:false, border:false"
             style="text-align:center;padding:0px;width:16%;height:95%"
             title="Actions"
        >
            <div><button type="button" style="width:80%" onclick="insert()">Add</button></div>
            <div><button type="button" style="width:80%" onclick="clearArtists()">Clear</button></div>
            <div><button type="button" style="width:80%" onclick="validateAndSave()">Save</button></div>
            <div><button type="button" style="width:80%" onclick="removeDatagridRow('dgArtist','Please select an artist to remove!')">Remove A1</button></div>
            <div><button type="button" style="width:80%" onclick="removeDatagridRow('dgArtistSeq', 'Please select an artist to remove!')">Remove A2</button></div>
            <div><button type="button" style="width:80%" onclick="refreshCombo('cbArtist')">Refresh Art.</button></div>
            <div><button type="button" style="width:80%" onclick="testChk()">Test Check</button></div>
        </div>
        <div id="rightPanel"
             data-options="region:'east',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title="Selected Artists"
        >

            <table id="dgArtist" class="easyui-datagrid" style="width:400px;height:150px"
                   title="Unordered Artist Group"
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
                    title="Ordered Artist Group"
                    singleSelect="true"
            >
                <thead>
                <tr>
                    <th field="artistId" hidden="true">ID</th>
                    <th field="artistName" width="70%">Artist</th>
                    <th field="splitterId" width="30%" align="right"
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
</form>

<script>
    function testChk(){
        alert($('input[name=master]:checked').val());
    }

    function refreshAllLists(){
        refreshList('dlArtistList');
        refreshList('dgMultiArtistList');
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
        clearMultiArtistConfig();
    }

    function clearMultiArtistConfig(){
        document.getElementById("multiArtistConfig").value="";
    }

    function getCmbArtist(cmbId) {
        cmbId = '#cbArtist';
        var _options = $(cmbId).combobox('options');
        var _data = $(cmbId).combobox('getData');
        var _value = $(cmbId).combobox('getValue');
        var _text = $(cmbId).combobox('getText');
        var _b = false;
        var row = {id:null, value:null};
        for (var i = 0; i < _data.length; i++) {
            if (_data[i][_options.valueField] == _value) {
                _b = true;
                row.id = _value;
                row.value = _text;
                break;
            }
        }
        if (!_b) {
            $(cmbId).combobox('setValue', '');
            row = null;
        }
        return row;
    }

    function insert(){
        //var row = $('#dlArtistList').datagrid('getSelected');
        var cbArtist = '#cbArtist';
        var row = getCmbArtist(cbArtist);
        if (row != null){
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
                    name: row.value,
                    id: row.id
                });
                var index = $('#dgArtistSeq').edatagrid('getRows').length;
                $('#dgArtistSeq').edatagrid('addRow',{
                    index: index,
                    row:{
                        artistId: row.id,
                        artistName: row.value,
                        splitterId: DEFAULT_SPLITTER.id,
                        splitterName: DEFAULT_SPLITTER.value2
                    }
                });

                //$('#dlArtistList').datagrid('clearSelections');
                $(cbArtist).combobox('clear');
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

    function savedOK(){
        clearArtists();
        alert("Multi Artist Config Item successfully saved");
        $('#dgMultiArtistList').datagrid('reload');
    }

    function saveMulti(rowsArtists, rowsSeq){

        var object = {artists:rowsArtists,
            artistSequence:rowsSeq,
            exactPosition: $('#exactPosition').is(":checked"),
            master: $('input[name=master]:checked').val()

        };
        saveObject(object, 'MP3PrettifierAction.php?method=saveMulti', savedOK);
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
            saveMulti(rowsArtists, rowsSeq);
        }
    }

    function addConfig(){

        var object = {multiArtistConfig:($("#multiArtistConfig").val())
        };
        if (!object.multiArtistConfig){
            alert("Multi Artist Config field must be filled in!");
            return;
        }
        saveObject(object, 'MP3PrettifierMultiArtistAction.php?method=addMulti', clearMultiArtistConfig);
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
