<?php
$file = getFullPath(FILE_ALBUM);
$textValue = '';
if (file_exists($file)){
    $textValue = read($file);
}

$layout = new Layout(array('numCols' => 1));
$layout->textArea(new Input(array('name' => "albumContent",
    'col' => 1,
    'cols' => 80,
    'rows' => 40,
    'label' => '',
    'value' => $textValue)));

$layout->close();
?>
