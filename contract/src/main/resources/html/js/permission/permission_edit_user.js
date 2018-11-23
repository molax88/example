function submitForm(){
	 if(!CheckSubmitted.submitRequest()){
         return;
     }
     $('#permission_query_list_datagrid').datagrid({
     	queryParams: {
             "serviceCode":"permission",
             "serviceType":"queryUser",
             "encodeKey":"",
             "accessToken":"DF34F",
             "returnUrl":null,
             "param":$("#permission_query_user_form").serializeJson()
         },
        singleSelect : true,
        onDblClickRow:function(index,row){
        	edit();
        }
     }).datagrid('getPager').pagination({
         buttons:[
             {
                 iconCls:'icon-add',
                 text:'创建用户',
                 handler:function(){
                     add();
                 }
             },
             {
                 iconCls:'icon-edit',
                 text:'编辑用户',
                 handler:function(){
                     edit();
                 }
             }
         ],
         loading : false
     });
}
function clearForm(){
    $('#permission_query_user_form').form('clear');
}
function add(){
	$("#permission_add_user_form").form("reset");
	layer.open({
    	type: 1,
    	title: "新增用户",
    	shadeClose: true,
    	shade: 0.8,
    	zIndex:100000,
    	area: ['500px','410px'],
    	content: $("#permission_add_user_form"),
    	btn:["确定","关闭"], 
    	yes:function(index){
            var params = $("#permission_add_user_form").serializeJson();
            $.ajaxPostReq({
                "serviceCode":"account",
                "serviceType":"manage",
                "encodeKey":"",
                "accessToken":"DF34F",
                "returnUrl":null,
                "param":params
            },function(data){
                if(data&&data.code=="40000"){
                    toLogout();
                }
                if(data&&data.code=="200"){
                    $('#permission_query_list_datagrid').datagrid("reload");
                    layer.close(index);
                    layer.alert("添加成功！");
                }else{
                    layer.alert("添加失败!");
                }
            });
		},
		no:function(index){
			layer.close(index);
		}
    });
}

function edit(){
	var row = $('#permission_query_list_datagrid').datagrid('getSelected');
	if (row){
		$("#permission_add_user_form").form("reset").form('load',row);
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
	    			var rows = data.msg.rows;
	    			$('#roleTreeUpdate').combotree('loadData', $.map(rows,function(val){
	    				return {
	    					id:val.id,
	    					text:val.roleName
	    				}
	    			})).combotree('setValue', row.roleId);
	    			layer.open({
	    		    	type: 1,
	    		    	title: "编辑用户",
	    		    	shadeClose: true,
	    		    	shade: 0.8,
	    		    	zIndex:100000,
	    		    	area: ['500px','410px'],
	    		    	content: $("#permission_add_user_form"),
	    		    	btn:["确定","关闭"], 
	    		    	yes:function(index){
	    		    		var params = $("#permission_add_user_form").serializeJson();
	    			        $.ajaxPostReq({
	    			    		"serviceCode":"permission",
	    			    		"serviceType":"update",
	    			    		"param":params
	    			    	},function(data){
	    			    		if(data&&data.code=="40000"){
	    			    			toLogout();
	    			    		}
	    			    		if(data&&data.code=="200"){
                                    $('#permission_query_list_datagrid').datagrid("reload");
	    			    			layer.close(index);
	    			    		}
	    			    	})
	    				}, 
	    				no:function(index){
	    					layer.close(index);
	    				}
	    		    });
	    		}
	    	});
	}else{
		layer.alert("请选择一条记录");
	}
}
function saveMain(){
	var row = $('#permission_create_list_datagrid').datagrid('getSelected');
	if (row){
		$.messager.confirm("确认", "确认变更为"+row.username+"为主账户吗？", function (r) {  
	        if (r) {  
	        	$("#permission_add_user_form").form("reset").form('load',row);
	    		layer.open({
	    	    	type: 1,
	    	    	title: "变更为主账户",
	    	    	shadeClose: true,
	    	    	shade: 0.8,
	    	    	zIndex:100000,
	    	    	area: ['500px','410px'],
	    	    	content: $("#permission_add_user_form"),
	    	    	btn:["确定","关闭"], 
	    	    	yes:function(index){
	    	    		layer.close(index);
	    			}, 
	    			no:function(index){
	    				layer.close(index);
	    			}
	    	    });
	        } 
	    }); 
	}
}
function clearDialogForm(){
	$('#permission_add_user_form').form('clear');
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
                $("#roleTreeUpdate").combotree("loadData",$.map(data.msg.rows,function(val){
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