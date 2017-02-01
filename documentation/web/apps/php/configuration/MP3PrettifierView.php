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
include("../config.php");
include("../html/config.php");
include("../model/HTML.php");
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
goMenu();
?>
<h1>MP3 Pretttifier</h1>
<div class="horizontalLine">.</div>
<br>
<div id="container">
    <div id="column1">
        <div id="innercolumn1">
            <?php include "MP3PrettifierViewGlobalWord.php"; ?>
        </div>
    </div>
    <div id="column2">
        <div id="innercolumn2">
            <?php include "MP3PrettifierViewArtistWord.php"; ?>
        </div>
    </div>
</div>
<div id="container">
    <div id="column1">
        <div id="innercolumn1">
            <?php include "MP3PrettifierViewArtistName.php"; ?>
        </div>
    </div>
    <div id="column2">
        <div id="innercolumn2">
            <?php include "MP3PrettifierViewSongTitle.php"; ?>
        </div>
    </div>
</div>
<br>


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

