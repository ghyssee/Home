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
session_start();
?>
<script type="text/javascript" src="<?php echo webPath(ROOT_JS_UTILS, 'MyUtils.js')?>"></script>

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

<div id="settingsTab" class="easyui-tabs" data-options="selected:0" style="width:700px;height:400px;">
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

<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>

</body>
</html>

