package open.study.second.g4.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm.RS256
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class JwtSigner {

    val keyPair: KeyPair = Keys.keyPairFor(RS256)

    fun createJwt(userId: String): String {
        return Jwts.builder()
            .signWith(keyPair.private, RS256)
            .setSubject(userId)
            .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
            .setIssuer("identity")
            .setIssuedAt(Date.from(Instant.now()))
            .compact()
    }

    fun validateJwt(jws: String): Jws<Claims> {
        return Jwts.parserBuilder()
            .setSigningKey(keyPair.public)
            .build()
            .parseClaimsJws(jws)
    }

}
