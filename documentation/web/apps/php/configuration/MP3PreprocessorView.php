<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/metro-blue/easyui.css">
        <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
        <link rel="stylesheet" type="text/css" href="../../css/form.css">
        <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
        <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
    </head>
</head>
<body>

<?php
include("../setup.php");
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .inlineTable {
        float: left;
    }

    th {
        text-align: left;
    }

</style>


<?php
goMenu();
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Splitter();
}
?>
<h1>MP3Preprocessor Settings</h1>
<h3>Splitters</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <?php
    $layout = new Layout(array('numCols' => 1));
    $layout->inputBox(new Input(array('name' => "splitterId",
        'size' => 30,
        'label' => 'Id',
        'value' => $splitter->id)));
    $layout->inputBox(new Input(array('name' => "pattern",
        'size' => 30,
        'label' => 'Pattern',
        'value' => $splitter->pattern)));
    $layout->button(new Input(array('name' => "mp3Preprocessor",
        'value' => 'addSplitter',
        'text' => 'Add',
        'colspan' => 2)));
    $layout->close();
    ?>
</form>
<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('title','Splitter');
$smarty->assign('item','Splitter Item');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = "MP3PreprocessorAction.php";
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['id']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl',"'" . $url . "?method=delete',{id:row['id']}");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "description", "label"=>"Description", "size" => 100, "sortable" => true),
        array("field" => "pattern", "label"=>"Pattern", "size" => 20)
    )
);
$smarty->display('TableGridV3.tpl');
?>


<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html> 

