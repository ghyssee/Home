<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
?>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.edatagrid.js')?>"></script>
</head>

<body>

<?php
sessionStart();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();
?>
<h1>Test</h1>



<input id="cbArtist" class="easyui-combobox" name="cbArtist"
       data-options="valueField:'id',
                     width:200,
                     textField:'name',
                     url:'MP3PrettifierAction.php?method=listArtists'
                     ">
<div><button onclick="getCmbArtist()">Add</button></div>
<div style="margin-bottom:20px">
    <input id="oldSong" class="easyui-textbox" label="Old Song" labelPosition="top" style="width:80%;height:52px">
</div>
<div><button onclick="setValue()">Set</button></div>

<script>
    function setValue(){
        $("#oldSong").textbox('setValue', 'TTTTTTTTTTEEEEEEEEEESSSSSSTTTTTT');
    }
function getCmbArtist(cmbId) {
    cmbId = '#cbArtist';
    var _options = $(cmbId).combobox('options');
    var _data = $(cmbId).combobox('getData');
    var _value = $(cmbId).combobox('getValue');
    var _text = $(cmbId).combobox('getText');
    var _b = false;
    var row = {id:null, value:null};
    for (var i = 0; i < _data.length; i++) {
        if (_data[i][_options.valueField] == _value) {
            _b = true;
            row.id = _value;
            row.value = _text;
            //$(cmbId).combobox('clear');
            break;
        }
    }
    if (!_b) {
        $(cmbId).combobox('setValue', '');
        row = null;
    }
    return row;
}

</script>

</body>
</html>
