<form action="MP3PrettifierTestAction.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "artist",
        'size' => 100,
        'label' => 'Artist'
    )));
    $layout->inputBox(new Input(array('name' => "song",
        'size' => 100,
        'label' => 'Song'
    )));
    $layout->button(new Input(array('name' => "settings",
        'value' => 'add',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>