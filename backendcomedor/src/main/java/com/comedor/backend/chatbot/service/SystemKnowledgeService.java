package com.comedor.backend.chatbot.service;

import com.comedor.backend.chatbot.model.ChatbotResolution;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemKnowledgeService {

    private final ChatbotAuthorizationService authorizationService;

    private static final List<ModuleGuide> GUIDES = List.of(
            new ModuleGuide(
                    "Dashboard",
                    List.of("dashboard", "indicadores", "balance", "rotacion"),
                    Set.of("DASHBOARD_VIEW"),
                    "Consulta ingresos, egresos, balance neto y productos con mayor movimiento.",
                    List.of("Abre Dashboard desde el menú lateral.",
                            "Revisa las tarjetas de indicadores y el gráfico del periodo disponible.")
            ),
            new ModuleGuide(
                    "Registro de menú",
                    List.of("registrar menu", "registrar un menu", "crear menu", "crear un menu", "reporte de menu", "menu del dia"),
                    Set.of("MENU_REPORT_CREATE_REPORT", "MENU_REPORT_LIST_ALL"),
                    "Crea y consulta el reporte del menú preparado para una fecha.",
                    List.of("Abre Registro de menú.",
                            "Selecciona la fecha, el plato y la cantidad preparada.",
                            "Completa los datos solicitados y confirma el registro.")
            ),
            new ModuleGuide(
                    "Recojo de menú",
                    List.of("recojo", "registrar recojo", "entrega de menu", "beneficiario recojo"),
                    Set.of("MENU_REPORT_LIST_ALL", "MENU_REPORT_ADD_BENEFICIARY", "MENU_REPORT_EDIT_BENEFICIARY"),
                    "Registra qué beneficiarios recibieron el menú y la cantidad entregada.",
                    List.of("Abre Registrar recojo en el menú lateral.",
                            "Selecciona el reporte de menú correspondiente.",
                            "Agrega o edita al beneficiario, marca el recojo y guarda los cambios.")
            ),
            new ModuleGuide(
                    "Productos e inventario",
                    List.of("producto", "productos", "inventario", "stock", "editar producto", "crear producto"),
                    Set.of("PRODUCT_LIST_BY_STATUS", "PRODUCT_CREATE", "PRODUCT_EDIT", "PRODUCT_ALERT_STOCK_VIEW"),
                    "Consulta productos, existencias, unidades y alertas de stock bajo.",
                    List.of("Abre Inventario y luego Productos.",
                            "Usa los filtros para localizar el producto.",
                            "Según tus permisos, crea, edita o cambia su estado.")
            ),
            new ModuleGuide(
                    "Platos",
                    List.of("plato", "platos", "receta", "insumos de plato"),
                    Set.of("DISH_MENU_LIST_ALL", "DISH_MENU_CREATE", "DISH_MENU_EDIT"),
                    "Administra los platos y los insumos requeridos por porción.",
                    List.of("Abre Inventario y luego Platos.",
                            "Crea o selecciona un plato.",
                            "Registra sus insumos y la cantidad necesaria por porción, y guarda.")
            ),
            new ModuleGuide(
                    "Categorías y etiquetas",
                    List.of("categoria", "categorias", "etiqueta", "etiquetas"),
                    Set.of("CATEGORY_LIST_BY_STATUS", "TAG_LIST_BY_STATUS", "CATEGORY_CREATE", "TAG_CREATE"),
                    "Organiza los productos mediante categorías y etiquetas.",
                    List.of("Abre Inventario y selecciona Categorías o Etiquetas.",
                            "Crea el registro o cambia su estado según la acción requerida.")
            ),
            new ModuleGuide(
                    "Ingreso de insumos",
                    List.of("ingreso de insumos", "orden de entrada", "compra", "donacion", "ingresar productos"),
                    Set.of("CREATE_ORDER_IN", "ORDER_IN_LIST_ALL", "PURCHASE_LIST_ALL", "DONATION_LIST_ALL"),
                    "Registra y consulta ingresos de productos provenientes de compras o donaciones.",
                    List.of("Abre Ingreso de insumos.",
                            "Selecciona Registrar y el tipo de ingreso.",
                            "Agrega los productos y cantidades, revisa el detalle y confirma.")
            ),
            new ModuleGuide(
                    "Beneficiarios",
                    List.of("beneficiario", "beneficiarios", "dni", "reniec"),
                    Set.of("BENEFICIARY_LIST_BY_STATUS", "BENEFICIARY_CREATE", "BENEFICIARY_CREATE_BY_DNI"),
                    "Consulta, registra y actualiza beneficiarios; el alta puede apoyarse en RENIEC.",
                    List.of("Abre Usuarios y entra en Beneficiarios.",
                            "Elige registro manual o mediante DNI, según tus permisos.",
                            "Completa o verifica los datos, asigna el tipo de beneficiario y guarda.")
            ),
            new ModuleGuide(
                    "Tipos de beneficiario",
                    List.of("tipo de beneficiario", "tipos de beneficiario"),
                    Set.of("BENEFICIARY_TYPE_LIST_BY_STATUS", "BENEFICIARY_TYPE_CREATE"),
                    "Administra las clasificaciones y costos aplicables a beneficiarios.",
                    List.of("Abre Usuarios y entra en Tipos de beneficiario.",
                            "Crea o edita el tipo, completa el costo del menú y guarda.")
            ),
            new ModuleGuide(
                    "Usuarios",
                    List.of("usuario", "usuarios", "crear usuario", "editar usuario"),
                    Set.of("USER_LIST_ALL", "USER_LIST_ACTIVE", "USER_CREATE", "USER_EDIT"),
                    "Administra las cuentas que acceden a la aplicación.",
                    List.of("Abre Usuarios desde el menú lateral.",
                            "Entra en la sección de usuarios.",
                            "Crea, edita, activa o desactiva la cuenta según tus permisos.")
            ),
            new ModuleGuide(
                    "Roles y permisos",
                    List.of("rol", "roles", "permiso", "permisos", "asignar permiso"),
                    Set.of("ROLE_LIST_BY_STATUS", "ROLE_CREATE", "ROLE_EDIT", "ROLE_ASSIGN_PERMISSIONS"),
                    "Define roles y controla las acciones disponibles para cada usuario.",
                    List.of("Abre Roles.",
                            "Crea o selecciona un rol.",
                            "Marca los permisos necesarios, revisa la selección y guarda.")
            ),
            new ModuleGuide(
                    "Reportes y auditoría",
                    List.of("reporte", "reportes", "auditoria", "transaccion", "exportar"),
                    Set.of("TRANSACTION_LIST_ALL", "AUDIT_LIST_ALL", "MENU_REPORT_GET_BY_DATE", "MENU_REPORT_EXPORT"),
                    "Consulta transacciones, auditorías, resúmenes y exportaciones.",
                    List.of("Abre Auditoría y Reportes.",
                            "Selecciona la sección requerida.",
                            "Aplica los filtros de fecha o tipo y exporta si cuentas con permiso.")
            ),
            new ModuleGuide(
                    "Perfil",
                    List.of("perfil", "mi perfil", "datos personales", "cambiar contrasena"),
                    Set.of(),
                    "Permite consultar la información de la cuenta y gestionar la contraseña.",
                    List.of("Abre tu tarjeta de perfil en el menú lateral.",
                            "Revisa la información disponible y usa la opción de contraseña cuando corresponda.")
            )
    );

    public ChatbotResolution features() {
        List<ModuleGuide> visible = visibleGuides();
        String modules = visible.stream()
                .map(guide -> "• " + guide.title() + ": " + guide.summary())
                .collect(Collectors.joining("\n"));
        return ChatbotResolution.text("Según los permisos de tu cuenta, puedes usar estas funcionalidades:\n" + modules
                + "\nPuedes preguntarme, por ejemplo: “¿Cómo registro un recojo?”");
    }

    public ChatbotResolution guidance(String message) {
        String normalized = ChatIntentDetector.normalize(message);
        ModuleGuide guide = GUIDES.stream()
                .filter(item -> item.aliases().stream().anyMatch(normalized::contains))
                .findFirst()
                .orElse(null);

        if (guide == null) {
            return ChatbotResolution.text("Puedo explicarte procedimientos de inventario, platos, ingresos, "
                    + "beneficiarios, recojos, reportes, usuarios y roles. Indícame qué operación deseas realizar.");
        }

        if (!isVisible(guide)) {
            return ChatbotResolution.text("La operación corresponde a “" + guide.title()
                    + "”, pero tu cuenta no tiene un permiso asociado a ese módulo. "
                    + "Solicita acceso al responsable de roles y permisos.");
        }

        String steps = java.util.stream.IntStream.range(0, guide.steps().size())
                .mapToObj(index -> (index + 1) + ". " + guide.steps().get(index))
                .collect(Collectors.joining("\n"));
        return ChatbotResolution.text(guide.title() + "\n" + guide.summary() + "\n\nPasos generales:\n" + steps);
    }

    public ChatbotResolution general() {
        return ChatbotResolution.text("Puedo consultar stock y alertas de productos, contar recojos, listar "
                + "beneficiarios con recojo, recomendar platos según el inventario y explicar cómo usar el sistema. "
                + "Formula la consulta indicando el producto, la fecha o la operación que necesitas.");
    }

    private List<ModuleGuide> visibleGuides() {
        return GUIDES.stream().filter(this::isVisible).toList();
    }

    private boolean isVisible(ModuleGuide guide) {
        if (guide.requiredAny().isEmpty()) {
            return true;
        }
        Set<String> authorities = authorizationService.currentAuthorities();
        return guide.requiredAny().stream().anyMatch(authorities::contains);
    }

    private record ModuleGuide(
            String title,
            List<String> aliases,
            Set<String> requiredAny,
            String summary,
            List<String> steps
    ) {
    }
}
