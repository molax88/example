function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
    var newDatas=$.extend({"pageNum":1,"pageSize":10,"type":'1'},
        $("#query_organization_form").serializeJson());
    $('#query_org_datalist').datagrid({
    	queryParams: {
            "serviceCode":"account",
            "serviceType":"query",
            "encodeKey":"",
            "accessToken":"DF34F",
            "returnUrl":null,
            "param":newDatas
        },
        onDblClickRow:function(index,row){
            $("#query_org_detail_form").form("clear").form("load",row);
            $(".newAccount").show();
            layer.open({
	        	type: 1,
	        	title: "账户编辑",
	        	shadeClose: true,
	        	shade: 0.8,
	        	zIndex:100000,
	        	area: ['600px','540px'],
	        	content: $("#query_org_detail_form"),
	        	btn:["更新","取消"], 
	        	yes:function(index){
					var params = $("#query_org_detail_form").serializeJson();
					$.ajaxPostReq({
						"serviceCode":"account",
						"serviceType":"update",
						"encodeKey":"",
						"accessToken":"DF34F",
						"returnUrl":null,
						"param":params
					},function(data){
						if(data&&data.code=="40000"){
							toLogout();
						}
						if(data&&data.code=="200"){
							layer.close(index);
							layer.msg("更新成功!");
							$('#query_org_datalist').datagrid("reload");
						}else{
							layer.alert("更新失败!");
						}
					});
        		},
        		no:function(index){
        			layer.close(index);
        		}
	        });
        }
    }).datagrid('getPager').pagination({
    	buttons:[{
            iconCls:'icon-add',
            text:'创建新账户',
            handler:function(){
              $("#query_org_detail_form").form("reset")
              $(".newAccount").hide();
              layer.open({
  	        	type: 1,
  	        	title: "创建账户",
  	        	shadeClose: true,
  	        	shade: 0.8,
  	        	zIndex:100000,
  	        	area: ['600px','470px'],
  	        	content: $("#query_org_detail_form"),
  	        	btn:["确定","取消"], 
  	        	yes:function(index){
  	        		if(checkForm()){
  	        			var params = $("#query_org_detail_form").serializeJson();
  	        			$.ajaxPostReq({
  	        				"serviceCode":"account",
  	        				"serviceType":"create",
  	        				"encodeKey":"",
  	        				"accessToken":"DF34F",
  	        				"returnUrl":null,
  	        				"param":params
  	        			},function(data){
  	        				if(data&&data.code=="40000"){
  	        					toLogout();
  	        				}
  	        				if(data&&data.code=="200"){
  	        					layer.close(index);
  	        					layer.msg("创建成功!");
  	        					$('#query_org_datalist').datagrid("reload");
  	        				}else{
  	        					layer.alert("创建失败!");
  	        				}
  	        			});
  	        		}
          		}, 
          		no:function(index){
          			layer.close(index);
          		}
  	        });
            }
        }]
    });
}

function clearForm(){
    $('#query_organization_form').form('reset');
}

function checkForm(){
	if(!CheckSubmitted.submitRequest()){
        return false;
    }
	var fullName=$("#fullName").textbox("getValue");
    if(!fullName){
    	easyuiTips("请输入机构名称",'fullName','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var shortName=$("#shortName").textbox("getValue");
    if(!shortName){
    	easyuiTips("请输入机构简称",'shortName','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var password=$("#password").textbox("getValue");
    if(!password){
    	easyuiTips("请输入机构密码",'password','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    if(password.length>18||password.length<8){
    	easyuiTips("机构密码长度必须在8-18位",'password','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var identityNumber=$("#identityNumber").textbox("getValue");
    if(!identityNumber){
    	easyuiTips("请输入机构纳税识别号",'identityNumber','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var userMobile=$("#mobile").textbox("getValue");
    if(!userMobile){
    	easyuiTips("请输入机构联系电话",'mobile','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var userEmail=$("#email").textbox("getValue");
    if(!userEmail){
    	easyuiTips("请输入机构邮箱",'email','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(userEmail)){
    	easyuiTips("机构邮箱格式不对",'email','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var publicKey=$("#publicKey").textbox("getValue");
    if(!publicKey){
    	easyuiTips("请输入机构公钥",'publicKey','#query_org_detail_form');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    return true;
}
submitForm();