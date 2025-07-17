package com.E2DShare.filemngt.service;

import com.E2DShare.filemngt.entity.UserKey;
import com.E2DShare.filemngt.repository.UserKeyRepository;
import com.E2DShare.filemngt.security.RSAUtil;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class UserKeyServiceImpl implements UserKeyService {

    private final UserKeyRepository userKeyRepository;

    public UserKeyServiceImpl(UserKeyRepository userKeyRepository) {
        this.userKeyRepository = userKeyRepository;
    }

    @Override
    public PublicKey getReceiverPublicKey(String receiverId) throws Exception {
        UserKey key = userKeyRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Key not found for receiver: " + receiverId));

        byte[] publicBytes = Base64.getDecoder().decode(key.getPublicKey());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    @Override
//    public PrivateKey getReceiverPrivateKey(String receiverId) throws Exception {
//        receiverId = receiverId.trim();
//        UserKey key = userKeyRepository.findById(receiverId)
//                .orElseThrow(() -> new RuntimeException("Key not found for receiver: " + receiverId));
//        return RSAUtil.decodePrivateKey(key.getPrivateKey());
//    ?}
    public PrivateKey getReceiverPrivateKey(String receiverId) throws Exception {
        if (receiverId == null || receiverId.trim().isEmpty()) {
            throw new IllegalArgumentException("receiverId is null or empty");
        }

        String cleanReceiverId = receiverId.trim(); // effectively final

        UserKey key = userKeyRepository.findById(cleanReceiverId)
                .orElseThrow(() -> new RuntimeException("Key not found for receiver: " + cleanReceiverId));

        return RSAUtil.decodePrivateKey(key.getPrivateKey());
    }





}
