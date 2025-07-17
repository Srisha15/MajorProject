package com.E2DShare.filemngt.service;


import java.security.PrivateKey;
import java.security.PublicKey;

public interface UserKeyService {
    PublicKey getReceiverPublicKey(String receiverId) throws Exception;
    PrivateKey getReceiverPrivateKey(String userId) throws Exception;

}
