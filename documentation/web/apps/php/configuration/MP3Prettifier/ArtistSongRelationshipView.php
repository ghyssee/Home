<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistSongRelationshipBO.php");
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
    $artistSongId = htmlspecialchars($_REQUEST['id']);
    $artistSongRelationShipBO = new ArtistSongRelationShipBO();
    $artistSongRelationshipTO = $artistSongRelationShipBO->findArtistSongRelationship($artistSongId);
    $artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO($artistSongRelationshipTO);
}
else {
    $artistSongId = null;
    $artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO(null);
}
?>
<script>
    //var oldData = [{"id":"1", "name":"test"}];
    var oldData = <?= json_encode($artistSongRelationshipObj->oldArtistListObj) ?>;
</script>
<h1>Artist / Song Relationship</h1>
<div class="horizontalLine">.</div>
<br>

    <form id="artistSongForm">

    <div id="cc" class="easyui-layout" style="width:100%;height:85%;">

        <div id="oldArtistLayOut" data-options="region:'west',
                           collapsible:false,
                           tools: [
                           {iconCls:'icon-add',  handler:function(){alert('add')}
                           },
                           {iconCls:'icon-remove', handler:function(){alert('remove')}
                           }]"
             title="Old Artist"
             style="width:50%;height:80%">

            <div class="easyui-layout" style="width:100%;height:100%;">

                <div id="oldArtistTypeLayOut" data-options="region:'north',collapsible:false, border:false" style="padding:0px;width:100%;height:22%">
                    <div style="line-height:22px;background:#fafafa;padding:3px;">Select The Artist Type</div>
                    <div style="padding:10px">
                        <input type="radio" name="oldArtistType" value="<?= ArtistType::ARTIST?>" <?= radioButton(ArtistType::ARTIST, $artistSongRelationshipObj->oldArtistType); ?>><span>Artist</span><br/>
                        <input type="radio" name="oldArtistType" value="<?= ArtistType::MULTIARTIST?>" <?= radioButton(ArtistType::MULTIARTIST, $artistSongRelationshipObj->oldArtistType); ?>><span>MultiArtist</span><br/>
                    </div>
                </div>

                <div id="oldArtistListDataGridLayout" data-options="region:'west',collapsible:false, border:false" style="padding:0px;width:40%;height:100%">
                    <?php
                    include_once('Smarty.class.php');
                    $url = webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'MP3PrettifierAction.php');
                    $fieldId = "id";
                    $smarty = initializeSmarty();
                    $smarty->assign('title','Artists');
                    $smarty->assign('item','Artist');
                    $smarty->assign('tableWidth','100%');
                    $smarty->assign('tableHeight','100%');
                    $smarty->assign('tablegrid',"OldArtist");
                    $smarty->assign('id',$fieldId);
                    $smarty->assign('fitColumns',"true");
                    $smarty->assign('pageSize',"5");
                    $smarty->assign('customSave', 'customSaveArtistCRUD()');
                    $smarty->assign('viewUrl',$url . "?method=getListArtists");
                    $smarty->assign('updateUrl',"'" . $url . "?method=updateArtist&id='+row['" . $fieldId . "']");
                    $smarty->assign('newUrl',"'" . $url . "?method=addArtist'");
                    $smarty->assign('deleteUrl', $url . "?method=deleteArtist");


                    $smarty->assign("contacts", array(
                            array("field" => "id", "label"=>"Id", "size" => 30, "hidden" => true),
                            array("field" => "name", "label"=>"Name", "size" => 40, "required" => true, "sortable" => true)
                        )
                    );
                    //** un-comment the following line to show the debug console
                    //$smarty->debugging = true;

                    $smarty->display('TableGridV4.tpl');
                    ?>
                </div>
                <div id="oldArtistRightSideLayout" data-options="region:'east',collapsible:false, border:false" style="padding:0px;width:60%;height:100%">
                    <div class="easyui-layout" style="width:95%;height:95%;">
                        <div id="oldArtistListButtonsLayout" data-options="region:'west',collapsible:false, border:false" style="padding:0px;width:50%;height:30%">
                            <div><button  type="button" type="button" style="width:100%" onclick="addArtist()">Add Artist</button></div>
                            <div><button  type="button" style="width:100%" onclick="clear Song Info()">Clear Song</button></div>
                            <div><button  type="button" style="width:100%" onclick="removeArtist('dgListOldArtist')">Remove Artist</button></div>
                            <div><button  type="button" style="width:100%" onclick="copyArtist()">Copy Artist</button></div>
                        </div>
                        <div id="oldArtistListSelectedDatagridLayout" data-options="region:'east',collapsible:false, border:false" style="width:50%;height:42%">
                            <table id="dgListOldArtist" class="easyui-datagrid" style="width:95%;height:95%"
                                   title="Unordered Artist Group"
                                   data-options="fitColumns:true,
                                                 data:oldData,
                                                 singleSelect:true">
                                <thead>
                                <tr>
                                    <th data-options="field:'id',hidden:true">Id</th>
                                    <th data-options="field:'name',width:100">Artist</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                        <div id="oldArtistInputFieldsLayout" data-options="region:'south',collapsible:false, border:false" style="padding:5px;width:100%;height:58%">
                            MultiArtist<br>
                            <input id="cbOldMultiArtist" class="easyui-combobox" style="height:30px;"
                                   name="cbOldMultiArtist"
                                   value="<?= $artistSongRelationshipObj->oldMultiArtistId ?>"
                                   data-options="valueField:'id',
                                 width:400,
                                 textField:'description2',
                                 url:'MP3PrettifierAction.php?method=getMultiArtistList'
                                 ">
                            <br>
                            <div style="margin-bottom:5px">
                                <input id="oldFreeArtist" class="easyui-textbox" label="Old Artist (Free)" value="<?= $artistSongRelationshipObj->oldArtist ?>" labelPosition="top" style="width:80%;height:52px">
                                <button type="button" onclick="addFreeArtist();">Add</button>
                            </div>
                            <div style="margin-bottom:20px">
                                <input id="oldSong" class="easyui-textbox" value="<?= $artistSongRelationshipObj->oldSong ?>"
                                       label="Old Song" labelPosition="top" style="width:80%;height:52px">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="newArtistLayout" data-options="region:'east',collapsible:false" title="New Artist" style="width:50%;height:90%">
            <div class="easyui-layout" style="width:100%;height:100%;">
                <div data-options="region:'north',collapsible:false, border:false" style="padding:5px;width:100%;height:20%">
                    <div style="line-height:22px;background:#fafafa;padding:5px;">Select The New Artist Type</div>
                    <div style="padding:10px">
                        <input type="radio" name="newArtistType" value="<?= ArtistType::ARTIST?>" <?= radioButton(ArtistType::ARTIST, $artistSongRelationshipObj->newArtistType); ?>><span>Artist</span><br/>
                        <input type="radio" name="newArtistType" value="<?= ArtistType::MULTIARTIST?>" <?= radioButton(ArtistType::MULTIARTIST, $artistSongRelationshipObj->newArtistType); ?>><span>MultiArtist</span><br/>
                    </div>

                </div>
                <div data-options="region:'center',collapsible:false, border:false" style="padding:5px;width:100%;height:10%">
                    New Artist<br>
                    <input id="cbNewArtist" class="easyui-combobox" name="cbNewArtist"
                           value="<?= $artistSongRelationshipObj->newArtistId ?>"
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
                           value="<?= $artistSongRelationshipObj->newMultiArtistId ?>"
                           data-options="valueField:'id',
                                 width:500,
                                 textField:'description2',
                                 url:'MP3PrettifierAction.php?method=getMultiArtistList'
                                 ">
                    <div style="margin-bottom:20px">
                        <input id="newSong" class="easyui-textbox"
                               value="<?= $artistSongRelationshipObj->newSong ?>"
                               label="New Song" labelPosition="top" style="width:80%;height:52px">
                    </div>
                    <div style="margin-bottom:20px">
                        <input id="priority"
                               value="<?= $artistSongRelationshipObj->priority ?>"
                               class="easyui-numberspinner" style="width:80px;"
                               data-options="required:false,min:0,editable:true">
                    </div>
                    <div style="margin-bottom:20px">
                        <input id="indexTitle"
                               value="<?= $artistSongRelationshipObj->indexTitle ?>"
                               class="easyui-numberspinner" style="width:80px;"
                               data-options="required:false,min:0,editable:true">
                    </div>
                </div>
            </div>
        </div>
        <div id="buttonLayOut" data-options="region:'south',collapsible:false" title="Buttons" style="width:100%;height:10%">
            <div><button type="button" onclick="validateAndSave()">Save</button>
                <button type="button" onclick="clearForm()">Clear</button>
                <button  type="button" onclick="refreshCombo('cbOldArtist')">Refresh Artist List</button></div>
            </div>
        </div>
    </div>

</form>
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
    $(function(){
        var dg = $('#dgOldArtist').datagrid();
        dg.datagrid('enableFilter');
    });

    //$('#cbOldArtist').combobox('setValue', 'Luciana');

    function clearForm(){
        $('#artistSongForm')[0].reset();
        $('#dgListOldArtist').datagrid('loadData',[]);
    }

    function copyArtist(){
        var rows = $('#dgListOldArtist').edatagrid('getRows');
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

    function addFreeArtist(){
        var oldFreeArtist =($("#oldFreeArtist").val());
        if (oldFreeArtist != '') {
            $('#dgListOldArtist').datagrid('appendRow', {
                name: oldFreeArtist,
                id: null
            });
            $("#oldFreeArtist").textbox('setValue', '');
        }
        else {
            alert("No Free Artist Filled In");
        }
    }

    function insert(){
        var cbOldArtist = 'cbOldArtist';
        var row = getCmbArtist(cbOldArtist);
        if (row != null){
            var rows = $('#dgListOldArtist').edatagrid('getRows');
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {

                $('#dgListOldArtist').datagrid('appendRow',{
                    name: row.value,
                    id: row.id
                });
                /*
                var index = $('#dgOldArtist').edatagrid('getRows').length;
                $('#dgOldArtist').edatagrid('addRow',{
                    index: index,
                    row:{
                        artistId: row.id,
                        artistName: row.value
                    }
                });*/

                //$('#dlArtistList').datagrid('clearSelections');
                $(cbOldArtist).combobox('clear');
                $('#dgListOldArtist').datagrid('enableDnd');
                $('#dgListOldArtist').edatagrid('selectRow', index);
            }
            else {
                alert("already added");
            }
        } else {
            alert("Please select an artist");
        }
        return false;
    }

    function clearSongInfo(){
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
        var oldArtists = $('#dgListOldArtist').datagrid('getRows');
        var oldMultiArtist = getCmbArtist("cbOldMultiArtist");

        var newArtistType = ($('input[name=newArtistType]:checked').val());
        var newArtist = getCmbArtist("cbNewArtist");
        var newMultiArtist = getCmbArtist("cbNewMultiArtist");
        var newSong =$("#newSong").textbox('getValue');
        var priority = $("#priority").numberspinner('getValue');
        var indexTitle = $("#indexTitle").numberspinner('getValue');

        var object = {
                      id:'<?= $artistSongId?>',
                      oldArtistType:oldArtistType,
                      oldArtists:oldArtists,
                      oldMultiArtist:oldMultiArtist == null ? "" : oldMultiArtist.id,
                      oldFreeArtist:($("#oldFreeArtist").val()),
                      oldSong:($("#oldSong").val()),
                      newArtistType:newArtistType,
                      newArtist:newArtist,
                      newMultiArtist:newMultiArtist == null ? "" : newMultiArtist.id,
                      newSong:newSong,
                      priority:priority,
                      indexTitle:indexTitle
        };

        var tmp = $.post('ArtistSongRelationshipAction.php?method=add', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    clearSongInfo();
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

    function addArtist(){
        var row = $('#dgOldArtist').datagrid('getSelected');
        if (row) {

            var rows = $('#dgListOldArtist').edatagrid('getRows');
            var rowIndex = $('#dgListOldArtist').edatagrid("getRowIndex", row);
            var alreadyAdded = false;
            for(var i=0; i<rows.length; i++){
                if (rows[i].id == row.id) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {

                $('#dgListOldArtist').datagrid('appendRow',{
                    name: row.name,
                    id: row.id
                });
                $('#dgListOldArtist').datagrid('enableDnd');
                $('#dgOldArtist').datagrid('unselectRow', rowIndex);
                clearFilter('dgOldArtist');
            }
            else {
                $.messager.alert('Warning','Artist already added');
            }
        }
        else {
            $.messager.alert('Warning','No Artist Selected');
        }
    }

</script>

