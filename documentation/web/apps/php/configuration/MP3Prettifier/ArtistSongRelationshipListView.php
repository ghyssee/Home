<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
chdir("..");
include_once "../setup.php";
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
?>
<?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>
<h1>Artist / Song Relationship</h1>
<div class="horizontalLine">.</div>
<br>

<div class="easyui-layout" style="width:100%;height:720px;">

    <div data-options="region:'north',collapsible:false" title="Lines" style="width:100%;height:80%">


        <?php
        include_once('Smarty.class.php');

        $fieldId = "id";
        $smarty = initializeSmarty();
        $smarty->assign('title','Mulit Artist List');
        $smarty->assign('item','Artist');
        $smarty->assign('tableWidth','100%');
        $smarty->assign('tableHeight','100%');
        $url = "ArtistSongRelationshipAction.php";
        $url2 = "ArtistSongRelationshipView.php";
        $smarty->assign('tablegrid',"ListArtistSong");
        $smarty->assign('id',$fieldId);
        $smarty->assign('fitColumns',"true");
        $smarty->assign('pageSize',"15");
        $smarty->assign('customEdit',"editLink()");
        $smarty->assign('customAdd',"addArtistSong()");
        $smarty->assign('viewUrl',$url . "?method=listArtistSong");
        $smarty->assign('newUrl',$url2);
        $smarty->assign('updateUrl', "'" . $url2 . "?id='+row['" . $fieldId . "']");
        $smarty->assign('deleteUrl', $url . "?method=deleteArtistSong");
        //$smarty->assign('deleteUrl', $url . "?method=delete");


        $smarty->assign("contacts", array(
        array("field" => "id", "label"=>"Id", "size" => 40, "editable" => false),
        array("field" => "oldArtist", "label"=>"Old Artist", "size" => 60, "editable" => false, "sortable" => true),
        array("field" => "newArtist", "label"=>"New Artist", "size" => 60, "editable" => false, "sortable" => true),
        array("field" => "oldSong", "label"=>"Old Song", "size" => 60, "required" => true, "sortable" => true),
        array("field" => "newSong", "label"=>"New Song", "size" => 60, "required" => true, "sortable" => true),
        array("field" => "album", "label"=>"Album", "size" => 15, "formatter" => "checkboxFormatter",
        "align" => "center", "checkbox" => true),
        array("field" => "comment", "label"=>"Comment", "size" => 60, "required" => false, "sortable" => false),
        array("field" => "exact", "label"=>"Exact", "size" => 15, "formatter" => "checkboxFormatter",
        "align" => "center", "checkbox" => true)
        )
        );
        //** un-comment the following line to show the debug console
        //$smarty->debugging = true;

        $smarty->display('TableGridV4.tpl');
        ?>

    </div>
    <div data-options="region:'south',collapsible:false" title="Result" style="width:100%;height:15%">

        <button type="button" onclick="refreshMultiArtist()">Refresh</button>
        <button type="button" onclick="clearFilterAA('dgListArtistSong')">Clear Filter</button>

    </div>

        <script>
            $(function(){
                var dg = $('#dgListArtistSong').datagrid();
                dg.datagrid('enableFilter');
            });

            function refreshMultiArtist(){
                $('#dgListArtistSong').datagrid('reload');
            }
            $(".datagrid-header-rownumber").click(function(){
                alert("clicked");
            });
            var div = document.getElementsByClassName('datagrid-header-rownumber')[0];

            div.addEventListener('click', function (event) {
                alert('Hi!');
            });

            function clearFilterAA(datagrid){
                clearFilter(datagrid);
            }

            function editLink(){
                var selectedRow = $('#dgListArtistSong').edatagrid("getSelected");
                var url = "<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'ArtistSongRelationshipView.php') ?>" + '?id='+selectedRow.id;
                var win = window.open(url, '_blank');
                win.focus();
            }

            function addArtistSong(){
                openUrl("<?php echo webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'ArtistSongRelationshipView.php') ?>");
            }

        </script>


</div>


</body>
</html>

