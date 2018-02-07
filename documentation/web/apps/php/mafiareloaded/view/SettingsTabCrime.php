<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
$FORM_ID = "settingsCrime";
?>

<form id="<?php echo $FORM_ID;?>" method="post">
    <div class="fitem">
        <label title="Enable Crime Event">Crime Event</label>
        <input name="enabled"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Start a new crime">Start New Crime</label>
        <input name="startNewCrime"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Do Crime Jobs you joined">Help on Crime Job</label>
        <input name="help"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Position of the crime job (1-4)">
            Position</label>
        <input name="position" id="position" class="easyui-numberspinner" style="width:150px;"
               data-options="min:1, max:4, value:1"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsCrime&profile=' + getProfile())" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsCrime();" style="width:90px">Undo</a>
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
    function loadSettingsCrime(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsCrime&profile=' + profile);
    }
    loadSettingsCrime();
</script>