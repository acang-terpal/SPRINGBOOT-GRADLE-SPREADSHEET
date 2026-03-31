/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import configuration.DependencyConfig;
import entity.EntityConfig;
import interfc.InterfcEncryptDecrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author hasan
 */
@Service
@Scope("prototype")
public class ImpleEncryptDecrypt implements InterfcEncryptDecrypt {

    private final EntityConfig entityConfig;
    private String IV;
    private String encryptionKey;

    @Autowired
    public ImpleEncryptDecrypt(DependencyConfig dependencyConfig) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.IV = entityConfig.getEncIvDb();
        this.encryptionKey = entityConfig.getEncKeyDb();
    }

    @Override
    public byte[] encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(this.encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(this.IV.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    @Override
    public String decrypt(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(this.encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(this.IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

    @Override
    public String decryptAes128(byte[] cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(this.encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(this.IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

}
