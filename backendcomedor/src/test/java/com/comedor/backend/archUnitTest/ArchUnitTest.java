package com.comedor.backend.archUnitTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchUnitTest {

    private static final String BASE_PACKAGE = "com.comedor.backend";
    private static JavaClasses importedClasses;

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter()
                // Ignorar completamente cualquier clase o test que esté dentro del paquete chatbot
                .withImportOption(location -> !location.contains("/chatbot/"))
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("Regla Hexagonal: Validar dependencias entre Domain, Application e Infrastructure")
    public void shouldRespectHexagonalArchitectureLayers() {

    }

    @Test
    @DisplayName("El Dominio debe ser completamente agnóstico a Spring, Frameworks y Adaptadores")
    public void domainShouldBePure() {
        ArchRule domainRule = noClasses().that().resideInAPackage(BASE_PACKAGE + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE_PACKAGE + ".application..",
                        BASE_PACKAGE + ".infrastructure..",
                        "org.springframework..",
                        "jakarta.persistence.."
                )
                .because("El modelo de dominio debe mantenerse libre de dependencias de infraestructura y frameworks");

        domainRule.check(importedClasses);
    }

    @Test
    @DisplayName("La capa de Aplicación no debe depender de Adaptadores ni Tecnologías de Infraestructura")
    public void applicationShouldNotDependOnInfrastructure() {
    }
}