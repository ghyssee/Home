



<html>
<head>
    <link rel="stylesheet" type="text/css" href="/catalog/apps/css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/css/form.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/themes/easyui/metro-blue/easyui.css">
    <link rel="stylesheet" type="text/css" href="/catalog/apps/themes/easyui/icon.css">
    <script type="text/javascript" src="/catalog/apps/js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/datagrid-dnd.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/datagrid-filter.js"></script>
    <script type="text/javascript" src="/catalog/apps/js/easyui/easyUIUtils.js"></script>

</head>
<body style="background">

<style>
    .datagrid-cell{
        font-size:12px;
    }
    .datagrid-header .datagrid-cell span {
        font-size: 12px;
    }
    .datagrid-row{
        height: 18px;
    }

    #column1 {
        float: left;
        width: 8%;
    }
    #column2 {
        float: right ;
        width: 92% ;
    }
    #innercolumn1 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
    #innercolumn2 {
        padding-left: 5px ;
        padding-right: 5px ;
    }
</style>

<div class="centered">
    <a href="/catalog">Menu</a>
</div>

<h3>Mezzmo / MP3 Check</h3>

<div id="tt" class="easyui-tabs" data-options="selected:0" style="width:1400px;height:700px;">
    <div title="Settings" style="padding:20px;display:none;">
        <form action="AlbumErrorsAction.php?method=updateSettings" method="post">
            <input type="hidden" name="formKey" value="7Ypp3al0iz26JEbnDc0fNr5ScWeuN2GF"><table>
                <tr class='spaceUnder'>
                <tr class="spaceUnder">
                    <td>Status</td>
                    <td><select class="inputField" name="albumErrorsStatus"><option value="SCAN_FULL" selected>Full Scan</option>
                            <option value="SCAN_FILES">Scan files that were in error</option>
                            <option value="FIX_ERRORS">Fix the selected errors</option>
                            <option value="UPDATE_SONGS">Validate The Update Song Info</option>
                            <option value="FIX_FILETITLE">Fix All Errors With FileTitle</option>
                        </select></td>
                </tr>
                <tr class='spaceUnder'>
                    <td>Max Number Of Errors</td>
                    <td><input class="inputField" size="5" min="1" type="number" name="maxNumberOfErrors" value="1000" autofocus></td>
                </tr>
                <tr class='spaceUnder'>
                    <td class="buttonCell" colspan="2"><button class="formButton" name="mp3Check" value="save">Save</button></td>
                </tr>
            </table>
        </form>



        <div style="margin:10px 0;">
            <span>Selection Mode: </span>
            <select onchange="$('#dg').datagrid({singleSelect:(this.value==0)})">
                <option value="0">Single Row</option>
                <option value="1" selected>Multiple Rows</option>
            </select>
            <br/>
            SelectOnCheck: <input type="checkbox" checked onchange="$('#dg').datagrid({selectOnCheck:$(this).is(':checked')})">
            <br/>
            CheckOnSelect: <input type="checkbox" checked onchange="$('#dg').datagrid({checkOnSelect:$(this).is(':checked')})">
        </div>


    </div>
    <div title="Errors" style="overflow:auto;padding:20px;display:none;" >
        <div id="container">
            <div id="column1">
                <div id="innercolumn1">
                    <form onsubmit="return myFunction()">
                        <input type="submit" value="Process">
                    </form>
                </div>
            </div>
            <div id="column2">
                <div id="innercolumn2">
                    <script type="text/javascript">
                        function stripCell(val,row){
                            if (row.type == "FILE") {
                                return val.replace(row.basePath, "");
                            }
                            else {
                                return val;
                            }
                        }
                    </script>



                    <table id="dg" title="Album Errors" class="easyui-datagrid" style="width:100%;height:700px" idField="uniqueId"
                           url="javascript:window.open('../view/AlbumErrorsAction.php?method=list','_self')"       data-options='fitColumns:true,
		                 onLoadSuccess:function(data){
		                 		                 		                 },
		                 onCheck:function(index,row){
		                    alert(row.uniqueId);
		                 },
        rowStyler: function(index,row){
            if ((index % 2) == 3){
                return "background-color:grey;font-weight:bold;";

            }
       },
       toolbar:"#toolbar",
       pagination:true,
       nowrap:false,
       remoteFilter:true,
       rownumbers:true,
       pagePosition:"both",
       pageSize:10,
       pageList:[5,10,15,20,25,30,40,50],
       singleSelect:false'>
                        <thead>
                        <tr>
                            <th data-options="field:'process',checkbox:true"></th>
                            <th data-options="field:'id',hidden:true">Id</th>
                            <th data-options="field:'file',width:200,sortable:true">File</th>
                            <th data-options="field:'type',width:50,sortable:true">Type</th>
                            <th data-options="field:'oldValue',width:200,formatter:function(value,row,index){return stripCell(value,row,index);} ">Old Value</th>
                            <th data-options="field:'newValue',width:200,formatter:function(value,row,index){return stripCell(value,row,index);} ">New Value</th>
                        </tr>
                        </thead>
                    </table>

<span style="font-size:20px">
	<div id="toolbar">
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord()">New Album Errors</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord()">Edit Album Errors</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecord()">Remove Album Errors</a>
    </div>
	</span>

                    <div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
                         closed="true" buttons="#dlg-buttons">
                        <div class="ftitle">Album Errors Information</div>
                        <form id="fm" method="post" novalidate>

                            <div class="fitem">
                                <label>File</label>
                                <input name="file"
                                       class="easyui-textbox"

                                >
                            </div>
                            <div class="fitem">
                                <label>Type</label>
                                <input name="type"
                                       class="easyui-textbox"

                                >
                            </div>
                            <div class="fitem">
                                <label>Old Value</label>
                                <input name="oldValue"
                                       class="easyui-textbox"

                                >
                            </div>
                            <div class="fitem">
                                <label>New Value</label>
                                <input name="newValue"
                                       class="easyui-textbox"

                                >
                            </div>

                        </form>
                    </div>
                    <div id="dlg-buttons">
                        <a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok"
                           onclick="save()" style="width:90px">Save</a>
                        <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
                    </div>


                    <script type="text/javascript">
                        var url;
                        function newRecord(){
                            $('#dlg').dialog('open').dialog('setTitle','New Album Errors');
                            $('#fm').form('reset');
                            url = 'AlbumErrorsAction.php?method=add';
                        }
                        function editRecord(){
                            var row = $('#dg').datagrid('getSelected');
                            if (row){
                                $('#dlg').dialog('open').dialog('setTitle','Edit Album Errors');
                                $('#fm').form('load',row);
                                url = 'AlbumErrorsAction.php?method=update&id='+row['uniqueId'];
                            }
                        }
                        function save(){
                            $('#fm').form('submit',{
                                url: url,
                                onSubmit: function(){
                                    return $(this).form('validate');
                                },
                                success: function(result){
                                    //var result = eval('('+result+')');
                                    var result = JSON.parse(result);
                                    if (result.errorMsg){
                                        $.messager.show({
                                            title: 'Error',
                                            msg: result.errorMsg
                                        });
                                    } else {
                                        $('#dlg').dialog('close');		// close the dialog
                                        $('#dg').datagrid('reload');	// reload the user data
                                    }
                                }
                            });
                        }

                        function deleteRecord(){
                            var row = $('#dg').datagrid('getSelected');
                            if (row){
                                $.messager.confirm('Confirm','Are you sure you want to delete this Album Errors?',function(r){
                                    if (r){
                                        $.ajax({
                                            type:    "POST",
                                            url:     "'AlbumErrorsAction.php?method=delete',{id:row['uniqueId']}",
                                            data:    {id: row.uniqueId},
                                            success: function(data) {
                                                var dataObj = JSON.parse(data);
                                                if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')){
                                                    alert(dataObj.errorMessage);
                                                }
                                                $('#dg').datagrid('reload');
                                            },
                                            // vvv---- This is the new bit
                                            error:   function(jqXHR, textStatus, errorThrown) {
                                                alert("Error, status = " + textStatus + ", " +
                                                    "error thrown: " + errorThrown
                                                );
                                            }
                                        });
                                    }
                                });
                            }
                        }
                        function checkboxFormatter(val,row,index){

                            if (val== 1) {
                                return "√";
                            }
                            else {
                                return "";
                            }
                        }

                    </script>

                    <style type="text/css">
                        #fm{
                            margin:0;
                            padding:10px 30px;
                        }
                        .ftitle{
                            font-size:14px;
                            font-weight:bold;
                            padding:5px 0;
                            margin-bottom:10px;
                            border-bottom:1px solid #ccc;
                        }
                        .fitem{
                            margin-bottom:5px;
                        }
                        .fitem label{
                            display:inline-block;
                            width:80px;
                        }
                        .fitem input{
                            width:160px;
                        }
                    </style>
                    <script type="text/javascript">
                        $('#dg').datagrid({
                            onLoadSuccess:function(data){
                                var rows = $(this).datagrid('getRows');
                                /*
                                 for(i=0;i<rows.length;++i){
                                 if(rows[i]['process']==1) $(this).datagrid('checkRow',i);
                                 }*/
                                $(this).data('datagrid').checkedRows = data.selectedRows;
                                $(this).data('datagrid').selectedRows = data.selectedRows;
                                var dgPanel = $(this).datagrid('getPanel');
                                var title = 'Album Errors';
                                dgPanel.panel('setTitle', title + '(' + data.selectedRows.length + ' rows selected)');
                            }
                        });
                    </script>
                    <script type="text/javascript">
                        function myUrl(){
                            return
                        }

                        function myFunction(){
                            var selectObj = { ids: []}
                            var ids = [];
                            var rows = $('#dg').datagrid('getSelections');
                            for(var i=0; i<rows.length; i++){
                                ids.push(rows[i].uniqueId);
                            }
                            selectObj.ids = ids;
                            var tmp = $.post('AlbumErrorsAction.php?method=select', { selectedIds : JSON.stringify(selectObj)}, function(data2){
                                    if (data2.success){
                                        //$('#dg').datagrid('gotoPage', data2.pageNumber);
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
                </div>
            </div>
        </div>

    </div>
</div>

<script src="/catalog/apps/js/easyui/EasyUITabsMouseHover.js"></script>



<br>
<br />
<font size='1'><table class='xdebug-error xe-warning' dir='ltr' border='1' cellspacing='0' cellpadding='1'>
        <tr><th align='left' bgcolor='#f57900' colspan="5"><span style='background-color: #cc0000; color: #fce94f; font-size: x-large;'>( ! )</span> Warning: file_put_contents(C:\My Programs\SkyDrive/Log/Java/HPW8ERIC2/infoLog20171021.txt): failed to open stream: Permission denied in C:\Projects\GitHub\Home\documentation\web\apps\php\setup.php on line <i>183</i></th></tr>
        <tr><th align='left' bgcolor='#e9b96e' colspan='5'>Call Stack</th></tr>
        <tr><th align='center' bgcolor='#eeeeec'>#</th><th align='left' bgcolor='#eeeeec'>Time</th><th align='left' bgcolor='#eeeeec'>Memory</th><th align='left' bgcolor='#eeeeec'>Function</th><th align='left' bgcolor='#eeeeec'>Location</th></tr>
        <tr><td bgcolor='#eeeeec' align='center'>1</td><td bgcolor='#eeeeec' align='center'>0.0061</td><td bgcolor='#eeeeec' align='right'>365344</td><td bgcolor='#eeeeec'>{main}(  )</td><td title='c:\Projects\GitHub\Home\Documentation\web\apps\php\view\AlbumErrorsView.php' bgcolor='#eeeeec'>...\AlbumErrorsView.php<b>:</b>0</td></tr>
        <tr><td bgcolor='#eeeeec' align='center'>2</td><td bgcolor='#eeeeec' align='center'>0.9665</td><td bgcolor='#eeeeec' align='right'>43347000</td><td bgcolor='#eeeeec'>goMenu(  )</td><td title='c:\Projects\GitHub\Home\Documentation\web\apps\php\view\AlbumErrorsView.php' bgcolor='#eeeeec'>...\AlbumErrorsView.php<b>:</b>82</td></tr>
        <tr><td bgcolor='#eeeeec' align='center'>3</td><td bgcolor='#eeeeec' align='center'>0.9665</td><td bgcolor='#eeeeec' align='right'>43347024</td><td bgcolor='#eeeeec'>readJSONWithCode( ???, ??? )</td><td title='C:\Projects\GitHub\Home\documentation\web\apps\php\config.php' bgcolor='#eeeeec'>...\config.php<b>:</b>114</td></tr>
        <tr><td bgcolor='#eeeeec' align='center'>4</td><td bgcolor='#eeeeec' align='center'>0.9666</td><td bgcolor='#eeeeec' align='right'>43347184</td><td bgcolor='#eeeeec'>logInfo( ??? )</td><td title='C:\Projects\GitHub\Home\documentation\web\apps\php\setup.php' bgcolor='#eeeeec'>...\setup.php<b>:</b>154</td></tr>
        <tr><td bgcolor='#eeeeec' align='center'>5</td><td bgcolor='#eeeeec' align='center'>0.9667</td><td bgcolor='#eeeeec' align='right'>43347376</td><td bgcolor='#eeeeec'><a href='http://www.php.net/function.file-put-contents' target='_new'>file_put_contents</a>
                ( ???, ???, ??? )</td><td title='C:\Projects\GitHub\Home\documentation\web\apps\php\setup.php' bgcolor='#eeeeec'>...\setup.php<b>:</b>183</td></tr>
    </table></font>
<div class="centered">
    <a href="/catalog">Menu</a>
</div>
<br/>

</body>
</html>

