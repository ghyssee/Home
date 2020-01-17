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
        <th data-options="field:'whiteTag',width:20">White Tag</th>
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
            <label>White Tag</label>
            <input id="whiteTag"
                   name="whiteTag"
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
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#<?php echo $DIALOG_ID;?>').dialog('close')" style="width:90px">Cancel</a>
    </div>
</div>

<script>
    function newAlly(){
        newRecord("<?php echo $DIALOG_ID;?>", "<?php echo $FORM_ID;?>", "New Ally", 'SettingsAction.php?method=addAlly' + "&profile=" + getProfile());
    }
    function editAlly(){
        editRecord('<?php echo $DATAGRID_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'id', 'Edit Ally',
            'SettingsAction.php?method=updateAlly&id=\'+row[\'id\'] + "&profile=' + getProfile());
    }
    function saveAlly(){
        saveRecord('<?php echo $FORM_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $DATAGRID_ID;?>');
    }

    function deleteAlly(){
        deleteRecordV2('<?php echo $DATAGRID_ID;?>','Ally', 'id', "SettingsAction.php?method=deleteAlly" + "&profile=" + getProfile());
    }

    function refreshAllies(profile){
        refreshDataGridProfile('<?php echo $DATAGRID_ID;?>', profile);
        return false;
    }
</script>