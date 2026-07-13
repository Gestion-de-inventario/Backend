package com.comedor.backend.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Pruebas e2e del modulo de Productos (inventario): crear y editar.
 *
 * <p>Los datos de prueba usan el prefijo "PRUEBA EEE" para ubicarlos facilmente
 * y poder borrarlos despues. El nombre solo admite letras y espacios, por eso
 * el sufijo unico se genera con letras.
 */
@DisplayName("E2E - Modulo de Productos")
class ProductoE2ETest extends BaseE2ETest {

    /** Abre el modulo Inventario -> Productos (ya logueado). */
    private void irAProductos() {
        login();
        abrirModuloDesdeMenu("tour-inventario"); // lleva a /inventory/products
        wait.until(ExpectedConditions.urlContains("/inventory"));
    }

    @Test
    @DisplayName("Crear producto: se registra y el modal se cierra con exito")
    void crearProducto() {
        irAProductos();

        String nombre = textoUnicoLetras("PRUEBA EEE");

        // Abrimos el modal "Nuevo Producto".
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Crear Producto')]"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createProductModal")));

        // Llenamos el formulario.
        WebElement inputNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#createProductModal input[formcontrolname='name']")));
        inputNombre.sendKeys(nombre);

        // Categoria: primera opcion real (indice 1, el 0 es "Seleccione").
        seleccionarOpcionPorIndice(
                By.cssSelector("#createProductModal select[formcontrolname='categoryId']"), 1);
        // Unidad de medida: valor fijo.
        seleccionarOpcionPorValor(
                By.cssSelector("#createProductModal select[formcontrolname='unit']"), "UNIDADES");
        // Punto de reorden.
        driver.findElement(By.cssSelector("#createProductModal input[formcontrolname='reorderPoint']"))
                .sendKeys("5");

        // Confirmamos la creacion.
        clickConReintento(By.xpath("//button[contains(normalize-space(.), 'Confirmar y Crear')]"));

        // Exito: el modal se cierra solo cuando la creacion fue correcta
        // (si falla, el modal permanece abierto mostrando el error).
        boolean creado = wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("createProductModal")));
        assertTrue(creado, "Tras crear el producto el modal deberia cerrarse");
    }

    @Test
    @DisplayName("Editar producto: abre un producto, cambia el punto de reorden y guarda")
    void editarProducto() {
        irAProductos();

        // Esperamos a que cargue la lista y abrimos el primer producto.
        WebElement primerProducto = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".product-card")));
        primerProducto.click();

        // Se abre el modal de detalle. Entramos a modo edicion.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productDetailModal")));
        clickConReintento(By.xpath(
                "//div[@id='productDetailModal']//button[contains(normalize-space(.), 'Editar Datos')]"));

        // Regla de negocio: si el producto tiene transacciones vinculadas, solo
        // se puede modificar el PUNTO DE REORDEN (no el nombre/categoria/unidad).
        // Por eso editamos ese campo, que siempre es valido.
        WebElement inputReorden = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#productDetailModal input[formcontrolname='reorderPoint']")));
        inputReorden.clear();
        inputReorden.sendKeys("7");

        // Guardamos los cambios.
        clickConReintento(By.xpath(
                "//div[@id='productDetailModal']//button[contains(normalize-space(.), 'Guardar Cambios')]"));

        // Exito: al guardar, el modal vuelve a "modo lectura" y reaparece el
        // boton "Editar Datos" (si fallara, seguiria en modo edicion).
        WebElement botonEditarDeNuevo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='productDetailModal']//button[contains(normalize-space(.), 'Editar Datos')]")));
        assertTrue(botonEditarDeNuevo.isDisplayed(),
                "Tras guardar deberia volver al modo lectura del producto");
    }
}
