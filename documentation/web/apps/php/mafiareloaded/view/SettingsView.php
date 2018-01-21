<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:26
 */

include_once("../../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "ProfileBO.php");
session_start();
?>
<script type="text/javascript" src="<?php echo webPath(ROOT_JS_UTILS, 'MyUtils.js')?>"></script>
<script>
function getProfile(){
    var _value = '<?php echo DEFAULT_PROFILE?>';
    try {
    _value = $("#profile").combobox('getValue');
    }
    catch (err){
    }
    return _value;
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
        width:160px;
    }
    .fitem input{
        width:160px;
    }
</style>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body style="background">

<?php
goMenu();
?>

<h3>Settings</h3>

<div id="cc" class="easyui-layout" style="width:900px;height:400px;">
    <div data-options="region:'north',title:'North Title',split:true" style="height:100px;padding:5px">
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

    </div>
    <div data-options="region:'center',title:'center title'" style="padding:5px;background:#eee;">

        <div id="settingsTab" class="easyui-tabs" data-options="selected:0" style="width:100%;height:100%;">
            <div title="Settings" style="padding:20px;display:none;">
                   <?php include "SettingsTab1.php"; ?>
            </div>
            <div title="Boss" style="padding:20px;display:none;">
                <?php include "SettingsBoss.php"; ?>
            </div>
            <div title="Job Settings" style="padding:20px;display:none;">
                <?php include "SettingsJob.php"; ?>
            </div>
            <div title="Homefeed Settings" style="padding:20px;display:none;">
                <?php include "SettingsHomefeed.php"; ?>
            </div>
            <div title="Daily Link" style="overflow:auto;padding:20px;display:none;" >
                <?php include "SettingsTab2.php"; ?>
            </div>
        </div>
    </div>
<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>

</body>
</html>
