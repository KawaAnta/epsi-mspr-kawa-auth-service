package com.kawa.mspr.auth_service.service.impl;

import com.kawa.mspr.auth_service.dto.ApiResponse;
import com.kawa.mspr.auth_service.dto.User.UserPasswordUpdateDTO;
import com.kawa.mspr.auth_service.dto.User.UserResponseDTO;
import com.kawa.mspr.auth_service.dto.User.UserUpdateRequestDTO;
import com.kawa.mspr.auth_service.exception.UserNotFoundException;
import com.kawa.mspr.auth_service.mapper.UserMapper;
import com.kawa.mspr.auth_service.model.User;
import com.kawa.mspr.auth_service.repository.UserRepository;
import com.kawa.mspr.auth_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface.
 */
@Component
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return ApiResponse contenant la liste de tous les utilisateurs.
     */
    @Override
    public ApiResponse<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userRepository.findAll().stream()
                .map(userMapper::mapToUserResponseDTO)
                .collect(Collectors.toList());
        LOGGER.info("Users retrieved successfully");
        return new ApiResponse<>(true, "Users retrieved successfully", users);
    }

    /**
     * Met à jour un utilisateur par ID.
     *
     * @param id                   L'ID de l'utilisateur à mettre à jour.
     * @param userUpdateRequestDto Les données de la requête de mise à jour de l'utilisateur.
     * @return ApiResponse contenant l'utilisateur mis à jour.
     */
    @Override
    public ApiResponse<UserResponseDTO> updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFirstName(userUpdateRequestDto.getFirstName());
        user.setLastName(userUpdateRequestDto.getLastName());
        userRepository.save(user);

        UserResponseDTO updatedUser = userMapper.mapToUserResponseDTO(user);
        LOGGER.info("User updated successfully: {}", updatedUser);
        return new ApiResponse<>(true, "User updated successfully", updatedUser);
    }

    /**
     * Met à jour le mot de passe d'un utilisateur par ID.
     *
     * @param id                    L'ID de l'utilisateur à mettre à jour.
     * @param userPasswordUpdateDto Les données de la requête de mise à jour du mot de passe de l'utilisateur.
     * @return ApiResponse sans contenu.
     */
    @Override
    public ApiResponse<Void> updateUserPassword(Long id, UserPasswordUpdateDTO userPasswordUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(userPasswordUpdateDto.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse<>(true, "User password updated successfully", null);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ApiResponse containing the user.
     */
    @Override
    public ApiResponse<UserResponseDTO> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO userResponse = userMapper.mapToUserResponseDTO(user);
        return new ApiResponse<>(true, "User retrieved successfully", userResponse);
    }

    /**
     * Retrieves a user by email.
     *
     * @param email The email of the user to retrieve.
     * @return ApiResponse containing the user.
     */
    @Override
    public ApiResponse<UserResponseDTO> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO userResponse = userMapper.mapToUserResponseDTO(user);
        return new ApiResponse<>(true, "User retrieved successfully", userResponse);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The ID of the user to delete.
     * @return ApiResponse with no content.
     */
    @Override
    public ApiResponse<Void> deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        return new ApiResponse<>(true, "User deleted successfully", null);
    }
}
