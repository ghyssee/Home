<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
?>


<html>
<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'stylesheet.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_CSS, 'form.css')?>">
    <?php include documentRoot ("apps/php/templates/easyui.php");?>
</head>
<body style="background">

<div class="easyui-layout" style="width:500px;height:300px;">
    <div data-options="region:'north',collapsible:false, border:false" style="padding:5px;width:100%;height:90%">
        <?php
        include_once('Smarty.class.php');

        $url = webPath(ROOT_PHP_CONFIGURATION_MP3PRETTIFIER, 'MP3PrettifierAction.php');
        $fieldId = "id";
        $smarty = initializeSmarty();
        $smarty->assign('title','Artists');
        $smarty->assign('item','Artist');
        $smarty->assign('tableWidth','100%');
        $smarty->assign('tableHeight','100%');
        $smarty->assign('tablegrid',"OldArtist");
        $smarty->assign('id',$fieldId);
        $smarty->assign('fitColumns',"true");
        $smarty->assign('pageSize',"40");
        $smarty->assign('customSave', 'customSaveArtistCRUD()');
        $smarty->assign('viewUrl',$url . "?method=getListArtists");
        $smarty->assign('updateUrl',"'" . $url . "?method=updateArtist&id='+row['" . $fieldId . "']");
        $smarty->assign('newUrl',"'" . $url . "?method=addArtist'");
        $smarty->assign('deleteUrl', $url . "?method=deleteArtist");


        $smarty->assign("contacts", array(
                array("field" => "id", "label"=>"Id", "size" => 30, "hidden" => true),
                array("field" => "name", "label"=>"Name", "size" => 40, "required" => true, "sortable" => true)
            )
        );
        //** un-comment the following line to show the debug console
        //$smarty->debugging = true;

        $smarty->display('TableGridV4.tpl');
        ?>
        </div>
        <div data-options="region:'south',collapsible:false, border:false" style="padding:5px;width:100%;height:10%">
            <button type="button" onclick="addArtist()">Add Artist</button>
        </div>

</div>

</body>
</html>

<script type="text/javascript">
    $(function(){
        var dg = $('#dgOldArtist').datagrid();
        dg.datagrid('enableFilter');
    });

    function addArtist(){
        var row = $('#dgOldArtist').datagrid('getSelected');
        var rowIndex = $("#dgOldArtist").datagrid("getRowIndex", row);
        if (row) {
            $('#dgOldArtist').datagrid('unselectRow', rowIndex);
        }
        else {
            $.messager.alert('Warning','No Artist Selected');
        }
    }


</script>

