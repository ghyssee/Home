<form action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "synchronizerStartDirectory",
        'size' => 100,
        'label' => 'Start Directory',
        'value' => $mp3SettingsObj->synchronizer->startDirectory)));
    $layout->checkBox(new Input(array('name' => "updateRating",
        'label' => 'Update Rating',
        'value' => $mp3SettingsObj->synchronizer->updateRating)));
    $layout->button(new Input(array('name' => "mp3Settings",
        'value' => 'saveiPod',
        'text' => 'Save',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>
