package com.comedor.backend.infrastructure.segurity.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AesDeterministicCryptoService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private final SecretKey secretKey;
    private final IvParameterSpec ivParameterSpec;

    public AesDeterministicCryptoService(
            @Value("${app.security.crypto.aes-secret}") String secretBase64,
            @Value("${app.security.crypto.aes-iv}") String ivConfig) {

        // 1. Decodificar la clave de 32 bytes (256 bits)
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");

        // 2. Resolver el IV asegurando exactamente 16 bytes
        byte[] ivBytes;
        try {
            // Intenta decodificar asumiendo Base64
            ivBytes = Base64.getDecoder().decode(ivConfig);
        } catch (IllegalArgumentException e) {
            // Si viene en texto plano, toma los bytes directos en UTF-8
            ivBytes = ivConfig.getBytes(StandardCharsets.UTF_8);
        }

        if (ivBytes.length != 16) {
            byte[] normalizedIv = new byte[16];
            System.arraycopy(ivBytes, 0, normalizedIv, 0, Math.min(ivBytes.length, 16));
            ivBytes = normalizedIv;
        }

        this.ivParameterSpec = new IvParameterSpec(ivBytes);
    }

    public String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cipherText = cipher.doFinal(plainText.trim().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar dato en reposo", e);
        }
    }

    public String decrypt(String cipherTextBase64) {
        if (cipherTextBase64 == null || cipherTextBase64.isBlank()) return null;
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(cipherTextBase64);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] plainText = cipher.doFinal(cipherBytes);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar dato en reposo", e);
        }
    }
}