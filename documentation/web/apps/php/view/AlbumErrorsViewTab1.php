<?php
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$htmlObj = readJSONWithCode(JSON_ALBUMERRORS);
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);
?>
<form action="AlbumErrorsAction.php?method=updateSettings" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->comboBox($mp3SettingsObj->mezzmo->mp3Checker->statuses, "code", "description",
        new Input(array('name' => "albumErrorsStatus",
            'label' => 'Status',
            'default' => $mp3SettingsObj->mezzmo->mp3Checker->status)));

    $layout->inputBox(new Input(array('name' => "maxNumberOfErrors",
        'size' => 5,
        'label' => 'Max Number Of Errors',
        'type' => 'number',
        'min' => '1',
        'max' => '10000',
        'value' => $mp3SettingsObj->mezzmo->mp3Checker->maxNumberOfErrors)));
    $layout->button(new Input(array('name' => "mp3Check",
        'col' => 1,
        'value' => 'save',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>

<form onsubmit="return myFunction()">
    <input type="submit" value="Process Selected Rows">
</form>

<div style="margin:10px 0;">
    <span>Selection Mode: </span>
    <select onchange="$('#dg').datagrid({singleSelect:(this.value==0)})">
        <option value="0">Single Row</option>
        <option value="1" selected>Multiple Rows</option>
    </select>
    <br/>
    SelectOnCheck: <input type="checkbox" checked onchange="$('#dg').datagrid({selectOnCheck:$(this).is(':checked')})">
    <br/>
    CheckOnSelect: <input type="checkbox" checked onchange="$('#dg').datagrid({checkOnSelect:$(this).is(':checked')})">
</div>


