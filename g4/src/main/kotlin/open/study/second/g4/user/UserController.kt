package open.study.second.g4.user

import open.study.second.g4.jwt.JwtSigner
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/user")
class UserController(private val jwtSigner: JwtSigner) {

    private val users: MutableMap<String, UserCredential> =
        mutableMapOf(
            "tester@test.com" to UserCredential("tester@test.com", "test123")
        )

    @PutMapping("/signup")
    fun signUp(@RequestBody userCredential: UserCredential): Mono<ResponseEntity<Void>> {
        users[userCredential.email] = userCredential

        return Mono.just(ResponseEntity.noContent().build())
    }

    @PostMapping("/login")
    fun login(@RequestBody user: UserCredential): Mono<ResponseEntity<Void>> {
        return Mono.justOrEmpty(users[user.email])
            .filter { it.password == user.password }
            .map {
                val jwt = jwtSigner.createJwt(it.email)

                val authCookie = ResponseCookie.fromClientResponse("X-Auth", jwt)
                    .maxAge(3600)
                    .httpOnly(true)
                    .path("/")
                    .secure(false)
                    .build()

                ResponseEntity.noContent()
                    .header("Set-Cookie", authCookie.toString())
                    .build<Void>()
            }
            .switchIfEmpty(
                Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
            )
    }

    @GetMapping
    fun pingPong(): Mono<ResponseEntity<User>> {
        val email = "tester@test.com"

        return Mono.justOrEmpty(users[email])
            .map { ResponseEntity.ok(User(email)) }
            .switchIfEmpty(
                Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
            )
    }

}
