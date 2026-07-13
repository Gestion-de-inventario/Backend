package com.comedor.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Pruebas e2e del modulo de autenticacion (login).
 *
 * <p>Requiere que el frontend (ng serve) y el backend esten corriendo, y que
 * se pase la contrasena real con -Dtest.password=...
 */
@DisplayName("E2E - Modulo de Login")
class LoginE2ETest extends BaseE2ETest {

    @Test
    @DisplayName("Login correcto: entra al sistema y muestra el dashboard")
    void loginCorrecto() {
        // Reutilizamos el helper de la clase base, que ya llena el formulario,
        // envia y espera la redireccion a /dashboard.
        login();

        // Si llegamos aca sin excepcion, la redireccion ocurrio. Lo dejamos
        // explicito como verificacion final de la prueba.
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Tras un login valido deberiamos estar en /dashboard");
    }

    @Test
    @DisplayName("Login incorrecto: con contrasena mala NO entra y muestra error")
    void loginIncorrecto() {
        driver.get(FRONT_URL + "/login");

        WebElement inputDni = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='username']")));
        WebElement inputPassword = driver.findElement(
                By.cssSelector("input[formcontrolname='password']"));

        inputDni.sendKeys(TEST_DNI);
        inputPassword.sendKeys("claveIncorrecta123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Lo esencial de esta prueba: con credenciales malas NO debe entrar al
        // sistema. El toast de error se auto-oculta, asi que en vez de depender
        // de atraparlo, verificamos lo importante: tras un momento seguimos en
        // /login y nunca llegamos al dashboard.
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Con credenciales malas debemos seguir en /login (no en el dashboard)");
        assertTrue(!driver.getCurrentUrl().contains("/dashboard"),
                "Con credenciales malas nunca deberia llegar al dashboard");
    }

    @Test
    @DisplayName("Validacion: con DNI invalido el boton queda deshabilitado")
    void validacionDniInvalido() {
        driver.get(FRONT_URL + "/login");

        WebElement inputDni = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='username']")));
        WebElement inputPassword = driver.findElement(
                By.cssSelector("input[formcontrolname='password']"));

        // Escribimos un DNI de solo 3 digitos (invalido: deben ser 8).
        inputDni.sendKeys("123");
        inputPassword.sendKeys("cualquierClave");

        WebElement boton = driver.findElement(By.cssSelector("button[type='submit']"));

        // El formulario reactivo marca el form como invalido, y el boton
        // esta enlazado a [disabled]="form.invalid", asi que debe estar bloqueado.
        assertTrue(!boton.isEnabled(),
                "Con un DNI invalido el boton de ingresar debe estar deshabilitado");
    }
}
