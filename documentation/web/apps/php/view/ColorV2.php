<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="../../Themes/easyui/icon.css">
    <script type="text/javascript" src="../../js/jquery-3.1.1.js"></script>
    <script type="text/javascript" src="../../js/jquery.easyui.min.js"></script>
</head>
<body style="background">

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);
?>
<?php
goMenu();
?>
<h1>Colors</h1>
<div class="horizontalLine">.</div>
<?php
goMenu();
?>
<br>
<?php
echo json_encode(array(
'description' => 'dd',
'code' => 'test'
));
?>
	<table id="dg" title="My Users" class="easyui-datagrid" style="width:700px;height:500px"
			url="test.php?method=list"
			toolbar="#toolbar" pagination="true" rownumbers="true" fitColumns="true" singleSelect="true">
		<thead>
			<tr>
				<th field="description" width="50">Description</th>
				<th field="code" width="50">Code</th>
			</tr>
		</thead>
	</table>
	<div id="toolbar">
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord()">New Color</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord()">Edit Color</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecord()">Remove Color</a>
	</div>
	
	<div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
			closed="true" buttons="#dlg-buttons">
		<div class="ftitle">Color Information</div>
		<form id="fm" method="post" novalidate>
			<div class="fitem">
				<label>Description</label>
				<input name="description" class="easyui-textbox" required="true">
			</div>
			<div class="fitem">
				<label>Code</label>
				<input name="code" class="easyui-textbox" required="true" hidden="true">
			</div>
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="saveUser()" style="width:90px">Save</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
	</div>
	<script type="text/javascript">
		$('#dg').datagrid({
			columns:[[
				{field:'id',title:'Id', hidden:true},
				{field:'description',title:'Desc', width:20},
				{field:'code',title:'Code', width:20}
			]]
		});
		var url;
		function newRecord(){
			$('#dlg').dialog('open').dialog('setTitle','New Color');
			$('#fm').form('clear');
			url = 'test.php?method=save';
		}
		function editRecord(){
			var row = $('#dg').datagrid('getSelected');
			if (row){
				$('#dlg').dialog('open').dialog('setTitle','Edit Color');
				$('#fm').form('load',row);
				url = 'test.php?method=update&id='+row['id'];
			}
		}
		function saveUser(){
			$('#fm').form('submit',{
				url: url,
				onSubmit: function(){
					return $(this).form('validate');
				},
				success: function(result){
					var result = eval('('+result+')');
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
				$.messager.confirm('Confirm','Are you sure you want to delete this color?',function(r){
					if (r){
						$.post('ColorSaveV2.php?mode=D',{id:row.id},function(result){
							if (result.success){
								$('#dg').datagrid('reload');	// reload the user data
							} else {
								$.messager.show({	// show error message
									title: 'Error',
									msg: result.errorMsg
								});
							}
						},'json');
					}
				});
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



</body>
</html>

