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

function openUrl(url) {
    var win = window.open(url, '_blank');
    win.focus();
}

function submitForm(formId, url) {
    $('#' + formId).form('submit', {
        url: url,
        onSubmit: function () {
// do some check
// return false to prevent submit;
        },
        success: function (data) {
            var obj = JSON.parse(data);
            alert(obj.message);
        }
    });
}

function deleteRecord(name, dg, url, idField){
    dg = '#' + dg;
    var row = $(dg).datagrid('getSelected');
    if (row){
        $.messager.confirm('Confirm','Are you sure you want to delete this ' + name + '?',function(r){
            if (r){
                $.ajax({
                    type:    "POST",
                    url:     url,
                    data:    {id: row[idField]},
                    success: function(data) {
                        var dataObj = JSON.parse(data);
                        if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')){
                            alert(dataObj.errorMessage);
                        }
                        $(dg).datagrid('reload');
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

function getSwitchValue(id){
    id = '#' + id;
    var checked = $(id).switchbutton('options').checked;
    return checked;
}

function checkboxFormatter(val,row,index){

    if (val== 1) {
        return "√";
    }
    else {
        return "";
    }
}



