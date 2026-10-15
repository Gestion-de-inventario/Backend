package com.comedor.backend.chatbot.dialog;

import com.comedor.backend.chatbot.nlu.Intent;

import java.util.Set;

/** Responde uno o más temas consultando la base de datos o la guía del sistema. */
public interface TopicHandler {

    Set<Intent> intents();

    Reply handle(Turn turn);
}
