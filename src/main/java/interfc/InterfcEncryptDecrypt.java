/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interfc;

/**
 *
 * @author DPPH
 */
public interface InterfcEncryptDecrypt {

    byte[] encrypt(String plainText) throws Exception;

    String decrypt(byte[] cipherText) throws Exception;

    String decryptAes128(byte[] cipherText) throws Exception;
}
