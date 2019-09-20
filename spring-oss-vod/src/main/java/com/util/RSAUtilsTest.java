package com.util;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 * <p>
 * plain:{"age":18, "name": "zhangsan"} encrypt by public
 * key:HKlpDW0lNQ0DQRUf0BuXxeT0x8VMnzRvUg5pQNEFyflkKXlGeN/
 * NcRjFs8mpaAtmZ9rO5wovl3aP9YmuPxLOQXaPMGMo2jkdF0EQIZRHtT2ihA0iyDHy7+
 * oC2rTaDvB7J2Kr+ZJ8BQVsDT+B4tppgylHs2L07Sfk5cT6n9fBqq8= encrypt by private
 * key:Snjvlc9pwAr/FeifmI5yts1wA8pvZciF3xkjVBn36VcyonSflMWtFpypLN6YN45fnL+
 * yOKn0ulOxJb9LQXhJdX1cukEgoOlMpXRZZw9a1CZPEdhDi1XsIfvezNyht2/nsNYPGa0fL55+
 * 9pYPBmwgT2XN59p2Jw3PdrUm/+Drjqg=
 *
 * </p>
 */
public class RSAUtilsTest {
	public static void main(String... strings) throws Exception {
		String file = "/root/d/herobt/workspace_php/miaobt_pki/integration/api-demo.miaobt.com/rsa_key_paire_pkcs8.pem";
		String pubfile = "/root/d/herobt/workspace_php/miaobt_pki/product/openapi.miaobt.com/rsa_public_key.pem";

		String signType = "SHA1WithRSA";
		// System.out.println(new String(s));
		final PrivateKey privateKey = RSAUtils.loadPrivateKey(file);
		System.out.println(privateKey);

		final PublicKey publicKey = RSAUtils.loadPublicKey(pubfile);
		System.out.println(publicKey);

		String data = "{\"age\":18, \"name\": \"zhangsan\"}";
		System.out.println("-------------source:" + data);
		String sign = RSAUtils.sign(signType, data.getBytes("utf-8"), privateKey);
		System.out.println("-------------sign:" + sign);
		sign = "cZpL4fDdP2yzHL8bCCfn2503niJL+ApGbvXXWASFbd2nZ/J0PQxXrdMy0uUU7yq5IBAdbJM3cyG5vUnib6uJTdSAbvuv3wJm+IgSv3DNu9cMiqP3KnRIlwZ02zLe9KMl4Fb0Je+kxtnRNJZ2t7dUr5BYDmQlPkZLokV9PlvyWuI=";
		boolean signOK = RSAUtils.verifySign(signType, data.getBytes("utf-8"), publicKey, sign);
		System.out.println("-------------signOK:" + signOK);
		
		long start = System.currentTimeMillis();
		String enc = RSAUtils.encryptByPublicKey(data, publicKey, "utf-8");

		System.out.println("-------------enc:" + enc);

		enc = "xV0hPM49N0Hpk+9EMO2DdV0GpgiCcPGEtDg7JzqWLiVK/rsz4NK4QI2Q2wrOIf+oRTyKsY2vD3trfflym7cBHxeMn1ezIlHGfOPuhYG9Rs2zj5DIJJn6BiXEBAKibEtHNwAPG28fTigHf1TuO4DI1Ivhx7yYoZtqkHdHTfKkVUQ=";
		String back = RSAUtils.decryptByPrivateKey(enc, privateKey, "utf-8");
		System.out.println("back:" + back);

		String enc2 = RSAUtils.encryptByPrivateKey(data, privateKey, "utf-8");
		System.out.println("-------------enc2:" + enc2);

		String back2 = RSAUtils.decryptByPublicKey(enc2, publicKey, "utf-8");
		System.out.println("back2:" + back2);

		long stop = System.currentTimeMillis();
		System.out.println("-----------done:" + (stop - start) / 1000);
	}

}
