function clearForm(){
    $('#query_review_form').form('reset');
}
function submitForm(){
    if(!CheckSubmitted.submitRequest()){
        return;
    }
    var newDatas=$.extend({},$("#query_review_form").serializeJson());
    $('#query_review_product_content').datagrid({
    	queryParams: {
            "serviceCode":"queryTrade",
            "serviceType":"review",
            "encodeKey":"",
            "accessToken":"DF34F",
            "returnUrl":null,
            "param":newDatas
        },
        onLoadSuccess : function() {
		},
	    fitColumns : true,
        singleSelect : true,
		onSelect:function(index,row){
        	showReviewContentDetail(index,row);
        }
    });
}
function showReviewContentDetail(index,row){
	if(!CheckSubmitted.submitRequest()){
        return;
    }
	layer.open({
      	type: 1,
      	title: "溯源授权详细信息",
      	shadeClose: true,
      	shade: 0.8,
      	zIndex:100002,
      	area: ['80%','90%'],
      	content: $("#query_review_content_detail_div"),
      	btn:["确定","关闭"],
      	success: function(layero, index){
      		$.parser.parse("#query_review_content_detail_div");
      		$("#query_review_detail_form").form("clear").form("load",row);
      	    $.ajaxLoading("正在加载授权详情。。。");
      	    $('#query_review_content_detail').datagrid({
      	    	queryParams: {
      	    		"serviceCode":"queryTrade",
      	            "serviceType":"trade",
      	            "encodeKey":"",
      	            "accessToken":"",
      	            "returnUrl":null,
      	            "param":{
      	                "authCode":row.authCode
      	            }
      	        },
      	        onLoadSuccess : function() {
      	        	$.ajaxLoaded();
      	        },
      	        pagination:false,
      	        loadFilter:function(data) {
      		    	if("200"==data.code){
      					var total=data.msg.totalSize;
      					delete data.msg.totalSize;
      					data.msg.total=total;
      					var finishedColor = '#85e085';
      		            var currentColor = '#3c3';
      		            for (var n=0;n<data.msg.rows.length;n++) {
      		                if(data.msg.rows[n].tradeType=='0'){//用户授权
      		                    $("#start")
      		                        .css('background', 'url(images/11.jpg) no-repeat')
      		                        .next('span').css('color', currentColor)
      		                        .parent('div').prev('div.progress-line').css('background-color', finishedColor);
      		                }else if(data.msg.rows[n].tradeType=='1'){//机构授权
      		                    $('#second')
      		                        .css('background','url(images/12.jpg) no-repeat')
      		                        .next('span').css('color', currentColor)
      		                        .parent('div').prev('div.progress-line').css('background-color', finishedColor);
      		                }else if(data.msg.rows[n].tradeType=='2'){//银联授权
      		                    $('#third')
      		                        .css('background','url(images/13.jpg) no-repeat')
      		                        .next('span').css('color', currentColor)
      		                        .parent('div').prev('div.progress-line').css('background-color', finishedColor);
      		                }
      		            }
      					return data.msg;
      				}
      				return data;
      			}
      	    });
      	},
      	yes:function(index){
      		layer.close(index);
      	}, 
  		no:function(index){
  			layer.close(index);
  		}
      });
}
submitForm();