<?php
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
    include_once "../../setup.php";
    include_once documentPath (ROOT_PHP, "config.php");
    include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
    include_once documentPath (ROOT_PHP_HTML, "config.php");
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
    <div title="Artist Groups List" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewMultiArtistList.php"; ?>
    </div>
    <div title="Artist Group" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewMultiArtistConfig.php"; ?>
    </div>
    <div title="Artist Group Batch" style="padding:20px;display:none;">
        <?php include "MP3PrettifierViewMultiArtistBatch.php"; ?>
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
