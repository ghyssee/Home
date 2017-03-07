<?php
$layout = new Layout(array('numCols' => 1));
$layout->inputBox(new Input(array('name' => "albumLocation",
    'size' => 80,
    'col' => 1,
    'label' => 'Album Location',
    'value' => $mp3SettingsObj->album)));
$layout->inputBox(new Input(array('name' => "albumArtist",
    'size' => 50,
    'col' => 1,
    'label' => 'Album Artist',
    'value' => $mp3SettingsObj->albumArtist)));
$layout->inputBox(new Input(array('name' => "albumYear",
    'size' => 4,
    'col' => 1,
    'label' => 'Album Year',
    'type' => 'number',
    'min' => '1900',
    'value' => $mp3SettingsObj->albumYear)));
$layout->checkBox(new Input(array('name' => "filenameRenameEnabled",
    'label' => 'Rename file',
    'value' => $mp3SettingsObj->filename->renameEnabled)));
$layout->close();
?>