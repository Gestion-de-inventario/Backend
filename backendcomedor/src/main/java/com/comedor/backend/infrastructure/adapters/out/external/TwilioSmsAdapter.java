package com.comedor.backend.infrastructure.adapters.out.external;

import com.comedor.backend.application.ports.out.NotificationPort;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class TwilioSmsAdapter{

    @Value("${app.frontend.reset-password-url:http://localhost:4200/reset-password}")
    private String resetPasswordBaseUrl;

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String fromPhoneNumber;

    private boolean isConfigured = false;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank() &&
                authToken != null && !authToken.isBlank() &&
                fromPhoneNumber != null && !fromPhoneNumber.isBlank()) {
            Twilio.init(accountSid, authToken);
            isConfigured = true;
            System.out.println("Twilio SMS Adapter inicializado correctamente.");
        } else {
            System.out.println("Twilio SMS no configurado. Las notificaciones se emitirán únicamente por log.");
        }
    }

    public void sendPasswordResetSms(String phoneNumber, String resetToken) {
        String formattedPhone = phoneNumber.startsWith("+") ? phoneNumber : "+51" + phoneNumber;

        // La construcción del link pertenece a la capa de infraestructura
        String resetUrl = String.format("%s?token=%s", resetPasswordBaseUrl, resetToken);
        String messageBody = String.format("Comedor Luz de Israel: Para restablecer tu clave, ingresa al enlace (valido 15 min): %s", resetUrl);

        if (isConfigured) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(formattedPhone),
                        new PhoneNumber(fromPhoneNumber),
                        messageBody
                ).create();

            } catch (Exception e) {

            }
        } else {

        }
    }
}