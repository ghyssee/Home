<?php
session_start();
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
include_once documentPath (ROOT_PHP_BO, "EricBO.php");
?>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <link rel="stylesheet" type="text/css" href="../../css/form.css">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body>
<?php
$albumSave = 'MezzmoSearchview.php';

$mp3Settings = readJSONWithCode(JSON_MP3SETTINGS);
$songBO =SongBO::db(MEZZMOV3);

$artistArray = $songBO->loadArtistIdsFile();

if (isset($_POST['submit'])) {
    $data = array();
    $song = new SongTO();
    assignNumber($artistId, "cmbArtistId");
    assignNumber($song->artistId, "artistId");
    assignNumber($song->fileId, "fileId");
    assignField($song->artist, "artistName");
    $listboxArtistId = false;
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        $song->artistId = $artistId;
        $listboxArtistId = true;
        writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
    }
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        echo "all empty";
    }
    else {
        if ($listboxArtistId) {
            $mp3Settings->mezzmo->artistId = $song->artistId;
            writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
            $mp3Settings->mezzmo->artistId = getNextArtistId($artistArray, $mp3Settings->mezzmo->artistId);
        }
        $data = $songBO->searchSong($song);
        $songBO->saveArtistSongTest($data);
    }
}
else {
    $data = array();
}

$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();

function getLatestVersion(){
    $tmpBO =SongBO::db(MEZZMO);
    $version = $tmpBO->findLatestVersion();
    return $version;
}

function getNextArtistId($artistArray, $artistId){
    $next = false;
    foreach($artistArray as $song) {
        if ($next){
            $artistId = $song->artistId;
            break;
        }
        if ($song->artistId == $artistId) {
            $next = true;;
        }
    }
    return $artistId;

    function showVersion(){
        $version = getLatestVersion();
        $txt = '';
        if ($version != null){
            $txt = "(Version: " . $version->version . ", " . $version->lastUpdated . ")";
        }
        return $txt;
    }

}

?>

<h1>Search Song<?php
    $version = getLatestVersion();
    $txt = '';
    if ($version != null){
        $txt = " (DB Version: " . $version->version . ", " . $version->lastUpdated . ")";
    }
    echo $txt;
    ?>
</h1>
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
        new Input(array('name' => "cmbArtistId",
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
    <style type="text/css">
        .box {
            width: 75px;
            display: inline-block;
        }
        .box2 {
            display: inline-block;
        }
        .datagrid-row-selected {
            background: #ffe48d;
            color: #000000;
        }    </style>
    <script>
        function editSong(){
            var row = $('#dgSearch').datagrid('getSelected');
            if (row) {
                var url = "<?php echo getSongViewLink(); ?>" + '?id='+row.fileId;
                var win = window.open(url, '_blank');
                win.focus();
            }
        }
        function editLink(val,row){
            var url = "<?php echo getSongViewLink(); ?>" + '?id=' + row.fileId;
            return '<a href="'+url+'" target="_blank">Edit</a>';
            //return "edit";
        }
        function addRule(){
            var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'ArtistSongRelationshipView.php') ?>";
            var row = $('#dgSearch').datagrid('getSelected');
            var artist = row.newArtist == null ? row.artist : row.newArtist;
            if (row) {
                url += "?artist=" + encodeURIComponent(artist) + "&song=" + encodeURIComponent(row.title);
            }
            openUrl(url);
        }
        function editRule(){
            var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'ArtistSongRelationshipView.php') ?>";
            var row = $('#dgSearch').datagrid('getSelected');
            if (row && row.status == "AS_RELATION") {
                url += "?id=" + row.ruleId;
                openUrl(url);
            }
            else {
                alert("No row selected or No rule Defined");
            }
        }
        function addOrModifyMultiArtist(){
            var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'MultiArtistView.php') ?>";
            var row = $('#dgSearch').datagrid('getSelected');
            if (row){
                if (row.status == "MULTIARTIST") {
                    url += "?id=" + row.ruleId;
                }
                else {
                    url += "?artist=" + encodeURIComponent(row.artist);
                }
                openUrl(url);
            }
            else {
                alert("No row selected");
            }
        }
        function formatPrice(val,row){
            var text = row.artist + ' ' + row.title;
            var url = "https://www.google.be/search?q=" +encodeURIComponent(text);
            return '<a href="'+url+'" target="_blank">'+row.artist + ' - ' + row.title+'</a>';
        }
        function formatArtist(val,row){
            var url = "https://www.google.be/search?q=" + encodeURIComponent(row.artist);
            //return '<div class="box">Old Artist:</div><div class="box2"><a href="'+url+'" target="_blank">'+val+'</a></div><br><div class="box">New Artist:</div><div class="box2">BlaBla</div>';
            var cellInfo = '<a href="'+url+'" target="_blank">'+val+'</a>';
            if (row.newArtist != null && row.artist != row.newArtist){
                cellInfo = '<div><span class="box">Old Artist:</span><span class="box2">' + cellInfo +
                    '</span></div><div><span class="box">New Artist:</span><span class="box2">' + row.newArtist + '</span></div>';
            }
            return cellInfo;
            //return '<div><span class="box">Old Artist:</span><span class="box2">' + cellInfo +
              //     '</span></div><div><span class="box">New Artist:</span><span class="box2">' + row.newArtist + '</span></div>';
            //return '<div><span class="box">Old Artist:</span><span class="box2"><a href="'+url+'" target="_blank">'+val+'</a></span></div><div><span class="box">New Artist:</span><span class="box2">BlaBla</span></div>';
        }
        function formatTitle(val,row){
            var cellInfo = row.title;
            if (row.newTitle != null && row.title != row.newtitle){
                cellInfo = '<div><span class="box">Old Title:</span><span class="box2">' + cellInfo +
                    '</span></div><div><span class="box">New Title:</span><span class="box2">' + row.newTitle + '</span></div>';
            }
            return cellInfo;
            //return '<div><span class="box">Old Artist:</span><span class="box2">' + cellInfo +
            //     '</span></div><div><span class="box">New Artist:</span><span class="box2">' + row.newArtist + '</span></div>';
            //return '<div><span class="box">Old Artist:</span><span class="box2"><a href="'+url+'" target="_blank">'+val+'</a></span></div><div><span class="box">New Artist:</span><span class="box2">BlaBla</span></div>';
        }


    </script>

    <table id="dgSearch" class="easyui-datagrid" style="width:100%;height:300px"
           title="Result"

           data-options='fitColumns:true,
                         singleSelect:true,
                         rowStyler: function(index,row){ return formatRow(index,row) },
                         toolbar:"#toolbarArtistWord",
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
            <th data-options="field:'artist',width:150, formatter: formatArtist">Artist</th>
            <th data-options="field:'title',width:100">Title</th>
            <th data-options="field:'song',width:100, formatter: formatPrice">Song</th>
            <th data-options="field:'duration',width:25">Duration</th>
            <th data-options="field:'album',width:50">Album</th>
            <th data-options="field:'fileId',width:15">File</th>
            <th data-options="field:'file',width:200">File</th>
            <th data-options="field:'isNew',width:15,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">New</th>
            <th data-options="field:'status',width:20">Status</th>
        </tr>
        </thead>
    </table>

    <span style="font-size:20px">
        <div id="toolbarArtistWord">
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addRule()">Add Rule</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRule()">Edit Rule</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="addOrModifyMultiArtist()">MultiArtist</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editSong()">Edit Song</a>
        </div>
	</span>


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

<script>
    function checkboxFormatter(val,row,index){

        if (val== 1) {
            return "√";
        }
        else {
            return "";
        }
    }

    function formatRow(index, row) {
        if (row.isNew){
            // red background
            return 'background-color:#e60000;color:#000;'
        }
        if (row.status) {
            switch (row.status){
                case "AS_RELATION":
                    // light green
                    return 'background-color:#ccffd9;color:#000;';
                    break;
                case "AS_EXCEPTION":
                    // light green
                    return 'background-color:#ccffd9;color:#000;';
                    break;
            }
        }
        return '';
    }


</script>
