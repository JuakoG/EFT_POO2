package datos;

import conexion.ConexionBD;
import models.Libro;
import models.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public List<Libro> listarTodos() {

        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql);
             ResultSet resultSet =
                     preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                libros.add(mapear(resultSet));
            }

            return libros;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Libro buscarPorId(int id) {

        String sql = "SELECT * FROM libros WHERE id = ?";

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

    public boolean guardarLibroBD(Libro libro) {

        String sql = """
                INSERT INTO libros
                (titulo, autor, isbn, editorial, stock, id_categoria)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, libro.getTitulo());
            preparedStatement.setString(2, libro.getAutor());
            preparedStatement.setString(3, libro.getIsbn());
            preparedStatement.setString(4, libro.getEditorial());
            preparedStatement.setInt(5, libro.getStock());
            preparedStatement.setInt(6, libro.getCategoria().getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean modificarLibroBD(Libro libro) {

        String sql = """
                UPDATE libros
                SET titulo = ?,
                    autor = ?,
                    isbn = ?,
                    editorial = ?,
                    stock = ?,
                    id_categoria = ?
                WHERE id = ?
                """;

        try (Connection conexion =
                     ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement =
                     conexion.prepareStatement(sql)) {

            preparedStatement.setString(1, libro.getTitulo());
            preparedStatement.setString(2, libro.getAutor());
            preparedStatement.setString(3, libro.getIsbn());
            preparedStatement.setString(4, libro.getEditorial());
            preparedStatement.setInt(5, libro.getStock());
            preparedStatement.setInt(6, libro.getCategoria().getId());
            preparedStatement.setInt(7, libro.getId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean eliminarLibro(int id) {

        String sql = "DELETE FROM libros WHERE id = ?";

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

    private Libro mapear(ResultSet resultSet) throws SQLException {

        CategoriaDAO categoriaDAO = new CategoriaDAO();

        Categoria categoria = categoriaDAO.buscarPorId(
                resultSet.getInt("id_categoria")
        );

        return new Libro(
                resultSet.getInt("id"),
                resultSet.getString("titulo"),
                resultSet.getString("autor"),
                resultSet.getString("editorial"),
                resultSet.getString("isbn"),
                resultSet.getInt("stock"),
                categoria
        );
    }
}