<table id="dgAssassin" class="easyui-datagrid" style="width:95%;height:100%"
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
                <th data-options="field:'id', width:10">Id</th>
                <th data-options="field:'name',width:20">Name</th>
                <th data-options="field:'active',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Active</th>
            </tr>
            </thead>
</table>

<span style="font-size:20px">
                <div id="toolbarAssassin">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteLine()">Delete</a>
                    <button type="button" onclick="refreshDatagrid()">Refresh</button>
                </div>
</span>

<div id="dlgAssassin" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
     closed="true" buttons="#dlg-buttonsAssassin">
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
</div>
