package com.huerto.service;

import com.huerto.dto.UserDTO;
import com.huerto.model.User;
import com.huerto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserService
 * 
 * Prueba 9: Test de obtener usuario por ID
 * Prueba 10: Test de actualizar usuario
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de UserService - Gestión de Usuarios")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        // Configurar usuario de prueba
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("123456789");
        mockUser.setEnabled(true);
        mockUser.setRoles(Set.of(User.Role.ROLE_USER));

        // Configurar DTO de usuario
        mockUserDTO = new UserDTO();
        mockUserDTO.setId(1L);
        mockUserDTO.setName("Test User");
        mockUserDTO.setEmail("test@example.com");
        mockUserDTO.setPhone("123456789");
        mockUserDTO.setEnabled(true);
        mockUserDTO.setRoles(Set.of("ROLE_USER"));
    }

    @Test
    @DisplayName("Prueba 9: Debe obtener un usuario por ID exitosamente")
    void testGetUserById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(mockUserDTO);

        // When
        UserDTO result = userService.getUserById(userId);

        // Then
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(1L, result.getId(), "El ID debe coincidir");
        assertEquals("Test User", result.getName(), "El nombre debe coincidir");
        assertEquals("test@example.com", result.getEmail(), "El email debe coincidir");
        assertEquals("123456789", result.getPhone(), "El teléfono debe coincidir");
        assertTrue(result.getEnabled(), "El usuario debe estar habilitado");
        assertTrue(result.getRoles().contains("ROLE_USER"), "Debe tener el rol USER");

        // Verificaciones
        verify(userRepository, times(1)).findById(userId);
        verify(modelMapper, times(1)).map(mockUser, UserDTO.class);
    }

    @Test
    @DisplayName("Prueba 10: Debe actualizar un usuario exitosamente")
    void testUpdateUser_Success() {
        // Given
        Long userId = 1L;
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated User");
        updateDTO.setPhone("987654321");
        updateDTO.setEnabled(true);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("test@example.com");
        updatedUser.setPhone("987654321");
        updatedUser.setEnabled(true);
        updatedUser.setRoles(Set.of(User.Role.ROLE_USER));

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setId(userId);
        updatedDTO.setName("Updated User");
        updatedDTO.setEmail("test@example.com");
        updatedDTO.setPhone("987654321");
        updatedDTO.setEnabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(updatedDTO);

        // When
        UserDTO result = userService.updateUser(userId, updateDTO);

        // Then
        assertNotNull(result, "El resultado no debe ser null");
        assertEquals("Updated User", result.getName(), "El nombre debe estar actualizado");
        assertEquals("987654321", result.getPhone(), "El teléfono debe estar actualizado");
        assertTrue(result.getEnabled(), "El usuario debe estar habilitado");

        // Verificaciones
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
        verify(modelMapper, times(1)).map(any(User.class), eq(UserDTO.class));
    }

    @Test
    @DisplayName("Prueba Extra: Debe lanzar excepción al buscar usuario inexistente")
    void testGetUserById_NotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado") ||
                   exception.getMessage().contains("User not found"),
                "El mensaje debe indicar que el usuario no existe");

        // Verificar que NO se intentó mapear
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @DisplayName("Prueba Extra: Debe lanzar excepción al actualizar usuario inexistente")
    void testUpdateUser_NotFound() {
        // Given
        Long userId = 999L;
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateDTO);
        });

        assertTrue(exception.getMessage().contains("Usuario no encontrado") ||
                   exception.getMessage().contains("User not found"),
                "El mensaje debe indicar que el usuario no existe");

        // Verificar que NO se intentó guardar
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Prueba Extra: Debe mantener el email original al actualizar usuario")
    void testUpdateUser_EmailNotChanged() {
        // Given
        Long userId = 1L;
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated User");
        updateDTO.setEmail("newemail@example.com"); // Intento de cambiar email
        updateDTO.setPhone("987654321");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated User");
        updatedUser.setEmail("test@example.com"); // Email original se mantiene
        updatedUser.setPhone("987654321");

        UserDTO resultDTO = new UserDTO();
        resultDTO.setId(userId);
        resultDTO.setName("Updated User");
        resultDTO.setEmail("test@example.com");
        resultDTO.setPhone("987654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(resultDTO);

        // When
        UserDTO result = userService.updateUser(userId, updateDTO);

        // Then
        assertEquals("test@example.com", result.getEmail(), 
                "El email debe mantenerse sin cambios");
        assertNotEquals("newemail@example.com", result.getEmail(),
                "El email NO debe cambiar al proporcionado");
    }
}

