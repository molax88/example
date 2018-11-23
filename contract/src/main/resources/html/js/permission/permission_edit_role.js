function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	var datagrid=$('#permission_role_list_datagrid').datagrid({
		queryParams : {
			"serviceCode" : "authRole",
			"serviceType" : "query",
			"encodeKey" : "",
			"accessToken" : "DF34F",
			"returnUrl" : null,
			"param" : $("#permission_query_role_form").serializeJson()
		},
		fitColumns : true,
		singleSelect : true,
		onDblClickRow : function(index, row) {
			editRole(index, row);
		}
	}).datagrid('getPager').pagination({
        buttons:[
            {
                iconCls:'icon-add',
                text:'创建角色',
                handler:function(){
                    addRole();
                }
            }
        ],
        loading : false
    });
}
$.parser.onComplete=function(){
	//获取所有菜单项
	$.ajaxPostReq({
		"serviceCode":"authResource",
		"serviceType":"getAllResource",
		"param":{}
	},function(data){
		if(data&&data.code=="40000"){
			toLogout();
		}
		if(data&&data.code=="200"){
			if(data.msg&&data.msg.rows){
				$("#resourceTreeUpdate").combotree("loadData",data.msg.rows);
			}
		}
	});
	$.parser.onComplete=function(){}
};
//初始化树形菜单
function initResourceTreeUpdate(data){
	var rows = data.msg.rows;
	if(rows&&rows.length>0){
		var ids=[];
		rows.forEach(function(row,i){
			ids.push(row.resourceId);
		});
		$("#resourceTreeUpdate").combotree('setValues',ids)
	}
}
function editNewRole(index, row){
	layer.open({
      	type: 1,
      	title: "角色编辑",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	area: ['500px','280px'],
      	content: $("#permission_update_role_form"),
      	btn:["更新","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			var nodes = $('#resourceTreeUpdate').combotree('tree').tree('getChecked');
                var resource=[];
                nodes.forEach(function(node,i){
                    resource.push(node.id);
                });
      	        var param=$.extend({},{"resource":resource},$("#permission_update_role_form").serializeJson());
				$.ajaxPostReq({
  	        		"serviceCode":"authRole",
  	        		"serviceType":"update",
  	        		"param":param
				}, function(data) {
					if (data && data.code == "40000") {
						toLogout();
					}
					if (data && data.code == "200") {
						$('#permission_role_list_datagrid').datagrid("reload");
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
function editRole(index, row){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#permission_update_role_form").form("clear").form("load",row);
	$.ajaxPostReq({
		"serviceCode" : "authResource",
		"serviceType" : "queryByRoleId",
		"param" : {
			roleId : row.id
		}
	}, function(data) {
		if (data && data.code == "40000") {
			toLogout();
		}
		if (data && data.code == "200") {
			initResourceTreeUpdate(data)
			editNewRole(index, row);
		}
	});
}
function addRole(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#permission_update_role_form").form("clear");
	  layer.open({
      	type: 1,
      	title: "角色新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	area: ['500px','280px'],
      	content: $("#permission_update_role_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			var nodes = $('#resourceTreeUpdate').combotree('tree').tree('getChecked');
      	        var resource=[];
      	        nodes.forEach(function(node,i){
      	        	resource.push(node.id);
      	        });
      	        var param=$.extend({},{"resource":resource},$("#permission_update_role_form").serializeJson());
				$.ajaxPostReq({
					"serviceCode":"authRole",
	        		"serviceType":"create",
  	        		"param":param
				}, function(data) {
					if (data && data.code == "40000") {
						toLogout();
					}
					if (data && data.code == "200") {
						$('#permission_role_list_datagrid').datagrid("reload");
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
function checkForm(){
	if(!CheckSubmitted.submitRequest()){
        return false;
    }
	return true;
}
function clearForm(){
    $('#permission_query_role_form').form('reset');
}
submitForm();