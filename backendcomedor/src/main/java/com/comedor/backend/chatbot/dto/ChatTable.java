package com.comedor.backend.chatbot.dto;

import java.util.List;

/** Datos tabulares para mostrar como tabla en el chat (stock, recojos, compras...). */
public record ChatTable(List<String> columns, List<List<String>> rows, String footer) {
}
