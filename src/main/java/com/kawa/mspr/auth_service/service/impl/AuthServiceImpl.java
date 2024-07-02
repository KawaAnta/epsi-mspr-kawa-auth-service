package com.kawa.mspr.auth_service.service.impl;

import com.kawa.mspr.auth_service.service.AuthService;
import com.kawa.mspr.auth_service.service.JwtService;
import com.kawa.mspr.auth_service.dto.ApiResponse;
import com.kawa.mspr.auth_service.dto.Auth.LoginResponseDTO;
import com.kawa.mspr.auth_service.dto.Auth.UserLoginDTO;
import com.kawa.mspr.auth_service.dto.Auth.UserRegisterDTO;
import com.kawa.mspr.auth_service.model.User;
import com.kawa.mspr.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implémentation de l'interface AuthService.
 */
@Component
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructeur pour AuthServiceImpl.
     *
     * @param userRepository Le référentiel d'utilisateurs
     */
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }


    /**
     * Connecte un utilisateur avec les identifiants fournis.
     *
     * @param userLoginDTO Les identifiants de connexion de l'utilisateur.
     * @return ApiResponse avec les données de la réponse de connexion.
     * @throws IllegalArgumentException si l'utilisateur n'est pas trouvé ou si les identifiants sont invalides.
     */
    @Override
    public ApiResponse<LoginResponseDTO> login(UserLoginDTO userLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if (!authentication.isAuthenticated()) {
            LOGGER.error("Invalid credentials for user with email: {}", userLoginDTO.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }

        // genere JWT token
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
        LOGGER.info("User with email: {} successfully logged in", userLoginDTO.getEmail());

        return new ApiResponse<>(true, "Login successful", new LoginResponseDTO(token));
    }

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param userRegisterDTO Les données d'inscription de l'utilisateur.
     * @return ApiResponse avec les données de la réponse d'inscription.
     * @throws IllegalArgumentException si l'adresse e-mail existe déjà.
     */
    @Override
    public ApiResponse<Void> register(UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO.getEmail() == null || userRegisterDTO.getPassword() == null
                || userRegisterDTO.getFirstName() == null || userRegisterDTO.getLastName() == null) {
            LOGGER.error("User registration data not provided");
            throw new IllegalArgumentException("Required fields not provided");
        }
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            LOGGER.error("Email already exists: {}", userRegisterDTO.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        User newUser = new User();
        newUser.setFirstName(userRegisterDTO.getFirstName());
        newUser.setLastName(userRegisterDTO.getLastName());
        newUser.setEmail(userRegisterDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        userRepository.save(newUser);

        LOGGER.info("User registered successfully with email: {}", userRegisterDTO.getEmail());

        return new ApiResponse<>(true, "User registered successfully", null);
    }

    /**
     * Vérifie un jeton JWT.
     *
     * @param token Le jeton JWT à vérifier.
     * @return ApiResponse avec les données de la réponse de vérification.
     */
    @Override
    public ApiResponse<Void> verifyToken(String token) {
        LOGGER.info("Verifying token: {}", token);
        jwtService.validateToken(token);
        LOGGER.info("Token verified: {}", token);
        return new ApiResponse<>(true, "Token verified successfully", null);
    }
}