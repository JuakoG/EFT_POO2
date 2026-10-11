package datos;

import conexion.ConexionBD;
import models.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listarTodos() {

        List<Categoria> lista = new ArrayList<>();

        String sql = "SELECT * FROM categorias";

        try (Connection conexion = ConexionBD.getInstance().obtenerConexion();
             PreparedStatement preparedStatement = conexion.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                lista.add(mapear(resultSet));
            }

            return lista;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Categoria buscarPorId(int id) {

        String sql = "SELECT * FROM categorias WHERE id = ?";

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

    private Categoria mapear(ResultSet resultSet) throws SQLException {

        return new Categoria(
                resultSet.getInt("id"),
                resultSet.getString("nombre")
        );
    }
}