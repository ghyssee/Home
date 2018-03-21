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
            if (obj.message) {
                alert(obj.message);
            }
            else {
                alert(data);
            }
        }
    });
}

function submitFormMessage(formId, url, messageId) {
    $('#' + formId).form('submit', {
        url: url,
        onSubmit: function () {
            // do some check
            // return false to prevent submit;
        },
        success: function (data) {
            var obj = JSON.parse(data);
            if (obj) {
                $('#' + messageId).html(obj.message);
            }
            else {
                alert("Problem with the submit of this form: " + data);
            }
        }
    });
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

function newRecord($dialogId, $formId, $title, $url){
    $('#' + $dialogId).dialog('open').dialog('setTitle', $title);
    $('#' + $formId).form('reset');
    url = $url;
}

function saveRecord(formId, dialogId, datagridId){
    $('#' + formId).form('submit',{
        url: url,
        onSubmit: function(){
            return $(this).form('validate');
        },
        success: function(result){
            var result = JSON.parse(result);
            if (result.errorMsg){
                $.messager.show({
                    title: 'Error',
                    msg: result.errorMsg
                });
            } else {
                $('#' + dialogId).dialog('close');		// close the dialog
                $('#' + datagridId).datagrid('reload');	// reload the user data
            }
        }
    });
}

function editRecord(datagridId, dialogId, formId, idField, title, editUrl){
    var row = $('#' + datagridId).datagrid('getSelected');
    if (row){
        $('#' + dialogId).dialog('open').dialog('setTitle',title);
        $('#' + formId).form('load',row);
        url = editUrl + '&id=' + row[idField];
    }
}

function deleteRecordV2(datagridId, title, idField, deleteUrl){
    var row = $('#' + datagridId).datagrid('getSelected');
    if (row){
        $.messager.confirm('Confirm','Are you sure you want to delete this ' + title + '?',function(r){
            if (r){
                $.ajax({
                    type:    "POST",
                    url:     deleteUrl,
                    data:    {id: row[idField]},
                    success: function(data) {
                        try {
                            var dataObj = JSON.parse(data);
                            if (!dataObj.success && dataObj.hasOwnProperty('errorMessage')) {
                                alert(dataObj.errorMessage);
                            }
                            $('#' + datagridId).datagrid('reload');
                        }
                        catch (ex){
                            alert(data);
                        }
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
