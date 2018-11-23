function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#cms_category_list_datagrid').datagrid({
		url:_servicePath+"category/query",
		queryParams : $("#cms_category_query_form").serializeJson(),
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
			editMenu(index, row);
		}
	}).datagrid('getPager').pagination({
        buttons:[
            {
                iconCls:'icon-add',
                text:'创建栏目',
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
	var categoryKey = $("#categoryKey").textbox('getValue');
	var categoryName = $("#categoryName").textbox('getValue');
	if($.trim(categoryKey)==''){
		layer.alert('请输入栏目key');
		CheckSubmitted.requestComplete();
		return false;
	}
	if($.trim(categoryName)==''){
		layer.alert('请输入栏目名称');
		CheckSubmitted.requestComplete();
		return false;
	}
	return true;
}
function addMenu(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#cms_category_edit_form").form("clear");
	$("input[name=state]").get(0).checked=true;
	$("input[name=display]").get(0).checked=true;
	 layer.open({
      	type: 1,
      	title: "栏目新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	btnAlign: 'c',
      	area: ['500px','400px'],
      	content: $("#cms_category_edit_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			$.ajax({
      		        type: "post",
      		        url:_servicePath+"category/save",
      		        data: $("#cms_category_edit_form").serializeJson(),
      		        dataType: "json",
      		        success: function (data ,textStatus, jqXHR)
      		        {
      		        	if (data[0] == "000000") {
      		        		layer.alert("新增成功!");
    						$('#cms_category_list_datagrid').datagrid("reload");
    						layer.close(index);
    					}else if(data[0] == "10102"){
    						layer.alert("该栏目KEY对应的记录已经存在，不允许重复!\n");
    					}else{
    						layer.alert("新增失败!\n"+data.error);
    					}
      		        },
      		        error:function (XMLHttpRequest, textStatus, errorThrown) {      
      		        	layer.alert("新增失败!\n"+ XMLHttpRequest.responseJSON.exception);
      		        }
      		     });
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
	$("#cms_category_edit_form").form("clear").form("load",row);
	layer.open({
		type: 1,
		title: "栏目编辑",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:100000,
		area: ['500px','400px'],
		content: $("#cms_category_edit_form"),
				btn:["确定","取消"], 
				yes:function(index){
					if(checkForm()){
						$.ajax({
		      		        type: "post",
		      		        url:_servicePath+"category/update",
		      		        data: $("#cms_category_edit_form").serializeJson(),
		      		        dataType: "json",
		      		        success: function (data ,textStatus, jqXHR)
		      		        {
		      		        	if (data[0] == "000000") {
		      		        		layer.alert("更新成功!");
		    						$('#cms_category_list_datagrid').datagrid("reload");
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
function clearForm(){
    $('#cms_category_query_form').form('reset');
}
submitForm();