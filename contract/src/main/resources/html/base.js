var _commonHost=window.location.host;
var _domain=document.domain;
var _protocol=document.location.protocol;
var _servicePath=_protocol+"//"+_commonHost+"/contract/";
_version="?v=1.0.0";
_min_js="";
_min_css="";
if(!_commonContextPath){
	_commonContextPath=_protocol+"//"+_commonHost+"/contract/html/"
}

var _resurceCache=false;

$.include = function (purl,targetTag,callBack,errorCallback,asyncpValue) {
	var urls=purl.split(" ");
	var url=urls[0];
	function processHTML(result){
		return result.
					  replace(/href="\/main.html/,'href="'+_commonContextPath+'main.html').
					  replace(/href="\/index.html/,'href="'+_commonContextPath+'index.html').
					  replace(/href="\/login.html/,'href="'+_commonContextPath+'login.html').
					  replace(/href="\/page\//g,'href="'+_commonContextPath+'page/').
					  replace(/src="\/page\//g,'href="'+_commonContextPath+'page/').
					  replace(/href="\/css\//g,'href="'+_commonContextPath+'css/').
					  replace(/src="\/images\//g,'src="'+_commonContextPath+'images/').
					  replace(/src="\/js\//g,'src="'+_commonContextPath+'js/').
					  replace(/\.bmp/g,'.bmp'+_version).
					  replace(/\.png/g,'.png'+_version).
					  replace(/\.gif/g,'.gif'+_version).
					  replace(/\.jpg/g,'.jpg'+_version).
					  replace(/\.jpeg/g,'.jpeg'+_version).
					  replace(/\.js/g,'.js'+_min_js+_version).
					  replace(/\.css/g,'.css'+_min_css+_version).
					  replace(/\.html/g,'.html'+_version);
	};
	function loadAjaxContent(asyncValue,targetTag,callBack,errorCallback,asyncpValue){
		$.ajax({
			"url" : _commonContextPath+url,
			"async" : asyncValue,
			"cache" : _resurceCache,
			"success" : function(result) {
				var newResult=processHTML(result);
				if(urls.length>1){
					newResult=newResultObj.find(urls[1]).html();
					if(urls.length>2){
						if(newResultObj.find(urls[2]).length>0){
							newResult+=newResultObj.find(urls[2]).html();
						}
					}
				}
				if(targetTag){
					$(targetTag).html(newResult);
					$(targetTag).find("*[hpb_text]").each(function(i,tagItem){
						$(tagItem).html(getPageProp($(tagItem).attr("hpb_text")));
					});
					$(targetTag).find("*[hpb_attr_text]").each(function(i,tagItem){
						var attrObj=$.parseJSON($(tagItem).attr("hpb_attr_text"));
						var attrName=attrObj['name'];
						var attrValue=attrObj['value'];
						$(tagItem).attr(attrName,getPageProp(attrValue));
					});
				}else{
					document.write(newResult);
					$("*[hpb_text]").each(function(i,tagItem){
						$(tagItem).html(getPageProp($(tagItem).attr("hpb_text")));
					});
					$("*[hpb_attr_text]").each(function(i,tagItem){
						var attrObj=$.parseJSON($(tagItem).attr("hpb_attr_text"));
						var attrName=attrObj['name'];
						var attrValue=attrObj['value'];
						$(tagItem).attr(attrName,getPageProp(attrValue));
					});
				}
				if($.type(callBack) === "function"){
					callBack(newResult);
				}
			},
			"error" : function(XMLHttpRequest, textStatus, errorThrown) {
				if($.type(errorCallback) === "function"){
					errorCallback(XMLHttpRequest, textStatus, errorThrown);
				}
				console.log(errorThrown);
			}
		});
	
	};
	var asyncValue=false;
	if(asyncpValue){
		asyncValue=asyncpValue;
	};
	loadAjaxContent(asyncValue,targetTag,callBack,errorCallback,asyncValue);
};
$.includeJs=function(jsSrc,callBack,errorCallback,asyncpValue){
	var times=0;
	function processJS(result){
		return '<script type="text/javascript">'+result+'</script>';
	};
	function loadAjaxContent(url,callBack,errorCallback,asyncpValue){
		$.ajax({
			"url" : _commonContextPath+url,
			"async" : asyncValue,
			"cache" : _resurceCache,
			"headers":{
		    },
			"dataType":'script',
			"success" : function(result) {
				var newResult=processJS(result);
				$("head").append($(newResult));
				if($.type(callBack) === "function"){
					callBack(newResult);
				};
			},
			"error" : function(XMLHttpRequest, textStatus, errorThrown) {
				if($.type(errorCallback) === "function"){
					errorCallback(XMLHttpRequest, textStatus, errorThrown);
				}
				times++;
				if(times<3){
					loadAjaxContent(jsSrc,callBack,errorCallback,asyncValue);
				}
				console.log(errorThrown);
			}
		});
	};
	var asyncValue=false;
	if(asyncpValue){
		asyncValue=asyncpValue;
	};
	loadAjaxContent(jsSrc,callBack,errorCallback,asyncValue);
};
var _areaPra=['550px', '350px'];
var _name_with=100;
var _value_with=700;
$.includeJs('js/core/corecode.js'+_min_js,function(){
	$.includeJs('js/core/security.js'+_min_js);
	$.includeJs('js/core/rsa.js'+_min_js,function(){
		$.includeJs('js/core/common.js'+_min_js,function(){
			if(/Android|webOS|iPhone|iPod|BlackBerry/i.test(navigator.userAgent)) {
				_areaPra=['350px', '350px'];
				_name_with=100;
				_value_with=500;
			}
		});
	});
	$.includeJs('js/core/page_properties.js'+_min_js,function(){
		$("meta[name='description']").attr({
			"content":getPageProp("m_description")
		});
		$.includeJs('js/pagination/jquery.twbsPagination.js');
	});
});
function changeProcessStep(param,hash){
	var _newUrl=_commonContextPath+"index.html";
	if(hash){
		_newUrl+="#"+hash;
	}
	if(param.pageParam){
		var isFirst=true;
		$.each(pageParam,function(pk,pv){
			if(isFirst){
				isFirst=false;
				_newUrl+="?";
			}else{
				_newUrl+="&";
			}
			_newUrl+=pk+"="+pv;
		})
	}
	history.pushState(param,null,_newUrl);
}

window.addEventListener('popstate', function(event) {
	if(event.state){
		var datas=event.state;
		changePages(datas.ndata,datas.textData,datas.callBack,datas.isNotMenu);
	}
});
function setPageLang(cLang,bt){
	$(bt).parent().find(".active").removeClass("active");
	corecode.cookie.add('currentLang',cLang,{expires:60*24*7});
	$.fn.twbsPagination.defaults.first="<span hpb_text='pagination_first'>"+getPageProp('pagination_first')+"</span>";
	$.fn.twbsPagination.defaults.prev="<span hpb_text='pagination_prev'>"+getPageProp('pagination_prev')+"</span>";
	$.fn.twbsPagination.defaults.next="<span hpb_text='pagination_next'>"+getPageProp('pagination_next')+"</span>";
	$.fn.twbsPagination.defaults.last="<span hpb_text='pagination_last'>"+getPageProp('pagination_last')+"</span>";
	$("meta[name='description']").attr({
		"content":getPageProp("m_description")
	});
	$("*[hpb_text]").each(function(i,tagItem){
		$(tagItem).html(getPageProp($(tagItem).attr("hpb_text")));
	});
	$("*[hpb_text_data]").each(function(i,tagItem){
		$(tagItem).html($.parseJSON($(tagItem).attr("hpb_text_data"))[cLang]);
	});
	$("*[hpb_attr_text]").each(function(i,tagItem){
		var attrObj=$.parseJSON($(tagItem).attr("hpb_attr_text"));
		var attrName=attrObj['name'];
		var attrValue=attrObj['value'];
		$(tagItem).attr(attrName,getPageProp(attrValue));
	});
}