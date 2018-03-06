<?php
$DATAGRID_ID = "dgAllies";
$FORM_ID = "fmAllies";
$DIALOG_ID = "dlgAlly";
?>
<table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:95%;height:100%"
       title="List Of Allies"
       idField="id"
       url='SettingsAction.php?method=getListAllies'
       data-options='fitColumns:true,
                        singleSelect:true,
                        onLoadSuccess:function(){
                        },
                        onLoadError:function(){
                            alert("Allies Loaded With Errors");
                        },
                        toolbar:"#toolbarAlly",
                        pagination:true,
                        pageSize:10,
                        pageList:[5,10,15,20,25,30,40,50],
                        nowrap:false,
                        queryParams:{profile:getProfile()},
                        rownumbers:true,
                        singleSelect:true'
>

    <thead>
    <tr>
        <th data-options="field:'id', width:10">Id</th>
        <th data-options="field:'name',width:20">Name</th>
        <th data-options="field:'active',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Active</th>
    </tr>
    </thead>
</table>

<span style="font-size:20px">
                <div id="toolbarAlly">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newAlly()">New Ally</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editAlly()">Edit Ally</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteAlly()">Delete</a>
                    <button type="button" onclick="refreshAllies()">Refresh</button>
                </div>
</span>

<div id="<?php echo $DIALOG_ID;?>" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
     closed="true" buttons="#dlg-buttonsAlly">
    <form id="<?php echo $FORM_ID;?>" method="post" novalidate>
        <div class="ftitle">Ally</div>
        <div class="fitem">
            <label>Id</label>
            <input id="id"
                   name="id"
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
    <div id="dlg-buttonsAlly">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="saveAlly()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#<?php echo DIALOG_ID;?>').dialog('close')" style="width:90px">Cancel</a>
    </div>
</div>

<script>
    function newAlly(){
        $('#<?php echo DIALOG_ID;?>').dialog('open').dialog('setTitle','New Ally');
        $('#<?php echo $FORM_ID;?>').form('reset');
        url = 'SettingsAction.php?method=addAlly' + "&profile=" + getProfile();
    }
    function editAlly(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $('#<?php echo DIALOG_ID;?>').dialog('open').dialog('setTitle','Edit Ally');
            $('#<?php echo $FORM_ID;?>').form('load',row);
            url = 'SettingsAction.php?method=updateAlly&id='+row['id'] + "&profile=" + getProfile();
        }
    }
    function saveAlly(){
        $('#<?php echo $FORM_ID;?>').form('submit',{
            url: url,
            onSubmit: function(){
                return $(this).form('validate');
            },
            success: function(result){
                var result = JSON.parse(result);
                if (result.errorMsg){
                    $.messager.show({
                        title: 'Error',
                        msg: result.errorMsg
                    });
                } else {
                    $('#<?php echo DIALOG_ID;?>').dialog('close');		// close the dialog
                    $('#<?php echo $DATAGRID_ID;?>').datagrid('reload');	// reload the user data
                }
            }
        });
    }

    function deleteAlly(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $.messager.confirm('Confirm','Are you sure you want to delete this Ally?',function(r){
                if (r){
                    $.ajax({
                        type:    "POST",
                        url:     "SettingsAction.php?method=deleteAlly" + "&profile=" + getProfile(),
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

    function refreshAllies(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        var obj = {profile:profile};
        $('#<?php echo $DATAGRID_ID;?>').datagrid('reload', obj);
        return false;
    }
</script>