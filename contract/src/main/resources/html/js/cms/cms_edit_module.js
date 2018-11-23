function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	var datagrid=$('#cms_module_list_datagrid').datagrid({
		url:_servicePath+"module/query",
		queryParams: $("#cms_query_module_form").serializeJson(),
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
			editModule(index, row);
		}
	}).datagrid('getPager').pagination({
        buttons:[
            {
                iconCls:'icon-add',
                text:'创建模块',
                handler:function(){
                    addModule();
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
		"serviceType":"getAllCateTree",
		"param":{}
	},function(data){
		if(data&&data.code=="40000"){
			toLogout();
		}
		if(data&&data.code=="200"){
			if(data.msg&&data.msg.rows){
				//console.log("data.msg.rows:"+data.msg.rows);
				$("#categoryTreeUpdate").combotree("loadData",data.msg.rows);
			}
		}
	});
	$.parser.onComplete=function(){}
};

/*function updateModule(index, row){
	layer.open({
      	type: 1,
      	title: "角色编辑",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	area: ['500px','280px'],
      	content: $("#cms_update_module_form"),
      	btn:["更新","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			var nodes = $('#categoryTreeUpdate').combotree('tree').tree('getChecked');
                var category=[];
                nodes.forEach(function(node,i){
                	category.push(node.id);
                });
      	        var param=$.extend({},{"category":resource},$("#cms_update_module_form").serializeJson());
      	      $.ajax({
    		        type: "post",
    		        url:_servicePath+"module/save",
    		        data: $("#cms_update_module_form").serializeJson(),
    		        dataType: "json",
    		        success: function (data ,textStatus, jqXHR)
    		        {
    		        	if (data[0] == "000000") {
    		        		layer.alert("修改成功!");
	  						$('#cms_module_list_datagrid').datagrid("reload");
	  						layer.close(index);
	  					}else{
	  						layer.alert("修改失败!\n"+data.error);
	  					}
    		        },
    		        error:function (XMLHttpRequest, textStatus, errorThrown) {      
    		        	layer.alert("修改失败!\n"+ XMLHttpRequest.responseJSON.exception);
    		        }
    		     });
      		}
  		}, 
  		no:function(index){
  			layer.close(index);
  		}
      });
}*/
//$.parser.onComplete=function(){
//$.parser.onComplete=function(){}
//};
function initCategory(){
	//初始化栏目
	$.ajax({
	        type: "post",
	        url:_servicePath+"category/tree",
	        data: {"page":1,"rows":100},
	        dataType: "json",
	        success: function (data ,textStatus, jqXHR){
	        	console.log("data[0]:"+data[0]+"data[2]:"+data[2]);
	        	if (data[0] == "000000") {
	        		console.log("000000:"+data[0]);
	        		console.log("json:"+JSON.stringify(data[2]));
	        		//TODO 初始化树形菜单
	        		//var treeData = toTreeData(data[2]);
	        		//$("#categoryTreeUpdate").combotree("loadData",treeData);
	        		//$("#categoryTreeUpdate").combotree("loadData",JSON.stringify(data[2]));
	        		//var dataStr=JSON.stringify(data[2]);
	        		$('#categoryTreeUpdate').combotree('loadData',data[2]);
	        		//$('#categoryTreeUpdate').combotree('loadData', [{"id":28,"text":"系统栏目","state":"closed"},{"id":36,"text":"人工客服","state":"closed"},{"id":2,"text":"新闻资讯","state":"closed"},{"id":3,"text":"系统栏目","state":"closed"},{"id":1,"text":"帮助中心","state":"closed"}]);
	        		//$('#categoryTreeUpdate').combotree({
	        			//url:'tree_data.json',
	        			//multiple: true,
	        			//cascadeCheck: false
	        			//onlyLeafCheck: true
	        		//});
				}else{
					layer.alert("获取栏目失败!",data.error);
				}
	        },
	        error:function (XMLHttpRequest, textStatus, errorThrown) {      
	        	layer.alert("获取栏目失败!\n"+ XMLHttpRequest.responseJSON.exception);
	        }
	});
}
function editModule(index, row){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	//initCategory();
	$("#cms_update_module_form").form("clear").form("load",row);
	$("#categoryTreeUpdate").combotree('setValues',[row.categoryId]);
	layer.open({
		type: 1,
		title: "菜单编辑",
		shadeClose: true,
		shade: 0.8,
		zIndex:100000,
		btnAlign: 'c',
		area: ['400px','400px'],
		content: $("#cms_update_module_form"),
				btn:["确定","取消"], 
				yes:function(index){
					if(checkForm()){
						$.ajax({
		      		        type: "post",
		      		        url:_servicePath+"module/update",
		      		        data: $("#cms_update_module_form").serializeJson(),
		      		        dataType: "json",
		      		        success: function (data ,textStatus, jqXHR)
		      		        {
		      		        	if (data[0] == "000000") {
		      		        		layer.alert("更新成功!");
		    						$('#cms_module_list_datagrid').datagrid("reload");
		    						layer.close(index);
		    					}else{
		    						layer.alert("更新失败!",data.error);
		    					}
		      		        },
		      		        error:function (XMLHttpRequest, textStatus, errorThrown) {      
		      		        	layer.alert("更新失败!\n"+ XMLHttpRequest.responseJSON.exception);
		      		        }
		      		     });
						
					}
				}, 
				no:function(index){
					layer.close(index);
				}
	});
}
function addModule(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#cms_update_module_form").form("clear");
	//initCategory();
	$("input[name=state]").get(0).checked=true;
	$("input[name=display]").get(0).checked=true;
	$('#categoryTreeUpdate').combotree('setValue', { id: 0, text: "请选择栏目" });
	layer.open({
      	type: 1,
      	title: "模块新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	btnAlign: 'c',
		area: ['400px','400px'],
      	content: $("#cms_update_module_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
      	        $.ajax({
    		        type: "post",
    		        url:_servicePath+"module/save",
    		        data: $("#cms_update_module_form").serializeJson(),
    		        dataType: "json",
    		        success: function (data ,textStatus, jqXHR)
    		        {
    		        	if (data[0] == "000000") {
    		        		layer.alert("新增成功!");
	  						$('#cms_module_list_datagrid').datagrid("reload");
	  						layer.close(index);
	  					}else{
	  						debugger
	  						layer.alert("新增失败!\r\n"+data[1]);
	  					}
    		        },
    		        error:function (XMLHttpRequest, textStatus, errorThrown) {      
    		        	layer.alert("新增失败!\r\n"+ XMLHttpRequest.responseJSON.exception);
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
	var moduleName=$("#moduleName").textbox("getValue");
    if(!moduleName){
        easyuiTips("请输入模块名称",'moduleName','#cms_update_module_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var moduleKey=$("#moduleKey").textbox("getValue");
    if(!moduleKey){
        easyuiTips("请输入模块key",'moduleKey','#cms_update_module_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    debugger
    var categoryTreeUpdate=$("#categoryTreeUpdate").combotree("getValue");
    if(categoryTreeUpdate==0){
        easyuiTips("请选择栏目",'categoryId','#cms_update_module_form');
        CheckSubmitted.requestComplete();
        return false;
    }
	return true;
}
function clearForm(){
    $('#cms_query_module_form').form('reset');
}
submitForm();
