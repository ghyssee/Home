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
    include_once documentPath (ROOT_PHP_BO, "MyClasses.php");
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
    $multiArtistId = htmlspecialchars($_REQUEST['id']);
    $multiAritstBO = new MultiArtistBO();
    $multiArtistTO = $multiAritstBO->findMultiArtist($multiArtistId);
    $multiAritstBO->fillMultiArtistInfo($multiArtistTO);
}
else if (isset($_REQUEST["artist"])) {
    $artist = $_REQUEST["artist"];
    $multiArtistId = null;
    $multiArtistTO = new MultiArtist();
    $errorObj = new FeedBackTO();
    $array = Array();
    $multiArtistBO = new MultiArtistBO();
    $multiArtistBO->buildArtists($artist, $errorObj, $array);
    // filter out non existing artists
    $filteredArray = Array();
    foreach ($array as $key=>$item){
        if (isset($item->id)){
            $filteredArray[] = $item;
        }
    }
    if (count($filteredArray) > 0){
        $multiArtistTO = $multiArtistBO->fillMultiArtistForm($filteredArray);
    }
    $multiArtistTO->master = MasterType::SEQUENCE;
}
else {
    $multiArtistId = null;
    $multiArtistTO = new MultiArtist();
    $multiArtistTO->master = MasterType::SEQUENCE;

}
?>
<script>
    var oldData = [{"id":"1", "name":"test"}];
    //var tmpData1 = [{"artistId":"Bodyrox", "artistName":"Bodyrox","splitterId":"FEAT","splitterName":" Feat. "}];
    //var tmpData2 = [{"id":"Bodyrox", "name":"Bodyrox"}];
    var artists = <?php echo json_encode($multiArtistTO->artists); ?>;
    var artistSequence = <?php echo json_encode($multiArtistTO->artistSequence); ?>;
</script>
<h1><?php
        if (isset($multiArtistId)){
            echo " Update ";
        }
        else {
            echo " Add ";
        }
        ?>Multi Artist Configuration</h1>
<div class="horizontalLine">.</div>
<br>

<form id="multiArtistForm">

    <div id="cc" class="easyui-layout" style="width:1080px;height:480px;">
        <div id="leftPanel"
             data-options="region:'west',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title=" "
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
            <div id="p" class="easyui-panel" title="Type"
                 style="width:100%;height:300px;padding:10px;background:#fafafa;"
                 data-options="closable:false,
                    collapsible:false,minimizable:false,maximizable:false">
                <div style="line-height:22px;background:#fafafa;padding:5px;">Master</div>
                <div style="padding:0px">
                    <input type="radio" name="master" value="<?= MasterType::ARTIST?>" <?= radioButton(MasterType::ARTIST, $multiArtistTO->master); ?>><span>Artists</span><br/>
                    <input type="radio" name="master" value="<?= MasterType::SEQUENCE?>" <?= radioButton(MasterType::SEQUENCE, $multiArtistTO->master); ?>><span>Artist Sequence</span><br/>
                </div>
                <div style="padding-top:8px">
                    <?php
                    $input = new Input(array('name' => "exactPosition",
                        'label' => 'Exact Position',
                        'value' => $multiArtistTO->exactPosition));
                    checkBox($input);
                    ?>
                </div>
                <div style="margin-top:10px">
                    <input id="multiArtistConfig"
                           class="easyui-textbox"
                           label="Multi Artist Config"
                           value=""
                           labelPosition="top"
                           style="width:80%;height:52px">
                    <button type="button" onclick="addConfig();">Add</button>
                </div>
            </div>


        </div>
        <div id="actions"
             data-options="region:'center',collapsible:false, border:false"
             style="text-align:center;padding:0px;width:16%;height:95%"
             title=" "
        >
            <div class="verticallyalign">
            <div><button type="button" style="width:80%" onclick="insert()">Add</button></div>
            <div><button type="button" style="width:80%" onclick="clearArtists()">Clear</button></div>
            <div><button type="button" style="width:80%" onclick="validateAndSave()">Save</button></div>
            <div><button type="button" style="width:80%" onclick="removeDatagridRow('dgArtist','Please select an artist to remove!')">Remove A1</button></div>
            <div><button type="button" style="width:80%" onclick="removeDatagridRow('dgArtistSeq', 'Please select an artist to remove!')">Remove A2</button></div>
            <div><button type="button" style="width:80%" onclick="refreshCombo('cbArtist')">Refresh Art.</button></div>
            <div><button type="button" style="width:80%" onclick="testChk()">Test Check</button></div>
            </div>
        </div>
        <div id="rightPanel"
             data-options="region:'east',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title=" "
        >

            <table id="dgArtist" class="easyui-datagrid" style="width:400px;height:150px"
                   title="Unordered Artist Group"
                   data-options="data:artists,fitColumns:true,singleSelect:true">
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
                    data-options="data:artistSequence,singleSelect:true">
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
            if (ed != null) {
                selectedrow.splitterId = $(ed.target).combobox('getValue');
                selectedrow.splitterName = $(ed.target).combobox('getText');
            }
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

    function insertArtist(id, artist, splitterId, splitterValue){
        $('#dgArtist').datagrid('appendRow',{
             name: artist,
             id: id
         });
        var index = $('#dgArtistSeq').edatagrid('getRows').length;
         $('#dgArtistSeq').edatagrid('addRow',{
                index: index,
                row:{
                    artistId: id,
                    artistName: artist,
                    splitterId: splitterId,
                    splitterName: splitterValue
                }
         });
         //$('#dlArtistList').datagrid('clearSelections');
         $('#dgArtistSeq').datagrid('enableDnd');
         $('#dgArtistSeq').edatagrid('selectRow', index);
         //saveCombo();
    }

    function insert(){
        //var row = $('#dlArtistList').datagrid('getSelected');
        var cbArtist = '#cbArtist';
        var row = getCmbArtist(cbArtist);
        if (row != null){
            var rows = $('#dgArtistSeq').edatagrid('getRows');
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].artistId == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                insertArtist(row.id, row.value, DEFAULT_SPLITTER.id, DEFAULT_SPLITTER.value2);
                //$('#dlArtistList').datagrid('clearSelections');
                $(cbArtist).combobox('clear');
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

    function clearForm(){
        clearArtists();
        $('#dgMultiArtistList').datagrid('reload');
    }
    
    function saveMulti(rowsArtists, rowsSeq){

        var object = {artists:rowsArtists,
            id:'<?= $multiArtistId?>',
            artistSequence:rowsSeq,
            exactPosition: $('#exactPosition').is(":checked"),
            master: $('input[name=master]:checked').val()

        };
        if (object.id){
            saveObject(object, 'MP3PrettifierMultiArtistAction.php?method=saveMulti', null);
        }
        else {
            saveObject(object, 'MP3PrettifierMultiArtistAction.php?method=saveMulti', clearForm);
        }
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
