<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
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
?>
<h1>Artist / Song Relationship</h1>
<div class="horizontalLine">.</div>
<br>

<div id="cc" class="easyui-layout" style="width:100%;height:85%;">

    <div data-options="region:'west',
                       collapsible:false,
                       tools: [
                       {iconCls:'icon-add',  handler:function(){alert('add')}
                       },
                       {iconCls:'icon-remove', handler:function(){alert('remove')}
                       }]"
         title="Old Artist"
         style="width:50%;height:80%">

        <div id="old" class="easyui-layout" style="width:100%;height:100%;">
            <div data-options="region:'north',collapsible:false, border:false" style="width:100%;height:20%">
                    <div style="line-height:22px;background:#fafafa;padding:5px;">Select The Artist Type</div>
                    <div style="padding:10px">
                        <input type="radio" name="oldArtistType" value="01"><span>Artist</span><br/>
                        <input type="radio" name="oldArtistType" value="02"><span>MultiArtist</span><br/>
                        <input type="radio" name="oldArtistType" value="03"><span>Free Text</span><br/>
                    </div>
            </div>
            <div data-options="region:'center',collapsible:false, border:false" style="width:100%;height:50%">
                <div id="oldArtistType" class="easyui-layout" style="width:100%;height:100%;">
                    <div data-options="region:'west',collapsible:false, border:false" style="width:40%;height:100%">
                        <input id="cbArtist" class="easyui-combobox" name="cbArtist"
                               data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'
                             ">
                    </div>
                    <div data-options="region:'center',collapsible:false, border:false" style="width:10%;height:100%">
                        <div><button style="width:80%" onclick="insert()">Add Artist</button></div>
                        <div><button style="width:80%" onclick="clearArtists()">Clear Artist List</button></div>
                        <div><button style="width:80%" onclick="removeArtist('dgOldArtist')">Remove Artist</button></div>
                        <div><button style="width:80%" onclick="refreshCombo('cbArtist')">Refresh Artist List</button></div>
                    </div>
                    <div data-options="region:'east',collapsible:false, border:false" style="width:40%;height:100%">
                        <table id="dgOldArtist" class="easyui-datagrid" style="width:95%;height:95%"
                               title="Unordered Artist Group"
                               data-options="fitColumns:true,singleSelect:true">
                            <thead>
                            <tr>
                                <th data-options="field:'id',hidden:true">Id</th>
                                <th data-options="field:'name',width:100">Artist</th>
                            </tr>
                            </thead>
                        </table>

                    </div>
                </div>
            </div>
            <div data-options="region:'south',collapsible:false, border:false" style="width:100%;height:30%">
                MultiArtist Listbox<br>
                <input id="cbOldMultiArtist" class="easyui-combobox" style="height:30px;"
                       name="cbOldMultiArtist"
                       data-options="valueField:'id',
                             width:500,
                             textField:'description2',
                             url:'MP3PrettifierAction.php?method=getMultiArtistList'
                             ">
                <br>
                <div style="margin-bottom:20px">
                    <input id="oldFreeArtist" class="easyui-textbox" label="Old Artist (Free)" labelPosition="top" style="width:80%;height:52px">
                </div>
                <div style="margin-bottom:20px">
                    <input id="oldSong" class="easyui-textbox" label="Old Song" labelPosition="top" style="width:80%;height:52px">
                </div>
            </div>
        </div>


    </div>
    <div data-options="region:'east',collapsible:false" title="New Artist" style="width:50%;height:90%">
        <div class="easyui-layout" style="width:100%;height:100%;">
            <div data-options="region:'north',collapsible:false, border:false" style="width:100%;height:20%">
                <div style="line-height:22px;background:#fafafa;padding:5px;">Select The New Artist Type</div>
                <div style="padding:10px">
                    <input type="radio" name="lang" value="01"><span>Artist</span><br/>
                    <input type="radio" name="lang" value="02"><span>MultiArtist</span><br/>
                </div>

            </div>
            <div data-options="region:'center',collapsible:false, border:false" style="width:100%;height:20%">
                <input id="cbNewArtist" class="easyui-combobox" name="cbNewArtist"
                       data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'
                             ">
            </div>
            <div data-options="region:'south',collapsible:false, border:false" style="width:100%;height:20%">
                New MultiArtist Listbox<br>
                New Song
            </div>
        </div>
    </div>
    <div data-options="region:'south',collapsible:false" title="Buttons" style="width:100%;height:10%">
        <div><button onclick="validateAndSave()">Save</button></div>
    </div>

</div>

</body>
</html>

<script>
    function insert(){
        var cbArtist = '#cbArtist';
        var row = getCmbArtist(cbArtist);
        if (row != null){
            var rows = $('#dgOldArtist').edatagrid('getRows');
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                $('#dgOldArtist').datagrid('appendRow',{
                    name: row.value,
                    id: row.id
                });
                var index = $('#dgOldArtist').edatagrid('getRows').length;
                $('#dgOldArtist').edatagrid('addRow',{
                    index: index,
                    row:{
                        artistId: row.id,
                        artistName: row.value
                    }
                });

                //$('#dlArtistList').datagrid('clearSelections');
                $(cbArtist).combobox('clear');
                $('#dgOldArtist').datagrid('enableDnd');
                $('#dgOldArtist').edatagrid('selectRow', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
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

    function clearArtists(){
        $('#dgOldArtist').datagrid('loadData',[]);
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

    function validateAndSave(){
        var oldArtistType = ($('input[name=oldArtistType]:checked').val());
        if (oldArtistType == null){
            alert("No Old ArtistType Selected");
        }
        var object = {multiArtistConfig:($("#multiArtistConfig").val())
        };

        var oldArtists = $('#dgOldArtist').datagrid('getRows');
        var oldMultiArtist = getCmbArtist("cbOldMultiArtist");
        var object = {oldArtistType:oldArtistType,
                      oldArtists:oldArtists,
                      oldMultiArtist:oldMultiArtist == null ? "" : oldMultiArtist.id,
                      oldFreeArtist:($("#oldFreeArtist").val()),
                      oldSong:($("#oldSong").val())
        };
        alert(JSON.stringify(object));

        var tmp = $.post('ArtistSongRelationshipAction.php?method=add', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    //clearArtists();
                    alert("Multi Artist Config Item successfully saved");
                    //alert(JSON.stringify(data2, null, 4));
                    //$('#dgMultiArtistList').datagrid('reload');
                }
                else {
                    alert(JSON.stringify(data2, null, 4));
                    if (data2.errorMsg) {
                        alert(data2.errorMsg);
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

        /*if (rowsArtists == null || rowsArtists.length < 1){
            alert("At least one row must be added!");
        }
        else {
            //console.log(JSON.stringify(rowsArtists, rowsSeq, null, 4));
            //save(rowsArtists, rowsSeq);
            alert("saved");
        }*/
    }

    function getCmbArtist(cmbId) {
        cmbId = '#' + cmbId;
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
                $(cmbId).combobox('clear');
                break;
            }
        }
        if (!_b) {
            $(cmbId).combobox('setValue', '');
            row = null;
        }
        return row;
    }

    function prefOld(){
        var oldArtistType = ($('input[name=oldArtistType]:checked').val());
        switch (oldArtistType){
            case "01":
                alert("test 01");
                var val = getCmbArtist("cbOldArtist");
                if (val == null){
                    alert("Old Artist Not Selected");
                }
                break;
            case "02":
                alert("test 02");
                var val = getCmbArtist("cbOldMultiArtist");
                if (val == null){
                    alert("Old Multi Artist Not Selected");
                }
                break;
            case "03":
                break;
        }
    }

</script>

