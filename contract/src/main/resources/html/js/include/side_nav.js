function loadNavData(){
	$.ajaxPostReq({
		"serviceCode":"permission",
		"serviceType":"resource",
		"param":{}
	},function(data){
		if(data&&data.code=="40000"){
			toLogout();
		}
		if(data&&data.code=="200"){
			if(data.msg&&data.msg.msg){
				genNav(data.msg.msg);
			}
		}
	})
};
function genNav(navData){
	var sideNavUl=$("#side_nav_ul");
	var pageHash=window.location.hash.replace("#","");
	if(!pageHash||"/index/"==pageHash){
		$.include("page/portal/portal-content.html","#page-content-detail");
		changeProcessStep({},"/index/");
	}
	$.each(navData,function(i,ndata){
		if(!ndata.hash){
			if(ndata.pageUrl){
				ndata.hash=pageUrl.replace("page/",'/').replace(".html","/")
			}
		}
		if(pageHash==ndata.hash){
			ndata.state="active";
		}
		if('string'==typeof(ndata.text)){
			ndata.text={
	        	"zh-CN":ndata.text,
	    		"en":""
	        }
		}
		if('string'==typeof(ndata.iconCls)){
			ndata.iconCls=[ndata.iconCls];
		}
		var li=$("<li></li>");
		sideNavUl.append(li);
		li.attr({
			id:"side_nav_ul_li"+ndata.id
		});
		var a=$("<a href='javascript:void(0);' class='dropdown-toggle'></a>");
		li.append(a);
		if(!("children" in ndata)&&ndata.hrefUrl){
			a.attr({
				onclick:"javascript:changePages("+$.toJSON(ndata)+");"
			});
			if(pageHash==ndata.hash){
				changePages(ndata);
			}
		}
		var menuIcon=$("<i class='menu-icon fa'></i>");
		if(ndata.iconCls){
			$.each(ndata.iconCls,function(i,icon){
				menuIcon.addClass(icon);
			});
		}
		a.append(menuIcon);
		var menuText=$("<span class='menu-text'></span>");
		menuText.html(getPageProp("key",{"key":ndata.text}));
		menuText.attr({
			"hpb_text_data":$.toJSON(ndata.text)
		});
		a.append(menuText);
		if("children" in ndata){
			a.append($("<b class='arrow fa fa-angle-down'></b>"));
			a.addClass("dropdown-toggle");
			a.attr({
				"data-toggle":"dropdown"
			});
			li.append($("<b class='arrow'></b>"));
			var ul=$("<ul class='submenu'></ul>");
			li.append(ul);
			$.each(ndata.children,function(i,child){
				if(!child.hash){
					if(child.hrefUrl){
						child.hash=child.hrefUrl.replace("page/",'/').replace(".html","/");
					}
				}
				if('string'==typeof(child.text)){
					child.text={
			        	"zh-CN":child.text,
			    		"en":""
			        }
				}
				if('string'==typeof(child.iconCls)){
					child.iconCls=[child.iconCls];
				}
				var cli=$("<li></li>");
				cli.attr({
					id:"side_nav_ul_li"+child.id
				});
				ul.append(cli);
				var ca=$("<a href='javascript:void(0);' class='dropdown-toggle'></a>");
				ca.html(getPageProp("key",{"key":child.text}));
				ca.prepend($("<i class='menu-icon fa fa-caret-right'></i>"));
				ca.attr({
					"hpb_text_data":$.toJSON(child.text)
				});
				ca.prepend($("<b class='arrow icon-angle-down'></b>"));
				cli.append(ca);
				if(child.hrefUrl){
					ca.attr({
						onclick:"javascript:changePages("+$.toJSON($.extend({},{iconCls:ndata.iconCls},child))+");"
					});
					if(pageHash==child.hash){
						changePages(child);
						li.addClass("open");
						cli.addClass("active");
					}
				}
			});
		}else{
			a.append($("<b class='arrow fa fa-angle-right'></b>"));
		}
	});
}