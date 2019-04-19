package com.springboot.jwtauthenticationserver;

import com.springboot.jwtauthenticationserver.domain.dao.UserRepository;
import com.springboot.jwtauthenticationserver.domain.model.Role;
import com.springboot.jwtauthenticationserver.domain.model.User;
import com.springboot.jwtauthenticationserver.security.JwtTokenUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackageClasses = {
        JwtAuthenticationServerApplication.class,
        Jsr310Converters.class})
public class JwtAuthenticationServerApplication implements CommandLineRunner {
    // you must extend SpringBootServletInitializer for WAR deployment

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthenticationServerApplication.class, args);
	}

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(JwtAuthenticationServerApplication.class);
//	}

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtility tokenProvider;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * @param username as a raw representation. Not encrypted.
     * @param password as a raw representation. Not encrypted.
     * To generate Jwt token on start. You must to encrypt user password with
     * {@link PasswordEncoder} or your own implementation;
     */
    public String getToken(String username, String password) {
        UsernamePasswordAuthenticationToken upToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(upToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.generateToken(authentication);
    }

    @Override
    public void run(String... args) {
        String defaultPassword = "secret_password";
        User admin = new User(
                "admin",
                "admin@gmail.com",
                encoder.encode(defaultPassword));

        User simpleUser = new User(
                "simpleUser",
                "simpleUser@gmail.com",
                encoder.encode(defaultPassword));

        User user3 = new User(
                "alex",
                "alex@gmail.com",
                defaultPassword);

        User user4 = new User(
                "greg",
                "greg@gmail.com",
                defaultPassword);

        User user5 = new User(
                "helen",
                "hellen@gmail.com",
                defaultPassword);

        admin.setRoles(Collections.singleton(Role.ADMIN));
        simpleUser.setRoles(Collections.singleton(Role.USER));
        user3.setRoles(Collections.singleton(Role.USER));
        user4.setRoles(Collections.singleton(Role.USER));
        user5.setRoles(Collections.singleton(Role.USER));

        userRepository.save(admin);
        userRepository.save(simpleUser);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);

        String adminToken = getToken(admin.getUsername(), defaultPassword);
        String userToken = getToken(simpleUser.getUsername(), defaultPassword);

        System.out.println("====================== USERS LIST ============================ \n \n");
        System.out.println(
                        admin.toString()+"\n" +
                        simpleUser.toString()+"\n" +
                        user3.toString()+"\n" +
                        user4.toString()+"\n" +
                        user5.toString() + "\n"
        );

        System.out.println("=============================================================== \n \n");

        System.out.println("====================== ADMIN TOKEN ============================ \n \n");

        System.out.println(adminToken + "\n \n");

        System.out.println("===============================================================");

        System.out.println("====================== SIMPLE USER TOKEN ============================ \n \n");

        System.out.println(userToken + "\n \n");

        System.out.println("===============================================================");
    }
}
