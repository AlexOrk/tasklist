package orkhoian.aleksei.tasklist.service;

import orkhoian.aleksei.tasklist.dto.auth.JwtRequest;
import orkhoian.aleksei.tasklist.dto.auth.JwtResponse;

public interface AuthService {

    JwtResponse login(JwtRequest loginRequest);

    JwtResponse refresh(String refreshToken);
}
