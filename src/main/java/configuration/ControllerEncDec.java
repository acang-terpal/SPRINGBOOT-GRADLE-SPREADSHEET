/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import com.google.common.base.Throwables;
import implement.ImpleEncryptDecrypt;
import interfc.InterfcEncryptDecrypt;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 *
 * @author hasan
 */
@Component("ControllerEncDec")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ControllerEncDec {

    InterfcEncryptDecrypt service;
    private String decrypt;
    private byte[] encrypt;
    private ControllerLog controllerLog;

    @Autowired
    public ControllerEncDec(DependencyConfig dependencyConfig, ImpleEncryptDecrypt impleEncryptDecrypt) {
        this.service = impleEncryptDecrypt;
        this.controllerLog = dependencyConfig.getControllerLog();
    }

    public String getDecrypt(byte[] decodedBytes) throws ClassNotFoundException, Exception {
        this.decrypt = this.service.decrypt(decodedBytes);
        return this.decrypt;
    }

    public String getDecryptAes128(byte[] decodedBytes) throws ClassNotFoundException, Exception {
        this.decrypt = this.service.decryptAes128(decodedBytes);
        return this.decrypt;
    }

    public byte[] getEncrypt(String str) throws ClassNotFoundException, Exception {
        this.encrypt = this.service.encrypt(str);
        return this.encrypt;
    }
    
    //--------------------dari sistem sebelumnya, untuk enc dec pin di database

    private byte[] konversiKeByte(String Kunci) {
        byte[] array_byte = new byte[32];
        int i = 0;
        while (i < Kunci.length()) {
            array_byte[i] = (byte) Kunci.charAt(i);
            i++;
        }
        if (i < 32) {
            while (i < 32) {
                array_byte[i] = (byte) i;
                i++;
            }
        }
        return array_byte;
    }

    private Key generateKey(String kunciEnkripsi) throws Exception {
        Key key = new SecretKeySpec(konversiKeByte(kunciEnkripsi), "AES");
        return key;
    }

    public String encryptDbVal(String text, String Kunci) throws Exception {
        // SecretKeySpec spec = getKeySpec();
        Key key = generateKey(Kunci);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
    }

    public String decryptDbVal(String text, String Kunci) throws Exception {
        // SecretKeySpec spec = getKeySpec();
        String result = null;
        try {
            Key key = generateKey(Kunci);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            result = Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            this.controllerLog.logErrorWriter(s);
        } finally {
            // optional, use this block if necessary
        }

        return result;
    }
}
