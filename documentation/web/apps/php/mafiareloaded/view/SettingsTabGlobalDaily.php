<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
    CONST FORM_DAILY_LINK = "globalSettingsDaily";
    CONST FORM_GLOBAL = "globalSettings";
    CONST FORM_GLOBAL_BOSS = "globalSettingsBoss";
?>
<script>
    $.fn.datebox.defaults.formatter = function(date){
        var strDate = getDateYYYYMMDD(date);
        return strDate;
    };
    $.fn.datebox.defaults.parser = function(s){
        var date = formatStringYYYYMMDDToDate(s);
        return date;
    };
</script>

<form id="<?php echo FORM_GLOBAL;?>" method="post">
    <div id="ccFight" class="easyui-layout" style="width:99%;height:300px">
        <div data-options="region:'west'" style="width:50%;border:none;">
            <fieldset>
                <legend>Daily Link</legend>
                <div class="fitem">
                    <label for="<?php echo DailyLinkTO::getBase()?>_date">Date</label>
                    <input id="<?php echo DailyLinkTO::getBase()?>_date" name="<?php echo DailyLinkTO::getBase()?>_date" type="text" class="easyui-datebox" required="required"
                    >
                </div>
                <div class="fitem">
                    <label for="<?php echo DailyLinkTO::getBase()?>_link">Link</label>
                    <input name="<?php echo DailyLinkTO::getBase()?>_link" id="<?php echo DailyLinkTO::getBase()?>_link" class="easyui-textbox" data-options="required:true" style="width:350px;"
                    >
                </div>
            </fieldset>

            <fieldset>
                <legend>Fighting</legend>
                <div class="fitem">
                    <label title="Name of rival mobster" for="<?php echo GlobalFightingSettingsTO::getBase()?>_rival">Rival mobster</label>
                    <input name="<?php echo GlobalFightingSettingsTO::getBase()?>_rival" id="<?php echo GlobalFightingSettingsTO::getBase()?>_rival" class="easyui-textbox" data-options="required:true" style="width:300px;"
                    >
                </div>
            </fieldset>
            <fieldset>
                <legend>Global</legend>
            <div title="Secret District is on/off" class="fitem">
                <label>Event</label>
                <input name="<?php echo GlobalSettingsTO::getBase()?>_eventEnabled"
                       class="easyui-checkbox" type="checkbox" value="true"
                >
            </div>
            </fieldset>
        </div>
        <div data-options="region:'east'" style="width:50%;border:none;">
            <fieldset>
                <legend>Robbing</legend>
            <div class="fitem">
                <label title="Number of Properties to rob">Nr. Properties</label>
                <input name="<?php echo GlobalRobbingSettingsTO::getBase()?>_nrOfProperties" id="<?php echo GlobalRobbingSettingsTO::getBase()?>_nrOfProperties" class="easyui-numberspinner" style="width:50px;"
                       data-options="min:1,max:10"
                >
            </div>
            <div class="fitem">
                <label title="List of Property Names for robbing seperated by ;" for="<?php echo GlobalRobbingSettingsTO::getBase()?>_properties">Properties</label>
                <input name="<?php echo GlobalRobbingSettingsTO::getBase()?>_properties" id="<?php echo GlobalRobbingSettingsTO::getBase()?>_properties" class="easyui-textbox" data-options="required:true" style="width:300px;"
                >
            </div>
            </fieldset>
            <fieldset>
                <legend>Boss</legend>
                    <div title="Boss Name" class="fitem">
                        <label>Boss Name</label>
                        <input name="<?php echo GlobalSettingsBossTO::getBase()?>_bossName" id="<?php echo GlobalSettingsBossTO::getBase()?>_bossName" class="easyui-textbox" data-options="required:true" style="width:350px;">
                    </div>
            </fieldset>
        </div>
        <div data-options="region:'south'" style="width:100%;border:none;">
            <div class="centered">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="submitForm('<?php echo FORM_GLOBAL;?>', 'SettingsAction.php?method=saveGlobalSettings')" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettings();" style="width:90px">Undo</a>
            </div>
        </div>
    </div>
</form>

<script>
    function loadGlobalSettingsDaily(){
        $('#<?php echo FORM_DAILY_LINK;?>').form('load','SettingsAction.php?method=getDailyLink');
    }

    function loadGlobalSettings(){
        $('#<?php echo FORM_GLOBAL;?>').form('load','SettingsAction.php?method=getGlobalSettings');
    }

    function loadGlobalSettingsBoss(){
        $('#<?php echo FORM_GLOBAL_BOSS;?>').form('load','SettingsAction.php?method=getGlobalSettingsBoss');
    }

    loadGlobalSettings();
    loadGlobalSettingsDaily();
    loadGlobalSettingsBoss();
</script>