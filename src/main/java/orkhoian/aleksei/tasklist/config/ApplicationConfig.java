package orkhoian.aleksei.tasklist.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import orkhoian.aleksei.tasklist.security.JwtTokenFilter;
import orkhoian.aleksei.tasklist.security.JwtTokenProvider;
import orkhoian.aleksei.tasklist.service.props.MinioProperties;
import orkhoian.aleksei.tasklist.utils.ExcludeFromJacocoGeneratedReport;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ExcludeFromJacocoGeneratedReport
public class ApplicationConfig {

    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
    public static final String AUTH_URI = "/api/v1/auth/**";
    public static final String SWAGGER_URI = "/swagger-ui/**";
    public static final String DOCKS_URI = "/v3/api-docs/**";
    public static final String NULAB_URI = "/nulab/**";

    private final JwtTokenProvider tokenProvider;
    private final MinioProperties minioProperties;

    @Autowired
    public ApplicationConfig(@Lazy JwtTokenProvider tokenProvider, MinioProperties minioProperties) {
        this.tokenProvider = tokenProvider;
        this.minioProperties = minioProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioProperties.getUrl())
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(config -> config
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(UNAUTHORIZED_MESSAGE);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write(UNAUTHORIZED_MESSAGE);
                }))
            .authorizeHttpRequests(config -> config
                .requestMatchers(AUTH_URI).permitAll()
                .requestMatchers(SWAGGER_URI).permitAll()
                .requestMatchers(DOCKS_URI).permitAll()
                .requestMatchers(NULAB_URI).permitAll()
                .anyRequest().authenticated())
            .anonymous(AbstractHttpConfigurer::disable)
            .addFilterBefore(new JwtTokenFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }
}
