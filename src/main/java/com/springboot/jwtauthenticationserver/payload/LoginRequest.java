package com.springboot.jwtauthenticationserver.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    @Size(min = 3, max = 15, message = "LoginRequest username min=3, max=15")
    private String usernameOrEmail;

    @NotBlank
    @NotEmpty
    @NotNull
    @Size(min = 3, max = 15, message = "LoginRequest password min=3, max=15")
    private String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
