<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 29/01/2018
 * Time: 12:51
 */

chdir("..");
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_DATABASE, "MezzmoDatabase.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
include_once documentPath (ROOT_PHP_MR_BO, "HomeFeedBO.php");
include_once documentPath (ROOT_PHP_COMMON, "DateUtils.php");
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
<h3>Cleanup Homefeed</h3>

<?php
$date = DateTime::createFromFormat('YmdHis', '20171122201621');
print_r($date->format('Y-m-d H:i:s'));
$date = $date->sub(new DateInterval("P31D"));
print_r($date->format('Y-m-d H:i:s'));
$homefeedBO = new HomeFeedBO("01");
$homefeedBO->cleanupHomefeed();

?>

</body>
</html>
