<html>
<style>
    #column1 {
        float: left;
        width: 50%;
    }
    #column2 {
        float: right ;
        width: 50% ;
    }
    #innercolumn1 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
    #innercolumn2 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
</style>

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
error_reporting(E_ALL);
ini_set('display_errors', 1);
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();
?>
<h1>MP3 Pretttifier</h1>
<div class="horizontalLine">.</div>
<br>

<div id="tt" class="easyui-tabs" data-options="selected:0" style="width:900px;height:500px;">
    <div title="Global Word" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewGlobalWord.php"; ?>
    </div>
    <div title="Artist Word" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewArtistWord.php"; ?>
    </div>
    <div title="Artist Name" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewArtistName.php"; ?>
    </div>
    <div title="Song Title" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewSongTitle.php"; ?>
    </div>
    <div title="Filename" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewFilename.php"; ?>
    </div>
    <div title="Artists" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewArtists.php"; ?>
    </div>
    <div title="Multi Artist" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewMultiArtistList.php"; ?>
    </div>
    <div title="Multi Artist Config" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewMultiArtistConfig.php"; ?>
    </div>
</div>

<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>


<?php
goMenu();

function constructUrl($url, $method, $type, $category){
    $newUrl = $url . "?method=" . $method . "&type=" . $type . "&category=" . $category;
    return $newUrl;
}

?>
<br>

</body>
</html>

