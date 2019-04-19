package com.springboot.jwtauthenticationserver.controller;

import com.springboot.jwtauthenticationserver.domain.dao.UserRepository;
import com.springboot.jwtauthenticationserver.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String main() {
        return "main";
    }

    @GetMapping("/info")
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }
}
