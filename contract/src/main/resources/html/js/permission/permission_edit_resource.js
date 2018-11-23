function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#permission_resource_list_datagrid').datagrid({
		queryParams : {
			"serviceCode" : "authResource",
			"serviceType" : "query",
			"encodeKey" : "",
			"accessToken" : "DF34F",
			"returnUrl" : null,
			"param" : $("#permission_query_resource_form").serializeJson()
		},
		fitColumns : true,
		singleSelect : true,
		onDblClickRow : function(index, row) {
			editMenu(index, row);
		}
	}).datagrid('getPager').pagination({
        buttons:[
            {
                iconCls:'icon-add',
                text:'创建菜单',
                handler:function(){
                    addMenu();
                }
            }
        ],
        loading : false
    });
}
function checkForm(){
	if(!CheckSubmitted.submitRequest()){
        return false;
    }

	return true;
}
function addMenu(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#permission_edit_resource_form").form("clear");
	  layer.open({
      	type: 1,
      	title: "菜单新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	area: ['500px','340px'],
      	content: $("#permission_edit_resource_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
				$.ajaxPostReq({
					"serviceCode":"authResource",
	        		"serviceType":"create",
  	        		"param": $("#permission_edit_resource_form").serializeJson()
				}, function(data) {
					if (data && data.code == "40000") {
						toLogout();
					}
					if (data && data.code == "200") {
						$('#permission_resource_list_datagrid').datagrid("reload");
						layer.close(index);
					}
				});
      		}else{

			}
  		}, 
  		no:function(index){
  			layer.close(index);
  		}
      });
}
function editMenu(index, row){
	if(!CheckSubmitted.submitRequest()){
		return;
	}
	$("#permission_edit_resource_form").form("clear").form("load",row);
	layer.open({
		type: 1,
		title: "菜单编辑",
		shadeClose: true,
		shade: 0.8,
		zIndex:100000,
		area: ['500px','340px'],
		content: $("#permission_edit_resource_form"),
				btn:["确定","取消"], 
				yes:function(index){
					if(checkForm()){
						$.ajaxPostReq({
							"serviceCode":"authResource",
							"serviceType":"update",
							"param": $("#permission_edit_resource_form").serializeJson()
						}, function(data) {
							if (data && data.code == "40000") {
								toLogout();
							}
							if (data && data.code == "200") {
								$('#permission_resource_list_datagrid').datagrid("reload");
								layer.close(index);
							}
						});
					}
				}, 
				no:function(index){
					layer.close(index);
				}
	});
}
function clearForm(){
    $('#permission_query_resource_form').form('reset');
}
submitForm();