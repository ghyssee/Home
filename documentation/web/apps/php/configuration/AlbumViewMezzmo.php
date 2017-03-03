<form action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "mezzmoBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mezzmo->base)));
    $layout->inputBox(new Input(array('name' => "importBase",
        'size' => 100,
        'label' => 'Import Base',
        'value' => $mp3SettingsObj->mezzmo->importF->base)));
    $layout->inputBox(new Input(array('name' => "filename",
        'size' => 50,
        'label' => 'Import File',
        'value' => $mp3SettingsObj->mezzmo->importF->filename)));
    $layout->inputBox(new Input(array('name' => "exportBase",
        'size' => 100,
        'label' => 'Export Path',
        'value' => $mp3SettingsObj->mezzmo->export->base)));
    $layout->inputBox(new Input(array('name' => "exportiPod",
        'size' => 100,
        'label' => 'Export iPod Path',
        'value' => $mp3SettingsObj->mezzmo->export->iPod)));
    $layout->checkBox(new Input(array('name' => "synchronizePlaycount",
        'label' => 'Synchronize Play Count',
        'value' => $mp3SettingsObj->mezzmo->synchronizePlaycount)));
    $layout->comboBox($mp3SettingsObj->mezzmo->mp3Checker->relativePaths, "id", "description",
        new Input(array('name' => "currentRelativePath",
            'label' => 'Current Relative Path',
            'col' => 1,
            'default' => $mp3SettingsObj->mezzmo->mp3Checker->currentRelativePath)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveMezzmo',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>