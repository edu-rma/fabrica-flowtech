package edu.eduark.bizarre.fabrica.flowtech.controller;

import javafx.fxml.FXML;


public class EmpleadoDashboardController extends BaseDashboardController {

    @FXML
    private void mostrarProduccion() {

        lblContenidoActual.setText("Módulo de Producción (en construcción)");
    }

    @FXML
    private void mostrarInventario() {

        lblContenidoActual.setText("Módulo de Inventario (en construcción)");
    }
}