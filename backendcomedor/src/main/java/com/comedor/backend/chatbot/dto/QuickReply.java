package com.comedor.backend.chatbot.dto;

/**
 * Botón de respuesta rápida. Al tocarlo, el frontend envía {@code message} como si la
 * persona lo hubiera escrito: así puede seguir la conversación sin teclear.
 */
public record QuickReply(String label, String message) {

    public static QuickReply of(String text) {
        return new QuickReply(text, text);
    }
}
