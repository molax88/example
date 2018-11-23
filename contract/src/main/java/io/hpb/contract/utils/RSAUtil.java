package io.hpb.contract.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.HashMap;
import java.util.Map;

/** 
 * RSA 工具类。提供加密，解密，生成密钥对等方法。 
 * 需要到http://www.bouncycastle.org下载bcprov-jdk14-123.jar。 
 *  
 */  
public class RSAUtil {  
      
    private static String RSAKeyStore = "d:/RSAKey.txt";  
    /** 
     * * 生成密钥对 * 
     *  
     * @return KeyPair * 
     * @throws EncryptException 
     */  
    public static KeyPair generateKeyPair() throws Exception {  
        try {  
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider());  
            final int KEY_SIZE = 1024;// 没什么好说的了，这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低  
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());  
            KeyPair keyPair = keyPairGen.generateKeyPair();  
            
            saveKeyPair(keyPair);  
            return keyPair;  
        } catch (Exception e) {  
            throw new Exception(e.getMessage());  
        }  
    }  
  
    public static KeyPair getKeyPair() throws Exception {  
        FileInputStream fis = new FileInputStream(RSAKeyStore);  
        ObjectInputStream oos = new ObjectInputStream(fis);  
        KeyPair kp = (KeyPair) oos.readObject();  
        oos.close();  
        fis.close();  
        return kp;  
    }  
  
    public static void saveKeyPair(KeyPair kp) throws Exception {  
  
        FileOutputStream fos = new FileOutputStream(RSAKeyStore);  
        ObjectOutputStream oos = new ObjectOutputStream(fos);  
        // 生成密钥  
        oos.writeObject(kp);  
        oos.close();  
        fos.close();  
    }  
  
    /** 
     * * 生成公钥 * 
     *  
     * @param modulus * 
     * @param publicExponent * 
     * @return RSAPublicKey * 
     * @throws Exception 
     */  
    public static RSAPublicKey generateRSAPublicKey(byte[] modulus,  
            byte[] publicExponent) throws Exception {  
        KeyFactory keyFac = null;  
        try {  
            keyFac = KeyFactory.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider());  
        } catch (NoSuchAlgorithmException ex) {  
            throw new Exception(ex.getMessage());  
        }  
  
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger( 
                modulus), new BigInteger(publicExponent));  
        try {  
            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);  
        } catch (InvalidKeySpecException ex) {  
            throw new Exception(ex.getMessage());  
        }  
    }  
  
    /** 
     * * 生成私钥 * 
     *  
     * @param modulus * 
     * @param privateExponent * 
     * @return RSAPrivateKey * 
     * @throws Exception 
     */  
    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus,  
            byte[] privateExponent) throws Exception {  
        KeyFactory keyFac = null;  
        try {  
            keyFac = KeyFactory.getInstance("RSA",  
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());  
        } catch (NoSuchAlgorithmException ex) {  
            throw new Exception(ex.getMessage());  
        }  
  
        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(  
                modulus), new BigInteger(privateExponent));  
        try {  
            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);  
        } catch (InvalidKeySpecException ex) {  
            throw new Exception(ex.getMessage());  
        }  
    }  
  
    /** 
     * * 加密 * 
     *  
     * @param key 
     *            加密的密钥 * 
     * @param data 
     *            待加密的明文数据 * 
     * @return 加密后的数据 * 
     * @throws Exception 
     */  
    public static byte[] encrypt(PublicKey pk, byte[] data) throws Exception {  
        try {  
            Cipher cipher = Cipher.getInstance("RSA",  
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());  
            cipher.init(Cipher.ENCRYPT_MODE, pk);  
            int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024  
            // 加密块大小为127  
            // byte,加密后为128个byte;因此共有2个加密块，第一个127  
            // byte第二个为1个byte  
            int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小  
            int leavedSize = data.length % blockSize;  
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1  
                    : data.length / blockSize;  
            byte[] raw = new byte[outputSize * blocksSize];  
            int i = 0;  
            while (data.length - i * blockSize > 0) {  
                if (data.length - i * blockSize > blockSize)  
                    cipher.doFinal(data, i * blockSize, blockSize, raw, i  
                            * outputSize);  
                else  
                    cipher.doFinal(data, i * blockSize, data.length - i  
                            * blockSize, raw, i * outputSize);  
  
                i++;  
            }  
            return raw;  
        } catch (Exception e) {  
            throw new Exception(e.getMessage());  
        }  
    }  
  
    /** 
     * * 解密 * 
     *  
     * @param key 
     *            解密的密钥 * 
     * @param raw 
     *            已经加密的数据 * 
     * @return 解密后的明文 * 
     * @throws Exception 
     */  
    public static byte[] decrypt(PrivateKey pk, byte[] raw) throws Exception {  
        try {  
            Cipher cipher = Cipher.getInstance("RSA",  
                    new org.bouncycastle.jce.provider.BouncyCastleProvider());  
            cipher.init(cipher.DECRYPT_MODE, pk);  
            int blockSize = cipher.getBlockSize();  
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);  
            int j = 0;  
  
            while (raw.length - j * blockSize > 0) {  
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));  
                j++;  
            }  
            return bout.toByteArray();  
        } catch (Exception e) {  
            throw new Exception(e.getMessage());  
        }  
    } 
  
    
   /* *//** 
     * * * 
     *  
     * @param args * 
     * @throws Exception 
     *//*  
    public static void main(String[] args) throws Exception {  
    	KeyPair keyPair = RSAUtil.generateKeyPair();
    	PrivateKey private1 = keyPair.getPrivate();
    	PublicKey public1 = keyPair.getPublic();
    	
    	String serialize = ObjectJsonHelper.serialize(public1);
    	PublicKey deserialize = ObjectJsonHelper.deserialize(serialize, PublicKey.class);
    	
    	String test = "hello world";  
	    byte[] en_test = encrypt(deserialize, test.getBytes());  
        byte[] de_test = decrypt(private1, en_test);  
        System.out.println(new String(de_test)); 
    	
    	
    	
        RSAPublicKey rsap = (RSAPublicKey) RSAUtil.generateKeyPair().getPublic();  
        byte[] en_test = encrypt(getKeyPair().getPublic(), test.getBytes());  
        byte[] de_test = decrypt(getKeyPair().getPrivate(), en_test);  
        System.out.println(new String(de_test));  
    	
    }*/  
    
    
    
private static String privateExponent;
	
	private static String modulus;
	
	/** 
	 * 私钥解密 
	 *  
	 * @param key 接口入参的key
	 * @return 
	 * @throws Exception 
	 */  
	public static String getKey(String key) throws Exception {
		
	    //获取经过rsa非对称加密的 客户端生成的aes密钥 
	    if(StringUtils.isEmpty(key)) {
	        return "";
	    }
	    RSAPrivateKey privateKey = null;
	    try {
	        String privateExponentStr = privateExponent;//本案例中的privateExponentStr通过配置文件设置
	        String modulusStr = modulus;//本案例中的modulusStr通过配置文件设置
	        BigInteger modulus = new BigInteger(new String(Base64Utils.decodeFromString(modulusStr), "UTF-8"), 16);
	        BigInteger privateExponent = new BigInteger(new String(Base64Utils.decodeFromString(privateExponentStr), "UTF-8"), 16);
	        privateKey = getPrivateKey(modulus, privateExponent);
	        return decryptByPrivateKey(key, privateKey);
	    }catch(Exception e) {
	      
	    }
	    return "";
	}
	
	public static RSAPrivateKey getPrivateKey(BigInteger modulus, BigInteger exponent) {
	    try {
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
	        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	    } catch (Exception e) {
	        return null;
	    }
	}
	/** 
	 * 私钥解密 
	 *  
	 * @param data 
	 * @param privateKey 私钥
	 * @return 
	 * @throws Exception 
	 */  
	public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
	    Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
	    cipher.init(Cipher.DECRYPT_MODE, privateKey);
	    byte[] bytes = Base64Utils.decodeFromString(data);
	    // 如果密文长度大于模长则要分组解密
	    return new String(cipher.doFinal(bytes));
	}
	
	/** 
	 * 公钥加密 
	 *  
	 * @param data 
	 * @param publicKey 公钥
	 * @return 
	 * @throws Exception 
	 */  
	public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
	    Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding","");
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	    byte[] bytes = Base64Utils.decodeFromString(data);
	    // 如果密文长度大于模长则要分组解密
	    return new String(cipher.doFinal(bytes));
	}
	
	
	public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";


    public static Map<String, String> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<String, String>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateDecrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data
     * @param privateKey
     * @return
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()));
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */
    public static String publicDecrypt(String data, RSAPublicKey publicKey){
    	
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }
    
    
	// 使用N、D值还原公钥
	public static PublicKey getPublicKey(String modulus, String	exponent)
	        throws NoSuchAlgorithmException, InvalidKeySpecException {
		
	    BigInteger bigIntModulus = new BigInteger(modulus);
	    BigInteger bigIntPrivateExponent = new BigInteger(exponent);
	    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus,bigIntPrivateExponent);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PublicKey publicKey = keyFactory.generatePublic(keySpec);
	    
	    return publicKey;
	}
	 
	// 使用N、D值还原私钥 
	public static PrivateKey getPrivateKey(String modulus, String exponent)
	        throws NoSuchAlgorithmException, InvalidKeySpecException {
		
	    BigInteger bigIntModulus = new BigInteger(modulus);
	    BigInteger bigIntPrivateExponent = new BigInteger(exponent);
	    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(bigIntModulus,bigIntPrivateExponent);
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
	    return privateKey;
	}
	// 公钥钥 获取N、D值
	public static Map<String,Object> getPublicKeyModulusAndexponent(PublicKey publicKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Map<String,Object> map = new HashMap<>();
		RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
		map.put("modulus", rsaPublicKey.getModulus());
		map.put("publicExponent", rsaPublicKey.getPublicExponent());
		return map;
	}
	
	// 私钥 获取N、D值
	public static Map<String,Object> getPrivateKeyModulusAndexponent(PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Map<String,Object> map = new HashMap<>();
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)privateKey;
    	map.put("modulus", rsaPrivateKey.getModulus());
		map.put("publicExponent", rsaPrivateKey.getPrivateExponent());
		return map;
	}
	
	/**
	 * AES 解密数据
	 * @param encryptStr
	 * @param decryptKey
	 * @param iv
	 * @return
	 * @throws Exception
	 */
	public static String aesDecrypt(String encryptStr, String decryptKey,String iv)
	        throws Exception {
	    if(org.apache.commons.lang3.StringUtils.isBlank(decryptKey)) {
	        return encryptStr;
	    }
	    return aesDecryptByBytes(
	            base64Decode(encryptStr), decryptKey,iv);
	}
	
	public static String aesDecryptByBytes(byte[] encryptBytes,
	        String decryptKey,String ivInput) throws Exception {
	    byte[] keys = base64Decode(decryptKey);
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//此处的部位方式应该跟客户端保持一致CryptoJS.mode.CBC
	    IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());//此处的向量应该跟客户端保持一致0102030405060708
	    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keys, "AES"), iv);
	    byte[] decryptBytes = cipher.doFinal(encryptBytes);

	    return new String(decryptBytes, "utf-8");
	}
	
	public static byte[] base64Decode(String base64Code) throws Exception {
	    return base64Code == null ? null : Base64.decodeBase64(base64Code);
	}
	
	/**
	 *  AES 加密数据
	 * @param content
	 * @param encryptKey
	 * @return
	 * @throws Exception
	 */
	public static String aesEncrypt(String content, String encryptKey,String iv)
	        throws Exception {
	    if(org.apache.commons.lang3.StringUtils.isBlank(encryptKey)) {
	        return content;
	    }
	    return base64Encode(aesEncryptToBytes(content, encryptKey,iv));
	}
	public static byte[] aesEncryptToBytes(String content, String encryptKey,String ivInput)
	        throws Exception {
	    byte[] keys = base64Decode(encryptKey);
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//此处的部位方式应该跟客户端保持一致CryptoJS.mode.CBC
	    IvParameterSpec iv = new IvParameterSpec(ivInput.getBytes());//此处的向量应该跟客户端保持一致0102030405060708
	    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys, "AES"), iv);
	    return cipher.doFinal(content.getBytes("utf-8"));
	}
	public static String base64Encode(byte[] bytes) {
	    return Base64.encodeBase64String(bytes);
	}
	

} 