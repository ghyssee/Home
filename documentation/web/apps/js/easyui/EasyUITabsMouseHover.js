    $(function(){
        var tabs = $('#tt').tabs().tabs('tabs');
        for(var i=0; i<tabs.length; i++){
            tabs[i].panel('options').tab.unbind().bind('mouseenter',{index:i},function(e){
                $('#tt').tabs('select', e.data.index);
            });
        }
    });
