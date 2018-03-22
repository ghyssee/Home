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
include_once documentPath (ROOT_PHP_MR_BO, "ProfileBO.php");

$DATAGRID_ID = "dgScheduledJob";
$FORM_ID = "fmScheduledJob";
$DIALOG_ID = "dlgScheduledJob";
$TOOLBAR_ID = "toolbarScheduledJob";
$DIALOG_BUTTONS_ID = "dlg-buttonsScheduledJob";
?>

<html>
    <head>
        <?php include documentPath (ROOT_PHP_TEMPLATES, "Stylesheet.php");?>
        <?php include documentPath (ROOT_PHP_TEMPLATES, "easyui.php");?>
    </head>
    <body>

    <?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>
    <h3>Job Manager</h3>


    <div id="cc" class="easyui-layout" style="width:95%;height:90%">
        <div data-options="region:'south',title:'Jobs',split:true" style="height:100%;padding:5px;">

            <table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:100%;height:95%"
                   title="List Of Scheduled Jobs"
                   idField="id"
                   url='JobManagerAction.php?method=list'
                   data-options='fitColumns:true,
                         singleSelect:true,
                        onLoadSuccess:function(){
                            $(this).datagrid("enableDnd");
                        },
                        toolbar:"#<?php echo $TOOLBAR_ID;?>",
                        footer:"#footerScheduledJob",
                        pagination:false,
                        nowrap:false,
                        queryParams:{profile:getProfile()},
                        remoteFilter:false,
                        rownumbers:true,
                        singleSelect:true'
            >

                <thead>
                <tr>
                    <th data-options="field:'id',hidden:true">ID</th>
                    <th data-options="field:'enabled',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Enabled</th>
                    <th data-options="field:'type',width:5">Type</th>
                    <th data-options="field:'description',width:15">Description</th>
                    <th data-options="field:'districtId',width:2">DistrictId</th>
                    <th data-options="field:'districtName', width:5, formatter:function(value,row){return row.district.description}">District</th>
                    <th data-options="field:'chapter', width:2">Chapter</th>
                    <th data-options="field:'jobId',width:2">jobId</th>
                    <th data-options="field:'jobName', width:10, formatter:function(value,row){return row.job.description}">Jobname</th>
                    <th data-options="field:'jobType', width:5, formatter:function(value,row){return row.job.type}">Type</th>
                    <th data-options="field:'consumable', width:2, formatter:function(value,row,index){return checkboxFormatter(row.job.consumable,row,index);}">Cnsmbl</th>
                    <th data-options="field:'consumableCost', width:2, formatter:function(value,row,index){return checkboxFormatter(row.job.consumableCost,row,index);}">Use Cnsmbl</th>
                    <th data-options="field:'loot', width:2, formatter:function(value,row,index){return checkboxFormatter(row.job.loot,row,index);}">Loot</th>
                    <th data-options="field:'money', width:2, formatter:function(value,row,index){return row.job.money;}">Money</th>
                    <th data-options="field:'energy', width:2, formatter:function(value,row,index){return row.job.energy;}">Energy</th>
                    <th data-options="field:'exp', width:2, formatter:function(value,row,index){return row.job.exp;}">Exp</th>
                    <th data-options="field:'minRange',width:2">Min Range</th>
                    <th data-options="field:'maxRange',width:2">Max Range</th>
                    <th data-options="field:'total', width:2,
                    formatter:function(value,row,index){return (row.numberOfTimesExecuted == null ? '0' : row.numberOfTimesExecuted) + '/' + row.total;}">
                        Total</th>
                </tr>
                </thead>
            </table>


            <span style="font-size:20px">
                <div id="<?php echo $TOOLBAR_ID;?>">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecordScheduledJob()">New Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecordScheduledJob()">Edit Job</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecordScheduledJob()">Delete Job</a>
                </div>
                <div id="footerScheduledJob">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-save" plain="true" onclick="saveJobList()">Save Job Order</a>
                    <?php
                    $smarty = initializeSmarty();
                    $smarty->assign('datagrid',$DATAGRID_ID);
                    $smarty->assign('layout','cc');
                    $smarty->display('MafiaReloadedProfile.tpl');
                    ?>
                </div>
	        </span>

            <div id="<?php echo $DIALOG_ID;?>" class="easyui-dialog" style="width:600px;height:500px;padding:10px 20px"
                 closed="true" buttons="#<?php echo $DIALOG_BUTTONS_ID;?>">
                <div class="ftitle">Job Schedule</div>
                <form id="<?php echo $FORM_ID;?>" method="post" novalidate>

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
                      url:'JobManagerAction.php?method=getJobTypes&profile=<?=getDefaultProfile() ?>'
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
                     url:'JobManagerAction.php?method=getDistricts&profile=<?=getDefaultProfile() ?>'
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
                     url:'JobManagerAction.php?method=getChapters&profile=<?=getDefaultProfile() ?>'
                     ">
                    </div>
                    <div class="fitem">
                        <label>Job</label>
                        <input id="ccJobId" name="jobId">
                        <script>
                            $('#ccJobId').combogrid({
                                width: 300,
                                panelWidth: 800,
                                idField: 'id',
                                fitColumns: true,
                                textField: 'description',
                                url: 'JobManagerAction.php?method=getJobs',
                                columns: [[
                                    {field: 'id', title: 'Id', width: 20},
                                    {field: 'description', title: 'Description', width: 200},
                                    {field: 'type', title: 'Type', width: 40},
                                    {field: 'energy', title: 'En/Sta', width: 50},
                                    {field: 'exp', title: 'Experience', width: 50},
                                    {field: 'money', title: 'Cash', width: 40},
                                    {
                                        field: 'consumable', title: 'Consumable', width: 50, align: 'center',
                                        formatter: function (value, row, index) {
                                            return checkboxFormatter(value, row, index);
                                        }
                                    },
                                    {
                                        field: 'consumableCost', title: 'Use Cnsmbl', width: 50, align: 'center',
                                        formatter: function (value, row, index) {
                                            return checkboxFormatter(value, row, index);
                                        }
                                    },
                                    {
                                        field: 'loot', title: 'Loot', width: 50, align: 'center',
                                        formatter: function (value, row, index) {
                                            return checkboxFormatter(value, row, index);
                                        }
                                    }
                                ]]
                            });
                        </script>
                    </div>
                    <div class="fitem">
                        <label>Total</label>
                        <input name="total" id="total" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                    <div class="fitem">
                        <label>Number Of Times Executed</label>
                        <input name="numberOfTimesExecuted" id="numberOfTimesExecuted" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                    <div class="fitem">
                        <label>Enabled</label>
                        <input name="enabled"
                               class="easyui-checkbox" type="checkbox" value="true"
                        >
                    </div>
                    <div class="fitem">
                        <label>Min Range</label>
                        <input name="minRange" id="minRange" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                    <div class="fitem">
                        <label>Max Range</label>
                        <input name="maxRange" id="maxRange" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                </form>
            </div>
            <div id="<?php echo $DIALOG_BUTTONS_ID;?>">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="saveScheduledJob()" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#<?php echo $DIALOG_ID;?>').dialog('close')" style="width:90px">Cancel</a>
            </div>



        </div>
    </div>

    <script type="text/javascript">

        function saveJobList(){
            var rows = $('#dgScheduledJob').datagrid('getRows');
            var object = {jobs:rows,profile:getProfile()
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

        function refreshJobs(obj){
            var grid = $('#ccJobId').combogrid('grid');
            grid.datagrid('reload', obj);
        }

        function onChapterChange(row){
            var obj = {
                "district": $('#districtId').combobox('getValue'),
                "profile": getProfile(),
                "chapter": row.id
            };
            refreshJobs(obj);
        }

        function onDistrictChange(row){
            var obj = {
                "district": row.id,
                "profile": getProfile()
            };
            refreshJobs(obj);
            //$('#jobId').combobox('reload', obj);
            $('#chapter').combobox('reload', obj);
        }

        function onchangeType(row){
            checkForm(row.id);
        }

        function checkForm(jobType){
            if (jobType == "CHAPTER"){
                //$("#jobId").combobox('setValue', null);
                //$( "#jobId" ).combobox({ disabled: true });
                $('#ccJobId').combogrid('setValue', null);
                $( "#ccJobId" ).combogrid('disable');
                //$( "#ccJobId" ).combogrid({ disabled: true });
                $("#total").numberspinner('disable');
                $("#total").numberspinner('setValue', 0);
            }
            else {
                //$("#jobId").combobox({ disabled: false });
                //$("#ccJobId").combogrid({ disabled: false });
                $( "#ccJobId" ).combogrid('enable');
                $("#total").numberspinner('enable');
            }
        }

        function newRecordScheduledJob(){
            var row = $('#<?php echo $DATAGRID_ID;?>').datagrid('getSelected');
            var rowIndex = '';
            if (row){
              rowIndex='&insertBefore=' + row.id;
            }
            newRecord('<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'Scheduled Job', 'JobManagerAction.php?method=addActiveJob' + rowIndex + "&profile=" + getProfile());
            $('#districtId').combobox('setValue', '1');
        }

        function editRecordScheduledJob(){
            editRecord('<?php echo $DATAGRID_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'id', 'Scheduled Job', 'JobManagerAction.php?method=updateActiveJob' + "&profile=" + getProfile());
            var row = $('#dgScheduledJob').datagrid('getSelected');
            if (row){
                checkForm(row.type);
                var obj = {
                    "district": row.districtId,
                    "chapter": row.chapter,
                    "profile": getProfile()
                };
                refreshJobs(obj);
                //if (row.type != 'CHAPTER') {
                    //$('#ccJobId').combogrid('setValue', row.jobId);
                //}
            }
        }
        function saveScheduledJob(){
            saveRecord('<?php echo $FORM_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $DATAGRID_ID;?>');
        }

        function deleteRecordScheduledJob(){
            deleteRecordV2('<?php echo $DATAGRID_ID;?>', 'Scheduled Job', 'id', "JobManagerAction.php?method=deleteScheduledJob" + "&profile=" + getProfile());
        }
    </script>

    </body>
</html>

<?php
    function getDefaultProfile(){
        return DEFAULT_PROFILE;
    }
    ?>