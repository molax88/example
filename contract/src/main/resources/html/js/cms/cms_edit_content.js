function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$('#cms_article_list_datagrid').datagrid({
		url:_servicePath+"article/query",
		queryParams : $("#cms_article_query_form").serializeJson(),
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
                text:'发布内容',
                handler:function(){
                    addMenu();
                }
            }
        ],
        loading : false
    });
}
function checkForm(){
	debugger
	var title=$("#title").val();
    if(!title){
        easyuiTips("请输入标题",'title','#cms_article_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var moduleId=$('#moduleId').combobox('getValues');
    if(!moduleId){
        easyuiTips("请选择所属模块",'moduleId','#cms_article_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var languageType=$("#languageType").combobox('getValues');
    if(!languageType){
        easyuiTips("请选择语言类型",'languageType','#cms_article_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var status=$('#status').combobox('getValues');
    if(!status){
        easyuiTips("请选择前端是否显示",'status','#cms_article_edit_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    var publishTime=$("#publishTime").val();
    if(!DateFormatter.DateISO(publishTime, 'yyyy-MM-dd HH:mm:ss')){
    	 easyuiTips("发布时间格式错误,正确格式：yyyy-MM-dd HH:mm:ss",'publishTime','#cms_article_edit_form');
         CheckSubmitted.requestComplete();
         return false;
    }
    
	if(!CheckSubmitted.submitRequest()){
        return false;
    }
	return true;
}
function addMenu(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	LoadCk(); //一定要先加载ckeditor
	$("#cms_article_edit_form").form("clear");
	imgSrc = '';
	$("#image").attr("src",imgSrc);
	$('#moduleId').combobox({
		url:_servicePath+"article/combox",
		valueField:'id',
		textField:'name',
		onLoadSuccess:function(){
			$('#qModuleKey').combobox('select', -1);
		},
		loadFilter:function(data){
			if(data[0]=='000000'){
//				data[2].push({
//					"id":-1,
//					"name":"请选择模块"
//				});
				return data[2];
			}else{
				return{
					"id":-1,
					"name":"请选择模块"
				}
			}
		}
	});
	var languageType111 = $('#languageType').combobox('getData');
	 $("#languageType ").combobox('select',languageType111[0].value);
	 layer.open({
		type: 1,
		title: "发布内容",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:5000, //不能太大，会遮挡ckeditor
		area: ['1160px','700px'],
		content: $("#cms_article_edit_form"),
		btn:["发布","取消"],
      	yes:function(index){
			if(checkForm()){
				var param=$.extend({},$("#cms_article_edit_form").serializeJson(),{"content": CKEDITOR.instances.content.getData()});
				$.ajax({
      		        type: "post",
      		        url:_servicePath+"article/save",
      		        data: param,
      		        processData:false,
                    contentType:false,
      		        dataType: "json",
      		        success: function (data ,textStatus, jqXHR)
      		        {
      		        	if (data[0] == "000000") {
      		        		layer.alert("发布成功!");
    						$('#cms_article_list_datagrid').datagrid("reload");
    						layer.close(index);
    					}else{
    						layer.alert("发布失败!",data.error);
    					}
      		        },
      		        error:function (XMLHttpRequest, textStatus, errorThrown) {      
      		        	layer.alert("发布失败!\n"+ XMLHttpRequest.responseJSON.exception);
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
	LoadCk(); //一定要先加载ckeditor
	$("#cms_article_edit_form").form("clear").form("load",row);
	imgSrc = row.images;
	console.log(row);
	$("#image").attr("src",imgSrc);
	$('#moduleId').combobox({
		url:_servicePath+"article/combox",
	    valueField:'id',
	    textField:'name',
	    loadFilter:function(data){
	    	if(data[0]=='000000'){
	    		return data[2];
	    	}else{
	    		return{
	    			"id":"",
	    			"name":"全部"
	    		}
	    	}
	    }
	});
	$('#moduleId').combobox('setValue', row.moduleId);
	layer.open({
		type: 1,
		title: "内容编辑",
		shadeClose: true,
		shade: 0.8,
		btnAlign: 'c',
		zIndex:5000, //不能太大，会遮挡ckeditor
		area: ['1160px','700px'],
		content: $("#cms_article_edit_form"),
		btn:["保存","取消"], 
		yes:function(index){
			var param=$.extend({},$("#cms_article_edit_form").serializeJson(),{"content": CKEDITOR.instances.content.getData()});
			if(checkForm()){
				$.ajax({
      		        type: "post",
      		        url:_servicePath+"article/update",
      		        data: param,
      		        processData:false,
                    contentType:false,
      		        dataType: "json",
      		        success: function (data ,textStatus, jqXHR)
      		        {
      		        	if (data[0] == "000000") {
      		        		layer.alert("更新成功!");
    						$('#cms_article_list_datagrid').datagrid("reload");
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
    $('#cms_article_query_form').form('reset');
}
//初始化ckeditor
function LoadCk() {
	$("#ck").empty();
	$("#ck").append('<textarea id="content" name="content"></textarea>');
    //加载CKeditor
    //判定 Content 是否存在
    var editor;
    if (!CKEDITOR.instances['content']) {
        editor = CKEDITOR.replace('content');
    } else {
    	CKEDITOR.remove(CKEDITOR.instances['content']);
        CKEDITOR.replace('content');
    }
}
//异步上传图片
function upload(){
	var formData = new FormData();
	formData.append('file', $('#file')[0].files[0]);
	if(!$('#file')[0].files[0]){
		return;
	}
	$.ajax({
		url:_servicePath+"article/updateMasterImage",
	    type: 'POST',
	    cache: false,
	    data: formData,
	    processData: false,
	    timeout:60000,
	    contentType: false
	}).done(function(data) {
		if(data && data.uploaded && data.url){
			document.getElementById('preview').innerHTML='<img id="previewImage" src="'+data.url+'" style="width:300px;height:150px;"/>'
					+ '<div style="margin-left: 301px;margin-top: -60px" class="btn btn-danger btn-sm" onClick="reback();"><i class="ace-icon fa fa-reply icon-only"></i></div>'
					+'<div style="margin-left: 301px;margin-top: -29px;width:39px;" class="btn btn-danger btn-sm" onClick="reset();"><i class="ace-icon fa fa-trash-o icon-only"></i></div>';
			imgSrc = $("#images");
			$("#images").val(data.url);
		}
	}).fail(function(data) { $.messager.alert('提示', "服务器发生错误!", 'info');	});
}
//取消图片修改
var imgSrc;
function reback(){
//	document.getElementById('preview').innerHTML = '<img src="" style="margin-left: 100px;margin-top: 20px"/>'
//	$("#images").val(imgSrc);
	//http://localhost:58081/admin/html/images/add.png
	document.getElementById('preview').innerHTML = '<img id="previewImage" src="" style="margin-left: 100px;margin-top: 20px;"/>'
		+'<div style="margin-left: 301px;margin-top: 100px" class="btn btn-danger btn-sm" onClick="reset();"><i class="ace-icon fa fa-trash-o icon-only"></i></div>';
	$("#images").val(imgSrc);
}
function reset(){
	debugger
	document.getElementById('preview').innerHTML = '<img id="previewImage" src="" style="margin-left: 100px;margin-top: 20px;"/>'
		+'<div style="margin-left: 301px;margin-top: 100px" class="btn btn-danger btn-sm" onClick="reset();"><i class="ace-icon fa fa-trash-o icon-only"></i></div>';
	$("#images").val("");
	$("#image").attr("src","");
	//http://localhost:58081/admin/html/images/add.png
}


$(document).ready(function () {
	$('#qModuleKey').combobox({
		url:_servicePath+"article/combox",
		valueField:'id',
		textField:'name',
		onLoadSuccess:function(){
			$('#qModuleKey').combobox('select', -1);
		},
		loadFilter:function(data){
			console.info(data);
			if(data[0]=='000000'){
				data[2].push({
					"id":-1,
					"name":"全部"
				})
				return data[2];
			}else{
				return{
					"id":-1,
					"name":"全部"
				}
			}
		}
	});
	submitForm();
});

var DateFormatter = {
		Patterns:{
			YEAR      : /y/g,
			MONTH     : /M/g,
			DAY       : /d/g,
			HOUR      : /H/g,
			MINUTE    : /m/g,
			SECOND    : /s/g,
			MILSECOND : /f/g
		},
		FormatPatterns:function(format){
			return eval("/"+
					format
					.replace(this.Patterns.YEAR,'[0-9]')
					.replace(this.Patterns.MONTH,'[0-9]')
					.replace(this.Patterns.DAY,'[0-9]')
					.replace(this.Patterns.HOUR,'[0-9]')
					.replace(this.Patterns.MINUTE,'[0-9]')
					.replace(this.Patterns.SECOND,'[0-9]')
					.replace(this.Patterns.MILSECOND,'[0-9]')+
					"/g");
		},
		DateISO:function(value,format){
			var formatReg = "";
			if(value == "" || format=="")
				return false;
			formatReg = this.FormatPatterns(format);
//			alert(formatReg);
			return formatReg.test(value);
		}
	}

