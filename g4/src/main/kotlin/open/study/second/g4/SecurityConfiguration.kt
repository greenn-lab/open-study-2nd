package open.study.second.g4

import open.study.second.g4.jwt.JwtAuthenticationManager
import open.study.second.g4.jwt.JwtServerAuthenticationConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationManager: JwtAuthenticationManager,
        jwtServerAuthenticationConverter: JwtServerAuthenticationConverter
    ): SecurityWebFilterChain {

        val authenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter)

        return http
            .authorizeExchange()
            .pathMatchers("/user/signup", "/user/login")
            .permitAll()
            .pathMatchers("/user")
            .authenticated()

            .and()
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .logout().disable()

            .build()
    }

}
