<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
$FORM_ID = "settingsGlobal";
?>

<form id="<?php echo $FORM_ID;?>" method="post">
    <div title="Stop script when experience is below this value (0 = disabled)" class="fitem">
        <label>Stop When Experience Below</label>
        <input name="stopWhenExpBelow" id="stopWhenExpBelow" class="easyui-numberspinner" style="width:150px;"
               data-options="min:0"
        >
    </div>
    <div title="Stop script when stamina is below this value (0 = disabled)" class="fitem">
        <label>Stop When Stamina Below</label>
        <input name="stopWhenStaminaBelow" id="stopWhenStaminaBelow" class="easyui-numberspinner" style="width:150px;"
               data-options="min:0"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitForm('<?php echo $FORM_ID;?>', 'SettingsAction.php?method=saveSettingsGlobal&profile=' + getProfile())" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadSettingsGlobal();" style="width:90px">Undo</a>
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
    function loadSettingsGlobal(){

        $('#<?php echo $FORM_ID;?>').form('load','SettingsAction.php?method=getSettingsGlobal&profile=' + getProfile());
    }
    loadSettingsGlobal();
</script>