package com.example.BasicSecuritySetup.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


// CLAIMS = JWT PAYLOAD
@Service
public class JwtService {

    private final String SECRET_KEY = getKey();

    private String getKey() {
        String secretKey = null;

        try {
            Configuration config = new Configurations().properties("src/main/resources/application.properties");
            secretKey = config.getString("secretKey");
            System.out.println("Secret Key: " + secretKey);
        } catch (ConfigurationException e) {
            // Handle the exception if the configuration file cannot be loaded
            e.printStackTrace();
        }
        if(secretKey == null){
            return "Error";
        }
        return secretKey;

    }


    // Method which extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    // Method which generates a token with username, expiration date and sign in key
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
      return Jwts
              .builder()
              .setClaims(extraClaims)
              .setSubject(userDetails.getUsername())
              .setIssuedAt(new Date(System.currentTimeMillis()))
              .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
              .signWith(getSigningKey(), SignatureAlgorithm.HS256)
              .compact();
    }

    // Method which check if token is valid based on username and expiration date
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Method which check if token has expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // Method which extracts the expiration from a given token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    // Method which extracts a JWT payload from a given token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
}


    public String generateToken(UserDetails userDetails){
            return generateToken(new HashMap<>(), userDetails);
    }


    // Method which uses JWT libary to verify a JWT token.
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Method which generate a sign in key
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
