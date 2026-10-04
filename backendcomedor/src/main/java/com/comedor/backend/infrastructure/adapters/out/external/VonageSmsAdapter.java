package com.comedor.backend.infrastructure.adapters.out.external;

import com.comedor.backend.application.ports.out.NotificationPort;
import com.vonage.client.VonageClient;
import com.vonage.client.messages.sms.SmsTextRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VonageSmsAdapter implements NotificationPort {

    @Value("${vonage.api.key:}")
    private String apiKey;

    @Value("${vonage.api.secret:}")
    private String apiSecret;

    @Value("${app.frontend.reset-password-url:https://www.comedorpopularluzisrael.site/reset-password}")
    private String resetPasswordBaseUrl;

    private VonageClient client;

    @PostConstruct
    public void init() {
        if (apiKey != null && !apiKey.isBlank() && apiSecret != null && !apiSecret.isBlank()) {
            this.client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();
            log.info("Vonage Messages API inicializado correctamente.");
        } else {
            log.warn("Credenciales de Vonage no configuradas. Los SMS saldrán solo por consola.");
        }
    }

    @Override
    public void sendPasswordResetSms(String phoneNumber, String resetToken) {
        String cleanPhone = phoneNumber.replaceAll("[^0-9]", "");
        if (!cleanPhone.startsWith("51")) {
            cleanPhone = "51" + cleanPhone;
        }

        String resetUrl = String.format("%s?token=%s", resetPasswordBaseUrl, resetToken);
        String body = String.format("Comedor Luz de Israel: Para restablecer tu contrasena, ingresa a: %s", resetUrl);

        if (client != null) {
            try {
                var response = client.getMessagesClient().sendMessage(
                        SmsTextRequest.builder()
                                .from("Vonage APIs")
                                .to(cleanPhone)
                                .text(body)
                                .build()
                );
                log.info("SMS enviado por Vonage a {}. UUID: {}", cleanPhone, response.getMessageUuid());
                return;
            } catch (Exception e) {
                log.error("Error al enviar SMS vía Vonage Messages API: {}", e.getMessage(), e);
            }
        }

        log.info("==== [SMS SIMULADO (CONSOLA)] ====");
        log.info("Destinatario: {}", cleanPhone);
        log.info("Enlace de recuperación: {}", resetUrl);
        log.info("==================================");
    }
}