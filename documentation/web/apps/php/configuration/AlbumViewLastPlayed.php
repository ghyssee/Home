<form action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "number",
        'size' => 5,
        'label' => 'Number',
        'type' => 'number',
        'min' => '1',
        'max' => '100',
        'value' => $mp3SettingsObj->lastPlayedSong->number)));
    $layout->comboBox($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollColor",
            'label' => 'Scroll Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollColor)));
    $layout->comboBox($htmlObj->colors, "code", "description",
        new Input(array('name' => "scrollBackgroundColor",
            'label' => 'Scroll Background Color',
            'default' => $mp3SettingsObj->lastPlayedSong->scrollBackgroundColor)));
    $layout->checkBox(new Input(array('name' => "scrollShowAlbum",
        'label' => 'Show Album',
        'value' => $mp3SettingsObj->lastPlayedSong->scrollShowAlbum)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveLastPlayed',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>