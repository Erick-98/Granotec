package com.granotec.inventory_api.auth.service;

import com.granotec.inventory_api.auth.dto.AuthRequest;
import com.granotec.inventory_api.auth.dto.RegisterRequest;
import com.granotec.inventory_api.auth.dto.TokenResponse;
import com.granotec.inventory_api.auth.repository.PasswordResetToken;
import com.granotec.inventory_api.auth.repository.PasswordResetTokenRepository;
import com.granotec.inventory_api.auth.repository.Token;
import com.granotec.inventory_api.auth.repository.TokenRepository;
import com.granotec.inventory_api.common.exception.BadRequestException;
import com.granotec.inventory_api.common.exception.ResourceNotFoundException;
import com.granotec.inventory_api.role.Role;
import com.granotec.inventory_api.role.RoleRepository;
import com.granotec.inventory_api.user.User;
import com.granotec.inventory_api.user.UserRepository;
import com.granotec.inventory_api.email.EmailService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final  AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    private final ApplicationContext applicationContext;

    @Value("${application.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public TokenResponse register(final RegisterRequest request) {
        Role userRole = roleRepository.findById(request.roleId())
                .orElseThrow(()-> new ResourceNotFoundException("Rol no encontrado"));

        final User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(userRole)
                .build();

        final User savedUser = repository.save(user);
        final String jwtToken = jwtService.generateToken(savedUser);
        final String refreshToken = jwtService.generateRefreshToken(savedUser);

        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }


    @Transactional
    public TokenResponse authenticate(final AuthRequest request){
        final User user = repository.findByEmail(request.email())
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado"));

        if(user.getLockTime() != null && user.getLockTime().isAfter(LocalDateTime.now())){
            throw new BadRequestException("Cuenta bloqueada. Intenta nuevamente en 1 minuto");
        }

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

        }catch (BadCredentialsException e){
            // Invocar el método mediante el proxy del bean para que la anotación @Transactional(propagation = REQUIRES_NEW) funcione
            applicationContext.getBean(AuthService.class).handleFailedLogin(user.getId());
            throw new BadRequestException("Credenciales incorrectas");
        }

        resetFailedAttempts(user);
        final String accessToken = jwtService.generateToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailedLogin(Integer userId) {
        final User user = repository.findById(userId).orElseThrow(() -> new BadRequestException("Usuario no encontrado al registrar intento fallido"));
        Integer current = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
        user.setFailedAttempts(current + 1);
        if (user.getFailedAttempts() >= 3) {
            user.setLockTime(LocalDateTime.now().plusMinutes(1));
            user.setFailedAttempts(0);
        }
        repository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLockTime(null);
        repository.save(user);
    }


    private void saveUserToken(User user, String jwtToken) {
        final Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(final User user) {
        final List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setIsExpired(true);
                token.setIsRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

    public TokenResponse refreshToken(@NotNull final String authentication) {

        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String refreshToken = authentication.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) {
            return null;
        }

        final User user = this.repository.findByEmail(userEmail).orElseThrow();
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);
        if (!isTokenValid) {
            return null;
        }

        final String accessToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    // Nuevo: logout — revoca el token actual recibido en el header Authorization
    public void logout(@NotNull final String authentication) {
        if (authentication == null || !authentication.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String jwtToken = authentication.substring(7);
        tokenRepository.findByToken(jwtToken).ifPresent(token -> {
            token.setIsExpired(true);
            token.setIsRevoked(true);
            tokenRepository.save(token);
        });
    }

    // ----------------- Password reset functionality -----------------

    public void forgotPassword(final String email) {
        final User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // limpiar tokens antiguos
        passwordResetTokenRepository.deleteAllByUser_Id(user.getId());

        final String token = UUID.randomUUID().toString();
        final PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        final String resetLink = frontendUrl + "/auth/reset-password?token=" + token;
        final String subject = "Recuperación de contraseña - Granotec";
        final String body = "Hola " + user.getName() + ",\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña. Usa el siguiente enlace (válido 1 hora):\n" +
                resetLink + "\n\n" +
                "Si no solicitaste este cambio, puedes ignorar este correo.";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(final String token, final String newPassword) {
        final PasswordResetToken prt = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (prt.isUsed() || prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token inválido o expirado");
        }

        final User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user);

        prt.setUsed(true);
        passwordResetTokenRepository.save(prt);

        // Revocar tokens de acceso existentes
        revokeAllUserTokens(user);
    }

}
