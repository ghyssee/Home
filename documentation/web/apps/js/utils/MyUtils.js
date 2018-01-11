function pad(number, length) {
    var str = '' + number;
    while (str.length < length) {
        str = '0' + str;
    }
    return str;
}

function formatStringYYYYMMDDToDate(strDate){
    var month = parseInt(strDate.substr(4,2))-1;
    var d = new Date(strDate.substr(0,4),
        month,
        strDate.substr(6,2),
        strDate.substr(8,2),
        strDate.substr(10,2),
        strDate.substr(12,2));
    return d;
}

function getDateYYYYMMDD(date){
    var d = new Date();
    if ((typeof date !== 'undefined') && date !== null){;
        d = date;
    }
    var curr_date = d.getDate();
    var curr_month = d.getMonth();
    curr_month++;
    var curr_year = d.getFullYear();
    return ("" + curr_year + pad(curr_month,2) + pad(curr_date,2));
}
