function refreshDataGridProfile(datagrid, profile){
    if (profile){
        // do nothing
    }
    else {
        profile = getProfile();
    }
    var obj = {profile:profile};
    $('#' + datagrid).datagrid('reload', obj);
    return false;
}
