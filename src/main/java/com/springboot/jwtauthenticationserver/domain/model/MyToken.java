package com.springboot.jwtauthenticationserver.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

@Entity
public class MyToken {
    @Id
    @GeneratedValue
    private  Long id;
    private String tokenValue;

    public MyToken(String value) {
        this.tokenValue = value;
    }
}
