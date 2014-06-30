package com.teste.brilhante.teste;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by junio_000 on 29/06/2014.
 */
public class AES {

    public static final byte[] PASSFRASE = {1,2,3,4,5,6,7,6,5,4,3,2,1,2,3,4};

    public static byte[] encrypt(byte[] clear, byte[] key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKey(key), "AES");
        IvParameterSpec initVector = new IvParameterSpec(PASSFRASE);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, initVector);
        byte[] bytes = cipher.doFinal(clear);
        Log.d(AES.class.getName(), "bytes to encrypt:" + clear);
        Log.d(AES.class.getName(), "bytes encrypted:" + bytes);
        Log.d(AES.class.getName(), "key:" + new String(key, "UTF-8"));
        return bytes;
    }

    public static byte[] decrypt( byte[] encrypted, byte[] key) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(getKey(key), "AES");
        IvParameterSpec initVector = new IvParameterSpec(PASSFRASE);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, initVector);
        byte[] bytes = cipher.doFinal(encrypted);
        Log.d(AES.class.getName(), "bytes to decrypt:" + encrypted);
        Log.d(AES.class.getName(), "bytes decrypted :" + bytes);
        Log.d(AES.class.getName(), "key" + new String(key, "UTF-8"));
        return bytes;
    }

    public static byte[] makeKey() throws NoSuchAlgorithmException {

        byte[] keyStart = "this is a key".getBytes();
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] encoded = skey.getEncoded();
        Log.d(AES.class.getName(), "key:" + Base64.encodeToString(encoded, Base64.NO_PADDING));
        return encoded;
    }

    private static byte[] getKey(byte[] suggestedKey)
    {
        ArrayList<Byte> kList = new ArrayList<Byte>();

        for (int i = 0; i < 128; i += 8) {
            kList.add(suggestedKey[(i / 8) % suggestedKey.length]);
        }

        byte[] byteArray = new byte[kList.size()];
        for(int i = 0; i<kList.size(); i++){
            byteArray[i] = kList.get(i);
        }
        return byteArray;
    }
}
