package configuration;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration("commonConfig")
public class CommonConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Semua endpoint API
                        .allowedOrigins("*") // Izinkan semua domain (atau ganti jadi "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Method yang boleh
                        .allowedHeaders("*"); // Semua header diizinkan
            }
        };
    }
}
