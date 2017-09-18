<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
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
if (isset($_REQUEST["id"])) {
    //$artistSongId = htmlspecialchars($_REQUEST['id']);
    //$artistSongRelationShipBO = new ArtistSongRelationShipBO();
    //$artistSongRelationshipTO = $artistSongRelationShipBO->findArtistSongRelationship($artistSongId);
    //$artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO($artistSongRelationshipTO);
}
else {
    //$artistSongId = null;
    //$artistSongRelationshipObj = new ArtistSongRelationshipCompositeTO(null);
}
?>
<script>
    //var oldData = [{"id":"1", "name":"test"}];
    var oldData = <?= json_encode($artistSongRelationshipObj->oldArtistListObj) ?>;
</script>
<h1>Multi Artist Configuration</h1>
<div class="horizontalLine">.</div>
<br>

<form id="multiArtistForm">

    <div id="cc" class="easyui-layout" style="width:1080px;height:480px;">
        <div id="oldArtistTypeLayOut"
             data-options="region:'west',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title="Artist List"
        >
            <div style="padding-top:8px;padding-left:8px;padding-bottom:0px">
                List Artists
            </div>
            <div style="padding:8px">

                <input id="cbArtist" class="easyui-combobox" name="cbArtist"
                       data-options="valueField:'id',
                             width:200,
                             textField:'name',
                             url:'MP3PrettifierAction.php?method=listArtists'"
                >
            </div>
        </div>
        <div id="actions"
             data-options="region:'center',collapsible:false, border:false"
             style="text-align:center;padding:0px;width:16%;height:95%"
             title="Actions"
        >
            <div><button type="button" style="width:80%" onclick="insert()">Add</button></div>
            <div><button type="button" style="width:80%" onclick="clearArtists()">Clear</button></div>
            <div><button type="button" style="width:80%" onclick="validateAndSave()">Save</button></div>
            <div><button type="button" style="width:80%" onclick="removeArtist('dgArtist')">Remove A1</button></div>
            <div><button type="button" style="width:80%" onclick="removeArtist('dgArtistSeq')">Remove A2</button></div>
            <div><button type="button" style="width:80%" onclick="refreshCombo('cbArtist')">Refresh Art.</button></div>
            <div><button type="button" style="width:80%" onclick="testChk()">Test Check</button></div>
        </div>
        <div id="actions"
             data-options="region:'east',collapsible:false, border:false"
             style="padding:0px;width:42%;height:95%"
             title="Selected Artists"
        >
        </div>
</form>


