package com.comedor.backend.application.ports.out;

public interface NotificationPort {
    void sendPasswordResetSms(String phoneNumber, String resetToken);
}