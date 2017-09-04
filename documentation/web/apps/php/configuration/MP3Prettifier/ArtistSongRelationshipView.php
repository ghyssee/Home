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

    <input type="hidden" name="elems[]" />
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
            <div data-options="region:'north',collapsible:false, border:false" style="padding:5px;width:100%;height:20%">
                    <div style="line-height:22px;background:#fafafa;padding:5px;">Select The Artist Type</div>
                    <div style="padding:10px">
                        <input type="radio" name="oldArtistType" value="01"><span>Artist</span><br/>
                        <input type="radio" name="oldArtistType" value="02"><span>MultiArtist</span><br/>
                        <input type="radio" name="oldArtistType" value="03"><span>Free Text</span><br/>
                    </div>
            </div>
            <div data-options="region:'center',collapsible:false, border:false" style="padding:5px;width:100%;height:50%">
                <div id="oldArtistType" class="easyui-layout" style="width:100%;height:100%;">
                    <div data-options="region:'west',collapsible:false, border:false" style="width:40%;height:100%">
                        <input id="cbOldArtist" class="easyui-combobox" name="cbOldArtist"
                               data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'
                             ">
                    </div>
                    <div data-options="region:'center',collapsible:false, border:false" style="width:10%;height:100%">
                        <div><button type="button" style="width:80%" onclick="insert()">Add Artist</button></div>
                        <div><button style="width:80%" onclick="clearArtists()">Clear Artist List</button></div>
                        <div><button style="width:80%" onclick="removeArtist('dgOldArtist')">Remove Artist</button></div>
                        <div><button style="width:80%" onclick="refreshCombo('cbOldArtist')">Refresh Artist List</button></div>
                        <div><button style="width:80%" onclick="copyArtist()">Copy Artist</button></div>
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
            <div data-options="region:'south',collapsible:false, border:false" style="padding:5px;width:100%;height:30%">
                MultiArtist<br>
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
            <div data-options="region:'north',collapsible:false, border:false" style="padding:5px;width:100%;height:20%">
                <div style="line-height:22px;background:#fafafa;padding:5px;">Select The New Artist Type</div>
                <div style="padding:10px">
                    <input type="radio" name="newArtistType" value="01"><span>Artist</span><br/>
                    <input type="radio" name="newArtistType" value="02"><span>MultiArtist</span><br/>
                </div>

            </div>
            <div data-options="region:'center',collapsible:false, border:false" style="padding:5px;width:100%;height:10%">
                New Artist<br>
                <input id="cbNewArtist" class="easyui-combobox" name="cbNewArtist"
                       data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'
                             ">
            </div>
            <div data-options="region:'south',collapsible:false, border:false" style="padding:5px;width:100%;height:70%">
                New MultiArtist<br>
                <input id="cbNewMultiArtist" class="easyui-combobox" style="height:30px;"
                       name="cbNewMultiArtist"
                       data-options="valueField:'id',
                             width:500,
                             textField:'description2',
                             url:'MP3PrettifierAction.php?method=getMultiArtistList'
                             ">
                <br>
                <div style="margin-bottom:20px">
                    <input id="newSong" class="easyui-textbox" label="New Song" labelPosition="top" style="width:80%;height:52px">
                </div>
                <div style="margin-bottom:20px">
                    <input id="priority" class="easyui-numberspinner" style="width:80px;"
                        required="required" data-options="min:0,editable:true,value:0">
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
    $(function(){
        $('#ff').form({
            onSubmit: function() {
            },
            success:function(data){
                $.messager.alert('Info', data, 'info');
            }
        });
    });

    function copyArtist(){
        var rows = $('#dgOldArtist').edatagrid('getRows');
        if (rows.length > 0){
            $('#cbNewArtist').combobox('setValue', rows[0].id);
        }
        var oldMultiArtist = getCmbArtist("cbOldMultiArtist");
        if (oldMultiArtist){
            $('#cbNewMultiArtist').combobox('setValue', oldMultiArtist.id);
        }
        var oldSong =$("#oldSong").textbox('getValue');
        if (oldSong){
            newSong =$("#newSong").textbox('setValue', oldSong);
        }
    }

    function insert(){
        var cbOldArtist = 'cbOldArtist';
        var row = getCmbArtist(cbOldArtist);
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
                $(cbOldArtist).combobox('clear');
                $('#dgOldArtist').datagrid('enableDnd');
                $('#dgOldArtist').edatagrid('selectRow', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
        return false;
    }

    function clearArtists(){
        //$('#dgOldArtist').datagrid('loadData',[]);
        $("#oldSong").textbox('setValue', '');
        $("#newSong").textbox('setValue', '');
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
        var oldFreeArtist =($("#oldFreeArtist").val());
        var oldSong =$("#oldSong").textbox('getValue');
        var oldArtists = $('#dgOldArtist').datagrid('getRows');
        var oldMultiArtist = getCmbArtist("cbOldMultiArtist");

        var newArtistType = ($('input[name=newArtistType]:checked').val());
        var newArtist = getCmbArtist("cbNewArtist");
        var newMultiArtist = getCmbArtist("cbNewMultiArtist");
        var newSong =$("#newSong").textbox('getValue');
        var priority = $("#priority").numberspinner('getValue');

        var object = {oldArtistType:oldArtistType,
                      oldArtists:oldArtists,
                      oldMultiArtist:oldMultiArtist == null ? "" : oldMultiArtist.id,
                      oldFreeArtist:($("#oldFreeArtist").val()),
                      oldSong:($("#oldSong").val()),
                      newArtistType:newArtistType,
                      newArtist:newArtist,
                      newMultiArtist:newMultiArtist == null ? "" : newMultiArtist.id,
                      newSong:newSong,
                      priority:priority
        };

        var tmp = $.post('ArtistSongRelationshipAction.php?method=add', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    clearArtists();
                    alert("Artist Song Relationship successfully saved");
                    //alert(JSON.stringify(data2, null, 4));
                    //$('#dgMultiArtistList').datagrid('reload');
                }
                else {
                    if (data2.errorMsg) {
                        alert(data2.errorMsg);
                    }
                    else {
                        //alert(JSON.stringify(data2, null, 4));
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
                //$(cmbId).combobox('clear');
                break;
            }
        }
        if (!_b) {
            $(cmbId).combobox('setValue', '');
            row = null;
        }
        return row;
    }

</script>

