package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.EmpleadoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;
import edu.eduark.bizarre.fabrica.flowtech.repository.UsuarioRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;


public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this(new UsuarioRepository());
    }

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Valida email/contraseña contra el hash BCrypt almacenado. Vacío si no hay coincidencia o el usuario está inactivo. */
    public Optional<Usuario> autenticar(String email, String contrasena) {
        if (email == null || email.isBlank() || contrasena == null || contrasena.isBlank()) {
            return Optional.empty();
        }
        Optional<Usuario> usuario = buscarPorEmailActivo(email.trim());
        if (usuario.isEmpty()) {
            return Optional.empty();
        }
        return coincideContrasena(contrasena, usuario.get().getPasswordHash()) ? usuario : Optional.empty();
    }

    private Optional<Usuario> buscarPorEmailActivo(String email) {
        try {
            return usuarioRepository.buscarPorEmailActivo(email);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible validar las credenciales.", error);
        }
    }

    private boolean coincideContrasena(String contrasena, String hashGuardado) {
        try {
            return hashGuardado != null && BCrypt.checkpw(contrasena, hashGuardado);
        } catch (IllegalArgumentException error) {
            return false;
        }
    }

    /** Determina el rol por dominio de correo y registra al usuario con la contraseña ya cifrada. */
    public int registrar(String nombre, String apellido, String email, String contrasena) {
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new ServicioException("Nombre, correo y contraseña son obligatorios.");
        }
        int idRol = rolPorCorreo(email.trim().toLowerCase());
        String hash = BCrypt.hashpw(contrasena, BCrypt.gensalt(12));
        try {
            return usuarioRepository.registrar(idRol, nombre, apellido, email.trim().toLowerCase(), hash);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible registrar el usuario.", error);
        }
    }

    public int rolPorCorreo(String correo) {
        if (correo.endsWith("@flowtechad.com")) {
            return 1; // ADMIN
        }
        if (correo.endsWith("@flowtech.com")) {
            return 2; // EMPLEADO
        }
        return 3; // CLIENTE
    }

    public String nombreRol(int idRol) {
        return switch (idRol) {
            case 1 -> "administrador";
            case 2 -> "empleado";
            default -> "cliente";
        };
    }

    public List<EmpleadoResumen> listarEmpleados() {
        try {
            return usuarioRepository.listarEmpleados();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar los empleados.", error);
        }
    }

    public void cambiarEstadoEmpleado(int idUsuario, boolean activo) {
        try {
            usuarioRepository.actualizarEstado(idUsuario, activo);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible actualizar el estado del empleado.", error);
        }
    }

    public List<UsuarioOpcion> listarClientesActivos() {
        return listarActivosPorRol("CLIENTE");
    }

    public List<UsuarioOpcion> listarEmpleadosActivos() {
        return listarActivosPorRol("EMPLEADO");
    }

    private List<UsuarioOpcion> listarActivosPorRol(String rol) {
        try {
            return usuarioRepository.listarActivosPorRol(rol);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar los usuarios con rol " + rol + ".", error);
        }
    }
}
