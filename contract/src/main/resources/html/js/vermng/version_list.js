function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#vermng_list_datagrid').datagrid({
		url:_servicePath+"versionMng/query",
		queryParams : $("#vermng_query_form").serializeJson(),
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
	if(!CheckSubmitted.submitRequest()){
        return false;
    }
	var verNo=$("#verNo").textbox("getValue");
    if(!verNo){
        easyuiTips("请输入版本号",'verNo','#vermng_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var verContent=$("#verContent").textbox("getValue");
    if(!verContent){
        easyuiTips("请输入版本内容",'verContent','#vermng_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var state=$("#state").textbox("getValue");
    if(!state){
        easyuiTips("请选择版本状态",'state','#vermng_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    if(verNo.length>100){
    	easyuiTips("版本号，超过最大长度100！",'verNo','#vermng_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    if(!$("#vermng_edit_form").form('validate')){
    	return false;
    }
	return true;
}
function addVersionInfo(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#vermng_edit_form").form("clear");
	 layer.open({
      	type: 1,
      	title: "版本新增",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100000,
      	btnAlign: 'c',
      	area: ['500px','600px'],
      	content: $("#vermng_edit_form"),
      	btn:["确定","取消"], 
      	yes:function(index){
      		if(checkForm()){
      			$.ajax({
      		        type: "post",
      		        url:_servicePath+"versionMng/save",
      		        data: $("#vermng_edit_form").serializeJson(),
      		        dataType: "json",
      		        success: function (data ,textStatus, jqXHR)
      		        {
      		        	if (data[0] == "000000") {
      		        		layer.alert("新增成功!");
    						$('#vermng_list_datagrid').datagrid("reload");
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
String.prototype.endWith=function(endStr){
	  var d=this.length-endStr.length;
	  return (d>=0&&this.lastIndexOf(endStr)==d);
	}
//上传apk文件
function uploadApk(){
	var load = null;
	var formData = new FormData();
	formData.append('file', $('#file')[0].files[0]);
	console.info(formData);
	var fileName=formData.get("file").name;
	var fileSize=formData.get("file").size;
	if(fileSize>52428800){
		layer.alert("文件太大");
		return
	}
	if(!fileName.endWith(".apk")){
		layer.alert("不支持文件类型,请上传apk文件");
		return
	}
	$.ajax({
		url:_servicePath+"versionMng/uploadApkFile",
	    type: 'POST',
	    cache: false,
	    timeout:1800000,
	    data: formData,
	    processData: false,
	    contentType: false,
	    beforeSend : function(XMLHttpRequest) {
			load = layer.load(2, {
				time : 1800000
			}); 
		}
	}).done(function(data) {
		if (load) {
			layer.close(load);
		}
		if(data && data.uploaded){
			$("#downloadUrl").textbox('setValue',data.url)
		}
	}).fail(function(data) { 
		if (load) {
			layer.close(load);
		}
		$.messager.alert('提示', "服务器发生错误!", 'info');
		});
}
function editMenu(index, row){
	if(!CheckSubmitted.submitRequest()){
		return;
	}
	$("#vermng_edit_form").form("clear").form("load",row);
	layer.open({
		type: 1,
		title: "版本修改",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:100000,
		area: ['500px','600px'],
		content: $("#vermng_edit_form"),
				btn:["确定","取消"], 
				yes:function(index){
					if(checkForm()){
						$.ajax({
		      		        type: "post",
		      		        url:_servicePath+"versionMng/update",
		      		        data: $("#vermng_edit_form").serializeJson(),
		      		        dataType: "json",
		      		        success: function (data ,textStatus, jqXHR)
		      		        {
		      		        	if (data[0] == "000000") {
		      		        		layer.alert("更新成功!");
		    						$('#vermng_list_datagrid').datagrid("reload");
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
    $('#vermng_query_form').form('reset');
}

submitForm();