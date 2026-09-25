package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de CRUD sobre usuarios, pensado para un panel de
 * administración (listar/crear/editar/eliminar cualquier usuario del
 * sistema, sin importar su rol).
 *
 * No reemplaza a UsuarioService: UsuarioService concentra las reglas de
 * negocio de autenticación, registro público y gestión de empleados;
 * DashboardService/AuthService cubren el CRUD administrativo completo.
 */
public interface DashboardService {

    List<Usuario> obtenerTodos();

    Optional<Usuario> obtenerPorId(Long id);

    Usuario guardar(Usuario entidad);

    Usuario actualizar(Long id, Usuario entidad);

    boolean eliminarPorId(Long id);
}
