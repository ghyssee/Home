<form action="MP3PrettifierTestAction.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "number",
        'size' => 5,
        'label' => 'Items To Flush',
        'type' => 'number',
        'min' => '1',
        'max' => '1000',
        'value' => 50)));
    $layout->button(new Input(array('name' => "settings",
        'value' => 'flush',
        'text' => 'Flush',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>