package techForAll.techPoints.config
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//
//
//@Configuration
//@EnableWebSecurity
class SecurityConfig {
//
//    @Bean
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        http {
//            authorizeHttpRequests {
//                authorize("/login", permitAll)
//                authorize(anyRequest, authenticated)
//            }
//        }
//
//        return http.build()
//    }
//
//    @Bean
//    fun authenticationManager(
//        userDetailsService: UserDetailsService,
//        passwordEncoder: PasswordEncoder): AuthenticationManager {
//        val authenticationProvider = DaoAuthenticationProvider()
//        authenticationProvider.setUserDetailsService(userDetailsService)
//        authenticationProvider.setPasswordEncoder(passwordEncoder)
//
//        return ProviderManager(authenticationProvider)
//    }
//
//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val user = User.withDefaultPasswordEncoder()
//            .username("user")
//            .password("password")
//            .roles("USER")
//            .build()
//
//        return InMemoryUserDetailsManager(user)
//    }
//
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
//    }
//
}