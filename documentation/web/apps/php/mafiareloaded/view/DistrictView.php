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
    ?>


    <style type="text/css">
        .Table
        {
            display: table;
        }
        .Title
        {
            display: table-caption;
            text-align: center;
            font-weight: bold;
            font-size: larger;
        }
        .Heading
        {
            display: table-row;
            font-weight: bold;
            text-align: center;
        }
        .Row
        {
            display: table-row;
        }
        .Cell
        {
            display: table-cell;
            border: none;
            border-width: thin;
            padding-top: 5px;
            padding-left: 5px;
            padding-right: 5px;
            padding-bottom: 5px;
        }
        DIV.vertical-center {
            min-height: 10em;
            display: table-cell;
            vertical-align: middle
        }
    </style>

    <div id="cc" class="easyui-layout" style="width:95%;height:90%">
        <div data-options="region:'north',title:'Actions',split:true" style="height:20%;">
            <div class="Table">
                <div class="Row">
                    <div class="Cell vertical-center">
                        <div style="padding:5px"><label>Profile</label></div>
                        <div>
                            <input id="profile" class="easyui-combobox" name="profile"
                                   data-options="valueField:'id',
                         width:200,
                         limitToList: true,
                         textField:'name',
                         onChange: function(row){
                            onProfileChange(row);
                          },
                          url:'ProfileAction.php?method=getProfiles'
                         ">
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div data-options="region:'south',title:'Jobs',split:true" style="height:80%;padding:5px;">

            <table id="dgScheduledJob" class="easyui-datagrid" style="width:100%;height:95%"
                   title="List"
                   idField="uniqueId"
                   url='DistrictAction.php?method=list'
                   data-options='fitColumns:true,
                         singleSelect:true,
                        onLoadSuccess:function(){
                            $(this).datagrid("enableDnd");
                        },
                        toolbar:"#toolbarScheduledJob",
                        pagination:false,
                        nowrap:false,
                        queryParams:{profile:getProfile()},
                        remoteFilter:true,
                        rownumbers:true,
                        singleSelect:true'
            >

                <thead>
                <tr>
                    <th data-options="field:'uniqueId',hidden:true">ID</th>
                    <th data-options="field:'id', width:2">District Id</th>
                    <th data-options="field:'description',width:10">Description</th>
                    <th data-options="field:'scan',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Enabled</th>
                    <th data-options="field:'scanChapterStart', width:2">Min Chapter</th>
                    <th data-options="field:'scanChapterEnd', width:2">Max Chapter</th>
                </tr>
                </thead>
            </table>


            <span style="font-size:20px">
                <div id="toolbarScheduledJob">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecordScheduledJob()">New Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecordScheduledJob()">Edit Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecordScheduledJob()">Delete Job</a>
                </div>
	        </span>

            <div id="dlgScheduledJob" class="easyui-dialog" style="width:400px;height:400px;padding:10px 20px"
                 closed="true" buttons="#dlg-buttonsScheduledJob">
                <div class="ftitle">Job Schedule</div>
                <form id="fmScheduledJob" method="post" novalidate>
                    <div class="fitem">
                        <label>District Id</label>
                        <input name="id" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:1"
                        >
                    </div>
                    <div class="fitem">
                        <label>Description</label>
                        <input id="description"
                               name="description"
                               required="true"
                               class="easyui-textbox"
                               style="width:220px"
                        >
                    </div>
                    <div class="fitem">
                        <label>Enable Scan</label>
                        <input name="scan"
                               class="easyui-checkbox" type="checkbox" value="true"
                        >
                    </div>
                    <div class="fitem">
                        <label>Starting Chapter</label>
                        <input name="scanChapterStart" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                    <div class="fitem">
                        <label>Ending Chapter</label>
                        <input name="scanChapterEnd" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                        <div class="fitem">
                            <label>Event</label>
                            <input name="event"
                                   class="easyui-checkbox" type="checkbox" value="true"
                            >
                        </div>
                    </div>
                </form>
            </div>
            <div id="dlg-buttonsScheduledJob">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="saveScheduledJob()" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgScheduledJob').dialog('close')" style="width:90px">Cancel</a>
            </div>



        </div>
    </div>


    <script type="text/javascript">

        function getProfile(){
            var _value = $("#profile").combobox('getValue');
            return _value;
        }

        function onProfileChange(){
            var profileId = getProfile();
            var _value = $("#profile").combobox('getText');
            var obj = {profile:profileId};
            $('#dgScheduledJob').datagrid('reload', obj);
            $('#cc').layout('panel', 'south').panel('setTitle', 'Profile: ' + profileId + " " + _value);
        }

        function newRecordScheduledJob(){
            var row = $('#dgScheduledJob').datagrid('getSelected');
            var rowIndex = '';
            $('#dlgScheduledJob').dialog('open').dialog('setTitle','New District');
            $('#fmScheduledJob').form('reset');
            url = 'DistrictAction.php?method=addDistrict' + rowIndex + "&profile=" + getProfile();
        }
        function editRecordScheduledJob(){
            var row = $('#dgScheduledJob').datagrid('getSelected');
            if (row){
                $('#dlgScheduledJob').dialog('open').dialog('setTitle','Edit Active Job');
                $('#fmScheduledJob').form('load',row);
                $('#scanChapterStart').numberspinner('setValue', row.scanChapterStart);
                $('#scanChapterEnd').numberspinner('setValue', row.scanChapterEnd);
                url = 'DistrictAction.php?method=updateDistrict&id='+row['uniqueId'] + "&profile=" + getProfile();
            }
        }
        function saveScheduledJob(){
            $('#fmScheduledJob').form('submit',{
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
                        $('#dlgScheduledJob').dialog('close');		// close the dialog
                        $('#dgScheduledJob').datagrid('reload');	// reload the user data
                    }
                }
            });
        }

        function deleteRecordScheduledJob(){
            var row = $('#dgScheduledJob').datagrid('getSelected');
            if (row){
                $.messager.confirm('Confirm','Are you sure you want to delete this district?',function(r){
                    if (r){
                        $.ajax({
                            type:    "POST",
                            url:     "DistrictAction.php?method=deleteDistrict" + "&profile=" + getProfile(),
                            data:    {id: row.uniqueId},
                            success: function(data) {
                                var dataObj = JSON.parse(data);
                                if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')){
                                    alert(dataObj.errorMessage);
                                }
                                $('#dgScheduledJob').datagrid('reload');
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

    </body>
    </html>
