package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RequestPasswordResetUseCase;
import com.comedor.backend.application.ports.out.NotificationPort;
import com.comedor.backend.application.ports.out.PasswordResetTokenRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.PasswordResetToken;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.config.PeruTime;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class RequestPasswordResetService implements RequestPasswordResetUseCase {
    private final UserRepositoryPort usuarioRepository;
    private final PasswordResetTokenRepositoryPort tokenRepositoryPort;
    private final NotificationPort notificationPort;

    public RequestPasswordResetService(
            UserRepositoryPort usuarioRepository,
            PasswordResetTokenRepositoryPort tokenRepositoryPort,
            NotificationPort notificationPort
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepositoryPort = tokenRepositoryPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public void solicitarRecuperacionContrasena(String dni, String phone) {
        Optional<User> userOptional = usuarioRepository.findByUsername(dni);

        // 1. Si no existe el usuario, retorno silencioso (evita user enumeration)
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();
        // 2. Si no tiene persona asignada o el teléfono no coincide exactamente, retorno silencioso
        if (user.getPersona() == null ||
                user.getPhone() == null ||
                !user.getPhone().trim().equals(phone.trim())) {
            return;
        }

        // 3. Proceso legítimo si DNI y celular coinciden
        tokenRepositoryPort.invalidateTokensByUserId(user.getId());

        String tokenStr = UUID.randomUUID().toString();
        LocalDateTime expiration = PeruTime.now().plusMinutes(15);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenStr);
        token.setUserId(user.getId());
        token.setExpirationDate(expiration);
        token.setUsed(false);

        tokenRepositoryPort.save(token);

        notificationPort.sendPasswordResetSms(phone, tokenStr);
    }
}
