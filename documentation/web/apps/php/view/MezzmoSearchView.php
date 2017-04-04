<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
    <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
</head>
<body>

<?php
session_start();
include_once("../setup.php");
include_once("../config.php");
include_once("../model/HTML.php");
include_once("../html/config.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
$albumSave = 'MezzmoSearchview.php';

if (isset($_POST['submit'])) {
    $data = array();
    $data[] = getObject("15", "Test15");
    $data[] = getObject("16", "Test16");
    $song = new SongTO();
    assignNumber($song->artistId, "artistId");
    assignNumber($song->fileId, "fileId");
    assignField($song->artistName, "artistName");
    if (empty($song->artistId && empty($song->fileId) && empty($song->artistName))){
        echo "all empty";
    }
}
else {
    $data = array();
}

$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();
?>

<h1>Search Song</h1>
<div class="horizontalLine">.</div>

<form id="myFormId" action="<?php echo $albumSave ?>" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "artistId",
        'size' => 5,
        'label' => 'Number',
        'type' => 'number',
        'value' => '0')));
    $layout->inputBox(new Input(array('name' => "fileId",
        'size' => 5,
        'label' => 'Number',
        'type' => 'number',
        'value' => '0')));
    $layout->inputBox(new Input(array('name' => "artistName",
        'size' => 100,
        'label' => 'Artist Name')));

    $layout->button(new Input(array('name' => "submit",
        'value' => 'search',
        'text' => 'Search',
        'colspan' => 2)));
    $layout->close();
    ?>
    <script>
        var data = <?php echo json_encode($data) ?>;
        var data2 = [{"id":10, "name":'Test'},
                    {"id":11, "name":'Test2'}
            ];

        function doIt(){
            alert("done");
            $('#myFormId').submit();
        }
    </script>
    <button type="button" onclick="doIt();">Do It</button>
    <table id="dgSearch" class="easyui-datagrid" style="width:400px;height:500px"
           title="Result"

           data-options='fitColumns:true,
                         singleSelect:true,
                         data:data
                         '
           pagination="false"
           nowrap = "false"
           rownumbers="true"
           pageSize="10"
    >


        <thead>
        <tr>
            <th data-options="field:'id',hidden:true">Id</th>
            <th data-options="field:'name',width:100">Artist</th>
        </tr>
        </thead>
    </table>

    <?php
    $array = array();
    $layout = new Layout(array('numCols' => 1));
    $layout->displayTable($data, new Input(array(
        'title' => 'Songs',
        'id' => 'id'
    )));
    $layout->close();
    ?>


</form>

<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);

function getObject($id, $name){
    $object = new stdClass();
    $object->id = $id;
    $object->name = $name;
    return $object;
}

?>
</form>
</body>
</html>

