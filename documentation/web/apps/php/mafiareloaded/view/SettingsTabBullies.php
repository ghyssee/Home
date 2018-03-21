<?php
$DATAGRID_ID = "dbBullies";
$FORM_ID = "fmBullies";
$DIALOG_ID = "dlgBullies";
$TOOLBAR_ID = "toolbarBullies";
$DIALOG_BUTTONS_ID = "dlgButtonsBully";
?>
<table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:95%;height:100%"
       title="List Of Allies"
       idField="id"
       url='SettingsAction.php?method=getListBullies'
       data-options='fitColumns:true,
                        singleSelect:true,
                        onLoadSuccess:function(){
                        },
                        onLoadError:function(){
                            alert("Allies Loaded With Errors");
                        },
                        toolbar:"#<?php echo $TOOLBAR_ID;?>",
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
                <div id="<?php echo $TOOLBAR_ID;?>">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newBully()">New</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editBully()">Edit</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteBully()">Delete</a>
                    <button type="button" onclick="refreshBullies()">Refresh</button>
                </div>
</span>

<div id="<?php echo $DIALOG_ID;?>" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
     closed="true" buttons="#<?php echo $DIALOG_BUTTONS_ID;?>">
    <form id="<?php echo $FORM_ID;?>" method="post" novalidate>
        <div class="ftitle">Bully</div>
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
    <div id="<?php echo $DIALOG_BUTTONS_ID;?>">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="saveBully()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#<?php echo $DIALOG_ID;?>').dialog('close')" style="width:90px">Cancel</a>
    </div>
</div>

<script>
    function newBully(){
        newRecord("<?php echo $DIALOG_ID;?>", "<?php echo $FORM_ID;?>", "New Bully", 'SettingsAction.php?method=addBully' + "&profile=" + getProfile());
    }
    function editBully(){
        editRecord('<?php echo $DATAGRID_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'id', 'Edit Bully',
            'SettingsAction.php?method=updateBully&profile=' + getProfile());
    }
    function saveBully(){
        saveRecord('<?php echo $FORM_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $DATAGRID_ID;?>');
    }

    function deleteBully(){
        deleteRecordV2('<?php echo $DATAGRID_ID;?>','Bully', 'id', "SettingsAction.php?method=deleteBully" + "&profile=" + getProfile());
    }

    function refreshBullies(profile){
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