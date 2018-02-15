<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
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
<form id="globalSettingsDaily" method="post">
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
           onclick="submitForm('globalSettingsDaily', 'SettingsAction.php?method=saveDailyLink')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettingsDaily();" style="width:90px">Undo</a>
    </div>
</form>
    </fieldset>
<fieldset>
    <legend>Global</legend>
<form id="globalSettings" method="post">
    <div title="Secret District is on/off" class="fitem">
        <label>Event</label>
        <input name="eventEnabled"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div>
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('globalSettings', 'SettingsAction.php?method=saveGlobalSettings')" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettings();" style="width:90px">Undo</a>
    </div>
</form>
</fieldset>

<fieldset>
    <legend>Boss</legend>
    <form id="globalBossSettings" method="post">
        <div title="Boss Name" class="fitem">
            <label>Boss Name</label>
            <input name="bossName" id="bossName" class="easyui-textbox" data-options="required:true" style="width:350px;">
        </div>
        <div>
        </div>
    </form>
</fieldset>

<script>
    function loadGlobalSettingsDaily(){
        $('#globalSettingsDaily').form('load','SettingsAction.php?method=getDailyLink');
    }

    loadGlobalSettingsDaily();

    function loadGlobalSettings(){
        $('#globalSettings').form('load','SettingsAction.php?method=getGlobalSettings');
    }
    loadGlobalSettings();


</script>