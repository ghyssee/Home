<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../../../css/stylesheet.css">
        <link rel="stylesheet" type="text/css" href="../../../Themes/easyui/metro-blue/easyui.css">
        <link rel="stylesheet" type="text/css" href="../../../Themes/easyui/icon.css">
        <link rel="stylesheet" type="text/css" href="../../../css/form.css">
        <script type="text/javascript" src="../../../js/jquery-3.1.1.js"></script>
        <script type="text/javascript" src="../../../js/jquery.easyui.min.js"></script>
    </head>
<body>

<?php
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
$mp3PreprocessorObj = readJSONWithCode(JSON_MP3PREPROCESSOR);
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);

function getCurrentPage(){
    return basename($_SERVER['PHP_SELF']);
}

function getAction(){
    return "MP3PreprocessorAction.php";
}

?>

<style>
    .inlineTable {
        float: left;
    }

    th {
        text-align: left;
    }

</style>

<script>
    window.addEventListener('load', function(){
        var select = document.getElementById('activeconfig');

        select.addEventListener('change', function(){
            window.location = '<?php echo getAction()?>?method=init&id=' + this.value;
        }, false);
    }, false);

</script>


<?php
goMenu();
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Splitter();
}
?>

<?php
$layout = new Layout(array('numCols' => 1));
if (isset($_SESSION["CONFIG_ID"])){
    $currentConfigId = $_SESSION["CONFIG_ID"];
    unset($_SESSION["CONFIG_ID"]);
}
else {
    $currentConfigId = $mp3PreprocessorObj->activeConfiguration;
}
$configs = $mp3PreprocessorObj->configurations;
array_unshift($configs, new PreprocessorConfiguration());
$layout->comboBox($configs, "id", "id",
    new Input(array('name' => "activeConfiguration",
        'label' => 'Active Configuration',
        'col' => 1,
        'method' => 'getConfigurationText',
        'methodArg' => 'config',
        'fieldId' => 'activeconfig',
        'default' => $currentConfigId)));
if ($currentConfigId == ''){
    $layout->button(new Input(array('name' => "addConfig",
        'value' => 'ADD',
        'text' => 'Add',
        'colspan' => 2)));
}
$layout->close();
?>
<?php
//$tableGrid = new TableGrid();
//$tableGrid->title = "Colors222";
//include ("TableGrid.php");
include_once('Smarty.class.php');

$smarty = initializeSmarty();
$smarty->assign('tablegrid',"Splitters");
$smarty->assign('title','Splitter');
$smarty->assign('item','Splitter Item');
$smarty->assign('tableWidth','800px');
$smarty->assign('tableHeight','400px');
$url = getAction();
$smarty->assign('id', 'id');
$smarty->assign('viewUrl',$url . "?method=list");
$smarty->assign('updateUrl',"'" . $url . "?method=update&id='+row['id']");
$smarty->assign('newUrl',"'" . $url . "?method=add'");
$smarty->assign('deleteUrl', $url . "?method=delete");

$smarty->assign("contacts", array(array("field" => "id", "label"=>"Id", "size" => 10, "hidden" => "true"),
        array("field" => "description", "label"=>"Description", "size" => 100, "sortable" => true),
        array("field" => "pattern", "label"=>"Pattern", "size" => 20)
    )
);
$smarty->display('TableGridV3.tpl');
?>

<div style="margin:10px 0">
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="insert()">Insert Row</a>
    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="insert()">Add Configuration</a>
</div>

<table id="tt"></table>


<!--suppress JSAnnotator -->
<script>
    var fields =
        <?php
        global $mp3PreprocessorObj;
        echo json_encode($mp3PreprocessorObj->fields);
        ?>;
    var splitters =
        <?php
        global $mp3PreprocessorObj;
        echo json_encode($mp3PreprocessorObj->splitters);
        ?>;

    function getField(id){
        for(var i = 0; i < fields.length; i++) {
            if (fields[i].id == id){
                return fields[i].description;
            }
        }
        return "";
    }

    function getSplitter(id){
        for(var i = 0; i < splitters.length; i++) {
            if (splitters[i].id == id){
                return splitters[i].description;
            }
        }
        return "";
    }

    $.extend($.fn.datagrid.defaults.editors,
        {
            workingcheckbox:
            {
                init: function (container, options) {
                    var input = $('<input type="checkbox">').appendTo(container);
                    return input;
                },
                getValue: function (target) {
                    return $(target).prop('checked');
                },
                setValue: function (target, value) {
                    $(target).prop('checked', value);
                }
            }

        });

    $(function(){
        $('#tt').datagrid({
            title:'Editable DataGrid',
            iconCls:'icon-edit',
            width:350,
            height:250,
            singleSelect:true,
            idField:'itemid',
            url:'<?php echo getAction(); ?>?method=listConfigurations&id=' + getCurrentConfig(),
            columns:[[
                {field:'id',title:'ID', hidden:true},
                {field:'type',title:'Field',width:100,
                    formatter:function(value,row){
                        //return row.productname || value;
                        return getField(row.type);
                    },
                    editor:{
                        type:'combobox',
                        options:{
                            valueField:'id',
                            textField:'description',
                            data:fields,
                            required:true
                        }
                    }
                },
                {field:'splitter',title:'Splitter',width:100,
                    formatter:function(value,row){
                        //return row.productname || value;
                        return getSplitter(row.splitter);
                    },
                    editor:{
                        type:'combobox',
                        options:{
                            valueField:'id',
                            textField:'description',
                            data:splitters,
                            required:false
                        }
                    }
                },
                {field:'duration',title:'Duration',width:50,align:'center',
                    editor:{
                        type:'workingcheckbox'
                    }
                },
                {field:'action',title:'Action',width:80,align:'center',
                    formatter:function(value,row,index){
                        if (row.editing){
                            var s = '<a href="javascript:void(0)" onclick="saverow(this)">Save</a> ';
                            var c = '<a href="javascript:void(0)" onclick="cancelrow(this)">Cancel</a>';
                            return s+c;
                        } else {
                            var e = '<a href="javascript:void(0)" onclick="editrow(this)">Edit</a> ';
                            var d = '<a href="javascript:void(0)" onclick="deleterow(this)">Delete</a>';
                            return e+d;
                        }
                    }
                }
            ]],
            onEndEdit:function(index,row){
                var ed = $(this).datagrid('getEditor', {
                    index: index,
                    field: 'type'
                });
                //row.productname = $(ed.target).combobox('getText');
            },
            onBeforeEdit:function(index,row){
                row.editing = true;
                $(this).datagrid('checkRow',index);
                $(this).datagrid('refreshRow', index);
            },
            onAfterEdit:function(index,row){
                row.editing = false;
                $(this).datagrid('refreshRow', index);
            },
            onCancelEdit:function(index,row){
                row.editing = false;
                $(this).datagrid('refreshRow', index);
            }
        });
    });
    function getRowIndex(target){
        var tr = $(target).closest('tr.datagrid-row');
        return parseInt(tr.attr('datagrid-row-index'));
    }
    function editrow(target){
        $('#tt').datagrid('beginEdit', getRowIndex(target));
    }
    function deleterow(target){
        $.messager.confirm('Confirm','Are you sure?',function(r){
            if (r){
                $('#tt').datagrid('deleteRow', getRowIndex(target));
            }
        });
    }
    function saverow(target){
        var row = $('#tt').datagrid('getSelected');
        update(row);
        $('#tt').datagrid('endEdit', getRowIndex(target));
    }
    function cancelrow(target){
        $('#tt').datagrid('cancelEdit', getRowIndex(target));
    }
    function insert(){
        var row = $('#tt').datagrid('getSelected');
        if (row){
            var index = $('#tt').datagrid('getRowIndex', row) + 1;
        } else {
            index = 0;
        }
        $('#tt').datagrid('insertRow', {
            index: index,
            row:<?php echo getPreprocessorConfiguration()?>
        });
        $('#tt').datagrid('selectRow',index);
        $('#tt').datagrid('beginEdit',index);
    }

    function addConfig(){
        var row = $('#tt').datagrid('getSelected');
        if (row){
            var index = $('#tt').datagrid('getRowIndex', row) + 1;
        } else {
            index = 0;
        }
        $('#tt').datagrid('insertRow', {
            index: index,
            row:<?php echo getPreprocessorConfiguration()?>
        });
        $('#tt').datagrid('selectRow',index);
        $('#tt').datagrid('beginEdit',index);
    }

    function getCurrentConfig(){
        var e = document.getElementById("activeconfig");
        var configId = e.options[e.selectedIndex].value;
        return configId;
    }

    function update(row){

        var object = {row:row, configId:getCurrentConfig()};
        var tmp = $.post('<?php echo getAction();?>?method=updateConfig', { selectedRow : JSON.stringify(object)}, function(data2){
                if (data2.success){
                    $('#dg').datagrid('reload');
                }
            },'json')
            .done(function() {
                //alert( "second success" );
            })
            .fail(function() {
                //alert( "error" );
            })
            .always(function() {
                //alert( "finished" );
            });
        tmp.always(function() {
            //alert( "second finished" );
        });
        //$('#dg').datagrid('gotoPage', 2);
        return false;
    }


</script>
<div class="emptySpace"></div>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html>

<?php
function getPreprocessorConfiguration(){
    $conf = new PreprocessorConfigurationItem();
    return json_encode($conf);
}

function getConfigurationText($array)
{
$desc = "";
if (empty ($array)){
    $desc = 'New Configuration';
}
else {
    foreach ($array as $key => $config) {
        if (isset($config->type)) {
            $desc = $desc . (empty($desc) ? '' : ' ') . $config->type;
        }
        if (isset($config->splitter)) {
            $desc = $desc . " " . $config->splitter;
        }
        if (!empty($config->duration)) {
            $desc = $desc . " (duration TRUE)";
        }
    }
}
return $desc;
}
?>