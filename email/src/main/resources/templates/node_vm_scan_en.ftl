<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<title>HPB Node Plan</title>

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
#customers{
    text-align: left;
}
#customers tr{
    height: 50px;
}
#customers tr th {
    text-align: left;
    color: white;
    vertical-align: top;
	padding-top: 4px;
    padding-left: 4px;
    background-color: rgb(47,85,151);
}
#customers tr td{
    padding-left: 4px;
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
				<p>Dear ${params.name}:</p>
				<br/>
				<p>Thank you for your participation in our HPB Node Application Campaign. The system has detected that you have registered and applied using a Virtual Machine / Cloud Server. As a result, your test results have been voided and your application has been cancelled.</p>
				<br/>
				<p>If you wish to re-apply, we kindly ask you to complete testing using a physical server that has passed the compatibility tests and is listed on our “Server Compatibility Test List”. If you have any questions or concerns regarding this message, please scan the QR-code below to connect with our HPB support staff.</p>
				<div style="width:100%;height: 100px;text-align: center">
				<br/>
				<img style="width:90px;height: 90%;" src="http://nodeplan.gxn.io/assets/images/telegram-help.jpg"/>
				<p style="text-align: center;font-weight: bold;">Scan the QR code to join the official Telegram</p>
				</div>
				<div style="width: 99%;margin-top: 80px;">
				<p>With kind regards,</p>
				<p>HPB Support</p>
				<p>- HPB Foundation</p>
                </div>
			</div>
			</div>
			<div class="footer" style="margin-top:40px;">
				<div class="footer-inner">
					<div class="footer-content">
						<div><img width="90px;" src="http://nodeplan.gxn.io/assets/images/foot.png"/></div>
						<span class="smaller-20"> <span class="white bolder">
							2017-2018&copy; HPB Foundation. All rights reserved.</span>
						</span>
					</div>
				</div>
			</div>
		</div>
</body>
</html>