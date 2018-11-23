package io.hpb.contract.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImageUploadUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageUploadUtil.class);
    // 图片类型
    private static List<String> fileTypes = new ArrayList<String>();

    static {
        fileTypes.add(".jpg");
        fileTypes.add(".jpeg");
        fileTypes.add(".bmp");
        fileTypes.add(".gif");
        fileTypes.add(".png");
    }

    /**
     * 图片上传
     * 
     * @Title upload
     * @param request
     * @param DirectoryName  文件上传目录：/data/cms
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public static String upload(MultipartHttpServletRequest request, String DirectoryName) throws IllegalStateException,
            IOException {
        // 创建一个通用的多部分解析器
        // 图片名称
        String fileName = null;
        // 判断 request 是否有文件上传,即多部分请求
        // 转换成多部分request
        MultipartHttpServletRequest multiRequest = request;
        // 取得request中的所有文件名
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            // 记录上传过程起始时的时间，用来计算上传时间
            // 取得上传文件
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file != null) {
                // 取得当前上传文件的文件名称
                String myFileName = file.getOriginalFilename();
                // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                if (myFileName.trim() != "") {
                    // 获得图片的原始名称
                    String originalFilename = file.getOriginalFilename();
                    // 获得图片后缀名称,如果后缀不为图片格式，则不上传
                    String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                    if (!fileTypes.contains(suffix)) {
                    	logger.error("不支持的文件类型,文件名：{}", originalFilename);
                        continue;
                    }
                    // 重命名上传后的文件名 UUID-111112323.jpg
                    fileName = UUIDGeneratorUtil.generate(originalFilename)+"-"+System.nanoTime() + suffix;
                    
                    OssUtil.uploadImageFile(file.getInputStream(), fileName, true);
                    logger.info("上传文件成功,原始文件名：{},OSS key:{}", originalFilename,fileName);
                }
            }
        }
        return fileName;
    }
  
    /**
     * 修改主图 ,传回图片路径，实现预览效果。
     * 
     * @param request
     * @param response
     * @param domain   		图片访问的域名前缀
     * @param DirectoryName 文件上传的服务器路径
     * @throws IOException
     */
    //4.0+版本
    public static String updateMasterImage(MultipartHttpServletRequest request,String DirectoryName)
    		throws IOException {
    	return upload(request, DirectoryName);
    }
    /**
     * ckeditor文件上传功能，回调，传回图片路径，实现预览效果。
     * 
     * @Title ckeditor
     * @param request
     * @param response
     * @param domain         显示域名+文件名    == 全路径   www.hpb.io/images/ + filename.jpg
     * @param DirectoryName  服务器文件路径      /data/cms
     * @throws IOException
     */
    //4.0+版本
    public static void ckeditor(MultipartHttpServletRequest request, HttpServletResponse response,String domain, String DirectoryName)
            throws IOException {
        String fileName = upload(request, DirectoryName);
        // 结合ckeditor功能
        // imageContextPath为图片在服务器地址，如upload/123.jpg,非绝对路径

        String imageContextPath = request.getContextPath() + File.separator + DirectoryName + File.separator + fileName;

        if(StringUtils.hasText(domain)) {
        	imageContextPath = domain + fileName;
        }
        response.setContentType("text/html;charset=UTF-8");
        String callback = request.getParameter("CKEditorFuncNum");
        PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + imageContextPath + "',''" + ")");
        out.println("</script>");
        out.flush();
        out.close();
    }
    
    /**
     * 文件上传功能，返回文件名
     * @param request
     * @param response
     * @param DirectoryName 服务器路径
     * @throws IOException
     */
    public static void uploadImage(MultipartHttpServletRequest request, HttpServletResponse response, String DirectoryName)
    		throws IOException {
    	String fileName = upload(request, DirectoryName);
    	response.setContentType("text/html;charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	out.print(AppObjectUtil.toJson(fileName));
    	out.flush();
    	out.close();
    }
}