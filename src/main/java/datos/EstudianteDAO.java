package datos;

import conexion.ConexionBD;
import models.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public List<Estudiante> listarTodos() {

        List<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql);
             ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                estudiantes.add(mapear(resultSet));
            }

            return estudiantes;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Estudiante buscarPorId(int id) {

        String sql = "SELECT * FROM estudiantes WHERE id = ?";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return mapear(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean guardarEstudianteBD(Estudiante estudiante) {

        String sql = """
                INSERT INTO estudiantes
                (nombre, rut, curso, correo)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, estudiante.getNombre());
            preparedStatement.setString(2, estudiante.getRut());
            preparedStatement.setString(3, estudiante.getCurso());
            preparedStatement.setString(4, estudiante.getCorreo());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean eliminarEstudianteBD(int id) {

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Estudiante mapear(ResultSet resultSet)
            throws SQLException {

        return new Estudiante(
                resultSet.getInt("id"),
                resultSet.getString("nombre"),
                resultSet.getString("rut"),
                resultSet.getString("curso"),
                resultSet.getString("correo")
        );
    }

    public boolean modificarEstudianteBD(Estudiante estudiante) {

        String sql = """
            UPDATE estudiantes
            SET nombre = ?, rut = ?, curso = ?, correo = ?
            WHERE id = ?
            """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, estudiante.getNombre());
            preparedStatement.setString(2, estudiante.getRut());
            preparedStatement.setString(3, estudiante.getCurso());
            preparedStatement.setString(4, estudiante.getCorreo());
            preparedStatement.setInt(5, estudiante.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Estudiante buscarPorRut(String rut) {

        String sql = "SELECT * FROM estudiantes WHERE rut = ?";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, rut);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return mapear(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}