<?php
    $DATAGRID_ID = "dgAssassin";
    $FORM_ID = "fmAssassin";
?>
<table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:95%;height:100%"
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
                        queryParams:{profile:getProfile()},
                        rownumbers:true,
                        singleSelect:true'
                   >

            <thead>
            <tr>
                <th data-options="field:'id', width:10, hidden:true">Id</th>
                <th data-options="field:'fighterId', width:10">Fighter Id</th>
                <th data-options="field:'name',width:20">Name</th>
                <th data-options="field:'active',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Active</th>
            </tr>
            </thead>
</table>

<span style="font-size:20px">
                <div id="toolbarAssassin">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newAssassin()">New Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editAssassin()">Edit Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteAssassin()">Delete</a>
                    <button type="button" onclick="refreshDatagrid()">Refresh</button>
                </div>
</span>

<div id="dlgAssassin" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
     closed="true" buttons="#dlg-buttonsAssassin">
    <form id="<?php echo $FORM_ID;?>" method="post" novalidate>
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
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="saveAssassin()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgAssassin').dialog('close')" style="width:90px">Cancel</a>
    </div>
</div>

<script>
    function newAssassin(){
        $('#dlgAssassin').dialog('open').dialog('setTitle','New Assassin');
        $('#<?php echo $FORM_ID;?>').form('reset');
        url = 'SettingsAction.php?method=addAssassin' + "&profile=" + getProfile();
    }
    function editAssassin(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $('#dlgAssassin').dialog('open').dialog('setTitle','Edit Assassin');
            $('#<?php echo $FORM_ID;?>').form('load',row);
            url = 'SettingsAction.php?method=updateAssassin&id='+row['id'] + "&profile=" + getProfile();
        }
    }
    function saveAssassin(){
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
                    $('#dlgAssassin').dialog('close');		// close the dialog
                    $('#<?php echo $DATAGRID_ID;?>').datagrid('reload');	// reload the user data
                }
            }
        });
    }

    function deleteAssassin(){
        var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
        if (row){
            $.messager.confirm('Confirm','Are you sure you want to delete this Assassin?',function(r){
                if (r){
                    $.ajax({
                        type:    "POST",
                        url:     "SettingsAction.php?method=deleteAssassin" + "&profile=" + getProfile(),
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