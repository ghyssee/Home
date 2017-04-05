<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
?>

<head>
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/metro-blue/easyui.css')?>">
    <link rel="stylesheet" type="text/css" href="<?php echo webPath(ROOT_THEMES, 'easyui/icon.css')?>">
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS, 'jquery-3.1.1.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.easyui.min.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'jquery.edatagrid.js')?>"></script>
    <script type="text/javascript" src="<?php echo webPath(ROOT_JS_EASYUI, 'datagrid-dnd.js')?>"></script>

</head>

<body>
<div class="easyui-panel" style="width:200px;height:20px"
     title="Actions" data-options="iconCls:'icon-ok',tools:[
				{
					iconCls:'icon-add',
					handler:function(){alert('add')}
				},{
					iconCls:'icon-edit',
					handler:function(){alert('edit')}
				}]">
</div>

<div id="p" class="easyui-panel" title="Basic Panel" style="width:430px;height:200px;">
    <header>
        <span style="display:inline-block;line-height:20px">title</span>
		<span style="float:right;">
			<a href="#" class="easyui-linkbutton" plain="true" iconCls="icon-save" style="height:20px">Save</a>
			<a href="#" class="easyui-linkbutton" plain="true" iconCls="icon-add" style="height:20px">Add</a>
		</span>
    </header>
</div>

<h2>Basic Layout</h2>
<p>The layout contains north,south,west,east and center regions.</p>
<div style="margin:20px 0;"></div>
<div class="easyui-layout" style="width:880px;height:450px;">

    <!-- Buttons -->

    <div data-options="region:'north',collapsible:false" title="" style="height:10%;">
    </div>

    <!-- Artists -->

    <div data-options="region:'west',collapsible:false" title="Artist" style="width:30%;">West
    </div>

    <!-- Buttons -->

    <div data-options="region:'center',title:'Actions'" style="width:10%;">



        <div class="easyui-layout" data-options="fit:true">
            <div data-options="region:'north',collapsible:false" title=" " style="height:30%;">
                <div><button style="width: 80%;"onclick="removeArtist('dgArtist')"><<</button></div>
                <div><button onclick="refreshList('dlArtistList')">Refresh</button></div>
            </div>
            <div data-options="region:'center',title:' '" style="height:30%;">
                <div><button onclick="removeArtist('dgArtistSeq')"><<</button></div>
                <div><button onclick="testChk()">Test</button></div>
            </div>
            <div data-options="region:'south',collapsible:false" title=" " style="height:40%;">
                <div><button onclick="insert()">Add</button></div>
                <div><button onclick="clearArtists()">Clear</button></div>
                <div><button onclick="validateAndSave()">Save</button></div>
            </div>
        </div>

    </div>

    <!-- Config -->


    <div data-options="region:'east',collapsible:false" title="Multi Artist" style="width:60%;">
        <div class="easyui-layout" data-options="fit:true">
            <div data-options="region:'north',collapsible:false" title="A1" style="height:30%;">North</div>
            <div data-options="region:'center',title:'A2'" style="height:30%;">
                Center
            </div>
            <div data-options="region:'south',collapsible:false" title="Form" style="height:40%;">Form</div>
        </div>

    </div>
</div>

</body>
</html>

