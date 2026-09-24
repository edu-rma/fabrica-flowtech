package edu.eduark.bizarre.fabrica.flowtech.controller;

import javafx.fxml.FXML;

public class AdministradorDashboardController extends BaseDashboardController {

    @FXML
    private void mostrarProduccion() {

        lblContenidoActual.setText("Módulo de Producción (en construcción)");
    }

    @FXML
    private void mostrarInventario() {

        lblContenidoActual.setText("Módulo de Inventario (en construcción)");
    }

    @FXML
    private void mostrarReportes() {

        lblContenidoActual.setText("Módulo de Reportes (en construcción)");
    }

    @FXML
    private void mostrarUsuarios() {

        lblContenidoActual.setText("Módulo de Gestión de Usuarios (en construcción)");
    }
}