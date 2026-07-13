package com.comedor.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Escenario e2e en cadena de la Orden de Produccion y la Orden de Salida.
 *
 * <p>Estos flujos tienen reglas de negocio con ESTADO, por eso se corren en
 * orden (no son pruebas independientes):
 * <ol>
 *   <li>Crear orden de produccion: solo una por dia y consume stock. Si falta
 *       stock, la prueba lo resuelve sola: lee los insumos faltantes, los
 *       compra y confirma, y reintenta.</li>
 *   <li>Editar la orden de produccion (posible mientras no tenga beneficiarios).</li>
 *   <li>Registrar la orden de salida sobre esa orden (asignar un beneficiario).</li>
 * </ol>
 *
 * <p>Nota: al permitirse una sola orden por dia, este escenario esta pensado
 * para correr una vez al dia sobre una base sin orden de produccion previa.
 */
@DisplayName("E2E - Orden de Produccion y Salida (escenario en cadena)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrdenProduccionE2ETest extends BaseE2ETest {

    @Test
    @Order(1)
    @DisplayName("Crear orden de produccion (comprando insumos si falta stock)")
    void crearOrdenProduccion() {
        login();

        String resultado = intentarCrearProduccion();

        if ("MISSING".equals(resultado)) {
            // El modal de stock esta abierto: leemos que insumos faltan y cuanto.
            List<String[]> faltantes = leerInsumosFaltantes();
            forzarCierreModales();
            // Compramos y confirmamos cada insumo (cantidad realista: lo que falta + margen).
            for (String[] insumo : faltantes) {
                comprarYConfirmar(insumo[0], insumo[1]);
            }
            // Reintentamos crear la orden de produccion.
            resultado = intentarCrearProduccion();
        }

        assertTrue("OK".equals(resultado),
                "No se pudo crear la orden de produccion. Resultado: " + resultado);
    }

    @Test
    @Order(2)
    @DisplayName("Editar orden de produccion")
    void editarOrdenProduccion() {
        login();
        abrirModuloDesdeMenu("tour-menu-report");
        wait.until(ExpectedConditions.urlContains("/menu-report"));

        // Boton "atras" del formulario -> lleva a la lista de ordenes.
        clickConReintento(By.cssSelector(".card-header button.rounded-circle"));
        wait.until(ExpectedConditions.urlContains("/menu-report/list"));

        // Abrimos el detalle de la primera orden (la mas reciente, sin beneficiarios).
        clickConReintento(By.cssSelector(".report-card .card-body"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("purchaseDetailModal")));

        // Entramos a modo edicion.
        clickConReintento(By.xpath(
                "//div[@id='purchaseDetailModal']//button[normalize-space(.)='Editar']"));

        // Ajustamos la cantidad preparada (mismo valor: no requiere stock extra).
        WebElement cantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#purchaseDetailModal input[placeholder='0']")));
        cantidad.clear();
        cantidad.sendKeys("1");

        // Guardamos.
        clickConReintento(By.xpath(
                "//div[@id='purchaseDetailModal']//button[contains(normalize-space(.), 'Guardar cambios')]"));

        // Exito: aparece el toast "Orden actualizada correctamente".
        assertTrue(esperarToastContiene("actualizada"),
                "Deberia confirmarse la edicion de la orden de produccion");
    }

    @Test
    @Order(3)
    @DisplayName("Registrar orden de salida (asignar beneficiario)")
    void registrarOrdenSalida() {
        login();
        abrirModuloDesdeMenu("tour-control-salida");
        wait.until(ExpectedConditions.urlContains("/beneficiaries-control"));

        // Abrimos la gestion de la primera orden de la lista.
        clickConReintento(By.cssSelector("div.card[style*='pointer']"));
        wait.until(ExpectedConditions.urlContains("/manage"));

        // Boton "+" para agregar un registro de salida.
        clickConReintento(By.cssSelector("button.btn-primary.rounded-circle"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("beneficiaryRecordModal")));

        // Elegimos el primer beneficiario del buscador.
        elegirEnBuscador(By.cssSelector("#beneficiaryRecordModal app-search-select input"), "");
        // Cantidad de menus.
        driver.findElement(By.cssSelector("#beneficiaryRecordModal input[inputmode='numeric']"))
                .sendKeys("1");
        // Precio del menu.
        driver.findElement(By.cssSelector("#beneficiaryRecordModal input[type='number']"))
                .sendKeys("1");
        // Metodo de pago.
        seleccionarOpcionPorValor(By.cssSelector("#beneficiaryRecordModal select"), "EFECTIVO");

        // Guardamos el registro de salida.
        clickConReintento(By.xpath(
                "//div[@id='beneficiaryRecordModal']//button[contains(normalize-space(.), 'Agregar Beneficiario')]"));

        // Exito: el modal se cierra (y sale el toast "Beneficiario agregado").
        boolean guardado = wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("beneficiaryRecordModal")));
        assertTrue(guardado, "Tras registrar la salida el modal deberia cerrarse");
    }

    // ----------------------------------------------------------------------
    // Helpers del escenario
    // ----------------------------------------------------------------------

    /**
     * Va al formulario de orden de produccion, lo llena (plantilla, cantidad,
     * cocinero, fecha de hoy) y lo envia. Devuelve:
     * "OK" si se creo, "MISSING" si falto stock (modal abierto), o
     * "OTHER:<mensaje>" para cualquier otro error mostrado en pantalla.
     */
    private String intentarCrearProduccion() {
        abrirModuloDesdeMenu("tour-menu-report");
        wait.until(ExpectedConditions.urlContains("/menu-report"));

        // Plantilla (primera opcion real).
        seleccionarOpcionPorIndice(By.xpath("//select[option[contains(., 'plantilla')]]"), 1);
        // Cantidad a preparar (minima para necesitar poco stock).
        WebElement cantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[placeholder='Ingrese la cantidad']")));
        cantidad.clear();
        cantidad.sendKeys("1");
        // Cocinero (primero del buscador).
        elegirEnBuscador(By.cssSelector("app-search-select input"), "");
        // La fecha ya viene con el dia de hoy.

        // Esperamos a que se oculte cualquier toast previo (por ejemplo el de
        // "Orden marcada como recibida" del paso de compra) para no confundirlo
        // con la respuesta de este envio.
        esperarSinToast();

        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Crear reporte')]"));

        // Esperamos el desenlace: exito, modal de stock, o toast de error.
        for (int i = 0; i < 40; i++) { // ~10s
            if (!driver.findElements(
                    By.xpath("//*[contains(normalize-space(.), 'Orden Creada')]")).isEmpty()) {
                return "OK";
            }
            if (!driver.findElements(By.cssSelector("#missingStockModal.show")).isEmpty()) {
                return "MISSING";
            }
            List<WebElement> toasts = driver.findElements(By.cssSelector("app-toast"));
            if (!toasts.isEmpty() && !toasts.get(0).getText().trim().isEmpty()) {
                return "OTHER:" + toasts.get(0).getText().trim();
            }
            try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return "OTHER:sin respuesta";
    }

    /**
     * Lee el modal de stock faltante y devuelve, por cada insumo, un par
     * {nombre, cantidadAComprar}. La cantidad a comprar es lo que falta (que el
     * modal muestra como "Faltan X unidad") redondeado hacia arriba mas un
     * pequeno margen, para que sea una compra realista.
     */
    private List<String[]> leerInsumosFaltantes() {
        List<String[]> insumos = new ArrayList<>();
        List<WebElement> nombres = driver.findElements(
                By.cssSelector("#missingStockModal span.fw-semibold"));
        List<WebElement> cantidades = driver.findElements(
                By.cssSelector("#missingStockModal span.badge"));
        for (int i = 0; i < nombres.size(); i++) {
            String nombre = nombres.get(i).getText().trim();
            // Extraemos el numero de "Faltan X unidad".
            double falta = 1;
            if (i < cantidades.size()) {
                Matcher m = Pattern.compile("([0-9]+(?:[.,][0-9]+)?)")
                        .matcher(cantidades.get(i).getText().replace(',', '.'));
                if (m.find()) {
                    falta = Double.parseDouble(m.group(1));
                }
            }
            int comprar = (int) Math.ceil(falta) + 2; // margen pequeno
            insumos.add(new String[] { nombre, String.valueOf(comprar) });
        }
        return insumos;
    }

    /**
     * Registra una orden de COMPRA de un producto con la cantidad indicada y la
     * confirma (marca como recibido) para que el stock quede disponible.
     */
    private void comprarYConfirmar(String nombreProducto, String cantidadComprar) {
        abrirModuloDesdeMenu("tour-entrada");
        wait.until(ExpectedConditions.urlContains("/purchase-order"));
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Nueva')]"));
        wait.until(ExpectedConditions.urlContains("/create"));

        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Agregar otro producto')]"));
        // Buscamos el producto por su nombre y lo elegimos.
        elegirEnBuscador(By.cssSelector("app-search-select input"), nombreProducto);
        // Cantidad realista: lo que falta mas un pequeno margen.
        WebElement cantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='number']")));
        cantidad.sendKeys(cantidadComprar);
        // Precio unitario (compra).
        driver.findElements(By.cssSelector("input[type='number']")).get(1).sendKeys("1");

        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Crear Orden de')]"));

        // Leemos el id de la orden creada y la confirmamos.
        WebElement parrafo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(., 'registrada')]")));
        Matcher m = Pattern.compile("#(\\d+)").matcher(parrafo.getText());
        assertTrue(m.find(), "Deberia mostrarse el numero de la orden de compra");
        String id = m.group(1);

        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Volver a la lista')]"));
        wait.until(ExpectedConditions.urlContains("/purchase-order"));

        // La lista esta paginada (5 por pagina) y las ordenes se ordenan por
        // fecha. Filtramos por estado PENDIENTE para que la orden recien creada
        // (que aun no esta recibida) quede en la primera pagina.
        seleccionarOpcionPorValor(By.id("statusBuy"), "PENDIENTE");
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Buscar')]"));
        try { Thread.sleep(1200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        abrirDetalleOrdenPorId(id);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("purchaseDetailModal")));
        clickConReintento(By.xpath(
                "//div[@id='purchaseDetailModal']//button[contains(normalize-space(.), 'Marcar como recibido')]"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(
                "//div[@id='purchaseDetailModal']//button[contains(normalize-space(.), 'Marcar como recibido')]")));
    }
}
