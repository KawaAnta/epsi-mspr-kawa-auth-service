package com.kawa.mspr.auth_service.service;

import com.kawa.mspr.auth_service.dto.ApiResponse;
import com.kawa.mspr.auth_service.dto.Auth.LoginResponseDTO;
import com.kawa.mspr.auth_service.dto.Auth.UserLoginDTO;
import com.kawa.mspr.auth_service.dto.Auth.UserRegisterDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    ApiResponse<LoginResponseDTO> login(UserLoginDTO userLoginDTO);

    ApiResponse<Void> register(UserRegisterDTO userRegisterDTO);

    ApiResponse<Void> verifyToken(String token);
}
