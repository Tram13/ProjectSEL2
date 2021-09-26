package be.sel2.api.util.jwt;

import be.sel2.api.util.key_readers.PrivateKeyReader;
import be.sel2.api.util.key_readers.PublicKeyReader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Util class used to generate and manipulate JWT tokens
 */
@Component
public class JwtUtil implements Serializable {

    public static final long TOKEN_VALIDITY = 2 * 60 * 60L;
    public static final long REFRESH_TOKEN_VALIDITY = 12 * 60 * 60L;

    /* Warning: these files should be re-generated when rolling out
    How to generate:
    > openssl genrsa -out jwt-keypair.pem 2048
    > openssl rsa -in jwt-keypair.pem -pubout -out jwt-publickey.crt
    > openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in jwt-keypair.pem -out jwt-private-pkcs8.key
     */
    private static final String PUBLIC_KEY_PATH = "classpath:JWTKeys/jwt-publickey.crt";
    private static final String PRIVATE_KEY_PATH = "classpath:JWTKeys/jwt-private-pkcs8.key";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${jwt-session-type}")
    private String jwtSessionType;

    public JwtUtil(@Autowired ResourceLoader resourceLoader) throws Exception {
        Resource privateKeyResource = resourceLoader.getResource(PRIVATE_KEY_PATH);
        Resource publicKeyResource = resourceLoader.getResource(PUBLIC_KEY_PATH);

        privateKey = PrivateKeyReader.get(privateKeyResource.getInputStream());
        publicKey = PublicKeyReader.get(publicKeyResource.getInputStream());
    }

    public Long extractId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Boolean isCorrectTokenType(String token, String tokenType) {
        return extractAllClaims(token).get(jwtSessionType, String.class).equals(tokenType);
    }

    public String generateToken(Long id) {
        Claims claims = Jwts.claims().setSubject(id.toString());
        claims.put(jwtSessionType, "sessionToken");
        return createToken(claims);
    }

    private String createToken(Claims claims) {
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256).compact();
    }

    public String generateRefreshToken(Long id) {
        Claims claims = Jwts.claims().setSubject(id.toString());
        claims.put(jwtSessionType, "refreshToken");
        return createRefreshToken(claims);
    }

    private String createRefreshToken(Claims claims) {
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256).compact();
    }


    public Boolean validateToken(String token) {
        return (!isTokenExpired(token) && isCorrectTokenType(token, "sessionToken"));
    }

    public Boolean validateRefreshToken(String token) {
        return (!isTokenExpired(token) && isCorrectTokenType(token, "refreshToken"));
    }
}
