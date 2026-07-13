package com.comedor.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Pruebas e2e de las Ordenes de Entrada: Compra y Donacion.
 *
 * <p>Cubre 4 flujos: registrar compra, confirmar compra, registrar donacion y
 * confirmar donacion. Todas usan un helper comun que llena el formulario de
 * creacion (buscador de producto, cantidad y, en compras, precio).
 *
 * <p>Reglas de negocio respetadas: la fecha de la orden es la de hoy (no puede
 * ser anterior), y solo se puede "marcar como recibido" una orden que no tenga
 * fecha futura; como creamos con la fecha de hoy, se puede confirmar de una.
 */
@DisplayName("E2E - Ordenes de Entrada (Compra y Donacion)")
class OrdenEntradaE2ETest extends BaseE2ETest {

    /**
     * Registra una orden de entrada del tipo indicado ("COMPRA" o "DONACION")
     * y devuelve el numero (id) de la orden creada, leido de la pantalla de exito.
     */
    private String registrarOrden(String tipo) {
        boolean esCompra = "COMPRA".equals(tipo);

        login();
        abrirModuloDesdeMenu("tour-entrada"); // /purchase-order (lista)
        wait.until(ExpectedConditions.urlContains("/purchase-order"));

        // Boton "Nueva" -> formulario de creacion.
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Nueva')]"));
        wait.until(ExpectedConditions.urlContains("/create"));

        // Tipo de orden (Compra por defecto; para donacion lo cambiamos).
        if (!esCompra) {
            seleccionarOpcionPorValor(By.xpath("//select[option[@value='DONACION']]"), "DONACION");
        }

        // Agregamos una fila de producto.
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Agregar otro producto')]"));

        // Elegimos el primer producto del buscador (dejamos el filtro vacio).
        elegirEnBuscador(By.cssSelector("app-search-select input"), "");

        // Cantidad (primer input numerico).
        WebElement cantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='number']")));
        cantidad.sendKeys("3");

        // Precio unitario (solo en compras: segundo input numerico).
        if (esCompra) {
            driver.findElements(By.cssSelector("input[type='number']")).get(1).sendKeys("5");
        }

        // Creamos la orden (el boton dice "Crear Orden de Compra/Donación";
        // buscamos por el prefijo comun para evitar problemas con la tilde).
        clickConReintento(By.xpath(
                "//button[contains(normalize-space(.), 'Crear Orden de')]"));

        // Pantalla de exito: leemos el id de la orden ("... #123 fue registrada ...").
        WebElement parrafo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(., 'registrada')]")));
        Matcher m = Pattern.compile("#(\\d+)").matcher(parrafo.getText());
        assertTrue(m.find(), "La pantalla de exito deberia mostrar el numero de la orden");
        return m.group(1);
    }

    /** Abre una orden de la lista por su id y la marca como recibido. */
    private void confirmarOrden(String idOrden) {
        // Volvemos a la lista desde la pantalla de exito.
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Volver a la lista')]"));
        wait.until(ExpectedConditions.urlContains("/purchase-order"));

        // La lista esta paginada; filtramos por estado PENDIENTE para que la
        // orden recien creada quede en la primera pagina.
        seleccionarOpcionPorValor(By.id("statusBuy"), "PENDIENTE");
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Buscar')]"));
        try { Thread.sleep(1200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Abrimos el detalle de la orden recien creada (badge "Compra/Donación #id").
        abrirDetalleOrdenPorId(idOrden);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("purchaseDetailModal")));

        // Marcamos como recibido.
        clickConReintento(By.xpath(
                "//div[@id='purchaseDetailModal']//button[contains(normalize-space(.), 'Marcar como recibido')]"));

        // Exito: el boton "Marcar como recibido" desaparece (la orden queda RECIBIDO).
        boolean recibido = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
                "//div[@id='purchaseDetailModal']//button[contains(normalize-space(.), 'Marcar como recibido')]")));
        assertTrue(recibido, "Tras confirmar, la orden #" + idOrden + " deberia quedar como recibida");
    }

    @Test
    @DisplayName("Registrar orden de compra")
    void registrarOrdenCompra() {
        String id = registrarOrden("COMPRA");
        assertTrue(id != null && !id.isEmpty(), "Deberia crearse la orden de compra");
    }

    @Test
    @DisplayName("Confirmar orden de compra (marcar como recibido)")
    void confirmarOrdenCompra() {
        String id = registrarOrden("COMPRA");
        confirmarOrden(id);
    }

    @Test
    @DisplayName("Registrar orden de donacion")
    void registrarOrdenDonacion() {
        String id = registrarOrden("DONACION");
        assertTrue(id != null && !id.isEmpty(), "Deberia crearse la orden de donacion");
    }

    @Test
    @DisplayName("Confirmar orden de donacion (marcar como recibido)")
    void confirmarOrdenDonacion() {
        String id = registrarOrden("DONACION");
        confirmarOrden(id);
    }
}
