package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AuthService implements DashboardService {

    private static final String SELECT_BASE =
            "SELECT id_usuario, nombre, usuario, clave, rol, estado, fecha_creacion FROM usuario";

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + " ORDER BY id_usuario");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible obtener los usuarios.", e);
        }
    }

    @Override
    public Optional<Usuario> obtenerPorId(Long id) {
        validarId(id);

        String sql = SELECT_BASE + " WHERE id_usuario = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapearUsuario(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible buscar el usuario con ID " + id + ".", e);
        }
    }

    @Override
    public Usuario guardar(Usuario entidad) {
        validarUsuario(entidad);

        String sql = "INSERT INTO usuario (nombre, usuario, clave, rol, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getUsuario());
            ps.setString(3, entidad.getClave());
            ps.setString(4, entidad.getRol());
            ps.setString(5, entidad.getEstado());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entidad.setIdUsuario(keys.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible guardar el usuario.", e);
        }
    }

    @Override
    public Usuario actualizar(Long id, Usuario entidad) {
        validarId(id);
        validarUsuario(entidad);

        String sql = "UPDATE usuario SET nombre = ?, usuario = ?, clave = ?, rol = ?, estado = ? "
                + "WHERE id_usuario = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, entidad.getNombre());
            ps.setString(2, entidad.getUsuario());
            ps.setString(3, entidad.getClave());
            ps.setString(4, entidad.getRol());
            ps.setString(5, entidad.getEstado());
            ps.setLong(6, id);

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("No existe un usuario con ID " + id + ".");
            }

            entidad.setIdUsuario(id.intValue());
            return entidad;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible actualizar el usuario con ID " + id + ".", e);
        }
    }

    @Override
    public boolean eliminarPorId(Long id) {
        validarId(id);

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No fue posible eliminar el usuario con ID " + id + ".", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("usuario"),
                rs.getString("clave"),
                rs.getString("rol"),
                rs.getString("estado"),
                rs.getTimestamp("fecha_creacion")
        );
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (usuario.getUsuario() == null || usuario.getUsuario().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            throw new IllegalArgumentException("La clave es obligatoria.");
        }
    }
}
