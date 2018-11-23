function clearForm(){
	$('#query_candy_form').form('reset');
}
function formatOper(val,row,index){
	console.info(row);
	return '<a href="javascript:void(0)"  class="easyui-linkbutton" onclick="delText('+index+')">删除</a>'; 
}
function delText(index){
	$('#query_candy_content').datagrid('selectRow',index);
	var row = $('#query_candy_content').datagrid('getSelected');
	var newDatas= $.extend({},row ||{});
	layer.confirm('确认删除？', {
		  btn: ['确认','取消'] //按钮
		}, function(){
		  $.ajaxPostReq({
		        "serviceCode":"overseaCandyHand",
		        "serviceType":"delCandyInfo",
		        "encodeKey":"",
		        "accessToken":"DF34F",
		        "returnUrl":null,
		        "param":newDatas
		    },function(data){
		        if(data&&data.code=="40000"){
		            toLogout();
		        }
		        if(data&&data.code=="200"){
		        	layer.msg('删除成功', {icon: 1});
    	        	submitForm();
		        }else{
		            layer.alert("删除失败!");
		        }
		    });
		  
		}, function(){
		  
		});
	
}
function submitForm(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	var newDatas=$.extend({},{"type":'0'},
		    $("#query_candy_form").serializeJson());
	$('#query_candy_content').datagrid({
		    	queryParams:{
		            "serviceCode":"overseaCandyHand",
		            "serviceType":"queryInfo",
		            "encodeKey":"",
		            "accessToken":"",
		            "returnUrl":null,
		            "param":newDatas
		        },
		        onDblClickRow:function(index,row) {
		        	$("#add_candy_form").form("clear").form("load", row);
		        	var vType="updateCandyInfo";
		        	openAddForm(vType);
		        },
		        pagePosition : "bottom"
		    }).datagrid('getPager').pagination({
		    	 buttons:[
		                    {	id:'addCandyInfo',
		                        iconCls:'icon-save',
		                        text:'添加记录',
		                        handler:function(){
		                        	addCandyInfo();
		                        }
		                    }
		                ],
                loading : false
    });
}
function addCandyInfo(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#add_candy_form").form("clear");
	var vType="addCandyInfo";
	openAddForm(vType);
}
function openAddForm(value){
	layer.open({
        type: 1,
        title: "糖果映射",
        shadeClose: true,
        shade: 0.8,
        zIndex: 100000,
        area: ['600px', '300px'],
        content: $("#add_candy_form"),
        btn: ["确认","返回"],
        yes:function (index) {
        	 var email = $("#email").textbox('getValue');
        	 if($.trim(email)==''){
        			layer.alert('请输入邮箱');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var formdata=$("#add_candy_form").serializeJson();
        	 var newDatas= $.extend({},formdata ||{});
        	$.ajaxPostReq({
    	        "serviceCode":"overseaCandyHand",
    	        "serviceType":value,
    	        "encodeKey":"",
    	        "accessToken":"DF34F",
    	        "returnUrl":null,
    	        "param":newDatas
    	    },function(data){
    	    	debugger;
    	        if(data&&data.code=="40000"){
    	            toLogout();
    	        }
    	        if(data.msg){
    	        	layer.close(index);
    	        	layer.alert(data.msg.RETURN_MSG);
    	        }else{
    	        	if(data&&data.code=="200"){
    	        		layer.close(index);
    	        		layer.msg('更新成功!', {icon: 1});
    	        	}else{
    	        		layer.alert('更新失败!');
    	        	}
    	        }
    	        submitForm();
    	    });
        },
        no: function (index) {
            layer.close(index);
        }
    });
}
submitForm();