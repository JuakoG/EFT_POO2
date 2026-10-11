package datos;

import conexion.ConexionBD;
import models.Usuario;
import models.Rol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario validarLogin(String correo, String contrasena) {

        String sql = """
     SELECT *
                FROM usuarios
                WHERE correo = ? AND contraseña = ?
                """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, correo);
            preparedStatement.setString(2, contrasena);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return mapear(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Usuario mapear(ResultSet resultSet)
            throws SQLException {

        return new Usuario(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("rut"),
                resultSet.getString("correo"),
                resultSet.getString("contraseña"),
                Rol.valueOf(
                        resultSet.getString("rol").toUpperCase()
                )
        );
    }
}