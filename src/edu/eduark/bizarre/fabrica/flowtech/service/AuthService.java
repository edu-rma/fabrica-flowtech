package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AuthService implements DashboardService {

    private static final String SELECT_BASE =
            "SELECT id_usuario, id_rol, nombre, apellido, email, password_hash, activo, fecha_registro FROM usuarios";

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
            throw new ServicioException("No fue posible obtener los usuarios.", e);
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
                return rs.next() ? Optional.of(mapearUsuario(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ServicioException("No fue posible buscar el usuario con ID " + id + ".", e);
        }
    }

    @Override
    public Usuario guardar(Usuario entidad) {
        validarUsuario(entidad);

        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entidad.getIdRol());
            ps.setString(2, entidad.getNombre());
            ps.setString(3, entidad.getApellido());
            ps.setString(4, entidad.getEmail());
            ps.setString(5, entidad.getPasswordHash());
            ps.setBoolean(6, entidad.isActivo());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entidad.setIdUsuario(keys.getInt(1));
                }
            }
            return entidad;
        } catch (SQLException e) {
            throw new ServicioException("No fue posible guardar el usuario.", e);
        }
    }

    @Override
    public Usuario actualizar(Long id, Usuario entidad) {
        validarId(id);
        validarUsuario(entidad);

        String sql = "UPDATE usuarios SET id_rol = ?, nombre = ?, apellido = ?, email = ?, "
                + "password_hash = ?, activo = ? WHERE id_usuario = ?";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, entidad.getIdRol());
            ps.setString(2, entidad.getNombre());
            ps.setString(3, entidad.getApellido());
            ps.setString(4, entidad.getEmail());
            ps.setString(5, entidad.getPasswordHash());
            ps.setBoolean(6, entidad.isActivo());
            ps.setLong(7, id);

            if (ps.executeUpdate() == 0) {
                throw new ServicioException("No existe un usuario con ID " + id + ".");
            }

            entidad.setIdUsuario(id.intValue());
            return entidad;
        } catch (SQLException e) {
            throw new ServicioException("No fue posible actualizar el usuario con ID " + id + ".", e);
        }
    }

    @Override
    public boolean eliminarPorId(Long id) {
        validarId(id);

        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ServicioException("No fue posible eliminar el usuario con ID " + id + ".", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getInt("id_rol"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getBoolean("activo"),
                rs.getTimestamp("fecha_registro")
        );
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ServicioException("El ID debe ser mayor que cero.");
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new ServicioException("El usuario no puede ser nulo.");
        }
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new ServicioException("El nombre es obligatorio.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ServicioException("El correo es obligatorio.");
        }
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()) {
            throw new ServicioException("La contraseña es obligatoria.");
        }
    }
}
