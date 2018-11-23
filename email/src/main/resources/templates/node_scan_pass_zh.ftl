<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<title>HPB节点竞选活动</title>

<style type="text/css">
.main-container{
	height: 100%;
	width: 100%;
}
.footer-inner{
	height:86px;
	text-align: center;
	margin-top: 5%;
	padding-top: 50px;
}
.email-content{
	padding-left: 4px;
}
input[disabled],textarea[disabled] {
    cursor: auto!important;
}
i{
    color:#babec0!important;
}
table tr{
	height: 50px;
}
table td{
	width:250px;
	text-align: center;
}
#mailContentContainer{
	margin-right: 0px !important;
}
</style>
<!--[if lte IE 9]>
  <link rel="stylesheet" href="http://nodeplan.gxn.io/assets/css/ace-ie.min.css" />
<![endif]-->
<!-- inline styles related to this page -->
<!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->
<!--[if lte IE 8]>
	<script src="http://nodeplan.gxn.io/assets/js/html5shiv.min.js"></script>
	<script src="http://nodeplan.gxn.io/assets/js/respond.min.js"></script>
<![endif]-->
</head>
<body class="no-skin" id="hpbIndex">
	<div class="main-container ace-save-state" id="main-container">
		<div class="main-content">
			<div class="row email-content">
			<h2>尊敬的${params.name}:</h2>
			<p style="text-indent:2em;">感谢您参与本次HPB芯链节点注册，很高兴的通知您，您成功通过了本次HPB芯链节点计划硬件测试，请核实您的信息。</p>
			<p>个人信息：<br/>手机号：${params.phone}<br/>邮箱：${params.email}</p>
			<p>硬件信息：</p>
			<table id="customers" border="1" style='border-collapse: collapse;' title="硬件信息">
				<tr>
					<th style="width: 100px;">硬件信息</th>
					<th style="width: 100px;">推荐最低配置</th>
					<th style="width: 100px;">您的硬件测试信息</th>
					<th style="width: 100px;">测试结果</th>
				</tr>
				<tr>
					<td>CPU</td>
					<td>16核  主频2.0GHZ以上</td>
					<td>${(params.cpu)!""}</td>
					<td>${(params.cpuresult)!"通过"}</td>
				</tr>
				<tr>
					<td>内存</td>
					<td>32G</td>
					<td>${(params.mem)!""}</td>
					<td>${(params.memresult)!"通过"}</td>
				</tr>
				<tr>
					<td>硬盘</td>
					<td>8T 7200转SAS接口硬盘/8T SSD</td>
					<td>${(params.disk)!""}</td>
					<td>${(params.diskresult)!"通过"}</td>
				</tr>
				<tr>
					<td>网络</td>
					<td>双向带宽最低20Mbps
						推荐双向带宽100Mbps</td>
					<td>${(params.bandwidth)!""}</td>
					<td>${(params.bandwidthresult)!"通过"}</td>
				</tr>
				<tr>
					<td>系统</td>
					<td>Ubuntu 16.04  64bit
						Centos 7.0及以上</td>
					<td>${(params.os)!""}</td>
					<td>${(params.osresult)!"通过"}</td>
				</tr>
			</table>
			<p style="text-indent:2em;">竞选HPB芯链节点需实名认证，接受社区监督，因此，我们会在接下来的一段时间与您联系，进行实名认证，请您保证联系畅通。</p>
			<p style="text-indent:2em;">本次节点计划申请时间为2018年7月1日—2018年7月31日，BOE硬件加速引擎分发开始时间为2018年8月1日。为了您顺利获得BOE板卡，成功参与到HPB芯链节点竞选，请您密切关注您的全网排名情况。</p>
			<p style="text-indent:2em;color:red">排名入选须知：请用申请地址转账0.1个HPB至官方地址0x6dE4Ef6F79e6BB461e59f9f026A898174E434564，核实为真实有效地址后，即可进入全网排名。（如果地址有变动，请联系工作人员更改，否则以申请地址为准。）</p>
			<p style="text-indent:2em;">如果您有其他问题，您可扫描下方二维码添加工作人员进行咨询。</p>
			<div style="width:100%;height: 100px;text-align: center">
			<img style="width:90px;height: 90%;" src="http://nodeplan.gxn.io/assets/images/weixin-help.jpg"/>
			<p style="text-align: center;font-weight: bold;">客服微信</p>
			</div>
		</div>
		</div>
		<div class="footer" style="margin-top:40px;">
			<div class="footer-inner">
				<div class="footer-content">
					<div><img width="90px;" src="http://nodeplan.gxn.io/assets/images/foot.png"/></div>
					<span class="smaller-20"> <span class="white bolder">
						2017-2018&copy; HPB Foundation. All rights reserved</span>
					</span>
				</div>
			</div>
		</div>
	</div>
</body>
</html>