<form action="<?php echo $settingsSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "sessionTimeout",
        'size' => 5,
        'label' => 'Number',
        'type' => 'number',
        'min' => '1',
        'max' => '100',
        'value' => 30)));
    $layout->button(new Input(array('name' => "clearCache",
        'value' => 'clearCache',
        'text' => 'Clear Cache',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>