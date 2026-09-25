package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import edu.eduark.bizarre.fabrica.flowtech.repository.ProductoRepository;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService() {
        this(new ProductoRepository());
    }

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoOpcion> listarTodos() {
        try {
            return productoRepository.listarTodos();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar el catálogo de productos.", error);
        }
    }
}
