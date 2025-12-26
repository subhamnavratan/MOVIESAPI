package com.SUBHAM.MOVIEAPI.auth.services;

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
/*It does NOT:
authenticate users
talk to the database
It only works with tokens.*/
@Service
public class JwtService {

    private static final String SECRET_KEY = "BF7FD11ACE545745B7BA1AF98B6F156D127BC7BB544BAB6A4FD74E4FC7";
/*JWT with HS256 uses symmetric encryption:
If someone doesn’t know this key → they cannot forge a valid token*/
    // extract username from JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
   /*This method allows you to extract any claim from JWT.
   .To know who the user is and what they are allowed to do—without hitting the database.*/
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // extract information from JWT.
    // Without this method, claims cannot be trusted.
    //Claims = Map-like object holding all JWT data
    //Parsing means taking a raw string and breaking it into a structured, usable form.
    private Claims extractAllClaims(String token) {

        // Create a JWT parser using the JJWT utility class
        return Jwts
                // Initialize a parser builder to configure how the JWT should be parsed
                .parserBuilder()

                // Set the secret key used to verify the JWT signature
                // This ensures the token was issued by our server and not tampered with
                .setSigningKey(getSignInKey())

                // Build the configured JWT parser
                .build()

                // Parse the JWT string:
                // - validates the token structure
                // - verifies the signature
                // - checks expiration
                .parseClaimsJws(token)

                // Extract and return the payload (claims) part of the JWT
                // Claims contain username, roles, expiration time, etc.
                .getBody();
    }

    // decode and get the key
    /*What this method does (in simple words)
This method converts a Base64-encoded secret string into a secure
cryptographic key that is used to sign and verify JWT tokens using the HMAC SHA-256 algorithm.*/
    private Key getSignInKey() {
        // decode SECRET_KEY
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // generate token using Jwt utility class and return token as String
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        extraClaims = new HashMap<>(extraClaims);
        extraClaims.put("role", userDetails.getAuthorities());
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 25 * 100000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // if token is valid by checking if token is expired for current user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // get expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

