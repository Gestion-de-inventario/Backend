package com.comedor.backend.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Clase base para todas las pruebas e2e con Selenium.
 *
 * <p>Se encarga de lo repetitivo: abrir Chrome antes de cada prueba, cerrarlo
 * al terminar, y ofrecer utilidades comunes como {@link #login()}. Cada prueba
 * concreta (LoginE2ETest, NavegacionModulosE2ETest, etc.) hereda de aqui y solo
 * escribe lo suyo.
 *
 * <p>Los datos (URL del frontend, DNI y contrasena de prueba) se pueden cambiar
 * desde la linea de comandos sin tocar el codigo, por ejemplo:
 * <pre>
 *   mvn test -Dtest.password=miClave -Dfront.url=http://localhost:4200
 * </pre>
 * Si no se pasan, se usan los valores por defecto de abajo.
 */
public abstract class BaseE2ETest {

    /** URL donde corre el frontend Angular (ng serve). */
    protected static final String FRONT_URL =
            System.getProperty("front.url", "http://localhost:4200");

    /** DNI del usuario admin sembrado en la base de datos. */
    protected static final String TEST_DNI =
            System.getProperty("test.dni", "72111167");

    /** Contrasena del usuario admin. Se debe pasar con -Dtest.password=... */
    protected static final String TEST_PASSWORD =
            System.getProperty("test.password", "12345678");

    /** El navegador que Selenium controla. */
    protected WebDriver driver;

    /** Utilidad para "esperar hasta que" algo ocurra (evita fallos por timing). */
    protected WebDriverWait wait;

    @BeforeEach
    void abrirNavegador() {
        ChromeOptions opciones = new ChromeOptions();
        // Ventana grande para que se vea todo el layout (sidebar, botones, etc.).
        opciones.addArguments("--window-size=1400,900");
        opciones.addArguments("--remote-allow-origins=*");
        // Si quieres que Chrome corra SIN abrir ventana (util en servidores/CI),
        // ejecuta con -Dheadless=true
        if (Boolean.getBoolean("headless")) {
            opciones.addArguments("--headless=new");
        }

        driver = new ChromeDriver(opciones);
        // Espera maxima de 10s en las condiciones; si algo tarda mas, falla.
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void cerrarNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Hace login con el usuario de prueba y espera a llegar al dashboard.
     * Lo usan las pruebas que necesitan estar "ya dentro" del sistema.
     */
    protected void login() {
        driver.get(FRONT_URL + "/login");

        // Esperamos a que el formulario este cargado (Angular es asincrono).
        WebElement inputDni = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[formcontrolname='username']")));
        WebElement inputPassword = driver.findElement(
                By.cssSelector("input[formcontrolname='password']"));

        inputDni.clear();
        inputDni.sendKeys(TEST_DNI);
        inputPassword.clear();
        inputPassword.sendKeys(TEST_PASSWORD);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // El login exitoso redirige a /dashboard. Esperamos ese cambio de URL.
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // En el primer login aparecen dos modales, uno tras otro:
        //   1) "Cambia tu contrasena" (optionalPasswordModal)
        //   2) el tour de ayuda al usuario (tourModal)
        // Le damos un instante a que aparezcan y luego los cerramos a la fuerza.
        pausa(1200);
        forzarCierreModales();
    }

    /**
     * Cierra cualquier modal abierto manipulando el DOM directamente.
     *
     * <p>Por que asi y no clic en la "X": los modales del primer login aparecen
     * con animacion, y si uno intenta cerrarlos mientras animan, Bootstrap
     * ignora el cierre. Quitando la clase "show" y borrando el "backdrop"
     * (fondo oscuro) el modal desaparece al instante, sin depender de tiempos.
     * Ademas marcamos el tour como "ya visto" para que su temporizador no lo
     * vuelva a abrir.
     */
    protected void forzarCierreModales() {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                // Marcar el tour como completado para que no reaparezca.
                "Object.keys(localStorage).forEach(k => {"
                + "  if (k.startsWith('tour_completado')) {} });"
                + "for (let i = 0; i < 5; i++) { localStorage.setItem('tour_completado_' + i, 'true'); }"
                // Ocultar todos los modales visibles.
                + "document.querySelectorAll('.modal.show').forEach(m => {"
                + "  m.classList.remove('show'); m.style.display='none';"
                + "  m.setAttribute('aria-hidden','true'); });"
                // Borrar los backdrops y restaurar el <body>.
                + "document.querySelectorAll('.modal-backdrop').forEach(e => e.remove());"
                + "document.body.classList.remove('modal-open');"
                + "document.body.style.overflow=''; document.body.style.paddingRight='';");
    }

    /**
     * Hace clic en un elemento y, si un modal se interpone (por ejemplo el tour
     * que reaparece), lo cierra a la fuerza y reintenta. Asi la prueba no falla
     * por modales que salen en momentos inesperados.
     */
    protected void clickConReintento(By selector) {
        for (int intento = 0; intento < 4; intento++) {
            try {
                WebElement elemento = wait.until(
                        ExpectedConditions.elementToBeClickable(selector));
                // Lo desplazamos al centro para que no quede tapado por barras
                // (paginacion, cabeceras) segun la resolucion de pantalla.
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", elemento);
                pausa(150);
                elemento.click();
                return; // clic exitoso
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                // Algo tapo el clic (un modal, una barra fija, etc.). Cerramos
                // modales y, si el elemento sigue tapado, hacemos clic via JS.
                forzarCierreModales();
                pausa(300);
                try {
                    WebElement el = driver.findElement(selector);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", el);
                    return;
                } catch (org.openqa.selenium.WebDriverException ignore) {
                    // Reintentamos en la siguiente vuelta del bucle.
                }
            }
        }
        // Ultimo intento: si vuelve a fallar, dejamos que la excepcion suba.
        wait.until(ExpectedConditions.elementToBeClickable(selector)).click();
    }

    /**
     * Abre un modulo del sistema desde el menu lateral: hace login (si hace
     * falta ya se hizo antes), abre el menu con la hamburguesa y clickea el
     * enlace cuyo id se pasa (por ejemplo "tour-inventario").
     */
    protected void abrirModuloDesdeMenu(String idLink) {
        clickConReintento(By.cssSelector(".hamburger-button"));
        clickConReintento(By.id(idLink));
    }

    /**
     * Selecciona una opcion de un &lt;select&gt; por su indice (0 suele ser el
     * "Seleccione..." deshabilitado, asi que normalmente se usa 1 para la
     * primera opcion real). Sirve para los combos que cargan datos del backend.
     */
    protected void seleccionarOpcionPorIndice(By selectorSelect, int indice) {
        WebElement elementoSelect = wait.until(
                ExpectedConditions.visibilityOfElementLocated(selectorSelect));
        new org.openqa.selenium.support.ui.Select(elementoSelect).selectByIndex(indice);
    }

    /** Selecciona una opcion de un &lt;select&gt; por su atributo value. */
    protected void seleccionarOpcionPorValor(By selectorSelect, String valor) {
        WebElement elementoSelect = wait.until(
                ExpectedConditions.visibilityOfElementLocated(selectorSelect));
        new org.openqa.selenium.support.ui.Select(elementoSelect).selectByValue(valor);
    }

    /**
     * Usa el buscador custom (app-search-select): enfoca su input, escribe un
     * texto para filtrar y hace clic en el primer resultado de la lista.
     *
     * @param selectorInput selector del input del buscador
     * @param texto         texto a escribir para filtrar (puede ser "" para ver todo)
     */
    protected void elegirEnBuscador(By selectorInput, String texto) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(selectorInput));
        input.click();
        if (texto != null && !texto.isEmpty()) {
            input.sendKeys(texto);
        }
        // Al enfocar/escribir se despliega una lista de botones; clic en el primero.
        WebElement primerResultado = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".list-group-item-action")));
        primerResultado.click();
    }

    /**
     * Abre el detalle de una orden de la lista buscando el badge cuyo texto
     * TERMINA en "#id" (coincidencia exacta, para no confundir #9 con #90).
     */
    protected void abrirDetalleOrdenPorId(String id) {
        String aguja = "#" + id;
        // XPath 1.0 no tiene ends-with(); lo emulamos con substring().
        String xpath = "//span[substring(normalize-space(.), string-length(normalize-space(.)) - "
                + (aguja.length() - 1) + ") = '" + aguja + "']";
        clickConReintento(By.xpath(xpath));
    }

    /**
     * Espera a que aparezca un toast (mensaje flotante) cuyo texto contenga el
     * fragmento indicado. Devuelve true si aparece dentro del tiempo dado.
     * Util para verificar mensajes de exito que se auto-ocultan.
     */
    protected boolean esperarToastContiene(String fragmento) {
        for (int i = 0; i < 40; i++) { // ~10s en pasos de 250ms
            for (WebElement toast : driver.findElements(By.cssSelector("app-toast"))) {
                if (toast.getText().toLowerCase().contains(fragmento.toLowerCase())) {
                    return true;
                }
            }
            try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return false;
    }

    /**
     * Espera (hasta ~8s) a que no quede ningun toast visible con texto, para no
     * confundir un mensaje de un paso anterior con la respuesta del siguiente.
     */
    protected void esperarSinToast() {
        for (int i = 0; i < 32; i++) { // ~8s
            boolean hayToast = false;
            for (WebElement toast : driver.findElements(By.cssSelector("app-toast"))) {
                if (!toast.getText().trim().isEmpty()) {
                    hayToast = true;
                    break;
                }
            }
            if (!hayToast) {
                return;
            }
            try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    /** Genera un texto unico y reconocible para datos de prueba, ej "E2E-193045". */
    protected String textoUnico(String prefijo) {
        return prefijo + "-" + (System.currentTimeMillis() % 1000000);
    }

    /**
     * Igual que {@link #textoUnico(String)} pero SOLO con letras y espacios, para
     * campos que no admiten numeros ni simbolos (como el nombre de producto).
     * Convierte los digitos del tiempo actual en letras (0=A, 1=B, ...).
     */
    protected String textoUnicoLetras(String prefijo) {
        String digitos = String.valueOf(System.currentTimeMillis() % 1000000);
        StringBuilder letras = new StringBuilder();
        for (char d : digitos.toCharArray()) {
            letras.append((char) ('A' + (d - '0')));
        }
        return prefijo + " " + letras;
    }

    /** Pausa breve para dejar terminar las animaciones de Bootstrap. */
    private void pausa(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
