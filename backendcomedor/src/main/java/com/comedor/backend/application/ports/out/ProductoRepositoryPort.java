package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Producto;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface ProductoRepositoryPort {
    List<Producto> getProductosByStatus(Estado estado);
    Producto createProducto(Producto producto);
    boolean existByName(String nombre);
    Producto deactivateById(int id);
    Producto activateById(int id);
    Producto getProductoById(int id);

    Producto updateProducto(Producto producto);
    boolean tieneTransaccionesVinculadas(int id);
    boolean existByNameAndIdNot(String nombre, int id);
}
