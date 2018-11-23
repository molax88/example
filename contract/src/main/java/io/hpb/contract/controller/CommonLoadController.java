package io.hpb.contract.controller;

import io.hpb.contract.common.BcConstant;
import io.hpb.contract.config.AccountConfig;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.exception.ResponseMessage;
import io.hpb.contract.service.BaseService;
import io.hpb.contract.utils.AppObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class CommonLoadController extends BaseController {
	final String METHODNAME = "invokeHpbDownloadCmdCmd";
	private static final Logger log = LoggerFactory.getLogger(CommonLoadController.class);
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private AccountConfig config;
	
	@RequestMapping(value = "/invokeHpbDownloadCmd", method = RequestMethod.GET)
	public Map<String,Object> invokeHpbDownloadCmd(String filePath,String accessToken, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String processId = getProccessId(request);
//		reqParam.put("processId",processId);
//		LOG_ENTRY(METHODNAME, processId, request.getRequestURL().toString(), reqParam);

		Map<String, Object> resp = new HashMap<>();
		resp.put("code", ResponseMessage.SUCCESS.getCode());
		resp.put("status", ResponseMessage.SUCCESS.getStatus());
		resp.put("processId", processId);
		try {
			String serviceCode = "";
//			String serviceCode = MapUtils.getString(reqParam, "serviceCode");
//			String serviceType = MapUtils.getString(reqParam, "serviceType");
//			String accessToken = MapUtils.getString(reqParam, "accessToken");
//			if(!"logon".equals(serviceType) && !"weblogon".equals(serviceType)) {
				if(!checkLogon(accessToken)) {
					serviceCode = "userLogon";
				}
//			}
			if(!"userLogon".equals(serviceCode)) {
//				String filepath = MapUtils.getString(reqParam, "filePath");
				File file = new File(filePath);
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
				response.setHeader("Content-Type","application/octet-stream");
				response.setHeader("Content-Length", String.valueOf(file.length()));

				bis = new BufferedInputStream(new FileInputStream(file));
				bos = new BufferedOutputStream(response.getOutputStream());
				byte[] buff = new byte[2048];
				while (true) {
					int bytesRead;
					if (-1 == (bytesRead = bis.read(buff, 0, buff.length))) break;
					bos.write(buff, 0, bytesRead);
				}
				bis.close();
				bos.close();
			}else{
				throw new BusinessException("用户未登陆");
			}
			log.info("响应数据："+AppObjectUtil.toJson(resp));
			return resp;
		}catch (BusinessException e) {
			log.error(e.getMessage(), e);
	    }catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return resp;
		
	}

	/**导入文件
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/invokeHpbUploadCmd", method = RequestMethod.POST)
	public Map<String,Object> invokeHpbUploadCmd(@RequestParam(value="file",required=false) MultipartFile file) throws Exception{
		String processId = getProccessId(request);
		String serviceCode = request.getParameter("serviceCode");
		String serviceType = request.getParameter("serviceType");
		String accessToken = request.getParameter("accessToken");
		if(checkLogon(accessToken)) {
			Map<String, Object> reqParam = new HashMap<>();
			reqParam.put(BcConstant.SERVICE_TYPE, serviceType);
			reqParam.put("file", file);
			BaseService serviceImpl = fetchServiceImpl(serviceCode);
			String methodName="poccess";
			Map<String, Object> returnParam = serviceImpl.poccess(reqParam);
			Map<String, Object> resp = new HashMap<>();
			resp.put("code", ResponseMessage.SUCCESS.getCode());
			resp.put("status", ResponseMessage.SUCCESS.getStatus());
			resp.put("processId", processId);
			resp.put("accessToken", accessToken);
			resp.put("msg", returnParam);
			return resp;
		}else{
			throw new BusinessException("用户未登陆");
		}
	}

	@Override
	protected Class<?> getBeanClass() {
		return this.getClass();
	}

}
