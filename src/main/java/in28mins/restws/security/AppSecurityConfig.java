package in28mins.restws.security;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppSecurityConfig
{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        // 1. All Requests should be authenticated
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());

        // Disable Login web Page - show in popup
        http.httpBasic(withDefaults());

        // Disable CSRF - PUT-POST
        http.csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
