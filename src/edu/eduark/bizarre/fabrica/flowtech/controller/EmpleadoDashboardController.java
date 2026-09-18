package edu.eduark.bizarre.fabrica.flowtech.controller;

import javafx.fxml.FXML;

/**
 * Controller del dashboard para usuarios con rol EMPLEADO.
 * Vista asociada: dashboar-empleado-view.fxml
 */
public class EmpleadoDashboardController extends BaseDashboardController {

    @FXML
    private void mostrarProduccion() {
        // TODO: cargar produccion-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Producción (en construcción)");
    }

    @FXML
    private void mostrarInventario() {
        // TODO: cargar inventario-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Inventario (en construcción)");
    }
}