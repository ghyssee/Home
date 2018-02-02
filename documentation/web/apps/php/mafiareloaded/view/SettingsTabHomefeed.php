<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
include_once documentPath (ROOT_PHP_MR_BO, "MafiaReloadedBO.php");
$FORM_ID = "settingsHomeFeed";
?>

<form id="<?php echo $FORM_ID;?>" method="post">
    <div class="fitem">
        <label title="Process the homefeed lines and clear the homefeed after that">Process Homefeed Lines</label>
        <input name="processLines"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Attack Players that killed your mini account">Attack Players</label>
        <input name="attack"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="The last n Number of players that attacked your mini">
            Attack Size</label>
        <input name="attackSize" id="attackSize" class="easyui-numberspinner" style="width:150px;"
               data-options="min:1"
        >
    </div>
    <div class="fitem">
        <label title="Get all players that attacked your mini and add them to your fight list, if not already added">
            Check Attackers of your Mini account</label>
        <input name="checkMini"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsHomefeed&profile=' + getProfile())" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsHomefeed();" style="width:90px">Undo</a>
    </div>
</form>

<script>
    $('#<?php echo $FORM_ID;?>').form({
        onLoadSuccess:function(data){
        },
        onLoadError:function(){
            alert("Error Loading Form <?php echo $FORM_ID;?>");
        }
    });
    function loadSettingsHomefeed(profile){

        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsHomefeed&profile=' + profile);
    }
    loadSettingsHomefeed();

</script>