<script type="text/javascript">
    window.addEventListener('load', function(){
        var select = document.getElementById('activeconfig');

        select.addEventListener('change', function(){
            var tab = $('#preprocessorTabs').tabs('getSelected');
            var index = $('#preprocessorTabs').tabs('getTabIndex',tab);
            window.location = '<?php echo getAction()?>?method=init&id=' + this.value + '&tab='+index;
        }, false);
    }, false);
</script>

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