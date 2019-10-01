package com.spring.fm.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.spring.fm.security.SecurityConstants.PUB;
import static com.spring.fm.security.SecurityConstants.SEC;

class EncryptionUtil {

    static Key getPrivateKey() {
        try {
            PKCS8EncodedKeySpec privks = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(SEC));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(privks);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    static Key getPublicKey() {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubks = new X509EncodedKeySpec(Base64.getDecoder().decode(PUB));
            return kf.generatePublic(pubks);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
