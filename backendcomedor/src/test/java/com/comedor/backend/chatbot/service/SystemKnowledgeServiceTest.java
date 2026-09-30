package com.comedor.backend.chatbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemKnowledgeServiceTest {

    @Mock
    private ChatbotAuthorizationService authorizationService;

    @Test
    void onlyListsModulesAvailableToCurrentUser() {
        when(authorizationService.currentAuthorities()).thenReturn(Set.of("PRODUCT_LIST_BY_STATUS"));
        var service = new SystemKnowledgeService(authorizationService);

        String reply = service.features().reply();

        assertTrue(reply.contains("Productos e inventario"));
        assertTrue(reply.contains("Perfil"));
        assertFalse(reply.contains("Roles y permisos"));
    }
}
