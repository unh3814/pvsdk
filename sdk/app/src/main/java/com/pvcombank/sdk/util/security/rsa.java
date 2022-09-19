package com.pvcombank.sdk.util.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class rsa {
    public static String encryptRSA(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    private static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            String realPK = base64PublicKey.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", "").replaceAll("\r\n", "").replaceAll("\n", "");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(realPK.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    public static String signSHA1RSA(String input, String strPk) throws Exception {
        String realPK = strPk.replaceAll("-----BEGIN RSA PRIVATE KEY-----", "").replaceAll("-----END RSA PRIVATE KEY-----", "").replaceAll("\r\n", "").replaceAll("\n", "");
        byte[] b1 = Base64.getDecoder().decode(realPK);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        Signature privateSignature = Signature.getInstance("SHA1withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
    }

    public static String encryptRSA(String dataToEncrypt, String publicKeyString, Boolean isFile) throws Exception {
        RSAPublicKey publicKey = (RSAPublicKey) getPublicKey(publicKeyString);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, publicKey);
        int keySize = publicKey.getModulus().bitLength() / 8;
        int maxLength = keySize - 42;
        byte[] bytes = dataToEncrypt.getBytes();
        int dataLength = bytes.length;
        int iterations = dataLength / maxLength;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= iterations; i++) {
            byte[] tempBytes = new byte[dataLength - maxLength * i > maxLength ? maxLength : dataLength - maxLength * i];
            System.arraycopy(bytes, maxLength * i, tempBytes, 0, tempBytes.length);
            byte[] encryptedBytes = cipher.doFinal(tempBytes);
            encryptedBytes = reverse(encryptedBytes);
            sb.append(Base64.getEncoder().encodeToString(encryptedBytes));
        } String sEncrypted = sb.toString();
        sEncrypted = sEncrypted.replace("\r", "");
        sEncrypted = sEncrypted.replace("\n", "");
        return sEncrypted;
    }

    private static byte[] reverse(byte[] b) {
        int left = 0;
        int right = b.length - 1;
        while (left < right) {
            byte temp = b[left];
            b[left] = b[right];
            b[right] = temp;
            left++;
            right--;
        }
        return b;
    }
}
