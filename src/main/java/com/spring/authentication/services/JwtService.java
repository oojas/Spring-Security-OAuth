package com.spring.authentication.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
* Claims are basically user information. It consists of the type of audience the jwt token is intended for. Eg : google.com, expiration time, username etc.
* There can be multiple claims in the token. Along with user information claims can also hold additional data required in the application.
* Basically the payload is a collection of claims. sub, username, role etc are all claims. We can have additional claims like exp time etc.
* Eg : Payload : {
  "sub": "1234567890",              // Subject (usually the user ID)
  "name": "John Doe",               // Custom claim (name)
  "role": "admin",                  // Custom claim (role)
  "iss": "auth.example.com",        // Issuer
  "exp": 1672291400                 // Expiration time (Unix timestamp)
}
In this payload : sub, name and all others are individual claims. that is why while extracting the userEmail we are using extractClaims method.
* */

/*
* Example of Function used in line : 28. FYI : Function is used commonly with Lambda Expression
* // A Function that takes an Integer and returns its String representation
Function<Integer, String> intToString = i -> "Number: " + i;

// Applying the function
String result = intToString.apply(5);  // Output: "Number: 5"

* */

@Service
public class JwtService {
    private static final String SECRET_KEY="1f2b3d4a5e6f7c8d9a0b1c2d3e4f5061728394a5b6c7d8e9fa0b1c2d3e4f5061";
    public String extractUserName(String jwtToken) {
    return extractClaim(jwtToken,Claims::getSubject);
    }
    /*
    * Claims::getSubject : this line can also be written as getClaim(){claim.getSubject()}. But in order to pass as a function parameter we write it
    * using method reference which basically is implying to use the getSubject method of the claim
    * */

    public<T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claim=extractAllClaims(token);
        return claimResolver.apply(claim);
        // this will return : "1234567890"
    }
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        // this will return :
        /*
        * {
            "sub": "1234567890",              // Subject (usually the user ID)
            "name": "John Doe",               // Custom claim (name)
            "role": "admin",                  // Custom claim (role)
            "iss": "auth.example.com",        // Issuer
            "exp": 1672291400                 // Expiration time (Unix timestamp)
          }
        * */
        /*
        * signing Key : this is basically to verify whether the sender is exactly what it claims to be. Every system has its own signing key.
        * */
    }

    /*
    * This method is basically converting the String secret key into a byte of array and then into a object of Key which is needed as a signing key by jwt
    * */
    private Key getSigningKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // this method can be used if we want to generate token without claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails); // since we dont need other claims and only useremail so we can pass extraClaims as empty.
    }
    public String generateToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails
    ){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails
                        .getUsername()) // this is the place we are actually setting the email.
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60+24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        // compact generates the token
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExp(token)); // Basically we are checking that the token that we have received from the request
        // has the same username as the username from the UserDetails from the DB, and also whether the token is expired or not.
    }
    public boolean isTokenExp(String token){
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
}
