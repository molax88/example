var _allPro={
	"common_loading_msg":{
		"zh-CN":"\u8bf7\u6c42\u63d0\u4ea4\u4e2d,\u8bf7\u7a0d\u7b49\u3002\u3002\u3002",
		"en":"Loading..."
	},
	"tip_title":{
		"zh-CN":"\u63d0\u793a",
		"en":"prompt"
	},
	"response_timeout":{
		"zh-CN":"\u670d\u52a1\u5668\u54cd\u5e94\u8d85\u65f6",
		"en":"response timeout"
	},
	"response_error":{
		"zh-CN":"\u670d\u52a1\u5668\u9519\u8bef",
		"en":"server error"
	},
	"m_page_title":{
		"zh-CN":"\u671d\u5915\u533a\u5757\u94fe\u5185\u7ba1\u7cfb\u7edf",
		"en":"authorization system"
	},
	"block_chain":{
		"zh-CN":"\u671d\u5915\u533a\u5757\u94fe\u5185\u7ba1\u7cfb\u7edf",
		"en":"authorization - home"
	},
	"exchange_nav":{
		"zh-CN":"\u5207\u6362\u83dc\u5355\u680f",
		"en":"Shift Nav"
	},
	"Choose_Skin":{
		"zh-CN":"\u9009\u62e9\u76ae\u80a4",
		"en":"Choose Skin"
	},
	"Fixed_Navbar":{
		"zh-CN":"\u56fa\u5b9a\u5bfc\u822a",
		"en":"Fixed Navbar"
	},
	"Fixed_Sidebar":{
		"zh-CN":"\u56fa\u5b9a\u83dc\u5355\u680f",
		"en":"Fixed Sidebar"
	},
	"Fixed_Breadcrumbs":{
		"zh-CN":"\u56fa\u5b9a\u6211\u7684\u4f4d\u7f6e",
		"en":"Fixed Breadcrumbs"
	},
	"Submenu_on_Hover":{
		"zh-CN":"\u60ac\u505c\u5b50\u83dc\u5355",
		"en":"Submenu on Hover"
	},
	"Compact_Sidebar":{
		"zh-CN":"\u7d27\u51d1\u4fa7\u8fb9\u680f",
		"en":"Compact Sidebar"
	},
	"exchange_lang":{
		"zh-CN":"\u5207\u6362\u8bed\u8a00",
		"en":"Shift Language"
	}
}
function getPageProp(key,proObj){
	var currentLang = navigator.language;   //\u5224\u65ad\u9664IE\u5916\u5176\u4ed6\u6d4f\u89c8\u5668\u4f7f\u7528\u8bed\u8a00
	if(!currentLang){//\u5224\u65adIE\u6d4f\u89c8\u5668\u4f7f\u7528\u8bed\u8a00
	    currentLang = navigator.browserLanguage;
	}
	if(!currentLang){//\u5224\u65adIE\u6d4f\u89c8\u5668\u4f7f\u7528\u8bed\u8a00
		currentLang = "en";
	}
	if("zh-CN"!=currentLang&&"en"!=currentLang){
		currentLang="en";
	}
	if(corecode&&corecode.cookie&&corecode.cookie.get("currentLang")){
		currentLang=corecode.cookie.get("currentLang");
	}
	if(proObj&&key in proObj){
		return proObj[key][currentLang];
	}else if(key in _allPro){
		return _allPro[key][currentLang];
	}
	return "";
}
