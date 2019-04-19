package com.springboot.jwtauthenticationserver.security;

import com.springboot.jwtauthenticationserver.domain.model.User;
import freemarker.template.utility.DateUtil;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;

@Component
public class JwtTokenUtility {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtility.class);

    private static final Long jwtValidityPeriod = (long)(200 * 10 * 1000);
    private static final Long jwtRefreshValidityPeriod = (long)(3600 * 168 * 1000);
    private static final Long jwtServiceValidityPeriod = (long)(200 * 10 * 1000);
    private static String code;
    private static String appID = "12345";
    private static String appSecret = "cat";

    @Value("${app.jwt_secret}")
    private String jwtSecret;


    public String generateCode(String appID) {
        int n = 7;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        code = sb.toString().concat(appID);
        return code;
    }



    public String generateToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Date expiryDate = new Date(new Date().getTime() + jwtValidityPeriod);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

    }

    public String generateTokenForForeignService(String appID, String appSecret, String code) {
        if (appID.equals(this.appID) && appSecret.equals(this.appSecret) && code.equals(this.code))
        {
            Date expiryDate = new Date(new Date().getTime() + jwtValidityPeriod);
            return Jwts.builder()
                    .setSubject(code)
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
        }
        return null;
    }

    public String generateRefreshToken(Authentication authentication){
        User user = (User) authentication.getPrincipal();

        Date expiryDate = new Date(new Date().getTime() + jwtRefreshValidityPeriod);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Claims getClaimsBody(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpiryDateFromJwt(String token) {
        return getClaimsBody(token).getExpiration();
    }

    public Long getUserIdFromJwt(String token) {
        return Long.parseLong(
                getClaimsBody(token)
                        .getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .requireExpiration(getExpiryDateFromJwt(token))
                    .parseClaimsJws(token);

            return true;
        }catch (SignatureException | ExpiredJwtException
                | UnsupportedJwtException | IllegalArgumentException ex) {
            logger.error("CLASSIC TOKEN EXPIRED");
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .requireExpiration(getExpiryDateFromJwt(token))
                    .parseClaimsJws(token);
            return true;
        }catch (SignatureException | ExpiredJwtException
                | UnsupportedJwtException | IllegalArgumentException ex) {
            logger.error("REFRESH TOKEN EXPIRED");
            return false;
        }
    }

    public String refreshClassicToken(String refreshtoken){
        if (validateRefreshToken(refreshtoken)){
            Date expiryDate = new Date(new Date().getTime() + jwtRefreshValidityPeriod);
            return Jwts.builder()
                    .setSubject(Long.toString(this.getUserIdFromJwt(refreshtoken)))
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)
                    .compact();
        }
        return null;
    }


    public Claims getClaimsBodyForServiceToken(String token,String appSecret) {
        return Jwts.parser()
                .setSigningKey(appSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpiryDateFromServiceToken(String token, String appSecret) {
        return getClaimsBodyForServiceToken(token,appSecret).getExpiration();
    }

    public boolean validateServiceToken(String appSecret, String token) {
        System.out.println("######TOKEN:" + token + "  #####AppSECRET:" + appSecret);
        try {
            Jwts.parser()
                    .setSigningKey(appSecret)
                    .requireExpiration(getExpiryDateFromServiceToken(token,appSecret))
                    .parseClaimsJws(token);

            return true;
        }catch (SignatureException | ExpiredJwtException
                | UnsupportedJwtException | IllegalArgumentException ex) {
            logger.error("SERVICE TOKEN EXPIRED");
            return false;
        }
    }

    public String generateTokenForService(Long appid, String appSecret) {
        System.out.println("######APPID:" + appid + "  #####AppSECRET:" + appSecret);
        Date expiryDate = new Date(new Date().getTime() + jwtServiceValidityPeriod);

        return Jwts.builder()
                .setSubject(Long.toString(appid))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, appSecret)
                .compact();

    }


}
