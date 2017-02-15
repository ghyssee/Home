	<table id="dg{{$tablegrid}}" title="{{$title}}" class="easyui-datagrid" style="width:{{$tableWidth}};height:{{$tableHeight}}" idField="{{$id}}"
		   url="{{$viewUrl}}"
		   data-options='fitColumns:false,
		                 onLoadSuccess:function(data){ {{if isset($onLoadSuccess)}}{{$onLoadSuccess}}{{/if}}  }
		                '
		   toolbar="#toolbar{{$tablegrid}}"
		   pagination="true"
		   nowrap="false"
		   rownumbers="true"
		   fitColumns="true"
		   pagePosition={{if isset($pagePosition)}}"{{$pagePosition}}"{{else}}"bottom"{{/if}}
		   pageSize="10"
		   singleSelect="{{if isset($singleSelect)}}{{$singleSelect}}{{else}}true{{/if}}"
	>
		<thead>
		<tr>
			{{if isset($checkbox)}}
				<th field="{{$checkboxField}}" checkbox="true"></th>
			{{/if}}
			{{section name=sec1 loop=$contacts}}
			<th field="{{$contacts[sec1].field}}" {{if isset($contacts[sec1].hidden) && $contacts[sec1].hidden}}hidden="true"{{/if}}
                {{if isset($contacts[sec1].size)}}width="{{$contacts[sec1].size}}"{{/if}}
                {{if isset($contacts[sec1].align)}}align="center"{{/if}}
				{{if isset($contacts[sec1].formatter)}} formatter="{{$contacts[sec1].formatter}}"{{/if}}
                {{if isset($contacts[sec1].sortable)}} sortable={{if $contacts[sec1].sortable}}"true"{{else}}"false"{{/if}}{{/if}}
                {{if isset($contacts[sec1].selectRow)}} checkbox="true"{{/if}}
            >
            {{if isset($contacts[sec1].label)}}{{$contacts[sec1].label}}{{/if}}
			</th>
			{{/section}}
		</tr>
		</thead>
	</table>

	<span style="font-size:20px">
	<div id="toolbar{{$tablegrid}}">
	{{if isset($newUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="newRecord{{$tablegrid}}()">New {{$item}}</a>
	{{/if}}
	{{if isset($updateUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="editRecord{{$tablegrid}}()">Edit {{$item}}</a>
	{{/if}}
	{{if isset($deleteUrl)}}
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRecord{{$tablegrid}}()">Remove {{$item}}</a>
	{{/if}}
	</div>
	</span>

	<div id="dlg{{$tablegrid}}" class="easyui-dialog" style="width:400px;height:280px;padding:10px 20px"
			closed="true" buttons="#dlg-buttons{{$tablegrid}}">
		<div class="ftitle">{{$item}} Information</div>
		<form id="fm{{$tablegrid}}" method="post" novalidate>
		
			{{section name=sec1 loop=$contacts}}
			  {{if !isset($contacts[sec1].selectRow) AND (!isset($contacts[sec1].hidden) OR !$contacts[sec1].hidden)}}
				<div class="fitem">
					<label>{{$contacts[sec1].label}}</label>
					<input name="{{$contacts[sec1].field}}"
                            {{if isset($contacts[sec1].required)}} required="true"{{/if}}
                            {{if isset($contacts[sec1].checkbox)}} class="easyui-checkbox" type="checkbox" checked value="true"
                            {{else}}
							   {{if isset($contacts[sec1].type)}}
									{{if $contacts[sec1].type == "number"}}
						   				class="easyui-numberbox" value="100" data-options="min:0,precision:0"
									{{else}}
									   class="easyui-textbox"
									{{/if}}
								{{else}}
						   			class="easyui-textbox"
								{{/if}}

                            {{/if}}
                    >
				</div>
				{{/if}}
			{{/section}}

		</form>
	</div>
	<div id="dlg-buttons{{$tablegrid}}">
		<a href="javascript:void(0)" class="easyui-linkbutton c6" iconCls="icon-ok" onclick="save{{$tablegrid}}()" style="width:90px">Save</a>
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="javascript:$('#dlg{{$tablegrid}}').dialog('close')" style="width:90px">Cancel</a>
	</div>


	<script type="text/javascript">
		$('#dg{{$tablegrid}}').datagrid({
			options:[[
				{rowHeight:100}
			]],
			autoRowHeight:true
		});
		var url;
		function newRecord{{$tablegrid}}(){
			$('#dlg{{$tablegrid}}').dialog('open').dialog('setTitle','New {{$item}}');
			$('#fm{{$tablegrid}}').form('clear');
			url = {{$newUrl}};
		}
		function editRecord{{$tablegrid}}(){
			var row = $('#dg{{$tablegrid}}').datagrid('getSelected');
			if (row){
				$('#dlg{{$tablegrid}}').dialog('open').dialog('setTitle','Edit {{$item}}');
				$('#fm{{$tablegrid}}').form('load',row);
				url = {{$updateUrl}};
			}
		}

        function save{{$tablegrid}}(){
            $('#fm{{$tablegrid}}').form('submit',{
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
                        $('#dlg{{$tablegrid}}').dialog('close');		// close the dialog
                        $('#dg{{$tablegrid}}').datagrid('reload');	// reload the user data
                    }
                }
            });
        }

        function deleteRecord{{$tablegrid}}(){
			var row = $('#dg{{$tablegrid}}').datagrid('getSelected');
			if (row){
				$.messager.confirm('Confirm','Are you sure you want to delete this {{$item}}?',function(r){
                    if (r){
                        $.ajax({
                            type:    "POST",
                            url:     "{{$deleteUrl}}",
                            data:    {id: row.{{$id}}},
                            success: function(data) {
                                $('#dg{{$tablegrid}}').datagrid('reload');
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
        function checkboxFormatter(val,row){
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
