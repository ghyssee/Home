<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
const FORM_ID = "settings";
?>

<form id="<?php echo FORM_ID;?>" method="post">
    <div class="fitem">
        <label for="minLengthOfFightList">Min. Length Of Fight List</label>
        <input name="minLengthOfFightList" id="minLengthOfFightList" class="easyui-numberspinner" style="width:150px;"
               data-options="min:1"
        >
    </div>
    <div class="fitem">
        <label for="autoHeal">AutoHeal</label>
        <input name="autoHeal"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label for="heal">Heal Limit</label>
        <input name="heal" id="heal" class="easyui-numberspinner" style="width:150px;"
               data-options="min:0"
        >
    </div>
    <div class="fitem">
         <label for="numberOfHealsLimit">Max. Number of Rival Heals</label>
         <input name="numberOfHealsLimit" id="numberOfHealsLimit" class="easyui-numberspinner" style="width:150px;"
                data-options="min:0"
         >
    </div>
    <div class="fitem">
        <label for="stopWhenStaminaBelow">Stop When Stamina Below</label>
        <input name="stopWhenStaminaBelow" id="stopWhenStaminaBelow" class="easyui-numberspinner" style="width:150px;"
               data-options="min:0"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo FORM_ID;?>', 'SettingsAction.php?method=saveSettingsFighting')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsFighting();" style="width:90px">Undo</a>
    </div>
</form>

<script>
    function loadSettingsFighting(){
        $('#<?php echo FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsFighting');
    }
    loadSettingsFighting();
</script>