<div class="easyui-layout" style="width:100%;height:100%;">

    <div data-options="region:'west',collapsible:false" title="Lines" style="width:50%;">West
        <table id="dgFeedback" class="easyui-datagrid" style="width:400px;height:150px"
               title="Unordered Artist Group"
               data-options="fitColumns:true,singleSelect:true">
            <thead>
            <tr>
                <th data-options="field:'id',hidden:true">Id</th>
                <th data-options="field:'name',width:100">Artist</th>
            </tr>
            </thead>
        </table>
    </div>
    <div data-options="region:'east',collapsible:false" title="Result" style="width:50%;">East

        <form id="ff" method="post" action="MP3PrettifierMultiArtistAction.php?method=batch" >
            <div style="margin-bottom:20px">
                <input name="multiArtistList" class="easyui-textbox" label="Description:"
                       labelPosition="top" multiline="true"
                       value="This TextBox will allow the user to enter multiple lines of text."
                       style="width:100%;height:180px">
            </div>
        </form>
        <div style="text-align:center;padding:5px 0">
            <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()" style="width:80px">Submit</a>
            <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()" style="width:80px">Clear</a>
        </div>
        <script type="text/javascript">
            $(function(){
                $('#ff').form({
                    success:function(data){
                        $.messager.alert('Info', data, 'info');
                    }
                });
            });
        </script>
        <script>
            function submitForm(){
                $('#ff').form('submit');
            }
            function clearForm(){
                $('#ff').form('clear');
            }
        </script>
    </div>
</div>
