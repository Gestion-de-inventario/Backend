package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Product;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;

public interface ProductRepositoryPort {
    List<Product> getProductosByStatus(Estado estado);
    Product createProducto(Product product);
    boolean existByName(String nombre);
    Product deactivateById(int id);
    Product activateById(int id);
    Product getProductoById(int id);

    Product updateProducto(Product product);
    Product updateStock(Product product);
    boolean tieneTransaccionesVinculadas(int id);
    boolean existByNameAndIdNot(String nombre, int id);

    List<Product> getProductosBajoStockMinimo();
}
