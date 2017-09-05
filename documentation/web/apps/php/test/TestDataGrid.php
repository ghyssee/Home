<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
session_start();
?>


<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body style="background">

<table id="dgGlobalWord" title="Global Words" class="easyui-datagrid" style="width:100%;height:100%" idField="id"
       url="../data/datagrid_data2.json"
       data-options='fitColumns:true,
		                 onLoadSuccess:function(data){
		                 		                 		                 },
        rowStyler: function(index,row){
            if ((index % 2) == 3){
                return "background-color:grey;font-weight:bold;";
            }
       },
       toolbar:"#toolbarGlobalWord",
       pagination:true,
       nowrap:false,
       remoteFilter:true,
       rownumbers:true,
       pagePosition:"bottom",
       pageSize:10,
       singleSelect:true'>
    <thead>
    <tr>
        <th data-options="field:'oldWord',width:70,sortable:true">Old Word</th>
        <th data-options="field:'newWord',width:70,sortable:true">New Word</th>
        <th data-options="field:'priority',width:70,sortable:true">Priority</th>
        <th data-options="field:'exactMatch',width:15,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Exact Match</th>
        <th data-options="field:'id',hidden:true,width:50">Id</th>
    </tr>
    </thead>
</table>

<span style="font-size:20px">
	<div id="toolbarGlobalWord">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecordGlobalWord()">New Global Word</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecordGlobalWord()">Edit Global Word</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecordGlobalWord()">Remove Global Word</a>
    </div>
	</span>

<div id="dlgGlobalWord" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
     closed="true" buttons="#dlg-buttonsGlobalWord">
    <div class="ftitle">Global Word Information</div>
    <form id="fmGlobalWord" method="post" novalidate>
        <div class="fitem">
            <label>Old Word</label>
            <input name="oldWord"
                   required="true"                                                           class="easyui-textbox"

            >
        </div>
        <div class="fitem">
            <label>New Word</label>
            <input name="newWord"
                   class="easyui-textbox"
                   data-options="value:'Test'"

            >
        </div>
        <div class="fitem">
            <label>Priority</label>
            <input name="priority"
                   class="easyui-numberspinner"
                   data-options="value:100">

        </div>
        <div class="fitem">
            <label>Exact Match</label>
            <input name="exactMatch"
                   class="easyui-checkbox" type="checkbox" value="true"
            >
        </div>

    </form>
</div>
<div id="dlg-buttonsGlobalWord">
    <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
       onclick="saveGlobalWord()" style="width:90px">Save</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgGlobalWord').dialog('close')" style="width:90px">Cancel</a>
</div>


<script type="text/javascript">
    var url;
    function newRecordGlobalWord(){
        $('#fmGlobalWord').form('reset');
        $('#dlgGlobalWord').dialog('open').dialog('setTitle','New Global Word');
        url = 'MP3PrettifierAction.php?method=add&type=global&category=words';
    }
    function editRecordGlobalWord(){
        var row = $('#dgGlobalWord').datagrid('getSelected');
        if (row){
            $('#dlgGlobalWord').dialog('open').dialog('setTitle','Edit Global Word');
            $('#fmGlobalWord').form('load',row);
            url = 'MP3PrettifierAction.php?method=update&id='+row['id']+'&type=global&category=words';
        }
    }
    function saveGlobalWord(){
        $('#fmGlobalWord').form('submit',{
            url: url,
            onSubmit: function(){
                return $(this).form('validate');
            },
            success: function(result){
                //var result = eval('('+result+')');
                var result = JSON.parse(result);
                if (result.errorMsg){
                    $.messager.show({
                        title: 'Error',
                        msg: result.errorMsg
                    });
                } else {
                    $('#dlgGlobalWord').dialog('close');		// close the dialog
                    $('#dgGlobalWord').datagrid('reload');	// reload the user data
                }
            }
        });
    }

    function deleteRecordGlobalWord(){
        var row = $('#dgGlobalWord').datagrid('getSelected');
        if (row){
            $.messager.confirm('Confirm','Are you sure you want to delete this Global Word?',function(r){
                if (r){
                    $.ajax({
                        type:    "POST",
                        url:     "MP3PrettifierAction.php?method=delete&type=global&category=words",
                        data:    {id: row.id},
                        success: function(data) {
                            var dataObj = JSON.parse(data);
                            if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')){
                                alert(dataObj.errorMessage);
                            }
                            $('#dgGlobalWord').datagrid('reload');
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
    function checkboxFormatter(val,row,index){

        if (val== 1) {
            return "√";
        }
        else {
            return "";
        }
    }

</script>











</body>
</html>












</body>
</html>

