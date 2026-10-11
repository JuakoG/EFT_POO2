package vistas;

import datos.LibroDAO;
import datos.CategoriaDAO;
import models.Libro;
import models.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GestionLibrosView extends JFrame {

    private JTextField txtId;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtEditorial;
    private JTextField txtIsbn;
    private JTextField txtStock;

    private JComboBox<Categoria> comboCategoria;

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnVolver;

    private LibroDAO libroDAO = new LibroDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    public GestionLibrosView() {

        setTitle("Gestión de Libros");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        crearComponentes();
        cargarCategorias();
        cargarTabla();
    }

    private void crearComponentes() {

        setLayout(null);

        JLabel lblTitulo = new JLabel("GESTIÓN DE LIBROS");
        lblTitulo.setBounds(390, 15, 200, 30);
        add(lblTitulo);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 55, 100, 30);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(130, 55, 200, 30);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblTituloLibro = new JLabel("Título:");
        lblTituloLibro.setBounds(30, 95, 100, 30);
        add(lblTituloLibro);

        txtTitulo = new JTextField();
        txtTitulo.setBounds(130, 95, 200, 30);
        add(txtTitulo);

        JLabel lblAutor = new JLabel("Autor:");
        lblAutor.setBounds(30, 135, 100, 30);
        add(lblAutor);

        txtAutor = new JTextField();
        txtAutor.setBounds(130, 135, 200, 30);
        add(txtAutor);

        JLabel lblEditorial = new JLabel("Editorial:");
        lblEditorial.setBounds(30, 175, 100, 30);
        add(lblEditorial);

        txtEditorial = new JTextField();
        txtEditorial.setBounds(130, 175, 200, 30);
        add(txtEditorial);

        JLabel lblIsbn = new JLabel("ISBN:");
        lblIsbn.setBounds(30, 215, 100, 30);
        add(lblIsbn);

        txtIsbn = new JTextField();
        txtIsbn.setBounds(130, 215, 200, 30);
        add(txtIsbn);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(30, 255, 100, 30);
        add(lblStock);

        txtStock = new JTextField();
        txtStock.setBounds(130, 255, 200, 30);
        add(txtStock);

        JLabel lblCategoria = new JLabel("Categoría:");
        lblCategoria.setBounds(30, 295, 100, 30);
        add(lblCategoria);

        comboCategoria = new JComboBox<>();
        comboCategoria.setBounds(130, 295, 200, 30);
        add(comboCategoria);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(30, 345, 140, 35);
        add(btnGuardar);

        btnModificar = new JButton("Modificar");
        btnModificar.setBounds(180, 345, 140, 35);
        add(btnModificar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(30, 390, 140, 35);
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(180, 390, 140, 35);
        add(btnLimpiar);

        btnVolver = new JButton("Volver al menú");
        btnVolver.setBounds(80, 450, 200, 35);
        add(btnVolver);

        modeloTabla = new DefaultTableModel(
                new Object[]{
                        "ID", "Título", "Autor", "Editorial",
                        "ISBN", "Stock", "Categoría"
                }, 0
        );

        tablaLibros = new JTable(modeloTabla);

        JScrollPane scroll = new JScrollPane(tablaLibros);
        scroll.setBounds(350, 55, 570, 430);
        add(scroll);

        btnGuardar.addActionListener(e -> guardarLibro());
        btnModificar.addActionListener(e -> modificarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> dispose());

        tablaLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarLibro();
            }
        });
    }

    private void cargarCategorias() {

        try {
            List<Categoria> categorias = categoriaDAO.listarTodos();

            for (Categoria categoria : categorias) {
                comboCategoria.addItem(categoria);
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar las categorías: " + e.getMessage()
            );
        }
    }

    private void cargarTabla() {

        try {
            modeloTabla.setRowCount(0);

            List<Libro> libros = libroDAO.listarTodos();

            for (Libro libro : libros) {

                modeloTabla.addRow(new Object[]{
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getEditorial(),
                        libro.getIsbn(),
                        libro.getStock(),
                        libro.getCategoria()
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar los libros: " + e.getMessage()
            );
        }
    }

    private void guardarLibro() {

        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String editorial = txtEditorial.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String stockTexto = txtStock.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty()
                || editorial.isEmpty() || isbn.isEmpty()
                || stockTexto.isEmpty()
                || comboCategoria.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, complete todos los campos"
            );
            return;
        }

        int stock;

        try {
            stock = Integer.parseInt(stockTexto);

            if (stock < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "El stock no puede ser negativo"
                );
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El stock debe ser un número entero"
            );
            return;
        }

        Categoria categoria =
                (Categoria) comboCategoria.getSelectedItem();

        Libro libro = new Libro(
                0,
                titulo,
                autor,
                editorial,
                isbn,
                stock,
                categoria
        );

        try {
            boolean guardado = libroDAO.guardarLibroBD(libro);

            if (guardado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Libro registrado correctamente"
                );

                cargarTabla();
                limpiarFormulario();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el libro"
                );
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar el libro: " + e.getMessage()
            );
        }
    }

    private void modificarLibro() {

        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un libro de la tabla"
            );
            return;
        }

        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String editorial = txtEditorial.getText().trim();
        String isbn = txtIsbn.getText().trim();
        String stockTexto = txtStock.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty()
                || editorial.isEmpty() || isbn.isEmpty()
                || stockTexto.isEmpty()
                || comboCategoria.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, complete todos los campos"
            );
            return;
        }

        int id;
        int stock;

        try {
            id = Integer.parseInt(txtId.getText());
            stock = Integer.parseInt(stockTexto);

            if (stock < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "El stock no puede ser negativo"
                );
                return;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El ID o el stock no son válidos"
            );
            return;
        }

        Categoria categoria =
                (Categoria) comboCategoria.getSelectedItem();

        Libro libro = new Libro(
                id,
                titulo,
                autor,
                editorial,
                isbn,
                stock,
                categoria
        );

        try {
            System.out.println("ID del libro: " + id);
            System.out.println("Stock enviado a MySQL: " + stock);

            boolean modificado = libroDAO.modificarLibroBD(libro);

            if (modificado) {
                JOptionPane.showMessageDialog(
                        this,
                        "Libro modificado correctamente"
                );

                cargarTabla();
                limpiarFormulario();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se modificaron filas. Revisa el ID del libro."
                );
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al modificar el libro: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    private void eliminarLibro() {

        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un libro de la tabla"
            );
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el libro seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {

            try {
                int id = Integer.parseInt(txtId.getText());

                boolean eliminado = libroDAO.eliminarLibro(id);

                if (eliminado) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Libro eliminado correctamente"
                    );

                    cargarTabla();
                    limpiarFormulario();

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar el libro"
                    );
                }

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el libro. "
                                + "Verifique si tiene préstamos asociados."
                );
            }
        }
    }

    private void seleccionarLibro() {

        int fila = tablaLibros.getSelectedRow();

        if (fila >= 0) {

            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtTitulo.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtAutor.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtEditorial.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtIsbn.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtStock.setText(modeloTabla.getValueAt(fila, 5).toString());

            comboCategoria.setSelectedItem(
                    modeloTabla.getValueAt(fila, 6)
            );
        }
    }

    private void limpiarFormulario() {

        txtId.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtEditorial.setText("");
        txtIsbn.setText("");
        txtStock.setText("");

        if (comboCategoria.getItemCount() > 0) {
            comboCategoria.setSelectedIndex(0);
        }

        tablaLibros.clearSelection();
    }
}