package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.OrdenProduccionResumen;
import edu.eduark.bizarre.fabrica.flowtech.repository.OrdenProduccionRepository;
import java.sql.SQLException;
import java.util.List;

public class OrdenProduccionService {

    private final OrdenProduccionRepository ordenProduccionRepository;

    public OrdenProduccionService() {
        this(new OrdenProduccionRepository());
    }

    public OrdenProduccionService(OrdenProduccionRepository ordenProduccionRepository) {
        this.ordenProduccionRepository = ordenProduccionRepository;
    }

    public List<OrdenProduccionResumen> listarTodas() {
        try {
            return ordenProduccionRepository.listarTodas();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar las órdenes de producción.", error);
        }
    }
}
