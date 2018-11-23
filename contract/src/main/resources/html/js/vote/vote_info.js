function clearForm(){
	$('#query_vote_form').form('reset');
}
function formatOper(val,row,index){
	console.info(row);
	return '<a href="javascript:void(0)"  class="easyui-linkbutton" onclick="delText('+index+')">删除</a>'; 
}
function delText(index){
	$('#query_vote_content').datagrid('selectRow',index);
	var row = $('#query_vote_content').datagrid('getSelected');
	var newDatas= $.extend({},row ||{});
	layer.confirm('确认删除？', {
		  btn: ['确认','取消'] //按钮
		}, function(){
		  $.ajaxPostReq({
		        "serviceCode":"voteInfoHandle",
		        "serviceType":"delVoteInfo",
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
		    $("#query_vote_form").serializeJson());
	$('#query_vote_content').datagrid({
		    	queryParams:{
		            "serviceCode":"voteInfoHandle",
		            "serviceType":"queryInfo",
		            "encodeKey":"",
		            "accessToken":"",
		            "returnUrl":null,
		            "param":newDatas
		        },
		        onDblClickRow:function(index,row) {
		        	$("#add_vote_form").form("clear").form("load", row);
		        	var vType="updateVoteInfo";
		        	openAddForm(vType);
		        },
		        pagePosition : "bottom"
		    }).datagrid('getPager').pagination({
		    	 buttons:[
		                    {	id:'addVoteInfo',
		                        iconCls:'icon-save',
		                        text:'添加记录',
		                        handler:function(){
		                        	addVoteInfo();
		                        }
		                    }
		                ],
                loading : false
    });
}
function addVoteInfo(){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	$("#add_vote_form").form("clear");
	var vType="addVoteInfo";
	openAddForm(vType);
}
function openAddForm(value){
	layer.open({
        type: 1,
        title: "候选人信息",
        shadeClose: true,
        shade: 0.8,
        zIndex: 100000,
        area: ['600px', '600px'],
        content: $("#add_vote_form"),
        btn: ["确认","返回"],
        yes:function (index) {
        	 var coinbase = $("#coinbase").textbox('getValue');
        	 if($.trim(coinbase)==''){
        			layer.alert('请输入挖矿地址');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 if((coinbase.indexOf("0x")!=0)||coinbase.length!=42){
        		 layer.alert('请输入正确的地址');
     			CheckSubmitted.requestComplete();
     			return false;
        	 }
        	 var name = $("#name").textbox('getValue');
        	 if($.trim(name)==''){
        			layer.alert('请输入名称');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var address = $("#address").textbox('getValue');
        	 if($.trim(address)==''){
        			layer.alert('请输入地址');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 /*var count = $("#count").textbox('getValue');
        	 if($.trim(count)==''){
        			layer.alert('请输入票数');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var re = /^[0-9]+.?[0-9]*$/;
        	 if(!re.test(count)){
        		layer.alert('票数格式错误');
     			CheckSubmitted.requestComplete();
     			return false;
        	 }*/
        	 
        	 var operatorId = $("#operatorId").textbox('getValue');
        	 if($.trim(operatorId)==''){
        			layer.alert('请输入操作员');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var linkUrl = $("#linkUrl").textbox('getValue');
        	 if($.trim(linkUrl)==''){
        			layer.alert('请输入官网地址');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var description = $("#description").textbox('getValue');
        	 if($.trim(description)==''){
        			layer.alert('请输入描述字段');
        			CheckSubmitted.requestComplete();
        			return false;
        		}
        	 var formdata=$("#add_vote_form").serializeJson();
        	 var newDatas= $.extend({},formdata ||{});
        	$.ajaxPostReq({
    	        "serviceCode":"voteInfoHandle",
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