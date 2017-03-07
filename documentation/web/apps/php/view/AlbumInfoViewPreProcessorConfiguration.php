<?php
$layout = new Layout(array('numCols' => 1));
$layout->inputBox(new Input(array('name' => "cdTag",
    'size' => 50,
    'col' => 1,
    'label' => 'CdTag',
    'value' => $mp3PreprocessorObj->cdTag)));
$layout->inputBox(new Input(array('name' => "prefix",
    'size' => 50,
    'col' => 1,
    'label' => 'Prefix',
    'value' => $mp3PreprocessorObj->prefix)));
$layout->inputBox(new Input(array('name' => "suffix",
    'size' => 50,
    'col' => 1,
    'label' => 'Suffix',
    'value' => $mp3PreprocessorObj->suffix)));
$layout->comboBox($mp3PreprocessorObj->configurations, "id", "id",
    new Input(array('name' => "activeConfiguration",
        'label' => 'Active Configuration',
        'col' => 1,
        'method' => 'getConfigurationText',
        'methodArg' => 'config',
        'default' => $mp3PreprocessorObj->activeConfiguration)));
$layout->inputBox(new Input(array('name' => "album",
    'size' => 80,
    'col' => 1,
    'label' => 'Album',
    'value' => $mp3PreprocessorObj->album)));
$layout->checkBox(new Input(array('name' => "renum",
    'label' => 'Renum Tracks',
    'value' => $mp3PreprocessorObj->renum)));
$layout->button(new Input(array('name' => "albumInfo",
    'col' => 1,
    'value' => 'save',
    'text' => 'Save',
    'colspan' => 2)));
$layout->close();

?>

<?php
function getConfigurationText($array)
{
    $desc = "";
    foreach ($array as $key => $config) {
        if (isset($config->type)) {
            $desc = $desc . (empty($desc) ? '' : ' ') . $config->type;
        }
        if (isset($config->splitter)) {
            $desc = $desc . " " . $config->splitter;
        }
        if (!empty($config->duration)) {
            $desc = $desc . " (duration TRUE)";
        }
    }
    return $desc;
}
?>
