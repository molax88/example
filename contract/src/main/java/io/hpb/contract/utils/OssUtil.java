package io.hpb.contract.utils;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//阿里云对象存储服务OSS工具
public class OssUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);
	
	//product
	public static String domain="http://dapp-prod-fileserver01.oss-cn-hongkong.aliyuncs.com";
	public static String endpoint="http://oss-cn-hongkong.aliyuncs.com";
	
	//test
//	public static String domain="https://dapp-test.oss-cn-hongkong.aliyuncs.com";
//	public static String endpoint="https://oss-cn-hongkong.aliyuncs.com";
 
 
	// 图片处理，需要用单独的地址。访问、裁剪、缩放、效果、水印、格式转换等服务。
//	public static String endpointImg = "http://img-cn-hangzhou.aliyuncs.com";
 
	public static String accessKeyId = "LTAIMuTqZgfqQpvi";
	public static String accessKeySecret = "52BTTCCey6n9WI2oqIZ63O3pmok0m7";
	public static String bucketName = "dapp-prod-fileserver01";
	
	
	public static String tmp = "/tmp";
 
	// 单例，只需要建立一次链接
	private static OSSClient client = null;
	// 是否使用另外一套本地账户
	public static final boolean MINE = false;
 
 
	static {
		if (MINE) {
			accessKeyId = "LTAIMuTqZgfqQpvi";
			accessKeySecret = "52BTTCCey6n9WI2oqIZ63O3pmok0m7";
			bucketName = "dapp-prod-fileserver01";
			endpoint = "https://oss-cn-hongkong.aliyuncs.com";
		}
		tmp = System.getProperty("java.io.tmpdir","/tmp");
		
	}
 
 
	//配置参数
	static ClientConfiguration config() {
		ClientConfiguration conf = new ClientConfiguration();
		conf.setMaxConnections(100);
		conf.setConnectionTimeout(5000);
		conf.setMaxErrorRetry(3);
		conf.setSocketTimeout(2000);
		return conf;
	}
 
 
	//客户端
	public static OSSClient client() {
		if (client == null) {
			ClientConfiguration conf = config();
			client = new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);
			makeBucket(client, bucketName);
		}
		return client;
	}
 
 
	//创建Bucket
	public static void makeBucket(String bucketName) {
		OSSClient client = client();
		makeBucket(client, bucketName);
	}
 
 
	//创建Bucket
	public static void makeBucket(OSSClient client, String bucketName) {
		boolean exist = client.doesBucketExist(bucketName);
		if (exist) {
			p("The bucket exist.");
			return;
		}
		client.createBucket(bucketName);
	}
 
 
	//上传一个文件，InputStream
	public static PutObjectResult uploadFile(InputStream is, String key) {
		OSSClient client = client();
		PutObjectRequest putObjectRequest = new PutObjectRequest(
				OssUtil.bucketName, key, is);
		return client.putObject(putObjectRequest);
	}
	
	
	/**
	 * 上传文件流,重命名,压缩
	 * @param is
	 * @param key
	 * @param compress
	 * @return
	 */
	public static PutObjectResult uploadImageFile(InputStream is, String key,boolean compress) {
		File destination = new File(tmp+File.separator+key);
		try {
			FileUtils.copyInputStreamToFile(is, destination );
			if(compress) {
				googleImgCompress(destination);
			}
		} catch (Exception e) {
			throw new RuntimeException("uploadFile failed",e);
		}
		return uploadFile(destination,key);
	}
 
 
	//上传一个文件，File
	public static PutObjectResult uploadFile(File file, String key) {
		OSSClient client = client();
		PutObjectRequest putObjectRequest = new PutObjectRequest(
				OssUtil.bucketName, key, file);
		return client.putObject(putObjectRequest);
	}
	
	public static PutObjectResult uploadFile(File file, String key,Boolean compress) {
		OSSClient client = client();
		if(compress) {
			googleImgCompress(file);
		}
		PutObjectRequest putObjectRequest = new PutObjectRequest(OssUtil.bucketName, key, file);
		return client.putObject(putObjectRequest);
	}
 
 
	//下载一个文件到本地
	public static OSSObject downloadFile(String key) {
		OSSClient client = client();
		GetObjectRequest getObjectRequest = new GetObjectRequest(
				OssUtil.bucketName, key);
		OSSObject object = client.getObject(getObjectRequest);
		return object;
	}
 
 
	//上传某个文件到某个目录，key是自动生成的
	public static String uploadFile(MultipartFile file, String dir)
			throws IOException {
		if (null != file && !file.isEmpty() && file.getSize() > 0) {
			String fileName = UUIDGeneratorUtil.get32UUID()
					+ "."
					+ StringUtils.substringAfterLast(
							file.getOriginalFilename(), ".");
			String ymd = DateUtil.getDateStamp();
			String key = dir + ymd + "/" + fileName;
			OssUtil.uploadFile(file.getInputStream(), key);
			return key;
		}
		return null;
	}
 
 
	/*//删除某个文件
	public static void delete(String key) {
		if (BackendConst.OSS_DELTE_IMG) {
			try {
				client().deleteObject(OssUtil.bucketName, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
 
 
	//创建目录，不能以斜杠“/”开头
	public static void makeDir(String keySuffixWithSlash) {
		OSSClient client = client();
		
		 * Create an empty folder without request body, note that the key must
		 * be suffixed with a slash
		 
		if (StringUtils.isEmpty(keySuffixWithSlash)) {
			return;
		}
		if (!keySuffixWithSlash.endsWith("/")) {
			keySuffixWithSlash += "/";
		}
		client.putObject(bucketName, keySuffixWithSlash,
				new ByteArrayInputStream(new byte[0]));
	}
 
 
	// 实时的分页查询
	public static OssPage listPage(String dir, String nextMarker,
			Integer maxKeys) {
		OSSClient client = client();
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(
				bucketName);
		if (StringUtils.isNoneBlank(dir)) {
			listObjectsRequest.setPrefix(dir);
		}
		if (StringUtils.isNoneBlank(nextMarker)) {
			listObjectsRequest.setMarker(nextMarker);
		}
		if (maxKeys != null) {
			listObjectsRequest.setMaxKeys(maxKeys);
		}
		ObjectListing objectListing = client.listObjects(listObjectsRequest);
 
 
		List<OSSObjectSummary> summrayList = objectListing.getObjectSummaries();
		List<OssItem> itemList = summaryToItem(summrayList);
		OssPage page = new OssPage();
 
 
		String newxNextMarker = objectListing.getNextMarker();
		page.setNextMarker(newxNextMarker);
		page.setSummrayList(itemList);
		return page;
	}
 
 
	//把OSS的对象，转换成自己的。因为OSS的对象没有实现Serialiable，不能序列化。
	private static List<OssItem> summaryToItem(
			List<OSSObjectSummary> summrayList) {
		List<OssItem> itemList = new ArrayList<OssItem>();
		for (OSSObjectSummary summary : summrayList) {
			OssItem item = new OssItem();
			try {
				BeanUtils.copyProperties(item, summary);
				itemList.add(item);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return itemList;
	}
 
 
	//一次迭代，获得某个目录下的所有文件列表
	public static List<OssItem> listAll(String dir) {
		OSSClient client = client();
		List<OssItem> list = new ArrayList<OssItem>();
		// 查询
		ObjectListing objectListing = null;
		String nextMarker = null;
		final int maxKeys = 1000;
 
 
		do {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(
					bucketName).withPrefix(dir).withMarker(nextMarker)
					.withMaxKeys(maxKeys);
			objectListing = client.listObjects(listObjectsRequest);
 
 
			List<OSSObjectSummary> summrayList = objectListing
					.getObjectSummaries();
			List<OssItem> itemList = summaryToItem(summrayList);
			list.addAll(itemList);
			nextMarker = objectListing.getNextMarker();
		} while (objectListing.isTruncated());
		return list;
	}*/
 
 
	public static void p(Object str) {
		logger.info("OSSUtil message:{}", str);
	}
 
	public static void print(OSSException oe) {
		p("Caught an OSSException, which means your request made it to OSS, "
				+ "but was rejected with an error response for some reason.");
		p("Error Message: " + oe.getErrorCode());
		p("Error Code:       " + oe.getErrorCode());
		p("Request ID:      " + oe.getRequestId());
		p("Host ID:           " + oe.getHostId());
	}
	
	private static void googleImgCompress(File file){
		try {
			Thumbnails.of(file) 
					  .scale(1f) 
			          .outputQuality(0.5f) 
			          .toFile(file);
		} catch (Exception e) {
			throw new RuntimeException("compress file failed",e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\zhangjian\\Pictures\\test.jpg");
		
		googleImgCompress(file);
		
		OssUtil.uploadFile(file, "test.jpg");
	}
	
}