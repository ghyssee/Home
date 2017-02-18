<form action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyBase",
        'size' => 100,
        'label' => 'Base',
        'value' => $mp3SettingsObj->mediaMonkey->base)));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyPlaylistPath",
        'size' => 100,
        'label' => 'Playlist Path',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->path)));
    $layout->inputBox(new Input(array('name' => "mediaMonkeyTop20",
        'size' => 100,
        'label' => 'Top 20 Name',
        'value' => $mp3SettingsObj->mediaMonkey->playlist->top20)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveMM',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>