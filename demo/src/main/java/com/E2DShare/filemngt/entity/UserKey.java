package com.E2DShare.filemngt.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class UserKey {

    @Id
    private String userId; // sender or receiver ID
//    private String publicKey; // Base64
//    private String privateKey;
    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 2048)
    private String publicKey;

    @Column(length = 2048)
    private String privateKey;


}
