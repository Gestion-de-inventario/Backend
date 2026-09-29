package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.dto.ChatMessageRequest;
import com.comedor.backend.chatbot.dto.ConversationMessage;
import org.springframework.stereotype.Component;

import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChatPortionResolver {

    private static final int MAX_PORTIONS = 10_000;
    private static final Pattern WITH_UNIT = Pattern.compile(
            "\\b(\\d{1,5})\\s*(?:porciones?|raciones?|personas?|comensales?|menus?)\\b"
    );
    private static final Pattern AFTER_CONTEXT = Pattern.compile(
            "\\b(?:para|somos)\\s+(\\d{1,5})\\b"
    );
    private static final Pattern STANDALONE = Pattern.compile(
            "^\\s*(\\d{1,5})(?:\\s*(?:porciones?|raciones?|personas?|comensales?|menus?))?\\s*$"
    );

    public OptionalInt resolve(ChatMessageRequest request) {
        OptionalInt current = extract(request.message(), true);
        if (current.isPresent()) {
            return current;
        }

        if (request.portions() != null) {
            return valid(request.portions());
        }

        for (int index = request.safeHistory().size() - 1; index >= 0; index--) {
            ConversationMessage message = request.safeHistory().get(index);
            if (message != null && "user".equals(message.role())) {
                OptionalInt previous = extract(message.content(), false);
                if (previous.isPresent()) {
                    return previous;
                }
            }
        }
        return OptionalInt.empty();
    }

    private OptionalInt extract(String message, boolean allowStandalone) {
        String normalized = ChatIntentDetector.normalize(message);
        OptionalInt withUnit = firstValid(WITH_UNIT.matcher(normalized));
        if (withUnit.isPresent()) {
            return withUnit;
        }

        OptionalInt afterContext = firstValid(AFTER_CONTEXT.matcher(normalized));
        if (afterContext.isPresent()) {
            return afterContext;
        }

        return allowStandalone ? firstValid(STANDALONE.matcher(normalized)) : OptionalInt.empty();
    }

    private OptionalInt firstValid(Matcher matcher) {
        return matcher.find() ? valid(Integer.parseInt(matcher.group(1))) : OptionalInt.empty();
    }

    private OptionalInt valid(int value) {
        return value >= 1 && value <= MAX_PORTIONS ? OptionalInt.of(value) : OptionalInt.empty();
    }
}
