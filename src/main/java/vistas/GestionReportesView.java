package vistas;

import datos.LibroDAO;
import datos.PrestamoDAO;
import datos.EstudianteDAO;

import models.Libro;
import models.Prestamo;
import models.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionReportesView extends JFrame {

    private JTable tablaReportes;
    private DefaultTableModel modeloTabla;

    private JComboBox<Estudiante> comboEstudiantes;

    private LibroDAO libroDAO = new LibroDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private EstudianteDAO estudianteDAO = new EstudianteDAO();

    public GestionReportesView() {

        setTitle("Gestión de Reportes");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        JLabel lblTitulo = new JLabel("GESTIÓN DE REPORTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBounds(300, 15, 300, 30);
        add(lblTitulo);

        JButton btnLibros = new JButton("Libros más prestados");
        btnLibros.setBounds(20, 60, 200, 35);
        add(btnLibros);

        JButton btnActivos = new JButton("Libros en préstamo");
        btnActivos.setBounds(230, 60, 190, 35);
        add(btnActivos);

        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.setBounds(430, 60, 200, 35);
        add(comboEstudiantes);

        JButton btnHistorial = new JButton("Ver historial");
        btnHistorial.setBounds(640, 60, 170, 35);
        add(btnHistorial);

        modeloTabla = new DefaultTableModel(
                new Object[]{"Dato 1", "Dato 2", "Dato 3", "Dato 4"}, 0
        );

        tablaReportes = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaReportes);
        scroll.setBounds(20, 115, 790, 370);
        add(scroll);

        btnLibros.addActionListener(e -> cargarLibrosMasPrestados());
        btnActivos.addActionListener(e -> cargarPrestamosActivos());
        btnHistorial.addActionListener(e -> cargarHistorialEstudiante());

        cargarEstudiantes();
    }

    private void cargarEstudiantes() {

        try {
            List<Estudiante> estudiantes = estudianteDAO.listarTodos();

            for (Estudiante estudiante : estudiantes) {
                comboEstudiantes.addItem(estudiante);
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar estudiantes: " + e.getMessage()
            );
        }
    }

    private void cargarLibrosMasPrestados() {

        try {
            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(
                    new Object[]{"ID libro", "Título", "Préstamos"}
            );

            List<Libro> libros = libroDAO.listarTodos();
            List<Prestamo> prestamos = prestamoDAO.listar();

            for (Libro libro : libros) {

                int total = 0;

                for (Prestamo prestamo : prestamos) {
                    if (prestamo.getIdLibro() == libro.getId()) {
                        total++;
                    }
                }

                if (total > 0) {
                    modeloTabla.addRow(new Object[]{
                            libro.getId(),
                            libro.getTitulo(),
                            total
                    });
                }
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar el reporte: " + e.getMessage()
            );
        }
    }

    private void cargarPrestamosActivos() {

        try {
            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(
                    new Object[]{"ID préstamo", "Estudiante", "Libro", "Fecha préstamo"}
            );

            List<Libro> libros = libroDAO.listarTodos();
            List<Estudiante> estudiantes = estudianteDAO.listarTodos();
            List<Prestamo> prestamos =
                    prestamoDAO.listarPrestamosActivos();

            for (Prestamo prestamo : prestamos) {

                String nombreLibro = "No encontrado";
                String nombreEstudiante = "No encontrado";

                for (Libro libro : libros) {
                    if (libro.getId() == prestamo.getIdLibro()) {
                        nombreLibro = libro.getTitulo();
                    }
                }

                for (Estudiante estudiante : estudiantes) {
                    if (estudiante.getId() == prestamo.getIdEstudiante()) {
                        nombreEstudiante = estudiante.getNombre();
                    }
                }

                modeloTabla.addRow(new Object[]{
                        prestamo.getId(),
                        nombreEstudiante,
                        nombreLibro,
                        prestamo.getFechaPrestamo()
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los préstamos: " + e.getMessage()
            );
        }
    }

    private void cargarHistorialEstudiante() {

        Estudiante estudiante =
                (Estudiante) comboEstudiantes.getSelectedItem();

        if (estudiante == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un estudiante."
            );
            return;
        }

        try {
            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(
                    new Object[]{"ID préstamo", "Libro", "Fecha préstamo", "Fecha devolución"}
            );

            List<Libro> libros = libroDAO.listarTodos();

            List<Prestamo> prestamos =
                    prestamoDAO.listarPorEstudiante(estudiante.getId());

            for (Prestamo prestamo : prestamos) {

                String nombreLibro = "No encontrado";

                for (Libro libro : libros) {
                    if (libro.getId() == prestamo.getIdLibro()) {
                        nombreLibro = libro.getTitulo();
                    }
                }

                modeloTabla.addRow(new Object[]{
                        prestamo.getId(),
                        nombreLibro,
                        prestamo.getFechaPrestamo(),
                        prestamo.getFechaDevolucion() == null
                                ? "Pendiente"
                                : prestamo.getFechaDevolucion()
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar el historial: " + e.getMessage()
            );
        }
    }
}