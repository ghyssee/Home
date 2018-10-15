<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
$FORM_ID = "settingsJob";
?>

<form id="<?php echo $FORM_ID;?>" method="post">
    <div class="fitem">
        <label title="Only money jobs will be executed. All other scheduled jobs will be ignored">Do only money jobs</label>
        <input name="money"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Do Stamina Jobs">
            Stamina Jobs</label>
        <input name="stamina"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Do Money Jobs for leveling up when Experience points smaller than this amount. Remark: Scheduled jobs will first be executed.">
            Max Experience For Level Up</label>
        <input name="levelUpExp" id="levelUpExp" class="easyui-numberspinner" style="width:150px;"
               data-options="min:100"
        >
    </div>
    <div class="fitem">
        <label title="Wait for minimum Energy for level up money jobs">Minimum Energy for level up money job</label>
        <input name="levelUpMinEnergy" id="levelUpMinEnergy" class="easyui-numberspinner" style="width:150px;"
               data-options="min:5"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsJob&profile=' + getProfile())" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsJob();" style="width:90px">Undo</a>
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
    function loadSettingsJob(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsJob&profile=' + profile);
    }
    loadSettingsJob();
</script>