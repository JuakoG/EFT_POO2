package vistas;

import datos.EstudianteDAO;
import models.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionEstudiantesView extends JFrame {

    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtCurso;
    private JTextField txtCorreo;

    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;

    private EstudianteDAO estudianteDAO;

    public GestionEstudiantesView() {

        estudianteDAO = new EstudianteDAO();

        setTitle("Gestión de Estudiantes");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        crearComponentes();
        cargarEstudiantes();
    }

    private void crearComponentes() {

        JLabel lblTitulo = new JLabel("Gestión de Estudiantes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setBounds(280, 15, 300, 30);
        add(lblTitulo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 65, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(120, 65, 220, 25);
        add(txtNombre);

        JLabel lblRut = new JLabel("RUT:");
        lblRut.setBounds(380, 65, 100, 25);
        add(lblRut);

        txtRut = new JTextField();
        txtRut.setBounds(470, 65, 220, 25);
        add(txtRut);

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setBounds(30, 105, 100, 25);
        add(lblCurso);

        txtCurso = new JTextField();
        txtCurso.setBounds(120, 105, 220, 25);
        add(txtCurso);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(380, 105, 100, 25);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(470, 105, 220, 25);
        add(txtCorreo);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(30, 155, 120, 30);
        add(btnRegistrar);

        JButton btnSeleccionar = new JButton("Seleccionar");
        btnSeleccionar.setBounds(160, 155, 120, 30);
        add(btnSeleccionar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(290, 155, 120, 30);
        add(btnActualizar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(420, 155, 100, 30);
        add(btnLimpiar);

        JButton btnEliminar = new JButton("Eliminar estudiante");
        btnEliminar.setBounds(530, 155, 180, 30);
        add(btnEliminar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0
        );

        tablaEstudiantes = new JTable(modeloTabla);

        JScrollPane scrollPane = new JScrollPane(tablaEstudiantes);
        scrollPane.setBounds(30, 210, 775, 260);
        add(scrollPane);

        btnRegistrar.addActionListener(e -> registrarEstudiante());
        btnSeleccionar.addActionListener(e -> seleccionarEstudiante());
        btnActualizar.addActionListener(e -> actualizarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnEliminar.addActionListener(e -> eliminarEstudiante());
    }

    private void cargarEstudiantes() {

        try {

            modeloTabla.setRowCount(0);

            List<Estudiante> estudiantes = estudianteDAO.listarTodos();

            for (Estudiante estudiante : estudiantes) {

                modeloTabla.addRow(new Object[]{
                        estudiante.getId(),
                        estudiante.getNombre(),
                        estudiante.getRut(),
                        estudiante.getCurso(),
                        estudiante.getCorreo()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Error al cargar los estudiantes: " + e.getMessage());
        }
    }

    private void registrarEstudiante() {

        String nombre = txtNombre.getText();
        String rut = txtRut.getText();
        String curso = txtCurso.getText();
        String correo = txtCorreo.getText();

        if (nombre.isEmpty() || rut.isEmpty()
                || curso.isEmpty() || correo.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Debes completar todos los campos.");
            return;
        }

        Estudiante estudiante = new Estudiante(
                0, nombre, rut, curso, correo
        );

        try {

            boolean registrado =
                    estudianteDAO.guardarEstudianteBD(estudiante);

            if (registrado) {

                JOptionPane.showMessageDialog(this,
                        "Estudiante registrado correctamente.");

                limpiarCampos();
                cargarEstudiantes();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Error al registrar. Verifica que el RUT no esté repetido.");
        }
    }

    private void seleccionarEstudiante() {

        int fila = tablaEstudiantes.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un estudiante.");
            return;
        }

        txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
        txtRut.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtCurso.setText(modeloTabla.getValueAt(fila, 3).toString());
        txtCorreo.setText(modeloTabla.getValueAt(fila, 4).toString());
    }
    private void actualizarEstudiante() {

        int fila = tablaEstudiantes.getSelectedRow();

        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe seleccionar un estudiante"
            );

            return;
        }

        try {

            String nombre = txtNombre.getText().trim();
            String rut = txtRut.getText().trim();
            String curso = txtCurso.getText().trim();
            String correo = txtCorreo.getText().trim();

            if (nombre.isEmpty() || rut.isEmpty()
                    || curso.isEmpty() || correo.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debe completar todos los campos",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            int id = Integer.parseInt(
                    modeloTabla.getValueAt(fila, 0).toString()
            );

            Estudiante estudiante = new Estudiante(
                    id, nombre, rut, curso, correo
            );

            estudianteDAO.modificarEstudianteBD(estudiante);

            limpiarCampos();
            cargarEstudiantes();

            JOptionPane.showMessageDialog(
                    this,
                    "Estudiante actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarEstudiante() {

        int fila = tablaEstudiantes.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un estudiante.");
            return;
        }

        int id = Integer.parseInt(
                modeloTabla.getValueAt(fila, 0).toString()
        );

        try {

            boolean eliminado =
                    estudianteDAO.eliminarEstudianteBD(id);

            if (eliminado) {

                limpiarCampos();
                cargarEstudiantes();

                JOptionPane.showMessageDialog(this,
                        "Estudiante eliminado");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar. Puede tener préstamos asociados.");
        }
    }

    private void limpiarCampos() {

        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
    }
}