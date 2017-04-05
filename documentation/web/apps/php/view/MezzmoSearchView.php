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

$mp3Settings = readJSONWithCode(JSON_MP3SETTINGS);
$file = getFullPath(PATH_CONFIG) . '/ArtistIds.txt';
$lines = file($file, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
$artistArray = array();
foreach($lines as $line) {
    $songTO = new SongTO();
    $items = explode("\t", $line);
    if (count($items) > 1) {
        $songTO->artistId = $items[0];
        $songTO->artist = $items[1];
        $artistArray[] = $songTO;
    }
}

if (isset($_POST['submit'])) {
    $data = array();
    $data[] = getObject("15", "Test15");
    $data[] = getObject("16", "Test16");
    $song = new SongTO();
    assignNumber($artistId, "artistId2");
    assignNumber($song->artistId, "artistId");
    if (empty($song->artistId)){
        $song->artistId = $artistId;
    }
    assignNumber($song->fileId, "fileId");
    assignField($song->artistName, "artistName");
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artistName)){
        echo "all empty";
    }
    else {
        $songBO = new SongBO();
        $mp3Settings->mezzmo->artistId = $song->artistId;
        writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
        $data = $songBO->searchSong($song);
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
        'label' => 'Artist Id',
        'type' => 'number',
        'value' => '0')));
    $layout->inputBox(new Input(array('name' => "fileId",
        'size' => 5,
        'label' => 'File Id',
        'type' => 'number',
        'value' => '0')));
    $layout->inputBox(new Input(array('name' => "artistName",
        'size' => 100,
        'label' => 'Artist Name')));
    $layout->comboBox($artistArray, "artistId", "artist",
        new Input(array('name' => "artistId2",
            'label' => 'List Artists',
            'default' => $mp3Settings->mezzmo->artistId)));

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
    <script>
        function editLink(val,row){
            var url = "<?php echo getSongViewLink(); ?>" + '?id=' + row.fileId;
            return '<a href="'+url+'" target="_blank">Edit</a>';
            //return "edit";
        }
        function formatPrice(val,row){
            var text = row.artist + ' ' + row.title;
            var url = "https://www.google.be/search?q=" +encodeURIComponent(text);
            return '<a href="'+url+'" target="_blank">'+row.artist + ' - ' + row.title+'</a>';
        }
        function formatArtist(val,row){
            var url = "https://www.google.be/search?q=" + encodeURIComponent(row.artist);
            return '<a href="'+url+'" target="_blank">'+val+'</a>';
        }
    </script>

    <table id="dgSearch" class="easyui-datagrid" style="width:100%;height:300px"
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
            <th data-options="field:'editLink',width:15, formatter: editLink">Edit</th>
            <th data-options="field:'artistId',width:15">ArtistId</th>
            <th data-options="field:'artist',width:100, formatter: formatArtist">Artist</th>
            <th data-options="field:'title',width:100">Title</th>
            <th data-options="field:'Song',width:100, formatter: formatPrice">Song</th>
            <th data-options="field:'album',width:50">Album</th>
            <th data-options="field:'fileId',width:15">File</th>
            <th data-options="field:'file',width:200">File</th>
        </tr>
        </thead>
    </table>

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

function getSongViewLink(){
    $document = webPath(ROOT_PHP_VIEW, 'MezzmoSongView.php');
    return $document;
}

?>
</form>
</body>
</html>

