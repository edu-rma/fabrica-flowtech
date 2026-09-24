/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface DashboardService {

    List<Usuario> obtenerTodos();

    Optional<Usuario> obtenerPorId(Long id);

    Usuario guardar(Usuario entidad);

    Usuario actualizar(Long id, Usuario entidad);

    boolean eliminarPorId(Long id);
}