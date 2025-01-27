package orkhoian.aleksei.tasklist.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import orkhoian.aleksei.tasklist.domain.exception.NulabResponseException;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.security.JwtEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public final class Utils {

    private Utils() {
    }

    public static String getCurrentUserApiKey(UserService userService) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtEntity jwtEntity) {
            return Optional.ofNullable(userService.getById(jwtEntity.getId()).getApiKey())
                .orElseThrow(() -> new NulabResponseException("User does not have Nulab ApiKey"));
        }
        throw new RuntimeException("User not authorised!");
    }

    //todo: do we need a default response? will be better to return an exception info instead?
//    public static <T> T executeWithApiKey(Supplier<T> action, T fallbackValue) {
//        try {
//            return action.get();
//        } catch (NulabResponseException ex) {
//            LOG.warn("Authorization failed: {}", ex.getMessage());
//            return fallbackValue;
//        }
//    }

    public static void checkFutureDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate inputDate = LocalDate.parse(date, formatter);
            LocalDate today = LocalDate.now();

            if (inputDate.isBefore(today)) {
                throw new IllegalArgumentException("Invalid date. The date entered must be later than now.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd.");
        }
    }
}
