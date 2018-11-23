package io.hpb.contract.controller;

import io.hpb.contract.common.AccountConstant;
import io.hpb.contract.common.BcConstant;
import io.hpb.contract.exception.BusinessException;
import io.hpb.contract.service.BaseService;
import io.hpb.contract.utils.LogProcessUtil;
import io.hpb.contract.utils.ObjectJsonHelper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class CommonUploadDownloadController extends BaseController{
	private static final String USER_AGENT = "User-Agent";
	private static final String CONTENT_DISPOSITION = "Content-disposition";
	private static final String REQ_STR_LIST = "reqStrList";
	@RequestMapping(value = "/invokeHpbUploadDownloadCmd", method = RequestMethod.POST)
	public void invokeHpbCmd(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		try (LogEnd o = LogEnd.getInstance()) {
			final String METHODNAME = "invokeHpbCmd";
			String proccessId = getProccessId(request);
			try {
				String reqStr =request.getParameter(REQ_STR_LIST);
				if(StringUtils.isBlank(reqStr)) {
					LOG_EXIT(METHODNAME, proccessId);
					o.close();
					throw new BusinessException(BcConstant.ERROR_CODE,"传递的参数不合格，请检查传输的参数");
				}
				@SuppressWarnings("unchecked")
				List<String> reqStrList=ObjectJsonHelper.deserialize(reqStr, List.class);
				LOG_ENTRY(METHODNAME, proccessId, request.getRequestURL().toString(), reqStrList);
				if (reqStrList.size() > 4) {
					//获取service实现
					BaseService serviceImpl = fetchServiceImpl(reqStrList.get(0));
					param.put(BcConstant.RETURN_URL, reqStrList.get(2));
					param.put(BcConstant.PROCESS_ID, proccessId);
					param.put(BcConstant.SESSION_ID, getSessionId(request,response));
					//调用service
					Map<String, Object> returnParam=null;
					String methodName="poccess";
					try {
						LogProcessUtil.LOG_ENTRY(serviceImpl.getClass(),
								methodName, proccessId, request.getRequestURL().toString(), param);
						param.put(BcConstant.REQUEST, request);
						param.put(BcConstant.RESPONSE, response);
						returnParam = serviceImpl.poccess(param);
						String isDownloadFile = MapUtils.getString(returnParam, AccountConstant.IS_DOWNLOAD_FILE);
						if(AccountConstant.FALSE.equals(isDownloadFile)) {
							returnParam.remove(AccountConstant.IS_DOWNLOAD_FILE);
							if(MapUtils.isEmpty(returnParam)) {
								returnParam=new HashMap<String, Object>();
							}
							//处理返回的结果
							String decodeKey=BcConstant.DECODEKEY_DEFAULT;
							String rsaKey=BcConstant.RSAKEY_DEFAULT;
							List<Object> result = processResult(returnParam,decodeKey,rsaKey);
							LOG_EXIT(METHODNAME, proccessId);
							o.close();
							if(MapUtils.isNotEmpty(returnParam)) {
								OutputStream outputStream = response.getOutputStream();
								outputStream.write(ObjectJsonHelper.serialize(result)
										.getBytes(StandardCharsets.UTF_8));
								outputStream.flush();
								IOUtils.closeQuietly(outputStream);
							}
						}
						if(AccountConstant.TRUE.equals(isDownloadFile)) {
							returnParam.remove(AccountConstant.IS_DOWNLOAD_FILE);
							File file = (File) MapUtils.getObject(returnParam, AccountConstant.DOWNLOAD_FILE_INFO);
					        String fileName=FilenameUtils.getName(file.getAbsolutePath());
					        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());  
					        //告诉客户端以什么解码方式打开文件  
					        response.setContentType(StandardCharsets.UTF_8.name());  
					        //告诉客户端下载文件  
					        if (request.getHeader(USER_AGENT).toLowerCase().indexOf("firefox") > -1) {  
					        	response.setHeader(CONTENT_DISPOSITION, String.format("attachment; filename*=UTF-8''\"%s\"", fileName));
					        } else {              
					        	response.setHeader(CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", fileName));
					        } 
							FileInputStream inputStream = FileUtils.openInputStream(file);
							OutputStream outputStream = response.getOutputStream();
							IOUtils.copyLarge(inputStream, outputStream);
							outputStream.flush();
							IOUtils.closeQuietly(inputStream);
							IOUtils.closeQuietly(outputStream);
						}
						
						LogProcessUtil.LOG_EXIT(serviceImpl.getClass(), proccessId, methodName);
					}  catch (Exception e) {
						if(MapUtils.isEmpty(returnParam)) {
							returnParam=new HashMap<String, Object>();
						}
						returnParam.put(BcConstant.RETURN_CODE,BcConstant.ERROR_CODE);
						returnParam.put(BcConstant.RETURN_MSG,e.getMessage());
						LOG_ERROR(METHODNAME, proccessId,e.getMessage());
					}
				}else {
					LOG_EXIT(METHODNAME, proccessId);
					o.close();
					throw new BusinessException(BcConstant.ERROR_CODE,"传递的参数不合格，请检查传输的参数");
				}
			} catch (Exception e) {
				LOG_EXIT(METHODNAME, proccessId);
				o.close();
				throw e;
			}
		}
	}
	@Override
	protected Class<?> getBeanClass() {
		return this.getClass();
	}
}
