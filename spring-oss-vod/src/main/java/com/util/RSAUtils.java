package com.util;
import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * <p>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * 
 */
public class RSAUtils {
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	private static final int MAX_ENCRYPT_BLOCK = 117;

	private static final int MAX_DECRYPT_BLOCK = 128;


	/**
	 * method will close inputSteam
	 * 
	 * @param pemFileInputStream
	 * @return
	 */
	public static PublicKey loadPublicKey(InputStream pemFileInputStream) {
		return readPublicKey(readPEMFile(pemFileInputStream));
	}

	/**
	 * method will close inputSteam
	 * 
	 * @param pkcs8PemFileInputStream
	 * @return
	 */
	public static PrivateKey loadPrivateKey(InputStream pkcs8PemFileInputStream) {
		return readPrivateKey(readPEMFile(pkcs8PemFileInputStream));
	}

	/**
	 * 
	 * @param pemFile
	 * @return
	 */
	public static PublicKey loadPublicKey(String pemFile) {
		return readPublicKey(readPEMFile(pemFile));
	}

	/**
	 * 
	 * @param pkcs8PemFile
	 * @return
	 */
	public static PrivateKey loadPrivateKey(String pkcs8PemFile) {
		return readPrivateKey(readPEMFile(pkcs8PemFile));
	}

	/**
	 * read pem file, delete first and last line, sth. like:<br />
	 * <p>
	 * -----BEGIN PUBLIC KEY----- -----END PUBLIC KEY-----
	 * </p>
	 * 
	 * @param filename
	 * @return
	 */
	public static String readPEMFile(String filename) {
		try {
			return readPEMFile(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * method will close inputSteam
	 * 
	 * @param stream
	 *            pem file inputstream
	 * @return
	 */
	public static String readPEMFile(InputStream stream) {
		if (null != stream) {
			BufferedReader in = null;
			StringBuilder ret = new StringBuilder();
			String line;
			try {
				in = new BufferedReader(new InputStreamReader(stream, "ASCII"));
				line = in.readLine();
				while (null != line) {
					if (!(line.startsWith("-----BEGIN ") || line.startsWith("-----END "))) {
						ret.append(line);
						ret.append("\n");
					}

					line = in.readLine();
				}

				return ret.toString();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					stream.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				if (null != in) {
					try {
						in.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	/**
	 * 
	 * @param pkcs8Base64String
	 *            <p>
	 *            delete the first and last line, sth. like below: -----BEGIN
	 *            PRIVATE KEY----- -----END PRIVATE KEY-----
	 *            </p>
	 * @return
	 */
	public static PrivateKey readPrivateKey(String pkcs8Base64String) {
		byte[] keyByte = Base64Utils.decode(pkcs8Base64String);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyByte);
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

			return privateKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static PublicKey readPublicKey(String pkcs8Base64String) {
		byte[] keyByte = Base64Utils.decode(pkcs8Base64String);
		try {
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyByte);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publicKey = (PublicKey) keyFactory.generatePublic(x509KeySpec);

			return publicKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * <P>
	 * 私钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 * @return
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, PrivateKey privateKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			return cipher.doFinal(encryptedData);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 公钥加密
	 * 
	 * @param content
	 *            待加密内容
	 * @param publicKey
	 *            公钥
	 * @param charset
	 *            content的字符集，如UTF-8, GBK, GB2312
	 * @return 密文内容 base64 ASCII
	 */
	public static String encryptByPublicKey(String content, PublicKey publicKey, String charset) {
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] data = (charset == null || charset.isEmpty()) ? content.getBytes() : content.getBytes(charset);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}

			byte[] encryptedData = Base64Utils.encode(out.toByteArray());
			out.close();

			return new String(encryptedData, "ASCII");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 私钥加密
	 * 
	 * @param content
	 *            待加密内容
	 * @param privateKey
	 *            私钥
	 * @param charset
	 *            content的字符集，如UTF-8, GBK, GB2312
	 * @return 密文内容 base64 ASCII
	 */
	public static String encryptByPrivateKey(String content, PrivateKey privateKey, String charset) {
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] data = (charset == null || charset.isEmpty()) ? content.getBytes() : content.getBytes(charset);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}

			byte[] encryptedData = Base64Utils.encode(out.toByteArray());
			out.close();

			return new String(encryptedData, "ASCII");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 私钥解密
	 * 
	 * @param content
	 *            待解密内容(base64, ASCII)
	 * @param privateKey
	 *            私钥
	 * @param charset
	 *            加密前字符的字符集，如UTF-8, GBK, GB2312
	 * @return 明文内容
	 * @return
	 */
	public static String decryptByPrivateKey(String content, PrivateKey privateKey, String charset) {
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] encryptedData = Base64Utils.decode(content);
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}

				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return (charset == null || charset.isEmpty()) ? new String(decryptedData)
					: new String(decryptedData, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 公钥解密
	 * 
	 * @param content
	 *            待解密内容(base64, ASCII)
	 * @param publicKey
	 *            公钥
	 * @param charset
	 *            加密前字符的字符集，如UTF-8, GBK, GB2312
	 * @return 明文内容
	 * @return
	 */
	public static String decryptByPublicKey(String content, PublicKey publicKey, String charset) {
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] encryptedData = Base64Utils.decode(content);
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}

				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return (charset == null || charset.isEmpty()) ? new String(decryptedData)
					: new String(decryptedData, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param signType 如：SHA1withRSA
	 * @param data
	 * @param publicKey
	 * @param sign base64字符串
	 * @return
	 * @throws Exception
	 */
	public static boolean verifySign(String signType, byte[] data, PublicKey publicKey, String sign) throws Exception {
		Signature signature = Signature.getInstance(signType);
		signature.initVerify(publicKey);
		signature.update(data);
		return signature.verify(Base64Utils.decode(sign));
	}
	
	/**
	 * 
	 * @param signType 如：SHA1withRSA
	 * @param data
	 * @param privateKey
	 * @return base64字符串
	 * @throws Exception
	 */
	public static String sign(String signType, byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(signType);
        signature.initSign(privateKey);
        signature.update(data);
        
        return new String(Base64Utils.encode(signature.sign()));
    }


	/** *//**
	 * <P>
	 * 私钥解密
	 * </p>
	 *
	 * @param encryptedData 已加密数据
	 * @param privateKey 私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/** *//**
	 * <p>
	 * 公钥解密
	 * </p>
	 *
	 * @param encryptedData 已加密数据
	 * @param publicKey 公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPublicKey(byte[] encryptedData, String publicKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return com.util.Base64.encode(decryptedData);
	}

	/** *//**
	 * <p>
	 * 公钥加密
	 * </p>
	 *
	 * @param data 源数据
	 * @param publicKey 公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/** *//**
	 * <p>
	 * 私钥加密
	 * </p>
	 *
	 * @param data 源数据
	 * @param privateKey 私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPrivateKey(byte[] data, String privateKey)
			throws Exception {
		byte[] keyBytes = Base64Utils.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return Base64.encode(encryptedData);
	}
}
