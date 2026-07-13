package com.comedor.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Pruebas e2e de navegacion por los modulos principales del sistema.
 *
 * <p>Idea: hacemos login y luego navegamos a cada modulo COMO LO HARIA UN
 * USUARIO: abriendo el menu lateral (boton hamburguesa) y haciendo clic en el
 * enlace del modulo. Verificamos que la URL cambie a la del modulo. Es una
 * prueba de "humo" (smoke test): confirma que cada modulo abre bien.
 *
 * <p>Nota tecnica: no navegamos escribiendo la URL directamente porque esta app
 * usa un guard (appReadyGuard) que, ante una recarga completa de una ruta
 * profunda, rebota al dashboard. La navegacion interna (SPA) por el menu es la
 * forma correcta y estable de moverse.
 *
 * <p>Se usa @ParameterizedTest para no repetir la misma prueba varias veces:
 * JUnit la corre una vez por cada fila del @CsvSource ("texto del menu, URL").
 */
@DisplayName("E2E - Navegacion por modulos principales")
class NavegacionModulosE2ETest extends BaseE2ETest {

    @ParameterizedTest(name = "Modulo: {0}")
    @CsvSource({
            "Dashboard,  tour-dashboard,  /dashboard",
            "Usuarios,   tour-usuarios,   /management",
            "Inventario, tour-inventario, /inventory",
            "Roles,      tour-roles,      /roles",
            "Reportes,   tour-reportes,   /reports"
    })
    @DisplayName("Cada modulo abre desde el menu estando autenticado")
    void moduloAbreDesdeMenu(String nombreModulo, String idLink, String urlEsperada) {
        // 1. Iniciamos sesion (helper de la clase base). Quedamos en /dashboard.
        //    El helper tambien cierra el modal de contrasena si aparece.
        login();

        // 2. Abrimos el menu lateral con el boton hamburguesa.
        //    (clickConReintento cierra cualquier modal que se interponga)
        clickConReintento(By.cssSelector(".hamburger-button"));

        // 3. Clic en el enlace del modulo (cada uno tiene un id estable, ej "tour-usuarios").
        clickConReintento(By.id(idLink.trim()));

        // 4. Verificamos que la URL cambio a la del modulo.
        wait.until(ExpectedConditions.urlContains(urlEsperada.trim()));

        assertTrue(driver.getCurrentUrl().contains(urlEsperada.trim()),
                "Al hacer clic en '" + nombreModulo.trim() + "' deberiamos ir a " + urlEsperada.trim());
    }
}
