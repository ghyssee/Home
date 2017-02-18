<form action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "album",
        'size' => 100,
        'label' => 'Album',
        'value' => $mp3SettingsObj->album)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveAlbum',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>

</form>