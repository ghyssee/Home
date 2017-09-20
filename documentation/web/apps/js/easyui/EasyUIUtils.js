function clearFilter(datagridName){
        datagridName = "#" + datagridName;
		$(datagridName).datagrid('removeFilterRule');
        $(datagridName).datagrid('doFilter');
}

function removeDatagridRow(datagridName, errorMsg) {
    datagridName = "#" + datagridName;
    var selectedRow = $(datagridName).edatagrid("getSelected");
    if (selectedRow == null) {
        alert(errorMsg);
    }
    else {
        var rowIndex = $(datagridName).edatagrid("getRowIndex", selectedRow);
        $(datagridName).edatagrid("deleteRow", rowIndex);
    }
}

function refreshCombo(datagridName){
    datagridName = "#" + datagridName;
    $(datagridName).combobox('reload');
}

function saveObject(object, url, callback){

    var tmp = $.post(url, { config : JSON.stringify(object)}, function(data2){
            if (data2.success){
                if (data2.message){
                    alert(data2.message);
                }
                if (callback){
                    callback();
                }
            }
            else {
                if (data2.errorMsg) {
                    alert(data2.errorMsg);
                }
                else {
                    alert("Data Not Saved!");
                }
            }
        },'json')
        .done(function() {
            //alert( "second success" );
        })
        .fail(function(response) {
            alert( "error: "  + response.responseText);
        })
        .always(function() {
            //alert( "finished" );
        });
    tmp.always(function() {
        //alert( "second finished" );
    });

}

function save2(object, url, callback){

    var tmp = $.post(url, { config : JSON.stringify(object)}, function(data2){
            if (data2.success){
                //clearArtists();
                alert("Multi Artist Config Item successfully saved");
                //alert(JSON.stringify(data2, null, 4));
                //$('#dgMultiArtistList').datagrid('reload');
            }
            else {
                if (data2.errorMsg) {
                    alert(data2.errorMsg);
                }
                else {
                    alert("Data Not Saved!");
                }
            }
        },'json')
        .done(function() {
            //alert( "second success" );
        })
        .fail(function(response) {
            alert( "Fail: "  + response.responseText);
        })
        .always(function() {
            //alert( "finished" );
        });
    tmp.always(function() {
        //alert( "second finished" );
    });
}

