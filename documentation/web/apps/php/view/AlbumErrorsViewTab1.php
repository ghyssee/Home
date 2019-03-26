<?php
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$htmlObj = readJSONWithCode(JSON_ALBUMERRORS);
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);

function getLatestVersion(){
    $tmpBO =SongBO::db(MEZZMO);
    $version = $tmpBO->findLatestVersion();
    return $version;
}

?>
<form action="AlbumErrorsAction.php?method=updateSettings" method="post">
    <?php
    $version = getLatestVersion();
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "dbName",
        'size' => 100,
        'label' => 'DB Name',
        'type' => 'text',
        'disabled' => true,
        'value' => $version->dbName)));
    $layout->inputBox(new Input(array('name' => "version",
        'size' => 5,
        'label' => 'Version',
        'type' => 'text',
        'disabled' => true,
        'value' => $version->version)));
    $layout->inputBox(new Input(array('name' => "lastUpdated",
        'size' => 30,
        'label' => 'Version Time',
        'type' => 'text',
        'disabled' => true,
        'value' => $version->lastUpdated)));
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


