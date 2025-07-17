package com.E2DShare.filemngt.repository;


import com.E2DShare.filemngt.entity.UserKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserKeyRepository extends JpaRepository<UserKey, String>{
    Optional<UserKey> findByUsername(String username);
}
