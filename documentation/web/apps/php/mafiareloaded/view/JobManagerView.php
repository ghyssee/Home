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

    <script>
        function setValue(){
            $("#oldSong").textbox('setValue', 'TTTTTTTTTTEEEEEEEEEESSSSSSTTTTTT');
        }
    </script>
    <select class="easyui-combogrid" style="width:100%" data-options="
                    panelWidth: 500,
                    idField: 'itemid',
                    textField: 'productname',
                    url: 'JobManagerAction.php?method=getJobs',
                    method: 'get',
                    columns: [[
                        {field:'id',title:'JobId',width:80},
                        {field:'description',title:'Description',width:120}
                    ]],
                    fitColumns: true,
                    label: 'Select Job:',
                    labelPosition: 'top'
                ">

    <div id="cc" class="easyui-layout" style="width:95%;height:90%">
        <div data-options="region:'north',title:'Actions',split:true" style="height:20%;">
            <div class="Table">
                <div class="Row">
                    <div class="Cell vertical-center">
                        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                           onclick="saveJobList()" style="width:90px;">Save</a>
                    </div>
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

        <input type="profile" name="profile" value="<?php echo getProfile(); ?>">
        <div data-options="region:'south',title:'Jobs',split:true" style="height:80%;padding:5px;">

            <table id="dgScheduledJob" class="easyui-datagrid" style="width:100%;height:95%"
                   title="List"
                   idField="id"
                   url='JobManagerAction.php?method=list'
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
                    <th data-options="field:'id',hidden:true">ID</th>
                    <th data-options="field:'enabled',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Enabled</th>
                    <th data-options="field:'type',width:2">Type</th>
                    <th data-options="field:'description',width:10">Description</th>
                    <th data-options="field:'districtId',width:2">DistrictId</th>
                    <th data-options="field:'districtName', width:10, formatter:function(value,row){return row.district.description}">District</th>
                    <th data-options="field:'chapter', width:2">Chapter</th>
                    <th data-options="field:'jobId',width:2">jobId</th>
                    <th data-options="field:'jobName', width:10, formatter:function(value,row){return row.job.description}">Jobname</th>
                    <th data-options="field:'consumable', width:2, formatter:function(value,row,index){return checkboxFormatter(row.job.consumable,row,index);}">Consumable</th>
                    <th data-options="field:'money', width:2, formatter:function(value,row,index){return row.job.money;}">Money</th>
                    <th data-options="field:'energy', width:2, formatter:function(value,row,index){return row.job.energy;}">Energy</th>
                    <th data-options="field:'exp', width:2, formatter:function(value,row,index){return row.job.exp;}">Experience</th>
                    <th data-options="field:'total', width:2">Total</th>
                    <th data-options="field:'numberOfTimesExecuted', width:2">NumberOfTimesExecuted</th>
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
                        <label>Job Type</label>
                        <input id="type" class="easyui-combobox" name="type"
                               required="true"
                               data-options="valueField:'id',
                     width:200,
                     limitToList: true,
                     textField:'description',
                     onSelect: function(row){
                        onchangeType(row);
                      },
                      url:'JobManagerAction.php?method=getJobTypes'
                     ">
                    </div>
                    <div class="fitem">
                        <label>District</label>
                        <input id="districtId" class="easyui-combobox" name="districtId"
                               required="true"
                               data-options="valueField:'id',
                     width:200,
                     limitToList: true,
                     textField:'description',
                     onSelect: function(row){
                        onDistrictChange(row);
                      },
                     url:'JobManagerAction.php?method=getDistricts'
                     ">
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
                        <label>Chapter</label>
                        <input id="chapter" class="easyui-combobox" name="chapter"
                               required="true"
                               data-options="valueField:'id',
                     width:200,
                     limitToList: true,
                     textField:'name',
                     onLoadSuccess: function(param){
		                resetComboBox(param, '#chapter', true);
	                 },
                     onSelect: function(row){
                        onChapterChange(row);
                      },
                    formatter:function(row){
                        return row.id + ' ' + row.name;
                        },
                     url:'JobManagerAction.php?method=getChapters'
                     ">
                    </div>
                    <div class="fitem">
                        <label>Job</label>
                        <input id="jobId" class="easyui-combobox" name="jobId"
                               data-options="valueField:'id',
                     width:200,
                     limitToList: true,
                     textField:'description',
                     onLoadSuccess: function(param){
		                resetJob(param, '#jobId', true);
	                 },
	                 formatter:function(row){
                        return 'Chapter ' + row.chapter + ' - ' + row.id + ' - ' + row.description;
                     },
                     queryParams:{productid:getProfile()},
                     url:'JobManagerAction.php?method=getJobs'
                     ">
                    </div>
                    <div class="fitem">
                        <label>Total</label>
                        <input name="total" id="total" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
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

        function saveJobList(){
            var rows = $('#dgScheduledJob').datagrid('getRows');
            var object = {jobs:rows,
            };
            var tmp = $.post('JobManagerAction.php?method=saveJobList', { config : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    //clearArtists();
                    alert("Job List successfully saved");
                    //$('#dgMultiArtistList').datagrid('reload');
                }
                else {
                    if (data2.message != null && data2.message != '') {
                        alert(data2.message);
                    }
                    else {
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
        }

        function resetJob(param, cmbId, setDefaultToFirstItem){
            var _type = $("#type").combobox('getValue');
            if (_type != 'CHAPTER'){
                resetComboBox(param, cmbId, setDefaultToFirstItem);
            }
            else {
                $(cmbId).combobox('setValue', null);
            }
        }

        function resetComboBox(param, cmbId, setDefaultToFirstItem){
            var _options = $(cmbId).combobox('options');
            var _data = $(cmbId).combobox('getData');
            var _value = $(cmbId).combobox('getValue');
            var _value1 = '';
            var _found = false;
            for (var i = 0; i < _data.length; i++) {
                if (i==0){
                    _value1 = _data[i][_options.valueField];
                }
                if (_data[i][_options.valueField] == _value) {
                    _found = true;
                    break;
                }
            }
            if (!_found && setDefaultToFirstItem){
                $(cmbId).combobox('setValue', _value1);
            }
        }

        function onChapterChange(row){
            var obj = {
                "district": $('#districtId').combobox('getValue'),
                "profile": getProfile(),
                "chapter": row.id
            };
            $('#jobId').combobox('reload', obj);
        }

        function onChapterChange2(row){
            var districtId = $('#districtId').combobox('getValue');
            var profile = getProfile();
            var url = 'JobManagerAction.php?method=getJobs&profile=' + profile + '&district='+districtId + '&chapter=' + row.id;
            $('#jobId').combobox('reload', url);
        }

        function onDistrictChange(row){
            var obj = {
                "district": row.id,
                "profile": getProfile()
            };
            $('#jobId').combobox('reload', obj);
            $('#chapter').combobox('reload', obj);
        }

        function onDistrictChange2(row){
            var url = 'JobManagerAction.php?method=getJobs&district='+row.id;
            $('#jobId').combobox('reload', url);
            var url = 'JobManagerAction.php?method=getChapters&district='+row.id;
            $('#chapter').combobox('reload', url);
        }

        function onchangeType(row){
            checkForm(row.id);
        }

        function checkForm(jobType){
            if (jobType == "CHAPTER"){
                $("#jobId").combobox('setValue', null);
                $( "#jobId" ).combobox({ disabled: true });
                $("#total").numberspinner({ disabled: true });
            }
            else {
                $("#jobId").combobox({ disabled: false });
                $("#total").numberspinner({ disabled: false });
            }
        }

        function newRecordScheduledJob(){
            var row = $('#dgScheduledJob').datagrid('getSelected');
            var rowIndex = '';
            if (row){
                rowIndex='&insertBefore=' + row.id;
            }
            $('#dlgScheduledJob').dialog('open').dialog('setTitle','New Scheduled Job');
            $('#fmScheduledJob').form('reset');
            $('#districtId').combobox('setValue', '1');
            url = 'JobManagerAction.php?method=addActiveJob' + rowIndex + "&profile=" + getProfile();
        }
        function editRecordScheduledJob(){
            var row = $('#dgScheduledJob').datagrid('getSelected');
            if (row){
                $('#dlgScheduledJob').dialog('open').dialog('setTitle','Edit Active Job');
                $('#fmScheduledJob').form('load',row);
                checkForm(row.type);
                $('#total').numberspinner('setValue', row.total);

                if (row.type != 'CHAPTER') {
                    $('#jobId').combobox('setValue', row.jobId);
                }
                url = 'JobManagerAction.php?method=updateActiveJob&id='+row['id'] + "&profile=" + getProfile();
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
                $.messager.confirm('Confirm','Are you sure you want to delete this Scheduled Job?',function(r){
                    if (r){
                        $.ajax({
                            type:    "POST",
                            url:     "JobManagerAction.php?method=deleteScheduledJob" + "&profile=" + getProfile(),
                            data:    {id: row.id},
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
    <script type="text/javascript">
        $(function(){
            var dg = $('#dgScheduledJob');
            dg.datagrid('enableFilter');
        });
    </script>

    </body>
</html>

<?php
    function getProfile(){
        return "01";
    }
    ?>