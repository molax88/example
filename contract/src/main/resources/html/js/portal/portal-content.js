/*$.ajaxPostReq({
	"serviceCode":"index",
	"serviceType":"init",
	"encode":"false",
	"param":{
		"serviceType":"init"
	}
},function(data){
	$.ajaxLoaded();
	CheckSubmitted.requestComplete();
	if(data&&data.code=="40000"){
		toLogout();
	}
	if(data&&data.code=="200"){
		init(data);
	}
})*/
function init(data){
    if(!data || !data.msg){
    	return;
    }
	$("#organ").text(data.msg.organTotal);
	$("#user").text(data.msg.userTotal);
	$("#trade").text(data.msg.tradeTotal);
	Highcharts.chart('echart', {
	  chart: {
			type:'spline'
	  },
	  credits: {
		     enabled: false
	  },
	  title: {
		    text: null
	  },
	  xAxis: {
	    	categories:['01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17',
          	'18','19','20','21','22','23','24','25','26','27','28','29','30','31'],
	        allowDecimals: false
	  },
	  yAxis: {
	    title: {
	      text: null
	    },
	    gridLineWidth:'0px'
	  },
	  legend: {
	    layout: 'vertical',
	    align: 'right',
	    verticalAlign: 'middle'
	  },
	  plotOptions: {
	        area: {
	            marker: {
	            	radius:0
	            }
	        }
	  },
	  series: [{name:"用户数",data:data.msg.users.data, marker:{//线上数据点  
          symbol:'circle',//圆点显示  
          radius:0,  
          lineWidth:2,  
          states:{  
              hover:{  
                  enabled:false  
              }  
          }  
      } },
      {name:"机构数",data:data.msg.organ.data, marker:{//线上数据点  
          symbol:'circle',//圆点显示  
          radius:0,  
          lineWidth:2,  
          states:{  
              hover:{  
                  enabled:false  
              }  
          }  
      } },
      {name:"交易数",data:data.msg.trade.data, marker:{//线上数据点  
          symbol:'circle',//圆点显示  
          radius:0,  
          lineWidth:2,  
          states:{  
              hover:{  
                  enabled:false  
              }  
          }  
      } }],
	  responsive: {
	    rules: [{
	      condition: {
	        maxWidth: 500
	      },
	      chartOptions: {
	        legend: {
	          layout: 'horizontal',
	          align: 'center',
	          verticalAlign: 'bottom'
	        }
	      }
	    }]
	  }
	});
}