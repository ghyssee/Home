<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");
session_start();
?>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="/catalog/apps/css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/css/form.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/themes/easyui/icon.css">
    <script type="text/javascript" src="/catalog/apps/js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/datagrid-dnd.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/datagrid-filter.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/easyUIUtils.js"></script>

</head>
<body style="background">
<h3>Test</h3>

<?php
    if (isset($_REQUEST["id"])) {
        $id = htmlspecialchars($_REQUEST['id']);
    }
    else {
        $id = 175435;
    }
    $songBO = new SongBO();
    try {
        $songObj = $songBO->testSong($id);
    }
    catch (Exception $e){
        echo $e->getMessage();
        exit(0);
    }
    print_r($songObj);
?>

</body>
</html>

