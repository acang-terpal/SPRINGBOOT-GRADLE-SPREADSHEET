package configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.postgres.SpreadSheetAccountService;

import java.util.List;

@Configuration
@Scope("prototype")
public class SecurityConfig {
    @Autowired
    SpreadSheetAccountService spreadSheetAccountService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. AKTIFKAN CORS menggunakan konfigurasi dari bean di bawah
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF mattin dulu buat development
                .csrf(csrf -> csrf.disable())
                // 2. BEBASIN SEMUA URL biar gak kena gembok (Lakukan manual check di Controller)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // 3. MATIIN FORM LOGIN BAWAAN SPRING
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Daftarkan alamat dan port frontend Anda (jangan pakai tanda / di akhir)
        configuration.setAllowedOrigins(List.of("http://localhost:7001", "http://127.0.0.1:7001"));

        // Izinkan method HTTP yang dibutuhkan
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Izinkan semua header (seperti Authorization, Content-Type, X-XSRF-TOKEN)
        configuration.setAllowedHeaders(List.of("*"));

        // Izinkan pengiriman cookie / session / credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
