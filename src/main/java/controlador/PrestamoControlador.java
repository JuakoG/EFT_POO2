
package controlador;

import datos.PrestamoDAO;
import models.Prestamo;

import java.time.LocalDate;

public class PrestamoControlador {

    private PrestamoDAO prestamoDAO;

    public PrestamoControlador() {
        prestamoDAO = new PrestamoDAO();
    }

    public boolean registrarPrestamo(int idEstudiante, int idLibro) {
        try {
            Prestamo prestamo = new Prestamo();
            prestamo.setIdEstudiante(idEstudiante);
            prestamo.setIdLibro(idLibro);
            prestamo.setFechaPrestamo(LocalDate.now());

            return prestamoDAO.registrarPrestamoBD(prestamo);

        } catch (Exception e) {
            System.out.println("Error al registrar el préstamo: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean registrarDevolucion(int idPrestamo) {
        try {
            return prestamoDAO.registrarDevolucion(idPrestamo);
        } catch (Exception e) {
            System.out.println("Error al registrar la devolución: "
                    + e.getMessage());
            return false;
        }
    }
}
