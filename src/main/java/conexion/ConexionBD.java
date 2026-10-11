package conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca";

    private static final String USER = "root";

    private static final String PASSWORD = "admin";



    // todo esto es nuevo para la forma singleton
    private static ConexionBD instancia;



    private ConexionBD() {
    }

    public static synchronized ConexionBD getInstance() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }

        return instancia;
    }

    public Connection obtenerConexion() throws Exception {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}