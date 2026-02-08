package pt.ipleiria.estg.dei.ei.dae.academics.security;

import jakarta.enterprise.context.ApplicationScoped;

import io.jsonwebtoken.Jwts;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@ApplicationScoped
public class TokenIssuer {
    protected static final byte[] SECRET_KEY = "veRysup3rstr0nginv1ncible5ecretkeY@academics.dae.ipleiria".getBytes();

    protected static final String ALGORITHM = "HMACSHA384";

    public static final long EXPIRY_MINS = 60L;

    public String issue(String username){
        var expiricyPeriod = LocalDateTime.now().plusMinutes(EXPIRY_MINS);
        var expirationDateTime= Date.from(expiricyPeriod.atZone(ZoneId.systemDefault()).toInstant());
        Key key = new SecretKeySpec(SECRET_KEY,ALGORITHM);

        return Jwts.builder()
                .setSubject(username)
                .issuedAt(new Date())
                .setExpiration(expirationDateTime)
                .signWith(key)
                .compact();
    }
}
