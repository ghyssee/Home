<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 24/11/2017
 * Time: 13:47
 */
$DATAGRID_ID = "dgDistrict";
$FORM_ID = "fmDistrict";
$DIALOG_ID = "dlgDistrict";
$TOOLBAR_ID = "toolbarDistrict";
$DIALOG_BUTTONS_ID = "dlg-buttonsDistrict";
session_start();
include_once("../../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "JobBO.php");
?>

    <html>
    <head>

<?php include documentPath (ROOT_PHP_TEMPLATES, "Stylesheet.php");?>
<?php include documentPath (ROOT_PHP_TEMPLATES, "easyui.php");?>
    </head>
    <body>

    <?php include documentPath (ROOT_PHP_MENU, "Menu.php"); ?>

    <h3>District Manager</h3>


    <div id="cc" class="easyui-layout" style="width:80%;height:80%">
        <div data-options="region:'south',title:'Jobs',split:true" style="height:100%;padding:5px;">

            <table id="<?php echo $DATAGRID_ID;?>" class="easyui-datagrid" style="width:50%;height:50%"
                   title="List Of Districts"
                   idField="uniqueId"
                   url='DistrictAction.php?method=list'
                   data-options='fitColumns:true,
                         singleSelect:true,
                        onLoadSuccess:function(){
                            $(this).datagrid("enableDnd");
                        },
                        toolbar:"#<?php echo $TOOLBAR_ID;?>",
                        pagination:false,
                        nowrap:false,
                        queryParams:{profile:getProfile()},
                        remoteFilter:true,
                        rownumbers:true,
                        singleSelect:true'
            >

                <thead>
                <tr>
                    <th data-options="field:'uniqueId',hidden:true">ID</th>
                    <th data-options="field:'id', width:2">District Id</th>
                    <th data-options="field:'description',width:10">Description</th>
                    <th data-options="field:'scan',width:2,align:'center',formatter:function(value,row,index){return checkboxFormatter(value,row,index);} ">Enabled</th>
                    <th data-options="field:'scanChapterStart', width:2">Min Chapter</th>
                    <th data-options="field:'scanChapterEnd', width:2">Max Chapter</th>
                </tr>
                </thead>
            </table>


            <span style="font-size:20px">
                <div id="<?php echo $TOOLBAR_ID;?>" style="padding:10px">
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecordDistrict()">New District</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecordDistrict()">Edit District</a>
                    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecordDistrict()">Delete District</a>
                    <?php
                    $smarty = initializeSmarty();
                    $smarty->assign('datagrid',$DATAGRID_ID);
                    $smarty->assign('layout','cc');
                    $smarty->display('MafiaReloadedProfile.tpl');
                    ?>
                </div>
	        </span>

            <div id="<?php echo $DIALOG_ID;?>" class="easyui-dialog" style="width:400px;height:400px;padding:10px 20px"
                 closed="true" buttons="#<?php echo $DIALOG_BUTTONS_ID;?>">
                <div class="ftitle">Job Schedule</div>
                <form id="<?php echo $FORM_ID;?>" method="post" novalidate>
                    <div class="fitem">
                        <label>District Id</label>
                        <input name="id" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:1"
                        >
                    </div>
                    <div class="fitem">
                        <label>Description</label>
                        <input id="description"
                               name="description"
                               required="true"
                               class="easyui-textbox"
                               style="width:220px"
                        >
                    </div>
                    <div class="fitem">
                        <label>Enable Scan</label>
                        <input name="scan"
                               class="easyui-checkbox" type="checkbox" value="true"
                        >
                    </div>
                    <div class="fitem">
                        <label>Starting Chapter</label>
                        <input name="scanChapterStart" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                    </div>
                    <div class="fitem">
                        <label>Ending Chapter</label>
                        <input name="scanChapterEnd" class="easyui-numberspinner" style="width:150px;"
                               data-options="min:0"
                        >
                        <div class="fitem">
                            <label>Event</label>
                            <input name="event"
                                   class="easyui-checkbox" type="checkbox" value="true"
                            >
                        </div>
                    </div>
                </form>
            </div>
            <div id="<?php echo $DIALOG_BUTTONS_ID;?>">
                <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                   onclick="saveRecordDistrict()" style="width:90px">Save</a>
                <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#<?php echo $DIALOG_ID;?>').dialog('close')" style="width:90px">Cancel</a>
            </div>



        </div>
    </div>


    <script type="text/javascript">

        function saveRecordDistrict(){
            saveRecord('<?php echo $FORM_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $DATAGRID_ID;?>');
        }

        function newRecordDistrict(){
            newRecord('<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'District', 'DistrictAction.php?method=addDistrict' + "&profile=" + getProfile());
        }
        function editRecordDistrict(){
            editRecord('<?php echo $DATAGRID_ID;?>', '<?php echo $DIALOG_ID;?>', '<?php echo $FORM_ID;?>', 'uniqueId', 'District', 'DistrictAction.php?method=updateDistrict&profile=' + getProfile());
        }

        function deleteRecordDistrict(){
            deleteRecordV2('<?php echo $DATAGRID_ID;?>', 'district', 'uniqueId', "DistrictAction.php?method=deleteDistrict&profile=" + getProfile());
        }
    </script>

    </body>
    </html>
