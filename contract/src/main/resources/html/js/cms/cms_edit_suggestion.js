function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#cms_suggestion_list_datagrid').datagrid({
		url:_servicePath+"suggestion/query",
		queryParams : $("#cms_suggestion_query_form").serializeJson(),
		loadFilter: function(data){
			var resp={}
	    	if(data[0] =='000000'){
	    		resp.total=data[2].total;
	    		resp.rows=data[2].list;
	    		resp.code=data[0];
	    	}
            return resp;
        },
		fitColumns : true,
		singleSelect : true,
		onDblClickRow : function(index, row) {
			showDetail(index, row);
		}
	}).datagrid('getPager').pagination({
        loading : false
    });
}
function checkForm(){
	if(!CheckSubmitted.submitRequest()){
        return false;
    }
	return true;
}

function showDetail(index, row){
	if(!CheckSubmitted.submitRequest()){
		return;
	}
	$("#cms_suggestion_edit_form").form("clear").form("load",row);
	if(row.images){
		var images = row.images.split(",",6);
		$("#images").empty();
		for(j = 0,len=images.length; j < len; j++) {
		   $("#images").append('<img src="'+images[j]+'" style="width:150px;margin-left:30px;margin-bottom: 10px;" onclick="photos()"/>');
		}
	}
	layer.open({
		type: 1,
		title: "详细信息",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:100000,
		area: ['500px','600px'],
		content: $("#cms_suggestion_edit_form")
	});
}
function photos(){
	layer.photos({
		  photos: '#images'
	}); 
}
function clearForm(){
    $('#cms_suggestion_query_form').form('reset');
}
submitForm();