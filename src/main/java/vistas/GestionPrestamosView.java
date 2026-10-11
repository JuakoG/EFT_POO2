package vistas;

import controlador.PrestamoControlador;
import datos.EstudianteDAO;
import datos.LibroDAO;
import datos.PrestamoDAO;
import models.Estudiante;
import models.Libro;
import models.Prestamo;
import models.Usuario;
import models.Rol;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionPrestamosView extends JFrame {

    private JComboBox<String> comboEstudiantes;
    private JComboBox<String> comboLibros;

    private JButton btnRegistrar;
    private JButton btnDevolver;
    private JButton btnActualizar;
    private JButton btnCerrar;

    private JTable tablaPrestamos;
    private DefaultTableModel modeloTabla;

    private EstudianteDAO estudianteDAO = new EstudianteDAO();
    private LibroDAO libroDAO = new LibroDAO();
    private PrestamoDAO prestamoDAO = new PrestamoDAO();
    private PrestamoControlador prestamoControlador =
            new PrestamoControlador();

    private List<Estudiante> estudiantes;
    private List<Libro> libros;

    private Usuario usuario;

    public GestionPrestamosView() {
        this(null);
    }

    public GestionPrestamosView(Usuario usuario) {

        this.usuario = usuario;

        setTitle("Gestión de Préstamos");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        crearComponentes();
        cargarEstudiantes();
        cargarLibros();
        cargarPrestamos();
        configurarPermisos();
    }

    private void crearComponentes() {

        setLayout(null);

        JLabel lblTitulo = new JLabel(
                "Gestión de Préstamos y Devoluciones"
        );
        lblTitulo.setBounds(320, 15, 350, 25);
        add(lblTitulo);

        JLabel lblEstudiante = new JLabel("Estudiante:");
        lblEstudiante.setBounds(30, 60, 100, 25);
        add(lblEstudiante);

        comboEstudiantes = new JComboBox<>();
        comboEstudiantes.setBounds(120, 60, 300, 25);
        add(comboEstudiantes);

        JLabel lblLibro = new JLabel("Libro:");
        lblLibro.setBounds(450, 60, 70, 25);
        add(lblLibro);

        comboLibros = new JComboBox<>();
        comboLibros.setBounds(500, 60, 450, 25);
        add(comboLibros);

        btnRegistrar = new JButton("Registrar préstamo");
        btnRegistrar.setBounds(30, 105, 190, 30);
        add(btnRegistrar);

        btnDevolver = new JButton("Registrar devolución");
        btnDevolver.setBounds(235, 105, 200, 30);
        add(btnDevolver);

        btnActualizar = new JButton("Actualizar tabla");
        btnActualizar.setBounds(450, 105, 170, 30);
        add(btnActualizar);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(700, 105, 120, 30);
        add(btnCerrar);

        modeloTabla = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Estudiante",
                        "Libro",
                        "Fecha préstamo",
                        "Fecha vencimiento",
                        "Fecha devolución",
                        "Estado"
                }, 0
        );

        tablaPrestamos = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaPrestamos);
        scroll.setBounds(30, 155, 920, 330);
        add(scroll);

        btnRegistrar.addActionListener(e -> registrarPrestamo());

        btnDevolver.addActionListener(e -> registrarDevolucion());

        btnActualizar.addActionListener(e -> {
            cargarEstudiantes();
            cargarLibros();
            cargarPrestamos();

            JOptionPane.showMessageDialog(
                    this,
                    "Tabla actualizada correctamente."
            );
        });

        btnCerrar.addActionListener(e -> dispose());
    }


    private void cargarEstudiantes() {

        comboEstudiantes.removeAllItems();
        estudiantes = estudianteDAO.listarTodos();

        if (usuario != null
                && usuario.getRol() == Rol.ESTUDIANTE) {

            Estudiante estudiante =
                    estudianteDAO.buscarPorRut(usuario.getRut());

            if (estudiante != null) {

                estudiantes.clear();
                estudiantes.add(estudiante);

                comboEstudiantes.addItem(
                        estudiante.getId() + " - "
                                + estudiante.getNombre()
                );

                comboEstudiantes.setSelectedIndex(0);
                comboEstudiantes.setEnabled(false);
                btnRegistrar.setEnabled(true);

            } else {

                btnRegistrar.setEnabled(false);

                JOptionPane.showMessageDialog(
                        this,
                        "No existe un estudiante asociado a tu RUT."
                );
            }

        } else {

            for (Estudiante estudiante : estudiantes) {

                comboEstudiantes.addItem(
                        estudiante.getId() + " - "
                                + estudiante.getNombre()
                );
            }

            comboEstudiantes.setEnabled(true);
            btnRegistrar.setEnabled(true);
        }
    }

    private void cargarLibros() {

        comboLibros.removeAllItems();

        libros = libroDAO.listarTodos();

        for (Libro libro : libros) {
            comboLibros.addItem(
                    libro.getId() + " - "
                            + libro.getTitulo()
                            + " - Stock: "
                            + libro.getStock()
            );
        }
    }

    private void cargarPrestamos() {

        modeloTabla.setRowCount(0);

        List<Prestamo> prestamos;

        if (usuario != null
                && usuario.getRol() == Rol.ESTUDIANTE) {

            Estudiante estudiante =
                    estudianteDAO.buscarPorRut(usuario.getRut());

            if (estudiante == null) {
                return;
            }

            prestamos =
                    prestamoDAO.listarPorEstudiante(estudiante.getId());

        } else {

            prestamos = prestamoDAO.listar();
        }

        for (Prestamo prestamo : prestamos) {

            Estudiante estudiante =
                    estudianteDAO.buscarPorId(
                            prestamo.getIdEstudiante());

            Libro libro =
                    libroDAO.buscarPorId(prestamo.getIdLibro());

            modeloTabla.addRow(new Object[]{
                    prestamo.getId(),
                    estudiante != null ? estudiante.getNombre() : "",
                    libro != null ? libro.getTitulo() : "",
                    prestamo.getFechaPrestamo(),
                    prestamo.getFechaVencimiento(),
                    prestamo.getFechaDevolucion(),
                    prestamo.isDevuelto() ? "Devuelto" : "Prestado"
            });
        }
    }



    private void registrarPrestamo() {

        Estudiante estudiante;

        if (usuario != null
                && usuario.getRol() == Rol.ESTUDIANTE) {

            estudiante =
                    estudianteDAO.buscarPorRut(usuario.getRut());

            if (estudiante == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No existe un estudiante asociado a tu RUT."
                );
                return;
            }

        } else {

            int indiceEstudiante =
                    comboEstudiantes.getSelectedIndex();

            if (indiceEstudiante < 0
                    || estudiantes == null
                    || indiceEstudiante >= estudiantes.size()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debes seleccionar un estudiante."
                );
                return;
            }

            estudiante = estudiantes.get(indiceEstudiante);
        }

        int indiceLibro = comboLibros.getSelectedIndex();

        if (indiceLibro < 0
                || libros == null
                || indiceLibro >= libros.size()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un libro."
            );
            return;
        }

        Libro libro = libros.get(indiceLibro);

        if (libro.getStock() <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este libro no tiene stock disponible."
            );
            return;
        }

        int idEstudiante = estudiante.getId();
        int idLibro = libro.getId();

        btnRegistrar.setEnabled(false);

        Thread hilo = new Thread(() -> {
            try {
                boolean registrado =
                        prestamoControlador.registrarPrestamo(
                                idEstudiante, idLibro
                        );

                SwingUtilities.invokeLater(() -> {
                    btnRegistrar.setEnabled(true);

                    if (registrado) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Préstamo registrado correctamente."
                        );

                        cargarLibros();
                        cargarPrestamos();

                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "No se pudo registrar el préstamo. "
                                        + "Verifica el stock disponible."
                        );

                        cargarLibros();
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    btnRegistrar.setEnabled(true);

                    JOptionPane.showMessageDialog(
                            this,
                            "Error al registrar el préstamo: "
                                    + e.getMessage()
                    );
                });
            }
        });

        hilo.start();
    }


    private void registrarDevolucion() {

        if (usuario == null
                || usuario.getRol() != Rol.BIBLIOTECARIO) {

            JOptionPane.showMessageDialog(
                    this,
                    "No tienes permiso para registrar devoluciones."
            );
            return;
        }

        int fila = tablaPrestamos.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un préstamo."
            );
            return;
        }

        int idPrestamo = (int) tablaPrestamos
                .getValueAt(fila, 0);

        String estado = tablaPrestamos
                .getValueAt(fila, 6).toString();



        if (!estado.equalsIgnoreCase("Prestado")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este préstamo ya fue devuelto o no está pendiente."
            );
            return;
        }

        btnDevolver.setEnabled(false);

        Thread hilo = new Thread(() -> {
            try {
                boolean devuelto =
                        prestamoControlador.registrarDevolucion(
                                idPrestamo
                        );

                SwingUtilities.invokeLater(() -> {
                    btnDevolver.setEnabled(true);

                    if (devuelto) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Devolución registrada correctamente."
                        );

                        cargarLibros();
                        cargarPrestamos();

                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "No se pudo registrar la devolución."
                        );

                        cargarLibros();
                        cargarPrestamos();
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    btnDevolver.setEnabled(true);

                    JOptionPane.showMessageDialog(
                            this,
                            "Error al registrar la devolución: "
                                    + e.getMessage()
                    );
                });
            }
        });

        hilo.start();
    }

    private void configurarPermisos() {

        if (usuario == null) {
            return;
        }

        boolean esBibliotecario =
                usuario.getRol() == Rol.BIBLIOTECARIO;

        btnDevolver.setVisible(esBibliotecario);
        btnActualizar.setVisible(esBibliotecario);

        if (!esBibliotecario) {
            setTitle("Mis préstamos");
        }
    }


}