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

<div id="dlArtistList" class="easyui-datalist" title="Artists" style="width:200px;height:400px"
     data-options="
                            url: 'MP3PrettifierAction.php?method=listArtists',
                            method: 'get',
                            lines: 'true',
                            valueField: 'id',
                            singleSelect: true,
                            required:true,
                            prompt:'Select Type',
                            textField: 'name'
                         "
>
</div>

<input id="cbArtist" class="easyui-combobox" name="cbArtist"
       data-options="valueField:'id',
                     width:200,
                     textField:'name',
                     url:'MP3PrettifierAction.php?method=listArtists'
                     ">
<div><button onclick="insert()">Add</button></div>

<script>
function insert() {
    var row = $('#cbArtist').combobox('getValue');
    var _options = $('#cbArtist').combobox('options');
    var _data = $(cbArtist).combobox('getData');
    var _value = $(cbArtist).combobox('getValue');
    var _b = false;
    for (var i = 0; i < _data.length; i++) {
        if (_data[i][_options.valueField] == _value) {
            _b = true;
            break;
        }
    }
    if (!_b) {
        $(cbArtist).combobox('setValue', '');
    }
}

</script>

</body>
</html>
