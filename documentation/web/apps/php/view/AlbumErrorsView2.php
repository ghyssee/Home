<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
session_start();
?>


<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body style="background">

<style>
	.datagrid-cell{
		font-size:12px;
	}
	.datagrid-header .datagrid-cell span {
		font-size: 12px;
	}
	.datagrid-row{
		height: 18px;
	}
    
     #column1 {
         float: left;
         width: 20%;
     }
    #column2 {
        float: right ;
        width: 80% ;
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

<?php
goMenu();
?>

<h1>Mezzmo / MP3 Check</h1>
<div class="horizontalLine">.</div>
<br>


<div id="container">
    <div id="column1">
        <div id="innercolumn1">
            <?php include "AlbumErrorsViewTab1.php"; ?>
        </div>
    </div>
    <div id="column2">
        <div id="innercolumn2">
            <?php include "AlbumErrorsViewTab2.php"; ?>
        </div>
    </div>
</div>

<br>
<?php
goMenu();
?>
<br/>

</body>
</html>

