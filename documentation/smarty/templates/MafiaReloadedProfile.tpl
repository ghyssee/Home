<label for="profile" title="Profile">Profile</label>
<input id="profile" class="easyui-combobox" name="profile"
                data-options="valueField:'id',
                                                width:200,
                                                limitToList: true,
                                                textField:'name',
                                                onChange: function(row){
                                                    onProfileChange(row);
                                                },
                                                url:'ProfileAction.php?method=getProfiles'
                                    ">
<script type="text/javascript">
    function getProfile(){
        var _value = $("#profile").combobox('getValue');
        return _value;
    }
    function onProfileChange(){
        var profileId = getProfile();
        var _value = $("#profile").combobox('getText');
        var obj = {profile:profileId};
        {{if isset($datagrid)}}
        $('#{{$datagrid}}').datagrid('reload', obj);
        {{/if}}
        {{if isset($layout)}}
        $('#{{$layout}}').layout('panel', 'south').panel('setTitle', 'Profile: ' + profileId + " " + _value);
        {{/if}}
    }
</script>
