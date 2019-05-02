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
<fieldset>
    <legend>Daily Link</legend>
<form id="<?php echo FORM_DAILY_LINK;?>" method="post">
    <div class="fitem">
        <label for="date">Date</label>
        <input id="date" name="date" type="text" class="easyui-datebox" required="required"
        >
    </div>
    <div class="fitem">
        <label for="Link">Link</label>
        <input name="link" id="link" class="easyui-textbox" data-options="required:true" style="width:350px;"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo FORM_DAILY_LINK;?>', 'SettingsAction.php?method=saveDailyLink')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettingsDaily();" style="width:90px">Undo</a>
    </div>
</form>
    </fieldset>
<fieldset>
    <legend>Global</legend>
<form id="<?php echo FORM_GLOBAL;?>" method="post">
    <div title="Secret District is on/off" class="fitem">
        <label>Event</label>
        <input name="<?php echo GlobalSettingsTO::getBase()?>_eventEnabled"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div class="fitem">
        <label title="Number of Properties to rob">Nr. Properties</label>
        <input name="<?php echo GlobalRobbingSettingsTO::getBase()?>_nrOfProperties" id="<?php echo GlobalRobbingSettingsTO::getBase()?>_nrOfProperties" class="easyui-numberspinner" style="width:150px;"
               data-options="min:1,max:10"
        >
    </div>
    <div class="fitem">
        <label title="List of Property Names for robbing seperated by ;" for="robbing_properties">Properties</label>
        <input name="<?php echo GlobalRobbingSettingsTO::getBase()?>_properties" id="<?php echo GlobalRobbingSettingsTO::getBase()?>_properties" class="easyui-textbox" data-options="required:true" style="width:300px;"
        >
    </div>
    <div>
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo FORM_GLOBAL;?>', 'SettingsAction.php?method=saveGlobalSettings')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettings();" style="width:90px">Undo</a>
    </div>
</form>
</fieldset>

<fieldset>
    <legend>Boss</legend>
    <form id="<?php echo FORM_GLOBAL_BOSS;?>" method="post">
        <div title="Boss Name" class="fitem">
            <label>Boss Name</label>
            <input name="bossName" id="bossName" class="easyui-textbox" data-options="required:true" style="width:350px;">
        </div>
        <div>
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
               onclick="submitForm('<?php echo FORM_GLOBAL_BOSS;?>', 'SettingsAction.php?method=saveGlobalSettingsBoss')" style="width:90px">Save</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettingsBoss();" style="width:90px">Undo</a>
        </div>
    </form>
</fieldset>

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