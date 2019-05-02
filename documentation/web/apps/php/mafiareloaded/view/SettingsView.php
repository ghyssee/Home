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
include_once documentPath (ROOT_PHP_MR_BO, "ProfileSettingsBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "SettingsBO.php");
session_start();
?>
<link rel="stylesheet" type="text/css" href="../../css/form.css">
<script type="text/javascript" src="<?php echo webPath(ROOT_JS_UTILS, 'MyUtils.js')?>"></script>
<script type="text/javascript" src="<?php echo webPath(ROOT_JS_UTILS, 'MafiaReloaded.js')?>"></script>
<script>
var INITIAL_LOAD = true;

function getProfile(){
    var _def_value = '<?php echo DEFAULT_PROFILE?>';
    var _value = _def_value;
    try {
        _value = $("#profile").combobox('getValue');
        if (_value === "") {
            _value = _def_value;
        }
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
    <?php include documentPath (ROOT_PHP_TEMPLATES, "easyui.php");?>
</head>
<body style="background">

<?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>

<h3>Settings</h3>

<div id="cc" class="easyui-layout" style="width:900px;height:550px;">
    <div data-options="region:'north',title:'Profile',split:true" style="height:100px;padding:5px">
        Profile: <input id="profile" class="easyui-combobox" name="profile"
                        data-options="valueField:'id',
                                                width:200,
                                                limitToList: true,
                                                valueField: 'id',
                                                textField:'name',
                                                onChange: function(row){
                                                    onProfileChangeForm(row);
                                                },
                                                url:'ProfileAction.php?method=getProfiles'
                                    ">

    </div>
    <div data-options="region:'center',title:'Settings'" style="padding:5px;background:#eee;">

        <div id="settingsTab" class="easyui-tabs" data-options="selected:0" style="width:100%;height:100%;">
            <div title="Fighting" style="padding:20px;display:none;">
                   <?php include "SettingsTabFighting.php"; ?>
            </div>
            <div title="Boss" style="padding:20px;display:none;">
                <?php include "SettingsTabBoss.php"; ?>
            </div>
            <div title="Job Settings" style="padding:20px;display:none;">
                <?php include "SettingsTabJob.php"; ?>
            </div>
            <div title="Crime Job Settings" style="padding:20px;display:none;">
                <?php include "SettingsTabCrime.php"; ?>
            </div>
            <div title="Homefeed Settings" style="padding:20px;display:none;">
                <?php include "SettingsTabHomefeed.php"; ?>
            </div>
            <div title="Profile Settings" style="padding:20px;display:none;">
                <?php include "SettingsTabProfileGlobal.php"; ?>
            </div>
            <div title="Global Settings" style="overflow:auto;padding:20px;display:none;" >
                <?php include "SettingsTabGlobalDaily.php"; ?>
            </div>
            <div title="Assassin-a-nator" style="overflow:auto;padding:20px;display:none;" >
                <?php include "SettingsTabAssassin.php"; ?>
            </div>
            <div title="Allies" style="overflow:auto;padding:20px;display:none;" >
                <?php include "SettingsTabAllies.php"; ?>
            </div>
            <div title="Bullies" style="overflow:auto;padding:20px;display:none;" >
                <?php include "SettingsTabBullies.php"; ?>
            </div>
        </div>
    </div>
<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>
<script>
    function onProfileChangeForm(row){
        if (INITIAL_LOAD){
            INITIAL_LOAD = false;
        }
        else {
            loadSettingsFighting(row);
            loadSettingsBoss(row);
            loadSettingsJob(row);
            loadSettingsHomefeed(row);
            loadSettingsProfileGlobal(row);
            loadSettingsCrime(row);
            loadSettingsAssassin(row);
            refreshAllies();
            refreshBullies(row);
        }
    }
</script>

</body>
</html>
