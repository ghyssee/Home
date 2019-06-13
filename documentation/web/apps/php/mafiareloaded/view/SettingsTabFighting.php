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

    <div id="ccFight" class="easyui-layout" style="width:99%;height:300px;">
        <div data-options="region:'west'" style="width:33%;">
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
                <label for="profileAttackSize">Profile Attack Size</label>
                <input name="profileAttackSize" id="profileAttackSize" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="staminaCost">Stamina Cost</label>
                <input name="staminaCost" id="staminaCost" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="staminaCostHealth">Stamina Cost Health</label>
                <input name="staminaCostHealth" id="staminaCostHealth" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
        </div>
        <div data-options="region:'center'" style="width:33%;">

            <div class="fitem">
                <label for="maxNumberOfAttacks">Max. Number of attacks</label>
                <input name="maxNumberOfAttacks" id="maxNumberOfAttacks" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="minStaminaToFight">Min. Stamina to start fighting</label>
                <input name="minStaminaToFight" id="minStaminaToFight" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="maxKillsDay">Max. Daily Kills for a fighter</label>
                <input name="maxKillsDay" id="maxKillsDay" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="stopWhenStaminaBelow">Stop When Stamina Below</label>
                <input name="stopWhenStaminaBelow" id="stopWhenStaminaBelow" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="minLevel">Minimum Level To Attack</label>
                <input name="minLevel" id="minLevel" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="attackTillDiedHealth">Health To Keep Attacking</label>
                <input name="attackTillDiedHealth" id="attackTillDiedHealth" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
        </div>
        <div data-options="region:'east'" style="width:33%;">
            <div class="fitem">
                <label for="waitTillEnoughStamina">Wait till enough Stamina</label>
                <input name="waitTillEnoughStamina" id="waitTillEnoughStamina" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="waitingTimeKilled">Waiting Time Killed</label>
                <input name="waitingTimeKilled" id="waitingTimeKilled" class="easyui-numberspinner" style="width:150px;"
                       data-options="min:0"
                >
            </div>
            <div class="fitem">
                <label for="rivals">Rivals</label>
                <input name="rivals"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
                <label for="fightList">Fightlist</label>
                <input name="fightList"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
                <label for="profileAttack">Profile Attack</label>
                <input name="profileAttack"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
                <label for="wiseguy">Wiseguy</label>
                <input name="wiseguy"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
                <label for="war">War</label>
                <input name="war_enabled"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
        </div>

        <div data-options="region:'south'" style="width:33%;">
            <div id="dlg-buttonsScheduledJob">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="submitForm('<?php echo FORM_ID;?>', 'SettingsAction.php?method=saveSettingsFighting&profile=' + getProfile())" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsFighting();" style="width:90px">Undo</a>
            </div>

        </div>
    </div>


</form>

<script>
    function loadSettingsFighting(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsFighting&profile=' + profile);
    }
    loadSettingsFighting();
</script>