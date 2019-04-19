package com.springboot.jwtauthenticationserver.payload;

import javax.validation.constraints.*;

public class RegisterRequest {

    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 15, message = "Username must be min=3 and max=15")
    private String username;

    @NotEmpty
    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @NotNull
    @NotEmpty
    @Size(min = 3, max = 15, message = "Password must be min=3 and max=15")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
