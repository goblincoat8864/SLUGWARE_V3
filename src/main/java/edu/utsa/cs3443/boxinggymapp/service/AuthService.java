package edu.utsa.cs3443.boxinggymapp.service;

import edu.utsa.cs3443.boxinggymapp.dto.AuthentificationTokenDTO;

public interface AuthService {
    AuthentificationTokenDTO login(String username, String userType);
    String getCurrentUser();
    String getUsernameFromJWT(String token);
    String getUserTypeFromToken(String token);
}
