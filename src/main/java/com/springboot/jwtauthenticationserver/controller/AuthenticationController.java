package com.springboot.jwtauthenticationserver.controller;

import com.oracle.javafx.jmx.json.JSONException;
import com.springboot.jwtauthenticationserver.domain.dao.MyTokenRepository;
import com.springboot.jwtauthenticationserver.domain.dao.UserRepository;
import com.springboot.jwtauthenticationserver.domain.model.MyToken;
import com.springboot.jwtauthenticationserver.payload.ApiResponse;
import com.springboot.jwtauthenticationserver.payload.LoginRequest;
import com.springboot.jwtauthenticationserver.payload.RegisterRequest;
import com.springboot.jwtauthenticationserver.security.JwtTokenUtility;
import com.springboot.jwtauthenticationserver.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private final MyTokenRepository myTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtility tokenProvider;

    public AuthenticationController(MyTokenRepository repository) {
        this.myTokenRepository = repository;
    }

    @GetMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCode() throws IOException, JSONException{
        return "{\"message\": \"Hello!\"}";
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        HttpHeaders responseHeaders = new HttpHeaders();

        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        responseHeaders.add("Classic:  ", jwt );

        myTokenRepository.save(new MyToken(jwt));
        System.out.println("##SAVED TOKEN: |" + jwt + "|###");

        responseHeaders.add("Refresh: ", refreshToken);

        return new ResponseEntity<>(
                new ApiResponse(true, "User was successfully authenticated"),
                responseHeaders, HttpStatus.OK);
    }

    @PostMapping(path = "/login/{login}/{password}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String authenticateForeignService(@PathVariable String login, @PathVariable String password) {
        System.out.println("####### IN SERVICE METHOD LOGIN! ########");

        System.out.println("LOGIN:"+ login+ "| PASSWORD: " + password);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(login);
        loginRequest.setPassword(password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        //System.out.println(authentication.getCredentials());
        if (authentication.isAuthenticated()) System.out.println("IS AUTHENTICATED");
        return "{\"result\": \" true \"}";
    }

    @GetMapping(path = "/vk/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String authenticateViaVkontakte(@RequestParam(value = "token", required = false) String token, @PathVariable String code) {
        System.out.println("####### IN SERVICE METHOD VK AUTH! ########");

        if (token == null) System.out.println("token IS NULL");
        else System.out.println(token);

        RestTemplate restTemplate = new RestTemplate();
        String theUrl = "https://oauth.vk.com/authorize?client_id=6948120&display=page&redirect_uri=http://localhost:8080/setinfo&client_secret=kehMqJWjjfVVN4pGB68j&code=" + code;
        HttpEntity<?> requestEntity = new HttpEntity<>("");
        ResponseEntity<String> responseAuth = restTemplate.exchange(theUrl, HttpMethod.POST, requestEntity, String.class );
        System.out.println("########## @@@@@@@@@@@@@ THE RESPONSE IS: " + responseAuth.getBody().toString());

        return responseAuth.getBody().toString();

    }

    @GetMapping(path = "/gettoken/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getToken(@PathVariable String code) {
        System.out.println("####### IN SERVICE METHOD VK AUTH! ########");
        //return "hello!";

        RestTemplate restTemplate = new RestTemplate();
        String theUrl = "https://oauth.vk.com/authorize?client_id=6948120&display=page&client_secret=kehMqJWjjfVVN4pGB68j&code=" + code;
        HttpEntity<?> requestEntity = new HttpEntity<>("");
        ResponseEntity<String> responseAuth = restTemplate.exchange(theUrl, HttpMethod.POST, requestEntity, String.class );
        System.out.println("########## @@@@@@@@@@@@@ THE RESPONSE IS: " + responseAuth.getBody().toString());

        return responseAuth.getBody().toString();

    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return new ResponseEntity<>(
                    new ApiResponse(false, "User already exists!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>(
                    new ApiResponse(false, "Email already in use"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if(userService.createNewUser(registerRequest))
            return ResponseEntity.ok(new ApiResponse(true, "User was created"));
        else
            return new ResponseEntity<>(
                    new ApiResponse(false, "User wasn't created"),
                    HttpStatus.BAD_REQUEST
            );
    }

    @PostMapping(value = "/check/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token){
        System.out.println("####### IN METHOD CHECK! ########");

        if(tokenProvider.validateToken(token))
            return ResponseEntity.ok(new ApiResponse(true, "Token is Valid"));

        else
            return new ResponseEntity<>(
                    new ApiResponse(false, "Token is not Valid"),
                    HttpStatus.BAD_REQUEST
            );
    }

    /* @PostMapping(value = "/checkref/{token}")
    public ResponseEntity<?> checkRefreshToken(@PathVariable String token){
        System.out.println("####### IN METHOD REFRESHCHECK! ########");

        if(tokenProvider.validateRefreshToken(token))
            return ResponseEntity.ok(new ApiResponse(true, "Token is Valid"));

        else
            return new ResponseEntity<>(
                    new ApiResponse(false, "Token is not Valid"),
                    HttpStatus.BAD_REQUEST
            );
    }*/



    @PostMapping(value = "/refresh/{token}")
    public ResponseEntity<?> refreshToken(@PathVariable String token) {
        if (tokenProvider.validateRefreshToken(token)) {
            HttpHeaders responseHeaders = new HttpHeaders();

            responseHeaders.add("Classic:  ", tokenProvider.refreshClassicToken(token));

            return new ResponseEntity<>(
                    new ApiResponse(true, ""),
                    responseHeaders, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ApiResponse(false, "Refresh Token is not Valid"),
                HttpStatus.BAD_REQUEST
        );
    }

    @PostMapping("/service/{appid}/{appSecret}")
    public String authenticateService(@PathVariable Long appid, @PathVariable String appSecret) {
        System.out.println("####### IN SERVICE METHOD GENERATE! ########");

        String appToken = tokenProvider.generateTokenForService(appid,appSecret);

        //HttpHeaders responseHeaders = new HttpHeaders();

        //responseHeaders.add("Service: ", appToken);

        return "{\"token\":" +  "\""  + appToken +"\"}";

        /*return new ResponseEntity<>(
                new ApiResponse(true, "Service token generated"),
                responseHeaders, HttpStatus.OK).toString();*/
    }

    @GetMapping(path = "/service/{appID}/{appSecret}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String authenticateForeignService(@PathVariable String appID,@PathVariable String appSecret,@PathVariable String code) {
        System.out.println("####### IN SERVICE METHOD GENERATE! ########");

        String appToken = tokenProvider.generateTokenForForeignService(appID, appSecret,code);

        //HttpHeaders responseHeaders = new HttpHeaders();

        //`responseHeaders.add("Service:  ", appToken );

        return "{\"token\":" +  "\""  + appToken +"\"}";
    }

    @PostMapping(value = "/check/service/{appSecret}/{token}")
    public String checkToken(@PathVariable String token,@PathVariable String appSecret){
        System.out.println("####### IN SERVICE METHOD CHECK! ########");

        if(tokenProvider.validateServiceToken(appSecret,token))
            return "true";
        else
            return "true";
    }

    @GetMapping(path = "/service/{appID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateTempCode(@PathVariable String appID) {
        return tokenProvider.generateCode(appID);
    }


}

