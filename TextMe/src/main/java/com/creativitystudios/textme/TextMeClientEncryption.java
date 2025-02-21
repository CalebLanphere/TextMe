package com.creativitystudios.textme;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TextMeClientEncryption {
    private final String RSAMethod = "RSA/ECB/PKCS1Padding";
    private final String AESMethod = "AES/CTR/NoPadding";
    private Cipher encryptionCipherRSA;
    private Cipher encryptionCipherAES;
    private KeyPair RSAKeyPair;
    private PublicKey receivedPublicKey;
    private SecretKey AESKeyCreated;
    private IvParameterSpec AESIv;
    protected EncryptionStatuses encryptionStatus = EncryptionStatuses.NONE;

    /**
     * Sets up the three encryption states
     */
    public enum EncryptionStatuses {
        NONE, RSA, AES
    }

    /**
     * Initializes the encryption
     */
    public TextMeClientEncryption() {
        try {
            encryptionCipherRSA = Cipher.getInstance(RSAMethod);
            encryptionCipherAES = Cipher.getInstance(AESMethod);
            encryptionStatus = EncryptionStatuses.NONE;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt a message using the RSA protocol
     *
     * @param messageToEncrypt String - message to encrypt
     * @return String - Encrypted message
     * @throws InvalidKeyException Encryption error - Key is not valid
     * @throws IllegalBlockSizeException  Encryption error - Likely message
     * @throws BadPaddingException Encryption error - Likely key
     */
    public String encryptMessageRSA(String messageToEncrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        encryptionCipherRSA.init(Cipher.ENCRYPT_MODE, RSAKeyPair.getPrivate());
        byte[] encryptedMessageBytes = encryptionCipherRSA.doFinal(messageToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    /**
     * Encrypt a message using the AES protocol
     *
     * @param messageToEncrypt String - message to encrypt
     * @return String - Encrypted message
     * @throws InvalidKeyException Encryption error - Key is not valid
     * @throws IllegalBlockSizeException  Encryption error - Likely message
     * @throws BadPaddingException Encryption error - Likely key
     */
    public String encryptMessageAES(String messageToEncrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        encryptionCipherAES.init(Cipher.ENCRYPT_MODE, AESKeyCreated, AESIv);
        byte[] encryptedMessageBytes = encryptionCipherAES.doFinal(messageToEncrypt.getBytes());
        //AESIv = modifyIVParameter(AESIv);
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    /**
     * Decrypt a message using the RSA protocol
     *
     * @param messageToDecrypt String - message to decrypt
     * @return String - Decrypted message
     * @throws InvalidKeyException Encryption error - Key is not valid
     * @throws IllegalBlockSizeException  Encryption error - Likely message
     * @throws BadPaddingException Encryption error - Likely key
     */
    public String decryptMessageRSA(String messageToDecrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        encryptionCipherRSA.init(Cipher.DECRYPT_MODE, receivedPublicKey);
        byte[] decodedB64Message = Base64.getDecoder().decode(messageToDecrypt.getBytes());
        return new String(encryptionCipherRSA.doFinal(decodedB64Message));
    }

    /**
     * Decrypt a message using the AES protocol
     *
     * @param messageToDecrypt String - message to decrypt
     * @return String - Decrypted message
     * @throws InvalidKeyException Encryption error - Key is not valid
     * @throws IllegalBlockSizeException  Encryption error - Likely message
     * @throws BadPaddingException Encryption error - Likely key
     */
    public String decryptMessageAES(String messageToDecrypt) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        encryptionCipherAES.init(Cipher.DECRYPT_MODE, AESKeyCreated, AESIv);
        byte[] decodedB64Message = Base64.getDecoder().decode(messageToDecrypt.getBytes());
        //AESIv = modifyIVParameter(AESIv);
        return new String(encryptionCipherAES.doFinal(decodedB64Message));
    }

    /**
     * Recreates a public RSA key using the supplied String
     *
     * @param keyToRecreate String - String value of key to recreate
     * @throws NoSuchAlgorithmException Algorithm for key creation is set incorrectly
     * @throws InvalidKeySpecException Key byte array is incorrect to recreate a key
     */
    public void recreateRSAKey(String keyToRecreate) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(keyToRecreate);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        receivedPublicKey = keyFactory.generatePublic(keySpec);
    }

    /**
     * Creates an AES key based on the two Strings provided
     *
     * @param keyToRecreate String - Key to use when creating the AES key
     * @param ivToRecreate String - IvParameter to use when making a new IvParameterSpec
     */
    public void recreateAESKey(String keyToRecreate, String ivToRecreate) {
        byte[] decodedKey = Base64.getDecoder().decode(keyToRecreate);
        byte[] decodedIv = Base64.getDecoder().decode(ivToRecreate);
        AESKeyCreated = new SecretKeySpec(decodedKey, "AES");
        AESIv = new IvParameterSpec(decodedIv);
    }

    /**
     * Creates an RSA Key Pair
     */
    public void createRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            RSAKeyPair = keyGen.generateKeyPair();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifies the old IvParameterSpec to help improve encryption security
     *
     * @param oldIVSpec IvParameterSpec - Previous Iv to change
     * @return IvParameterSpec - New Iv spec created based on old one
     */
    private IvParameterSpec modifyIVParameter(IvParameterSpec oldIVSpec) {
        byte[] modifiedIvByte = oldIVSpec.getIV(); // TODO Implement without creating decryption error
        for(int i = 0; i < modifiedIvByte.length; i++) {
            modifiedIvByte[i] += 1;
        }
        return new IvParameterSpec(modifiedIvByte);
    }

    /**
     * Gets the RSA Public key of the user
     *
     * @return String - RSA public key
     */
    protected String getRSAPublicKey() {
        return Base64.getEncoder().encodeToString(RSAKeyPair.getPublic().getEncoded());
    }

    /**
     * Gets the encryption method the user has set
     *
     * @return TextMeClientEncryption.EncryptionStatuses - Encryption status of the user
     */
    public EncryptionStatuses getCurrentEncryptionMethod() {
        return encryptionStatus;
    }

    /**
     * Sets the encryption method the user has
     *
     * @param encStatus TextMeClientEncryption.EncryptionStatuses - Encryption status to set for the user
     */
    public void setCurrentEncryptionMethod(EncryptionStatuses encStatus) {
        encryptionStatus = encStatus;
    }

}
