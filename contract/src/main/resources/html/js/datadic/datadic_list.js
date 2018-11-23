function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#datadic_list_datagrid').datagrid({
		url:_servicePath+"dataDicMng/query",
		queryParams : $("#datadic_query_form").serializeJson(),
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
                text:'新增',
                handler:function(){
                    addVersionInfo();
                }
            }
        ],
        loading : false
    });
}
function checkForm(){
	
	var dataTypeNo=$("#dataTypeNo").textbox("getValue");
    if(!dataTypeNo){
        easyuiTips("请输字典类型编号",'dataTypeNo','#datadic_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var dataNo=$("#dataNo").textbox("getValue");
    if(!dataNo){
        easyuiTips("请输入字典编号",'dataNo','#datadic_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var state=$("#state").textbox("getValue");
    if(!state){
        easyuiTips("请选择状态",'state','#datadic_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    //if(verNo.length>100){
    	//easyuiTips("版本号，超过最大长度100！",'verNo','#datadic_edit_form');
        //CheckSubmitted.requestComplete();
        //return false;
    //}
    if(!$("#datadic_edit_form").form('validate')){
    	return false;
    }
    if(!CheckSubmitted.submitRequest()){
        return false;
    }
	return true;
}
function addVersionInfo(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#datadic_edit_form").form("clear");
	 layer.open({
      	type: 1,
      	title: "数据字典新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	btnAlign: 'c',
      	area: ['550px','400px'],
      	content: $("#datadic_edit_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			$.ajax({
      		        type: "post",
      		        url:_servicePath+"dataDicMng/save",
      		        data: $("#datadic_edit_form").serializeJson(),
      		        dataType: "json",
      		        success: function (data ,textStatus, jqXHR)
      		        {
      		        	if (data[0] == "000000") {
      		        		layer.alert("新增成功!");
    						$('#datadic_list_datagrid').datagrid("reload");
    						layer.close(index);
    					}else{
    						layer.alert("新增失败!\n"+data.error);
    					}
      		        },
      		        error:function (XMLHttpRequest, textStatus, errorThrown) {   
      		        	//console.log("----------textStatus:"+textStatus);
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
	$("#datadic_edit_form").form("clear").form("load",row);
	layer.open({
		type: 1,
		title: "数据字典修改",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:100000,
		area: ['550px','400px'],
		content: $("#datadic_edit_form"),
				btn:["确定","取消"], 
				yes:function(index){
					if(checkForm()){
						$.ajax({
		      		        type: "post",
		      		        url:_servicePath+"dataDicMng/update",
		      		        data: $("#datadic_edit_form").serializeJson(),
		      		        dataType: "json",
		      		        success: function (data ,textStatus, jqXHR)
		      		        {
		      		        	if (data[0] == "000000") {
		      		        		layer.alert("更新成功!");
		    						$('#datadic_list_datagrid').datagrid("reload");
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
    $('#datadic_query_form').form('reset');
}

submitForm();