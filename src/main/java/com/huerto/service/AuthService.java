package com.huerto.service;

import com.huerto.dto.AuthRequest;
import com.huerto.dto.AuthResponse;
import com.huerto.dto.ChangePasswordRequest;
import com.huerto.dto.RegisterRequest;
import com.huerto.dto.UpdateProfileRequest;
import com.huerto.dto.UserDTO;
import com.huerto.model.User;
import com.huerto.repository.UserRepository;
import com.huerto.security.JwtTokenProvider;
import com.huerto.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    private static final Set<String> ADMIN_EMAILS = Set.of(
        "huertohogar.info@gmail.com",
        "ha.durant@duocuc.cl"
    );

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRut(request.getRut());
        user.setAddress(mapAddress(request.getAddress()));
        user.setEnabled(true);

        // Asignamos los roles
        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        
        // Verificamos si el email es de administrador
        if (ADMIN_EMAILS.contains(request.getEmail())) {
            roles.add(User.Role.ROLE_ADMIN);
        }
        
        user.setRoles(roles);

        userRepository.save(user);

        // Autenticamos y generamos el token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roleNames = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .roles(roleNames)
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return AuthResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getEmail())
                .name(userDetails.getName())
                .roles(roles)
                .photoURL(user.getPhotoURL())
                .displayName(user.getDisplayName())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUserProfile() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setRoles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public UserDTO updateCurrentUserProfile(UpdateProfileRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificamos si el email ya está en uso por otro usuario
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDisplayName(request.getDisplayName());
        user.setPhotoURL(request.getPhotoURL());
        
        if (request.getAddress() != null) {
            user.setAddress(mapAddress(request.getAddress()));
        }
        
        User updatedUser = userRepository.save(user);
        
        UserDTO dto = modelMapper.map(updatedUser, UserDTO.class);
        dto.setRoles(updatedUser.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificamos la contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        // Actualizamos la contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private com.huerto.model.Address mapAddress(com.huerto.dto.AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        return new com.huerto.model.Address(
                addressDTO.getStreet(),
                addressDTO.getCity(),
                addressDTO.getRegion(),
                addressDTO.getCountry(),
                addressDTO.getPostalCode()
        );
    }
}

