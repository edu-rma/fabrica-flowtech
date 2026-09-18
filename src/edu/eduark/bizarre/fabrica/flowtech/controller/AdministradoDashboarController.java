package edu.eduark.bizarre.fabrica.flowtech.controller;

import javafx.fxml.FXML;

/**
 * Controller del dashboard para usuarios con rol ADMINISTRADOR.
 * Vista asociada: dashboar-administrador-view.fxml
 */
public class AdministradoDashboarController extends BaseDashboardController {

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

    @FXML
    private void mostrarReportes() {
        // TODO: cargar reportes-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Reportes (en construcción)");
    }

    @FXML
    private void mostrarUsuarios() {
        // TODO: cargar usuarios-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Gestión de Usuarios (en construcción)");
    }
}