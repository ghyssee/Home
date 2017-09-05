<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
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
sessionStart();
goMenu();
?>
<h1>Artist / Song Relationship</h1>
<div class="horizontalLine">.</div>
<br>

<div class="easyui-layout" style="width:100%;height:100%;">

    <div data-options="region:'west',collapsible:false" title="Lines" style="width:50%;height:100%">


        <?php
        include_once('Smarty.class.php');

        $fieldId = "id";
        $smarty = initializeSmarty();
        $smarty->assign('title','Mulit Artist List');
        $smarty->assign('item','Artist');
        $smarty->assign('tableWidth','100%');
        $smarty->assign('tableHeight','100%');
        //$url = "MP3ArtistSongRelationshipAction.php";
        $url = "MP3ArtistSongRelationshipAction.php";
        $smarty->assign('tablegrid',"MultiArtistList");
        $smarty->assign('id',$fieldId);
        $smarty->assign('fitColumns',"true");
        $smarty->assign('pageSize',"40");
        $smarty->assign('viewUrl',$url . "?method=listMultiArtists");
        $smarty->assign('updateUrl', "'" . $url . "?method=updateMultiArtist&id='+row['" . $fieldId . "']");
        $smarty->assign('deleteUrl', $url . "?method=deleteMultiArtist");
        //$smarty->assign('deleteUrl', $url . "?method=delete");


        $smarty->assign("contacts", array(
        array("field" => "id", "label"=>"Id", "size" => 40, "editable" => false),
        array("field" => "oldArtist", "label"=>"Old Artist", "size" => 40, "editable" => false, "sortable" => true),
        array("field" => "newArtist", "label"=>"New Artist", "size" => 40, "editable" => false, "sortable" => true),
        array("field" => "oldSong", "label"=>"Old Song", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "newSong", "label"=>"New Song", "size" => 40, "required" => true, "sortable" => true),
        array("field" => "exact", "label"=>"Exact", "size" => 15, "formatter" => "checkboxFormatter",
        "align" => "center", "checkbox" => true)
        )
        );
        //** un-comment the following line to show the debug console
        //$smarty->debugging = true;

        $smarty->display('TableGridV4.tpl');
        ?>

    </div>
    <div data-options="region:'east',collapsible:false" title="Result" style="width:50%;height:100%">

        <button onclick="refreshMultiArtist()">Refresh</button>

    </div>

        <script>
            function refreshMultiArtist(){
                $('#dgMultiArtistList').datagrid('reload');
            }
            $(".datagrid-header-rownumber").click(function(){
                alert("clicked");
            });
            var div = document.getElementsByClassName('datagrid-header-rownumber')[0];

            div.addEventListener('click', function (event) {
                alert('Hi!');
            });

        </script>

</div>


</body>
</html>

