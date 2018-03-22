<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 16/03/2018
 * Time: 9:16
 */
$FORM_ID = "reportKilled";

session_start();
include_once("../../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "JobBO.php");
include_once('Smarty.class.php');
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
<?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>
<h3>Report Of Deaths</h3>

<div id="cc" class="easyui-layout" style="width:100%;height:90%">
    <div data-options="region:'north',title:'Report For Being Killed',split:true" style="height:50%;padding:5px;">
        <form id="<?php echo $FORM_ID;?>" method="post">
            <div class="fitem">
                <?php
                $smarty = initializeSmarty();
                $smarty->display('MafiaReloadedProfile.tpl');
                ?>
            </div>
            <div class="fitem">
                <label title="Gang Id">Gang Id</label>
                <input name="gangId" id="gangId" class="easyui-textbox" style="width:150px;"
                >
            </div>
            <div class="fitem">
                <label title="Fighter Id">Fighter Id</label>
                <input name="fighterId" id="fighterId" class="easyui-textbox" style="width:150px;"
                >
            </div>
            <div class="fitem">
                <label>Include History</label>
                <input name="history"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
               onclick="submitFormMessage('<?php echo $FORM_ID;?>', 'ReportGettingKilledAction.php?report=view', 'test123')" style="width:90px">Save</a>
        </form>
    </div>
    <div data-options="region:'south',title:'List',split:true" style="height:50%;padding:5px;">
        <div id="test123">
        </div>
    </div>
</div>
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
