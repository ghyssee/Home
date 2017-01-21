	<table id="dg" title="{{$title}}" class="easyui-datagrid" style="width:{{$tableWidth}};height:{{$tableHeight}}" idField="id"
			url="{{$viewUrl}}"
			toolbar="#toolbar" pagination="true" nowrap="false" rownumbers="true" fitColumns="true" singleSelect="true">
		<thead>
		<tr>
			{{if isset($checkbox)}}
				<th field="{{$checkboxField}}" checkbox="true"></th>
			{{/if}}
			{{section name=sec1 loop=$contacts}}
			<th field="{{$contacts[sec1].field}}" {{if isset($contacts[sec1].hidden) && $contacts[sec1].hidden}}hidden="true"{{/if}}
				width="{{$contacts[sec1].size}}"
				{{if isset($contacts[sec1].formatter)}} formatter="{{$contacts[sec1].formatter}}"{{/if}}
                {{if isset($contacts[sec1].sortable)}} sortable={{if $contacts[sec1].sortable}}"true"{{else}}"false"{{/if}}{{/if}}
            >
                {{$contacts[sec1].label}}
			</th>
			{{/section}}
		</tr>
		</thead>
	</table>

	<span style="font-size:20px">
	<div id="toolbar">
	{{if isset($newUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord()">New {{$item}}</a>
	{{/if}}
	{{if isset($updateUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord()">Edit {{$item}}</a>
	{{/if}}
	{{if isset($deleteUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecord()">Remove {{$item}}</a>
	{{/if}}
	</div>
	</span>

	<div id="dlg" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
			closed="true" buttons="#dlg-buttons">
		<div class="ftitle">{{$item}} Information</div>
		<form id="fm" method="post" novalidate>
		
			{{section name=sec1 loop=$contacts}}
			  {{if !isset($contacts[sec1].hidden) OR !$contacts[sec1].hidden}}
				<div class="fitem">
					<label>{{$contacts[sec1].label}}</label>
					<input name="{{$contacts[sec1].field}}" class="easyui-textbox" required="true">
				</div>
				{{/if}}
			{{/section}}

		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save()" style="width:90px">Save</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg').dialog('close')" style="width:90px">Cancel</a>
	</div>


	<script type="text/javascript">
		$('#dg').datagrid({
			options:[[
				{rowHeight:100}
			]],
			autoRowHeight:true
		});
		var url;
		function newRecord(){
			$('#dlg').dialog('open').dialog('setTitle','New {{$item}}');
			$('#fm').form('clear');
			url = {{$newUrl}};
		}
		function editRecord(){
			var row = $('#dg').datagrid('getSelected');
			if (row){
				$('#dlg').dialog('open').dialog('setTitle','Edit {{$item}}');
				$('#fm').form('load',row);
				url = {{$updateUrl}};
			}
		}
		function save(){
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
				$.messager.confirm('Confirm','Are you sure you want to delete this {{$item}}?',function(r){
                    if (r){
                        $.ajax({
                            type:    "POST",
                            url:     "{{$deleteUrl}}",
                            data:    {id: row.{{$id}}},
                            success: function(data) {
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
