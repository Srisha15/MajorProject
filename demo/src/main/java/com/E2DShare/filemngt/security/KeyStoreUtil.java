package com.E2DShare.filemngt.security;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyStoreUtil {
    public static PublicKey getReceiverPublicKey() throws Exception {
        // Replace with real key loading (e.g., DB, PEM file, keystore)
        String base64PublicKey = "MIIBIjANBgkq..."; // Replace with real
        byte[] decoded = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
