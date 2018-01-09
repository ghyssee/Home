<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 12/12/2017
 * Time: 15:32
 */
?>

<form id="settings" method="post">
    <div class="fitem">
        <label for="date">Date</label>
        <input name="date" id="date" class="easyui-textbox" style="width:150px;"
        >
    </div>
    <div class="fitem">
        <label for="Link">Link</label>
        <input name="date" id="link" class="easyui-textbox" style="width:150px;"
        >
    </div>
    <div id="dlg-buttonsScheduledJob">
        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
           onclick="submitSet()" style="width:90px">Save</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgScheduledJob').dialog('close')" style="width:90px">Cancel</a>
    </div>
</form>

<script>
    $('#settings').form('load','SettingsAction.php?method=getSettings1');
    function submitSet() {
        $('#settings').form('submit', {
            url: "SettingsAction.php?method=saveDailyLink",
            onSubmit: function () {
// do some check
// return false to prevent submit;
            },
            success: function (data) {
                alert(data)
            }
        });
    }
</script>