package io.hpb.contract.utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImgCompress {
	private static final Logger log = LoggerFactory.getLogger(ImgCompress.class);
    private static Image img;  
    private static int width;  
    private static int height;  
    private static File tempFile;  
    
    /** 
     * 按照宽度还是高度进行压缩 
     * @param w int 最大宽度 
     * @param h int 最大高度 
     */  
    private static byte[] resizeFix(int w, int h) throws IOException { 
    	try {
			if(img!=null){
				width = img.getWidth(null);    // 得到源图宽  
				height = img.getHeight(null);  // 得到源图长  
				tempFile=File.createTempFile("ImgTempFile", "jpg");
			}
			if (width / height > w / h) {  
			    return resizeByWidth(w);  
			} else {  
			    return resizeByHeight(h);  
			}
		} catch (Exception e) {
			if(tempFile!=null&&tempFile.exists()){
				tempFile.delete();
			}
			log.error(e.getMessage(), e);
			return null;
		}  
    }  
    public static byte[] resizeFix(InputStream input,int w, int h) throws IOException { 
    	img = ImageIO.read(input);      // 构造Image对象  
    	return resizeFix(w, h);
    }  
    public static byte[] resizeFix(File file,int w, int h) throws IOException { 
    	img = ImageIO.read(file);      // 构造Image对象  
    	return resizeFix(w, h);
    }  
    public static byte[] resizeFix(String fileName,int w, int h) throws IOException { 
    	File file = new File(fileName);// 读入文件  
    	img = ImageIO.read(file);      // 构造Image对象  
    	return resizeFix(w, h);
    }  
    /** 
     * 以宽度为基准，等比例放缩图片 
     * @param w int 新宽度 
     */  
    private static byte[] resizeByWidth(int w) throws IOException {  
        int h = (int) (height * w / width);  
        return resize(w, h);  
    }  
    /** 
     * 以高度为基准，等比例缩放图片 
     * @param h int 新高度 
     */  
    private static byte[] resizeByHeight(int h) throws IOException {  
        int w = (int) (width * h / height);  
        return  resize(w, h);  
    }  
    /** 
     * 强制压缩/放大图片到固定的大小 
     * @param w int 新宽度 
     * @param h int 新高度 
     */  
    private static byte[] resize(int w, int h) throws IOException {  
        // SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢  
        BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB );   
        image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
        FileOutputStream out=null;
        try{
         out = new FileOutputStream(tempFile); // 输出到文件流  
         ImageIO.write(image, ImageUtils.ImageFormat.JPEG.name(), out);
        }finally {
            IOUtils.closeQuietly(out);
        }
        byte[] byteArray = FileCopyUtils.copyToByteArray(tempFile);
        if(tempFile!=null&&tempFile.exists()){
        	tempFile.delete();
        }
		return byteArray;
    }  
    
    
}