package io.hpb.contract.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public abstract class SecurityUtils{
	private static final String CIPHER_AES_NAME = "AES";
	private static final String CIPHER_AES_MODE = "AES/CBC/PKCS5Padding";
	private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
	/**
	 * 使用AES算法做数据解密处理。
	 * 
	 * @param key
	 *            解密Key（16字节）
	 * @param iv
	 *            解密初始化向量（16字节）
	 * @param encData
	 *            拟解密的数据（UTF8）
	 * @return 原始数据
	 */
	public static byte[] aesDecryption(byte[] key, byte[] iv, byte[] encData) {
		try {
			Cipher cipher;
			cipher = Cipher.getInstance(CIPHER_AES_MODE);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, CIPHER_AES_NAME), new IvParameterSpec(iv));
			byte[] orgData = cipher.doFinal(encData);
			return orgData;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}

	/**
	 * 使用AES算法做数据加密处理。
	 * 
	 * @param key
	 *            加密Key
	 * @param iv
	 *            加密初始向量
	 * @param orgData
	 *            拟加密数据
	 * @return 加密数据
	 */
	public static byte[] aesEncryption(byte[] key, byte[] iv, byte[] orgData) {
		try {
			Cipher cipher;
			cipher = Cipher.getInstance(CIPHER_AES_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, CIPHER_AES_NAME), new IvParameterSpec(iv));
			byte[] encData = cipher.doFinal(orgData);
			return encData;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}
	public static String decodeAESBySalt(String password, String salt) {
		while(salt.length()<32){
			salt+="hpb@gxn.io";
		}
		return new String(SecurityUtils.aesDecryption(
				salt.substring(0, 16).getBytes(StandardCharsets.UTF_8),
				salt.substring(16, 32).getBytes(StandardCharsets.UTF_8),
				Hex.decode(password)),StandardCharsets.UTF_8);
	}
	public static String encodeAESBySalt(String password, String salt) {
		while(salt.length()<32){
			salt+="hpb@gxn.io";
		}
		return new String(Hex.encode(SecurityUtils.aesEncryption(
				salt.substring(0, 16).getBytes(StandardCharsets.UTF_8),
				salt.substring(16, 32).getBytes(StandardCharsets.UTF_8),
				password.getBytes(StandardCharsets.UTF_8))));
	}
	public static String decodeAESByKey(String password, String key) {
		return new String(SecurityUtils.aesDecryption(
				key.substring(0, 16).getBytes(StandardCharsets.UTF_8), 
				key.substring(16, 32).getBytes(StandardCharsets.UTF_8), 
				Base64.decode(password)),StandardCharsets.UTF_8);
	}
	public static String encodeAESByKey(String password, String key) {
		return new String(Base64.encode(
					SecurityUtils.aesEncryption(
						key.substring(0, 16).getBytes(StandardCharsets.UTF_8),
						key.substring(16, 32).getBytes(StandardCharsets.UTF_8),
						password.getBytes(StandardCharsets.UTF_8)
					)
				),StandardCharsets.UTF_8);
	}
	/**
	 * @Title: decode
	 * @Description: 对应前台解密
	 * @param paramString
	 * @return
	 */
	public static String decode(String paramString) {
		if (paramString == null) {
			return "";
		}
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); ++i) {
			char c = paramString.charAt(i);
			String str;
			switch (c) {
			case '~':
				str = paramString.substring(i + 1, i + 3);
				localStringBuffer.append((char) Integer.parseInt(str, 16));
				i += 2;
				break;
			case '^':
				str = paramString.substring(i + 1, i + 5);
				localStringBuffer.append((char) Integer.parseInt(str, 16));
				i += 4;
				break;
			default:
				localStringBuffer.append(c);
			}
		}
		return localStringBuffer.toString();
	}

	/**
	 * @Title: encode
	 * @Description: 对应前台加密
	 * @param paramString
	 * @return
	 */
	public static String encode(String paramString) {
		if (paramString == null) {
			return "";
		}
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++) {
			int j = paramString.charAt(i);
			String str;

			str = Integer.toString(j, 16);
			for (int k = str.length(); k < 4; k++)
				str = "0" + str;
			localStringBuffer.append("^" + str);

		}
		return localStringBuffer.toString();
	}
	/**
	 * 对称加密秘钥解密
	 * @param encodeKey
	 * @param rsaKey
	 * @return
	 */
	public static String getDecodeKeyByRsaKey(String encodeKey, String rsaKey) {
		return encodeKey;
	}
	/**
	 * 对称加密秘钥加密
	 * @param decodeKey
	 * @param rsaKey
	 * @return
	 */
	public static String getEncodeKeyByRsaKey(String decodeKey, String rsaKey) {
		return decodeKey;
	}
	/**
	 * 对参数进行解密
	 * @param <T>
	 * @param encodeParam
	 * @param decodeKey
	 * @return 
	 * @throws Exception
	 */
	public static <T> T getDecodeParamByDecodeKey(String encodeParam, String decodeKey,Class<T> t) throws Exception{
		String decodeParam=new String(Base64Utils.decodeFromString(encodeParam),StandardCharsets.UTF_8);
		T param =ObjectJsonHelper.deserialize(decodeParam,t);
		return param;
	}
	/**
	 * 对参数进行加密
	 * @param decodeParam
	 * @param encodeKey
	 * @return
	 * @throws Exception
	 */
	public static String getEncodeParamByEecodeKey(Object decodeParam, String encodeKey) throws Exception{
		String serializeResult = ObjectJsonHelper.serialize(decodeParam);
		return new String(Base64Utils.encode(serializeResult.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
	}

	/**
	 * 获取公钥
	 *
	 * @param publicKeyPath
	 * @throws Exception
	 */
	public static PublicKey getPublicKeyFromFile(String publicKeyPath) throws Exception {
		FileInputStream fis = new FileInputStream(publicKeyPath);
		ObjectInputStream oos = new ObjectInputStream(fis);
		PublicKey kp = (PublicKey) oos.readObject();
		oos.close();
		fis.close();
		return kp;
	}

	/**
	 * 获取私钥
	 *
	 * @param privateKeyPath
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKeyFromFile(String privateKeyPath)
			throws Exception {
		FileInputStream fis = new FileInputStream(privateKeyPath);
		ObjectInputStream oos = new ObjectInputStream(fis);
		PrivateKey kp = (PrivateKey) oos.readObject();
		oos.close();
		fis.close();
		return kp;
	}

	/**
	 * 获取公钥
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PublicKey getPublicKeyFromBase64(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return publicKey;
	}

	/**
	 * 获取私钥
	 * @param key 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKeyFromBase64(String key) throws Exception {
		byte[] keyBytes;
		keyBytes = Base64.decode(key);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return privateKey;
	}

	/**
	 * 根据publicKey 对data进行加密.
	 *
	 * @param publicKey
	 * @param data
	 * @throws Exception
	 */
	public static byte[] encryptByRsaKey(PublicKey publicKey, byte[] data)
			throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA",
					new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);// ENCRYPT_MODE 加密
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(data.length);
			int leavedSize = data.length % blockSize;
			int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
					: data.length / blockSize;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (data.length - i * blockSize > 0) {
				if (data.length - i * blockSize > blockSize) {
					cipher.doFinal(data, i * blockSize, blockSize, raw, i
							* outputSize);
				} else {
					cipher.doFinal(data, i * blockSize, data.length - i
							* blockSize, raw, i * outputSize);
				}
				i++;
			}
			return raw;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据privateKey 对data进行解密.
	 *
	 * @param privateKey
	 * @param data
	 * @throws Exception
	 */
	public static byte[] decryptByRsaKey(PrivateKey privateKey, byte[] data) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);// DECRYPT_MODE 解密
		int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
		int j = 0;

		while (data.length - j * blockSize > 0) {
			bout.write(cipher.doFinal(data, j * blockSize, blockSize));
			j++;
		}
		return bout.toByteArray();
	}

	/**
	 * 用于生成密钥
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		SecureRandom sr = new SecureRandom();
		KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA",
				new BouncyCastleProvider());
		// 注意密钥大小最好为1024,否则解密会有乱码情况.
		kg.initialize(1024, sr);
		KeyPair genKeyPair = kg.genKeyPair();
		return genKeyPair;
	}

	/**
	 * 将密钥写入文件
	 *
	 * @param kp
	 * @param path
	 * @throws IOException 
	 */
	public static void saveKeyPair(KeyPair kp, String path) throws IOException {
		//保存公钥
		File file = new File(path+"/publicKey.key");
		if (!file.exists() || file.isDirectory()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		// 写入文件
		oos.writeObject(kp.getPublic());
		oos.close();
		fos.close();
		//保存私钥
		file = new File(path+"/privateKey.key");
		if (!file.exists() || file.isDirectory()) {
			file.createNewFile();
		}
		fos = new FileOutputStream(file);
		oos = new ObjectOutputStream(fos);
		// 写入文件
		oos.writeObject(kp.getPrivate());
		oos.close();
		fos.close();
	}

	/**
	 * 使用RSA加密
	 * @param input     加密信息     
	 * @param key 公钥
	 * @return
	 * @throws Exception 
	 */
	public static String encodeByRSA(String input, String key) throws Exception {
		byte[] keyBytes = Base64Utils.decodeFromString(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		byte[] encryptByRsaKey = SecurityUtils.encryptByRsaKey(publicKey,input.getBytes());
		return Base64Utils.encodeToString(encryptByRsaKey);
	}
	
	/**
	 * 使用RSA解密
	 * @param input      加密信息     
	 * @param key 私钥
	 * @return
	 * @throws Exception 
	 */
	public static String decodeByRSA(String input, String key) throws Exception {
		byte[] keyBytes = Base64Utils.decodeFromString(key);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		byte[] encryptByRsaKey = SecurityUtils.decryptByRsaKey(privateKey,input.getBytes());
		return Base64Utils.encodeToString(encryptByRsaKey);
		
	}
	
	
	/**
	 * 公钥对象转为字符串
	 * @param publicKey
	 * @return
	 */
	public static String publicKeytoString(PublicKey publicKey){
		byte[] encoded = publicKey.getEncoded();
		return Base64Utils.encodeToString(encoded);
	}
	
	/**
	 * 私钥对象转为字符串
	 * @param privateKey
	 * @return
	 */
	public static String privateKeytoString(PrivateKey privateKey){
		byte[] encoded = privateKey.getEncoded();
		return Base64Utils.encodeToString(encoded);
	}
	
	/**
	 * 字符串转为公钥
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static PublicKey stringToPublicKey(String publicKey) throws Exception {
		byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
	}
	
	/**
	 * 字符串转为私钥
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey stringToPrivateKey(String privateKey) throws Exception {
		byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}
}
