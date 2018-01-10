<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
?>

<form id="settings2" method="post">
    <div class="fitem">
        <label for="date">Date</label>
        <input name="date" id="date" class="easyui-textbox" data-options="required:true" style="width:150px;"
        >
    </div>
    <div class="fitem">
        <label for="Link">Link</label>
        <input name="link" id="link" class="easyui-textbox" data-options="required:true" style="width:350px;"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitSet1()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="loadGlobalSettingsDaily();" style="width:90px">Undo</a>
    </div>
</form>

<script>
    function loadGlobalSettingsDaily(){
        $('#settings2').form('load','SettingsAction.php?method=getDailyLink');
    }

    loadGlobalSettingsDaily();

    function submitSet1() {
        $('#settings2').form('submit', {
            url: "SettingsAction.php?method=saveDailyLink",
            onSubmit: function () {
// do some check
// return false to prevent submit;
            },
            success: function (data) {
                var obj = JSON.parse(data);
                alert(obj.message);
            }
        });
    }
</script>