package com.example.moneymanagement.repository;

import com.example.moneymanagement.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long>{

    // select * from profile where email=?
    Optional<ProfileEntity>findByEmail(String email);

    //
    Optional<ProfileEntity>findByActivationToken(String activationToken);

}
