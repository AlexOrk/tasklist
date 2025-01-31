package orkhoian.aleksei.tasklist.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import orkhoian.aleksei.tasklist.security.JwtEntity;

public class WithMockJwtUserSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        JwtEntity jwtEntity = JwtEntity.builder()
            .id(annotation.id())
            .username(annotation.username())
            .name(annotation.name())
            .password(annotation.password())
            .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtEntity, null, null);
        context.setAuthentication(authentication);

        return context;
    }
}
