package com.springboot.jwtauthenticationserver.domain.dao;

import com.springboot.jwtauthenticationserver.domain.model.MyToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyTokenRepository extends JpaRepository<MyToken,Long> {

}

