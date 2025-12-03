package com.huerto.controller;

import com.huerto.dto.AuthRequest;
import com.huerto.dto.AuthResponse;
import com.huerto.dto.ChangePasswordRequest;
import com.huerto.dto.RegisterRequest;
import com.huerto.dto.UpdateProfileRequest;
import com.huerto.dto.UserDTO;
import com.huerto.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Obtener perfil del usuario actual", description = "Retorna el perfil del usuario autenticado")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        return ResponseEntity.ok(authService.getCurrentUserProfile());
    }

    @PutMapping("/me/profile")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Actualizar perfil del usuario actual", description = "Actualiza la información del perfil del usuario autenticado")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authService.updateCurrentUserProfile(request));
    }

    @PutMapping("/me/password")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Cambiar contraseña", description = "Cambia la contraseña del usuario autenticado")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }
}

