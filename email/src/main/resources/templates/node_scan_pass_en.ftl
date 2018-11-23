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
#customers tr th{
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
			<p>Thank you for participating in the HPB Node Application. We are pleased to inform you that your server and/ or broadband have passed testing requirements. </p>
			<p>Testing information and data:</p>
			<p>Personal information:<br/>Mobile phone number:${params.phone}<br/>E-mail:${params.email}</p>
			<p>Hardware information:</p>
			<table id="customers" border="1" style='border-collapse: collapse;' title="hardware info">
				<tr>
					<th style="width: 200px;">Hardware information </th>
					<th style="width: 300px;">BOE-Node Configuration Requirements</th>
					<th style="width: 200px;">Hardware test information</th>
					<th style="width: 200px;">Testing results</th>
				</tr>
				<tr>
					<td style="width: 200px;">CPU</td>
					<td style="width: 300px;">16 CORE 2.0GHZ or above</td>
					<td style="width: 200px;">${(params.cpu)!""}</td>
					<td style="width: 200px;">${(params.cpuresult)!"Pass"}</td>
				</tr>
				<tr>
					<td style="width: 200px;">Memory</td>
					<td style="width: 300px;">32G</td>
					<td style="width: 200px;">${(params.mem)!""}</td>
					<td style="width: 200px;">${(params.memresult)!"Pass"}</td>
				</tr>
				<tr>
					<td style="width: 200px;">Hard disk</td>
					<td style="width: 300px;">8T 7200 SAS Interface Hard disk
                        / 8T SSD</td>
					<td style="width: 200px;">${(params.disk)!""}</td>
					<td style="width: 200px;">${(params.diskresult)!"Pass"}</td>
				</tr>
				<tr>
					<td style="width: 200px;">Network</td>
					<td style="width: 300px;">Minimum bi-directional bandwidth 20Mbps (for each direction)
                        Recommended bi-directional bandwidth 100Mbps (for each direction)</td>
					<td style="width: 200px;">${(params.bandwidth)!""}</td>
					<td style="width: 200px;">${(params.bandwidthresult)!"Pass"}</td>
				</tr>
				<tr>
					<td style="width: 200px;">System</td>
					<td style="width: 300px;">Ubuntu 16.04 64bit
                        Centos 7.2 64bit (currently only for RH2288V3)</td>
					<td style="width: 200px;">${(params.os)!""}</td>
					<td style="width: 200px;">${(params.osresult)!"Pass"}</td>
				</tr>
			</table>
			<p>With the Node Application and system requirements testing successfully completed, the next step will be the real-name verification process. A member of the HPB team will contact you to complete the KYC requirements. </p>
			<p style="font-weight: bold">Enter the HPB BOE Acceleration Engine hardware ranking, activate your application wallet address by sending 0.1 HPB to the official HPB wallet address: 0x6dE4Ef6F79e6BB461e59f9f026A898174E434564.</p>
			<p style="font-weight: bold">A member of the team will verify your wallet address and you will be able to enter the HPB BOE Acceleration Engine hardware ranking (in case of wallet address changes please contact the HPB team member who has reached out to you for the real-name verification).</p>
			<p style="">The HPB Node Application is open from 1 July 2018 – 31 July 2018. Applicants who have not passed the testing requirements, may re-run testing until minimum requirements are met. The BOE Acceleration Engine hardware unit distribution will start as per 1 August 2018. As stated in the HPB Node Plan, the top ranking 200 applicants will be entitled to receive a hardware unit. Please refer to the Node Application ranking on our website – www.hpb.io</p>
			<br/>
			<div style="width:100%;height: 100px;text-align: center">
			<img style="width:90px;height: 90%;" src="http://nodeplan.gxn.io/assets/images/telegram-help.jpg"/>
			<p style="text-align: center;font-weight: bold;">Scan the QR code to join the official Telegram</p>
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