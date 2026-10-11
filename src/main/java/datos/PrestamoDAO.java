
package datos;

import conexion.ConexionBD;
import models.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public List<Prestamo> listar() {

        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql);
             ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                prestamos.add(mapear(resultSet));
            }

            return prestamos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean registrarPrestamoBD(
            Prestamo prestamo) {

        String sqlStock =
                "UPDATE libros SET stock = stock - 1 " +
                        "WHERE id = ? AND stock > 0";

        String sqlPrestamo = """
                INSERT INTO prestamos
                (id_estudiante, id_libro, fecha_prestamo,
                 fecha_devolucion, devuelto)
                VALUES (?, ?, ?, NULL, FALSE)
                """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                LocalDate fechaPrestamo = LocalDate.now();

                prestamo.setFechaPrestamo(fechaPrestamo);
                prestamo.setFechaVencimiento(
                        fechaPrestamo.plusDays(7));
                prestamo.setFechaDevolucion(null);
                prestamo.setDevuelto(false);

                try (PreparedStatement preparedStatement =
                             conexion.prepareStatement(sqlStock)) {

                    preparedStatement.setInt(
                            1, prestamo.getIdLibro());

                    if (preparedStatement.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                try (PreparedStatement preparedStatement =
                             conexion.prepareStatement(sqlPrestamo)) {

                    preparedStatement.setInt(
                            1, prestamo.getIdEstudiante());

                    preparedStatement.setInt(
                            2, prestamo.getIdLibro());

                    preparedStatement.setDate(
                            3, Date.valueOf(fechaPrestamo));

                    if (preparedStatement.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                conexion.commit();
                return true;

            } catch (Exception e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized boolean registrarDevolucion(
            int idPrestamo) {

        String sqlBuscar = """
                SELECT id_libro
                FROM prestamos
                WHERE id = ? AND devuelto = FALSE
                FOR UPDATE
                """;

        String sqlDevolucion = """
                UPDATE prestamos
                SET devuelto = TRUE, fecha_devolucion = ?
                WHERE id = ? AND devuelto = FALSE
                """;

        String sqlStock =
                "UPDATE libros SET stock = stock + 1 WHERE id = ?";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                int idLibro;

                try (PreparedStatement preparedStatement =
                             conexion.prepareStatement(sqlBuscar)) {

                    preparedStatement.setInt(1, idPrestamo);

                    try (ResultSet resultSet =
                                 preparedStatement.executeQuery()) {

                        if (!resultSet.next()) {
                            conexion.rollback();
                            return false;
                        }

                        idLibro = resultSet.getInt("id_libro");
                    }
                }

                try (PreparedStatement preparedStatement =
                             conexion.prepareStatement(sqlDevolucion)) {

                    preparedStatement.setDate(
                            1, Date.valueOf(LocalDate.now()));

                    preparedStatement.setInt(2, idPrestamo);

                    if (preparedStatement.executeUpdate() == 0) {
                        conexion.rollback();
                        return false;
                    }
                }

                try (PreparedStatement preparedStatement =
                             conexion.prepareStatement(sqlStock)) {

                    preparedStatement.setInt(1, idLibro);

                    if (preparedStatement.executeUpdate() == 0) {
                        throw new SQLException(
                                "No se pudo actualizar el stock.");
                    }
                }

                conexion.commit();
                return true;

            } catch (Exception e) {
                conexion.rollback();
                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante) {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql =
                "SELECT * FROM prestamos WHERE id_estudiante = ?";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setInt(1, idEstudiante);

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    prestamos.add(mapear(resultSet));
                }
            }

            return prestamos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<Prestamo> listarPrestamosActivos() {

        List<Prestamo> prestamos = new ArrayList<>();

        String sql =
                "SELECT * FROM prestamos WHERE devuelto = FALSE";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql);
             ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                prestamos.add(mapear(resultSet));
            }

            return prestamos;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Prestamo> listarPrestamosAtrasados() {

        List<Prestamo> prestamosAtrasados = new ArrayList<>();

        String sql =
                "SELECT * FROM prestamos WHERE devuelto = FALSE";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql);
             ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            LocalDate hoy = LocalDate.now();

            while (resultSet.next()) {
                Prestamo prestamo = mapear(resultSet);

                if (prestamo.getFechaVencimiento() != null
                        && prestamo.getFechaVencimiento()
                        .isBefore(hoy)) {

                    prestamosAtrasados.add(prestamo);
                }
            }

            return prestamosAtrasados;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Prestamo mapear(ResultSet resultSet)
            throws SQLException {

        Prestamo prestamo = new Prestamo();

        prestamo.setId(resultSet.getInt("id"));
        prestamo.setIdEstudiante(
                resultSet.getInt("id_estudiante"));
        prestamo.setIdLibro(
                resultSet.getInt("id_libro"));

        Date fechaPrestamo =
                resultSet.getDate("fecha_prestamo");

        if (fechaPrestamo != null) {
            LocalDate fecha = fechaPrestamo.toLocalDate();

            prestamo.setFechaPrestamo(fecha);
            prestamo.setFechaVencimiento(fecha.plusDays(7));
        }

        Date fechaDevolucion = resultSet.getDate("fecha_devolucion");

        if (fechaDevolucion != null) {
            prestamo.setFechaDevolucion(
                    fechaDevolucion.toLocalDate());
        } else {
            prestamo.setFechaDevolucion(null);
        }

        prestamo.setDevuelto(
                resultSet.getBoolean("devuelto"));

        return prestamo;
    }
}