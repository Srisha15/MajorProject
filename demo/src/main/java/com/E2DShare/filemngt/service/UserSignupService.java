package com.E2DShare.filemngt.service;

import com.E2DShare.filemngt.entity.UserKey;
import com.E2DShare.filemngt.repository.UserKeyRepository;
import com.E2DShare.filemngt.security.RSAUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class UserSignupService {
    private final UserKeyRepository userKeyRepository;

    public UserSignupService(UserKeyRepository userKeyRepository) {
        this.userKeyRepository = userKeyRepository;
    }

    public void registerUser(String userId, String username, String password) throws Exception {
        var keyPair = RSAUtil.generateKeyPair();

        UserKey key = new UserKey();
        key.setUserId(userId);
        key.setUsername(username);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        key.setPasswordHash(Base64.getEncoder().encodeToString(hash));

        key.setPublicKey(RSAUtil.encodeKey(keyPair.getPublic()));
        key.setPrivateKey(RSAUtil.encodeKey(keyPair.getPrivate()));

        userKeyRepository.save(key); // ✅ fixed
    }
}
