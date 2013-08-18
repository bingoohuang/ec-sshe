package org.n3r.sshe.security;

import com.google.common.base.Throwables;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class AESEncrypter {
    private Cipher ecipher;
    private Cipher dcipher;

    public AESEncrypter(String key) {
        this(new SecretKeySpec(DatatypeConverter.parseBase64Binary(key), "AES"));
    }

    public AESEncrypter(SecretKey key) {
        // Create an 8-byte initialization vector
        byte[] iv = new byte[]{
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
        try {
            ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding" );
            dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding" );

            // CBC requires an initialization vector
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    public String encrypt(String in) {
        try {
            byte[] bytes = ecipher.doFinal(in.getBytes("UTF-8" ));
            return DatatypeConverter.printBase64Binary(bytes);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public String decrypt(String in) {
        try {
            byte[] bytes = dcipher.doFinal(DatatypeConverter.parseBase64Binary(in));
            return new String(bytes, "UTF-8" );
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static String createKey() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES" );
            kgen.init(128);
            SecretKey key = kgen.generateKey();
            return DatatypeConverter.printBase64Binary(key.getEncoded());
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static void main(String args[]) throws NoSuchAlgorithmException {
        // Generate a temporary key. In practice, you would save this key.
        // See also e464 Encrypting with DES Using a Pass Phrase.
        String key = createKey();
        System.out.println(key);

        // Create encrypter/decrypter class
        AESEncrypter encrypter = new AESEncrypter(key);

        String abc = encrypter.encrypt("ABC" );
        System.out.println(abc);

        String decrypt = encrypter.decrypt(abc);
        System.out.println(decrypt);
    }
}