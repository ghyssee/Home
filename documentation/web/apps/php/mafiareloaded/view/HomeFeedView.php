<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 11/12/2017
 * Time: 12:59
 */
session_start();
include_once("../../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "ProfileBO.php");
?>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body>

<?php
?>
<h3>Homefeed</h3>
<div id="cc" class="easyui-layout" style="width:100%;height:90%">
    <div data-options="region:'center',title:'Homefeed',split:true" style="height:80%;padding:5px;">

        <table id="dgHomeFeed" class="easyui-datagrid" style="width:50%;height:100%"
               title="List Of Homefeed Attackers"
               idField="id"
               url='HomeFeedAction.php?method=list'
               data-options='fitColumns:true,
                         singleSelect:true,
                        onLoadSuccess:function(){
                        },
                        toolbar:"#toolbarHomeFeed",
                        pagination:true,
                       pagePosition:"both",
                       pageSize:10,
                       pageList:[5,10,15,20,25,30,40,50],
                        nowrap:false,
                        queryParams:{profile:getProfile(),history:true},
                        rownumbers:true,
                        singleSelect:true'
        >

            <thead>
            <tr>
                <th data-options="field:'id', width:10">Id</th>
                <th data-options="field:'fighterId', width:10, formatter: formatFighterId">Fighter Id</th>
                <th data-options="field:'name',width:20">Name</th>
                <th data-options="field:'gangId',width:5">GangId</th>
                <th data-options="field:'gangName',width:15">GangName</th>
                <th data-options="field:'timeStamp', width:10, formatter:function(value,row,index){return formatDate(row.timeStamp);}">Time</th>
            </tr>
            </thead>
        </table>


        <span style="font-size:20px">
                <div id="toolbarHomeFeed">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteLine()">Delete Line</a>
                    Profile: <input id="profile" class="easyui-combobox" name="profile"
                                    data-options="valueField:'id',
                                                width:200,
                                                limitToList: true,
                                                textField:'name',
                                                onChange: function(row){
                                                    onProfileChange(row);
                                                },
                                                url:'ProfileAction.php?method=getProfiles'
                                    ">
                    <button type="button" onclick="refreshDatagrid()">Refresh</button>
                     <label>History </label><input id="history" class="easyui-switchbutton">
                </div>
	        </span>

    </div>
    <div data-options="region:'south',title:'Homefeed Maintenance'" style="height:20%;padding:5px;">
        <form id="homefeed" method="post">
            <div class="fitem">
                <label title="The number of days to keep. The older lines will be moved to the history table." for="daysToKeep" style="width:120px;">Number of days to keep</label>
                <input name="daysToKeep" id="daysToKeep" class="easyui-numberspinner" style="width:80px;"
                       data-options="min:7, required:true, value:30"
                >
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="cleanUp()" style="width:90px">Cleanup</a>
            </div>
        </form>
</div>


<script type="text/javascript">
        $(function(){
            $('#history').switchbutton({
                checked: false,
                onChange: function(checked){
                    refreshDatagrid();
                }
            })
        });

    function cleanUp(){
        submitForm('homefeed', 'HomeFeedAction.php?method=cleanup&profile=' + getProfile());
        refreshDatagrid();
    }

    function refreshDatagrid(){
        var obj = {profile:getProfile(),history:getSwitchValue("history")};
        $('#dgHomeFeed').datagrid('reload', obj);
        return false;
    }

    function deleteLine(){
        deleteRecord("line", "dgHomeFeed",  "HomeFeedAction.php?method=delete" + "&profile=" + getProfile() + "&history=" + getSwitchValue("history"), "id");
    }


    function formatDate(strDate){
        var newDate =  strDate.substr(6,2) + '/' + strDate.substr(4,2) + '/' + strDate.substr(0,4) + ' ' + strDate.substr(8,2) + ':' + strDate.substr(10,2) + ':' + strDate.substr(12,2);
        return newDate;
    }

    function formatFighterId(val,row){
        var url = "https://mafiareloaded.com/game/?controller=profile&id=" + encodeURIComponent(row.fighterId);
        var cellInfo = '<a href="'+url+'" target="_blank">'+val+'</a>';
        return cellInfo;
    }

    function getProfile(){
        var _value = $("#profile").combobox('getValue');
        if (_value){
            // do nothing
        }
        else {
            _value = "<?php echo DEFAULT_PROFILE ?>";
        }
        return _value;
    }

    function onProfileChange(){
        var profileId = getProfile();
        refreshDatagrid();
        var _value = $("#profile").combobox('getText');
        $('#cc').layout('panel', 'center').panel('setTitle', 'Profile: ' + profileId + " " + _value);
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
