function clearFilter(datagridName){
        datagridName = "#" + datagridName;
		$(datagridName).datagrid('removeFilterRule');
        $(datagridName).datagrid('doFilter');
}