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

<div id="dd" class="easyui-dialog" title="My Dialog" style="width:400px;height:200px;"
     data-options="iconCls:'icon-save',resizable:true,modal:true">
    Dialog Content.
</div>

<div id="dlgGlobalWord" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
>

    <form id="fmGlobalWord" method="post" novalidate>
        <div class="fitem">
            <label>Old Word</label>
            <input name="oldWord"
                   required="true"                                                           class="easyui-textbox"

            >
        </div>
        <div class="fitem">
            <label>New Word</label>
            <input name="newWord"
                   class="easyui-textbox"

            >
        </div>
        <div class="fitem">
            <label>Priority</label>
            <input name="priority"
                   value="100"
                   class="easyui-numberspinner" style="margin-bottom:20px"
                   data-options="required:true,min:0,editable:true">

        </div>
        <div class="fitem">
            <label>Exact Match</label>
            <input name="exactMatch"
                   class="easyui-checkbox" type="checkbox" checked value="true"
            >
        </div>

    </form>
</div>









</body>
</html>












</body>
</html>

