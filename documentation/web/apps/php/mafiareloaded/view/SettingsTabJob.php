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
    <div id="ccJobs" class="easyui-layout" style="width:99%;height:300px;">
        <div data-options="region:'west',title:'Jobs',collapsible:false" style="width:50%;">
            <div class="fitem">
                <label title="Only money jobs will be executed. All other scheduled jobs will be ignored">Do only money jobs</label>
                <input name="<?php echo JobSettingsTO::getBase()?>_money"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
            <div class="fitem">
                <label title="Do Stamina Jobs">
                    Stamina Jobs</label>
                <input name="<?php echo JobSettingsTO::getBase()?>_stamina"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
            <div class="fitem">
                <label title="Do Money Jobs for leveling up when Experience points smaller than this amount. Remark: Scheduled jobs will first be executed.">
                    Max Experience For Level Up</label>
                <input name="<?php echo JobSettingsTO::getBase()?>_levelUpExp" id="<?php echo JobSettingsTO::getBase()?>_levelUpExp" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:100"
                >
            </div>
            <div class="fitem">
                <label title="Wait for minimum Energy for level up money jobs">Minimum Energy for level up money job</label>
                <input name="<?php echo JobSettingsTO::getBase()?>_levelUpMinEnergy" id="<?php echo JobSettingsTO::getBase()?>_levelUpMinEnergy" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:5"
                >
            </div>
        </div>
        <div data-options="region:'center',title:'Robbing'" style="width:50%;">
            <div class="fitem">
                <label title="Enable/Disable Robbing">
                    Enabled</label>
                <input name="<?php echo RobbingSettingsTO::getBase()?>_enabled"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
        </div>
        <div data-options="region:'south',collapsible:false,border:false" style="width:100%;">
            <div id="dlg-buttonsScheduledJob">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsJob&profile=' + getProfile())" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsJob();" style="width:90px">Undo</a>
            </div>
        </div>
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