<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
$FORM_ID = "settingsBoss";
?>

<form id="<?php echo $FORM_ID;?>" method="post">
    <div class="fitem">
        <label>Bossfight</label>
        <input name="active"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
    <div title="Stop fighting boss when boss health is below this value" class="fitem">
        <label>Stop When Health Below</label>
        <input name="stopWhenHealthBelow" id="stopWhenHealthBelow" class="easyui-numberspinner" style="width:150px;"
               data-options="min:0"
        >
    </div>
    <div class="fitem">
        <label>Boss Name</label>
        <input name="name" id="name" class="easyui-textbox" style="width:150px;"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsBoss&profile=' + getProfile())" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsBoss();" style="width:90px">Undo</a>
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
    function loadSettingsBoss(profile){
        if (profile){
            // do nothing
        }
        else {
            profile = getProfile();
        }
        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsBoss&profile=' + profile);
    }
    loadSettingsBoss();
</script>