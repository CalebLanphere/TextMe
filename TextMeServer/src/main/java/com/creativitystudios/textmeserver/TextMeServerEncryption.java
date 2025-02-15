package com.creativitystudios.textmeserver;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class TextMeServerEncryption {
    private final String RSAMethod = "RSA/ECB/PKCS1Padding";
    private final String AESMethod = "AES/CTR/NoPadding";
    private Cipher encryptionCipherRSA;
    private Cipher encryptionCipherAES;
    private KeyPair RSAKeyPair;
    private PublicKey receivedPublicKey;
    private SecretKey AESKeyCreated;
    private IvParameterSpec AESIv;
    protected EncryptionStatuses encryptionStatus = EncryptionStatuses.NONE;

    public enum EncryptionStatuses {
        NONE, RSA, AES
    }

    public TextMeServerEncryption() {
        try {
            encryptionCipherRSA = Cipher.getInstance(RSAMethod);
            encryptionCipherAES = Cipher.getInstance(AESMethod);
            encryptionStatus = EncryptionStatuses.NONE;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String encryptMessageRSA(String messageToEncrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        encryptionCipherRSA.init(Cipher.ENCRYPT_MODE, RSAKeyPair.getPrivate());
        byte[] encryptedMessageBytes = encryptionCipherRSA.doFinal(messageToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String encryptMessageAES(String messageToEncrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        encryptionCipherAES.init(Cipher.ENCRYPT_MODE, AESKeyCreated, AESIv);
        byte[] encryptedMessageBytes = encryptionCipherAES.doFinal(messageToEncrypt.getBytes());
        //AESIv = modifyIVParameter(AESIv);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public String decryptMessageRSA(String messageToDecrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        encryptionCipherRSA.init(Cipher.DECRYPT_MODE, receivedPublicKey);
        byte[] decodedB64Message = Base64.getDecoder().decode(messageToDecrypt.getBytes());
        return new String(encryptionCipherRSA.doFinal(decodedB64Message));
    }

    public String decryptMessageAES(String messageToDecrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        encryptionCipherAES.init(Cipher.DECRYPT_MODE, AESKeyCreated, AESIv);
        byte[] decodedB64Message = Base64.getDecoder().decode(messageToDecrypt.getBytes());
        //AESIv = modifyIVParameter(AESIv);
        return new String(encryptionCipherAES.doFinal(decodedB64Message));
    }

    public void recreateRSAKey(String keyToRecreate) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(keyToRecreate);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        receivedPublicKey = keyFactory.generatePublic(keySpec);
    }

    public void createRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            RSAKeyPair = keyGen.generateKeyPair();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void createAESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            AESKeyCreated = keyGen.generateKey();
            SecureRandom random = new SecureRandom();
            byte[] ivStart = new byte[16];
            random.nextBytes(ivStart);
            AESIv = new IvParameterSpec(ivStart);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private IvParameterSpec modifyIVParameter(IvParameterSpec oldIVSpec) {
        byte[] modifiedIvByte = oldIVSpec.getIV();
        for(int i = 0; i < oldIVSpec.getIV().length; i++) {
            modifiedIvByte[i] += 1;
        }
        return new IvParameterSpec(modifiedIvByte);
    }

    protected String getRSAPublicKey() {
        String tempTest = Base64.getEncoder().encodeToString(RSAKeyPair.getPublic().getEncoded());
        return Base64.getEncoder().encodeToString(RSAKeyPair.getPublic().getEncoded());
    }

    protected String getAESKey() {
        return Base64.getEncoder().encodeToString(AESKeyCreated.getEncoded());
    }

    protected String getIv() {
        return Base64.getEncoder().encodeToString(AESIv.getIV());
    }
}
