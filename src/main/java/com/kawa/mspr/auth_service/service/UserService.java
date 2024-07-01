package com.kawa.mspr.auth_service.service;

import com.kawa.mspr.auth_service.dto.ApiResponse;
import com.kawa.mspr.auth_service.dto.User.UserPasswordUpdateDTO;
import com.kawa.mspr.auth_service.dto.User.UserResponseDTO;
import com.kawa.mspr.auth_service.dto.User.UserUpdateRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    ApiResponse<List<UserResponseDTO>> getAllUsers();

    ApiResponse<UserResponseDTO> getUserById(Long id);

    ApiResponse<UserResponseDTO> getUserByEmail(String email);

    ApiResponse<UserResponseDTO> updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDto);

    ApiResponse<Void> updateUserPassword(Long id, UserPasswordUpdateDTO userPasswordUpdateDto);

    ApiResponse<Void> deleteUser(Long id);
}
