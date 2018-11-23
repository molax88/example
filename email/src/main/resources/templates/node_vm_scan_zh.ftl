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
				<p style="text-indent:2em;">感谢您参与本次HPB芯链节点申请，系统检测到您通过虚拟机/云服务器申请注册节点，您本次的测试结果已被取消。参与申请HPB节点，请使用通过兼容性测试的物理服务器进行硬件测试。</p>
				<p style="text-indent:2em;">如有问题，您可扫描下方二维码添加工作人员进行咨询。</p>
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