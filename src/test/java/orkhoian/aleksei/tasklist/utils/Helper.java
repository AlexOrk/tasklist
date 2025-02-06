package orkhoian.aleksei.tasklist.utils;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Helper {

    public Helper() {
    }

    public static List<GrantedAuthority> getSortedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
            .sorted(Comparator.comparing(GrantedAuthority::getAuthority))
            .collect(Collectors.toList());
    }
}
