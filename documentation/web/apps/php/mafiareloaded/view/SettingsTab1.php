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
        <label for="minLengthOfFightList">Min. Length Of Fight List</label>
        <input name="total" id="total" class="easyui-numberspinner" style="width:150px;"
               data-options="min:1"
        >
    <div>
    <div class="fitem">
        <label>AutoHeal</label>
        <input name="autoHeal"
               class="easyui-checkbox" type="checkbox" value="true"
        >
    </div>
        <div id="dlg-buttonsScheduledJob">
            <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
               onclick="submitSet()" style="width:90px">Save</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlgScheduledJob').dialog('close')" style="width:90px">Cancel</a>
        </div>
</form>

<script>

function submitSet() {
    $('#settings').form('submit', {
        url: "SettingsAction.php?method=set1",
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