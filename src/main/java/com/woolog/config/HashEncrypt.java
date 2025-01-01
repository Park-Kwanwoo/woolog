package com.woolog.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Component
public class HashEncrypt {

    @Value("${hash.salt}")
    private String salt;

    public String encrypt(String text) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltId = salt + text;
            md.update(saltId.getBytes());

            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
