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
</form>
