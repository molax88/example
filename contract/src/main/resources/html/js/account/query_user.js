function clearForm(){
    $('#query_user_form').form('reset');
}
function submitForm(){
    if(!CheckSubmitted.submitRequest()){
        return;
    }
    var newDatas=$.extend({},{"type":'0'},
    $("#query_user_form").serializeJson());
    $('#query_user_datalist').datagrid({
    	queryParams:{
            "serviceCode":"account",
            "serviceType":"query",
            "encodeKey":"",
            "accessToken":"",
            "returnUrl":null,
            "param":newDatas
        },
        onDblClickRow:function(index,row) {
            $("#query_user_detail_form").form("clear").form("load", row);
            $("#roleTree").combotree("setValue",row.roleId);
            layer.open({
                type: 1,
                title: "用户详情",
                shadeClose: true,
                shade: 0.8,
                zIndex: 100000,
                area: ['600px', '700px'],
                content: $("#query_user_detail_form"),
                btn: ["更新","返回"],
                yes:function(index){
                    $(".newAccount").show();
                    if(checkForm()){
                        var params = $("#query_user_detail_form").serializeJson();
                        $.ajaxPostReq({
                            "serviceCode":"account",
                            "serviceType":"update",
                            "encodeKey":"",
                            "accessToken":"",
                            "returnUrl":null,
                            "param":params
                        },function(data){
                            if(data&&data.code=="40000"){
                                toLogout();
                            }
                            if(data&&data.code=="200"){
                                layer.close(index);
                                layer.msg("更新成功!");
                                $('#query_user_datalist').datagrid("reload");
                            }else{
                                layer.alert("更新失败!");
                            }
                        });
                    }
                },
                no: function (index) {
                    layer.close(index);
                }
            });
        },
        pagePosition : "bottom"
    }).datagrid('getPager').pagination({
        buttons:[{
            iconCls:'icon-add',
            text:'创建新用户',
            handler:function(){
                $(".newAccount").hide();
                $("#query_user_detail_form").form("reset");
                layer.open({
                    type: 1,
                    title: "创建账户",
                    shadeClose: true,
                    shade: 0.8,
                    zIndex:100000,
                    area: ['600px','660px'],
                    content: $("#query_user_detail_form"),
                    btn:["确定","取消"],
                    yes:function(index){
                        if(checkForm()){
                            var params = $("#query_user_detail_form").serializeJson();
                            $.ajaxPostReq({
                                "serviceCode":"account",
                                "serviceType":"create",
                                "encodeKey":"",
                                "accessToken":"",
                                "returnUrl":null,
                                "param":params
                            },function(data){
                                if(data&&data.code=="40000"){
                                    toLogout();
                                }
                                if(data&&data.code=="200"){
                                    layer.close(index);
                                    layer.msg("创建成功!");
                                    $('#query_user_datalist').datagrid("reload");
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
function checkForm(){
    if(!CheckSubmitted.submitRequest()){
        return false;
    }
    var userName=$("#userName").textbox("getValue");
    if(!userName){
        easyuiTips("请输入用户登录名",'userName','#query_user_detail_form');
        CheckSubmitted.requestComplete();
        return false;
    }
    return true;
}
$.parser.onComplete=function(){
    //获取所有菜单项
    $.ajaxPostReq({
        "serviceCode":"authRole",
        "serviceType":"queryActive",
        "param":{
            pageNum:0,
            pageSize:100,
            state:1
        }
    },function(data){
        if(data&&data.code=="40000"){
            toLogout();
        }
        if(data&&data.code=="200"){
            if(data.msg&&data.msg.rows){
                $("#roleTree").combotree("loadData",$.map(data.msg.rows,function(val){
                    return {
                        id:val.id,
                        text:val.roleName
                    }
                }));
            }
        }
    });
    $.parser.onComplete=function(){}
};
submitForm();