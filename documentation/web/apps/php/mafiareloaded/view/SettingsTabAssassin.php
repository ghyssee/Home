<?php
    include_once documentPath (ROOT_PHP_MR_BO, "AssassinBO.php");
    $DATAGRID_ID = "dgAssassin";
    $DIALOG_ID = "dlgAssassin";
    $FORM_ID = "fmAssassin";
    $FORM2_ID = "fmAssassin2";
?>
<form id="<?php echo $FORM_ID;?>" method="post">
    <div id="ccAssassin" class="easyui-layout" style="width:100%;height:90px;">
        <div data-options="region:'east',border:'none'" style="width:50%;padding:5px; display: table">
            <div style="display: table-cell; vertical-align: middle">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
               onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveAssassinActiveProfile&profile=' + getProfile())" style="width:90px">Save</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsAssassin();" style="width:90px">Undo</a>
            </div>
        </div>
        <div data-options="region:'center',border:'none'" style="padding:5px">
            <div class="fitem">
                <label title="Attack the player until he's deatg, even if you loose" style="width:150px">Red Ice</label>
                <input name="redIce"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
            <div class="fitem">
                <label title="Select the Active Assassin Profile" style="width:150px">Active Assassin Profile:</label>
                    <input id="activeProfile" class="easyui-combobox" name="activeProfile"
                            data-options="width:200,
                                limitToList: true,
                                valueField: 'id',
                                textField:'name',
                                queryParams:{profile:getProfile()},
                                onLoadError:function(){
                                    alert('An error occured');
                                },
                                onChange: function(newValue, oldValue){
                                    onAssassinProfileChange(newValue, oldValue);
                                },
                                url:'SettingsAction.php?method=getAssassinProfiles'
                    ">
            </div>
        </div>
    </div>

</form>
<table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:95%;height:95%"
       title="List Of Assassin-a-nator Players"
       idField="id"
       url='SettingsAction.php?method=getListAssassin'
       data-options='fitColumns:true,
                        singleSelect:true,
                        onLoadSuccess:function(){
                        },
                        toolbar:"#toolbarAssassin",
                        pagination:true,
                        pageSize:10,
                        pageList:[5,10,15,20,25,30,40,50],
                        nowrap:false,
                        queryParams:{profile:getProfile(),assassinProfile:getAssassinProfile()},
                        rownumbers:true,
                        singleSelect:true'
>

    <thead>
    <tr>
        <th data-options="field:'id', width:10, hidden:true">Id</th>
        <th data-options="field:'fighterId', width:8">Fighter Id</th>
        <th data-options="field:'name',width:20">Name</th>
        <th data-options="field:'status',width:8">Status</th>
        <th data-options="field:'active',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Active</th>
    </tr>
    </thead>
</table>

<span style="font-size:20px">
                <div id="toolbarAssassin">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newAssassin()">New Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editAssassin()">Edit Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteAssassin()">Delete</a>
                    <button type="button" onclick="refreshAssassinDg()">Refresh</button>
                </div>
</span>

<div id="<?php echo $DIALOG_ID;?>" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
     closed="true" buttons="#dlg-buttonsAssassin">
    <form id="<?php echo $FORM2_ID;?>" method="post" novalidate>
        <div class="ftitle">Assassin</div>
        <div class="fitem">
            <label>Id</label>
            <input id="fighterId"
                   name="fighterId"
                   required="true"
                   class="easyui-textbox"
                   style="width:220px"
            >
        </div>
        <div class="fitem">
            <label>Name</label>
            <input id="name"
                   name="name"
                   required="true"
                   class="easyui-textbox"
                   style="width:220px"
            >
        </div>
        <div class="fitem">
            <label>Active</label>
            <input name="active"
                   class="easyui-checkbox" type="checkbox" value="true"
            >
        </div>
    </form>
    <div id="dlg-buttonsAssassin">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveAssassin()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgAssassin').dialog('close')" style="width:90px">Cancel</a>
    </div>
</div>

<script>
    $('#<?php echo $FORM_ID;?>').form({
        onLoadSuccess:function(data){
            //alert("loaded ok");
        },
        onLoadError:function(){
            alert("Error Loading Form <?php echo $FORM_ID;?>");
        }
    });

    function refreshAssassinDg(){
        var obj = {profile:getProfile(),assassinProfile:getAssassinProfile()};
        $('#<?php echo $DATAGRID_ID;?>').datagrid('reload', obj);
        return false;
    }

    function loadSettingsAssassin(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsAssassin&profile=' + profile);
        refreshAssassinDg();
        //$('#activeProfile').combobox('reload', 'SettingsAction.php?method=getAssassinProfiles&profile=' + profile);
    }
    loadSettingsAssassin();

    function onAssassinProfileChange(newValue, oldValue){
        //alert("NewValue: " + newValue + " / OldValue: " + oldValue);
        //if (oldValue != ''){
            refreshAssassinDg();
        //}
    }

    function getAssassinProfile(){
        var _def_value = '<?php echo ASSASSIN_DEFAULT_PROFILE?>';
        var _value = _def_value;
        try {
            //alert("1: " + _value);
            _value = $("#activeProfile").combobox('getValue');
            if (_value === "") {
                //alert("2: " + _value);
                _value = _def_value;
            }
        }
        catch (err){
        }
        return _value;
    }

    function getUrlAssassinParameters(){
        var param = "&profile=" + getProfile() + "&assassinProfile=" + getAssassinProfile();
        return param;
    }

    function newAssassin(){
        $('#dlgAssassin').dialog('open').dialog('setTitle','New Assassin');
        //$('#<?php echo $FORM_ID;?>').form('reset');
        url = 'SettingsAction.php?method=addAssassin' + getUrlAssassinParameters();
    }
    function editAssassin(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $('#dlgAssassin').dialog('open').dialog('setTitle','Edit Assassin');
            $('#<?php echo $FORM_ID;?>').form('load',row);
            url = 'SettingsAction.php?method=updateAssassin&id='+row['id'] + getUrlAssassinParameters();
        }
    }
    function saveAssassin(){
        saveRecord('<?php echo $FORM2_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $DATAGRID_ID;?>');
    }

    function deleteAssassin(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $.messager.confirm('Confirm','Are you sure you want to delete this Assassin?',function(r){
                if (r){
                    $.ajax({
                        type:    "POST",
                        url:     "SettingsAction.php?method=deleteAssassin" +  + getUrlAssassinParameters(),
                        data:    {id: row.id},
                        success: function(data) {
                            var dataObj = JSON.parse(data);
                            if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')){
                                alert(dataObj.errorMessage);
                            }
                            $('#<?php echo $DATAGRID_ID;?>').datagrid('reload');
                        },
                        // vvv---- This is the new bit
                        error:   function(jqXHR, textStatus, errorThrown) {
                            alert("Error, status = " + textStatus + ", " +
                                "error thrown: " + errorThrown
                            );
                        }
                    });
                }
            });
        }
    }

</script>