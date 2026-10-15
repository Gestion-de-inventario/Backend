package com.comedor.backend.chatbot.handler;

import com.comedor.backend.chatbot.dialog.Reply;

/** Respuestas comunes a varios temas. */
final class Replies {

    private Replies() {
    }

    static Reply denied(String action) {
        return Reply.text("Tu usuario no tiene permiso para " + action + ".")
                .line("Si lo necesitas, pide acceso a la persona que administra los roles del sistema.")
                .option("¿Qué puedes hacer?");
    }
}
