package orkhoian.aleksei.tasklist.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import orkhoian.aleksei.tasklist.domain.exception.AccessDeniedException;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.service.props.JwtProperties;
import orkhoian.aleksei.tasklist.dto.auth.JwtResponse;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private SecretKey key;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties, UserDetailsService userDetailsService, UserService userService) {
        this.jwtProperties = jwtProperties;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(Long userId, String username, Set<Role> roles) {
        Claims claims = Jwts.claims()
            .subject(username)
            .add("id", userId)
            .add("roles", resolveRoles(roles))
            .build();

        Instant validity = Instant.now()
            .plus(jwtProperties.getAccess(), ChronoUnit.DAYS);

        return Jwts.builder()
            .claims(claims)
            .expiration(Date.from(validity))
            .signWith(key)
            .compact();
    }

    public String createRefreshToken(Long userId, String username) {
        Claims claims = Jwts.claims()
            .subject(username)
            .add("id", userId)
            .build();

        Instant validity = Instant.now()
            .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);

        return Jwts.builder()
            .claims(claims)
            .expiration(Date.from(validity))
            .signWith(key)
            .compact();
    }

    public JwtResponse refreshUserTokens(String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!isValid(refreshToken)) {
            throw new AccessDeniedException();
        }
        Long userId = Long.valueOf(getId(refreshToken));
        User user = userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setAccessToken(createAccessToken(userId, user.getUsername(), user.getRoles()));
        jwtResponse.setRefreshToken(createRefreshToken(userId, user.getUsername()));
        return jwtResponse;
    }

    public boolean isValid(String token) {
        Jws<Claims> claims = Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);

        return claims.getPayload()
            .getExpiration()
            .after(new Date());
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private List<String> resolveRoles(Set<Role> roles) {
        return roles.stream()
            .map(Enum::name)
            .collect(Collectors.toList());
    }

    private String getId(String token) {
        return Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("id", String.class);
    }

    private String getUsername(String token) {
        return Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
}
