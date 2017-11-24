<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 24/11/2017
 * Time: 13:47
 */
session_start();
include_once("../../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "JobBO.php");
?>

<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
        <link rel="stylesheet" type="text/css+
" href="../../css/form.css">
        <?php include documentRoot ("apps/php/templates/easyui.php");?>
    </head>
    <body>

    <?php
    $activeJobBO = new ActiveJobBO();
    $list = $activeJobBO->getScheduledJobs();
    foreach($list as $key => $value) {
        echo $value->description . "<br>";
    }

    ?>
    <table id="dgGlobalWord" class="easyui-datagrid" style="width:90%;height:300px"
           title="Result"
           idField="jobId"
            url='../action/JobManagerAction.php?method=list'
           data-options='fitColumns:true,
                         singleSelect:true,
       toolbar:"#toolbarGlobalWord",
       pagination:true,
       nowrap:false,
       remoteFilter:true,
       rownumbers:true,
       pagePosition:"bottom",
       pageSize:10,
       pageList:[5,10,15,20,25,30,40,50],
       singleSelect:true'>

        <thead>
        <tr>
            <th data-options="field:'districtId',width:15">DistrictId</th>
            <th data-options="field:'jobId',width:15">jobId</th>
            <th data-options="field:'enabled',width:15,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Enabled</th>        </tr>
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
        <div class="ftitle">Job Schedule</div>
        <form id="fmGlobalWord" method="post" novalidate>

            <div class="fitem">
                <label>DistrictId</label>
                <input name="distrctId"
                       required="true"                                                           class="easyui-textbox"

                >
            </div>
            <div class="fitem">
                <label>JobId</label>
                <input name="jobId"
                       class="easyui-textbox"

                >
            </div>
            <div class="fitem">
                <label>Enabled</label>
                <input name="enabled"
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
            $('#dlgGlobalWord').dialog('open').dialog('setTitle','New Global Word');
            $('#fmGlobalWord').form('reset');
            url = 'MP3PrettifierAction.php?method=add&type=global&category=words';
        }
        function editRecordGlobalWord(){
            var row = $('#dgGlobalWord').datagrid('getSelected');
            if (row){
                $('#dlgGlobalWord').dialog('open').dialog('setTitle','Edit Global Word');
                $('#fmGlobalWord').form('load',row);
                url = 'MP3PrettifierAction.php?method=update&id='+row['jobId']+'&type=global&category=words';
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

    <style type="text/css">
        #fm{
            margin:0;
            padding:10px 30px;
        }
        .ftitle{
            font-size:14px;
            font-weight:bold;
            padding:5px 0;
            margin-bottom:10px;
            border-bottom:1px solid #ccc;
        }
        .fitem{
            margin-bottom:5px;
        }
        .fitem label{
            display:inline-block;
            width:80px;
        }
        .fitem input{
            width:160px;
        }
    </style>
    <script type="text/javascript">
        $(function(){
            var dg = $('#dgGlobalWord');
            dg.datagrid('enableFilter');
        });
    </script>

    </body>
</html>

