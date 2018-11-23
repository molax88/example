function submitForm(){
    if(!CheckSubmitted.submitRequest()){
        return;
    }
    var formdata=$("#query_authorization_form").serializeJson();
    var newDatas= $.extend({},formdata ||{});
    $('#query_order_product_content').datagrid({
    	queryParams:{
            "serviceCode":"queryTrade",
            "serviceType":"trade",
            "encodeKey":"",
            "accessToken":"DF34F",
            "returnUrl":null,
            "param":newDatas
		},
        onDblClickRow:function(index,row){
            $(".newAccount").hide();
            $("#query_auth_detail_form").form("clear").form("load",row);
            layer.open({
                type: 1,
                title: "交易详情",
                shadeClose: true,
                shade: 0.8,
                zIndex:100000,
                area: ['600px','540px'],
                content: $("#query_auth_detail_form"),
                btn:["解密参数","返回"],
                yes:function(index){
                    var params = $("#query_auth_detail_form").serializeJson();
                    console.log('params',params);
                    $.ajaxPostReq({
                        "serviceCode":"queryTrade",
                        "serviceType":"decrypt",
                        "encodeKey":"",
                        "accessToken":"DF34F",
                        "returnUrl":null,
                        "param":params
                    },function(data){
                        if(data&&data.code=="40000"){
                            toLogout();
                        }
                        if(data&&data.code=="200"){
                            console.log('success data',data);
                            // $(".trade_encode_key").css("display","block");
                            $(".newAccount").show();
                            $(".trade_encode_key input").textbox("setValue",data.msg.input);
                        }else{
                            layer.alert("解析失败!");
                        }
                    });
                },
                no:function(index){
                    layer.close(index);
                }
            });
        }
    });
}	
function clearForm(){
	$('#query_authorization_form').form('reset');
}
submitForm();
