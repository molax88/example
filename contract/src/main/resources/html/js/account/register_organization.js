function createAccount(){
    if(!CheckSubmitted.submitRequest()){
        return;
    }
    var fullName=$("#fullName").textbox("getValue");
    if(!fullName){
    	easyuiTips("请输入机构名称",'fullName');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var shortName=$("#shortName").textbox("getValue");
    if(!shortName){
    	easyuiTips("请输入机构简称",'shortName');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var password=$("#password").textbox("getValue");
    if(!password){
    	easyuiTips("请输入机构密码",'password');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    if(password.length>18||password.length<8){
    	easyuiTips("机构密码长度必须在8-18位",'password');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var identityNumber=$("#identityNumber").textbox("getValue");
    if(!identityNumber){
    	easyuiTips("请输入机构纳税识别号",'identityNumber');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var userMobile=$("#userMobile").textbox("getValue");
    if(!userMobile){
    	easyuiTips("请输入机构联系电话",'userMobile');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var userEmail=$("#userEmail").textbox("getValue");
    if(!userEmail){
    	easyuiTips("请输入机构邮箱",'userEmail');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    if(!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(userEmail)){
    	easyuiTips("机构邮箱格式不对",'userEmail');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var publicKey=$("#publicKey").textbox("getValue");
    if(!publicKey){
    	easyuiTips("请输入机构公钥",'publicKey');
    	CheckSubmitted.requestComplete();
    	return false;
    }
    var formDatas=$("#register_organization_form").serializeJson();
     $.ajaxPostReq( {
         "serviceCode":"account",
         "serviceType":"create",
         "encodeKey":"",
         "accessToken":"123456",
         "returnUrl":null,
         "param":formDatas
     },function(data){
    	    $.ajaxLoaded();
    	    CheckSubmitted.requestComplete();
    	    layer.alert('提示', {
	    	  skin: 'layui-layer-molv' //样式类名
	    	  ,closeBtn: 0,
	    	  anim: 4
	    	}, function(){
	    		 window.location.reload();
	    	});
     });

}

function clearForm(form){
    $(form).form('reset');
}