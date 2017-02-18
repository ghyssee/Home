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
         width: 8%;
     }
    #column2 {
        float: right ;
        width: 92% ;
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

<h3>Mezzmo / MP3 Check</h3>

<div id="tt" class="easyui-tabs" data-options="selected:0" style="width:1400px;height:700px;">
    <div title="Settings" style="padding:20px;display:none;">
        <?php include "AlbumErrorsViewTab1.php"; ?>
    </div>
    <div title="Errors" style="overflow:auto;padding:20px;display:none;" >
        <div id="container">
            <div id="column1">
                <div id="innercolumn1">
                    <form onsubmit="return myFunction()">
                        <input type="submit" value="Process">
                    </form>
                </div>
            </div>
            <div id="column2">
                <div id="innercolumn2">
                    <?php include "AlbumErrorsViewTab2.php"; ?>
                </div>
            </div>
        </div>

    </div>
</div>

<script src="<?php echo webPath(ROOT_JS_EASYUI, 'EasyUITabsMouseHover.js')?>"></script>



<br>
<?php
goMenu();
?>
<br/>

</body>
</html>

