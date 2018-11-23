$.ajaxSetup({
    cache: _resurceCache,
    headers: {useEncode: true},
    timeout: 10000,
    contentType: "application/x-www-form-urlencoded;charset=utf-8",
    complete: function (XMLHttpRequest, textStatus) {
        if (this.dataType && this.dataType == "json") {
        	if(!corecode.cookie.get('accessToken')){
        		toLogout();
        	}
        }
        CheckSubmitted.requestComplete();
    },
    dataFilter:function(dataOrg,type){
    	// if(type == "json"){
    	// 	var data = $.parseJSON(dataOrg);
    	// 	if(data.code =="200" && data.encodeKey && data.msg){
    	// 		var aeskey = rsaDecode(data.encodeKey);
    	// 		var key=aeskey.substring(0,16);
    	// 		var iv=aeskey.substring(16,32);
    	// 		var msg=corecode.security.AES.decode(key, iv,data.msg);
    	// 		try{
    	// 			data.msg=$.parseJSON(msg);
    	// 		}catch(e){
    	// 			data.msg=msg;
    	// 		}
    	// 		data.encodeKey = aeskey;
    	// 		return $.toJSON(data);
    	// 	}
    	// }
    	return dataOrg;
    },
    beforeSend: function (XMLHttpRequest) {
        
    },
    error: function (XMLHttpRequest, textStatus, errorThrown) {
        if ("json" == this.dataType) {
            if ("timeout" == textStatus) {
                $.showCommonMsg(getPageProp("tip_title"), getPageProp('response_timeout!'));
            } else {
                $.showCommonMsg(getPageProp("tip_title"), getPageProp('response_error'));
                console.log($.toJSON(errorThrown));
            }
            CheckSubmitted.requestComplete();
            $.ajaxLoaded();
        }
    }
});

$.HtmlOnComplete = function (callBack,data) {
	if($.type(callBack) === "function"){
		$.parser.onComplete = function () {  
			callBack(data);
			$.parser.onComplete=function (){
				$.ajaxLoaded();
			}
		}
		$.parser.onCompleteCall=callBack;
	}else{
		$.ajaxLoaded();
	}
};

$.showCommonMsg=function(title, msg, showType, timeout,style) {
	layer.msg(msg, {
		  icon: 0,
		  time: timeout,
		  skin: 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
	})
}
// 指定预处理参数选项的函数，只预处理dataType为text
$.ajaxPrefilter("text json json", function(options, originalOptions, jqXHR){
    // options对象
	// 包括accepts、crossDomain、contentType、url、async、type、headers、error、dataType等许多参数选项
    // originalOptions对象 就是你为$.ajax()方法传递的参数对象，也就是 { url: "/index.php" }
    // jqXHR对象 就是经过jQuery封装的XMLHttpRequest对象(保留了其本身的属性和方法)
	if("json"==options.dataType){
		options.contentType="application/json";
	}
	/*if(options.headers.useEncode){
		if(!originalOptions.data.param){
            originalOptions.data.param={};
		}
		if(originalOptions.data.page){
			originalOptions.data.param.pageNum=originalOptions.data.page;
			delete originalOptions.data.page;
		}
		if(originalOptions.data.rows){
			originalOptions.data.param.pageSize=originalOptions.data.rows;
			delete originalOptions.data.rows;
		}
		var param = $.toJSON(originalOptions.data.param);
		var aeskey=getAesKey();
		var key=aeskey.substring(0,16);
		var iv=aeskey.substring(16,32);
		var encodeParamValue=corecode.security.AES.encode(key, iv,param);
		var rsaKey = rsaEncode(aeskey);
		var newDatas=$.extend({},originalOptions.data||{},{
				"param":encodeParamValue,
				"encodeKey":rsaKey,
				"encode":true,
				"accessToken":corecode.cookie.get('accessToken')
		});
		options.data=$.toJSON(newDatas);
	}else if(options.contentType.indexOf("application/json")>-1){*/
		var newDatas=$.extend({},originalOptions.data||{},{
			"accessToken":corecode.cookie.get('accessToken')
		});
		options.data=$.toJSON(newDatas);
	// }
});
/** 
 * 把表单数据转化成为json对象
 */
$.fn.serializeJson = function() {
	var serializeObj = {};
	$(this.serializeArray()).each(function() {
		if(this.name in serializeObj){
			var oldValue=serializeObj[this.name];
			if($.type(oldValue) === "array"){
				serializeObj[this.name].push(this.value);
			}else{
				serializeObj[this.name]=[];
				serializeObj[this.name].push(oldValue);
			}
//			serializeObj[this.name] =oldValue+","+this.value;
		}else{
			serializeObj[this.name] = this.value;
		}
	});
	return serializeObj;
};

/**
 *  扩展jQuery的方法
*/
jQuery.extend({
	/**
	 * 计算字符串的长度
     * @param TargetStr
     * @returns
     */
    strQybLength:function(TargetStr){
    	if(TargetStr){
			var length=0;
    		var TargetStr1=TargetStr.replace(/[^\x00-\xff]/g,'');
    		var TargetStr2=TargetStr.replace(/[\x00-\xff]/g,'');
    		return TargetStr1.length+TargetStr2.length*3;
		}
    	return 0;
    },
	ajaxLoading : function(msg,timeout,target,className) {
		if(CheckSubmitted.msg){
			layer.close(CheckSubmitted.msg);
		}
		CheckSubmitted.msg=layer.msg(msg,{
			 time: timeout?timeout:6000//6秒关闭（如果不配置，默认是3秒）
		});
	},
	ajaxLoaded : function(className) {
		if(CheckSubmitted.msg){
			layer.close(CheckSubmitted.msg);
		}
	},
	ajaxPostReq:function(paramData,callBack,asyncflag,typeflag){
		var asyncValue=true;
		if(asyncflag===false||asyncflag=='false'){
			asyncValue=false;
		};
		var typeValue="post";
		if(typeflag){
			var typeValue=typeflag;
		}
		jQuery.ajax({
			type : typeValue,
			async : asyncValue,
			dataType : "json",
			contentType : 'application/json',
			cache : false,
			url : _servicePath + "invokeHpbCmd",
			data :$.extend({},paramData||{}),
			headers : {"useEncode" : true},
			beforeSend : function(XMLHttpRequest) {
				$.ajaxLoading(getPageProp("common_loading_msg"), 5000);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$.ajaxLoaded();
				CheckSubmitted.requestComplete();
				if ("timeout" == textStatus) {
	                $.showCommonMsg(getPageProp("tip_title"), getPageProp('response_timeout!'));
	            } else {
	                $.showCommonMsg(getPageProp("tip_title"), getPageProp('response_error'));
	                console.log($.toJSON(errorThrown));
	            }
			},
			success : function(returnData) {
				$.ajaxLoaded();
				CheckSubmitted.requestComplete();
				if($.type(callBack) === "function"){
					callBack(returnData);
				}
			}
		});

	}
});
if (typeof (CheckSubmitted) == "undefined" || !CheckSubmitted) {
	CheckSubmitted={
			/**
			 * 这个变量指定请求是否已经提交，这个变量初始化值为false
			 */
			requestSubmitted:false,
			/**
			 * 检查请求是否已经提交。一个新的请求提交只能是当前没有其他请求在运行中，如果有其他请求在运行中，那么这个请求将会被拒绝
			 * if(!CheckSubmitted.submitRequest()){ return; }
			 * 
			 * @return {boolean} 指定请求是否已经提交 (true) 或者拒绝 (false).
			 */
			submitRequest:function() {
				if(!this.requestSubmitted) {
					this.requestSubmitted  = true;
					setTimeout(function(){
						CheckSubmitted.requestComplete();
					}, 1500);
					return true;
				}
				return false;
			},
			/**
			 * 当前请求已经完成
			 */
			requestComplete:function() {
				this.requestSubmitted  = false;
				this.currentId=null;
			},
			/**
			 * 这个变量存储标签元素的id值(ex: button/link)，当用户点击这个标签并触发一个ajax请求，这个id值将会被设置
			 */
			currentId : null,
			/**
			 * 存储一个标签元素的id值，这个标签元素已经触发并提交了一个请求。如果想改变这个id值，必须当前没有其他请求运行中
			 * 
			 * @param {string}
			 *            id 正在触发一个请求中的元素id
			 */
			setCurrentId:function(id){
				/**
				 * 如果没有请求触发，那么可以重新设置当前id值
				 */
				if(!this.requestSubmitted && $.trim(this.currentId) == ""){
					this.currentId = id;
				}
			}
	}
}

/**
 * 判断成功状态
 * @param data
 * @returns {Boolean}
 */
function isRespSuccess(data){
	if(data&&data[0]=="000000"){
		return true;
	}
	return false;
}
function getRespErrorMsg(data){
	if(data&&data[0]=="000000"){
		return null;
	}
	if(data&&data[1]){
		return data[1];
	}
	return null;
}
function toLogonPageWithMsg(){
	goToLogonPage('\u7528\u6237\u4f1a\u8bdd\u8fc7\u671f','\u7528\u6237\u4f1a\u8bdd\u8d85\u65f6\u6216\u8005\u8fc7\u671f',1000);
}
function goToLogonPage(title,msg,timeout){
	if(!CheckSubmitted.submitRequest()){
		return;
	}
	if(window.location.href.indexOf(_commonContextPath+"login.html")<0){
		setTimeout(function(){
			window.location.href=_commonContextPath+"login.html";
		},timeout);
		$.showCommonMsg(title,msg,'fade',timeout);
	}
}
String.prototype.trim = function(){
	return $.trim(this);
}

function checkSucess(proccessId,callBack,timeOut,count,maxCount){
	if(!CheckSubmitted.submitRequest()){
		return;
	}
	if(!timeOut){
		timeOut=1000;
	}
	if(!count){
		count=0;
	}
	if(!maxCount){
		maxCount=10;
	}
	if(count>maxCount){
	    callBack({});
		return;
	}
	if(count==0){
		setTimeout(function(){
			CheckSubmitted.requestComplete();
			checkSucess(proccessId,callBack,timeOut,++count,maxCount);
		},timeOut);
		return;
	}
	jQuery.ajax({
		type : 'post',
		async : true,
		dataType : "json",
		contentType : 'application/json',
		cache : false,
		url : _servicePath + "invokeHpbCmd",
		data :{
			"serviceCode":"checkSucess",
			"rsaKey":"123456",
			"enCodekey":"DF34F",
			"returnUrl":null,
			"param":{
				"processFromId":proccessId
			}
		},
		headers : {
			"useEncode" : true
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			CheckSubmitted.requestComplete();
			 $.messager.alert({
	                title:getPageProp("tip_title"),
	                msg:getPageProp('response_timeout'),
	                showType:'fade',
	                inline : false,
	                icon : 'info',
	                style:{
	                    right:'',
	                    bottom:''
	                }
	            });
			setTimeout(function(){
					checkSucess(proccessId,callBack,timeOut,++count,maxCount);
			},timeOut);
		},
		success : function(data) {
			if (isRespSuccess(data)) {
				CheckSubmitted.requestComplete();
				var returnData=$.parseJSON(corecode.security.BASE64.decode(data[2]));
				if($.type(callBack) === "function"){
					callBack(returnData);
				}
			}else{
				setTimeout(function(){
					CheckSubmitted.requestComplete();
					checkSucess(proccessId,callBack,timeOut,++count,maxCount);
				},timeOut);
			}
		}
	});
}

function changePages(ndata,callBack,isNotMenu){
	if(ndata.iconCls){
		var iconli=$("#breadcrumbs > .breadcrumb > li > i");
		iconli.removeAttr("class").addClass("ace-icon").addClass("fa");
		$.each(ndata.iconCls,function(i,icon){
			iconli.addClass(icon);
		});
		$("#navbar-toggle11").addClass("collapsed");
	}
	if(ndata.text){
		if($("#breadcrumb_text1").length>0){
			$("#breadcrumb_text1")[0].innerHTML=getPageProp("key",{"key":ndata.text});
			$("#breadcrumb_text1").attr({
				"hpb_text_data":$.toJSON(ndata.text)
			});
		}
	}
	var pageUrl=ndata.hrefUrl;
	var mid="side_nav_ul_li"+ndata.id;
	$(".nav-list > .active").removeClass("active");
	$(".nav-list > .open").removeClass("open");
	$(".submenu > .active").removeClass("active");
	$(".submenu > .open").removeClass("open");
	$("#"+mid).addClass("active");
	$.include(pageUrl,"#page-content-detail",function(data){
		if(typeof(callBack)=="function"){
			callBack(data);
		}
		if(!isNotMenu&&/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
			$("#navbar-toggle11").click();
		}
		if(ndata.hash){
			changeProcessStep({},ndata.hash);
		}else{
			changeProcessStep({},pageUrl.replace("page/",'/').replace(".html","/"));
		}
	});
}
function toLogout(){
	window.location.href=_commonContextPath+"login.html";
}
function gotoHomePage(){
	window.location.href=_commonContextPath+"index.html";
}
var _tipPosition=2;
if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
	_tipPosition=3;
}
function easyuiTips(msg,fieldName,form,tipPosition){
	if(form){
		layer.tips(msg, $(form).find("*[name='" +fieldName+"']").parent(), 
			{"tips": tipPosition||_tipPosition});
	}else{
		layer.tips(msg, $("*[name='" +fieldName+"']").parent(), 
				{"tips": tipPosition||_tipPosition});
	}
}
function toTreeData(data){
	var pos={};
	var tree=[];
	var i=0;
	while(data.length!=0){
		if(data[i].pid==0){
			tree.push({
				id:data[i].id,
				text:data[i].text,
				children:[]
			});
			pos[data[i].id]=[tree.length-1];	
			data.splice(i,1);
			i--;
		}else{
			var posArr=pos[data[i].pid];
			if(posArr!=undefined){
				
				var obj=tree[posArr[0]];
				for(var j=1;j<posArr.length;j++){
					obj=obj.children[posArr[j]];
				}
 
				obj.children.push({
					id:data[i].id,
					text:data[i].text,
					children:[]
				});
				pos[data[i].id]=posArr.concat([obj.children.length-1]);
				data.splice(i,1);
				i--;
			}
		}
		i++;
		if(i>data.length-1){
			i=0;
		}
	}
	return tree;
}

